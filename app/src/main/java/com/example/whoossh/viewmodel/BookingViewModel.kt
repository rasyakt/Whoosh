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
import com.example.whoossh.data.StationData
import com.example.whoossh.data.UserPreferences
import com.example.whoossh.model.BookingData
import com.example.whoossh.model.CoachClass
import com.example.whoossh.model.Schedule
import com.example.whoossh.model.User
import com.example.whoossh.utils.EmailSender
import com.example.whoossh.utils.TicketUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BookingViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferences = UserPreferences(application)

    // Login State
    var isLoggedIn by mutableStateOf(false)
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

    init {
        // Check if user was previously logged in
        val savedUser = userPreferences.getLoggedInUser()
        if (savedUser != null) {
            isLoggedIn = true
            userName = savedUser.name
            userEmail = savedUser.email
            userPhone = savedUser.phone
            refreshTickets()
        }
    }

    // ── LOGIN & REGISTER ─────────────────────────────────────────────────────

    fun login(email: String, password: String): Boolean {
        loginError = null
        if (email.isBlank() || password.isBlank()) {
            loginError = "Email dan password tidak boleh kosong"
            return false
        }
        val user = userPreferences.loginUser(email, password)
        if (user != null) {
            userName = user.name
            userEmail = user.email
            userPhone = user.phone
            isLoggedIn = true
            userPreferences.saveLoggedInUser(user)
            refreshTickets()
            return true
        }
        loginError = "Email atau password salah"
        return false
    }

    fun register(name: String, email: String, phone: String, password: String, confirmPassword: String): Boolean {
        registerError = null
        if (name.isBlank() || email.isBlank() || phone.isBlank() || password.isBlank()) {
            registerError = "Semua field harus diisi"
            return false
        }
        if (!email.contains("@") || !email.contains(".")) {
            registerError = "Format email tidak valid"
            return false
        }
        if (phone.length < 10) {
            registerError = "Nomor HP minimal 10 digit"
            return false
        }
        if (password.length < 6) {
            registerError = "Password minimal 6 karakter"
            return false
        }
        if (password != confirmPassword) {
            registerError = "Konfirmasi password tidak cocok"
            return false
        }

        val user = User(name = name, email = email, phone = phone, password = password)
        val success = userPreferences.registerUser(user)
        if (!success) {
            registerError = "Email sudah terdaftar"
            return false
        }

        // Auto-login after registration
        userName = name
        userEmail = email
        userPhone = phone
        isLoggedIn = true
        userPreferences.saveLoggedInUser(user)
        return true
    }

    fun logout() {
        isLoggedIn = false
        userName = ""
        userEmail = ""
        userPhone = ""
        userPreferences.clearLoggedInUser()
        resetBooking()
        activeTickets = emptyList()
        historyTickets = emptyList()
    }

    // ── PROFILE ──────────────────────────────────────────────────────────────

    fun updateProfile(name: String, email: String, phone: String): Boolean {
        if (name.isBlank() || email.isBlank() || phone.isBlank()) return false
        if (!email.contains("@") || !email.contains(".")) return false

        val currentUser = userPreferences.getLoggedInUser() ?: return false
        val updated = currentUser.copy(name = name, email = email, phone = phone)
        userPreferences.updateUser(currentUser.email, updated)
        userName = name
        userEmail = email
        userPhone = phone
        return true
    }

    fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): String? {
        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            return "Semua field harus diisi"
        }
        if (newPassword.length < 6) return "Password baru minimal 6 karakter"
        if (newPassword != confirmPassword) return "Konfirmasi password tidak cocok"
        if (oldPassword == newPassword) return "Password baru harus berbeda"

        val success = userPreferences.changePassword(userEmail, oldPassword, newPassword)
        return if (success) null else "Password lama salah"
    }

    // ── TICKETS PERSISTENCE ──────────────────────────────────────────────────

    fun refreshTickets() {
        activeTickets = userPreferences.getActiveTickets()
        historyTickets = userPreferences.getHistoryTickets()
    }

    fun getTotalTrips(): Int = userPreferences.getTicketCount()
    fun getActiveTicketCount(): Int = userPreferences.getActiveTicketCount()

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

    fun searchSchedules(): Boolean {
        val error = TicketUtils.validateBookingForm(
            originStation, destinationStation, ticketCount, departureDate
        )
        if (error != null) {
            formError = error
            return false
        }

        val duration = StationData.getDuration(originStation, destinationStation)
        val allTimes = TicketUtils.generateScheduleTimes()

        // Real-time filtering logic
        val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
        val todayStr = sdf.format(Calendar.getInstance().time)
        val isToday = departureDate == todayStr

        val filteredTimes = if (isToday) {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Calendar.getInstance().time)
            // Filter times that are in the future
            allTimes.filter { it > currentTime }
        } else {
            allTimes
        }

        if (filteredTimes.isEmpty()) {
            formError = if (isToday) {
                "Jadwal untuk hari ini sudah berakhir. Silakan pilih tanggal lain."
            } else {
                "Tidak ada jadwal tersedia untuk rute ini."
            }
            return false
        }

        schedules = filteredTimes.mapIndexed { index, time ->
            Schedule(
                departureTime = time,
                arrivalTime = TicketUtils.calculateArrivalTime(time, duration),
                duration = duration,
                originStation = originStation,
                destinationStation = destinationStation,
                price = TicketUtils.getTicketPrice(1, CoachClass.EKONOMI),
                trainCode = TicketUtils.generateTrainCode(time, index),
                stops = TicketUtils.getStops(originStation, destinationStation),
                stopDetails = TicketUtils.getStopDetails(originStation, destinationStation, time)
            )
        }
        return true
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
            bookingCode = TicketUtils.generateBookingCode(),
            selectedCarriage = selectedCarriage ?: 1,
            selectedSeats = selectedSeats.toList(),
            bookingTimestamp = System.currentTimeMillis(),
            isUsed = false
        )
        bookingData = data

        // Save to persistent storage
        userPreferences.saveTicket(data)
        refreshTickets()

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
