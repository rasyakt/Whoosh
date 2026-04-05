package com.example.whoossh.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AirlineSeatReclineExtra
import androidx.compose.material.icons.filled.AirlineSeatReclineNormal
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.model.CoachClass
import com.example.whoossh.ui.components.WhooshButton
import com.example.whoossh.ui.components.WhooshTopBar
import com.example.whoossh.ui.theme.WhooshRed
import com.example.whoossh.ui.theme.WhooshRedLight
import com.example.whoossh.ui.theme.WhooshTextSecondary
import com.example.whoossh.ui.theme.WhooshWhite
import com.example.whoossh.utils.TicketUtils
import com.example.whoossh.viewmodel.BookingViewModel

@Composable
fun SelectCoachScreen(
    viewModel: BookingViewModel,
    onCoachSelected: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            WhooshTopBar(title = "Pilih Gerbong", onBack = onBack)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Pilih Jenis Gerbong",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Untuk ${viewModel.ticketCount} tiket",
                style = MaterialTheme.typography.bodyMedium,
                color = WhooshTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Ekonomi
            CoachClassCard(
                coachClass = CoachClass.EKONOMI,
                icon = Icons.Filled.EventSeat,
                features = listOf("Kursi standar dengan AC", "Colokan listrik", "Tempat bagasi"),
                price = viewModel.getPriceForClass(CoachClass.EKONOMI),
                isSelected = viewModel.selectedCoachClass == CoachClass.EKONOMI,
                onClick = { viewModel.selectCoachClass(CoachClass.EKONOMI) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Bisnis
            CoachClassCard(
                coachClass = CoachClass.BISNIS,
                icon = Icons.Filled.AirlineSeatReclineNormal,
                features = listOf("Kursi lebih lebar", "Sandaran kaki", "Snack box gratis"),
                price = viewModel.getPriceForClass(CoachClass.BISNIS),
                isSelected = viewModel.selectedCoachClass == CoachClass.BISNIS,
                onClick = { viewModel.selectCoachClass(CoachClass.BISNIS) }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // VIP
            CoachClassCard(
                coachClass = CoachClass.VIP,
                icon = Icons.Filled.AirlineSeatReclineExtra,
                features = listOf("Kursi premium reclining", "Makanan lengkap", "WiFi premium"),
                price = viewModel.getPriceForClass(CoachClass.VIP),
                isSelected = viewModel.selectedCoachClass == CoachClass.VIP,
                onClick = { viewModel.selectCoachClass(CoachClass.VIP) },
                isBest = true
            )

            Spacer(modifier = Modifier.height(28.dp))

            WhooshButton(
                text = "Lanjutkan",
                onClick = onCoachSelected,
                enabled = viewModel.selectedCoachClass != null
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun CoachClassCard(
    coachClass: CoachClass,
    icon: ImageVector,
    features: List<String>,
    price: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    isBest: Boolean = false
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) WhooshRed else Color.LightGray.copy(alpha = 0.5f),
        animationSpec = tween(300),
        label = "border_color"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) WhooshRed.copy(alpha = 0.04f) else WhooshWhite,
        animationSpec = tween(300),
        label = "bg_color"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                color = if (isSelected) WhooshRed.copy(alpha = 0.12f) else Color.LightGray.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = if (isSelected) WhooshRed else WhooshTextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = coachClass.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (isBest) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = WhooshRed,
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = WhooshWhite,
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "BEST",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = WhooshWhite
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = coachClass.description,
                            fontSize = 12.sp,
                            color = WhooshTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Features
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "✓", fontSize = 13.sp, color = WhooshRed, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = feature, fontSize = 13.sp, color = WhooshTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Price
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = if (isSelected) WhooshRed.copy(alpha = 0.08f) else WhooshRed.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Harga / tiket",
                    fontSize = 13.sp,
                    color = WhooshTextSecondary
                )
                Text(
                    text = TicketUtils.formatRupiah(price),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = WhooshRed
                )
            }
        }
    }
}
