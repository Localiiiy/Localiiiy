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
val LocaliiiyLightColorScheme = lightColorScheme(
    primary = LocaliiiyPrimary,
    onPrimary = Color.White,
    primaryContainer = LocaliiiyPrimaryContainer,
    onPrimaryContainer = LocaliiiyOnPrimaryContainer,
    inversePrimary = LocaliiiyPrimaryLight,

    secondary = LocaliiiySecondary,
    onSecondary = Color.White,
    secondaryContainer = LocaliiiySecondaryContainer,
    onSecondaryContainer = LocaliiiyOnSecondaryContainer,

    tertiary = LocaliiiyTertiary,
    onTertiary = Color.White,
    tertiaryContainer = LocaliiiyTertiaryContainer,
    onTertiaryContainer = LocaliiiyOnTertiaryContainer,

    background = LocaliiiyBackground,
    onBackground = LocaliiiyTextPrimary,
    surface = LocaliiiySurface,
    onSurface = LocaliiiyTextPrimary,
    surfaceVariant = LocaliiiySurfaceVariant,
    onSurfaceVariant = LocaliiiyTextSecondary,
    surfaceTint = LocaliiiySurfaceTint,

    outline = LocaliiiyOutline,
    outlineVariant = LocaliiiyOutlineVariant,
    scrim = Color.Black.copy(alpha = 0.6f),

    error = LocaliiiyError,
    onError = Color.White,
    errorContainer = LocaliiiyErrorContainer,
    onErrorContainer = LocaliiiyOnErrorContainer,

    inverseSurface = LocaliiiySecondaryDark,
    inverseOnSurface = Color(0xFFF8FAFC)
)

// 2. Material 3 Dark Color Scheme (Radar Midnight Aesthetic)
val LocaliiiyDarkColorScheme = darkColorScheme(
    primary = LocaliiiyPrimaryLight,
    onPrimary = Color(0xFF00363A),
    primaryContainer = LocaliiiyPrimaryDark,
    onPrimaryContainer = LocaliiiyPrimaryContainer,
    inversePrimary = LocaliiiyPrimary,

    secondary = LocaliiiySecondaryLight,
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFE2E8F0),

    tertiary = LocaliiiyTertiaryLight,
    onTertiary = Color(0xFF451A03),
    tertiaryContainer = Color(0xFF78350F),
    onTertiaryContainer = LocaliiiyTertiaryContainer,

    background = LocaliiiyDarkBackground,
    onBackground = LocaliiiyDarkTextPrimary,
    surface = LocaliiiyDarkSurface,
    onSurface = LocaliiiyDarkTextPrimary,
    surfaceVariant = LocaliiiyDarkSurfaceVariant,
    onSurfaceVariant = LocaliiiyDarkTextSecondary,

    outline = LocaliiiyDarkOutline,
    outlineVariant = LocaliiiyDarkOutlineVariant,
    scrim = Color.Black.copy(alpha = 0.8f),

    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2),

    inverseSurface = Color(0xFFF8FAFC),
    inverseOnSurface = Color(0xFF0F172A)
)

// 3. Black Hole Atmospheric Color Scheme (Singularity Obsidian & Accretion Orange)
val LocaliiiyBlackHoleColorScheme = darkColorScheme(
    primary = Color(0xFFFF7A00), // Accretion Flare Orange
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF431407),
    onPrimaryContainer = Color(0xFFFFD8B3),
    secondary = Color(0xFFC084FC), // Gravitational Arc Violet
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF3B0764),
    onSecondaryContainer = Color(0xFFF3E8FF),
    tertiary = Color(0xFFFFB703), // Photon Amber
    onTertiary = Color(0xFF451A03),
    background = Color(0xFF020205), // Void Black
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0B0614),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF190C2C),
    onSurfaceVariant = Color(0xFFD8B4FE),
    outline = Color(0xFF4C1D95),
    outlineVariant = Color(0xFF2E1065)
)

// 4. Moon Atmospheric Color Scheme (Luminous Lunar Silver & Deep Crater Slate)
val LocaliiiyMoonColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8), // Moonbeam Cyan
    onPrimary = Color(0xFF082F49),
    primaryContainer = Color(0xFF0C4A6E),
    onPrimaryContainer = Color(0xFFE0F2FE),
    secondary = Color(0xFFE2E8F0), // Lunar Silver
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF334155),
    onSecondaryContainer = Color(0xFFF8FAFC),
    tertiary = Color(0xFF94A3B8), // Stardust Ash
    onTertiary = Color(0xFF0F172A),
    background = Color(0xFF0B0F19), // Lunar Night Canvas
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF131B2E),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF334155)
)

// 5. Galaxy Atmospheric Color Scheme (Cosmic Nebula Cyan & Pulsar Magenta)
val LocaliiiyGalaxyColorScheme = darkColorScheme(
    primary = Color(0xFF00F0FF), // Stellar Cyan
    onPrimary = Color(0xFF003840),
    primaryContainer = Color(0xFF004D56),
    onPrimaryContainer = Color(0xFFB3F9FF),
    secondary = Color(0xFFD946EF), // Pulsar Magenta
    onSecondary = Color(0xFF4A044E),
    secondaryContainer = Color(0xFF701A75),
    onSecondaryContainer = Color(0xFFFDF4FF),
    tertiary = Color(0xFF818CF8), // Nebula Purple
    onTertiary = Color(0xFF1E1B4B),
    background = Color(0xFF070414), // Deep Cosmos
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF0F0B26),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF1B1442),
    onSurfaceVariant = Color(0xFFC7D2FE),
    outline = Color(0xFF4338CA),
    outlineVariant = Color(0xFF312E81)
)

/**
 * Convenience accessor object for Localiiiy theme styling
 */
object LocaliiiyTheme {
    val colors: LocaliiiyBrandColors
        @Composable
        @ReadOnlyComposable
        get() = LocalLocaliiiyBrandColors.current

    val brandTypography: LocaliiiyBrandTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalLocaliiiyBrandTypography.current

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
 * Primary Localiiiy Brand Theme Composable with dynamic atmospheric theme palettes
 */
@Composable
fun LocaliiiyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeKey: String = "DEFAULT",
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeKey.uppercase()) {
        "BLACK_HOLE" -> LocaliiiyBlackHoleColorScheme
        "MOON" -> LocaliiiyMoonColorScheme
        "GALAXY" -> LocaliiiyGalaxyColorScheme
        else -> if (darkTheme) LocaliiiyDarkColorScheme else LocaliiiyLightColorScheme
    }
    val brandColors = remember(darkTheme, themeKey) {
        when (themeKey.uppercase()) {
            "BLACK_HOLE" -> LocaliiiyBrandColors(
                primaryTeal = Color(0xFFFF7A00),
                primaryTealLight = Color(0xFFFFB74D),
                accentCoral = Color(0xFFFF5722),
                accentMint = Color(0xFFC084FC),
                accentCyan = Color(0xFFFFB703),
                sunsetAmber = Color(0xFFFF9800),
                deepNavy = Color(0xFF020205),
                heartActive = Color(0xFFFF3366),
                verifiedBadge = Color(0xFFFF7A00)
            )
            "MOON" -> LocaliiiyBrandColors(
                primaryTeal = Color(0xFF38BDF8),
                primaryTealLight = Color(0xFF7DD3FC),
                accentCoral = Color(0xFFF43F5E),
                accentMint = Color(0xFF34D399),
                accentCyan = Color(0xFF38BDF8),
                sunsetAmber = Color(0xFFFBBF24),
                deepNavy = Color(0xFF0B0F19),
                heartActive = Color(0xFFF43F5E),
                verifiedBadge = Color(0xFF38BDF8)
            )
            "GALAXY" -> LocaliiiyBrandColors(
                primaryTeal = Color(0xFF00F0FF),
                primaryTealLight = Color(0xFF67E8F9),
                accentCoral = Color(0xFFD946EF),
                accentMint = Color(0xFF00F0FF),
                accentCyan = Color(0xFF818CF8),
                sunsetAmber = Color(0xFFC084FC),
                deepNavy = Color(0xFF070414),
                heartActive = Color(0xFFFF2A85),
                verifiedBadge = Color(0xFF00F0FF)
            )
            else -> if (darkTheme) {
                LocaliiiyBrandColors(
                    primaryTeal = LocaliiiyPrimaryLight,
                    primaryTealLight = LocaliiiyPrimaryLight,
                    accentCoral = LocaliiiyAccentCoral,
                    accentMint = LocaliiiyAccentMint,
                    accentCyan = LocaliiiyAccentCyan,
                    sunsetAmber = LocaliiiyTertiaryLight,
                    deepNavy = LocaliiiyDeepNavy,
                    heartActive = LocaliiiyHeart,
                    verifiedBadge = LocaliiiyPrimaryLight
                )
            } else {
                LocaliiiyBrandColors()
            }
        }
    }
    val brandTypography = remember { LocaliiiyBrandTypography() }

    CompositionLocalProvider(
        LocalLocaliiiyBrandColors provides brandColors,
        LocalLocaliiiyBrandTypography provides brandTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = LocaliiiyTypography,
            content = content
        )
    }
}



