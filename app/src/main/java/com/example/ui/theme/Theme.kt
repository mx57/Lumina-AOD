package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AmoledColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = PitchBlack,
    primaryContainer = ObsidianCard,
    onPrimaryContainer = CyberCyan,
    secondary = NeonMagenta,
    onSecondary = PureWhite,
    secondaryContainer = ObsidianCard,
    onSecondaryContainer = NeonMagenta,
    tertiary = SolarAmber,
    onTertiary = PitchBlack,
    background = PitchBlack,
    onBackground = PureWhite,
    surface = ObsidianSurface,
    onSurface = SoftSilver,
    surfaceVariant = ObsidianCard,
    onSurfaceVariant = MutedSlate,
    outline = ObsidianBorder
)

@Composable
fun LuminaAODTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AmoledColorScheme,
        typography = Typography,
        content = content
    )
}
