package com.example.whoossh.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.components.DashedDivider
import com.example.whoossh.ui.components.InfoRow
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshCard
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshGreen
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun SummaryScreen(
    viewModel: BookingViewModel,
    onConfirm: () -> Unit,
    onBack: () -> Unit
) {
    val schedule = viewModel.selectedSchedule!!
    val coach = viewModel.selectedCoachClass!!
    val pricePerTicket = TicketUtils.getPricePerTicket(schedule.originStation, schedule.destinationStation, coach, schedule.departureTime)
    val totalPrice = pricePerTicket * viewModel.ticketCount

    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = WhooshGreen,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Pembelian Berhasil!",
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Tiket Anda telah berhasil dipesan.",
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "📧 E-ticket dikirim ke:",
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        color = WhooshTextSecondary
                    )
                    Text(
                        text = viewModel.userEmail,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        color = WhooshRed
                    )
                }
            },
            confirmButton = {
                val scope = rememberCoroutineScope()
                val context = LocalContext.current
                var isProcessing by remember { mutableStateOf(false) }

                TextButton(
                    onClick = {
                        if (isProcessing) return@TextButton
                        isProcessing = true
                        
                        scope.launch {
                            val (success, message) = viewModel.confirmBooking()
                            isProcessing = false
                            
                            if (success) {
                                showDialog = false
                                onConfirm()
                            } else {
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = WhooshRed
                        )
                    } else {
                        Text("Lihat E-Ticket", color = WhooshRed, fontWeight = FontWeight.Bold)
                    }
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Ringkasan Pemesanan", onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Route Card
            WhooshCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Detail Perjalanan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Route visual
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = schedule.departureTime,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhooshRed
                            )
                            Text(
                                text = schedule.originStation,
                                fontSize = 13.sp,
                                color = WhooshTextSecondary
                            )
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "${schedule.duration} min",
                                fontSize = 11.sp,
                                color = WhooshRed,
                                fontWeight = FontWeight.Medium
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = WhooshRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = schedule.arrivalTime,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhooshRed
                            )
                            Text(
                                text = schedule.destinationStation,
                                fontSize = 13.sp,
                                color = WhooshTextSecondary
                            )
                        }
                    }

                    DashedDivider(modifier = Modifier.padding(vertical = 8.dp))

                    InfoRow(label = "Penumpang", value = viewModel.userName)
                    InfoRow(label = "Tanggal", value = viewModel.departureDate)
                    InfoRow(label = "Durasi", value = "${schedule.duration} menit")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Card
            WhooshCard {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Rincian Pembayaran",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    InfoRow(label = "Jenis Gerbong", value = coach.displayName)
                    InfoRow(label = "Nomor Gerbong", value = "Gerbong ${viewModel.selectedCarriage ?: 1}")
                    InfoRow(label = "Nomor Kursi", value = viewModel.selectedSeats.sorted().joinToString(", ").ifEmpty { "-" })
                    InfoRow(label = "Jumlah Tiket", value = "${viewModel.ticketCount} tiket")
                    InfoRow(
                        label = "Harga per tiket",
                        value = TicketUtils.formatRupiah(pricePerTicket)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Total Pembayaran",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = TicketUtils.formatRupiah(totalPrice),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhooshRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            WhooshButton(
                text = "Konfirmasi Pembelian",
                onClick = { showDialog = true },
                icon = Icons.Filled.ShoppingCart
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
