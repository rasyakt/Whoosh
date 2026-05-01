package com.example.whoossh.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whoossh.R
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
        // Animate icon scale with Overshoot effect
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 1000,
                easing = { fraction ->
                    val tension = 2f
                    val s = fraction - 1.0f
                    s * s * ((tension + 1.0f) * s + tension) + 1.0f
                }
            )
        )
    }

    LaunchedEffect(Unit) {
        // Smooth icon alpha
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(800)
        )
    }

    LaunchedEffect(Unit) {
        delay(600) // Start text animation slightly after logo
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(1000)
        )
    }

    LaunchedEffect(Unit) {
        delay(3500) // Slightly longer stay for professional feel
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
            )
    ) {
        // Main Branding Content
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Refined Logo Container
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(scale.value)
                    .alpha(alpha.value),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_white),
                    contentDescription = "Whoosh Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Professional Branding Hierarchy
            Text(
                text = "TICKET",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 12.sp,
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.25f),
                        offset = Offset(0f, 4f),
                        blurRadius = 10f
                    )
                ),
                color = WhooshWhite,
                modifier = Modifier.alpha(textAlpha.value)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Professional Footer/Company Name
            Text(
                text = "KERETA CEPAT INDONESIA",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp
                ),
                color = WhooshWhite.copy(alpha = 0.8f),
                modifier = Modifier.alpha(textAlpha.value)
            )
        }
    }
}
