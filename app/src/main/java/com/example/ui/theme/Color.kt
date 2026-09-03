package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * ========================================================================
 * Localiiiy Brand Color Palette
 * ========================================================================
 * A distinctive, hyperlocal community aesthetic blending luminous sea teal,
 * midnight slate canvas, solar coral signals, and neon radar mint.
 */

// 1. Primary Tonal Spectrum (Hyperlocal Teal / Viridian)
val LocaliPrimary = Color(0xFF00838F)           // Cyan-Teal 800: Core brand identity
val LocaliPrimaryLight = Color(0xFF4DD0E1)      // Aqua-Teal 300: Luminous dark-theme variant & glow
val LocaliPrimaryDark = Color(0xFF004D40)       // Deep Teal 900: High-contrast button fills
val LocaliPrimaryContainer = Color(0xFFE0F7FA)  // Soft cyan-teal container tint
val LocaliOnPrimaryContainer = Color(0xFF00363A)// Deep viridian text on primary container
val LocaliPrimaryTeal = LocaliPrimary

// 2. Secondary Tonal Spectrum (Midnight Navy & Community Slate)
val LocaliSecondary = Color(0xFF1E293B)         // Slate 800: Grounding structural navy
val LocaliSecondaryLight = Color(0xFF94A3B8)    // Slate 400: Dark mode secondary
val LocaliSecondaryDark = Color(0xFF0F172A)     // Slate 900: Deep night headers
val LocaliSecondaryContainer = Color(0xFFE2E8F0)// Slate 200: Chip backgrounds
val LocaliOnSecondaryContainer = Color(0xFF0F172A)

// 3. Tertiary Tonal Spectrum (Sunset Amber / Neighborhood Warmth)
val LocaliTertiary = Color(0xFFF59E0B)          // Amber 500: Community warmth, story highlights
val LocaliTertiaryLight = Color(0xFFFBBF24)     // Amber 400: Dark theme tertiary accent
val LocaliTertiaryDark = Color(0xFFB45309)      // Amber 700: High contrast warm borders
val LocaliTertiaryContainer = Color(0xFFFEF3C7) // Amber 100: Warm card backdrop
val LocaliOnTertiaryContainer = Color(0xFF78350F)

// 4. Vibrant Hyperlocal Accents
val LocaliAccentCoral = Color(0xFFFF5722)       // Live broadcasts, urgent community updates, pulses
val LocaliAccentCoralContainer = Color(0xFFFFECE2)
val LocaliAccentMint = Color(0xFF00E676)        // Radar active pings, verified local neighbor, live pulse
val LocaliAccentMintContainer = Color(0xFFD1FAE5)
val LocaliAccentCyan = Color(0xFF00B4D8)        // Proximity beacons, street route highlights
val LocaliAccentCyanContainer = Color(0xFFE0F2FE)
val LocaliDeepNavy = Color(0xFF0B132B)          // Radar map canvas backdrop, midnight mode root

// 5. Canvas & Neutral Surfaces (Light Theme)
val LocaliBackground = Color(0xFFF8FAFC)        // Clean, airy off-white canvas
val LocaliSurface = Color(0xFFFFFFFF)           // Pure white primary surface cards
val LocaliSurfaceVariant = Color(0xFFF1F5F9)    // Elevated surface / text field fill
val LocaliSurfaceTint = Color(0xFFE2E8F0)       // Subtle tonal highlight
val LocaliOutline = Color(0xFFCBD5E1)           // Border dividers
val LocaliOutlineVariant = Color(0xFFE2E8F0)    // Subtle card contours

// 6. Canvas & Neutral Surfaces (Dark Theme / Radar Midnight)
val LocaliDarkBackground = Color(0xFF0B132B)    // Immersive radar deep navy
val LocaliDarkSurface = Color(0xFF111C38)       // Card surface in dark mode
val LocaliDarkSurfaceVariant = Color(0xFF1B2A4A)// Interactive chip & input background in dark
val LocaliDarkOutline = Color(0xFF2A3D66)       // Dark mode contour borders
val LocaliDarkOutlineVariant = Color(0xFF1F2E52)

// 7. Typography Semantic Tokens
val LocaliTextPrimary = Color(0xFF0F172A)       // Slate 900: High contrast primary text
val LocaliTextSecondary = Color(0xFF475569)     // Slate 600: Body copy & author metadata
val LocaliTextTertiary = Color(0xFF94A3B8)      // Slate 400: Time stamps & location tags
val LocaliDarkTextPrimary = Color(0xFFF8FAFC)   // Crisp white-slate text in dark mode
val LocaliDarkTextSecondary = Color(0xFF94A3B8) // Muted slate in dark mode
val LocaliDarkTextTertiary = Color(0xFF64748B)

// 8. Functional & Status Colors
val LocaliHeart = Color(0xFFFF3366)             // Vibrant rose heart for likes
val LocaliVerified = Color(0xFF00838F)          // Verified community leader badge
val LocaliError = Color(0xFFDC2626)             // Red 600: Alert / Error status
val LocaliErrorContainer = Color(0xFFFEE2E2)
val LocaliOnErrorContainer = Color(0xFF7F1D1D)

// 9. Localiiiy Brand Gradients
val LocaliStoryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFF5722), // Sunset Coral
        Color(0xFFF59E0B), // Warm Amber
        Color(0xFF00B4D8), // Electric Cyan
        Color(0xFF00838F)  // Hyperlocal Teal
    )
)

val LocaliSeenStoryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF94A3B8),
        Color(0xFFCBD5E1)
    )
)

val LocaliRadarPulseGradient = Brush.radialGradient(
    colors = listOf(
        Color(0x6600E676), // 40% Mint
        Color(0x2200B4D8), // 13% Cyan
        Color(0x0000838F)  // Transparent Teal
    )
)

val LocaliHeroHeaderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0B132B),
        Color(0xFF0F2644),
        Color(0xFF004D40)
    )
)

val LocaliCardGlowGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x1A00838F),
        Color(0x0500B4D8),
        Color(0x00000000)
    )
)

// 10. Extended Brand Colors Data Class for Composable access
@Immutable
data class LocaliBrandColors(
    val primaryTeal: Color = LocaliPrimary,
    val primaryTealLight: Color = LocaliPrimaryLight,
    val accentCoral: Color = LocaliAccentCoral,
    val accentMint: Color = LocaliAccentMint,
    val accentCyan: Color = LocaliAccentCyan,
    val sunsetAmber: Color = LocaliTertiary,
    val deepNavy: Color = LocaliDeepNavy,
    val storyGradient: Brush = LocaliStoryGradient,
    val seenStoryGradient: Brush = LocaliSeenStoryGradient,
    val radarPulseGradient: Brush = LocaliRadarPulseGradient,
    val heroHeaderGradient: Brush = LocaliHeroHeaderGradient,
    val cardGlowGradient: Brush = LocaliCardGlowGradient,
    val heartActive: Color = LocaliHeart,
    val verifiedBadge: Color = LocaliVerified
)

val LocalLocaliBrandColors = staticCompositionLocalOf { LocaliBrandColors() }

// Legacy & compatibility aliases
val EditorialPrimary = LocaliPrimary
val EditorialPrimaryLight = LocaliPrimaryLight
val EditorialPrimaryDark = LocaliPrimaryDark
val EditorialPrimaryContainer = LocaliPrimaryContainer
val EditorialOnPrimaryContainer = LocaliOnPrimaryContainer
val EditorialSecondary = LocaliSecondary
val EditorialSecondaryContainer = LocaliSecondaryContainer
val EditorialTertiary = LocaliTertiary
val EditorialTertiaryContainer = LocaliTertiaryContainer
val EditorialBackground = LocaliBackground
val EditorialSurface = LocaliSurface
val EditorialSurfaceVariant = LocaliSurfaceVariant
val EditorialSurfaceTint = LocaliSurfaceTint
val EditorialOutline = LocaliOutline
val EditorialOutlineVariant = LocaliOutlineVariant
val EditorialTextPrimary = LocaliTextPrimary
val EditorialTextSecondary = LocaliTextSecondary
val EditorialTextTertiary = LocaliTextTertiary
val EditorialHeart = LocaliHeart
val EditorialVerified = LocaliVerified
val EditorialError = LocaliError

val LocaliHeartRed = LocaliHeart




