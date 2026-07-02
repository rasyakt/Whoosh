package com.example.whoossh.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.viewmodel.BookingViewModel
import com.example.whoossh.utils.tr
import com.example.whoossh.utils.trStr

@Composable
fun EditProfileScreen(
    viewModel: BookingViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf(viewModel.userName) }
    var email by remember { mutableStateOf(viewModel.userEmail) }
    var phone by remember { mutableStateOf(viewModel.userPhone) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Edit Profil".tr(), onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(WhooshWhite.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = viewModel.userName.take(1).uppercase(),
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = WhooshWhite
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(WhooshWhite),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.CameraAlt,
                                contentDescription = null,
                                tint = WhooshRed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Ubah Foto Profil".tr(),
                        fontSize = 13.sp,
                        color = WhooshWhite.copy(0.8f)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                // Name
                Text("Nama Lengkap".tr(), fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    leadingIcon = { Icon(Icons.Filled.Person, null, tint = WhooshRed) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = WhooshRed
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Email
                Text("Email".tr(), fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    leadingIcon = { Icon(Icons.Filled.Email, null, tint = WhooshRed) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = WhooshRed
                    )
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Phone
                Text("Nomor HP".tr(), fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it.filter { c -> c.isDigit() } },
                    leadingIcon = { Icon(Icons.Filled.Phone, null, tint = WhooshRed) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WhooshRed,
                        unfocusedBorderColor = Color.LightGray,
                        focusedLabelColor = WhooshRed
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                WhooshButton(
                    text = if (viewModel.isLoading) "Menyimpan...".tr() else "Simpan Perubahan".tr(),
                    onClick = {
                        viewModel.updateProfile(name, email, phone) { success ->
                            if (success) {
                                Toast.makeText(context, "Profil berhasil diperbarui".trStr(viewModel.currentLanguage.value), Toast.LENGTH_SHORT).show()
                                onBack()
                            } else {
                                Toast.makeText(context, "Gagal memperbarui profil. Periksa data Anda.".trStr(viewModel.currentLanguage.value), Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    enabled = !viewModel.isLoading
                )
            }
        }
    }
}
