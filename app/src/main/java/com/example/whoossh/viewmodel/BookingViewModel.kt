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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.whoossh.utils.EmailSender
import com.example.whoossh.utils.TicketUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)
    private val api = ApiClient.apiService

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

    // Booking Result
    var bookingData by mutableStateOf<BookingData?>(null)
        private set

    // Tickets
    var activeTickets by mutableStateOf<List<BookingData>>(emptyList())
        private set
    var historyTickets by mutableStateOf<List<BookingData>>(emptyList())
        private set

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
        resetBooking()
        activeTickets = emptyList()
        historyTickets = emptyList()
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
                        return@launch
                    }
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "refreshTickets Strategy 2 error: ${e.javaClass.simpleName} - ${e.message}", e)
            }

            Log.w("BookingViewModel", "refreshTickets: all strategies failed, keeping existing ${activeTickets.size} local tickets")
        }
    }

    private fun applyTickets(tickets: List<BookingResponse>) {
        val bookings = tickets.mapNotNull { ticket ->
            try {
                ticket.toBookingData(userName)
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error parsing ticket ${ticket.bookingCode}: ${e.message}")
                null
            }
        }

        // Dedup berdasarkan bookingCode (menghindari duplikat dari confirmBooking lokal)
        activeTickets = bookings.filter { !it.isUsed }.distinctBy { it.bookingCode }
        historyTickets = bookings.filter { it.isUsed }.distinctBy { it.bookingCode }
        Log.i("BookingViewModel", "applyTickets: ${activeTickets.size} active, ${historyTickets.size} history")
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
                        Schedule(
                            departureTime = s.departureTime,
                            arrivalTime = TicketUtils.calculateArrivalTime(s.departureTime, duration),
                            duration = duration,
                            originStation = s.originStation,
                            destinationStation = s.destinationStation,
                            price = TicketUtils.getTicketPrice(1, CoachClass.EKONOMI),
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
                        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
                        mappedSchedules.filter { it.departureTime > currentTime }
                    } else {
                        mappedSchedules
                    }

                    Log.i("BookingViewModel", "Schedules synced: ${schedules.size} items from API (Filtered: $isToday)")
                } else {
                    Log.e("BookingViewModel", "Failed to fetch schedules: ${response.message()}")
                    // Fallback ke local generation jika API gagal (opsional, tapi untuk demo kita biarkan kosong agar terlihat sync gap sudah diperbaiki)
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
        selectedSchedule = schedule
    }

    // ── COACH CLASS & SEATS ──────────────────────────────────────────────────

    fun selectCoachClass(coachClass: CoachClass) {
        if (selectedCoachClass != coachClass) {
            selectedCoachClass = coachClass
            selectedCarriage = getAvailableCarriages(coachClass).firstOrNull()
            selectedSeats.clear()
        }
    }

    fun getAvailableCarriages(coachClass: CoachClass?): List<Int> {
        return when (coachClass) {
            CoachClass.VIP -> listOf(1)
            CoachClass.BISNIS -> listOf(8)
            CoachClass.EKONOMI -> listOf(2, 3, 4, 5, 6, 7)
            null -> emptyList()
        }
    }

    fun selectCarriage(carriage: Int) {
        if (selectedCarriage != carriage) {
            selectedCarriage = carriage
            selectedSeats.clear()
        }
    }

    fun toggleSeatSelection(seatId: String) {
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
        return TicketUtils.getTicketPrice(ticketCount, coachClass)
    }

    // ── CONFIRM BOOKING ──────────────────────────────────────────────────────

    fun confirmBooking(): BookingData {
        val schedule = selectedSchedule!!
        val coach = selectedCoachClass!!
        val pricePerTicket = TicketUtils.getTicketPrice(ticketCount, coach)
        val total = pricePerTicket * ticketCount
        val bookingCode = TicketUtils.generateBookingCode()
        val timestamp = System.currentTimeMillis()

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
            bookingTimestamp = timestamp,
            isUsed = false
        )
        bookingData = data

        // Langsung tambahkan ke daftar tiket aktif agar segera tampil di halaman Tiket
        activeTickets = listOf(data) + activeTickets
        Log.i("BookingViewModel", "Tiket ditambahkan ke activeTickets: ${data.bookingCode}, total: ${activeTickets.size}")

        // Simpan ke server via API
        Log.i("BookingViewModel", "Attempting to save booking: code=$bookingCode, userId=$userId")

        if (userId <= 0) {
            Log.e("BookingViewModel", "GAGAL: userId=$userId tidak valid! User harus login ulang.")
            return data
        }

        viewModelScope.launch {
            try {
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
                    passengers = _selectedPassengers.value.mapIndexed { index, p ->
                        p.toRequestModel(
                            userId = userId,
                            seatNumber = if (index < selectedSeats.size) selectedSeats[index] else ""
                        )
                    }
                )

                Log.d("BookingViewModel", "Sending booking request to API...")
                val response = withContext(Dispatchers.IO) {
                    api.createBooking(request)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    Log.i("BookingViewModel", "✅ Booking saved to server: $bookingCode")
                    refreshTickets()
                } else {
                    val errorMsg = response.body()?.message ?: "Unknown error"
                    val httpCode = response.code()
                    Log.e("BookingViewModel", "❌ Failed to save booking: HTTP $httpCode - $errorMsg")
                    // Coba baca error body jika response body null
                    if (response.body() == null) {
                        val errorBody = response.errorBody()?.string()
                        Log.e("BookingViewModel", "Error body: $errorBody")
                    }
                }
            } catch (e: java.net.ConnectException) {
                Log.e("BookingViewModel", "❌ Tidak bisa terhubung ke server! Pastikan Laragon/Apache running dan URL benar.", e)
            } catch (e: java.net.SocketTimeoutException) {
                Log.e("BookingViewModel", "❌ Koneksi timeout ke server!", e)
            } catch (e: Exception) {
                Log.e("BookingViewModel", "❌ Create booking error: ${e.javaClass.simpleName} - ${e.message}", e)
            }
        }

        // Kirim e-ticket via email secara background
        if (userEmail.isNotBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                val success = EmailSender.sendETicket(userEmail, data)
                emailSentStatus = success
                if (success) {
                    Log.i("BookingViewModel", "E-ticket dikirim ke $userEmail")
                } else {
                    Log.w("BookingViewModel", "Gagal mengirim e-ticket ke $userEmail")
                }
            }
        }

        return data
    }

    fun viewTicket(ticket: BookingData) {
        bookingData = ticket
    }

    fun loadTicketByCode(code: String) {
        Log.d("BookingViewModel", "Mencoba memuat tiket: $code")

        viewModelScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    api.getTicketByCode(code)
                }

                if (response.isSuccessful && response.body()?.status == "success") {
                    val ticket = response.body()!!.data!!
                    bookingData = ticket.toBookingData(ticket.userName.ifBlank { userName })
                    Log.i("BookingViewModel", "Tiket ditemukan dari server: $code")
                } else {
                    // Fallback: cari di list lokal
                    val localTicket = activeTickets.find { it.bookingCode.equals(code, ignoreCase = true) }
                        ?: historyTickets.find { it.bookingCode.equals(code, ignoreCase = true) }

                    if (localTicket != null) {
                        bookingData = localTicket
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
                    bookingData = localTicket
                }
            }
        }
    }

    // ── PASSENGER MANAGEMENT ─────────────────────────────────────────────────

    fun addPassenger(passenger: Passenger) {
        val current = _selectedPassengers.value.toMutableList()
        if (current.size < 15 && current.none { it.id == passenger.id }) {
            current.add(passenger)
            _selectedPassengers.value = current
            Log.i("BookingViewModel", "Passenger added: ${passenger.name}, total: ${current.size}")
        }
    }

    fun removePassenger(passenger: Passenger) {
        val current = _selectedPassengers.value.toMutableList()
        current.removeAll { it.id == passenger.id }
        _selectedPassengers.value = current
        Log.i("BookingViewModel", "Passenger removed: ${passenger.name}, remaining: ${current.size}")
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
                val response = if (passenger.id.toIntOrNull() != null) {
                    // Update existing
                    api.updatePassenger(request.copy(id = passenger.id.toInt()))
                } else {
                    // Add new
                    api.addPassenger(request)
                }

                if (response.isSuccessful) {
                    Log.i("BookingViewModel", "Passenger saved to database: ${passenger.name}")
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
                }
            } catch (e: Exception) {
                Log.e("BookingViewModel", "Error refreshing passengers: ${e.message}")
            }
        }
    }

    fun clearSelectedPassengers() {
        _selectedPassengers.value = emptyList()
    }

    fun getSavedPassengerById(id: String): Passenger? {
        return _savedPassengers.value.find { it.id == id }
    }

    fun getSelectedPassengerById(id: String): Passenger? {
        return _selectedPassengers.value.find { it.id == id }
    }

    // ── RESET ────────────────────────────────────────────────────────────────

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
        bookingData = null
        emailSentStatus = null
        clearSelectedPassengers()
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
        bookingTimestamp = bookingTimestamp,
        isUsed = isUsed
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
