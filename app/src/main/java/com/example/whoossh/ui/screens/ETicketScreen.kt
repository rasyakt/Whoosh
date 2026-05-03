package com.example.whoossh.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.R
import com.example.whoossh.model.Schedule
import com.example.whoossh.ui.components.InfoRow
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshOutlinedButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.QrCodeUtils
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.whoossh.utils.tr
import com.example.whoossh.utils.trStr

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ETicketScreen(
    viewModel: BookingViewModel,
    onBackToDashboard: () -> Unit,
    onReschedule: () -> Unit = {},
    onRefund: () -> Unit = {},
    onAddInfant: () -> Unit = {},
    showQrOnEntry: Boolean = false
) {
    val booking = viewModel.bookingData
    val context = LocalContext.current
    var showQrDialog by remember { mutableStateOf(false) }
    var showRescheduleDialog by remember { mutableStateOf(false) }
    var showRefundDialog by remember { mutableStateOf(false) }
    var showAddInfantDialog by remember { mutableStateOf(false) }
    
    // Auto-show QR dialog when entering from deep link
    LaunchedEffect(showQrOnEntry, booking) {
        if (showQrOnEntry && booking != null) {
            android.util.Log.d("ETicketScreen", "Auto-showing QR dialog from deep link")
            kotlinx.coroutines.delay(300) // Small delay to ensure UI is ready
            showQrDialog = true
        }
    }
    
    // Reschedule state
    var rescheduleDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var availableSchedules by remember { mutableStateOf<List<Schedule>>(emptyList()) }
    var selectedNewSchedule by remember { mutableStateOf<Schedule?>(null) }
    var rescheduleFee by remember { mutableIntStateOf(0) }
    
    // Refund state
    var refundAmount by remember { mutableIntStateOf(0) }
    
    // Infant state
    var infantName by remember { mutableStateOf("") }
    var infantIdentityNo by remember { mutableStateOf("") }
    var infantDateOfBirth by remember { mutableStateOf("") }

    if (booking == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.CircularProgressIndicator(color = WhooshRed)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Memuat tiket Anda...".tr(),
                    fontSize = 14.sp,
                    color = WhooshTextSecondary
                )
            }
        }
        return
    }

    val qrBitmap = remember(booking.bookingCode) {
        QrCodeUtils.generateQRCode(booking.bookingCode, 600)
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhooshRed)
                    .padding(bottom = 20.dp)
            ) {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "Payment Succeeded".tr(), 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackToDashboard) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    },
                    actions = {
                        Text(
                            "Rules".tr(), 
                            color = Color.White, 
                            fontSize = 14.sp, 
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = WhooshRed
                    )
                )
                
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        "Order Number: ${booking.bookingCode}".tr(), 
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Order Time: ${booking.departureTime} ${booking.departureDate}".tr(), 
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF7F7F7))
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Trip Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(booking.departureDate, fontSize = 14.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Train, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${booking.duration} m".tr(), fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(booking.departureTime, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(booking.originStation, fontSize = 14.sp, color = Color.Black)
                        }
                        
                        Column(
                            modifier = Modifier.weight(0.5f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "G${1100 + (booking.bookingCode.hashCode() % 100).let { if (it < 0) -it else it }}".tr(), 
                                fontSize = 13.sp, 
                                color = Color.Gray
                            )
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
                        }
                        
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(booking.arrivalTime, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Text(booking.destinationStation, fontSize = 14.sp, color = Color.Black, textAlign = TextAlign.End)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    if (!booking.isCancelled && !booking.isUsed) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(
                                onClick = { showRescheduleDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Text("Reschedule".tr(), color = Color.Black)
                            }
                            OutlinedButton(
                                onClick = { showRefundDialog = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                            ) {
                                Text("Refund".tr(), color = Color.Black)
                            }
                        }
                    } else {
                        // Tampilkan status jika dibatalkan atau selesai
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (booking.isCancelled) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (booking.isCancelled) "TICKET CANCELLED" else "TICKET USED / COMPLETED",
                                color = if (booking.isCancelled) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Passenger Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(booking.userName, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Text("Adult ticket".tr(), fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Identity No. 3206****03".tr(), fontSize = 13.sp, color = Color.Gray)
                            Text(
                                "Coach ${booking.selectedCarriage} | ${booking.coachClass.displayName}".tr(), 
                                fontSize = 13.sp, 
                                color = Color.Gray
                            )
                             Text(
                                "Seat ".tr() + booking.selectedSeats.joinToString(", ").ifEmpty { "-" }, 
                                fontSize = 13.sp, 
                                color = Color.Gray
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(TicketUtils.formatRupiah(booking.pricePerTicket), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFFFF3E0), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                                    .clickable { showQrDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFE65100))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("QR Code >".tr(), fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    
                    if (!booking.isCancelled && !booking.isUsed) {
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        OutlinedButton(
                            onClick = { showAddInfantDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("Add Infant".tr(), color = Color.Black)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total Amount Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total payment amount:".tr(), fontSize = 14.sp, color = Color.Gray)
                    Text(TicketUtils.formatRupiah(booking.totalPrice), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Reminder
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Reminder".tr(), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "1. The ticket you purchased this time has been issued, You can enter the station with the QR code of the ticket, or enter the station after exchanging the paper ticket at the station window.".tr(),
                        fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "2. After exchanging for a paper ticket, the ticket cannot be refunded or Rescheduled on the APP.".tr(),
                        fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "3. You can save a screenshot of the current order details interface so that you can view the seat position when taking the bus.".tr(),
                        fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // QR CODE DIALOG
    if (showQrDialog && booking != null) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showQrDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dialog Header with Logo & Close
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_whoosh),
                            contentDescription = null,
                            modifier = Modifier.height(20.dp).align(Alignment.CenterStart)
                        )
                        Text(
                            "QR Code".tr(), 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        IconButton(
                            onClick = { showQrDialog = false }, 
                            modifier = Modifier.size(24.dp).align(Alignment.CenterEnd)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // QR Code Card
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE)),
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // QR Bitmap
                            if (qrBitmap != null) {
                                Image(
                                    bitmap = qrBitmap,
                                    contentDescription = "QR Code",
                                    modifier = Modifier.size(200.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Dynamic Booking Code / Hash
                            Text(
                                "${booking.bookingCode}".tr(), 
                                fontSize = 12.sp, 
                                color = Color.Gray,
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    
                    // Refresh Status
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .clickable { /* Refresh logic */ }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Click to Refresh Status".tr(), color = Color(0xFF0288D1), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF0288D1))
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Route Visual Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Origin Station
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = booking.originStation, 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                        
                        // Middle Section (Train Code & Arrow)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 12.dp) // Auto-width based on content
                        ) {
                            Text(
                                text = "G${1100 + (booking.bookingCode.hashCode() % 100).let { if (it < 0) -it else it }}".tr(), 
                                fontSize = 10.sp, 
                                color = WhooshRed, 
                                fontWeight = FontWeight.ExtraBold,
                                softWrap = false
                            )
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward, 
                                contentDescription = null, 
                                modifier = Modifier.size(14.dp), 
                                tint = Color.LightGray
                            )
                        }
                        
                        // Destination Station
                        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                            Text(
                                text = booking.destinationStation, 
                                fontSize = 14.sp, 
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                maxLines = 1
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    // Detailed Information Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Time Info
                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailColumn(label = "Departure Time".tr(), value = "${booking.departureTime}, ${booking.departureDate}", valueColor = WhooshRed, modifier = Modifier.weight(1f))
                        }
                        
                        // Seat & Class Info
                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailColumn(label = "Seat Info".tr(), value = "Coach ".tr() + "${booking.selectedCarriage} | ${booking.selectedSeats.joinToString(", ").ifEmpty { "-" }}", modifier = Modifier.weight(1f))
                            DetailColumn(label = "Class".tr(), value = booking.coachClass.displayName.tr(), modifier = Modifier.weight(1f))
                        }
                        
                        // Passenger Info
                        Row(modifier = Modifier.fillMaxWidth()) {
                            DetailColumn(label = "Name".tr(), value = booking.userName, modifier = Modifier.weight(1f))
                            val identityMasked = "3206****" + (booking.bookingCode.hashCode().let { if (it < 0) -it else it } % 100).toString().padStart(2, '0')
                            DetailColumn(label = "Identity No.".tr(), value = identityMasked, modifier = Modifier.weight(1f))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    // RESCHEDULE DIALOG
    if (showRescheduleDialog && booking != null) {
        val (canReschedule, errorMsg) = viewModel.canReschedule(booking)
        
        if (!canReschedule) {
            AlertDialog(
                onDismissRequest = { showRescheduleDialog = false },
                title = { Text("Cannot Reschedule".tr(), fontWeight = FontWeight.Bold) },
                text = { Text(errorMsg.tr(), fontSize = 14.sp) },
                confirmButton = {
                    Button(
                        onClick = { showRescheduleDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                    ) {
                        Text("OK".tr())
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showRescheduleDialog = false },
                title = { Text("Reschedule Ticket".tr(), fontWeight = FontWeight.Bold) },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Text("Current Schedule:".tr(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text("${booking.departureDate} at ${booking.departureTime}".tr(), fontSize = 13.sp, color = Color.Gray)
                        Text("${booking.originStation} → ${booking.destinationStation}".tr(), fontSize = 13.sp, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text("Select New Date:".tr(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(if (rescheduleDate.isBlank()) "Choose Date" else rescheduleDate)
                        }
                        
                        if (rescheduleDate.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Calculate fee
                            val fee = viewModel.calculateRescheduleFee(booking.departureDate, rescheduleDate, booking.totalPrice)
                            rescheduleFee = fee
                            
                            if (fee > 0) {
                                Text(
                                    "Reschedule Fee (25%): ${TicketUtils.formatRupiah(fee)}".tr(),
                                    fontSize = 13.sp,
                                    color = WhooshRed,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text(
                                    "Same day reschedule: No fee".tr(),
                                    fontSize = 13.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            // Load available schedules
                            LaunchedEffect(rescheduleDate) {
                                viewModel.setDate(rescheduleDate)
                                if (viewModel.searchSchedules()) {
                                    // Wait for schedules to load
                                    kotlinx.coroutines.delay(500)
                                    availableSchedules = viewModel.schedules
                                }
                            }
                            
                            if (availableSchedules.isNotEmpty()) {
                                Text("Available Schedules:".tr(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                availableSchedules.take(3).forEach { schedule ->
                                    val isSelected = selectedNewSchedule == schedule
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedNewSchedule = schedule },
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) WhooshRed else Color(0xFFE0E0E0)
                                        ),
                                        color = if (isSelected) Color(0xFFFFF0F0) else Color.White
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    "${schedule.departureTime} - ${schedule.arrivalTime}".tr(),
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    schedule.trainCode,
                                                    fontSize = 12.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                            Text(
                                                "${schedule.duration} m".tr(),
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            } else if (viewModel.isLoadingSchedules) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = WhooshRed
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            "Note: Reschedule is subject to seat availability.".tr(),
                            fontSize = 11.sp,
                            color = WhooshTextSecondary,
                            lineHeight = 14.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (selectedNewSchedule != null) {
                                viewModel.rescheduleTicket(
                                    booking = booking,
                                    newDate = rescheduleDate,
                                    newSchedule = selectedNewSchedule!!
                                ) { success, message ->
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                    if (success) {
                                        showRescheduleDialog = false
                                        rescheduleDate = ""
                                        selectedNewSchedule = null
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Please select a new schedule".trStr(viewModel.currentLanguage.value), Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = selectedNewSchedule != null && !viewModel.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Confirm Reschedule".tr())
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { 
                        showRescheduleDialog = false
                        rescheduleDate = ""
                        selectedNewSchedule = null
                    }) {
                        Text("Cancel".tr(), color = Color.Gray)
                    }
                }
            )
        }
        
        // Date Picker Dialog
        if (showDatePicker) {
            val calendar = Calendar.getInstance()
            val datePickerDialog = android.app.DatePickerDialog(
                context,
                { _, year, month, dayOfMonth ->
                    val selectedCal = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }
                    val sdf = SimpleDateFormat("EEEE, dd MMM yyyy", Locale("id", "ID"))
                    rescheduleDate = sdf.format(selectedCal.time)
                    showDatePicker = false
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.setOnDismissListener { showDatePicker = false }
            datePickerDialog.show()
        }
    }

    // REFUND DIALOG
    if (showRefundDialog && booking != null) {
        val (canRefund, errorMsg) = viewModel.canRefund(booking)
        
        LaunchedEffect(Unit) {
            if (canRefund) {
                refundAmount = viewModel.calculateRefundAmount(booking.totalPrice)
            }
        }
        
        if (!canRefund) {
            AlertDialog(
                onDismissRequest = { showRefundDialog = false },
                title = { Text("Cannot Refund".tr(), fontWeight = FontWeight.Bold) },
                text = { Text(errorMsg.tr(), fontSize = 14.sp) },
                confirmButton = {
                    Button(
                        onClick = { showRefundDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                    ) {
                        Text("OK".tr())
                    }
                }
            )
        } else {
            AlertDialog(
                onDismissRequest = { showRefundDialog = false },
                title = { Text("Refund Ticket".tr(), fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Are you sure you want to request a refund for this ticket?".tr(), fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFF5F5F5)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Original Amount:".tr(), fontSize = 13.sp, color = Color.Gray)
                                    Text(
                                        TicketUtils.formatRupiah(booking.totalPrice),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Cancellation Fee (10%):".tr(), fontSize = 13.sp, color = Color.Gray)
                                    Text(
                                        "- ${TicketUtils.formatRupiah(booking.totalPrice - refundAmount)}".tr(),
                                        fontSize = 13.sp,
                                        color = WhooshRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE0E0E0))
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Refund Amount:".tr(), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(
                                        TicketUtils.formatRupiah(refundAmount),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            "• Refund will be processed within 3-7 business days\n".tr() +
                            "• Amount will be returned to your original payment method\n".tr() +
                            "• This action cannot be undone".tr(),
                            fontSize = 12.sp,
                            color = WhooshTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.refundTicket(booking) { success, message, amount ->
                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Refund request submitted. You will receive ".trStr(viewModel.currentLanguage.value) + TicketUtils.formatRupiah(amount),
                                        Toast.LENGTH_LONG
                                    ).show()
                                    showRefundDialog = false
                                    // Navigate back to dashboard after refund
                                    onBackToDashboard()
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = !viewModel.isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Confirm Refund".tr())
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showRefundDialog = false }) {
                        Text("Cancel".tr(), color = Color.Gray)
                    }
                }
            )
        }
    }

    // ADD INFANT DIALOG
    if (showAddInfantDialog && booking != null) {
        AlertDialog(
            onDismissRequest = { showAddInfantDialog = false },
            title = { Text("Add Infant".tr(), fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        "Add an infant passenger (under 3 years old) to this booking.".tr(),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Infant Name
                    Text("Infant Name *".tr(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = infantName,
                        onValueChange = { infantName = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter infant's full name".tr(), fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Identity Number (Birth Certificate)
                    Text("Birth Certificate No. *".tr(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = infantIdentityNo,
                        onValueChange = { infantIdentityNo = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Enter birth certificate number".tr(), fontSize = 13.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Date of Birth
                    Text("Date of Birth *".tr(), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = infantDateOfBirth,
                        onValueChange = { infantDateOfBirth = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("DD/MM/YYYY".tr(), fontSize = 13.sp) },
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = {
                                val calendar = Calendar.getInstance()
                                val datePickerDialog = android.app.DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        infantDateOfBirth = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                    },
                                    calendar.get(Calendar.YEAR),
                                    calendar.get(Calendar.MONTH),
                                    calendar.get(Calendar.DAY_OF_MONTH)
                                )
                                // Set max date to 3 years ago
                                val threeYearsAgo = Calendar.getInstance().apply {
                                    add(Calendar.YEAR, -3)
                                }
                                datePickerDialog.datePicker.maxDate = System.currentTimeMillis()
                                datePickerDialog.datePicker.minDate = threeYearsAgo.timeInMillis
                                datePickerDialog.show()
                            }) {
                                Icon(
                                    painter = painterResource(id = android.R.drawable.ic_menu_my_calendar),
                                    contentDescription = "Select date",
                                    tint = WhooshRed
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = WhooshRed,
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF0F4F8),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD0DCE8))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Important Notes:".tr(), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "• Infants travel free of charge\n".tr() +
                                "• No separate seat required\n".tr() +
                                "• Must be accompanied by an adult\n".tr() +
                                "• Age must be under 3 years old".tr(),
                                fontSize = 11.sp,
                                color = WhooshTextSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (infantName.isBlank() || infantIdentityNo.isBlank() || infantDateOfBirth.isBlank()) {
                            Toast.makeText(context, "Please fill all required fields".trStr(viewModel.currentLanguage.value), Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addInfantToBooking(
                                booking = booking,
                                infantName = infantName,
                                infantIdentityNo = infantIdentityNo,
                                infantDateOfBirth = infantDateOfBirth
                            ) { success, message ->
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                if (success) {
                                    showAddInfantDialog = false
                                    infantName = ""
                                    infantIdentityNo = ""
                                    infantDateOfBirth = ""
                                }
                            }
                        }
                    },
                    enabled = !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Add Infant".tr())
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAddInfantDialog = false
                    infantName = ""
                    infantIdentityNo = ""
                    infantDateOfBirth = ""
                }) {
                    Text("Cancel".tr(), color = Color.Gray)
                }
            }
        )
    }
}

@Composable
private fun DetailItemRow(label: String?, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Text("$label: ".tr(), fontSize = 14.sp, color = Color.Gray)
        } else {
            // Optional: indentation for items without labels
            Spacer(modifier = Modifier.width(0.dp)) 
        }
        content()
    }
}
@Composable
private fun DetailColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Black
) {
    Column(modifier = modifier) {
        Text(text = label.tr(),
            fontSize = 11.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}
