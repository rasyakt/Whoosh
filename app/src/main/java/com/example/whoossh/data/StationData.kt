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

    fun updateStations(newStations: List<Station>) {
        stations = newStations
    }

    fun getDuration(from: String, to: String): Int {
        // Now delegating to TicketUtils for smart, asymmetric durations
        return com.example.whoossh.utils.TicketUtils.getActualDuration("06:25", from, to)
    }

    fun getStationNames(): List<String> = stations.map { it.name }
}
