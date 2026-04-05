package com.example.whoossh.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.components.InfoRow
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshOutlinedButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun ETicketScreen(
    viewModel: BookingViewModel,
    onBackToDashboard: () -> Unit
) {
    val booking = viewModel.bookingData ?: return
    val context = LocalContext.current

    Scaffold(
        topBar = {
            WhooshTopBar(title = "E-Ticket")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ticket Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = WhooshWhite,
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column {
                    // Ticket Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                    colors = listOf(WhooshGradientStart, WhooshGradientEnd)
                                ),
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Train,
                                    contentDescription = null,
                                    tint = WhooshWhite,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Whoosh Ticket",
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhooshWhite
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Boarding Pass",
                                fontSize = 13.sp,
                                color = WhooshWhite.copy(alpha = 0.8f),
                                letterSpacing = 3.sp
                            )
                        }
                    }

                    // Booking Code
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(WhooshRed.copy(alpha = 0.06f))
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "KODE BOOKING",
                                fontSize = 10.sp,
                                color = WhooshTextSecondary,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = booking.bookingCode,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhooshRed,
                                letterSpacing = 3.sp
                            )
                        }
                    }

                    // Route
                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = booking.departureTime,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = booking.originStation,
                                    fontSize = 14.sp,
                                    color = WhooshTextSecondary
                                )
                            }
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${booking.duration} min",
                                    fontSize = 11.sp,
                                    color = WhooshRed
                                )
                                Icon(
                                    Icons.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = WhooshRed,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = booking.arrivalTime,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = booking.destinationStation,
                                    fontSize = 14.sp,
                                    color = WhooshTextSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Dashed line divider
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                        ) {
                            drawLine(
                                color = Color.LightGray,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(10f, 10f),
                                    0f
                                ),
                                strokeWidth = 2f
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Details
                        InfoRow(label = "Nama Penumpang", value = booking.userName)
                        InfoRow(label = "Tanggal", value = booking.departureDate)
                        InfoRow(label = "Gerbong", value = booking.coachClass.displayName)
                        InfoRow(label = "Jumlah Tiket", value = "${booking.ticketCount} tiket")
                        InfoRow(
                            label = "Harga per Tiket",
                            value = TicketUtils.formatRupiah(booking.pricePerTicket)
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Pembayaran",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = TicketUtils.formatRupiah(booking.totalPrice),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhooshRed
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // QR Code placeholder
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(130.dp)
                                        .background(
                                            color = WhooshRed.copy(alpha = 0.06f),
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.QrCode2,
                                        contentDescription = "QR Code",
                                        tint = WhooshRed,
                                        modifier = Modifier.size(100.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Scan untuk validasi tiket",
                                    fontSize = 11.sp,
                                    color = WhooshTextSecondary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                WhooshOutlinedButton(
                    text = "Download",
                    icon = Icons.Filled.Download,
                    onClick = {
                        Toast.makeText(context, "Tiket berhasil didownload", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
                WhooshOutlinedButton(
                    text = "Share",
                    icon = Icons.Filled.Share,
                    onClick = {
                        Toast.makeText(context, "Fitur share akan segera tersedia", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            WhooshButton(
                text = "Kembali ke Dashboard",
                onClick = onBackToDashboard,
                icon = Icons.Filled.Home
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
