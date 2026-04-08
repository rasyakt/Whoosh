package com.example.whoossh.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.Promo
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshOrange
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite

@Composable
fun PromoScreen(
    onBack: () -> Unit
) {
    val promos = remember {
        listOf(
            Promo(1, "Diskon Pengguna Baru", "Nikmati potongan harga untuk pembelian pertama Anda di Whoosh", "15%", "30 Apr 2026", "NEWUSER15", "Min. pembelian Rp 200.000"),
            Promo(2, "Weekend Hemat", "Promo spesial untuk perjalanan di hari Sabtu & Minggu", "10%", "31 Mei 2026", "WEEKEND10", "Min. pembelian Rp 150.000"),
            Promo(3, "Flash Sale VIP", "Upgrade ke kelas VIP dengan harga spesial", "20%", "15 Apr 2026", "FLASHVIP20", "Khusus kelas VIP"),
            Promo(4, "Promo Ramadan", "Mudik lebih hemat dengan kereta cepat Whoosh", "25%", "10 Apr 2026", "RAMADAN25", "Min. 2 tiket"),
            Promo(5, "Cashback Bisnis", "Dapatkan cashback untuk perjalanan kelas Bisnis", "Rp 50.000", "30 Jun 2026", "BISNIS50K", "Khusus kelas Bisnis"),
            Promo(6, "Student Promo", "Diskon spesial untuk pelajar dan mahasiswa", "30%", "31 Des 2026", "STUDENT30", "Wajib tunjukkan KTM"),
        )
    }

    Scaffold(
        topBar = {
            WhooshTopBar(title = "Promo & Diskon", onBack = onBack)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 16.dp)
        ) {
            items(promos) { promo ->
                PromoCard(promo)
            }
        }
    }
}

@Composable
private fun PromoCard(promo: Promo) {
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            // Top gradient banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(listOf(WhooshGradientStart, WhooshGradientEnd))
                    )
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.LocalOffer,
                        contentDescription = null,
                        tint = WhooshWhite,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 12.dp))
                    Column {
                        Text(
                            text = promo.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhooshWhite
                        )
                        Text(
                            text = "Diskon ${promo.discount}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = WhooshWhite
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = promo.description,
                    fontSize = 13.sp,
                    color = WhooshTextSecondary,
                    lineHeight = 20.sp
                )

                if (promo.minPurchase.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = promo.minPurchase,
                        fontSize = 11.sp,
                        color = WhooshOrange,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Kode Promo:", fontSize = 10.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .background(WhooshRed.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = promo.code,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = WhooshRed,
                                    letterSpacing = 1.sp
                                )
                            }
                            IconButton(
                                onClick = { clipboardManager.setText(AnnotatedString(promo.code)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Filled.ContentCopy, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("Berlaku sampai", fontSize = 10.sp, color = Color.Gray)
                        Text(
                            text = promo.validUntil,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WhooshTextSecondary
                        )
                    }
                }
            }
        }
    }
}
