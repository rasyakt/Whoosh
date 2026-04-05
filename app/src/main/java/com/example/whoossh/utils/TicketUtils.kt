package com.example.whoossh.utils

import com.example.whoossh.model.CoachClass
import java.text.NumberFormat
import java.util.Locale

object TicketUtils {

    fun getTicketPrice(ticketCount: Int, coachClass: CoachClass): Int {
        return when (coachClass) {
            CoachClass.EKONOMI -> when {
                ticketCount in 1..2 -> 300000
                ticketCount in 3..5 -> 285000
                ticketCount in 6..8 -> 270000
                ticketCount in 9..10 -> 250000
                else -> 300000
            }
            CoachClass.BISNIS -> when {
                ticketCount in 1..2 -> 450000
                ticketCount in 3..5 -> 435000
                ticketCount in 6..8 -> 420000
                ticketCount in 9..10 -> 400000
                else -> 450000
            }
            CoachClass.VIP -> when {
                ticketCount in 1..2 -> 600000
                ticketCount in 3..5 -> 575000
                ticketCount in 6..8 -> 550000
                ticketCount in 9..10 -> 525000
                else -> 600000
            }
        }
    }

    fun formatRupiah(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount.toLong()).replace(",00", "")
    }

    fun generateBookingCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return "WSH-" + (1..8).map { chars.random() }.joinToString("")
    }

    fun calculateArrivalTime(departureTime: String, durationMinutes: Int): String {
        val parts = departureTime.split(":")
        val hours = parts[0].toInt()
        val minutes = parts[1].toInt()

        val totalMinutes = hours * 60 + minutes + durationMinutes
        val arrivalHours = (totalMinutes / 60) % 24
        val arrivalMinutes = totalMinutes % 60

        return String.format("%02d:%02d", arrivalHours, arrivalMinutes)
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
        if (ticketCount < 1 || ticketCount > 10) return "Jumlah tiket harus 1-10"
        if (date.isEmpty()) return "Pilih tanggal keberangkatan"
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
                val arrivalTime = calculateArrivalTime(currentTime, duration)
                
                if (stationStr == destination) {
                    details.add(com.example.whoossh.model.StopDetail(stationStr, arrivalTime, arrivalTime, "--"))
                } else {
                    val layover = 3 // 3 minutes layover as inferred from StationData duration matrix
                    val departureTimeNew = calculateArrivalTime(arrivalTime, layover)
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
