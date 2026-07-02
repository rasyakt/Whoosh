package com.example.whoossh.utils

import com.example.whoossh.model.CoachClass
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TicketUtils {

    /**
     * Get price PER TICKET based on route, coach class, and departure time (Dynamic Pricing)
     */
    fun getPricePerTicket(origin: String, destination: String, coachClass: CoachClass, departureTime: String = ""): Int {
        val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
        val originIdx = stations.indexOf(origin)
        val destIdx = stations.indexOf(destination)
        
        if (originIdx == -1 || destIdx == -1) return 300000

        val distance = Math.abs(originIdx - destIdx)
        
        // Harga Dasar Ekonomi Premium berdasarkan Rute (Sesuai Aplikasi Resmi)
        val baseEconomyPrice = when {
            // Rute Terpendek (1 stasiun)
            distance == 1 -> {
                when {
                    origin.contains("Halim") || destination.contains("Halim") -> 100000 // Halim - Karawang
                    origin.contains("Tegalluar") || destination.contains("Tegalluar") -> 75000 // Padalarang - Tegalluar
                    else -> 150000 // Karawang - Padalarang (Update: 150k)
                }
            }
            // Rute Menengah (2 stasiun)
            distance == 2 -> {
                when {
                    origin.contains("Tegalluar") || destination.contains("Tegalluar") -> 150000 // Tegalluar - Karawang
                    else -> 200000 // Halim - Padalarang
                }
            }
            // Rute Jauh (Halim - Tegalluar)
            distance == 3 -> {
                val isEastbound = destIdx > originIdx
                val hour = departureTime.split(":")[0].toIntOrNull() ?: 12
                
                if (isEastbound) {
                    // Halim -> Tegalluar: Rp 350.000 (Semua Jam di foto)
                    350000
                } else {
                    // Tegalluar -> Halim: Rp 300.000 - Rp 325.000
                    when {
                        hour in 7..8 -> 325000 // Contoh G1010 jam 07:35
                        else -> 300000
                    }
                }
            }
            else -> 300000
        }

        return when (coachClass) {
            CoachClass.VIP -> 600000
            CoachClass.BISNIS -> 450000
            CoachClass.EKONOMI -> baseEconomyPrice
        }
    }

    fun formatRupiah(amount: Int): String {
        val format = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return format.format(amount.toLong()).replace(",00", "")
    }

    /**
     * Get available coach classes for a specific route
     */
    fun getAvailableClasses(origin: String, destination: String): List<CoachClass> {
        val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
        val originIdx = stations.indexOf(origin)
        val destIdx = stations.indexOf(destination)
        
        if (originIdx == -1 || destIdx == -1) return CoachClass.values().toList()

        val isHalimKarawang = (origin.contains("Halim") && destination.contains("Karawang")) || 
                             (origin.contains("Karawang") && destination.contains("Halim"))
        val isPadalarangTegalluar = (origin.contains("Padalarang") && destination.contains("Tegalluar")) || 
                                   (origin.contains("Tegalluar") && destination.contains("Padalarang"))
        
        return if (isHalimKarawang || isPadalarangTegalluar) {
            // Hanya rute ini yang dibatasi ke Ekonomi Premium
            listOf(CoachClass.EKONOMI)
        } else {
            // Karawang-Padalarang dan rute lainnya menyediakan semua kelas
            CoachClass.values().toList()
        }
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
    fun formatArrivalTime(departureTime: String, durationMinutes: Int, origin: String = "", destination: String = ""): String {
        // Jika rute utama, hitung durasi spesifik (Express 47m vs Regular 54m)
        var actualDuration = durationMinutes
        if ((origin.contains("Halim") && destination.contains("Tegalluar")) || 
            (origin.contains("Tegalluar") && destination.contains("Halim"))) {
            
            val times = generateScheduleTimes(origin, destination)
            val index = times.indexOf(departureTime)
            
            if (origin.contains("Halim")) {
                actualDuration = if (index % 2 == 0) 54 else 47
            } else {
                actualDuration = if (index % 2 == 0) 47 else 54
            }
        }
        
        val (time, dayOffset) = calculateArrivalTimeWithDate(departureTime, actualDuration)
        return if (dayOffset > 0) {
            "$time (+$dayOffset hari)"
        } else {
            time
        }
    }

    fun generateScheduleTimes(origin: String = "", destination: String = ""): List<String> {
        val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
        val originIdx = stations.indexOf(origin)
        val destIdx = stations.indexOf(destination)
        
        if (originIdx == -1 || destIdx == -1) return emptyList()

        val isEastbound = destIdx > originIdx
        val baselineTimes = if (isEastbound) {
            listOf(
                "06:25", "07:00", "07:25", "08:00", "08:25", "09:00", "09:25", "10:00", "10:25", "11:00",
                "11:25", "12:00", "12:25", "13:00", "13:25", "14:00", "14:25", "15:00", "15:25", "16:00",
                "16:25", "17:00", "17:25", "18:00", "18:25", "19:00", "19:25", "20:00", "20:25", "21:00", "21:25"
            )
        } else {
            listOf(
                "06:05", "06:35", "07:05", "07:35", "08:05", "08:35", "09:05", "09:35", "10:05", "10:35",
                "11:05", "11:35", "12:05", "12:35", "13:05", "13:35", "14:05", "14:35", "15:05", "15:35",
                "16:05", "16:35", "17:05", "17:35", "18:05", "18:35", "19:05", "19:35", "20:05", "20:35", "21:05"
            )
        }

        val startStation = if (isEastbound) "Halim" else "Tegalluar"
        if (origin == startStation) return baselineTimes

        // Kalkulasi Offset Dinamis berdasarkan Kereta (Regular vs Express)
        return baselineTimes.mapIndexed { index, time ->
            val offset = if (isEastbound) {
                // Eastbound: Halim -> ...
                when (origin) {
                    "Karawang" -> 17 // Hanya muncul jika index % 2 == 0
                    "Padalarang" -> if (index % 2 == 0) 40 else 33
                    "Tegalluar" -> if (index % 2 == 0) 54 else 47
                    else -> 0
                }
            } else {
                // Westbound: Tegalluar -> ...
                when (origin) {
                    "Padalarang" -> 18
                    "Karawang" -> 39 // Hanya muncul jika index % 2 != 0
                    "Halim" -> if (index % 2 != 0) 54 else 47
                    else -> 0
                }
            }
            calculateArrivalTimeWithDate(time, offset).first
        }
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

    fun getActualDuration(departureTime: String, origin: String, destination: String): Int {
        val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
        val originIdx = stations.indexOf(origin)
        val destIdx = stations.indexOf(destination)
        if (originIdx == -1 || destIdx == -1) return 45

        val isEastbound = destIdx > originIdx
        val startStation = if (isEastbound) "Halim" else "Tegalluar"
        
        // Cari jam keberangkatan di stasiun AWAL (Baseline) dengan simulasi
        val baselineTimes = if (isEastbound) {
            listOf("06:25", "07:00", "07:25", "08:00", "08:25", "09:00", "09:25", "10:00", "10:25", "11:00", "11:25", "12:00", "12:25", "13:00", "13:25", "14:00", "14:25", "15:00", "15:25", "16:00", "16:25", "17:00", "17:25", "18:00", "18:25", "19:00", "19:25", "20:00", "20:25", "21:00", "21:25")
        } else {
            listOf("06:05", "06:35", "07:05", "07:35", "08:05", "08:35", "09:05", "09:35", "10:05", "10:35", "11:05", "11:35", "12:05", "12:35", "13:05", "13:35", "14:05", "14:35", "15:05", "15:35", "16:05", "16:35", "17:05", "17:35", "18:05", "18:35", "19:05", "19:35", "20:05", "20:35", "21:05")
        }

        var foundIndex = -1
        for (i in baselineTimes.indices) {
            val offsetAtOrigin = getOffsetForTrain(i, isEastbound, origin)
            val timeAtOrigin = calculateArrivalTimeWithDate(baselineTimes[i], offsetAtOrigin).first
            if (timeAtOrigin == departureTime) {
                foundIndex = i
                break
            }
        }
        
        val startOffset = getOffsetForTrain(foundIndex, isEastbound, origin)
        val endOffset = getOffsetForTrain(foundIndex, isEastbound, destination)
        
        return endOffset - startOffset
    }

    private fun getOffsetForTrain(index: Int, isEastbound: Boolean, targetStation: String): Int {
        if (index == -1) return 0
        return if (isEastbound) {
            when (targetStation) {
                "Halim" -> 0
                "Karawang" -> 17
                "Padalarang" -> if (index % 2 == 0) 40 else 33
                "Tegalluar" -> if (index % 2 == 0) 54 else 47
                else -> 0
            }
        } else {
            when (targetStation) {
                "Tegalluar" -> 0
                "Padalarang" -> 18
                "Karawang" -> 39
                "Halim" -> if (index % 2 != 0) 54 else 47
                else -> 0
            }
        }
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

    fun generateTrainCode(departureTime: String, origin: String = ""): String {
        val times = generateScheduleTimes(origin, if(origin.contains("Halim")) "Tegalluar" else "Halim")
        val index = times.indexOf(departureTime)
        
        return if (origin.contains("Halim")) {
            "G" + (1003 + (index * 2)).toString()
        } else {
            "G" + (1004 + (index * 2)).toString()
        }
    }

    fun getStopDetails(origin: String, destination: String, departureTime: String): List<com.example.whoossh.model.StopDetail> {
        val stations = if (origin.contains("Halim") || (origin.contains("Karawang") && destination.contains("Padalarang"))) 
            listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
        else 
            listOf("Tegalluar", "Padalarang", "Karawang", "Halim")
            
        val startIndex = stations.indexOf(origin)
        val endIndex = stations.indexOf(destination)
        if (startIndex == -1 || endIndex == -1) return emptyList()

        val times = generateScheduleTimes(stations[0], stations[3])
        val trainIndex = times.indexOf(departureTime) // Note: This assumes search from start station
        // In real case we'd need the train code or baseline departure time
        
        val details = mutableListOf<com.example.whoossh.model.StopDetail>()
        val range = if (startIndex < endIndex) startIndex..endIndex else startIndex downTo endIndex
        
        // Simplified but accurate stop details based on the pamphlet pattern
        for (i in range) {
            val stationName = stations[i]
            val timeAtStation = calculateTimeAtStation(departureTime, origin, stationName)
            details.add(com.example.whoossh.model.StopDetail(
                stationName, 
                timeAtStation, 
                timeAtStation, 
                if (stationName == origin || stationName == destination) "--" else "3 Menit"
            ))
        }
        return details
    }

    private fun calculateTimeAtStation(baselineTime: String, origin: String, targetStation: String): String {
        val duration = getDurationBetween(origin, targetStation)
        return calculateArrivalTimeWithDate(baselineTime, duration).first
    }

    private fun getDurationBetween(from: String, to: String): Int {
        if (from == to) return 0
        val stations = listOf("Halim", "Karawang", "Padalarang", "Tegalluar")
        val fromIdx = stations.indexOf(from)
        val toIdx = stations.indexOf(to)
        if (fromIdx == -1 || toIdx == -1) return 0

        val isEastbound = toIdx > fromIdx
        
        return if (isEastbound) {
            // Baseline Offsets from Halim
            val offsets = mapOf("Halim" to 0, "Karawang" to 17, "Padalarang" to 40, "Tegalluar" to 54)
            (offsets[to] ?: 0) - (offsets[from] ?: 0)
        } else {
            // Baseline Offsets from Tegalluar
            val offsets = mapOf("Tegalluar" to 0, "Padalarang" to 18, "Karawang" to 39, "Halim" to 54)
            (offsets[to] ?: 0) - (offsets[from] ?: 0)
        }
    }
}
