package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.sp

private val CompactPlatformTextStyle = PlatformTextStyle(includeFontPadding = false)
private val CompactLineHeightStyle = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.Both
)

// 1. Core Material 3 Typography Scale (Tightened 1st-to-2nd line vertical gap)
val LocaliiiyTypography = Typography(
    // Hero & Radar Display Titles
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        lineHeight = 35.sp,
        letterSpacing = (-0.75).sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 31.sp,
        letterSpacing = (-0.5).sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 27.sp,
        letterSpacing = (-0.25).sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),

    // Section & Pulse Card Headlines
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 25.sp,
        letterSpacing = (-0.2).sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 23.sp,
        letterSpacing = 0.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 20.5.sp,
        letterSpacing = 0.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),

    // Card Titles & User Screen Names
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 19.5.sp,
        letterSpacing = 0.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 17.5.sp,
        letterSpacing = 0.15.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.1.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),

    // Body Copy & Hyperlocal Story Captions
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 17.8.sp,
        letterSpacing = 0.2.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 16.5.sp,
        letterSpacing = 0.15.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 14.2.sp,
        letterSpacing = 0.2.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),

    // Chips, Live Radar Pills, Distances & Badges
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.35.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 13.8.sp,
        letterSpacing = 0.4.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 11.8.sp,
        letterSpacing = 0.6.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    )
)

// Legacy alias compatibility
val Typography = LocaliiiyTypography

// 2. Custom Localiiiy Brand Typography Extensions
@Immutable
data class LocaliiiyBrandTypography(
    val radarDistance: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 13.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.5.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    val liveStatusBadge: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Black,
        fontSize = 10.sp,
        lineHeight = 11.5.sp,
        letterSpacing = 1.0.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    val creatorHandle: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 15.sp,
        letterSpacing = 0.1.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    val locationStamp: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.25.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    ),
    val pulseMetric: TextStyle = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp,
        platformStyle = CompactPlatformTextStyle,
        lineHeightStyle = CompactLineHeightStyle
    )
)

val LocalLocaliiiyBrandTypography = staticCompositionLocalOf { LocaliiiyBrandTypography() }


