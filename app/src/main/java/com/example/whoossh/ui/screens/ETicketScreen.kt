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
import androidx.compose.material.icons.filled.ArrowForward
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ETicketScreen(
    viewModel: BookingViewModel,
    onBackToDashboard: () -> Unit
) {
    val booking = viewModel.bookingData
    val context = LocalContext.current
    var showQrDialog by remember { mutableStateOf(false) }

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
                    text = "Memuat tiket Anda...",
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
                            "Payment Succeeded", 
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
                            "Rules", 
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
                        "Order Number: ${booking.bookingCode}", 
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Order Time: ${booking.departureTime} ${booking.departureDate}", 
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
                            Text("${booking.duration} m", fontSize = 14.sp, color = Color.Gray)
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
                            Text("G1063", fontSize = 13.sp, color = Color.Gray)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
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
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("Reschedule", color = Color.Black)
                        }
                        OutlinedButton(
                            onClick = { },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                        ) {
                            Text("Refund", color = Color.Black)
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
                                    Text("Adult ticket", fontSize = 10.sp, color = Color.Gray)
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Identity No. 3206****03", fontSize = 13.sp, color = Color.Gray)
                            Text(
                                "Coach ${booking.selectedCarriage} | ${booking.coachClass.displayName}", 
                                fontSize = 13.sp, 
                                color = Color.Gray
                            )
                            Text(
                                "Class ${booking.selectedSeats.firstOrNull() ?: "04A"}", 
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
                                    Text("QR Code >", fontSize = 12.sp, color = Color(0xFFE65100), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    OutlinedButton(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
                    ) {
                        Text("Add Infant", color = Color.Black)
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
                    Text("Total payment amount:", fontSize = 14.sp, color = Color.Gray)
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
                    Text("Reminder", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "1. The ticket you purchased this time has been issued, You can enter the station with the QR code of the ticket, or enter the station after exchanging the paper ticket at the station window.",
                        fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "2. After exchanging for a paper ticket, the ticket cannot be refunded or Rescheduled on the APP.",
                        fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "3. You can save a screenshot of the current order details interface so that you can view the seat position when taking the bus.",
                        fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // QR CODE DIALOG
    if (showQrDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showQrDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Dialog Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_whoosh),
                            contentDescription = null,
                            modifier = Modifier.height(24.dp)
                        )
                        Text(
                            "QR Code", 
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                        IconButton(onClick = { showQrDialog = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.LightGray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Code String
                    Text(
                        "62001Xz086202604199253096", 
                        fontSize = 14.sp, 
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // QR Code Image
                    if (qrBitmap != null) {
                        Box(
                            modifier = Modifier
                                .size(240.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                bitmap = qrBitmap,
                                contentDescription = "QR Code",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                    
                    // Refresh Link
                    Row(
                        modifier = Modifier
                            .clickable { /* Refresh logic */ }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Click to Refresh Status", color = Color(0xFF03A9F4), fontSize = 13.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFF03A9F4))
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Trip Summary (Route)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            booking.originStation, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("G1063", fontSize = 11.sp, color = WhooshRed, fontWeight = FontWeight.Bold)
                            Icon(
                                Icons.Default.ArrowForward, 
                                contentDescription = null, 
                                modifier = Modifier.size(16.dp), 
                                tint = Color.LightGray
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        Text(
                            booking.destinationStation, 
                            fontSize = 15.sp, 
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Details List
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Departure Time
                        DetailItemRow("Departure Time") {
                            Text(
                                text = "${booking.departureTime} ${booking.departureDate}",
                                fontSize = 14.sp,
                                color = WhooshRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        
                        // Seat Detail
                        DetailItemRow("Seat") {
                            Row {
                                Text("Coach ", fontSize = 14.sp, color = Color.Black)
                                Text(booking.selectedCarriage.toString(), fontSize = 14.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                Text(" | ${booking.coachClass.displayName}", fontSize = 14.sp, color = Color.Black)
                            }
                        }
                        
                        // Class/Seat Code
                        DetailItemRow(null) {
                            Text(
                                text = "Class ${booking.selectedSeats.firstOrNull() ?: "04A"}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        
                        // Name
                        DetailItemRow("Name") {
                            Text(booking.userName, fontSize = 14.sp, color = Color.Black)
                        }
                        
                        // Identity
                        DetailItemRow("Identity No.") {
                            Text("3206****03", fontSize = 14.sp, color = Color.Black)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailItemRow(label: String?, content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (label != null) {
            Text("$label: ", fontSize = 14.sp, color = Color.Gray)
        } else {
            // Optional: indentation for items without labels
            Spacer(modifier = Modifier.width(0.dp)) 
        }
        content()
    }
}
