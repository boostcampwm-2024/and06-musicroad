package com.squirtles.musicroad.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Black,
    secondary = Blue,
    tertiary = Purple,
    surface = Black,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = White,
    secondary = Blue,
    tertiary = Purple,
    surface = White,
    onSurface = Black
)

@Composable
fun MusicRoadTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}