package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color

/**
 * ========================================================================
 * Localiiiy Material 3 Theme Configuration
 * ========================================================================
 * Defines semantic Material 3 Light & Dark ColorSchemes alongside
 * custom Localiiiy brand tokens and high-contrast typography hierarchy.
 */

// 1. Material 3 Light Color Scheme
val LocaliLightColorScheme = lightColorScheme(
    primary = LocaliPrimary,
    onPrimary = Color.White,
    primaryContainer = LocaliPrimaryContainer,
    onPrimaryContainer = LocaliOnPrimaryContainer,
    inversePrimary = LocaliPrimaryLight,

    secondary = LocaliSecondary,
    onSecondary = Color.White,
    secondaryContainer = LocaliSecondaryContainer,
    onSecondaryContainer = LocaliOnSecondaryContainer,

    tertiary = LocaliTertiary,
    onTertiary = Color.White,
    tertiaryContainer = LocaliTertiaryContainer,
    onTertiaryContainer = LocaliOnTertiaryContainer,

    background = LocaliBackground,
    onBackground = LocaliTextPrimary,
    surface = LocaliSurface,
    onSurface = LocaliTextPrimary,
    surfaceVariant = LocaliSurfaceVariant,
    onSurfaceVariant = LocaliTextSecondary,
    surfaceTint = LocaliSurfaceTint,

    outline = LocaliOutline,
    outlineVariant = LocaliOutlineVariant,
    scrim = Color.Black.copy(alpha = 0.6f),

    error = LocaliError,
    onError = Color.White,
    errorContainer = LocaliErrorContainer,
    onErrorContainer = LocaliOnErrorContainer,

    inverseSurface = LocaliSecondaryDark,
    inverseOnSurface = Color(0xFFF8FAFC)
)

// 2. Material 3 Dark Color Scheme (Radar Midnight Aesthetic)
val LocaliDarkColorScheme = darkColorScheme(
    primary = LocaliPrimaryLight,
    onPrimary = Color(0xFF00363A),
    primaryContainer = LocaliPrimaryDark,
    onPrimaryContainer = LocaliPrimaryContainer,
    inversePrimary = LocaliPrimary,

    secondary = LocaliSecondaryLight,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),

    tertiary = LocaliTertiaryLight,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = LocaliTertiaryContainer,

    background = LocaliDarkBackground,
    onBackground = LocaliDarkTextPrimary,
    surface = LocaliDarkSurface,
    onSurface = LocaliDarkTextPrimary,
    surfaceVariant = LocaliDarkSurfaceVariant,
    onSurfaceVariant = LocaliDarkTextSecondary,

    outline = LocaliDarkOutline,
    outlineVariant = LocaliDarkOutlineVariant,
    scrim = Color.Black.copy(alpha = 0.8f),

    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),

    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0F172A)
)

/**
 * Convenience accessor object for Localiiiy theme styling
 */
object LocaliTheme {
    val colors: LocaliBrandColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLocaliBrandColors.current

    val brandTypography: LocaliBrandTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalLocaliBrandTypography.current

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography
}

/**
 * Primary Localiiiy Brand Theme Composable
 */
@Composable
fun LocaliTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) LocaliDarkColorScheme else LocaliLightColorScheme
    val brandColors = remember(darkTheme) {
        if (darkTheme) {
            LocaliBrandColors(
                primaryTeal = LocaliPrimaryLight,
                primaryTealLight = LocaliPrimaryLight,
                accentCoral = LocaliAccentCoral,
                accentMint = LocaliAccentMint,
                accentCyan = LocaliAccentCyan,
                sunsetAmber = LocaliTertiaryLight,
                deepNavy = LocaliDeepNavy,
                heartActive = LocaliHeart,
                verifiedBadge = LocaliPrimaryLight
            )
        } else {
            LocaliBrandColors()
        }
    }
    val brandTypography = remember { LocaliBrandTypography() }

    CompositionLocalProvider(
        LocalLocaliBrandColors provides brandColors,
        LocalLocaliBrandTypography provides brandTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LocaliTypography,
            content = content
        )
    }
}


