package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// R1 Design System target palette (dark professional / premium industrial).
// Background #0F172A · Surface/Card #1E293B · Border #334155 ·
// Primary Yellow #FFC72C (dark navy content on yellow, like the reference
// board's "Log in" / "Sign up" / FAB buttons) · Primary Text #F1F5F9 · Secondary Text #94A3B8
private val DarkColorScheme = darkColorScheme(
    primary = KonstrivoGold,                           // #FFC72C (Reference Primary Yellow)
    onPrimary = KonstrivoNavy,                         // Dark navy text on yellow CTAs (reference)
    primaryContainer = Color(0xFF334155),              // Target Border
    onPrimaryContainer = Color(0xFFF1F5F9),
    secondary = ConstructionBlueLight,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = KonstrivoGold,                   // Gold text on dark chips/badges (reference)
    tertiary = KonstrivoGold,
    background = KonstrivoNavy,                        // #0F172A (Target Background)
    surface = Color(0xFF1E293B),                       // Target Surface/Card
    surfaceVariant = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),                     // Target Primary Text
    onSurfaceVariant = Color(0xFF94A3B8),              // Target Secondary Text
    outline = Color(0xFF334155)                        // Target Border
)

private val LightColorScheme = lightColorScheme(
    primary = ConstructionNavyPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1F5F9),
    onPrimaryContainer = ConstructionNavyPrimary,
    secondary = ConstructionAmberSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF92400E),
    tertiary = ConstructionBlue,
    background = ConstructionSurfaceLight,
    surface = ConstructionCardLight,
    surfaceVariant = Color(0xFFF8FAFC),
    onSurface = ConstructionTextPrimaryLight,
    onSurfaceVariant = ConstructionTextSecondaryLight,
    outline = ConstructionBorderLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

