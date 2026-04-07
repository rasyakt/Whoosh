package com.example.whoossh.model

data class Station(
    val id: Int,
    val name: String
)

data class StopDetail(
    val stationName: String,
    val arrivalTime: String,
    val departureTime: String,
    val stopDuration: String
)

data class Schedule(
    val departureTime: String,
    val arrivalTime: String,
    val duration: Int, // in minutes
    val originStation: String,
    val destinationStation: String,
    val price: Int = 300000,
    val trainCode: String = "G1101",
    val stops: String = "Langsung",
    val stopDetails: List<StopDetail> = emptyList()
)

enum class CoachClass(val displayName: String, val description: String) {
    EKONOMI("Ekonomi", "Kursi standar dengan AC dan colokan listrik"),
    BISNIS("Bisnis", "Kursi lebih lebar, sandaran kaki, snack box"),
    VIP("VIP", "Kursi premium reclining, makanan lengkap, WiFi")
}

data class BookingData(
    val userName: String = "",
    val originStation: String = "",
    val destinationStation: String = "",
    val ticketCount: Int = 1,
    val departureDate: String = "",
    val departureTime: String = "",
    val arrivalTime: String = "",
    val duration: Int = 0,
    val coachClass: CoachClass = CoachClass.EKONOMI,
    val pricePerTicket: Int = 0,
    val totalPrice: Int = 0,
    val bookingCode: String = "",
    val selectedCarriage: Int = 1,
    val selectedSeats: List<String> = emptyList()
)
