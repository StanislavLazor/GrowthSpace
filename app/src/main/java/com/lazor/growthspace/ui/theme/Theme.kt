package com.lazor.growthspace.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = PrimaryBlueDark,
    background = BackgroundDark,
    surface = SurfaceDark,
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite
)

@Composable
fun GrowthSpaceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}