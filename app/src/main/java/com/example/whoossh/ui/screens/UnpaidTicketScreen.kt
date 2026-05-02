package com.example.whoossh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.OrderStatus
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnpaidTicketScreen(
    viewModel: BookingViewModel,
    onPay: () -> Unit,
    onCancel: () -> Unit,
    onBack: () -> Unit,
    onReturnTrip: () -> Unit = {}
) {
    val schedule = viewModel.selectedSchedule ?: return
    val coach = viewModel.selectedCoachClass ?: return
    val passengers = viewModel.selectedPassengers.collectAsState().value
    val selectedSeats = viewModel.selectedSeats
    
    val bookingData = viewModel.bookingData ?: return
    val pricePerTicket = bookingData.pricePerTicket
    val totalFare = bookingData.totalPrice
    
    // Shared Timer from ViewModel
    val timeLeft = viewModel.paymentTimeLeft
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerText = String.format("%02d m %02d s", minutes, seconds)
    
    // Dialog state
    var showCancelDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var cancelMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    val status = viewModel.bookingData?.status ?: OrderStatus.UNPAID
                    Text(
                        text = when (status) {
                            OrderStatus.REFUNDED -> "Refunded"
                            OrderStatus.CANCELLED -> "Cancelled"
                            OrderStatus.PAID -> "Paid"
                            OrderStatus.CHECKED -> "Used"
                            else -> "Unpaid"
                        }, 
                        color = Color.White, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.SemiBold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = Color.White 
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = WhooshRed
                )
            )
        },
        bottomBar = {
            // Sembunyikan tombol jika tiket sudah dibatalkan atau timer habis
            val isCancelled = viewModel.bookingData?.isCancelled == true
            val isTimerExpired = viewModel.paymentTimeLeft <= 0
            
            if (!isCancelled && !isTimerExpired) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Fare", color = Color.Gray, fontSize = 14.sp)
                        Text(
                            TicketUtils.formatRupiah(totalFare), 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total Price", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                        Text(
                            TicketUtils.formatRupiah(totalFare), 
                            fontSize = 20.sp, 
                            fontWeight = FontWeight.Bold,
                            color = WhooshRed
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showCancelDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(
                            onClick = {
                                // Return trip: swap origin & destination, navigate back to dashboard
                                viewModel.swapStations()
                                onReturnTrip()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Return Trip", 
                                fontSize = 13.sp, 
                                fontWeight = FontWeight.SemiBold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                        Button(
                            onClick = onPay,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhooshRed),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Pay", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
            }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F8F8))
                .verticalScroll(rememberScrollState())
        ) {
            // Timer Bar
            val isCancelled = viewModel.bookingData?.isCancelled == true
            val isTimerExpired = viewModel.paymentTimeLeft <= 0
            
            when {
                isCancelled -> {
                    val isRefund = viewModel.bookingData?.status == OrderStatus.REFUNDED
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isRefund) Color(0xFFE8F5E9) else Color(0xFFFFF0F0))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isRefund) Icons.Default.CheckCircle else Icons.Default.Close, 
                            contentDescription = null, 
                            tint = if (isRefund) Color(0xFF2E7D32) else WhooshRed, 
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isRefund) "Pengembalian dana (Refund) telah berhasil" else "Tiket ini telah dibatalkan", 
                            fontSize = 12.sp, 
                            color = if (isRefund) Color(0xFF2E7D32) else WhooshRed, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                isTimerExpired -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF0F0))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Close, null, tint = WhooshRed, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Waktu pembayaran telah habis", fontSize = 12.sp, color = WhooshRed, fontWeight = FontWeight.Bold)
                    }
                }
                else -> {
                    val payTime = viewModel.paymentTimeLeft
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF0F0))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.AccessTime, null, tint = WhooshTextSecondary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Remaining payment time: ", fontSize = 12.sp, color = WhooshTextSecondary)
                        Text(
                            text = "${payTime / 60}:${String.format("%02d", payTime % 60)}",
                            fontSize = 12.sp,
                            color = WhooshRed,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Ticket Info Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = viewModel.departureDate, 
                            fontSize = 13.sp, 
                            color = WhooshTextSecondary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime, 
                                contentDescription = null, 
                                tint = WhooshTextSecondary, 
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${schedule.duration} m", 
                                fontSize = 13.sp, 
                                color = WhooshTextSecondary
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = schedule.departureTime, 
                                fontSize = 22.sp, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = schedule.originStation, 
                                fontSize = 14.sp, 
                                color = WhooshTextSecondary
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = schedule.trainCode, 
                                fontSize = 12.sp, 
                                color = WhooshTextSecondary
                            )
                            Icon(
                                Icons.Default.KeyboardArrowRight, 
                                contentDescription = null, 
                                tint = Color.LightGray
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = schedule.arrivalTime, 
                                fontSize = 22.sp, 
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = schedule.destinationStation, 
                                fontSize = 14.sp, 
                                color = WhooshTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Passenger Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Passenger", 
                        fontSize = 16.sp, 
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    passengers.forEachIndexed { index, passenger ->
                        val seat = if (index < selectedSeats.size) selectedSeats[index] else "-"
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = passenger.name, 
                                        fontSize = 15.sp, 
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        color = Color(0xFFF5F5F5),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${passenger.passengerType} ticket",
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                            color = WhooshTextSecondary
                                        )
                                    }
                                }
                                Text(
                                    text = "Identity No. ${passenger.identityNo.take(4)}****${passenger.identityNo.takeLast(2)}",
                                    fontSize = 13.sp,
                                    color = WhooshTextSecondary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                                Text(
                                    text = "Coach ${viewModel.selectedCarriage ?: 1} | ${coach.displayName} $seat",
                                    fontSize = 13.sp,
                                    color = WhooshTextSecondary
                                )
                            }
                            Text(
                                text = TicketUtils.formatRupiah(pricePerTicket),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        
                        if (index < passengers.lastIndex) {
                            Spacer(modifier = Modifier.height(16.dp))
                            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFF5F5F5))
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reminder (Sembunyikan jika sudah refund/batal)
            if (!isCancelled && !isTimerExpired) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0DCE8))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Reminder", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "1. Please complete the online payment within the specified time.\n" +
                                   "2. In case of late payment, the system will cancel the transaction.\n" +
                                   "3. You will not be able to purchase additional tickets until you complete payment or cancel this order.",
                            fontSize = 12.sp,
                            color = WhooshTextSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else if (viewModel.bookingData?.status == OrderStatus.REFUNDED) {
                // Info Refund
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC8E6C9))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Informasi Refund", 
                            fontSize = 14.sp, 
                            fontWeight = FontWeight.SemiBold, 
                            color = Color(0xFF1B5E20)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tiket ini telah berhasil direfund. Dana sebesar 75% dari total bayar (setelah biaya administrasi 25%) telah dikirimkan ke rekening yang Anda daftarkan.",
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
    
    // Cancel Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    "Batalkan Tiket?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column {
                    Text(
                        "Apakah Anda yakin ingin membatalkan tiket ini?",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "• Timer pembayaran akan dihentikan\n• Kursi akan dilepas",
                        fontSize = 12.sp,
                        color = WhooshTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelDialog = false
                        viewModel.cancelUnpaidTicket { success, message ->
                            cancelMessage = message
                            if (success) {
                                showSuccessDialog = true
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                ) {
                    Text("Ya, Batalkan", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showCancelDialog = false },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0D0D0))
                ) {
                    Text("Tidak", color = Color.Black)
                }
            }
        )
    }
    
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onCancel()
            },
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Tiket Dibatalkan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    cancelMessage,
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onCancel()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhooshRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }
}
