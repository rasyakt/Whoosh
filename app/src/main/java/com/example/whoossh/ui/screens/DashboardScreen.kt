package com.example.whoossh.ui.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.DirectionsTransit
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.whoossh.ui.components.CustomDatePickerOverlay
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.R
import com.example.whoossh.data.StationData
import com.example.whoossh.ui.theme.WhooshBackground
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale
import java.text.SimpleDateFormat
import com.example.whoossh.utils.tr
import com.example.whoossh.utils.trStr

private data class QuickAction(val icon: ImageVector, val label: String, val action: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BookingViewModel,
    onSearchSchedule: () -> Unit,
    onLoginRequired: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    onNavigateToPromo: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToPassengerList: () -> Unit = {},
    onNavigateToETicket: () -> Unit = {},
    onNavigateToUnpaidTicket: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var selectedBottomNav by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var lastBackPressTime by remember { mutableStateOf(0L) }

    BackHandler {
        if (selectedBottomNav != 0) {
            selectedBottomNav = 0
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < 2000) {
                (context as? ComponentActivity)?.finish()
            } else {
                lastBackPressTime = currentTime
                Toast.makeText(context, "Tekan sekali lagi untuk keluar".trStr(viewModel.currentLanguage.value), Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        containerColor = WhooshBackground,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = WhooshRed,
                    contentColor = WhooshWhite,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        bottomBar = {
            WhooshBottomBar(
                selectedIndex = selectedBottomNav,
                onItemSelected = { index ->
                    if ((index == 1 || index == 3) && !viewModel.isLoggedIn) {
                        onLoginRequired()
                    } else {
                        selectedBottomNav = index
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedBottomNav) {
                0 -> HomeContent(
                    viewModel = viewModel,
                    onSearchSchedule = onSearchSchedule,
                    onLoginRequired = onLoginRequired,
                    onLogout = onLogout,
                    snackbarHostState = snackbarHostState,
                    scope = scope,
                    onNavigateToTab = { index ->
                        if ((index == 1 || index == 3) && !viewModel.isLoggedIn) {
                            onLoginRequired()
                        } else {
                            selectedBottomNav = index
                        }
                    },
                    onNavigateToPromo = onNavigateToPromo,
                    onNavigateToHelpCenter = onNavigateToHelpCenter,
                    onNavigateToHistory = onNavigateToHistory
                )
                1 -> TicketsScreen(
                    viewModel = viewModel,
                    onTicketClick = { ticket ->
                        viewModel.viewTicket(ticket)
                        if (ticket.isPaid) {
                            onNavigateToETicket()
                        } else {
                            onNavigateToUnpaidTicket()
                        }
                    }
                )
                2 -> SchedulesScreen()
                3 -> AccountScreen(
                    viewModel = viewModel,
                    onNavigateToEditProfile = onNavigateToEditProfile,
                    onNavigateToHistory = onNavigateToHistory,
                    onNavigateToPromo = onNavigateToPromo,
                    onNavigateToNotifications = onNavigateToNotifications,
                    onNavigateToLanguage = onNavigateToLanguage,
                    onNavigateToPrivacy = onNavigateToPrivacy,
                    onNavigateToChangePassword = onNavigateToChangePassword,
                    onNavigateToHelpCenter = onNavigateToHelpCenter,
                    onNavigateToPassengerList = onNavigateToPassengerList,
                    onLogout = onLogout
                )
            }
        }
    }
}

// ── HOME CONTENT (Tab 0) ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeContent(
    viewModel: BookingViewModel,
    onSearchSchedule: () -> Unit,
    onLoginRequired: () -> Unit,
    onLogout: () -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    onNavigateToTab: (Int) -> Unit,
    onNavigateToPromo: () -> Unit = {},
    onNavigateToHelpCenter: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {}
) {
    val context = LocalContext.current
    var showMoreInfo by remember { mutableStateOf(false) }
    var showCustomDatePicker by remember { mutableStateOf(false) }

    if (showMoreInfo) {
        InformasiTambahanOverlay(onDismiss = { showMoreInfo = false })
    }
    
    if (showCustomDatePicker) {
        CustomDatePickerOverlay(
            onDismiss = { showCustomDatePicker = false },
            onDateSelected = { 
                viewModel.setDate(it)
                showCustomDatePicker = false
            }
        )
    }

    val stationNames = StationData.getStationNames()

    val quickActions = listOf(
        QuickAction(Icons.Outlined.ConfirmationNumber, "Tiket Saya") { onNavigateToTab(1) },
        QuickAction(Icons.Outlined.History, "Riwayat") { onNavigateToHistory() },
        QuickAction(Icons.Outlined.LocalOffer, "Promo") { onNavigateToPromo() },
        QuickAction(Icons.Outlined.HelpOutline, "Bantuan") { onNavigateToHelpCenter() }
    )

    LaunchedEffect(viewModel.formError) {
        viewModel.formError?.let {
            scope.launch {
                snackbarHostState.showSnackbar(it.trStr(viewModel.currentLanguage.value))
                viewModel.clearFormError()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {
        // ── HERO & BOOKING FORM SECTION ──────────────────────────────────────
        Box(modifier = Modifier.fillMaxWidth()) {
            // HERO BACKGROUND
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            ) {
                val carouselImages = listOf(
                    R.drawable.carousel_1,
                    R.drawable.carousel_2,
                    R.drawable.carousel_3,
                    R.drawable.carousel_4,
                    R.drawable.carousel_5
                )
                val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { carouselImages.size })

                LaunchedEffect(pagerState) {
                    while (true) {
                        kotlinx.coroutines.delay(3000)
                        if (carouselImages.isNotEmpty()) {
                            val nextPage = (pagerState.currentPage + 1) % carouselImages.size
                            pagerState.animateScrollToPage(nextPage)
                        }
                    }
                }

                androidx.compose.foundation.pager.HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                ) { page ->
                    Image(
                        painter = painterResource(id = carouselImages[page]),
                        contentDescription = "Carousel Image $page",
                        contentScale = androidx.compose.ui.layout.ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    )
                }
                
                // Foreground Content
                Box(
                    modifier = Modifier.matchParentSize()
                ) {

                    // Carousel Dots Indicator
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 135.dp), // Berada tepat di atas form modal booking
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(carouselImages.size) { iteration ->
                            val isSelected = pagerState.currentPage == iteration
                            val color = if (isSelected) WhooshWhite else WhooshWhite.copy(alpha = 0.5f)
                            val width = if (isSelected) 18.dp else 8.dp
                            val animatedWidth by androidx.compose.animation.core.animateDpAsState(
                                targetValue = width,
                                animationSpec = androidx.compose.animation.core.tween(300),
                                label = "width"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .height(8.dp)
                                    .width(animatedWidth)
                                    .clip(CircleShape)
                                    .background(color)
                            )
                        }
                    }
                }
            }

            // BOOKING FORM CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 152.dp) // Creates the overlap (210dp - 152dp = 58dp overlap)
                    .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {

                // ── STATION ROW ───────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Dari".tr(),
                            color = WhooshRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StationSelector(
                            label = viewModel.originStation.ifEmpty { "Pilih Stasiun" },
                            icon = Icons.Filled.LocationOn,
                            options = stationNames,
                            onSelect = { viewModel.setOrigin(it) }
                        )
                    }

                    // Swap button – centered dynamically with a structural placeholder
                    Column(
                        modifier = Modifier.padding(horizontal = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(" ".tr(), fontSize = 10.sp) // invisible placeholder for 'Dari'/'Ke' label height
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .shadow(2.dp, CircleShape)
                                .clip(CircleShape)
                                .background(WhooshWhite)
                                .border(1.dp, WhooshRed.copy(alpha = 0.25f), CircleShape)
                                .clickable { viewModel.swapStations() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SwapHoriz, "Swap", tint = WhooshRed, modifier = Modifier.size(18.dp))
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Ke".tr(),
                            color = WhooshRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        StationSelector(
                            label = viewModel.destinationStation.ifEmpty { "Pilih Stasiun" },
                            icon = Icons.Filled.Send,
                            options = stationNames,
                            onSelect = { viewModel.setDestination(it) }
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = Color(0xFFEEEEEE),
                    thickness = 1.dp
                )

                Text(
                    "Tanggal Berangkat".tr(),
                    color = WhooshTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8F8F8))
                        .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                        .clickable { showCustomDatePicker = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = viewModel.departureDate.ifEmpty { "Pilih tanggal perjalanan" },
                        fontSize = 13.sp,
                        color = if (viewModel.departureDate.isEmpty()) Color(0xFFBBBBBB) else Color(0xFF1A1A1A),
                        fontWeight = FontWeight.SemiBold
                    )
                    Icon(Icons.Filled.CalendarMonth, null, tint = WhooshRed.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                }



                Spacer(modifier = Modifier.height(16.dp))

                // ── SEARCH BUTTON ─────────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(WhooshRed)
                        .clickable { if (viewModel.searchSchedules()) onSearchSchedule() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Cari Tiket".tr(),
                        color = WhooshWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
        } // Close the Hero & Booking Form overlapping Box

        // ── RUNNING TEXT (MARQUEE) ───────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFFF5F5))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Campaign, 
                    contentDescription = null, 
                    tint = WhooshRed, 
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Pengembalian Dana / Perubahan Jadwal Mohon cek kembali jadwal perjalanan Anda. Proses memberikan izin kepada pelanggan atau calon penumpang untuk masuk stasiun atau naik Kereta Cepat".tr(),
                    fontSize = 12.sp,
                    color = Color(0xFF333333),
                    maxLines = 1,
                    modifier = Modifier.basicMarquee(
                        iterations = Int.MAX_VALUE,
                        initialDelayMillis = 1000,
                        velocity = 45.dp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── QUICK ACTIONS ─────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
                Text(
                    "Layanan".tr(),
                    color = Color(0xFF1A1A1A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp, bottom = 14.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    quickActions.forEach { action ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { action.action() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFFF9F9F9), CircleShape)
                                    .border(1.dp, Color(0xFFEEEEEE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = action.icon, 
                                    contentDescription = action.label, 
                                    tint = WhooshRed, 
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(7.dp))
                            Text(action.label.tr(),
                                fontSize = 11.sp,
                                color = Color(0xFF444444),
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── KETENTUAN LAYANAN ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Ketentuan Layanan".tr(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )
            Text(
                "Lebih banyak".tr(),
                fontSize = 12.sp,
                color = Color(0xFF7A8D9C),
                modifier = Modifier.clickable { showMoreInfo = true }
            )
        }
        Spacer(modifier = Modifier.height(14.dp))

        // Cards List
        var showKetentuanDetail by remember { mutableStateOf<Triple<String, String, (() -> Unit)?>?>(null) }
        
        if (showKetentuanDetail != null) {
            KetentuanDetailDialog(
                title = showKetentuanDetail!!.first,
                content = showKetentuanDetail!!.second,
                actionLabel = if (showKetentuanDetail!!.first.contains("Jadwal")) "Lihat Jadwal" else null,
                onActionClick = showKetentuanDetail!!.third,
                onDismiss = { showKetentuanDetail = null }
            )
        }

        val ketentuanItems = listOf(
            Triple(
                "Jadwal Whoosh & KA Feeder", 
                "Integrasi Kereta Cepat dengan KA Feeder",
                "• KA Feeder tersedia secara GRATIS bagi penumpang Whoosh dengan rute Padalarang - Bandung (PP).\n• Pastikan Anda memiliki waktu transit minimal 15-20 menit antara jadwal Whoosh dan KA Feeder.\n• Penumpang diwajibkan menunjukkan tiket Whoosh yang valid saat menggunakan KA Feeder."
            ),
            Triple(
                "Pengembalian Dana / Perubahan Jadwal", 
                "Aturan Pembatalan dan Reschedule",
                "• Pengembalian Dana (Refund): Dikenakan biaya admin sebesar 25% dari harga tiket. Dilakukan maksimal 2 jam sebelum keberangkatan.\n• Perubahan Jadwal (Reschedule): Dapat dilakukan satu kali selama ketersediaan kursi masih ada. Dilakukan maksimal 2 jam sebelum keberangkatan dengan biaya administrasi 25%."
            )
        )
        
        ketentuanItems.forEach { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable { 
                        showKetentuanDetail = Triple(
                            item.first, 
                            item.third,
                            if (item.first.contains("Jadwal")) { { onNavigateToTab(2) } } else null
                        ) 
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WhooshWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if(item.first.contains("Whoosh")) WhooshRed else Color(0xFFE5E5E5)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.first.contains("Whoosh")) {
                            Icon(Icons.Filled.Train, "Train", tint = WhooshWhite, modifier = Modifier.size(36.dp))
                            Icon(
                                Icons.Filled.Schedule, 
                                "Schedule", 
                                tint = WhooshRed, 
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 6.dp, y = (-6).dp)
                                    .size(24.dp)
                                    .background(WhooshWhite, CircleShape)
                                    .border(2.dp, WhooshWhite, CircleShape)
                            )
                        } else {
                            Icon(Icons.Filled.EventSeat, "Seat", tint = Color(0xFF888888), modifier = Modifier.size(36.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.first.tr(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A1A))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(item.second.tr(), fontSize = 11.sp, color = Color(0xFF7A8D9C), lineHeight = 16.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── STATION SELECTOR ──────────────────────────────────────────────────────────
@Composable
private fun StationSelector(
    label: String,
    icon: ImageVector,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF8F8F8))
                .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = WhooshRed, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = label.tr(),
                fontSize = 11.sp, // Reduced to prevent clipping on long text
                fontWeight = FontWeight.SemiBold,
                color = if (label == "Pilih Stasiun") Color(0xFFBBBBBB) else Color(0xFF1A1A1A),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.KeyboardArrowDown, null, tint = Color(0xFFAAAAAA), modifier = Modifier.size(16.dp))
        }
        
        // Clean dropdown menu with subtle rounded corners
        androidx.compose.material3.DropdownMenu(
            expanded = expanded, 
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    WhooshWhite,
                    RoundedCornerShape(8.dp)
                )
                .border(
                    1.dp,
                    Color(0xFFE0E0E0),
                    RoundedCornerShape(8.dp)
                )
        ) {
            options.forEach { option ->
                androidx.compose.material3.DropdownMenuItem(
                    text = { 
                        Text(text = option.tr(),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF1F1F1F),
                            letterSpacing = 0.2.sp,
                            lineHeight = 20.sp
                        )
                    },
                    onClick = { 
                        onSelect(option)
                        expanded = false 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.MenuDefaults.itemColors(
                        textColor = Color(0xFF1F1F1F),
                        disabledTextColor = Color(0xFFBBBBBB)
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
                )
            }
        }
    }
}

// ── BOTTOM NAVIGATION BAR ─────────────────────────────────────────────────────
@Composable
private fun WhooshBottomBar(selectedIndex: Int, onItemSelected: (Int) -> Unit) {
    val items = listOf(
        Pair(Icons.Filled.Home, "Beranda"),
        Pair(Icons.Filled.ConfirmationNumber, "Tiket"),
        Pair(Icons.Filled.Schedule, "Jadwal"),
        Pair(Icons.Filled.AccountCircle, "Akun")
    )
    Column {
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
        BottomAppBar(containerColor = WhooshWhite) {
            items.forEachIndexed { index, (icon, label) ->
                NavigationBarItem(
                    selected = selectedIndex == index,
                    onClick = { onItemSelected(index) },
                    icon = { Icon(icon, label.tr(), modifier = Modifier.size(22.dp)) },
                    label = {
                        Text(label.tr(),
                            fontSize = 10.sp,
                            fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = WhooshRed,
                        selectedTextColor = WhooshRed,
                        unselectedIconColor = Color(0xFFAAAAAA),
                        unselectedTextColor = Color(0xFFAAAAAA),
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}
// ── OVERLAY INFORMASI TAMBAHAN ────────────────────────────────────────────────
@Composable
private fun InformasiTambahanOverlay(onDismiss: () -> Unit) {
    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WhooshRed)
                    .height(64.dp)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.ArrowBackIosNew,
                    "Kembali".tr(),
                    tint = WhooshWhite,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { onDismiss() }
                )
                Text(
                    "Informasi Tambahan".tr(),
                    color = WhooshWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))

            // List Items (Image 2 representation)
            Column(modifier = Modifier.background(WhooshWhite)) {
                val fullItems = listOf(
                    Pair("Jadwal Whoosh & KA Feeder", "Jadwal perjalanan Kereta Cepat Whoos..."),
                    Pair("Pengembalian Dana / Perubahan Jad...", "Pengembalian Dana / Perubahan Jadwal"),
                    Pair("Ketentuan Boarding", "Proses memberikan izin kepada pelangg..."),
                    Pair("Syarat & Ketentuan KA Feeder", "Syarat & Ketentuan KA Feeder")
                )
                fullItems.forEachIndexed { index, item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.first.tr(), fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(item.second.tr(), fontSize = 13.sp, color = Color(0xFF7A8D9C), maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                        }
                        Icon(Icons.Filled.ChevronRight, null, tint = Color(0xFFBBBBBB), modifier = Modifier.size(20.dp))
                    }
                    if (index < fullItems.size - 1) {
                        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }

}

// ── DIALOG DETAIL KETENTUAN ──────────────────────────────────────────────────
@Composable
private fun KetentuanDetailDialog(
    title: String,
    content: String,
    actionLabel: String? = null,
    onActionClick: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        androidx.compose.material3.Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            color = WhooshWhite
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(text = title.tr(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = content.tr(),
                    fontSize = 14.sp,
                    color = Color(0xFF444444),
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))

                if (actionLabel != null && onActionClick != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(WhooshRed)
                            .clickable { 
                                onActionClick()
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(actionLabel.tr(), color = WhooshWhite, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
                
                androidx.compose.material3.OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDDDDDD))
                ) {
                    Text("Tutup".tr(), color = Color(0xFF666666), fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
