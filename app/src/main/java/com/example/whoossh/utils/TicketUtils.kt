package com.example.whoossh.utils

import com.example.whoossh.model.CoachClass
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TicketUtils {

    /**
     * Get price PER TICKET based on ticket count and coach class
     * Bulk discounts apply for multiple tickets
     */
    fun getPricePerTicket(ticketCount: Int, coachClass: CoachClass): Int {
        return when (coachClass) {
            CoachClass.EKONOMI -> when {
                ticketCount in 1..2 -> 300000
                ticketCount in 3..5 -> 285000  // 5% discount
                ticketCount in 6..8 -> 270000  // 10% discount
                ticketCount in 9..10 -> 250000 // 16.7% discount
                else -> 300000
            }
            CoachClass.BISNIS -> when {
                ticketCount in 1..2 -> 450000
                ticketCount in 3..5 -> 435000  // 3.3% discount
                ticketCount in 6..8 -> 420000  // 6.7% discount
                ticketCount in 9..10 -> 400000 // 11.1% discount
                else -> 450000
            }
            CoachClass.VIP -> when {
                ticketCount in 1..2 -> 600000
                ticketCount in 3..5 -> 575000  // 4.2% discount
                ticketCount in 6..8 -> 550000  // 8.3% discount
                ticketCount in 9..10 -> 525000 // 12.5% discount
                else -> 600000
            }
        }
    }

    /**
     * Legacy function for backward compatibility
     * @deprecated Use getPricePerTicket() instead for clarity
     */
    @Deprecated("Use getPricePerTicket() for clarity", ReplaceWith("getPricePerTicket(ticketCount, coachClass)"))
    fun getTicketPrice(ticketCount: Int, coachClass: CoachClass): Int {
        return getPricePerTicket(ticketCount, coachClass)
    }

    fun formatRupiah(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount.toLong()).replace(",00", "")
    }

    fun generateBookingCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        val timestamp = System.currentTimeMillis().toString().takeLast(4)
        val random = (1..4).map { chars.random() }.joinToString("")
        return "WSH-$timestamp$random"
    }

    /**
     * Calculate arrival time with proper date handling
     * Returns Pair<time, dayOffset> where dayOffset is 0 for same day, 1 for next day, etc.
     */
    fun calculateArrivalTimeWithDate(departureTime: String, durationMinutes: Int): Pair<String, Int> {
        val parts = departureTime.split(":")
        if (parts.size != 2) return Pair(departureTime, 0)
        
        val hours = parts[0].toIntOrNull() ?: return Pair(departureTime, 0)
        val minutes = parts[1].toIntOrNull() ?: return Pair(departureTime, 0)

        val totalMinutes = hours * 60 + minutes + durationMinutes
        val dayOffset = totalMinutes / (24 * 60)
        val arrivalHours = (totalMinutes / 60) % 24
        val arrivalMinutes = totalMinutes % 60

        val timeStr = String.format("%02d:%02d", arrivalHours, arrivalMinutes)
        return Pair(timeStr, dayOffset)
    }

    /**
     * Calculate arrival time (legacy - doesn't handle date crossing)
     * @deprecated Use calculateArrivalTimeWithDate() for proper date handling
     */
    @Deprecated("Use calculateArrivalTimeWithDate() for proper date handling")
    fun calculateArrivalTime(departureTime: String, durationMinutes: Int): String {
        return calculateArrivalTimeWithDate(departureTime, durationMinutes).first
    }

    /**
     * Format arrival time with date indicator if next day
     */
    fun formatArrivalTime(departureTime: String, durationMinutes: Int): String {
        val (time, dayOffset) = calculateArrivalTimeWithDate(departureTime, durationMinutes)
        return if (dayOffset > 0) {
            "$time (+$dayOffset hari)"
        } else {
            time
        }
    }

    fun generateScheduleTimes(): List<String> {
        val times = mutableListOf<String>()
        var hour = 6
        var minute = 0
        while (hour < 22) {
            if (hour == 21 && minute > 30) break
            times.add(String.format("%02d:%02d", hour, minute))
            minute += 30
            if (minute >= 60) {
                minute = 0
                hour++
            }
        }
        return times
    }

    fun validateBookingForm(
        origin: String,
        destination: String,
        ticketCount: Int,
        date: String
    ): String? {
        if (origin.isEmpty()) return "Pilih stasiun keberangkatan"
        if (destination.isEmpty()) return "Pilih stasiun tujuan"
        if (origin == destination) return "Stasiun keberangkatan dan tujuan tidak boleh sama"
        if (ticketCount < 1) return "Jumlah tiket minimal 1"
        if (ticketCount > 10) return "Jumlah tiket maksimal 10"
        if (date.isEmpty()) return "Pilih tanggal keberangkatan"
        
        // Validate date is not in the past
        try {
            val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
            val selectedDate = sdf.parse(date)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            if (selectedDate != null && selectedDate.before(today.time)) {
                return "Tanggal keberangkatan tidak boleh di masa lalu"
            }
        } catch (e: Exception) {
            return "Format tanggal tidak valid"
        }
        
        return null
    }

    fun getStops(origin: String, destination: String): String {
        val stations = listOf("Tegalluar", "Padalarang", "Karawang", "Halim")
        val startIndex = stations.indexOf(origin)
        val endIndex = stations.indexOf(destination)
        if (startIndex == -1 || endIndex == -1 || Math.abs(startIndex - endIndex) <= 1) {
            return "Langsung"
        }
        
        val stops = mutableListOf<String>()
        val range = if (startIndex < endIndex) (startIndex + 1) until endIndex else (startIndex - 1) downTo (endIndex + 1)
        for (i in range) {
            stops.add(stations[i])
        }
        return "Transit: " + stops.joinToString(", ")
    }

    fun generateTrainCode(departureTime: String, index: Int): String {
        // Mock Whoosh train code eg G1105
        return "G" + (1100 + index + 1).toString()
    }

    fun getStopDetails(origin: String, destination: String, departureTime: String): List<com.example.whoossh.model.StopDetail> {
        val stations = listOf("Tegalluar", "Padalarang", "Karawang", "Halim")
        val startIndex = stations.indexOf(origin)
        val endIndex = stations.indexOf(destination)
        if (startIndex == -1 || endIndex == -1) return emptyList()

        val details = mutableListOf<com.example.whoossh.model.StopDetail>()
        val range = if (startIndex < endIndex) startIndex..endIndex else startIndex downTo endIndex
        
        var currentTime = departureTime
        var prevStation: String? = null
        
        for (i in range) {
            val stationStr = stations[i]
            
            if (stationStr == origin) {
                details.add(com.example.whoossh.model.StopDetail(stationStr, currentTime, currentTime, "--"))
            } else {
                val duration = com.example.whoossh.data.StationData.getDuration(prevStation!!, stationStr)
                val (arrivalTime, _) = calculateArrivalTimeWithDate(currentTime, duration)
                
                if (stationStr == destination) {
                    details.add(com.example.whoossh.model.StopDetail(stationStr, arrivalTime, arrivalTime, "--"))
                } else {
                    val layover = 3 // 3 minutes layover
                    val (departureTimeNew, _) = calculateArrivalTimeWithDate(arrivalTime, layover)
                    details.add(com.example.whoossh.model.StopDetail(stationStr, arrivalTime, departureTimeNew, "$layover Menit"))
                    currentTime = departureTimeNew
                    prevStation = stationStr
                    continue
                }
            }
            prevStation = stationStr
        }
        return details
    }
}
