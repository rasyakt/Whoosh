package com.example.whoossh.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Train
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshGreen
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun AccountScreen(
    viewModel: BookingViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToPromo: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToLanguage: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToHelpCenter: () -> Unit,
    onNavigateToPassengerList: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showBankDialog by remember { mutableStateOf(false) }
    
    // Bank dialog temporary state
    var tempBankName by remember { mutableStateOf(viewModel.savedBankName) }
    var tempAccountNo by remember { mutableStateOf(viewModel.savedAccountNo) }
    var tempAccountHolder by remember { mutableStateOf(viewModel.savedAccountHolder) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Konfirmasi Keluar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "Apakah Anda yakin ingin keluar dari akun Anda?",
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        "Keluar",
                        color = Color(0xFFE53935),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = WhooshWhite,
            shape = RoundedCornerShape(24.dp)
        )
    }

    if (showBankDialog) {
        AlertDialog(
            onDismissRequest = { showBankDialog = false },
            title = { Text("Data Rekening Refund", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Simpan data rekening Anda untuk mempercepat proses refund tiket.", fontSize = 14.sp, color = Color.Gray)
                    
                    OutlinedTextField(
                        value = tempBankName,
                        onValueChange = { tempBankName = it },
                        label = { Text("Nama Bank") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = tempAccountNo,
                        onValueChange = { tempAccountNo = it },
                        label = { Text("Nomor Rekening") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    OutlinedTextField(
                        value = tempAccountHolder,
                        onValueChange = { tempAccountHolder = it },
                        label = { Text("Nama Pemilik Rekening") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveUserBankAccount(tempBankName, tempAccountNo, tempAccountHolder)
                        showBankDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhooshRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Simpan", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBankDialog = false }) {
                    Text("Batal", color = Color.Gray)
                }
            },
            containerColor = WhooshWhite,
            shape = RoundedCornerShape(16.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                )
                .padding(top = 50.dp, bottom = 28.dp, start = 20.dp, end = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(65.dp)
                        .clip(CircleShape)
                        .background(WhooshWhite.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isLoggedIn) {
                        Text(
                            text = viewModel.userName.take(1).uppercase(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhooshWhite
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = WhooshWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (viewModel.isLoggedIn) viewModel.userName else "Pengguna Tamu",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshWhite
                    )
                    Text(
                        text = if (viewModel.isLoggedIn) viewModel.userEmail else "Silakan login untuk akses penuh",
                        fontSize = 13.sp,
                        color = WhooshWhite.copy(alpha = 0.8f)
                    )
                    if (viewModel.isLoggedIn && viewModel.userPhone.isNotEmpty()) {
                        Text(
                            text = viewModel.userPhone,
                            fontSize = 12.sp,
                            color = WhooshWhite.copy(alpha = 0.6f)
                        )
                    }
                }
            }
        }

        // Stats Row
        if (viewModel.isLoggedIn) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Total Trip", "${viewModel.getTotalTrips()}", Icons.Outlined.Train)
                    StatItem("Tiket Aktif", "${viewModel.getActiveTicketCount()}", Icons.Outlined.ConfirmationNumber)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Akun Section
        if (viewModel.isLoggedIn) {
            SectionLabel("Akun")
            MenuCard {
                MenuRow(Icons.Outlined.Edit, "Edit Profil", onClick = onNavigateToEditProfile)
                MenuDivider()
                MenuRow(Icons.Outlined.People, "Kelola Penumpang", onClick = onNavigateToPassengerList)
                MenuDivider()
                MenuRow(Icons.Outlined.History, "Riwayat Perjalanan", onClick = onNavigateToHistory)
                MenuDivider()
                MenuRow(Icons.Outlined.AccountBalance, "Data Rekening Refund", onClick = { 
                    tempBankName = viewModel.savedBankName
                    tempAccountNo = viewModel.savedAccountNo
                    tempAccountHolder = viewModel.savedAccountHolder
                    showBankDialog = true 
                })
                MenuDivider()
                MenuRow(Icons.Outlined.LocalOffer, "Promo & Diskon", onClick = onNavigateToPromo)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Pengaturan Section
        SectionLabel("Pengaturan")
        MenuCard {
            MenuRow(Icons.Outlined.Notifications, "Notifikasi", onClick = onNavigateToNotifications)
            MenuDivider()
            MenuRow(Icons.Outlined.Language, "Bahasa", onClick = onNavigateToLanguage)
            MenuDivider()
            MenuRow(Icons.Outlined.Security, "Privasi & Keamanan", onClick = onNavigateToPrivacy)
            if (viewModel.isLoggedIn) {
                MenuDivider()
                MenuRow(Icons.Outlined.Lock, "Ubah Password", onClick = onNavigateToChangePassword)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lainnya Section
        SectionLabel("Lainnya")
        MenuCard {
            MenuRow(Icons.AutoMirrored.Outlined.HelpOutline, "Pusat Bantuan", onClick = onNavigateToHelpCenter)
        }

        if (viewModel.isLoggedIn) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logout
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clickable { showLogoutDialog = true },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ExitToApp, 
                        null, 
                        tint = Color(0xFFE53935), 
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(18.dp))
                    Text(
                        text = "Keluar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFE53935)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Whoosh Ticket v1.0.0",
            fontSize = 12.sp,
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = WhooshTextSecondary,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
    )
}

@Composable
private fun MenuCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column {
            content()
        }
    }
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    title: String,
    iconColor: Color = WhooshRed,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon, 
            null, 
            tint = iconColor, 
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(18.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A1A1A),
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFE0E0E0),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        color = Color(0xFFF5F5F5),
        modifier = Modifier.padding(horizontal = 66.dp)
    )
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = WhooshRed, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(text = label, fontSize = 11.sp, color = WhooshTextSecondary)
    }
}
