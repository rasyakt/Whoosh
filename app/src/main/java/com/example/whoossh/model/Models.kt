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
    val selectedSeats: List<String> = emptyList(),
    val bookingTimestamp: Long = System.currentTimeMillis(),
    val isUsed: Boolean = false
)

data class User(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class Promo(
    val id: Int,
    val title: String,
    val description: String,
    val discount: String,
    val validUntil: String,
    val code: String,
    val minPurchase: String = ""
)

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val date: String,
    val isRead: Boolean = false
)
