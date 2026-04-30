package com.example.whoossh.data

import com.example.whoossh.model.Station

object StationData {
    var stations = listOf(
        Station(1, "Tegalluar"),
        Station(2, "Padalarang"),
        Station(3, "Karawang"),
        Station(4, "Halim")
    )
        private set

    // Duration matrix in minutes (SYMMETRIC - same duration both directions)
    // Index corresponds to station ID - 1
    // Order: Tegalluar(0), Padalarang(1), Karawang(2), Halim(3)
    private var durationMatrix = arrayOf(
        intArrayOf(0, 17, 35, 52),   // From Tegalluar
        intArrayOf(17, 0, 18, 35),   // From Padalarang (fixed: 35 instead of 30)
        intArrayOf(35, 18, 0, 11),   // From Karawang
        intArrayOf(52, 35, 11, 0)    // From Halim (fixed: 35 to match Padalarang)
    )

    fun updateStations(newStations: List<Station>) {
        stations = newStations
    }

    fun updateDurationMatrix(newMatrix: Array<IntArray>) {
        // Validate matrix is symmetric
        if (newMatrix.size != newMatrix[0].size) {
            throw IllegalArgumentException("Duration matrix must be square")
        }
        for (i in newMatrix.indices) {
            for (j in newMatrix[i].indices) {
                if (newMatrix[i][j] != newMatrix[j][i]) {
                    throw IllegalArgumentException("Duration matrix must be symmetric: [$i][$j]=${newMatrix[i][j]} != [$j][$i]=${newMatrix[j][i]}")
                }
            }
        }
        durationMatrix = newMatrix
    }

    fun getDuration(from: String, to: String): Int {
        val fromIndex = stations.indexOfFirst { it.name == from }
        val toIndex = stations.indexOfFirst { it.name == to }
        
        // Improved bounds checking
        if (fromIndex == -1 || toIndex == -1) {
            android.util.Log.w("StationData", "Station not found: from=$from, to=$to")
            return 0
        }
        if (fromIndex >= durationMatrix.size || toIndex >= durationMatrix[fromIndex].size) {
            android.util.Log.e("StationData", "Index out of bounds: fromIndex=$fromIndex, toIndex=$toIndex, matrixSize=${durationMatrix.size}")
            return 0
        }
        
        return durationMatrix[fromIndex][toIndex]
    }

    fun getStationNames(): List<String> = stations.map { it.name }
}
