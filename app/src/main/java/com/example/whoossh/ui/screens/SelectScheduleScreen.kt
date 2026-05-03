package com.example.whoossh.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.border
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.Schedule
import com.example.whoossh.ui.components.WhooshCard
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshGrayLight
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshRedLight
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.delay

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ArrowCircleLeft
import androidx.compose.material.icons.outlined.ArrowCircleRight
import androidx.compose.material.icons.filled.Train

import androidx.compose.foundation.layout.statusBarsPadding
import com.example.whoossh.utils.tr

@Composable
fun SelectScheduleScreen(
    viewModel: BookingViewModel,
    onScheduleSelected: () -> Unit,
    onLoginRequired: () -> Unit,
    onBack: () -> Unit
) {
    val visibleItems = remember { mutableStateListOf<Boolean>() }
    var showCustomDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel.schedules) {
        visibleItems.clear()
        viewModel.schedules.forEach { _ -> visibleItems.add(false) }
        viewModel.schedules.forEachIndexed { index, _ ->
            delay(50L * index)
            if (index < visibleItems.size) {
                visibleItems[index] = true
            }
        }
    }

    if (showCustomDatePicker) {
        com.example.whoossh.ui.components.CustomDatePickerOverlay(
            onDismiss = { showCustomDatePicker = false },
            onDateSelected = { date ->
                viewModel.setDate(date)
                viewModel.searchSchedules()
                showCustomDatePicker = false
            }
        )
    }

    Scaffold(
        topBar = {
            Column {
                // ── RED HEADER (Origin -> Swap -> Destination) ─────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WhooshRed)
                        .statusBarsPadding()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            "Back",
                            tint = WhooshWhite
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = viewModel.originStation,
                        color = WhooshWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                        HorizontalDivider(color = WhooshWhite, modifier = Modifier.width(16.dp), thickness = 1.dp)
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, WhooshWhite, CircleShape)
                                .clickable { 
                                    viewModel.swapStations() 
                                    viewModel.searchSchedules()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Train, "Train", tint = WhooshWhite, modifier = Modifier.size(16.dp))
                        }
                        HorizontalDivider(color = WhooshWhite, modifier = Modifier.width(16.dp), thickness = 1.dp)
                    }

                    Text(
                        text = viewModel.destinationStation,
                        color = WhooshWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Start,
                        maxLines = 2,
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── DATE NAVIGATOR BAR ─────────────────────────────────────────
                val sdf = remember { java.text.SimpleDateFormat("EEEE, dd MMM yyyy", java.util.Locale("id", "ID")) }
                val currentDateCal = remember(viewModel.departureDate) {
                    val cal = java.util.Calendar.getInstance()
                    try { cal.time = sdf.parse(viewModel.departureDate)!! } catch (e: Exception) {}
                    cal
                }
                
                val currentCompareCal = java.util.Calendar.getInstance().apply {
                    time = currentDateCal.time
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                
                val todayCal = java.util.Calendar.getInstance().apply {
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                    set(java.util.Calendar.MINUTE, 0)
                    set(java.util.Calendar.SECOND, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                
                val maxCal = java.util.Calendar.getInstance().apply {
                    add(java.util.Calendar.DAY_OF_YEAR, 25)
                    set(java.util.Calendar.HOUR_OF_DAY, 23)
                    set(java.util.Calendar.MINUTE, 59)
                    set(java.util.Calendar.SECOND, 59)
                }
                
                val canGoPrev = currentCompareCal.after(todayCal)
                val testNextCal = java.util.Calendar.getInstance().apply {
                    time = currentDateCal.time
                    add(java.util.Calendar.DAY_OF_YEAR, 1)
                    set(java.util.Calendar.HOUR_OF_DAY, 0)
                }
                val canGoNext = testNextCal.before(maxCal)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WhooshWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sebelumnya
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.clickable(enabled = canGoPrev) { 
                            val newCal = java.util.Calendar.getInstance().apply { time = currentDateCal.time; add(java.util.Calendar.DAY_OF_YEAR, -1) }
                            viewModel.setDate(sdf.format(newCal.time))
                            viewModel.searchSchedules()
                        }
                    ) {
                        val color = if (canGoPrev) Color(0xFF1A1A1A) else Color(0xFFCCCCCC)
                        Icon(Icons.Outlined.ArrowCircleLeft, null, tint = color, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sebelumnya".tr(), fontSize = 12.sp, color = color)
                    }

                    // Selector
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF8F8F8))
                            .clickable { showCustomDatePicker = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if(viewModel.departureDate.length > 14) viewModel.departureDate.substring(0, 14) else viewModel.departureDate, 
                            fontSize = 13.sp, 
                            color = Color(0xFF1A1A1A), 
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color(0xFF444444), modifier = Modifier.size(16.dp))
                    }

                    // Selanjutnya
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        modifier = Modifier.clickable(enabled = canGoNext) { 
                            val newCal = java.util.Calendar.getInstance().apply { time = currentDateCal.time; add(java.util.Calendar.DAY_OF_YEAR, 1) }
                            viewModel.setDate(sdf.format(newCal.time))
                            viewModel.searchSchedules()
                        }
                    ) {
                        val color = if (canGoNext) Color(0xFF1A1A1A) else Color(0xFFCCCCCC)
                        Text("Selanjutnya".tr(), fontSize = 12.sp, color = color)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Outlined.ArrowCircleRight, null, tint = color, modifier = Modifier.size(22.dp))
                    }
                }
                HorizontalDivider(color = Color(0xFFF0F0F0))
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F5F5))
        ) {
            if (viewModel.isLoadingSchedules) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.CircularProgressIndicator(color = WhooshRed)
                }
            } else if (viewModel.schedules.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.Schedule,
                            null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Tidak ada jadwal tersedia".tr(),
                            color = WhooshTextSecondary,
                            fontSize = 16.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "${viewModel.schedules.size} jadwal tersedia".tr(),
                    style = MaterialTheme.typography.bodySmall,
                    color = WhooshTextSecondary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        top = 4.dp,
                        bottom = 20.dp
                    )
                ) {
                    itemsIndexed(viewModel.schedules) { index, schedule ->
                        val isVisible = index < visibleItems.size && visibleItems[index]
                        AnimatedVisibility(
                            visible = isVisible,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
                        ) {
                            ScheduleCard(
                                schedule = schedule,
                                onSelect = {
                                    if (viewModel.isLoggedIn) {
                                        viewModel.selectSchedule(schedule)
                                        onScheduleSelected()
                                    } else {
                                        onLoginRequired()
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(
    schedule: Schedule,
    onSelect: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WhooshWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Top Header: Train Code
            Box(
                modifier = Modifier
                    .background(WhooshRed.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(text = schedule.trainCode, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = WhooshRed)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Departure time
                Column(horizontalAlignment = Alignment.Start) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = schedule.departureTime,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "WIB".tr(),
                            fontSize = 9.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = schedule.originStation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = WhooshTextSecondary
                    )
                }

                // Duration line
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${schedule.duration} mnt".tr(),
                        fontSize = 10.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).border(1.dp, Color.LightGray, CircleShape))
                        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0), thickness = 1.dp)
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                    }
                }

                // Arrival time
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            text = schedule.arrivalTime,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "WIB".tr(),
                            fontSize = 9.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    Text(
                        text = schedule.destinationStation,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = WhooshTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            
            // Stasiun Pemberhentian Toggle (Dikembalikan)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Stasiun Pemberhentian".tr(), 
                    fontSize = 10.sp, 
                    color = WhooshRed, 
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null,
                    tint = WhooshRed,
                    modifier = Modifier.size(14.dp)
                )
            }
            
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .background(WhooshRed.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                        Text("Stasiun".tr(), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.weight(2f))
                        Text("Tiba".tr(), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Berangkat".tr(), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("Berhenti".tr(), fontSize = 9.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }
                    HorizontalDivider(color = WhooshRed.copy(0.08f))
                    
                    schedule.stopDetails.forEachIndexed { index, detail ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), 
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(detail.stationName, fontSize = 10.sp, color = Color(0xFF444444), fontWeight = FontWeight.Medium, modifier = Modifier.weight(2f))
                            Text(detail.arrivalTime, fontSize = 10.sp, color = Color(0xFF444444), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(detail.departureTime, fontSize = 10.sp, color = Color(0xFF444444), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text(detail.stopDuration, fontSize = 9.sp, color = Color.Gray, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }
                    }
                }
            }

            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            // Footer: Price & Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Mulai dari".tr(), fontSize = 10.sp, color = WhooshTextSecondary)
                    Text(
                        text = com.example.whoossh.utils.TicketUtils.formatRupiah(schedule.price),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WhooshRed
                    )
                }
                
                Button(
                    onClick = onSelect,
                    modifier = Modifier.height(40.dp).width(110.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WhooshRed),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(text = "Pilih".tr(), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
