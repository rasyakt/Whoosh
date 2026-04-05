package com.example.whoossh.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.theme.WhooshRed
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerOverlay(
    onDismiss: () -> Unit,
    onDateSelected: (String) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .background(Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Pilih tanggal", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                val days = listOf("Minggu", "Senin", "Selasa", "Rabu", "Kamis", "Jumat", "Sabtu")
                days.forEachIndexed { index, day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        color = if (index == 0 || index == 6) WhooshRed else Color(0xFF444444)
                    )
                }
            }
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(2) { monthOffset ->
                    val cal = Calendar.getInstance()
                    cal.add(Calendar.MONTH, monthOffset)
                    MonthView(cal, onDateSelected)
                }
            }
        }
    }
}

@Composable
private fun MonthView(cal: Calendar, onDateSelected: (String) -> Unit) {
    val monthSdf = SimpleDateFormat("MMM yyyy", Locale("id", "ID"))
    val currentDay = Calendar.getInstance()
    
    // PT KCIC rule: tickets can be booked up to H-25 days in advance
    val maxDay = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 25)
    }
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8F8FA))
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Text(
            text = monthSdf.format(cal.time),
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        )
    }
    
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    cal.set(Calendar.DAY_OF_MONTH, 1)
    val startDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
    val totalCells = startDayOfWeek + daysInMonth
    val rows = ceil(totalCells / 7.0).toInt()
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        var dayCounter = 1
        for (i in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                for (j in 0..6) {
                    val isWithinDays = (i == 0 && j >= startDayOfWeek) || (i > 0 && dayCounter <= daysInMonth)
                    if (isWithinDays) {
                        val clickDay = dayCounter
                        val month = cal.get(Calendar.MONTH)
                        val year = cal.get(Calendar.YEAR)
                        
                        val iteratorCal = Calendar.getInstance().apply {
                            set(year, month, clickDay, 23, 59, 59)
                        }
                        
                        // Disable if strictly before today, OR if strictly after maxDay (H+25)
                        val isPastDay = iteratorCal.before(currentDay) && (iteratorCal.get(Calendar.DAY_OF_YEAR) != currentDay.get(Calendar.DAY_OF_YEAR))
                        val isTooFar = iteratorCal.after(maxDay)
                        val isEnabled = !isPastDay && !isTooFar

                        val isSundayOrSaturday = j == 0 || j == 6
                        val textColor = if (!isEnabled) Color.LightGray else if (isSundayOrSaturday) WhooshRed else Color(0xFF1A1A1A)
                        
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clickable(enabled = isEnabled) {
                                    val reqCal = Calendar.getInstance().apply {
                                        set(year, month, clickDay)
                                    }
                                    val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
                                    onDateSelected(sdf.format(reqCal.time))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            val isToday = !isPastDay && year == currentDay.get(Calendar.YEAR) &&
                                          month == currentDay.get(Calendar.MONTH) &&
                                          clickDay == currentDay.get(Calendar.DAY_OF_MONTH)
                            
                            if (isToday) {
                                Box(modifier = Modifier.size(32.dp).background(WhooshRed), contentAlignment = Alignment.Center) {
                                    Text("$dayCounter", fontSize = 13.sp, color = Color.White)
                                }
                            } else {
                                Text("$dayCounter", fontSize = 13.sp, color = textColor)
                            }
                        }
                        dayCounter++
                    } else {
                        Spacer(modifier = Modifier.weight(1f).height(40.dp))
                    }
                }
            }
        }
    }
}
