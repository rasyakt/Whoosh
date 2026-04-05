package com.example.whoossh.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun AccountScreen(
    viewModel: BookingViewModel,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .verticalScroll(rememberScrollState())
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                )
                .padding(horizontal = 24.dp, vertical = 28.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(WhooshWhite.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = viewModel.userName.take(1).uppercase(),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshWhite
                    )
                }

                Spacer(modifier = Modifier.width(18.dp))

                Column {
                    Text(
                        text = viewModel.userName.replaceFirstChar { it.uppercase() },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshWhite
                    )
                    Text(
                        text = "${viewModel.userName}@whoosh.id",
                        fontSize = 13.sp,
                        color = WhooshWhite.copy(0.8f),
                        modifier = Modifier.padding(top = 3.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .background(WhooshWhite.copy(0.2f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.Star,
                                null,
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Member Premium", fontSize = 12.sp, color = WhooshWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Stats row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(value = "12", label = "Perjalanan")
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFEEEEEE)))
                StatItem(value = "3", label = "Tiket Aktif")
                Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color(0xFFEEEEEE)))
                StatItem(value = "4.9★", label = "Rating")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section: Akun Saya
        SectionHeader(title = "Akun Saya")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column {
                MenuRow(icon = Icons.Filled.Person, label = "Edit Profil", iconBg = Color(0xFFE3F2FD), iconTint = Color(0xFF1565C0))
                MenuDivider()
                MenuRow(icon = Icons.Filled.ConfirmationNumber, label = "Tiket Saya", iconBg = WhooshRed.copy(0.1f), iconTint = WhooshRed)
                MenuDivider()
                MenuRow(icon = Icons.Filled.History, label = "Riwayat Perjalanan", iconBg = Color(0xFFFFF3E0), iconTint = Color(0xFFF57C00))
                MenuDivider()
                MenuRow(icon = Icons.Filled.LocalOffer, label = "Promo & Diskon", iconBg = WhooshRed.copy(0.08f), iconTint = WhooshRed, badge = "3")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: Pengaturan
        SectionHeader(title = "Pengaturan")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column {
                MenuRow(icon = Icons.Filled.Notifications, label = "Notifikasi", iconBg = Color(0xFFF3E5F5), iconTint = Color(0xFF7B1FA2))
                MenuDivider()
                MenuRow(icon = Icons.Filled.Language, label = "Bahasa", iconBg = Color(0xFFE8F5E9), iconTint = Color(0xFF2E7D32), subtitle = "Bahasa Indonesia")
                MenuDivider()
                MenuRow(icon = Icons.Filled.Security, label = "Privasi & Keamanan", iconBg = Color(0xFFF3E5F5), iconTint = Color(0xFF6A1B9A))
                MenuDivider()
                MenuRow(icon = Icons.Filled.Lock, label = "Ubah Password", iconBg = Color(0xFFFFF8E1), iconTint = Color(0xFFF9A825))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Section: Bantuan
        SectionHeader(title = "Bantuan")
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Column {
                MenuRow(icon = Icons.Filled.HelpOutline, label = "Pusat Bantuan", iconBg = Color(0xFFE3F2FD), iconTint = Color(0xFF1565C0))
                MenuDivider()
                MenuRow(icon = Icons.Filled.Star, label = "Beri Rating Aplikasi", iconBg = Color(0xFFFFF8E1), iconTint = Color(0xFFF9A825))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Logout button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable { onLogout() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WhooshRed.copy(0.06f)),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(WhooshRed.copy(0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Logout, null, tint = WhooshRed, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Text("Keluar", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = WhooshRed)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Whoosh Ticket v1.0.0",
            fontSize = 11.sp,
            color = Color.LightGray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun StatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = WhooshRed)
        Text(label, fontSize = 12.sp, color = WhooshTextSecondary, modifier = Modifier.padding(top = 2.dp))
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = WhooshTextSecondary,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 20.dp, bottom = 8.dp, top = 4.dp)
    )
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        color = Color(0xFFF5F5F5),
        modifier = Modifier.padding(start = 70.dp)
    )
}

@Composable
private fun MenuRow(
    icon: ImageVector,
    label: String,
    iconBg: Color,
    iconTint: Color,
    subtitle: String? = null,
    badge: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(iconBg, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1A1A1A))
            if (subtitle != null) {
                Text(subtitle, fontSize = 12.sp, color = WhooshTextSecondary)
            }
        }
        if (badge != null) {
            Box(
                modifier = Modifier
                    .background(WhooshRed, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(badge, fontSize = 11.sp, color = WhooshWhite, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(6.dp))
        }
        Icon(Icons.Filled.ChevronRight, null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
    }
}
