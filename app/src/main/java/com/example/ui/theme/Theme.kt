package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MandateDarkColorScheme = darkColorScheme(
    primary = NaijaGold,
    onPrimary = DeepCharcoal,
    primaryContainer = NaijaGreenPrimary,
    onPrimaryContainer = NaijaGoldLight,
    secondary = NaijaGreenLight,
    onSecondary = DeepCharcoal,
    secondaryContainer = EmeraldSurfaceVariant,
    onSecondaryContainer = TextPrimaryLight,
    tertiary = NaijaGoldLight,
    onTertiary = DeepCharcoal,
    background = DeepCharcoal,
    onBackground = TextPrimaryLight,
    surface = EmeraldSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = EmeraldSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = CardBorderGold,
    error = AlertRed,
    onError = Color.White
)

private val MandateLightColorScheme = lightColorScheme(
    primary = NaijaGreenPrimary,
    onPrimary = Color.White,
    primaryContainer = NaijaGreenLight,
    onPrimaryContainer = Color.White,
    secondary = NaijaGoldDark,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF9E8B6),
    onSecondaryContainer = DeepCharcoal,
    tertiary = NaijaGold,
    onTertiary = DeepCharcoal,
    background = Color(0xFFF6FAF7),
    onBackground = DeepCharcoal,
    surface = Color.White,
    onSurface = DeepCharcoal,
    surfaceVariant = Color(0xFFE2EDE6),
    onSurfaceVariant = Color(0xFF284033),
    outline = Color(0xFF8BA697),
    error = AlertRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force rich regal atmosphere default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) MandateDarkColorScheme else MandateLightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
