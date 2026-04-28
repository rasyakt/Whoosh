package com.example.whoossh.api

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper
 */
data class ApiResponse<T>(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T? = null
)

/**
 * Non-generic response specifically for tickets list — avoids Gson type erasure issues
 */
data class TicketsListResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<BookingResponse>? = null
)

/**
 * User data from API
 */
data class UserResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String
)

/**
 * Booking/Ticket data from API
 */
data class BookingResponse(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("user_name") val userName: String = "",
    @SerializedName("user_email") val userEmail: String = "",
    @SerializedName("origin_station") val originStation: String,
    @SerializedName("destination_station") val destinationStation: String,
    @SerializedName("departure_date") val departureDate: String,
    @SerializedName("departure_time") val departureTime: String,
    @SerializedName("arrival_time") val arrivalTime: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("coach_class") val coachClass: String,
    @SerializedName("ticket_count") val ticketCount: Int,
    @SerializedName("price_per_ticket") val pricePerTicket: Int,
    @SerializedName("total_price") val totalPrice: Int,
    @SerializedName("selected_carriage") val selectedCarriage: Int,
    @SerializedName("selected_seats") val selectedSeats: String,
    @SerializedName("is_used") val isUsed: Boolean,
    @SerializedName("booking_timestamp") val bookingTimestamp: Long
)

/**
 * Station data from API
 */
data class StationResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String
)

/**
 * Duration data from API
 */
data class DurationResponse(
    @SerializedName("from") val from: String,
    @SerializedName("to") val to: String,
    @SerializedName("duration_minutes") val durationMinutes: Int
)

/**
 * Schedule data from API
 */
data class ScheduleResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("train_code") val trainCode: String,
    @SerializedName("departure_time") val departureTime: String,
    @SerializedName("origin_station") val originStation: String,
    @SerializedName("destination_station") val destinationStation: String
)

/**
 * Promo data from API
 */
data class PromoResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("discount") val discount: String,
    @SerializedName("valid_until") val validUntil: String,
    @SerializedName("code") val code: String,
    @SerializedName("min_purchase") val minPurchase: String = ""
)

// ── Request Bodies ───────────────────────────────────────────────────────────

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)

data class UpdateProfileRequest(
    @SerializedName("user_id") val userId: Int,
    val name: String,
    val email: String,
    val phone: String
)

data class ChangePasswordRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("old_password") val oldPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class CreateBookingRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("booking_code") val bookingCode: String,
    @SerializedName("origin_station") val originStation: String,
    @SerializedName("destination_station") val destinationStation: String,
    @SerializedName("departure_date") val departureDate: String,
    @SerializedName("departure_time") val departureTime: String,
    @SerializedName("arrival_time") val arrivalTime: String,
    val duration: Int,
    @SerializedName("coach_class") val coachClass: String,
    @SerializedName("ticket_count") val ticketCount: Int,
    @SerializedName("price_per_ticket") val pricePerTicket: Int,
    @SerializedName("total_price") val totalPrice: Int,
    @SerializedName("selected_carriage") val selectedCarriage: Int,
    @SerializedName("selected_seats") val selectedSeats: String,
    @SerializedName("booking_timestamp") val bookingTimestamp: Long
)
