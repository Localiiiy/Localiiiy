package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = LocaliPrimaryLight,
    onPrimary = Color(0xFF003732),
    primaryContainer = LocaliPrimaryDark,
    onPrimaryContainer = LocaliPrimaryContainer,
    secondary = Color(0xFF93C5FD),
    onSecondary = Color(0xFF1E3A8A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = LocaliSecondaryContainer,
    tertiary = LocaliAccentCoral,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF7F1D1D),
    background = IgDarkBackground,
    onBackground = IgDarkTextPrimary,
    surface = IgDarkSurface,
    onSurface = IgDarkTextPrimary,
    surfaceVariant = IgDarkSurfaceVariant,
    onSurfaceVariant = IgDarkTextSecondary,
    outline = IgDarkBorder,
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

private val LightColorScheme = lightColorScheme(
    primary = LocaliPrimary,
    onPrimary = Color.White,
    primaryContainer = LocaliPrimaryContainer,
    onPrimaryContainer = LocaliOnPrimaryContainer,
    secondary = LocaliSecondary,
    onSecondary = Color.White,
    secondaryContainer = LocaliSecondaryContainer,
    onSecondaryContainer = Color(0xFF0F172A),
    tertiary = LocaliTertiary,
    onTertiary = Color.White,
    tertiaryContainer = LocaliTertiaryContainer,
    onTertiaryContainer = Color(0xFF451A03),
    background = LocaliBackground,
    onBackground = LocaliTextPrimary,
    surface = LocaliSurface,
    onSurface = LocaliTextPrimary,
    surfaceVariant = LocaliSurfaceVariant,
    onSurfaceVariant = LocaliTextSecondary,
    outline = LocaliOutline,
    outlineVariant = LocaliOutlineVariant,
    error = LocaliError,
    onError = Color.White
)

@Composable
fun LocaliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for existing usages
@Composable
fun InstagramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = LocaliTheme(darkTheme = darkTheme, content = content)

