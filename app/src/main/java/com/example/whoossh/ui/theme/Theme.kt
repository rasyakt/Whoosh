package com.example.whoossh.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val WhooshColorScheme = lightColorScheme(
    primary = WhooshRed,
    onPrimary = WhooshWhite,
    primaryContainer = WhooshRedLight,
    onPrimaryContainer = WhooshWhite,
    secondary = WhooshRedLight,
    onSecondary = WhooshWhite,
    secondaryContainer = WhooshCardOverlay,
    onSecondaryContainer = WhooshRed,
    tertiary = WhooshGreen,
    onTertiary = WhooshWhite,
    background = WhooshBackground,
    onBackground = WhooshTextPrimary,
    surface = WhooshSurface,
    onSurface = WhooshTextPrimary,
    surfaceVariant = WhooshGrayLight,
    onSurfaceVariant = WhooshTextSecondary,
    outline = WhooshGray,
    outlineVariant = WhooshDivider,
    error = WhooshRedAccent,
    onError = WhooshWhite
)

@Composable
fun WhoosshTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = WhooshColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = WhooshRed.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = WhooshTypography,
        content = content
    )
}