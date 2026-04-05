package com.example.whoossh.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.whoossh.data.StationData
import com.example.whoossh.model.BookingData
import com.example.whoossh.model.CoachClass
import com.example.whoossh.model.Schedule
import com.example.whoossh.utils.TicketUtils

class BookingViewModel : ViewModel() {

    // Login State
    var isLoggedIn by mutableStateOf(false)
        private set
    var userName by mutableStateOf("")
        private set
    var loginError by mutableStateOf<String?>(null)
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

    // Booking Result
    var bookingData by mutableStateOf<BookingData?>(null)
        private set

    // Login Functions
    fun login(username: String, password: String): Boolean {
        loginError = null
        if (username.isBlank() || password.isBlank()) {
            loginError = "Username dan password tidak boleh kosong"
            return false
        }
        if (username == "admin" && password == "12345") {
            userName = username
            isLoggedIn = true
            return true
        }
        loginError = "Username atau password salah"
        return false
    }

    fun logout() {
        isLoggedIn = false
        userName = ""
        resetBooking()
    }

    // Station Functions
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

    // Ticket Count Functions
    fun incrementTicket() {
        if (ticketCount < 10) ticketCount++
    }

    fun decrementTicket() {
        if (ticketCount > 1) ticketCount--
    }

    // Date Functions
    fun setDate(date: String) {
        departureDate = date
        formError = null
    }

    // Search Schedule
    fun searchSchedules(): Boolean {
        val error = TicketUtils.validateBookingForm(
            originStation, destinationStation, ticketCount, departureDate
        )
        if (error != null) {
            formError = error
            return false
        }

        val duration = StationData.getDuration(originStation, destinationStation)
        val times = TicketUtils.generateScheduleTimes()

        schedules = times.mapIndexed { index, time ->
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

    // Select Schedule
    fun selectSchedule(schedule: Schedule) {
        selectedSchedule = schedule
    }

    // Coach Class
    fun selectCoachClass(coachClass: CoachClass) {
        selectedCoachClass = coachClass
    }

    fun getPriceForClass(coachClass: CoachClass): Int {
        return TicketUtils.getTicketPrice(ticketCount, coachClass)
    }

    // Confirm Booking
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
            bookingCode = TicketUtils.generateBookingCode()
        )
        bookingData = data
        return data
    }

    // Reset
    fun resetBooking() {
        originStation = ""
        destinationStation = ""
        ticketCount = 1
        departureDate = ""
        formError = null
        schedules = emptyList()
        selectedSchedule = null
        selectedCoachClass = null
        bookingData = null
    }

    fun clearLoginError() {
        loginError = null
    }

    fun clearFormError() {
        formError = null
    }
}
