package com.garbhyatra.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Saffron,
    onPrimary = TextPrimary,
    primaryContainer = Blush,
    onPrimaryContainer = TextPrimary,
    secondary = BlushDeep,
    onSecondary = TextPrimary,
    tertiary = Leaf,
    onTertiary = Surface,
    background = Cream,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = LeafSoft,
    onSurfaceVariant = TextSecondary,
    error = ErrorSoft,
    onError = Surface
)

private val DarkColors = darkColorScheme(
    primary = Saffron,
    onPrimary = TextPrimary,
    primaryContainer = NightLotus,
    onPrimaryContainer = DarkText,
    secondary = BlushDeep,
    onSecondary = TextPrimary,
    tertiary = Leaf,
    onTertiary = DarkText,
    background = DarkBackground,
    onBackground = DarkText,
    surface = DarkSurface,
    onSurface = DarkText,
    surfaceVariant = NightLotus,
    onSurfaceVariant = DarkText,
    error = ErrorSoft,
    onError = DarkText
)

@Composable
fun GarbhyatraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
