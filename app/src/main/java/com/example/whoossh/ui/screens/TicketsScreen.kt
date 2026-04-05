package com.example.whoossh.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.BookingData
import com.example.whoossh.model.CoachClass
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshGreen
import com.example.whoossh.ui.theme.WhooshGreenLight
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.delay

@Composable
fun TicketsScreen(viewModel: BookingViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Dummy active & used tickets
    val activeTickets = remember {
        listOf(
            BookingData(
                userName = viewModel.userName,
                originStation = "Halim",
                destinationStation = "Tegalluar",
                ticketCount = 2,
                departureDate = "05/04/2024",
                departureTime = "08:00",
                arrivalTime = "08:52",
                duration = 52,
                coachClass = CoachClass.BISNIS,
                pricePerTicket = 450000,
                totalPrice = 900000,
                bookingCode = "WSH-A1B2C3D4"
            ),
            BookingData(
                userName = viewModel.userName,
                originStation = "Karawang",
                destinationStation = "Padalarang",
                ticketCount = 1,
                departureDate = "10/04/2024",
                departureTime = "14:30",
                arrivalTime = "14:48",
                duration = 18,
                coachClass = CoachClass.EKONOMI,
                pricePerTicket = 300000,
                totalPrice = 300000,
                bookingCode = "WSH-E5F6G7H8"
            )
        )
    }

    val usedTickets = remember {
        listOf(
            BookingData(
                userName = viewModel.userName,
                originStation = "Tegalluar",
                destinationStation = "Halim",
                ticketCount = 3,
                departureDate = "01/04/2024",
                departureTime = "07:00",
                arrivalTime = "07:52",
                duration = 52,
                coachClass = CoachClass.VIP,
                pricePerTicket = 575000,
                totalPrice = 1725000,
                bookingCode = "WSH-X1Y2Z3W4"
            )
        )
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
                    brush = Brush.verticalGradient(
                        listOf(WhooshGradientStart, WhooshGradientEnd)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Tiket Saya",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhooshWhite
                )
                Text(
                    text = "Kelola semua tiket perjalanan Anda",
                    fontSize = 13.sp,
                    color = WhooshWhite.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = WhooshWhite,
            contentColor = WhooshRed,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = WhooshRed,
                    height = 3.dp
                )
            }
        ) {
            listOf("Aktif (${activeTickets.size})", "Selesai (${usedTickets.size})").forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    },
                    selectedContentColor = WhooshRed,
                    unselectedContentColor = WhooshTextSecondary
                )
            }
        }

        val displayTickets = if (selectedTab == 0) activeTickets else usedTickets
        val isActive = selectedTab == 0

        if (displayTickets.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Filled.ConfirmationNumber,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tidak ada tiket", color = WhooshTextSecondary, fontSize = 16.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
            ) {
                itemsIndexed(displayTickets) { _, ticket ->
                    TicketItemCard(ticket = ticket, isActive = isActive)
                }
            }
        }
    }
}

@Composable
private fun TicketItemCard(ticket: BookingData, isActive: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WhooshWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Top colored strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(
                        if (isActive) Brush.horizontalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                        else Brush.horizontalGradient(listOf(Color.Gray, Color.LightGray))
                    )
            )

            Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {
                // Status badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isActive) WhooshGreenLight else Color(0xFFF5F5F5),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                if (isActive) Icons.Filled.CheckCircle else Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                tint = if (isActive) WhooshGreen else Color.Gray,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (isActive) "Aktif" else "Selesai",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isActive) WhooshGreen else Color.Gray
                            )
                        }
                    }
                    Text(
                        text = ticket.bookingCode,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshRed,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Route
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = ticket.departureTime, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = ticket.originStation, fontSize = 12.sp, color = WhooshTextSecondary)
                    }
                    Column(
                        modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "${ticket.duration} min", fontSize = 11.sp, color = WhooshRed)
                        HorizontalDivider(color = WhooshRed.copy(0.3f), thickness = 1.5.dp)
                        Icon(Icons.Filled.Train, contentDescription = null, tint = WhooshRed, modifier = Modifier.size(16.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = ticket.arrivalTime, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text(text = ticket.destinationStation, fontSize = 12.sp, color = WhooshTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    InfoChip(label = "Tanggal", value = ticket.departureDate)
                    InfoChip(label = "Gerbong", value = ticket.coachClass.displayName)
                    InfoChip(label = "Tiket", value = "${ticket.ticketCount}x")
                }

                if (isActive) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.QrCode2, contentDescription = null, tint = WhooshRed, modifier = Modifier.size(28.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Tampilkan QR", fontSize = 13.sp, color = WhooshRed, fontWeight = FontWeight.SemiBold)
                        }
                        Text(
                            text = TicketUtils.formatRupiah(ticket.totalPrice),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhooshRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 10.sp, color = WhooshTextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1A1A1A))
    }
}
