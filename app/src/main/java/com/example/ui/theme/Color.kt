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
val LocaliiiyPrimary = Color(0xFF00838F)           // Cyan-Teal 800: Core brand identity
val LocaliiiyPrimaryLight = Color(0xFF4DD0E1)      // Aqua-Teal 300: Luminous dark-theme variant & glow
val LocaliiiyPrimaryDark = Color(0xFF004D40)       // Deep Teal 900: High-contrast button fills
val LocaliiiyPrimaryContainer = Color(0xFFE0F7FA)  // Soft cyan-teal container tint
val LocaliiiyOnPrimaryContainer = Color(0xFF00363A)// Deep viridian text on primary container
val LocaliiiyPrimaryTeal = LocaliiiyPrimary

// 2. Secondary Tonal Spectrum (Midnight Navy & Community Slate)
val LocaliiiySecondary = Color(0xFF1E293B)         // Slate 800: Grounding structural navy
val LocaliiiySecondaryLight = Color(0xFF94A3B8)    // Slate 400: Dark mode secondary
val LocaliiiySecondaryDark = Color(0xFF0F172A)     // Slate 900: Deep night headers
val LocaliiiySecondaryContainer = Color(0xFFE2E8F0)// Slate 200: Chip backgrounds
val LocaliiiyOnSecondaryContainer = Color(0xFF0F172A)

// 3. Tertiary Tonal Spectrum (Sunset Amber / Neighborhood Warmth)
val LocaliiiyTertiary = Color(0xFFF59E0B)          // Amber 500: Community warmth, story highlights
val LocaliiiyTertiaryLight = Color(0xFFFBBF24)     // Amber 400: Dark theme tertiary accent
val LocaliiiyTertiaryDark = Color(0xFFB45309)      // Amber 700: High contrast warm borders
val LocaliiiyTertiaryContainer = Color(0xFFFEF3C7) // Amber 100: Warm card backdrop
val LocaliiiyOnTertiaryContainer = Color(0xFF78350F)

// 4. Vibrant Hyperlocal Accents
val LocaliiiyAccentCoral = Color(0xFFFF5722)       // Live broadcasts, urgent community updates, pulses
val LocaliiiyAccentCoralContainer = Color(0xFFFFECE2)
val LocaliiiyAccentMint = Color(0xFF00E676)        // Radar active pings, verified local neighbor, live pulse
val LocaliiiyAccentMintContainer = Color(0xFFD1FAE5)
val LocaliiiyAccentCyan = Color(0xFF00B4D8)        // Proximity beacons, street route highlights
val LocaliiiyAccentCyanContainer = Color(0xFFE0F2FE)
val LocaliiiyDeepNavy = Color(0xFF0B132B)          // Radar map canvas backdrop, midnight mode root

// 5. Canvas & Neutral Surfaces (Light Theme)
val LocaliiiyBackground = Color(0xFFF8FAFC)        // Clean, airy off-white canvas
val LocaliiiySurface = Color(0xFFFFFFFF)           // Pure white primary surface cards
val LocaliiiySurfaceVariant = Color(0xFFF1F5F9)    // Elevated surface / text field fill
val LocaliiiySurfaceTint = Color(0xFFE2E8F0)       // Subtle tonal highlight
val LocaliiiyOutline = Color(0xFFCBD5E1)           // Border dividers
val LocaliiiyOutlineVariant = Color(0xFFE2E8F0)    // Subtle card contours

// 6. Canvas & Neutral Surfaces (Dark Theme / Radar Midnight)
val LocaliiiyDarkBackground = Color(0xFF0B132B)    // Immersive radar deep navy
val LocaliiiyDarkSurface = Color(0xFF111C38)       // Card surface in dark mode
val LocaliiiyDarkSurfaceVariant = Color(0xFF1B2A4A)// Interactive chip & input background in dark
val LocaliiiyDarkOutline = Color(0xFF2A3D66)       // Dark mode contour borders
val LocaliiiyDarkOutlineVariant = Color(0xFF1F2E52)

// 7. Typography Semantic Tokens
val LocaliiiyTextPrimary = Color(0xFF0F172A)       // Slate 900: High contrast primary text
val LocaliiiyTextSecondary = Color(0xFF475569)     // Slate 600: Body copy & author metadata
val LocaliiiyTextTertiary = Color(0xFF94A3B8)      // Slate 400: Time stamps & location tags
val LocaliiiyDarkTextPrimary = Color(0xFFF8FAFC)   // Crisp white-slate text in dark mode
val LocaliiiyDarkTextSecondary = Color(0xFF94A3B8) // Muted slate in dark mode
val LocaliiiyDarkTextTertiary = Color(0xFF64748B)

// 8. Functional & Status Colors
val LocaliiiyHeart = Color(0xFFFF3366)             // Vibrant rose heart for likes
val LocaliiiyVerified = Color(0xFF00838F)          // Verified community leader badge
val LocaliiiyError = Color(0xFFDC2626)             // Red 600: Alert / Error status
val LocaliiiyErrorContainer = Color(0xFFFEE2E2)
val LocaliiiyOnErrorContainer = Color(0xFF7F1D1D)

// 9. Localiiiy Brand Gradients
val LocaliiiyStoryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFFF5722), // Sunset Coral
        Color(0xFFF59E0B), // Warm Amber
        Color(0xFF00B4D8), // Electric Cyan
        Color(0xFF00838F)  // Hyperlocal Teal
    )
)

val LocaliiiySeenStoryGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF94A3B8),
        Color(0xFFCBD5E1)
    )
)

val LocaliiiyRadarPulseGradient = Brush.radialGradient(
    colors = listOf(
        Color(0x6600E676), // 40% Mint
        Color(0x2200B4D8), // 13% Cyan
        Color(0x0000838F)  // Transparent Teal
    )
)

val LocaliiiyHeroHeaderGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF0B132B),
        Color(0xFF0F2644),
        Color(0xFF004D40)
    )
)

val LocaliiiyCardGlowGradient = Brush.verticalGradient(
    colors = listOf(
        Color(0x1A00838F),
        Color(0x0500B4D8),
        Color(0x00000000)
    )
)

// 10. Extended Brand Colors Data Class for Composable access
@Immutable
data class LocaliiiyBrandColors(
    val primaryTeal: Color = LocaliiiyPrimary,
    val primaryTealLight: Color = LocaliiiyPrimaryLight,
    val accentCoral: Color = LocaliiiyAccentCoral,
    val accentMint: Color = LocaliiiyAccentMint,
    val accentCyan: Color = LocaliiiyAccentCyan,
    val sunsetAmber: Color = LocaliiiyTertiary,
    val deepNavy: Color = LocaliiiyDeepNavy,
    val storyGradient: Brush = LocaliiiyStoryGradient,
    val seenStoryGradient: Brush = LocaliiiySeenStoryGradient,
    val radarPulseGradient: Brush = LocaliiiyRadarPulseGradient,
    val heroHeaderGradient: Brush = LocaliiiyHeroHeaderGradient,
    val cardGlowGradient: Brush = LocaliiiyCardGlowGradient,
    val heartActive: Color = LocaliiiyHeart,
    val verifiedBadge: Color = LocaliiiyVerified
)

val LocalLocaliiiyBrandColors = staticCompositionLocalOf { LocaliiiyBrandColors() }

// Legacy & compatibility aliases
val EditorialPrimary = LocaliiiyPrimary
val EditorialPrimaryLight = LocaliiiyPrimaryLight
val EditorialPrimaryDark = LocaliiiyPrimaryDark
val EditorialPrimaryContainer = LocaliiiyPrimaryContainer
val EditorialOnPrimaryContainer = LocaliiiyOnPrimaryContainer
val EditorialSecondary = LocaliiiySecondary
val EditorialSecondaryContainer = LocaliiiySecondaryContainer
val EditorialTertiary = LocaliiiyTertiary
val EditorialTertiaryContainer = LocaliiiyTertiaryContainer
val EditorialBackground = LocaliiiyBackground
val EditorialSurface = LocaliiiySurface
val EditorialSurfaceVariant = LocaliiiySurfaceVariant
val EditorialSurfaceTint = LocaliiiySurfaceTint
val EditorialOutline = LocaliiiyOutline
val EditorialOutlineVariant = LocaliiiyOutlineVariant
val EditorialTextPrimary = LocaliiiyTextPrimary
val EditorialTextSecondary = LocaliiiyTextSecondary
val EditorialTextTertiary = LocaliiiyTextTertiary
val EditorialHeart = LocaliiiyHeart
val EditorialVerified = LocaliiiyVerified
val EditorialError = LocaliiiyError

val LocaliiiyHeartRed = LocaliiiyHeart




