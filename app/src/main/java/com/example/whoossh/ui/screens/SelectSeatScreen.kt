package com.example.whoossh.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.CoachClass
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshRedLight
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel
import com.example.whoossh.utils.tr

@Composable
fun SelectSeatScreen(
    viewModel: BookingViewModel,
    onSeatSelected: () -> Unit,
    onBack: () -> Unit
) {
    val coachClass = viewModel.selectedCoachClass ?: return
    val availableCarriages = viewModel.getAvailableCarriages(coachClass)
    val selectedCarriage = viewModel.selectedCarriage ?: availableCarriages.firstOrNull() ?: 1
    
    // Load real occupied seats from server
    LaunchedEffect(selectedCarriage) {
        viewModel.loadOccupiedSeats()
    }

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Pilih Gerbong & Kursi".tr(), onBack = onBack)
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(20.dp)
                ) {
                    val completed = viewModel.isSeatSelectionComplete()
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Kursi Terpilih:".tr(),
                            color = WhooshTextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${viewModel.selectedSeats.size} / ${viewModel.ticketCount} Tiket".tr(),
                            color = if (completed) WhooshRed else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (viewModel.selectedSeats.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "${"Gerbong".tr()} $selectedCarriage: ${viewModel.selectedSeats.sorted().joinToString(", ")}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = WhooshRed
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    WhooshButton(
                        text = "Lanjutkan".tr(),
                        onClick = onSeatSelected,
                        enabled = completed
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
        ) {
            // Passenger Selection (Top)
            val selectedPassengers = viewModel.selectedPassengers.collectAsState().value
            if (selectedPassengers.isNotEmpty()) {
                var isExpanded by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
                
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(12.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Penumpang Terpilih".tr(),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${selectedPassengers.size} Penumpang".tr(),
                                fontSize = 12.sp,
                                color = WhooshRed,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            androidx.compose.material3.Icon(
                                imageVector = if (isExpanded) androidx.compose.material.icons.Icons.Default.KeyboardArrowUp 
                                             else androidx.compose.material.icons.Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = WhooshRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (isExpanded) {
                        // Show all selected passengers
                        selectedPassengers.forEachIndexed { index, passenger ->
                            PassengerItem(index + 1, passenger)
                            if (index < selectedPassengers.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    thickness = 0.5.dp,
                                    color = Color(0xFFF5F5F5)
                                )
                            }
                        }
                    } else {
                        // Show only the first passenger
                        PassengerItem(1, selectedPassengers.first())
                    }
                }
            }

            // Carriage Selection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                availableCarriages.forEach { carriage ->
                    CarriageTab(
                        number = carriage,
                        isSelected = selectedCarriage == carriage,
                        onClick = { viewModel.selectCarriage(carriage) }
                    )
                }
            }

            // Legend
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                LegendItem("Tersedia".tr(), Color.White, Color.LightGray)
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem("Terpilih".tr(), WhooshRed, WhooshRed)
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem("Terisi".tr(), Color(0xFFEEEEEE), Color(0xFFDDDDDD))
            }

            // Seat Grid
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.isOccupiedSeatsLoading) {
                    CircularProgressIndicator(color = WhooshRed)
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 20.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val rows = 15 // Assuming 15 rows per carriage for demo
                        val layout = when (coachClass) {
                            CoachClass.EKONOMI -> listOf("A", "B", "C", "", "D", "F")
                            CoachClass.BISNIS -> listOf("A", "C", "", "D", "F")
                            CoachClass.VIP -> listOf("A", "", "C", "D")
                        }

                        // Row Headers
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            layout.forEach { letter ->
                                if (letter.isEmpty()) {
                                    Spacer(modifier = Modifier.width(36.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .width(46.dp)
                                            .padding(horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        for (row in 1..rows) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                layout.forEach { letter ->
                                    if (letter.isEmpty()) {
                                        // Aisle with Row Number
                                        Box(
                                            modifier = Modifier.width(36.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = row.toString(),
                                                color = Color.LightGray,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    } else {
                                        val seatId = "${row}${letter}"
                                        val isOccupied = viewModel.occupiedSeats.contains(seatId)
                                        
                                        val selectedSeats = viewModel.selectedSeats
                                        val isSelected = selectedSeats.contains(seatId)
                                        val seatIndex = if (isSelected) selectedSeats.indexOf(seatId) else -1
                                        val passengerLabel = if (seatIndex != -1 && seatIndex < selectedPassengers.size) {
                                            selectedPassengers[seatIndex].name.take(1).uppercase()
                                        } else null

                                        Box(
                                            modifier = Modifier
                                                .width(46.dp)
                                                .padding(horizontal = 4.dp)
                                        ) {
                                            SeatItem(
                                                seatId = seatId,
                                                isSelected = isSelected,
                                                isOccupied = isOccupied,
                                                passengerLabel = passengerLabel,
                                                onClick = {
                                                    if (!isOccupied) {
                                                        viewModel.toggleSeatSelection(seatId)
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun CarriageTab(
    number: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(if (isSelected) WhooshRed else Color.White)
    val textColor by animateColorAsState(if (isSelected) Color.White else Color.Black)
    val borderColor by animateColorAsState(if (isSelected) WhooshRed else Color(0xFFE0E0E0))

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Gerbong $number".tr(),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LegendItem(text: String, color: Color, borderColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, borderColor, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = text, fontSize = 12.sp, color = WhooshTextSecondary)
    }
}

@Composable
private fun SeatItem(
    seatId: String,
    isSelected: Boolean,
    isOccupied: Boolean,
    passengerLabel: String?,
    onClick: () -> Unit
) {
    val textColor = when {
        isOccupied -> Color.Transparent 
        isSelected -> Color.White
        else -> WhooshRed
    }

    val borderColor = when {
        isOccupied -> Color(0xFFDDDDDD)
        isSelected -> WhooshRed
        else -> WhooshRed.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .height(52.dp)
            .fillMaxWidth()
            .clickable(enabled = !isOccupied, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Seat Base Shape (Backrest + Seat)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Backrest
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                    .background(
                        if (isOccupied) Brush.verticalGradient(
                            colors = listOf(Color(0xFFF5F5F5), Color(0xFFE0E0E0))
                        ) else if (isSelected) Brush.verticalGradient(
                            colors = listOf(WhooshRedLight, WhooshRed)
                        ) else Brush.verticalGradient(
                            colors = listOf(Color.White, Color.White)
                        )
                    )
                    .border(
                        1.dp, 
                        borderColor, 
                        RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isOccupied) {
                    Icon(
                        imageVector = Icons.Default.Block,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.LightGray.copy(alpha = 0.6f)
                    )
                } else {
                    Text(
                        text = seatId,
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(2.dp))
            
            // Seat Cushion / Armrests Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                // Left Armrest
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) WhooshRed else if (isOccupied) Color(0xFFDDDDDD) else Color.White)
                        .border(1.dp, borderColor, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.width(26.dp))
                // Right Armrest
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isSelected) WhooshRed else if (isOccupied) Color(0xFFDDDDDD) else Color.White)
                        .border(1.dp, borderColor, RoundedCornerShape(2.dp))
                )
            }
        }

        // Passenger Badge
        if (passengerLabel != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 4.dp, y = 4.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.5.dp, WhooshRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = passengerLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhooshRed
                )
            }
        }
    }
}

@Composable
private fun PassengerItem(index: Int, passenger: com.example.whoossh.model.Passenger) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(WhooshRed.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index".tr(),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = WhooshRed
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = passenger.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}
