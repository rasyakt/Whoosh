package com.example.whoossh.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.CoachClass
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun SelectCoachScreen(
    viewModel: BookingViewModel,
    onCoachSelected: () -> Unit,
    onManagePassengers: () -> Unit,
    onBack: () -> Unit
) {
    val selectedPassengers by viewModel.selectedPassengers.collectAsState()
    
    Scaffold(
        topBar = {
            WhooshTopBar(title = "Book", onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFFAFAFA))
                .verticalScroll(rememberScrollState())
        ) {
            // Schedule Info Card
            viewModel.selectedSchedule?.let { schedule ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = viewModel.departureDate,
                            fontSize = 12.sp,
                            color = WhooshTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = schedule.departureTime,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = schedule.originStation,
                                    fontSize = 13.sp,
                                    color = WhooshTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = schedule.trainCode,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = WhooshRed
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(1.5.dp)
                                        .background(Color(0xFFE0E0E0))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        modifier = Modifier.size(13.dp),
                                        tint = WhooshTextSecondary
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "${schedule.duration}m",
                                        fontSize = 11.sp,
                                        color = WhooshTextSecondary,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Column(
                                horizontalAlignment = Alignment.End,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = schedule.arrivalTime,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = schedule.destinationStation,
                                    fontSize = 13.sp,
                                    color = WhooshTextSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Coach Class Selection
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Pilih Jenis Gerbong",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CoachClassOption(
                        title = "First",
                        price = viewModel.getPriceForClass(CoachClass.VIP),
                        isSelected = viewModel.selectedCoachClass == CoachClass.VIP,
                        onClick = { viewModel.selectCoachClass(CoachClass.VIP) },
                        modifier = Modifier.weight(1f)
                    )
                    CoachClassOption(
                        title = "Business",
                        price = viewModel.getPriceForClass(CoachClass.BISNIS),
                        isSelected = viewModel.selectedCoachClass == CoachClass.BISNIS,
                        onClick = { viewModel.selectCoachClass(CoachClass.BISNIS) },
                        modifier = Modifier.weight(1f)
                    )
                    CoachClassOption(
                        title = "Economy",
                        price = viewModel.getPriceForClass(CoachClass.EKONOMI),
                        isSelected = viewModel.selectedCoachClass == CoachClass.EKONOMI,
                        onClick = { viewModel.selectCoachClass(CoachClass.EKONOMI) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Passenger Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Penumpang",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                
                Card(
                    onClick = onManagePassengers,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (selectedPassengers.isEmpty()) Color.White else Color(0xFFFFF5F5)
                    ),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = if (selectedPassengers.isEmpty()) Color(0xFFE8E8E8) else WhooshRed.copy(alpha = 0.3f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            if (selectedPassengers.isEmpty()) {
                                Text(
                                    text = "Pilih Penumpang",
                                    fontSize = 14.sp,
                                    color = WhooshRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Tambahkan penumpang (maks. 15)",
                                    fontSize = 12.sp,
                                    color = WhooshTextSecondary
                                )
                            } else {
                                Text(
                                    text = "${selectedPassengers.size}/15 Penumpang Terpilih",
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Column {
                                    selectedPassengers.take(3).forEachIndexed { index, passenger ->
                                        Text(
                                            text = "${index + 1}. ${passenger.name}",
                                            fontSize = 11.sp,
                                            color = WhooshTextSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    if (selectedPassengers.size > 3) {
                                        Text(
                                            text = "+${selectedPassengers.size - 3} lainnya",
                                            fontSize = 11.sp,
                                            color = WhooshRed,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowRight,
                            contentDescription = null,
                            tint = WhooshRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Ticket Detection & Cancel Condition
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    drawRoundRect(
                        color = Color(0xFFE0E0E0),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 2f,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(10f, 10f), 0f
                            )
                        ),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(12.dp.toPx())
                    )
                }
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ticket Detection & Cancel Condition",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. BA baby under three\n" +
                              "2. Adults 17 years of age or older\n" +
                              "3. The name and identity number must be in accordance with that contained in the identity certificate (KTP/ Passport), when the passenger age below 17 years can be filled in with the date of birth of",
                        fontSize = 12.sp,
                        color = Color(0xFF666666),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                WhooshButton(
                    text = "Lanjut ke Kursi",
                    onClick = onCoachSelected,
                    enabled = viewModel.selectedCoachClass != null && selectedPassengers.isNotEmpty()
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFE8E8E8))
                ) {
                    Text(
                        text = "Lanjutkan",
                        color = Color(0xFFAAAAAA),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CoachClassOption(
    title: String,
    price: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isRecommended: Boolean = false
) {
    Card(
        onClick = onClick,
        modifier = modifier.height(115.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF5F5) else Color.White
        ),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) WhooshRed else Color(0xFFE0E0E0)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 2.dp else 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Price and Title
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = TicketUtils.formatRupiah(price),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) WhooshRed else Color.Black,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) WhooshRed else Color(0xFF666666),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
            
            // Status
            Text(
                text = "Tersedia",
                fontSize = 10.sp,
                color = Color(0xFF999999),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
