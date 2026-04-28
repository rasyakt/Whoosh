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

    // Duration matrix in minutes
    // Index corresponds to station ID - 1
    // Order: Tegalluar(0), Padalarang(1), Karawang(2), Halim(3)
    private var durationMatrix = arrayOf(
        intArrayOf(0, 17, 35, 52),   // From Tegalluar
        intArrayOf(17, 0, 18, 35),   // From Padalarang
        intArrayOf(35, 18, 0, 11),   // From Karawang
        intArrayOf(52, 30, 11, 0)    // From Halim
    )

    fun updateStations(newStations: List<Station>) {
        stations = newStations
    }

    fun updateDurationMatrix(newMatrix: Array<IntArray>) {
        durationMatrix = newMatrix
    }

    fun getDuration(from: String, to: String): Int {
        val fromIndex = stations.indexOfFirst { it.name == from }
        val toIndex = stations.indexOfFirst { it.name == to }
        if (fromIndex == -1 || toIndex == -1 || fromIndex >= durationMatrix.size) return 0
        val row = durationMatrix[fromIndex]
        if (toIndex >= row.size) return 0
        return row[toIndex]
    }

    fun getStationNames(): List<String> = stations.map { it.name }
}
