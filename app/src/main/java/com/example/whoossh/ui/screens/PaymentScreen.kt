package com.example.whoossh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.whoossh.R
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: BookingViewModel,
    onPaymentSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val bookingData = viewModel.bookingData ?: return
    val coach = bookingData.coachClass
    val pricePerTicket = bookingData.pricePerTicket
    val totalFare = bookingData.totalPrice
    val bookingCode = bookingData.bookingCode
    val primaryPassenger = bookingData.userName.ifBlank { "User" }

    var selectedBankName by remember { mutableStateOf<String?>(null) }
    var selectedBankId by remember { mutableStateOf<String?>(null) }
    var showVADetails by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Shared Timer from ViewModel
    val timeLeft = viewModel.paymentTimeLeft
    val minutes = timeLeft / 60
    val seconds = timeLeft % 60
    val timerText = String.format("%02d:%02d", minutes, seconds)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "Pay", 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.SemiBold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // Red Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhooshRed)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.ShoppingCart, 
                    contentDescription = null, 
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Complete Payment", 
                    color = Color.White, 
                    fontSize = 16.sp, 
                    fontWeight = FontWeight.Medium
                )
            }

            // Summary Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row {
                    Text("Passenger : ", color = WhooshTextSecondary, fontSize = 14.sp)
                    Text(primaryPassenger, color = Color.DarkGray, fontSize = 14.sp)
                }
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Text("Booking No : ", color = WhooshTextSecondary, fontSize = 14.sp)
                    Text(bookingCode, color = Color.DarkGray, fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Amount
            Text(
                text = TicketUtils.formatRupiah(totalFare),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = WhooshRed
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer Section
            val payTime = viewModel.paymentTimeLeft

            Text(
                text = "Payment Countdown : ${payTime / 60}:${String.format("%02d", payTime % 60)}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Warning
            Text(
                text = "In accordance to KCIC policy, ticket booking payment cannot start if remaining booking time is less than 5 minutes",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                color = WhooshRed,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Virtual Account Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F9F9))
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    "Virtual Account", 
                    modifier = Modifier.padding(start = 24.dp, bottom = 12.dp),
                    color = Color(0xFF1A73E8),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                
                HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFE0E0E0))
                
                BankItem("Bank Mandiri", "mandiri") {
                    selectedBankName = "Bank Mandiri"
                    selectedBankId = "mandiri"
                    showVADetails = true
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                BankItem("Bank BNI", "bni") {
                    selectedBankName = "Bank BNI"
                    selectedBankId = "bni"
                    showVADetails = true
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                BankItem("Bank BRI", "bri") {
                    selectedBankName = "Bank BRI"
                    selectedBankId = "bri"
                    showVADetails = true
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFEEEEEE))
                BankItem("Bank BTN", "btn") {
                    selectedBankName = "Bank BTN"
                    selectedBankId = "btn"
                    showVADetails = true
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }

        // VA Details Bottom Sheet
        if (showVADetails) {
            val vaNumber = remember(selectedBankId) { 
                "8877" + (10000000..99999999).random().toString() 
            }
            
            ModalBottomSheet(
                onDismissRequest = { showVADetails = false },
                sheetState = sheetState,
                containerColor = Color.White,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                            .padding(bottom = 32.dp)
                    ) {
                        Text(
                            text = "Detail Pembayaran",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF5F5F5))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = selectedBankName ?: "",
                                    fontSize = 13.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = vaNumber,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            IconButton(onClick = { 
                                clipboardManager.setText(AnnotatedString(vaNumber))
                                scope.launch {
                                    snackbarHostState.showSnackbar("Nomor VA berhasil disalin")
                                }
                            }) {
                                Icon(
                                    Icons.Default.ContentCopy, 
                                    contentDescription = "Copy",
                                    tint = Color(0xFF1A73E8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Text(
                            text = "Mohon selesaikan pembayaran sebelum waktu habis.",
                            fontSize = 13.sp,
                            color = WhooshTextSecondary,
                            lineHeight = 18.sp
                        )
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = {
                                showVADetails = false
                                onPaymentSuccess()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = WhooshRed)
                        ) {
                            Text(
                                "Konfirmasi Pembayaran", 
                                fontSize = 16.sp, 
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // SnackbarHost diletakkan di dalam Box agar muncul di atas konten BottomSheet
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 100.dp) // Mengatur posisi agar melayang di atas tombol
                    )
                }
            }
        }
    }
}

@Composable
private fun BankItem(name: String, id: String, onClick: () -> Unit) {
    val logoRes = when(id) {
        "mandiri" -> R.drawable.logo_mandiri
        "bni" -> R.drawable.logo_bni
        "bri" -> R.drawable.logo_bri
        "btn" -> R.drawable.logo_btn
        else -> 0
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Bank Logo
        if (logoRes != 0) {
            Image(
                painter = painterResource(id = logoRes),
                contentDescription = name,
                modifier = Modifier.size(width = 50.dp, height = 30.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 50.dp, height = 30.dp)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Text(id.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = name,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}
