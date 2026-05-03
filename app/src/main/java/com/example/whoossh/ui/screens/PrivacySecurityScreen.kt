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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshBlue
import com.example.whoossh.ui.theme.WhooshGreen
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.viewmodel.BookingViewModel
import com.example.whoossh.utils.tr

@Composable
fun PrivacySecurityScreen(
    viewModel: BookingViewModel,
    onBack: () -> Unit
) {
    var biometric by remember { mutableStateOf(viewModel.getBiometric()) }
    var saveLogin by remember { mutableStateOf(viewModel.getSaveLogin()) }

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Privasi & Keamanan".tr(), onBack = onBack)
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
            // Security Settings
            Text(
                text = "Pengaturan Keamanan".tr(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column {
                    SecurityToggleRow(
                        icon = Icons.Filled.Fingerprint,
                        title = "Login Biometrik",
                        description = "Gunakan sidik jari atau face ID untuk login",
                        checked = biometric,
                        onCheckedChange = {
                            biometric = it
                            viewModel.setBiometric(it)
                        }
                    )
                    HorizontalDivider(color = Color(0xFFF5F5F5))
                    SecurityToggleRow(
                        icon = Icons.Filled.Lock,
                        title = "Simpan Data Login",
                        description = "Tetap login meskipun aplikasi ditutup",
                        checked = saveLogin,
                        onCheckedChange = {
                            saveLogin = it
                            viewModel.setSaveLogin(it)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Privacy Info
            Text(
                text = "Kebijakan Privasi".tr(),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    PrivacyInfoRow(
                        icon = Icons.Filled.Shield,
                        title = "Perlindungan Data",
                        description = "Data pribadi Anda dilindungi dengan enkripsi end-to-end dan disimpan secara aman di server kami.",
                        iconColor = WhooshGreen
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PrivacyInfoRow(
                        icon = Icons.Filled.Security,
                        title = "Keamanan Transaksi",
                        description = "Setiap transaksi dilindungi oleh protokol keamanan standar industri perbankan.",
                        iconColor = WhooshBlue
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PrivacyInfoRow(
                        icon = Icons.Filled.Lock,
                        title = "Hak Akses Data",
                        description = "Anda memiliki hak penuh untuk mengakses, mengubah, atau menghapus data pribadi Anda kapan saja.",
                        iconColor = WhooshRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Versi Aplikasi: 1.0.0\nTerakhir diperbarui: April 2026".tr(),
                fontSize = 12.sp,
                color = Color.LightGray,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SecurityToggleRow(
    icon: ImageVector,
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
        Icon(
            icon,
            contentDescription = null,
            tint = WhooshRed,
            modifier = Modifier.padding(end = 12.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title.tr(), fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = description.tr(), fontSize = 12.sp, color = WhooshTextSecondary, modifier = Modifier.padding(top = 2.dp))
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

@Composable
private fun PrivacyInfoRow(
    icon: ImageVector,
    title: String,
    description: String,
    iconColor: Color
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.padding(end = 12.dp, top = 2.dp)
        )
        Column {
            Text(text = title.tr(), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Text(text = description.tr(),
                fontSize = 12.sp,
                color = WhooshTextSecondary,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}
