package com.example.whoossh.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshBlue
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshGreen
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite

@Composable
fun HelpCenterScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val faqItems = remember {
        listOf(
            "Bagaimana cara memesan tiket?" to "Pilih stasiun asal dan tujuan di halaman utama, tentukan tanggal keberangkatan dan jumlah tiket, lalu tekan \"Cari Jadwal\". Pilih jadwal yang diinginkan, pilih kelas gerbong dan kursi, lalu konfirmasi pemesanan Anda.",
            "Apakah bisa membatalkan tiket?" to "Pembatalan tiket dapat dilakukan maksimal 3 jam sebelum keberangkatan. Silakan hubungi customer service kami untuk proses pembatalan dan pengembalian dana.",
            "Berapa lama tiket berlaku?" to "Tiket berlaku hanya untuk tanggal dan jadwal yang dipilih saat pemesanan. Pastikan Anda datang minimal 30 menit sebelum keberangkatan.",
            "Bagaimana cara melihat e-ticket?" to "Setelah pemesanan berhasil, e-ticket akan tersedia di menu \"Tiket Saya\". Anda bisa menunjukkan QR code pada e-ticket saat boarding.",
            "Metode pembayaran apa saja yang tersedia?" to "Saat ini Whoosh mendukung pembayaran melalui transfer bank, e-wallet (GoPay, OVO, DANA), dan kartu kredit/debit.",
            "Apakah bisa reschedule tiket?" to "Reschedule dapat dilakukan maksimal 24 jam sebelum keberangkatan melalui menu \"Tiket Saya\" atau menghubungi customer service.",
        )
    }

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Pusat Bantuan", onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.horizontalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Icon(
                        Icons.Filled.QuestionAnswer,
                        contentDescription = null,
                        tint = WhooshWhite,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Ada yang bisa kami bantu?",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshWhite
                    )
                    Text(
                        text = "Temukan jawaban dari pertanyaan yang sering diajukan atau hubungi tim kami",
                        fontSize = 13.sp,
                        color = WhooshWhite.copy(0.85f),
                        lineHeight = 20.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // FAQ Section
            Text(
                text = "Pertanyaan Umum (FAQ)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column {
                    faqItems.forEachIndexed { index, (question, answer) ->
                        FaqItem(question = question, answer = answer)
                        if (index < faqItems.size - 1) {
                            HorizontalDivider(color = Color(0xFFF5F5F5))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Section
            Text(
                text = "Hubungi Kami",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                ContactCard(
                    icon = Icons.Filled.Call,
                    title = "Telepon",
                    value = "021-12345678",
                    subtitle = "Senin - Jumat, 08:00 - 21:00 WIB",
                    color = WhooshGreen,
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:02112345678"))
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                ContactCard(
                    icon = Icons.Filled.Email,
                    title = "Email",
                    value = "cs@whoosh.co.id",
                    subtitle = "Respons dalam 1x24 jam",
                    color = WhooshBlue,
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:cs@whoosh.co.id"))
                        context.startActivity(intent)
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                ContactCard(
                    icon = Icons.Filled.Language,
                    title = "Website",
                    value = "www.whoosh.co.id",
                    subtitle = "Kunjungi website resmi kami",
                    color = WhooshRed,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.whoosh.co.id"))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CS Button
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                WhooshButton(
                    text = "Chat dengan Customer Service",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/6281234567890"))
                        context.startActivity(intent)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = question,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                tint = WhooshRed,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = expanded) {
            Text(
                text = answer,
                fontSize = 13.sp,
                color = WhooshTextSecondary,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun ContactCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(color.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = subtitle, fontSize = 11.sp, color = Color.LightGray)
            }
        }
    }
}
