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
        targetValue = if (isSelected) WhooshRed else Color(0xFFEEEEEE),
        animationSpec = tween(300),
        label = "border_color"
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) WhooshRed.copy(alpha = 0.04f) else Color.White,
        animationSpec = tween(300),
        label = "bg_color"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            color = if (isSelected) WhooshRed else Color(0xFFF5F5F5),
                            shape = RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = coachClass.displayName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) WhooshRed else Color.Black
                )
                
                if (isBest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(WhooshRed.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "RECOMMENDED",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = WhooshRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFF0F0F0))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = features.joinToString(" • "),
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    lineHeight = 18.sp
                )
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = TicketUtils.formatRupiah(price),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = WhooshRed
                    )
                    Text(
                        text = "/ tiket",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
