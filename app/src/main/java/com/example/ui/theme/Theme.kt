package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLighter,
    onPrimary = ElectricBlueOnPrimary,
    primaryContainer = ElectricBlueContainer,
    onPrimaryContainer = ElectricBlueOnContainer,
    inversePrimary = ElectricBlue,
    secondary = CyberCyanSecondary,
    onSecondary = CockpitSurfaceLowest,
    secondaryContainer = CyberCyanContainer,
    onSecondaryContainer = CockpitSurfaceLowest,
    tertiary = SafetyGreen,
    onTertiary = CockpitSurfaceLowest,
    tertiaryContainer = SafetyGreenContainer,
    onTertiaryContainer = SafetyGreenOnContainer,
    background = CockpitSurface,
    onBackground = CockpitOnSurface,
    surface = CockpitSurface,
    onSurface = CockpitOnSurface,
    surfaceVariant = CockpitSurfaceHighest,
    onSurfaceVariant = CockpitOnSurfaceVariant,
    surfaceContainerLowest = CockpitSurfaceLowest,
    surfaceContainerLow = CockpitSurfaceLow,
    surfaceContainer = CockpitSurfaceContainer,
    surfaceContainerHigh = CockpitSurfaceHigh,
    surfaceContainerHighest = CockpitSurfaceHighest,
    surfaceBright = CockpitSurfaceBright,
    outline = CockpitOutline,
    outlineVariant = CockpitOutlineVariant,
    error = EmergencyRedLight,
    onError = EmergencyRedOnError,
    errorContainer = EmergencyRedContainer,
    onErrorContainer = EmergencyRedOnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = CockpitOnSurface,
    primaryContainer = ElectricBlue,
    onPrimaryContainer = DaySurfaceLowest,
    inversePrimary = PrimaryLighter,
    secondary = Color(0xFF007888),
    onSecondary = DaySurfaceLowest,
    secondaryContainer = Color(0xFFBCEEF5),
    onSecondaryContainer = Color(0xFF00363D),
    tertiary = Color(0xFF008744),
    onTertiary = DaySurfaceLowest,
    tertiaryContainer = Color(0xFFBAF5CE),
    onTertiaryContainer = Color(0xFF00391A),
    background = DaySurface,
    onBackground = DayOnSurface,
    surface = DaySurface,
    onSurface = DayOnSurface,
    surfaceVariant = DaySurfaceHighest,
    onSurfaceVariant = DayOnSurfaceVariant,
    surfaceContainerLowest = DaySurfaceLowest,
    surfaceContainerLow = DaySurfaceLow,
    surfaceContainer = DaySurfaceContainer,
    surfaceContainerHigh = DaySurfaceHigh,
    surfaceContainerHighest = DaySurfaceHighest,
    surfaceBright = DaySurfaceLowest,
    outline = DayOutline,
    outlineVariant = DayOutlineVariant,
    error = Color(0xFFBA1A1A),
    onError = DaySurfaceLowest,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)

@Composable
fun SafeDSTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
