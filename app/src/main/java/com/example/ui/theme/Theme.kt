package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightColorScheme = lightColorScheme(
    primary = SkyPrimary,
    onPrimary = OnSkyPrimary,
    primaryContainer = SkyContainer,
    onPrimaryContainer = OnSkyContainer,
    secondary = IndigoSecondary,
    onSecondary = OnIndigoSecondary,
    secondaryContainer = IndigoContainer,
    onSecondaryContainer = OnIndigoContainer,
    tertiary = AmberTertiary,
    onTertiary = OnAmberTertiary,
    tertiaryContainer = AmberContainer,
    onTertiaryContainer = OnAmberContainer,
    background = CanvasBackground,
    onBackground = InkOnSurface,
    surface = PureWhiteSurface,
    onSurface = InkOnSurface,
    surfaceVariant = TintSurfaceVariant,
    onSurfaceVariant = InkOnSurfaceVariant,
    outline = SoftOutline,
    outlineVariant = SoftOutlineVariant,
    error = CoralAccent,
    errorContainer = CoralContainer,
    onErrorContainer = OnCoralContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkSkyPrimary,
    onPrimary = DarkOnSkyPrimary,
    primaryContainer = DarkSkyContainer,
    onPrimaryContainer = DarkOnSkyContainer,
    secondary = DarkIndigoSecondary,
    onSecondary = DarkOnIndigoSecondary,
    secondaryContainer = DarkIndigoContainer,
    onSecondaryContainer = DarkOnIndigoContainer,
    tertiary = DarkAmberTertiary,
    onTertiary = DarkOnAmberTertiary,
    tertiaryContainer = DarkAmberContainer,
    onTertiaryContainer = DarkOnAmberContainer,
    background = DarkBackground,
    onBackground = DarkInkOnSurface,
    surface = DarkSurface,
    onSurface = DarkInkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkInkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = Color(0xFFFB7185),
    errorContainer = Color(0xFF4C0519),
    onErrorContainer = Color(0xFFFFE4E6)
)

val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun UasInggrisTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
