package com.example.whoossh.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.api.ApiClient
import com.example.whoossh.api.BookingResponse
import com.example.whoossh.api.TicketsListResponse
import com.example.whoossh.model.BookingData
import com.example.whoossh.model.CoachClass
import com.example.whoossh.ui.theme.WhooshGreen
import com.example.whoossh.ui.theme.WhooshGreenLight
import com.example.whoossh.ui.theme.WhooshOrange
import com.example.whoossh.ui.theme.WhooshOrangeLight
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel
import com.example.whoossh.viewmodel.toBookingData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketsScreen(
    viewModel: BookingViewModel,
    onTicketClick: (BookingData) -> Unit
) {
    val tabs = listOf("Belum Bayar", "Sudah Bayar", "Riwayat")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    // Fetch tickets via ViewModel
    LaunchedEffect(viewModel.userId) {
        if (viewModel.userId > 0) {
            viewModel.refreshTickets()
        }
    }

    val isLoading = viewModel.isLoadingTickets
    val errorMessage = null

    // Filter tickets from ViewModel's state directly
    val combinedUnpaidTickets = viewModel.activeTickets
        .filter { !it.isPaid && !it.isCancelled }
        .sortedByDescending { it.bookingTimestamp }  // ✅ Urutkan dari terbaru ke terlama
    val combinedPaidTickets = viewModel.activeTickets
        .filter { it.isPaid && !it.isUsed }
        .sortedByDescending { it.bookingTimestamp }  // ✅ Urutkan dari terbaru ke terlama
    val combinedHistoryTickets = viewModel.historyTickets
        .distinctBy { it.bookingCode }
        .sortedByDescending { it.bookingTimestamp }  // ✅ Urutkan dari terbaru ke terlama

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(WhooshRed)
                .padding(top = 50.dp, bottom = 16.dp, start = 20.dp, end = 20.dp)
        ) {
            Text(
                text = "Tiket Saya",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = WhooshWhite
            )
        }

        // Tabs
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = Color.White,
            contentColor = WhooshRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    color = WhooshRed,
                    height = 3.dp
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (pagerState.currentPage == index) WhooshRed else Color.Gray
                        )
                    }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val tickets = when(page) {
                0 -> combinedUnpaidTickets
                1 -> combinedPaidTickets
                else -> combinedHistoryTickets
            }

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = WhooshRed)
                    }
                }
                tickets.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = when(page) {
                                    0 -> "Belum ada tiket yang belum dibayar"
                                    1 -> "Belum ada tiket aktif"
                                    else -> "Belum ada riwayat tiket"
                                },
                                fontSize = 16.sp,
                                color = WhooshTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when(page) {
                                    0 -> "Selesaikan pembayaran untuk\nmendapatkan tiket Anda"
                                    1 -> "Tiket yang sudah dibayar\nakan muncul di sini"
                                    else -> "Riwayat tiket yang sudah\ndigunakan akan muncul di sini"
                                },
                                fontSize = 13.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
                    ) {
                        itemsIndexed(tickets) { index, ticket ->
                            AnimatedVisibility(
                                visible = true,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
                            ) {
                                TicketCard(
                                    ticket = ticket,
                                    onClick = { onTicketClick(ticket) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private suspend fun tryRawParsing(
    viewModel: BookingViewModel,
    onSuccess: (List<BookingData>, List<BookingData>, List<BookingData>) -> Unit
) {
    try {
        val rawResponse = withContext(Dispatchers.IO) {
            ApiClient.apiService.getTicketsRaw(viewModel.userId)
        }
        if (rawResponse.isSuccessful) {
            val jsonString = rawResponse.body()?.string() ?: ""
            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<TicketsListResponse>() {}.type
            val parsed = gson.fromJson<TicketsListResponse>(jsonString, type)

            if (parsed.status == "success" && parsed.data != null) {
                val bookings = parsed.data.mapNotNull { ticket ->
                    try { ticket.toBookingData(viewModel.userName) } catch (_: Exception) { null }
                }
                onSuccess(
                    bookings.filter { !it.isPaid && !it.isCancelled },
                    bookings.filter { it.isPaid && !it.isUsed },
                    bookings.filter { it.isUsed || it.isCancelled }
                )
            }
        }
    } catch (e: Exception) {
        Log.e("TicketsScreen", "Raw parsing failed: ${e.message}")
    }
}

@Composable
private fun TicketCard(
    ticket: BookingData,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("id", "ID"))
    val bookingDate = dateFormat.format(Date(ticket.bookingTimestamp))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(WhooshRed.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Train, null, tint = WhooshRed, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ticket.bookingCode,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshRed
                    )
                }
                val (statusText, statusColor, statusBg) = when {
                    ticket.isCancelled -> Triple("Dibatalkan", Color.Red, Color(0xFFFFF0F0))
                    ticket.isUsed -> Triple("Selesai", Color.Gray, Color(0xFFF5F5F5))
                    !ticket.isPaid -> Triple("Belum Bayar", WhooshOrange, WhooshOrangeLight)
                    else -> Triple("Sudah Bayar", WhooshGreen, WhooshGreenLight)
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Route
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = ticket.departureTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = ticket.originStation,
                        fontSize = 12.sp,
                        color = WhooshTextSecondary
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("${ticket.duration} mnt", fontSize = 10.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                        HorizontalDivider(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 2.dp),
                            color = Color(0xFFE0E0E0)
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(WhooshRed)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = ticket.arrivalTime,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = ticket.destinationStation,
                        fontSize = 12.sp,
                        color = WhooshTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = ticket.departureDate,
                        fontSize = 12.sp,
                        color = WhooshTextSecondary
                    )
                    Text(
                        text = "${ticket.ticketCount} tiket • ${ticket.coachClass.displayName} • Grb ${ticket.selectedCarriage}",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
                Text(
                    text = TicketUtils.formatRupiah(ticket.totalPrice),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhooshRed
                )
            }
        }
    }
}
