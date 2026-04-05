package com.example.whoossh.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.ui.theme.WhooshGradientDark
import com.example.whoossh.ui.theme.WhooshGradientEnd
import com.example.whoossh.ui.theme.WhooshGradientStart
import com.example.whoossh.ui.theme.WhooshWhite
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.3f) }
    val alpha = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Animate icon scale
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(Unit) {
        // Animate icon alpha
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )
    }

    LaunchedEffect(Unit) {
        delay(400)
        // Animate text
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(600)
        )
    }

    LaunchedEffect(Unit) {
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WhooshGradientDark,
                        WhooshGradientStart,
                        WhooshGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Train Icon
            Icon(
                imageVector = Icons.Filled.Train,
                contentDescription = "Whoosh Logo",
                tint = WhooshWhite,
                modifier = Modifier
                    .size(100.dp)
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // App Name
            Text(
                text = "Whoosh",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = WhooshWhite,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Ticket",
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                color = WhooshWhite.copy(alpha = 0.85f),
                letterSpacing = 8.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Kereta Cepat Indonesia",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = WhooshWhite.copy(alpha = 0.6f),
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}
