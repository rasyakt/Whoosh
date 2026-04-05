package com.example.whoossh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.data.StationData
import com.example.whoossh.model.Schedule
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.TicketUtils

@Composable
fun SchedulesScreen() {
    val stationNames = StationData.getStationNames()
    var selectedOrigin by remember { mutableStateOf(stationNames[0]) }
    var selectedDestination by remember { mutableStateOf(stationNames[3]) }

    // Generate schedules
    val schedules = remember(selectedOrigin, selectedDestination) {
        val duration = StationData.getDuration(selectedOrigin, selectedDestination)
        TicketUtils.generateScheduleTimes().mapIndexed { index, time ->
            Schedule(
                departureTime = time,
                arrivalTime = TicketUtils.calculateArrivalTime(time, duration),
                duration = duration,
                originStation = selectedOrigin,
                destinationStation = selectedDestination,
                price = TicketUtils.getTicketPrice(1, com.example.whoossh.model.CoachClass.EKONOMI),
                trainCode = TicketUtils.generateTrainCode(time, index),
                stops = TicketUtils.getStops(selectedOrigin, selectedDestination),
                stopDetails = TicketUtils.getStopDetails(selectedOrigin, selectedDestination, time)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text("Jadwal Kereta", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = WhooshWhite)
                Text("Lihat semua jadwal keberangkatan", fontSize = 13.sp, color = WhooshWhite.copy(0.8f))

                Spacer(modifier = Modifier.height(16.dp))

                // Route selector row
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WhooshWhite.copy(0.15f)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("DARI", fontSize = 9.sp, color = WhooshWhite.copy(0.7f), letterSpacing = 1.sp)
                            Text(selectedOrigin, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WhooshWhite)
                        }
                        Icon(Icons.Filled.ArrowForward, null, tint = WhooshWhite, modifier = Modifier.size(18.dp))
                        Column {
                            Text("KE", fontSize = 9.sp, color = WhooshWhite.copy(0.7f), letterSpacing = 1.sp)
                            Text(selectedDestination, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WhooshWhite)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(WhooshWhite.copy(0.2f))
                                .clickable {
                                    val tmp = selectedOrigin
                                    selectedOrigin = selectedDestination
                                    selectedDestination = tmp
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text("Tukar", fontSize = 12.sp, color = WhooshWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Station filter chips
        Column(modifier = Modifier.background(WhooshWhite)) {
            Text(
                "Pilih Rute:",
                fontSize = 12.sp,
                color = WhooshTextSecondary,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Origin chips
                items(stationNames) { station ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (selectedOrigin == station) WhooshRed else Color(0xFFF5F5F5)
                            )
                            .clickable { selectedOrigin = station }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Train,
                                null,
                                tint = if (selectedOrigin == station) WhooshWhite else WhooshRed,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                station,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedOrigin == station) WhooshWhite else Color(0xFF555555)
                            )
                        }
                    }
                }
            }
        }

        // Info bar
        HorizontalDivider(color = Color(0xFFF0F0F0))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhooshWhite)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Schedule, null, tint = WhooshRed, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "${schedules.size} jadwal  •  $selectedOrigin → $selectedDestination",
                    fontSize = 12.sp,
                    color = WhooshTextSecondary
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Timer, null, tint = WhooshRed, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${StationData.getDuration(selectedOrigin, selectedDestination)} menit",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = WhooshRed
                )
            }
        }

        // Schedule list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(schedules) { schedule ->
                ScheduleItemCard(schedule = schedule)
            }
        }
    }
}

@Composable
private fun ScheduleItemCard(schedule: Schedule) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhooshWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Departure time
            Text(
                text = schedule.departureTime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            // Duration line
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${schedule.duration} min",
                    fontSize = 9.sp,
                    color = WhooshRed
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(WhooshRed.copy(0.2f)))
                    Icon(
                        Icons.Filled.Train,
                        null,
                        tint = WhooshRed,
                        modifier = Modifier.size(14.dp).padding(horizontal = 2.dp)
                    )
                    Box(modifier = Modifier.weight(1f).height(1.dp).background(WhooshRed.copy(0.2f)))
                }
            }

            // Arrival time
            Text(
                text = schedule.arrivalTime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
        }
        
        HorizontalDivider(color = Color(0xFFF0F0F0))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = schedule.trainCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                Text(text = "WIB", fontSize = 9.sp, color = Color.Gray)
            }
            Text(
                text = TicketUtils.formatRupiah(schedule.price),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = WhooshRed
            )
        }
    }
}
