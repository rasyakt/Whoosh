package com.example.whoossh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun NotificationSettingsScreen(
    viewModel: BookingViewModel,
    onBack: () -> Unit
) {
    var promoNotif by remember { mutableStateOf(viewModel.getNotifPromo()) }
    var travelNotif by remember { mutableStateOf(viewModel.getNotifTravel()) }
    var updateNotif by remember { mutableStateOf(viewModel.getNotifUpdate()) }
    var emailNotif by remember { mutableStateOf(viewModel.getNotifEmail()) }

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Notifikasi", onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Pengaturan Notifikasi",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Kelola notifikasi yang ingin Anda terima",
                fontSize = 13.sp,
                color = WhooshTextSecondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column {
                    NotifToggleRow(
                        title = "Promo & Penawaran",
                        description = "Dapatkan info promo dan diskon terbaru",
                        checked = promoNotif,
                        onCheckedChange = {
                            promoNotif = it
                            viewModel.setNotifPromo(it)
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    NotifToggleRow(
                        title = "Info Perjalanan",
                        description = "Pengingat jadwal dan perubahan perjalanan",
                        checked = travelNotif,
                        onCheckedChange = {
                            travelNotif = it
                            viewModel.setNotifTravel(it)
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    NotifToggleRow(
                        title = "Update Aplikasi",
                        description = "Informasi fitur baru dan pembaruan app",
                        checked = updateNotif,
                        onCheckedChange = {
                            updateNotif = it
                            viewModel.setNotifUpdate(it)
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    NotifToggleRow(
                        title = "Email Notifikasi",
                        description = "Terima notifikasi melalui email",
                        checked = emailNotif,
                        onCheckedChange = {
                            emailNotif = it
                            viewModel.setNotifEmail(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotifToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                fontSize = 12.sp,
                color = WhooshTextSecondary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = WhooshRed,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}
