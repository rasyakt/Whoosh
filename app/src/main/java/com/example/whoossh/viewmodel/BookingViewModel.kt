package com.example.whoossh.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.whoossh.api.*
import com.example.whoossh.data.StationData
import com.example.whoossh.data.UserPreferences
import com.example.whoossh.model.BookingData
import com.example.whoossh.model.CoachClass
import com.example.whoossh.model.Passenger
import com.example.whoossh.model.Schedule
import com.example.whoossh.model.OrderStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.whoossh.utils.EmailSender
import com.example.whoossh.utils.TicketUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val api = ApiClient.apiService
    
    // Mutex for timer synchronization
    private val timerMutex = Mutex()

    // Login State
    var isLoggedIn by mutableStateOf(false)
        private set
    var userId by mutableIntStateOf(0)
        private set
    var userName by mutableStateOf("")
        private set
    var userEmail by mutableStateOf("")
        private set
    var userPhone by mutableStateOf("")
        private set
    var loginError by mutableStateOf<String?>(null)
        private set
    var registerError by mutableStateOf<String?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    // Booking Form State
    var originStation by mutableStateOf("")
        private set
    var destinationStation by mutableStateOf("")
        private set
    var ticketCount by mutableIntStateOf(1)
        private set
    var departureDate by mutableStateOf("")
        private set
    var formError by mutableStateOf<String?>(null)
        private set

    // Schedule State
    var schedules by mutableStateOf<List<Schedule>>(emptyList())
        private set
    var selectedSchedule by mutableStateOf<Schedule?>(null)
        private set

    // Coach State
    var selectedCoachClass by mutableStateOf<CoachClass?>(null)
        private set

    // Seat State
    var selectedCarriage by mutableStateOf<Int?>(null)
        private set
    var selectedSeats = mutableStateListOf<String>()
        private set
    var occupiedSeats = mutableStateListOf<String>()
        private set
    var isOccupiedSeatsLoading by mutableStateOf(false)
        private set
    private var occupiedSeatsJob: kotlinx.coroutines.Job? = null

    // Booking Result
    var bookingData by mutableStateOf<BookingData?>(null)
        private set

    // Tickets
    var activeTickets by mutableStateOf<List<BookingData>>(emptyList())
        private set
    var historyTickets by mutableStateOf<List<BookingData>>(emptyList())
        private set
    var isLoadingTickets by mutableStateOf(false)
        private set
    
    // Bank Account Info (Pre-filled from SharedPreferences)
    var savedBankName by mutableStateOf(userPreferences.getBankName())
        private set
    var savedAccountNo by mutableStateOf(userPreferences.getAccountNo())
        private set
    var savedAccountHolder by mutableStateOf(userPreferences.getAccountHolder())
        private set

    // Timer State (Shared)
    var seatLockTimeLeft by mutableStateOf(0)
        private set
    var paymentTimeLeft by mutableStateOf(0)
        private set
    private var timerJob: kotlinx.coroutines.Job? = null

    // Email Status
    var emailSentStatus by mutableStateOf<Boolean?>(null)
        private set

    // Passenger Management
    private val _selectedPassengers = MutableStateFlow<List<Passenger>>(emptyList())
    val selectedPassengers: StateFlow<List<Passenger>> = _selectedPassengers.asStateFlow()

    private val _savedPassengers = MutableStateFlow<List<Passenger>>(emptyList())
    val savedPassengers: StateFlow<List<Passenger>> = _savedPassengers.asStateFlow()

    init {
        // Check if user was previously logged in (dari cache lokal)
        val savedUser = userPreferences.getLoggedInUser()
        val savedUserId = userPreferences.getUserId()
        if (savedUser != null && savedUserId > 0) {
            isLoggedIn = true
            userId = savedUserId
            userName = savedUser.name
            userEmail = savedUser.email
            userPhone = savedUser.phone
            Log.i("BookingViewModel", "Restored session: userId=$userId, name=$userName")
            refreshTickets()
            refreshSavedPassengers()
        } else if (savedUser != null && savedUserId <= 0) {
            // Cache lama dari sebelum migrasi, userId tidak valid
            // Paksa user login ulang agar mendapat userId dari server
            Log.w("BookingViewModel", "Cache lama terdeteksi (userId=0), memaksa login ulang")
            userPreferences.clearLoggedInUser()
            isLoggedIn = false
        }
        
        // Sinkronisasi data stasiun dari API
        refreshStations()
    }
    
    /**
     * Clean up resources when ViewModel is destroyed
     * Prevents memory leaks from running coroutines
     */
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        Log.i("BookingViewModel", "ViewModel cleared, timer cancelled")
    }

    // ── STATIONS SYNC ────────────────────────────────────────────────────────

    fun refreshStations() {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getStations()
                }
                if (response.isSuccessful && response.body()?.status == "success") {
                    val stationResponses = response.body()?.data ?: emptyList()
                    if (stationResponses.isNotEmpty()) {
                        val newStations = stationResponses.map { 
                            com.example.whoossh.model.Station(it.id, it.name)
                        }
                        StationData.updateStations(newStations)
                        Log.i("BookingViewModel", "Stations synced: ${newStations.size} stations")
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Failed to sync stations: ${e.message}")
            }
        }
    }

    // ── LOGIN & REGISTER ─────────────────────────────────────────────────────

    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        loginError = null
        if (email.isBlank() || password.isBlank()) {
            loginError = "Email dan password tidak boleh kosong"
            onResult(false)
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.login(LoginRequest(email, password))
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    val user = response.body()!!.data!!
                    userId = user.id
                    userName = user.name
                    userEmail = user.email
                    userPhone = user.phone
                    isLoggedIn = true

                    // Cache locally
                    userPreferences.saveLoggedInUser(
                        com.example.whoossh.model.User(user.name, user.email, user.phone, ""),
                        user.id
                    )
                    refreshTickets()
                    refreshSavedPassengers()
                    onResult(true)
                } else {
                    val errorBody = response.body()?.message ?: "Email atau password salah"
                    loginError = errorBody
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Login error", e)
                loginError = "Gagal terhubung ke server: ${e.localizedMessage}"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun register(
        name: String, email: String, phone: String,
        password: String, confirmPassword: String,
        onResult: (Boolean) -> Unit
    ) {
        registerError = null
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            registerError = "Semua field harus diisi"
            onResult(false)
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            registerError = "Format email tidak valid"
            onResult(false)
            return
        }
        if (phone.length < 10) {
            registerError = "Nomor HP minimal 10 digit"
            onResult(false)
            return
        }
        if (password.length < 6) {
            registerError = "Password minimal 6 karakter"
            onResult(false)
            return
        }
        if (password != confirmPassword) {
            registerError = "Konfirmasi password tidak cocok"
            onResult(false)
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.register(RegisterRequest(name, email, phone, password))
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    val user = response.body()!!.data!!
                    userId = user.id
                    userName = user.name
                    userEmail = user.email
                    userPhone = user.phone
                    isLoggedIn = true

                    // Cache locally
                    userPreferences.saveLoggedInUser(
                        com.example.whoossh.model.User(user.name, user.email, user.phone, ""),
                        user.id
                    )
                    refreshSavedPassengers()
                    onResult(true)
                } else {
                    registerError = response.body()?.message ?: "Registrasi gagal"
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Register error", e)
                registerError = "Gagal terhubung ke server: ${e.localizedMessage}"
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun logout() {
        isLoggedIn = false
        userId = 0
        userName = ""
        userEmail = ""
        userPhone = ""
        userPreferences.clearLoggedInUser()
        userPreferences.clearPaidTicketsCache() // Bersihkan cache tiket yang sudah dibayar
        userPreferences.clearCancelledTicketsCache() // Bersihkan cache tiket yang dibatalkan
        resetBooking()
        activeTickets = emptyList()
        historyTickets = emptyList()
        Log.i("BookingViewModel", "User logged out, all caches cleared")
    }

    // ── PROFILE ──────────────────────────────────────────────────────────────

    fun updateProfile(name: String, email: String, phone: String, onResult: (Boolean) -> Unit) {
        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            onResult(false)
            return
        }
        if (!email.contains("@") || !email.contains(".")) {
            onResult(false)
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.updateProfile(UpdateProfileRequest(userId, name, email, phone))
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    val user = response.body()!!.data!!
                    userName = user.name
                    userEmail = user.email
                    userPhone = user.phone

                    // Update cache
                    userPreferences.saveLoggedInUser(
                        com.example.whoossh.model.User(user.name, user.email, user.phone, ""),
                        user.id
                    )
                    onResult(true)
                } else {
                    onResult(false)
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Update profile error", e)
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun changePassword(
        oldPassword: String, newPassword: String, confirmPassword: String,
        onResult: (String?) -> Unit
    ) {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            onResult("Semua field harus diisi")
            return
        }
        if (newPassword.length < 6) {
            onResult("Password baru minimal 6 karakter")
            return
        }
        if (newPassword != confirmPassword) {
            onResult("Konfirmasi password tidak cocok")
            return
        }
        if (oldPassword == newPassword) {
            onResult("Password baru harus berbeda")
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.changePassword(ChangePasswordRequest(userId, oldPassword, newPassword))
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    onResult(null) // null = success
                } else {
                    onResult(response.body()?.message ?: "Password lama salah")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Change password error", e)
                onResult("Gagal terhubung ke server")
            } finally {
                isLoading = false
            }
        }
    }

    // ── TICKETS PERSISTENCE ──────────────────────────────────────────────────

    fun refreshTickets() {
        if (userId <= 0) {
            Log.w("BookingViewModel", "refreshTickets: userId=$userId, skipping")
            return
        }

        Log.d("BookingViewModel", "refreshTickets: fetching tickets for userId=$userId")
        isLoadingTickets = true

        viewModelScope.launch {
            try {
                // Strategy 1: Non-generic TicketsListResponse (avoids Gson type erasure)
                val response = withContext(Dispatchers.IO) {
                    api.getTicketsList(userId)
                }

                Log.d("BookingViewModel", "refreshTickets: HTTP ${response.code()}")

                if (response.isSuccessful && response.body()?.status == "success") {
                    val tickets = response.body()!!.data ?: emptyList()
                    Log.i("BookingViewModel", "refreshTickets: received ${tickets.size} tickets from server")
                    applyTickets(tickets)
                    isLoadingTickets = false
                    return@launch
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("BookingViewModel", "refreshTickets Strategy 1 failed: HTTP ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "refreshTickets Strategy 1 error: ${e.javaClass.simpleName} - ${e.message}")
            }

            // Strategy 2: Raw JSON + manual parsing
            try {
                Log.d("BookingViewModel", "refreshTickets: trying raw JSON fallback...")
                val rawResponse = withContext(Dispatchers.IO) {
                    api.getTicketsRaw(userId)
                }

                if (rawResponse.isSuccessful) {
                    val jsonString = rawResponse.body()?.string() ?: ""
                    Log.d("BookingViewModel", "refreshTickets raw response: ${jsonString.take(200)}")

                    val gson = com.google.gson.Gson()
                    val type = object : com.google.gson.reflect.TypeToken<TicketsListResponse>() {}.type
                    val parsed = gson.fromJson<TicketsListResponse>(jsonString, type)

                    if (parsed.status == "success" && parsed.data != null) {
                        Log.i("BookingViewModel", "refreshTickets Strategy 2: parsed ${parsed.data.size} tickets")
                        applyTickets(parsed.data)
                        isLoadingTickets = false
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "refreshTickets Strategy 2 error: ${e.javaClass.simpleName} - ${e.message}", e)
            }

            Log.w("BookingViewModel", "refreshTickets: all strategies failed, keeping existing ${activeTickets.size} local tickets")
            isLoadingTickets = false
        }
    }

    private fun applyTickets(tickets: List<BookingResponse>) {
        Log.d("BookingViewModel", "applyTickets: Processing ${tickets.size} tickets from server")
        
        // Ambil cache booking codes dari SharedPreferences
        val paidTicketsCache = userPreferences.getPaidTickets()
        val cancelledTicketsCache = userPreferences.getCancelledTickets()
        val refundedTicketsCache = userPreferences.getRefundedTickets()
        
        Log.d("BookingViewModel", "applyTickets: Paid: ${paidTicketsCache.size}, Cancelled: ${cancelledTicketsCache.size}, Refunded: ${refundedTicketsCache.size}")
        
        val serverBookings = tickets.mapNotNull { ticket ->
            try {
                val booking = ticket.toBookingData(userName)
                
                // PROTEKSI 1: Jika tiket ada di cache cancelled
                if (cancelledTicketsCache.contains(booking.bookingCode)) {
                    // Jika ini adalah refund (pernah dibayar), JANGAN SKIP.
                    // Jika ini adalah pembatalan biasa (unpaid), baru boleh di-skip jika diinginkan.
                    // Tapi lebih aman tampilkan saja di riwayat.
                    
                    // Jika server sudah update ke cancelled, hapus dari cache
                    if (ticket.isCancelled == 1) {
                        Log.i("BookingViewModel", "  ✅ Server synced for ${booking.bookingCode}, removing from cancelled cache")
                        userPreferences.removeCancelledTicket(ticket.bookingCode)
                    }
                    
                    // JANGAN RETURN NULL di sini jika ingin tetap muncul di riwayat
                    // Kita biarkan mengalir ke finalBooking
                }
                
        val finalBooking = if (refundedTicketsCache.contains(booking.bookingCode)) {
            // PAKSA status REFUNDED jika ada di cache refund
            Log.w("BookingViewModel", "  ⚠️ PROTECTING ${booking.bookingCode}: cache says REFUNDED")
            booking.copy(isPaid = true, isCancelled = true, status = OrderStatus.REFUNDED)
        } else if (paidTicketsCache.contains(booking.bookingCode)) {
            if (!booking.isPaid || booking.isCancelled) {
                Log.w("BookingViewModel", "  ⚠️ PROTECTING ${booking.bookingCode}: cache says PAID, forcing isPaid=true, isCancelled=false")
                booking.copy(isPaid = true, isCancelled = false, status = OrderStatus.PAID)
            } else {
                booking
            }
        } else {
            booking
        }
                
                Log.d("BookingViewModel", "  Server ticket: ${ticket.bookingCode} isPaid=${ticket.isPaid} isCancelled=${ticket.isCancelled} (raw) -> isPaid=${finalBooking.isPaid} isCancelled=${finalBooking.isCancelled} (final)")
                
                // Jika server sudah update ke paid, hapus dari cache
                if (ticket.isPaid == 1 && paidTicketsCache.contains(ticket.bookingCode)) {
                    Log.i("BookingViewModel", "  ✅ Server synced for ${ticket.bookingCode}, removing from paid cache")
                    userPreferences.removePaidTicket(ticket.bookingCode)
                }
                
                finalBooking
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error parsing ticket ${ticket.bookingCode}: ${e.message}")
                null
            }
        }

        // Buat map dari tiket lokal untuk lookup cepat
        val localMap = activeTickets.associateBy { it.bookingCode }
        Log.d("BookingViewModel", "applyTickets: Current local tickets: ${localMap.keys.joinToString()}")
        localMap.forEach { (code, ticket) ->
            Log.d("BookingViewModel", "  Local ticket: $code isPaid=${ticket.isPaid} isCancelled=${ticket.isCancelled}")
        }
        
        val serverActive = serverBookings.filter { !it.isUsed && !it.isCancelled }

        // Gabungkan: untuk tiap booking code, pilih versi yang isPaid=true jika ada
        val mergedMap = mutableMapOf<String, BookingData>()

        // Masukkan tiket server dulu (sudah di-override dengan cache jika perlu)
        for (ticket in serverActive) {
            mergedMap[ticket.bookingCode] = ticket
        }

        // Timpa dengan data lokal JIKA lokal sudah isPaid=true dan server belum
        // Ini memastikan status pembayaran lokal tidak hilang
        for (ticket in activeTickets) {
            val server = mergedMap[ticket.bookingCode]
            if (server != null) {
                // PROTEKSI: Jika lokal sudah paid, JANGAN PERNAH ubah jadi cancelled
                if (ticket.isPaid) {
                    if (!server.isPaid || server.isCancelled) {
                        Log.w("BookingViewModel", "⚠️ PRESERVING local paid status for ${ticket.bookingCode} (local=paid, server=unpaid/cancelled)")
                        mergedMap[ticket.bookingCode] = ticket.copy(isCancelled = false)
                    } else {
                        // Keduanya sudah bayar, gunakan server (lebih update)
                        Log.i("BookingViewModel", "✅ Both paid for ${ticket.bookingCode}, using server data")
                    }
                } else if (!ticket.isPaid && server.isPaid) {
                    // Server sudah update, lokal belum
                    Log.i("BookingViewModel", "✅ Server updated to paid for ${ticket.bookingCode}")
                }
                // Jika lokal belum bayar, gunakan data server (default behavior)
            } else {
                // Tiket hanya ada di lokal (belum sync ke server), pertahankan
                mergedMap[ticket.bookingCode] = ticket
                Log.w("BookingViewModel", "⚠️ PRESERVING local-only ticket ${ticket.bookingCode} (isPaid=${ticket.isPaid})")
            }
        }

        // ✅ FITUR BARU: Auto-update status tiket yang sudah lewat waktu keberangkatan
        val updatedTickets = mergedMap.values.map { ticket ->
            if (ticket.isPaid && !ticket.isUsed && !ticket.isCancelled) {
                if (isTicketExpired(ticket)) {
                    Log.i("BookingViewModel", "🕐 Auto-marking ${ticket.bookingCode} as USED (departure time passed)")
                    // Update ke server
                    markTicketAsUsed(ticket.bookingCode)
                    // Update lokal
                    ticket.copy(isUsed = true)
                } else {
                    ticket
                }
            } else {
                ticket
            }
        }

        activeTickets = updatedTickets.filter { !it.isUsed && !it.isCancelled }

        // ✅ PERBAIKAN: Jangan akumulasi riwayat lama agar tidak terjadi duplikasi atau status basi
        historyTickets = (serverBookings.filter { it.isUsed || it.isCancelled } + 
                         updatedTickets.filter { it.isUsed || it.isCancelled })
            .distinctBy { it.bookingCode }
            .sortedByDescending { it.bookingTimestamp }  // ✅ Urutkan dari terbaru ke terlama

        Log.i("BookingViewModel", "applyTickets RESULT: ${activeTickets.size} active (merged), ${historyTickets.size} history")
        activeTickets.forEach { ticket ->
            Log.i("BookingViewModel", "  Final: ${ticket.bookingCode} isPaid=${ticket.isPaid} isCancelled=${ticket.isCancelled} isUsed=${ticket.isUsed}")
        }
    }
    
    /**
     * Cek apakah tiket sudah melewati waktu keberangkatan
     */
    private fun isTicketExpired(ticket: BookingData): Boolean {
        try {
            // Parse tanggal keberangkatan
            val dateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            
            val departureDate = dateFormat.parse(ticket.departureDate) ?: return false
            val departureTime = timeFormat.parse(ticket.departureTime) ?: return false
            
            // Gabungkan tanggal dan waktu
            val calendar = Calendar.getInstance()
            calendar.time = departureDate
            
            val timeCalendar = Calendar.getInstance()
            timeCalendar.time = departureTime
            
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            val departureTimestamp = calendar.timeInMillis
            val now = System.currentTimeMillis()
            
            // Tiket dianggap expired jika sudah lewat waktu keberangkatan
            return now > departureTimestamp
        } catch (e: Exception) {
            Log.e("BookingViewModel", "Error checking ticket expiry: ${e.message}")
            return false
        }
    }
    
    /**
     * Tandai tiket sebagai sudah digunakan di server
     */
    private fun markTicketAsUsed(bookingCode: String) {
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.updateBookingStatus(mapOf(
                        "booking_code" to bookingCode,
                        "is_used" to 1
                    ))
                }
                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.i("BookingViewModel", "✅ Ticket $bookingCode marked as USED on server")
                } else {
                    Log.e("BookingViewModel", "❌ Failed to mark ticket as used: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "❌ Error marking ticket as used: ${e.message}")
            }
        }
    }

    fun getTotalTrips(): Int = activeTickets.size + historyTickets.size
    fun getActiveTicketCount(): Int = activeTickets.size

    // ── STATION FUNCTIONS ────────────────────────────────────────────────────

    fun setOrigin(station: String) {
        originStation = station
        formError = null
    }

    fun setDestination(station: String) {
        destinationStation = station
        formError = null
    }

    fun swapStations() {
        val temp = originStation
        originStation = destinationStation
        destinationStation = temp
    }

    // ── TICKET COUNT ─────────────────────────────────────────────────────────

    fun incrementTicket() {
        if (ticketCount < 10) ticketCount++
    }

    fun decrementTicket() {
        if (ticketCount > 1) ticketCount--
    }

    // ── DATE ─────────────────────────────────────────────────────────────────

    fun setDate(date: String) {
        departureDate = date
        formError = null
    }

    // ── SEARCH SCHEDULE ──────────────────────────────────────────────────────

    var isLoadingSchedules by mutableStateOf(false)
        private set

    fun searchSchedules(): Boolean {
        val error = TicketUtils.validateBookingForm(
            originStation, destinationStation, ticketCount, departureDate
        )
        if (error != null) {
            formError = error
            return false
        }

        // Clear selection state when starting a new search to prevent state leakage
        selectedSchedule = null
        selectedCoachClass = null
        selectedCarriage = null
        selectedSeats.clear()
        
        // Jalankan fetch dari API di background
        fetchSchedulesFromApi()
        
        return true
    }

    private fun fetchSchedulesFromApi() {
        isLoadingSchedules = true
        schedules = emptyList() // Reset list lama

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getSchedules(originStation, destinationStation)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    val apiSchedules = response.body()?.data ?: emptyList()
                    val duration = StationData.getDuration(originStation, destinationStation)
                    
                    val mappedSchedules = apiSchedules.map { s ->
                        val actualDuration = TicketUtils.getActualDuration(s.departureTime, s.originStation, s.destinationStation)
                        Schedule(
                            departureTime = s.departureTime,
                            arrivalTime = TicketUtils.calculateArrivalTimeWithDate(s.departureTime, actualDuration).first,
                            duration = actualDuration,
                            originStation = s.originStation,
                            destinationStation = s.destinationStation,
                            price = TicketUtils.getPricePerTicket(s.originStation, s.destinationStation, CoachClass.EKONOMI, s.departureTime),
                            trainCode = s.trainCode,
                            stops = TicketUtils.getStops(s.originStation, s.destinationStation),
                            stopDetails = TicketUtils.getStopDetails(s.originStation, s.destinationStation, s.departureTime)
                        )
                    }

                    // Real-time filtering logic
                    val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
                    val todayStr = sdf.format(Calendar.getInstance().time)
                    val isToday = departureDate == todayStr

                    schedules = if (isToday) {
                        // Tambahkan buffer 30 menit dari waktu sekarang agar user punya waktu boarding
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.MINUTE, 30)
                        val cutoffTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
                        
                        mappedSchedules.filter { it.departureTime > cutoffTime }
                    } else {
                        mappedSchedules
                    }

                    Log.i("BookingViewModel", "Schedules synced: ${schedules.size} items from API (Filtered: $isToday)")
                } else {
                    Log.e("BookingViewModel", "Failed to fetch schedules: ${response.message()}")
                }

                // FALLBACK: Jika API gagal atau kosong, gunakan data lokal (Jadwal Asli)
                if (schedules.isEmpty()) {
                    Log.i("BookingViewModel", "Using local fallback for schedules")
                    val duration = StationData.getDuration(originStation, destinationStation)
                    val localTimes = TicketUtils.generateScheduleTimes(originStation, destinationStation)
                    
                    val fallbackSchedules = localTimes.map { time ->
                        val actualDuration = TicketUtils.getActualDuration(time, originStation, destinationStation)
                        Schedule(
                            departureTime = time,
                            arrivalTime = TicketUtils.calculateArrivalTimeWithDate(time, actualDuration).first,
                            duration = actualDuration,
                            originStation = originStation,
                            destinationStation = destinationStation,
                            price = TicketUtils.getPricePerTicket(originStation, destinationStation, CoachClass.EKONOMI, time),
                            trainCode = TicketUtils.generateTrainCode(time, originStation),
                            stops = TicketUtils.getStops(originStation, destinationStation),
                            stopDetails = TicketUtils.getStopDetails(originStation, destinationStation, time)
                        )
                    }

                    // Real-time filtering for today
                    val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
                    val todayStr = sdf.format(Calendar.getInstance().time)
                    val isToday = departureDate == todayStr

                    schedules = if (isToday) {
                        val cal = Calendar.getInstance()
                        cal.add(Calendar.MINUTE, 15) // Buffer 15 menit
                        val cutoffTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(cal.time)
                        fallbackSchedules.filter { it.departureTime > cutoffTime }
                    } else {
                        fallbackSchedules
                    }
                    Log.i("BookingViewModel", "Fallback generated ${schedules.size} schedules")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error fetching schedules: ${e.message}")
            } finally {
                isLoadingSchedules = false
            }
        }
    }

    // ── SELECT SCHEDULE ──────────────────────────────────────────────────────

    fun selectSchedule(schedule: Schedule) {
        if (selectedSchedule?.departureTime != schedule.departureTime || 
            selectedSchedule?.originStation != schedule.originStation) {
            selectedSchedule = schedule
            selectedCoachClass = null
            selectedCarriage = null
            selectedSeats.clear()
            clearSelectedPassengers()
            Log.d("BookingViewModel", "New schedule selected, selection state reset")
        } else {
            selectedSchedule = schedule
        }
    }

    // ── COACH CLASS & SEATS ──────────────────────────────────────────────────

    fun selectCoachClass(coachClass: CoachClass) {
        if (selectedCoachClass != coachClass) {
            selectedCoachClass = coachClass
            selectCarriage(getAvailableCarriages(coachClass).firstOrNull() ?: 1)
        }
    }

    fun getAvailableCarriages(coachClass: CoachClass?): List<Int> {
        return when (coachClass) {
            CoachClass.VIP -> listOf(1) // First Class
            CoachClass.BISNIS -> listOf(2) // Business Class
            CoachClass.EKONOMI -> listOf(3, 4, 5, 6, 7, 8) // Premium Economy
            null -> emptyList()
        }
    }

    fun selectCarriage(number: Int) {
        selectedCarriage = number
        selectedSeats.clear() // Reset selection when switching carriages
        loadOccupiedSeats()
    }
    
    fun loadOccupiedSeats() {
        val schedule = selectedSchedule ?: return
        val carriage = selectedCarriage ?: 1
        val date = departureDate
        
        // Cancel previous job to prevent race conditions
        occupiedSeatsJob?.cancel()
        
        // Clear current seats immediately so user doesn't see stale data from previous carriage
        occupiedSeats.clear()

        occupiedSeatsJob = viewModelScope.launch {
            isOccupiedSeatsLoading = true
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getOccupiedSeats(
                        origin = originStation,
                        destination = destinationStation,
                        date = date,
                        time = schedule.departureTime,
                        carriage = carriage
                    )
                }
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val seats = response.body()?.data?.get("occupied_seats") ?: emptyList()
                    occupiedSeats.clear()
                    occupiedSeats.addAll(seats)
                    
                    // Auto-remove seats that are now occupied from the selected list
                    val removedCount = selectedSeats.count { seats.contains(it) }
                    if (removedCount > 0) {
                        selectedSeats.removeAll { seats.contains(it) }
                        Log.w("BookingViewModel", "$removedCount selected seats were removed because they are now occupied")
                    }
                    
                    Log.i("BookingViewModel", "Occupied seats loaded: ${seats.size} seats for Carriage $carriage")
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    Log.e("BookingViewModel", "Error loading occupied seats: ${e.message}")
                }
            } finally {
                isOccupiedSeatsLoading = false
            }
        }
    }

    fun toggleSeatSelection(seatId: String) {
        if (occupiedSeats.contains(seatId)) return // Cannot select occupied
        
        if (selectedSeats.contains(seatId)) {
            selectedSeats.remove(seatId)
        } else {
            if (selectedSeats.size >= ticketCount && selectedSeats.isNotEmpty()) {
                selectedSeats.removeAt(0)
            }
            if (selectedSeats.size < ticketCount) {
                selectedSeats.add(seatId)
            }
        }
    }

    fun isSeatSelectionComplete(): Boolean {
        return selectedSeats.size == ticketCount
    }

    fun getPriceForClass(coachClass: CoachClass): Int {
        val schedule = selectedSchedule ?: return 300000
        return TicketUtils.getPricePerTicket(schedule.originStation, schedule.destinationStation, coachClass, schedule.departureTime)
    }

    // ── CONFIRM BOOKING ──────────────────────────────────────────────────────

    /**
     * Konfirmasi booking dan simpan ke server secara sinkron (suspend).
     * Mengembalikan Pair(Berhasil, Pesan/Error).
     */
    suspend fun confirmBooking(isPaid: Boolean = false): Pair<Boolean, String> {
        // 1. Validasi Internal
        if (_selectedPassengers.value.size < ticketCount) {
            return Pair(false, "Data penumpang belum lengkap. Dibutuhkan $ticketCount penumpang.")
        }
        val schedule = selectedSchedule ?: return Pair(false, "Jadwal perjalanan belum dipilih.")
        val coach = selectedCoachClass ?: return Pair(false, "Kelas gerbong belum dipilih.")
        
        val pricePerTicket = TicketUtils.getPricePerTicket(schedule.originStation, schedule.destinationStation, coach, schedule.departureTime)
        val total = pricePerTicket * ticketCount
        val bookingCode = TicketUtils.generateBookingCode()
        val timestamp = System.currentTimeMillis()

        // 2. Siapkan Data Lokal
        val data = BookingData(
            userName = userName,
            originStation = schedule.originStation,
            destinationStation = schedule.destinationStation,
            ticketCount = ticketCount,
            departureDate = departureDate,
            departureTime = schedule.departureTime,
            arrivalTime = schedule.arrivalTime,
            duration = schedule.duration,
            coachClass = coach,
            pricePerTicket = pricePerTicket,
            totalPrice = total,
            bookingCode = bookingCode,
            selectedCarriage = selectedCarriage ?: 1,
            selectedSeats = selectedSeats.toList(),
            passengers = _selectedPassengers.value.mapIndexed { index, p ->
                com.example.whoossh.model.PassengerInfo(
                    name = p.name,
                    identityNo = p.identityNo,
                    passengerType = p.passengerType,
                    seatNumber = if (index < selectedSeats.size) selectedSeats[index] else ""
                )
            },
            bookingTimestamp = timestamp,
            isUsed = false,
            isPaid = isPaid
        )

        // 3. Kirim ke Server & Tunggu Respon
        return try {
            val request = CreateBookingRequest(
                userId = userId,
                bookingCode = bookingCode,
                originStation = schedule.originStation,
                destinationStation = schedule.destinationStation,
                departureDate = departureDate,
                departureTime = schedule.departureTime,
                arrivalTime = schedule.arrivalTime,
                duration = schedule.duration,
                coachClass = coach.name,
                ticketCount = ticketCount,
                pricePerTicket = pricePerTicket,
                totalPrice = total,
                selectedCarriage = selectedCarriage ?: 1,
                selectedSeats = selectedSeats.sorted().joinToString(","),
                bookingTimestamp = timestamp,
                isPaid = if (isPaid) 1 else 0,
                passengers = _selectedPassengers.value.mapIndexed { index, p ->
                    p.toRequestModel(
                        userId = userId,
                        seatNumber = if (index < selectedSeats.size) selectedSeats[index] else ""
                    )
                }
            )

            val response = withContext(Dispatchers.IO) { api.createBooking(request) }

            if (response.isSuccessful && response.body()?.status == "success") {
                // 4. Sukses: Update State Lokal
                bookingData = data
                activeTickets = listOf(data) + activeTickets
                
                if (!isPaid) {
                    startSeatLockTimer()
                    startPaymentTimer()
                }
                
                // Kirim email di background (Opsional/Non-blocking)
                if (isPaid && userEmail.isNotBlank()) {
                    viewModelScope.launch(Dispatchers.IO) {
                        EmailSender.sendETicket(userEmail, data)
                    }
                }
                
                Log.i("BookingViewModel", "✅ Booking Success: $bookingCode")
                Pair(true, "Booking berhasil dikonfirmasi")
            } else {
                val errorMsg = response.body()?.message ?: "Gagal menyimpan data ke server"
                Log.e("BookingViewModel", "❌ API Error: $errorMsg")
                Pair(false, errorMsg)
            }
        } catch (e: Exception) {
            Log.e("BookingViewModel", "❌ Network Error", e)
            Pair(false, "Terjadi kesalahan jaringan: ${e.localizedMessage}")
        }
    }

    fun markAsPaid() {
        val current = bookingData ?: return
        Log.i("BookingViewModel", "markAsPaid: Marking ${current.bookingCode} as PAID")
        
        val updated = current.copy(isPaid = true)
        bookingData = updated
        
        // Simpan ke cache lokal SEGERA (persisten di SharedPreferences)
        userPreferences.savePaidTicket(current.bookingCode)
        Log.i("BookingViewModel", "✅ Saved ${current.bookingCode} to paid tickets cache")
        
        // Update di list activeTickets SEGERA agar UI langsung update
        activeTickets = activeTickets.map { 
            if (it.bookingCode == current.bookingCode) {
                Log.i("BookingViewModel", "  Updated ${it.bookingCode} in activeTickets to isPaid=true")
                updated
            } else {
                it
            }
        }
        
        Log.i("BookingViewModel", "markAsPaid: Local state updated. activeTickets count=${activeTickets.size}")
        activeTickets.forEach { ticket ->
            Log.d("BookingViewModel", "  activeTicket: ${ticket.bookingCode} isPaid=${ticket.isPaid}")
        }
        
        // Stop all timers when paid
        stopTimer()

        // Update status ke server
        viewModelScope.launch {
            try {
                Log.d("BookingViewModel", "markAsPaid: Sending update to server for ${current.bookingCode}")
                val response = withContext(Dispatchers.IO) {
                    api.updateBookingStatus(mapOf(
                        "booking_code" to current.bookingCode,
                        "is_paid" to 1,
                        "is_cancelled" to 0
                    ))
                }
                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.i("BookingViewModel", "✅ Server status updated to PAID: ${current.bookingCode}")
                    
                    // Send Email Notification
                    if (userEmail.isNotBlank()) {
                        viewModelScope.launch(Dispatchers.IO) {
                            EmailSender.sendETicket(userEmail, updated)
                        }
                    }

                    // Refresh tickets setelah update berhasil untuk sinkronisasi
                    refreshTickets()
                } else {
                    val errorMsg = response.body()?.message ?: response.message()
                    Log.e("BookingViewModel", "❌ Failed to update server status: HTTP ${response.code()} - $errorMsg")
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        Log.e("BookingViewModel", "Error body: $errorBody")
                    }
                    // Meskipun gagal update ke server, status lokal tetap paid (ada di cache)
                    // Akan di-retry saat refreshTickets() berikutnya
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "❌ Failed to update paid status on server: ${e.javaClass.simpleName} - ${e.message}", e)
                // Meskipun gagal update ke server, status lokal tetap paid (ada di cache)
            }
        }

        // Kirim e-ticket via email setelah konfirmasi pembayaran
        if (userEmail.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val success = EmailSender.sendETicket(userEmail, updated)
                emailSentStatus = success
                if (success) {
                    Log.i("BookingViewModel", "E-ticket berhasil dikirim ke $userEmail setelah pembayaran")
                } else {
                    Log.w("BookingViewModel", "Gagal mengirim e-ticket ke $userEmail")
                }
            }
        }
        
        Log.i("BookingViewModel", "✅ Tiket ${current.bookingCode} berhasil ditandai sebagai SUDAH BAYAR (lokal + cache)")
    }
    
    /**
     * Batalkan tiket yang belum dibayar
     */
    fun cancelUnpaidTicket(onResult: (Boolean, String) -> Unit) {
        val current = bookingData ?: run {
            onResult(false, "Tidak ada tiket yang dipilih")
            return
        }
        
        if (current.isPaid) {
            onResult(false, "Tiket yang sudah dibayar tidak bisa dibatalkan di sini. Gunakan fitur Refund.")
            return
        }
        
        if (current.isCancelled) {
            onResult(false, "Tiket ini sudah dibatalkan sebelumnya.")
            return
        }
        
        Log.i("BookingViewModel", "cancelUnpaidTicket: Cancelling ${current.bookingCode}")
        
        // Update status lokal
        val cancelled = current.copy(isCancelled = true)
        bookingData = cancelled
        
        // Stop timer
        stopTimer()
        
        // Simpan ke cancelled cache untuk mencegah tiket muncul lagi
        userPreferences.saveCancelledTicket(current.bookingCode)
        Log.i("BookingViewModel", "✅ Saved ${current.bookingCode} to cancelled cache")
        
        // Update ke server
        viewModelScope.launch {
            try {
                Log.d("BookingViewModel", "cancelUnpaidTicket: Sending cancel to server for ${current.bookingCode}")
                val response = withContext(Dispatchers.IO) {
                    api.updateBookingStatus(mapOf(
                        "booking_code" to current.bookingCode,
                        "is_cancelled" to 1
                    ))
                }
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.i("BookingViewModel", "✅ Server status updated to CANCELLED: ${current.bookingCode}")
                    
                    // Hapus dari activeTickets dan pindah ke historyTickets
                    activeTickets = activeTickets.filter { it.bookingCode != current.bookingCode }
                    
                    // Tambahkan ke historyTickets dengan proteksi duplikat
                    val existingHistory = historyTickets.filter { it.bookingCode != current.bookingCode }
                    historyTickets = (listOf(cancelled) + existingHistory).sortedByDescending { it.bookingTimestamp }
                    
                    onResult(true, "Tiket berhasil dibatalkan")
                } else {
                    val errorMsg = response.body()?.message ?: response.message()
                    Log.e("BookingViewModel", "❌ Failed to cancel on server: HTTP ${response.code()} - $errorMsg")
                    
                    // Meskipun gagal di server, status lokal sudah dibatalkan dan di-cache
                    activeTickets = activeTickets.filter { it.bookingCode != current.bookingCode }
                    val existingHistory = historyTickets.filter { it.bookingCode != current.bookingCode }
                    historyTickets = (listOf(cancelled) + existingHistory).sortedByDescending { it.bookingTimestamp }
                    
                    // Pesan tanpa "(lokal)" karena sudah di-cache dengan aman
                    onResult(true, "Tiket berhasil dibatalkan")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "❌ Error cancelling ticket: ${e.message}", e)
                
                // Meskipun gagal di server, status lokal sudah dibatalkan dan di-cache
                activeTickets = activeTickets.filter { it.bookingCode != current.bookingCode }
                val existingHistory = historyTickets.filter { it.bookingCode != current.bookingCode }
                historyTickets = (listOf(cancelled) + existingHistory).sortedByDescending { it.bookingTimestamp }
                
                // Pesan tanpa "(lokal)" karena sudah di-cache dengan aman
                onResult(true, "Tiket berhasil dibatalkan")
            }
        }
        
        Log.i("BookingViewModel", "✅ Tiket ${current.bookingCode} berhasil dibatalkan")
    }

    fun viewTicket(ticket: BookingData) {
        bookingData = ticket
        selectedSchedule = com.example.whoossh.model.Schedule(
            departureTime = ticket.departureTime,
            arrivalTime = ticket.arrivalTime,
            duration = ticket.duration,
            originStation = ticket.originStation,
            destinationStation = ticket.destinationStation,
            price = ticket.pricePerTicket
        )
        selectedCoachClass = ticket.coachClass
        ticketCount = ticket.ticketCount
        originStation = ticket.originStation
        destinationStation = ticket.destinationStation
        departureDate = ticket.departureDate
        
        // Load passengers from server if this is a stored ticket
        if (ticket.bookingCode.isNotEmpty()) {
            loadTicketByCode(ticket.bookingCode)
        }
        
        // Calculate remaining payment time (15 mins from timestamp)
        if (!ticket.isPaid && !ticket.isCancelled) {
            val now = System.currentTimeMillis()
            val fifteenMinsMs = 15 * 60 * 1000
            val elapsed = now - ticket.bookingTimestamp
            val remainingMs = fifteenMinsMs - elapsed
            
            if (remainingMs > 0) {
                paymentTimeLeft = (remainingMs / 1000).toInt()
                startTimer() // Resume timer
            } else {
                paymentTimeLeft = 0
            }
        } else {
            paymentTimeLeft = 0
        }
    }

    fun loadTicketByCode(code: String) {
        Log.d("BookingViewModel", "Mencoba memuat tiket: $code")
        
        // Save the current local status BEFORE async fetch, as viewTicket() already set the correct state
        val preExistingBooking = bookingData

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTicketByCode(code)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    val ticket = response.body()!!.data!!
                    var data = ticket.toBookingData(ticket.userName.ifBlank { userName })
                    
                    // Preserve status from local state (viewTicket already set correct isPaid/isCancelled)
                    val localTicket = preExistingBooking?.takeIf { it.bookingCode.equals(code, ignoreCase = true) }
                        ?: activeTickets.find { it.bookingCode.equals(code, ignoreCase = true) }
                    val isPaidInCache = userPreferences.isPaidTicket(code)
                    
                    // If local or cache says PAID → keep paid (server might not have synced yet)
                    if (!data.isPaid && (localTicket?.isPaid == true || isPaidInCache)) {
                        Log.i("BookingViewModel", "Preserving isPaid=true for $code (server not synced)")
                        data = data.copy(isPaid = true)
                    }
                    
                    // If local says NOT cancelled but server says cancelled → trust local
                    // (server might have stale is_cancelled from before payment)
                    if (data.isCancelled && localTicket != null && !localTicket.isCancelled) {
                        Log.i("BookingViewModel", "Preserving isCancelled=false for $code (local is authoritative)")
                        data = data.copy(isCancelled = false)
                    }
                    
                    bookingData = data
                    
                    // Update passengers list in VM state
                    ticket.passengers?.let { pList ->
                        val domainPassengers = pList.map { p ->
                            Passenger(
                                name = p.name,
                                identityNo = p.identityNo,
                                passengerType = p.passengerType,
                                gender = "Male", // Default
                                dateOfBirth = "",
                                country = "Indonesia",
                                documentType = "ID Card",
                                expiryDate = "",
                                whatsapp = "",
                                email = ""
                            )
                        }
                        _selectedPassengers.value = domainPassengers
                        ticketCount = domainPassengers.size
                    }
                    
                    Log.i("BookingViewModel", "Tiket ditemukan dari server: $code, passengers=${ticket.passengers?.size ?: 0}")
                } else {
                    // Fallback: cari di list lokal
                    val localTicket = activeTickets.find { it.bookingCode.equals(code, ignoreCase = true) }
                        ?: historyTickets.find { it.bookingCode.equals(code, ignoreCase = true) }

                    if (localTicket != null) {
                        viewTicket(localTicket)
                        Log.i("BookingViewModel", "Tiket ditemukan dari cache lokal: $code")
                    } else {
                        Log.w("BookingViewModel", "Tiket tidak ditemukan: $code")
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Load ticket error", e)
                // Fallback lokal
                refreshTickets()
                val localTicket = activeTickets.find { it.bookingCode.equals(code, ignoreCase = true) }
                    ?: historyTickets.find { it.bookingCode.equals(code, ignoreCase = true) }
                if (localTicket != null) {
                    viewTicket(localTicket)
                }
            }
        }
    }

    // ── PASSENGER MANAGEMENT ─────────────────────────────────────────────────

    fun addPassenger(passenger: Passenger) {
        val current = _selectedPassengers.value.toMutableList()
        // Check both ID and identity number to prevent duplicates
        val isDuplicate = current.any { 
            it.id == passenger.id || 
            (it.identityNo.isNotBlank() && it.identityNo == passenger.identityNo) 
        }
        
        if (current.size < 15 && !isDuplicate) {
            current.add(passenger)
            _selectedPassengers.value = current
            ticketCount = current.size // Sync ticketCount
            Log.i("BookingViewModel", "Passenger added: ${passenger.name}, total: ${current.size}")
        } else if (isDuplicate) {
            Log.w("BookingViewModel", "Prevented adding duplicate passenger: ${passenger.name}")
        }
    }

    fun removePassenger(passenger: Passenger) {
        val current = _selectedPassengers.value.toMutableList()
        current.removeAll { it.id == passenger.id }
        _selectedPassengers.value = current
        val newSize = current.size
        ticketCount = if (newSize == 0) 1 else newSize // Sync ticketCount
        
        // Trim selectedSeats if it exceeds new passenger count
        while (selectedSeats.size > newSize && selectedSeats.isNotEmpty()) {
            selectedSeats.removeAt(selectedSeats.size - 1)
        }
        
        Log.i("BookingViewModel", "Passenger removed: ${passenger.name}, remaining: $newSize")
    }

    fun updatePassenger(passenger: Passenger) {
        val current = _selectedPassengers.value.toMutableList()
        val index = current.indexOfFirst { it.id == passenger.id }
        if (index != -1) {
            current[index] = passenger
            _selectedPassengers.value = current
            Log.i("BookingViewModel", "Passenger updated: ${passenger.name}")
        }
        
        // Update in saved passengers too if it exists there
        val saved = _savedPassengers.value.toMutableList()
        val savedIndex = saved.indexOfFirst { it.id == passenger.id }
        if (savedIndex != -1) {
            saved[savedIndex] = passenger
            _savedPassengers.value = saved
        }
    }

    fun savePassengerForFuture(passenger: Passenger) {
        if (userId <= 0) return

        viewModelScope.launch {
            try {
                val request = passenger.toRequestModel(userId = userId)
                
                // Check if this identity number already exists in our saved list to avoid duplicates
                val existingSaved = _savedPassengers.value.find { 
                    it.identityNo.isNotBlank() && it.identityNo == passenger.identityNo 
                }
                
                val response = if (passenger.id.toIntOrNull() != null) {
                    // Update existing by numeric ID
                    api.updatePassenger(request.copy(id = passenger.id.toInt()))
                } else if (existingSaved != null && existingSaved.id.toIntOrNull() != null) {
                    // It's a virtual ID but we found a real record with same identity_no
                    Log.i("BookingViewModel", "Found existing record for ${passenger.name}, updating instead of adding.")
                    api.updatePassenger(request.copy(id = existingSaved.id.toInt()))
                } else {
                    // Truly new or virtual without match
                    api.addPassenger(request)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.i("BookingViewModel", "Passenger saved to database: ${passenger.name}")
                    
                    // Capture new ID from DB if it was a new passenger
                    val data = response.body()?.data
                    val newId = (data?.get("id") as? Double)?.toInt() ?: (data?.get("id") as? Int)
                    
                    if (newId != null) {
                        // Update current selected list with real ID instead of UUID
                        val selected = _selectedPassengers.value.toMutableList()
                        val sIdx = selected.indexOfFirst { it.id == passenger.id }
                        if (sIdx != -1) {
                            selected[sIdx] = selected[sIdx].copy(id = newId.toString(), isSaved = true)
                            _selectedPassengers.value = selected
                        }
                    }
                    
                    refreshSavedPassengers()
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error saving passenger to DB: ${e.message}")
            }
        }

        // Local update for immediate UI feedback
        val current = _savedPassengers.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == passenger.id }
        
        if (existingIndex != -1) {
            current[existingIndex] = passenger.copy(isSaved = true)
        } else {
            current.add(passenger.copy(isSaved = true))
        }
        
        _savedPassengers.value = current
    }

    fun deleteSavedPassenger(passenger: Passenger) {
        val current = _savedPassengers.value.toMutableList()
        val idInt = passenger.id.toIntOrNull()
        
        if (idInt != null && userId > 0) {
            viewModelScope.launch {
                try {
                    api.deletePassenger(mapOf("id" to idInt))
                } catch (e: Exception) {
                    Log.e("BookingViewModel", "Error deleting passenger from DB: ${e.message}")
                }
            }
        }
        
        current.removeAll { it.id == passenger.id }
        _savedPassengers.value = current
        Log.i("BookingViewModel", "Saved passenger deleted: ${passenger.name}")
    }

    fun refreshSavedPassengers() {
        if (userId <= 0) return
        
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getPassengers(userId)
                }
                
                if (response.isSuccessful && response.body()?.status == "success") {
                    val apiPassengers = response.body()?.data ?: emptyList()
                    val domainPassengers = apiPassengers.map { it.toDomainModel() }
                    _savedPassengers.value = domainPassengers
                    Log.i("BookingViewModel", "Saved passengers synced: ${domainPassengers.size} items")
                    
                    // AUTO-SELECT LOGIC: 
                    // Jika belum ada penumpang terpilih, coba jadikan profil user login sebagai penumpang default
                    if (_selectedPassengers.value.isEmpty()) {
                        // Cari apakah profil user sudah ada di saved passengers (berdasarkan nama atau email)
                        val userProfile = domainPassengers.find { 
                            it.name.equals(userName, ignoreCase = true) || 
                            it.email.equals(userEmail, ignoreCase = true)
                        }
                        
                        if (userProfile != null) {
                            addPassenger(userProfile)
                            Log.i("BookingViewModel", "Auto-selected user profile from saved list: ${userProfile.name}")
                        } else {
                            // Jika tidak ada di server, buat data passenger virtual dari profil login
                            val virtualPassenger = Passenger(
                                id = "user_$userId", // Virtual ID
                                name = userName,
                                identityNo = "", // User must edit this if empty
                                gender = "Male",
                                dateOfBirth = "",
                                passengerType = "Adult",
                                discountType = "none",
                                country = "Indonesia",
                                documentType = "ID Card",
                                expiryDate = "",
                                whatsapp = userPhone,
                                email = userEmail,
                                isSaved = false
                            )
                            addPassenger(virtualPassenger)
                            Log.i("BookingViewModel", "Auto-created virtual passenger from login profile: $userName")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error refreshing passengers: ${e.message}")
            }
        }
    }

    fun clearSelectedPassengers() {
        _selectedPassengers.value = emptyList()
        selectedSeats.clear()
        ticketCount = 1
    }

    fun getSavedPassengerById(id: String): Passenger? {
        return _savedPassengers.value.find { it.id == id }
    }

    fun getSelectedPassengerById(id: String): Passenger? {
        return _selectedPassengers.value.find { it.id == id }
    }

    // ── RESCHEDULE ───────────────────────────────────────────────────────────

    fun canReschedule(booking: BookingData): Pair<Boolean, String> {
        if (!booking.isPaid) {
            return Pair(false, "Ticket must be paid before rescheduling")
        }
        if (booking.isUsed) {
            return Pair(false, "Cannot reschedule used ticket")
        }
        if (booking.isCancelled) {
            return Pair(false, "Cannot reschedule cancelled ticket")
        }
        
        // Check if departure time is at least 2 hours away
        val now = Calendar.getInstance()
        val departureDateTime = parseDateTime(booking.departureDate, booking.departureTime)
        val twoHoursInMillis = 2 * 60 * 60 * 1000
        
        if (departureDateTime != null && departureDateTime.timeInMillis - now.timeInMillis < twoHoursInMillis) {
            return Pair(false, "Reschedule must be done at least 2 hours before departure")
        }
        
        return Pair(true, "")
    }

    fun calculateRescheduleFee(currentDate: String, newDate: String, originalPrice: Int): Int {
        // If same day, no fee
        if (currentDate == newDate) {
            return 0
        }
        // Different day: 25% fee
        return (originalPrice * 0.25).toInt()
    }

    fun rescheduleTicket(
        booking: BookingData,
        newDate: String,
        newSchedule: Schedule,
        onResult: (Boolean, String) -> Unit
    ) {
        val (canReschedule, errorMsg) = canReschedule(booking)
        if (!canReschedule) {
            onResult(false, errorMsg)
            return
        }

        val rescheduleFee = calculateRescheduleFee(booking.departureDate, newDate, booking.totalPrice)
        val totalAmount = if (rescheduleFee > 0) rescheduleFee else 0

        isLoading = true
        viewModelScope.launch {
            try {
                // Prepare update data
                val updateData = mutableMapOf<String, @JvmSuppressWildcards Any>(
                    "booking_code" to booking.bookingCode,
                    "user_id" to userId,
                    "is_paid" to 1,
                    "departure_date" to newDate,
                    "departure_time" to newSchedule.departureTime,
                    "arrival_time" to newSchedule.arrivalTime
                )
                
                // Note: We're temporarily removing reschedule_fee because the database 
                // column might be missing, causing the entire update to fail.
                // updateData["reschedule_fee"] = rescheduleFee

                val response = withContext(Dispatchers.IO) {
                    api.updateBookingStatus(updateData)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    // Update local booking data
                    val updatedBooking = booking.copy(
                        departureDate = newDate,
                        departureTime = newSchedule.departureTime,
                        arrivalTime = newSchedule.arrivalTime
                    )
                    bookingData = updatedBooking
                    
                    // Update in activeTickets list
                    activeTickets = activeTickets.map { 
                        if (it.bookingCode == booking.bookingCode) updatedBooking else it 
                    }
                    
                    refreshTickets()
                    
                    // Send Email Notification
                    if (userEmail.isNotBlank()) {
                        viewModelScope.launch(Dispatchers.IO) {
                            EmailSender.sendRescheduleNotification(
                                recipientEmail = userEmail,
                                bookingData = booking,
                                newDate = newDate,
                                newTime = newSchedule.departureTime,
                                newArrivalTime = newSchedule.arrivalTime,
                                rescheduleFee = rescheduleFee
                            )
                        }
                    }

                    val message = if (rescheduleFee > 0) {
                        "Ticket rescheduled successfully. Fee: ${TicketUtils.formatRupiah(rescheduleFee)}"
                    } else {
                        "Ticket rescheduled successfully (same day, no fee)"
                    }
                    onResult(true, message)
                } else {
                    val errorMsg = response.body()?.message ?: "Server error ${response.code()}"
                    Log.e("BookingViewModel", "Reschedule failed: $errorMsg")
                    onResult(false, "Gagal Reschedule: $errorMsg")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Reschedule exception", e)
                onResult(false, "Kesalahan Koneksi: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }
    }

    // ── REFUND ───────────────────────────────────────────────────────────────

    fun canRefund(booking: BookingData): Pair<Boolean, String> {
        if (!booking.isPaid) {
            return Pair(false, "Only paid tickets can be refunded")
        }
        if (booking.isUsed) {
            return Pair(false, "Cannot refund used ticket")
        }
        if (booking.isCancelled) {
            return Pair(false, "Ticket already cancelled")
        }
        
        // Check if departure time is at least 2 hours away
        val now = Calendar.getInstance()
        val departureDateTime = parseDateTime(booking.departureDate, booking.departureTime)
        val twoHoursInMillis = 2 * 60 * 60 * 1000
        
        if (departureDateTime != null && departureDateTime.timeInMillis - now.timeInMillis < twoHoursInMillis) {
            return Pair(false, "Refund must be requested at least 2 hours before departure")
        }
        
        return Pair(true, "")
    }

    fun calculateRefundAmount(originalPrice: Int): Int {
        // 25% cancellation fee (Official KCIC Rule)
        val cancellationFee = (originalPrice * 0.25).toInt()
        return originalPrice - cancellationFee
    }

    fun refundTicket(
        booking: BookingData, 
        bankName: String = "", 
        accountNo: String = "", 
        onResult: (Boolean, String, Int) -> Unit
    ) {
        val (canRefund, errorMsg) = canRefund(booking)
        if (!canRefund) {
            onResult(false, errorMsg, 0)
            return
        }

        val refundAmount = calculateRefundAmount(booking.totalPrice)

        isLoading = true
        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.updateBookingStatus(mapOf(
                        "booking_code" to booking.bookingCode,
                        "is_cancelled" to 1,
                        "refund_amount" to refundAmount,
                        "bank_name" to bankName,
                        "account_no" to accountNo
                    ))
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    // Update local booking data
                    val updatedBooking = booking.copy(isCancelled = true)
                    bookingData = updatedBooking
                    
                    // CLEAR FROM PAID CACHING (VERY IMPORTANT)
                    // If we don't remove this, applyTickets will force isCancelled=false
                    userPreferences.removePaidTicket(booking.bookingCode)
                    
                    // ADD TO REFUNDED CACHING (NEW MEMORY)
                    userPreferences.saveRefundedTicket(booking.bookingCode)
                    
                    // ADD TO CANCELLED CACHING
                    userPreferences.saveCancelledTicket(booking.bookingCode)
                    
                    // Remove from activeTickets, add to history
                    activeTickets = activeTickets.filter { it.bookingCode != booking.bookingCode }
                    historyTickets = listOf(updatedBooking) + historyTickets
                    
                    refreshTickets()
                    
                    // Send Email Notification
                    if (userEmail.isNotBlank()) {
                        viewModelScope.launch(Dispatchers.IO) {
                            EmailSender.sendRefundNotification(
                                recipientEmail = userEmail,
                                bookingData = booking,
                                refundAmount = refundAmount,
                                bankName = bankName,
                                accountNo = accountNo
                            )
                        }
                    }

                    onResult(true, "Refund request submitted successfully", refundAmount)
                } else {
                    onResult(false, response.body()?.message ?: "Failed to process refund", 0)
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Refund error", e)
                onResult(false, "Failed to connect to server: ${e.localizedMessage}", 0)
            } finally {
                isLoading = false
            }
        }
    }

    // ── ADD INFANT ───────────────────────────────────────────────────────────

    var infantPassengers = mutableStateListOf<Passenger>()
        private set

    fun addInfantToBooking(
        booking: BookingData,
        infantName: String,
        infantIdentityNo: String,
        infantDateOfBirth: String,
        onResult: (Boolean, String) -> Unit
    ) {
        // 1. Validasi Status Tiket
        if (booking.isCancelled) {
            onResult(false, "Tidak dapat menambah infant pada tiket yang sudah dibatalkan")
            return
        }
        if (booking.isUsed) {
            onResult(false, "Tidak dapat menambah infant pada tiket yang sudah digunakan")
            return
        }
        if (!booking.isPaid) {
            onResult(false, "Harap selesaikan pembayaran tiket utama terlebih dahulu sebelum menambah infant")
            return
        }

        // 2. Validasi data infant
        if (infantName.isBlank()) {
            onResult(false, "Nama infant tidak boleh kosong")
            return
        }
        if (infantName.length < 3) {
            onResult(false, "Nama infant minimal 3 karakter")
            return
        }
        if (infantIdentityNo.isBlank()) {
            onResult(false, "Nomor akta kelahiran tidak boleh kosong")
            return
        }
        if (infantIdentityNo.length < 5) {
            onResult(false, "Nomor akta kelahiran tidak valid")
            return
        }
        
        // Validate date format and value
        val dateError = validateDateString(infantDateOfBirth, "Tanggal lahir")
        if (dateError != null) {
            onResult(false, dateError)
            return
        }

        // Validate infant age (must be under 3 years old)
        val infantAge = calculateAge(infantDateOfBirth)
        if (infantAge < 0) {
            onResult(false, "Tanggal lahir tidak valid")
            return
        }
        if (infantAge >= 3) {
            onResult(false, "Infant harus berusia di bawah 3 tahun (usia saat ini: $infantAge tahun)")
            return
        }

        isLoading = true
        viewModelScope.launch {
            try {
                val requestBody = mapOf(
                    "booking_code" to booking.bookingCode,
                    "name" to infantName.trim(),
                    "identity_no" to infantIdentityNo.trim(),
                    "date_of_birth" to infantDateOfBirth
                )

                val response = withContext(Dispatchers.IO) {
                    api.addInfantToBooking(requestBody)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    // Update local state by reloading the ticket
                    loadTicketByCode(booking.bookingCode)
                    
                    onResult(true, "Infant berhasil ditambahkan ke tiket ini")
                } else {
                    onResult(false, response.body()?.message ?: "Gagal menambahkan infant")
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Add infant error", e)
                onResult(false, "Gagal terhubung ke server: ${e.localizedMessage}")
            } finally {
                isLoading = false
            }
        }
    }

    // ── HELPER FUNCTIONS ─────────────────────────────────────────────────────

    private fun parseDateTime(date: String, time: String): Calendar? {
        return try {
            val sdf = SimpleDateFormat("EEEE, dd MMM yyyy HH:mm", Locale("id", "ID"))
            val dateTimeStr = "$date $time"
            val parsedDate = sdf.parse(dateTimeStr)
            if (parsedDate != null) {
                Calendar.getInstance().apply { this.time = parsedDate }
            } else null
        } catch (e: Exception) {
            Log.e("BookingViewModel", "Error parsing date time: $date $time", e)
            null
        }
    }

    private fun calculateAge(dateOfBirth: String): Int {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false // Strict date parsing
            val birthDate = sdf.parse(dateOfBirth)
            if (birthDate != null) {
                val birthCal = Calendar.getInstance().apply { time = birthDate }
                val today = Calendar.getInstance()
                
                // Validate date is not in the future
                if (birthCal.after(today)) {
                    Log.w("BookingViewModel", "Birth date is in the future: $dateOfBirth")
                    return -1
                }
                
                // Validate date is not too old (max 150 years)
                val maxAge = Calendar.getInstance().apply {
                    add(Calendar.YEAR, -150)
                }
                if (birthCal.before(maxAge)) {
                    Log.w("BookingViewModel", "Birth date is too old: $dateOfBirth")
                    return -1
                }
                
                var age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)
                if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }
                age
            } else {
                Log.w("BookingViewModel", "Failed to parse birth date: $dateOfBirth")
                -1
            }
        } catch (e: Exception) {
            Log.e("BookingViewModel", "Error calculating age: ${e.message}", e)
            -1
        }
    }
    
    /**
     * Validate date string format and value
     */
    private fun validateDateString(dateStr: String, fieldName: String): String? {
        if (dateStr.isBlank()) {
            return "$fieldName tidak boleh kosong"
        }
        
        // Check format dd/MM/yyyy
        val datePattern = Regex("""^\d{2}/\d{2}/\d{4}$""")
        if (!datePattern.matches(dateStr)) {
            return "$fieldName harus dalam format DD/MM/YYYY"
        }
        
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.isLenient = false
            val date = sdf.parse(dateStr)
            if (date == null) {
                return "$fieldName tidak valid"
            }
            
            // Check if date is in reasonable range
            val cal = Calendar.getInstance().apply { time = date }
            val today = Calendar.getInstance()
            val minDate = Calendar.getInstance().apply { add(Calendar.YEAR, -150) }
            
            if (cal.after(today)) {
                return "$fieldName tidak boleh di masa depan"
            }
            if (cal.before(minDate)) {
                return "$fieldName terlalu lama (maksimal 150 tahun yang lalu)"
            }
        } catch (e: Exception) {
            return "$fieldName tidak valid: ${e.message}"
        }
        
        return null
    }

    // ── RESET ────────────────────────────────────────────────────────────────

    fun saveUserBankAccount(bankName: String, accountNo: String, accountHolder: String) {
        userPreferences.saveBankAccount(bankName, accountNo, accountHolder)
        savedBankName = bankName
        savedAccountNo = accountNo
        savedAccountHolder = accountHolder
    }
    
    fun clearUserBankAccount() {
        userPreferences.clearBankAccount()
        savedBankName = ""
        savedAccountNo = ""
        savedAccountHolder = ""
    }

    fun resetBooking() {
        originStation = ""
        destinationStation = ""
        ticketCount = 1
        departureDate = ""
        formError = null
        schedules = emptyList()
        selectedSchedule = null
        selectedCoachClass = null
        selectedCarriage = null
        selectedSeats.clear()
        occupiedSeats.clear()
        bookingData = null
        emailSentStatus = null
        clearSelectedPassengers()
        stopTimer()
    }

    // ── TIMER LOGIC ──────────────────────────────────────────────────────────

    fun startSeatLockTimer() {
        if (seatLockTimeLeft <= 0) {
            seatLockTimeLeft = 10 * 60 // 10 Minutes
            startTimer()
            Log.d("BookingViewModel", "Seat lock timer started: 10m")
        }
    }

    fun startPaymentTimer() {
        if (paymentTimeLeft <= 0) {
            paymentTimeLeft = 20 * 60 // 20 Minutes
            startTimer()
            Log.d("BookingViewModel", "Payment timer started: 20m")
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            timerMutex.withLock {
                // Prevent multiple timer instances
                if (timerJob?.isActive == true) {
                    Log.w("BookingViewModel", "Timer already running, skipping")
                    return@launch
                }
                
                timerJob = viewModelScope.launch {
                    try {
                        while (true) {
                            kotlinx.coroutines.delay(1000)
                            
                            // Jangan countdown jika sudah dibayar
                            val currentBooking = bookingData
                            if (currentBooking?.isPaid == true) {
                                Log.i("BookingViewModel", "Timer stopped: ticket already paid")
                                stopTimer()
                                break
                            }
                            
                            if (seatLockTimeLeft > 0) seatLockTimeLeft--
                            if (paymentTimeLeft > 0) paymentTimeLeft--
                            
                            // Logic: Release seats if lock expires but not paid
                            if (seatLockTimeLeft == 0 && bookingData != null && bookingData?.isPaid == false) {
                                releaseSeats()
                            }

                            // PENTING: Jangan batalkan tiket otomatis saat timer habis
                            // User harus manual cancel atau admin yang cancel
                            if (paymentTimeLeft == 0) {
                                Log.w("BookingViewModel", "Payment timer expired for ${bookingData?.bookingCode}")
                                // Hanya log warning, TIDAK membatalkan tiket
                                break
                            }
                            
                            if (seatLockTimeLeft == 0 && paymentTimeLeft == 0) break
                        }
                    } catch (e: Exception) {
                        Log.e("BookingViewModel", "Timer error: ${e.message}", e)
                    }
                }
            }
        }
    }

    private fun releaseSeats() {
        val current = bookingData ?: return
        if (current.selectedSeats.isNotEmpty()) {
            bookingData = current.copy(selectedSeats = emptyList())
            Log.w("BookingViewModel", "Seat lock expired! Seats for ${current.bookingCode} have been released.")
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        seatLockTimeLeft = 0
        paymentTimeLeft = 0
    }

    // ── SETTINGS ─────────────────────────────────────────────────────────────

    fun getNotifPromo(): Boolean = userPreferences.getNotifPromo()
    fun setNotifPromo(v: Boolean) = userPreferences.setNotifPromo(v)
    fun getNotifTravel(): Boolean = userPreferences.getNotifTravel()
    fun setNotifTravel(v: Boolean) = userPreferences.setNotifTravel(v)
    fun getNotifUpdate(): Boolean = userPreferences.getNotifUpdate()
    fun setNotifUpdate(v: Boolean) = userPreferences.setNotifUpdate(v)
    fun getNotifEmail(): Boolean = userPreferences.getNotifEmail()
    fun setNotifEmail(v: Boolean) = userPreferences.setNotifEmail(v)
    fun getLanguage(): String = userPreferences.getLanguage()
    fun setLanguage(v: String) = userPreferences.setLanguage(v)
    fun getBiometric(): Boolean = userPreferences.getBiometric()
    fun setBiometric(v: Boolean) = userPreferences.setBiometric(v)
    fun getSaveLogin(): Boolean = userPreferences.getSaveLogin()
    fun setSaveLogin(v: Boolean) = userPreferences.setSaveLogin(v)

    fun clearLoginError() { loginError = null }
    fun clearRegisterError() { registerError = null }
    fun clearFormError() { formError = null }
}

// ── Extension: Convert API response to domain model ──────────────────────────

fun BookingResponse.toBookingData(fallbackName: String = ""): BookingData {
    var calculatedIsUsed = isUsed == 1
    
    // Auto-mark as used if the arrival time has passed
    if (isPaid == 1 && !calculatedIsUsed) {
        try {
            val sdf = java.text.SimpleDateFormat("EEEE, dd MMM yyyy", java.util.Locale("id", "ID"))
            val date = sdf.parse(departureDate)
            if (date != null) {
                val cal = java.util.Calendar.getInstance()
                cal.time = date
                
                // arrivalTime format: "HH:mm" or "HH:mm (+X hari)"
                val timeStr = arrivalTime.substringBefore(" (")
                val timeParts = timeStr.split(":")
                
                if (timeParts.size >= 2) {
                    val hours = timeParts[0].toIntOrNull() ?: 0
                    val minutes = timeParts[1].toIntOrNull() ?: 0
                    cal.set(java.util.Calendar.HOUR_OF_DAY, hours)
                    cal.set(java.util.Calendar.MINUTE, minutes)
                    cal.set(java.util.Calendar.SECOND, 0)
                    
                    if (arrivalTime.contains("(+")) {
                         cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
                    }
                    
                    if (System.currentTimeMillis() > cal.timeInMillis) {
                        calculatedIsUsed = true
                    }
                }
            }
        } catch (e: Exception) {
            // Ignore parsing errors, keep default value
            android.util.Log.e("BookingViewModel", "Error calculating isUsed status: ${e.message}")
        }
    }

    return BookingData(
        userName = userName.ifBlank { fallbackName },
        originStation = originStation,
        destinationStation = destinationStation,
        ticketCount = ticketCount,
        departureDate = departureDate,
        departureTime = departureTime,
        arrivalTime = arrivalTime,
        duration = duration,
        coachClass = try { CoachClass.valueOf(coachClass) } catch (_: Exception) { CoachClass.EKONOMI },
        pricePerTicket = pricePerTicket,
        totalPrice = totalPrice,
        bookingCode = bookingCode,
        selectedCarriage = selectedCarriage,
        selectedSeats = if (selectedSeats.isBlank()) emptyList() else selectedSeats.split(","),
        passengers = passengers?.map { 
            com.example.whoossh.model.PassengerInfo(
                name = it.name,
                identityNo = it.identityNo,
                passengerType = it.passengerType,
                seatNumber = it.seatNumber
            )
        } ?: emptyList(),
        bookingTimestamp = bookingTimestamp,
        isUsed = calculatedIsUsed,
        isPaid = isPaid == 1,
        isCancelled = isCancelled == 1,
        refundAmount = refundAmount,
        status = when {
            (isCancelled == 1 && isPaid == 1) || (isCancelled == 1 && refundAmount > 0) -> OrderStatus.REFUNDED
            isCancelled == 1 -> OrderStatus.CANCELLED
            calculatedIsUsed -> OrderStatus.CHECKED
            isPaid == 1 -> OrderStatus.PAID
            else -> OrderStatus.UNPAID
        }
    )
}
fun PassengerResponse.toDomainModel(): Passenger {
    return Passenger(
        id = id.toString(),
        name = name,
        identityNo = identityNo,
        gender = gender,
        dateOfBirth = dateOfBirth,
        passengerType = passengerType,
        discountType = discountType,
        country = country,
        documentType = documentType,
        expiryDate = expiryDate,
        whatsapp = whatsapp,
        email = email,
        isSaved = isSaved
    )
}

fun Passenger.toRequestModel(userId: Int? = null, seatNumber: String? = null): PassengerRequest {
    return PassengerRequest(
        id = id.toIntOrNull(),
        name = name,
        identityNo = identityNo,
        gender = gender,
        dateOfBirth = dateOfBirth,
        passengerType = passengerType,
        discountType = discountType,
        country = country,
        documentType = documentType,
        expiryDate = expiryDate,
        whatsapp = whatsapp,
        email = email,
        isSaved = isSaved,
        userId = userId,
        seatNumber = seatNumber
    )
}
