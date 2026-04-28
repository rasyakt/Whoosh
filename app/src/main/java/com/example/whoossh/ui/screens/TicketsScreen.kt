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
    val tabs = listOf("Aktif", "Riwayat")
    val pagerState = rememberPagerState(pageCount = { tabs.size })
    val scope = rememberCoroutineScope()

    // Local state for tickets loaded directly from API
    var localActiveTickets by remember { mutableStateOf<List<BookingData>>(emptyList()) }
    var localHistoryTickets by remember { mutableStateOf<List<BookingData>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch tickets directly from API (bypassing ViewModel to guarantee it works)
    LaunchedEffect(viewModel.userId) {
        if (viewModel.userId <= 0) {
            Log.w("TicketsScreen", "userId=${viewModel.userId}, skipping fetch")
            isLoading = false
            return@LaunchedEffect
        }

        Log.d("TicketsScreen", "Fetching tickets for userId=${viewModel.userId}")
        isLoading = true
        errorMessage = null

        try {
            // Direct API call with non-generic response
            val response = withContext(Dispatchers.IO) {
                ApiClient.apiService.getTicketsList(viewModel.userId)
            }

            Log.d("TicketsScreen", "API response: HTTP ${response.code()}")

            if (response.isSuccessful && response.body()?.status == "success") {
                val tickets = response.body()!!.data ?: emptyList()
                Log.i("TicketsScreen", "Received ${tickets.size} tickets from API")

                val bookings = tickets.mapNotNull { ticket ->
                    try {
                        ticket.toBookingData(viewModel.userName)
                    } catch (e: Exception) {
                        Log.e("TicketsScreen", "Parse error for ${ticket.bookingCode}: ${e.message}")
                        null
                    }
                }

                localActiveTickets = bookings.filter { !it.isUsed }
                localHistoryTickets = bookings.filter { it.isUsed }
                Log.i("TicketsScreen", "Active: ${localActiveTickets.size}, History: ${localHistoryTickets.size}")

                // Also update ViewModel state
                viewModel.refreshTickets()
            } else {
                val errorBody = response.errorBody()?.string()
                errorMessage = "Server error: ${response.code()}"
                Log.e("TicketsScreen", "API failed: HTTP ${response.code()} - $errorBody")

                // Fallback to raw parsing
                tryRawParsing(viewModel) { active, history ->
                    localActiveTickets = active
                    localHistoryTickets = history
                    errorMessage = null
                }
            }
        } catch (e: Exception) {
            Log.e("TicketsScreen", "Fetch error: ${e.javaClass.simpleName} - ${e.message}", e)
            errorMessage = "Gagal memuat tiket: ${e.localizedMessage}"

            // Fallback: show locally-added tickets from ViewModel
            localActiveTickets = viewModel.activeTickets
            localHistoryTickets = viewModel.historyTickets
            if (localActiveTickets.isNotEmpty() || localHistoryTickets.isNotEmpty()) {
                errorMessage = null // We have local data, no need to show error
            }
        } finally {
            isLoading = false
        }
    }

    // Combine: API tickets + locally added tickets (from confirmBooking)
    val combinedActiveTickets = (localActiveTickets + viewModel.activeTickets)
        .distinctBy { it.bookingCode }
    val combinedHistoryTickets = (localHistoryTickets + viewModel.historyTickets)
        .distinctBy { it.bookingCode }

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
            val tickets = if (page == 0) combinedActiveTickets else combinedHistoryTickets

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = WhooshRed)
                    }
                }
                tickets.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Filled.ConfirmationNumber,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp),
                                tint = Color.LightGray
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (page == 0) "Belum ada tiket aktif" else "Belum ada riwayat tiket",
                                fontSize = 16.sp,
                                color = WhooshTextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = if (page == 0) "Pesan tiket sekarang untuk\nmemulai perjalanan Anda"
                                else "Riwayat tiket yang sudah\ndigunakan akan muncul di sini",
                                fontSize = 13.sp,
                                color = Color.LightGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            if (errorMessage != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = errorMessage!!,
                                    fontSize = 12.sp,
                                    color = WhooshRed,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
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
    onSuccess: (List<BookingData>, List<BookingData>) -> Unit
) {
    try {
        val rawResponse = withContext(Dispatchers.IO) {
            ApiClient.apiService.getTicketsRaw(viewModel.userId)
        }
        if (rawResponse.isSuccessful) {
            val jsonString = rawResponse.body()?.string() ?: ""
            Log.d("TicketsScreen", "Raw response: ${jsonString.take(300)}")

            val gson = com.google.gson.Gson()
            val type = object : com.google.gson.reflect.TypeToken<TicketsListResponse>() {}.type
            val parsed = gson.fromJson<TicketsListResponse>(jsonString, type)

            if (parsed.status == "success" && parsed.data != null) {
                val bookings = parsed.data.mapNotNull { ticket ->
                    try { ticket.toBookingData(viewModel.userName) } catch (_: Exception) { null }
                }
                onSuccess(
                    bookings.filter { !it.isUsed },
                    bookings.filter { it.isUsed }
                )
                Log.i("TicketsScreen", "Raw parsing success: ${bookings.size} tickets")
            }
        }
    } catch (e: Exception) {
        Log.e("TicketsScreen", "Raw parsing also failed: ${e.message}")
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
                Box(
                    modifier = Modifier
                        .background(
                            if (ticket.isUsed) WhooshGreenLight else WhooshOrangeLight,
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (ticket.isUsed) "Selesai" else "Aktif",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ticket.isUsed) WhooshGreen else WhooshOrange
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
