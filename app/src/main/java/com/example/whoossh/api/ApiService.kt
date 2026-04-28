package com.example.whoossh.api

import retrofit2.Response
import retrofit2.http.*

/**
 * Retrofit API interface for Whoossh PHP Backend
 */
interface ApiService {

    // ── AUTH ─────────────────────────────────────────────────────────────────

    @POST("auth/login.php")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<UserResponse>>

    @POST("auth/register.php")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<UserResponse>>

    @POST("auth/update_profile.php")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserResponse>>

    @POST("auth/change_password.php")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Nothing>>

    // ── BOOKING ──────────────────────────────────────────────────────────────

    @POST("booking/create.php")
    suspend fun createBooking(@Body request: CreateBookingRequest): Response<ApiResponse<BookingResponse>>

    @GET("booking/get_tickets.php")
    suspend fun getTickets(@Query("user_id") userId: Int): Response<ApiResponse<List<BookingResponse>>>

    // Non-generic version — avoids Gson type erasure
    @GET("booking/get_tickets.php")
    suspend fun getTicketsList(@Query("user_id") userId: Int): Response<TicketsListResponse>

    // Raw string fallback
    @GET("booking/get_tickets.php")
    suspend fun getTicketsRaw(@Query("user_id") userId: Int): Response<okhttp3.ResponseBody>

    @GET("booking/get_ticket_by_code.php")
    suspend fun getTicketByCode(@Query("booking_code") code: String): Response<ApiResponse<BookingResponse>>

    // ── STATION ──────────────────────────────────────────────────────────────

    @GET("station/get_all.php")
    suspend fun getStations(): Response<ApiResponse<List<StationResponse>>>

    @GET("station/get_duration.php")
    suspend fun getDuration(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<ApiResponse<DurationResponse>>

    @GET("schedule/get_by_route.php")
    suspend fun getSchedules(
        @Query("from") from: String,
        @Query("to") to: String
    ): Response<ApiResponse<List<ScheduleResponse>>>

    // ── PROMO ────────────────────────────────────────────────────────────────

    @GET("promo/get_all.php")
    suspend fun getPromos(): Response<ApiResponse<List<PromoResponse>>>
}
