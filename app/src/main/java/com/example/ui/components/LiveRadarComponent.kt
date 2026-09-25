package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import coil.compose.AsyncImage
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.radar.*
import com.example.util.HapticHelper
import kotlin.math.cos
import kotlin.math.sin

data class RadarRange(val label: String, val valueKm: Double)

@Composable
fun LiveRadarComponent(
    isRefreshing: Boolean = false,
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity>,
    nearbyPosts: List<PostEntity>,
    nearbyClips: List<ClipEntity> = emptyList(),
    nearbyMarketItems: List<MarketplaceItemEntity> = emptyList(),
    selectedRadiusKm: Double = 3.0,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    hidePreciseLocationOnRadar: Boolean = false,
    radarObfuscatedRange: String = "3k",
    radarCountryName: String = "United States",
    isPremiumSubscribed: Boolean = false,
    onToggleHidePreciseLocation: ((Boolean) -> Unit)? = null,
    onSelectObfuscatedRange: ((String) -> Unit)? = null,
    onRadiusChange: (Double) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onUserClick: (OtherUserEntity) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    onWaveAtUser: (OtherUserEntity) -> Unit = {},
    activeRadarPerk: com.example.data.RadarVisibilityPerk? = null,
    modifier: Modifier = Modifier
) {
    val isPremium = isPremiumSubscribed || userProfile.isVerified
    var selectedTheme by remember { mutableStateOf(if (isPremium) RadarHudTheme.NEON_GOLD else RadarHudTheme.PHOSPHOR_GREEN) }
    var signalAuraEnabled by remember { mutableStateOf(isPremium) }
    val currentHudTheme = if (isPremium) selectedTheme else RadarHudTheme.PHOSPHOR_GREEN
    val radarColor = currentHudTheme.primaryColor
    val gridColor = currentHudTheme.gridColor

    val currentDistanceOption = remember(selectedRadiusKm, radarCountryName) {
        SystematicDistanceScale.findOption(selectedRadiusKm, radarCountryName)
    }
    val pulseDurationMs = currentDistanceOption.pulseDurationMs

    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweeper")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    // Pulse expanding animation
    val pulseTransition = rememberInfiniteTransition(label = "RadarPulseAnimation")
    val pulseWave by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulseWave"
    )

    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    var isGhostActive by remember { mutableStateOf(hidePreciseLocationOnRadar) }
    var is3DPerspective by remember { mutableStateOf(false) }
    var isDayTheme by remember { mutableStateOf(false) }
    var isBatterySaver by remember { mutableStateOf(false) }
    var dispatchUserTarget by remember { mutableStateOf<OtherUserEntity?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        // =====================================================================
        // 1. CIRCULAR RADAR SCREEN (PLACED IMMEDIATELY AT TOP BELOW SEARCH BAR)
        // =====================================================================
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            val radarDiameterDp = minOf(maxWidth - 12.dp, 330.dp)
            val density = LocalDensity.current
            val radarRadiusPx = with(density) { (radarDiameterDp / 2).toPx() }
            val centerPx = radarRadiusPx
            val bezelThicknessPx = with(density) { 22.dp.toPx() }
            val sweepRadiusPx = radarRadiusPx - bezelThicknessPx

            val combinedCount = minOf(nearbyUsers.size + nearbyPosts.size, 12)
            val combinedItems = remember(nearbyUsers, nearbyPosts, isLocationEnabled, isGhostActive) {
                if (!isLocationEnabled || isGhostActive) emptyList()
                else (nearbyUsers + nearbyPosts).take(combinedCount)
            }

            Box(
                modifier = Modifier
                    .size(radarDiameterDp)
                    .clip(CircleShape)
                    .background(Color(0xFF020E04))
                    .border(2.5.dp, Brush.radialGradient(listOf(radarColor, gridColor, Color(0xFF001A06))), CircleShape)
                    .pointerInput(selectedRadiusKm) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (zoom != 1f) {
                                val maxAllowed = if (isPremium) 20000.0 else 50.0
                                val nextRadius = (selectedRadiusKm / zoom).coerceIn(1.0, maxAllowed)
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                                onRadiusChange(nextRadius)
                            }
                        }
                    }
                    .testTag("live_radar_circular_display")
            ) {
                // Background Radar Grid, Rings, Compass and Sweep Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val outerRimRadius = size.minDimension / 2
                    val sweepRadius = outerRimRadius - 20.dp.toPx()

                    // Outer Bezel Rim
                    drawCircle(color = Color(0xFF020E04), radius = outerRimRadius, center = center)
                    drawCircle(
                        color = radarColor.copy(alpha = 0.5f),
                        radius = outerRimRadius - 1.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Inner Sweep Border Ring
                    drawCircle(
                        color = radarColor.copy(alpha = 0.85f),
                        radius = sweepRadius,
                        center = center,
                        style = Stroke(width = 1.8.dp.toPx())
                    )

                    // Compass North Chevron and Text Paints
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor(if (isPremium) "#FFD700" else "#00FF41")
                        textSize = 8.5.dp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.MONOSPACE
                        isFakeBoldText = true
                        isAntiAlias = true
                        alpha = 240
                    }

                    // Cardinal Coordinates & Azimuth Ticks
                    // Top: ▲ N 000°
                    drawContext.canvas.nativeCanvas.drawText("▲", center.x, center.y - sweepRadius - 10.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("N 000°", center.x, center.y - sweepRadius - 2.dp.toPx(), textPaint)

                    // Bottom: S 180°
                    drawContext.canvas.nativeCanvas.drawText("S 180°", center.x, center.y + sweepRadius + 14.dp.toPx(), textPaint)

                    // Left: W 270°
                    val sideTextPaint = android.graphics.Paint(textPaint).apply { textSize = 7.5.dp.toPx() }
                    drawContext.canvas.nativeCanvas.drawText("W 270°", center.x - sweepRadius - 10.dp.toPx(), center.y + 3.dp.toPx(), sideTextPaint)

                    // Right: E 090°
                    drawContext.canvas.nativeCanvas.drawText("E 090°", center.x + sweepRadius + 10.dp.toPx(), center.y + 3.dp.toPx(), sideTextPaint)

                    // Polar Acoustic Radial Spokes (45° intervals)
                    for (deg in 0 until 360 step 45) {
                        val rad = Math.toRadians(deg.toDouble())
                        val spokeEndX = center.x + sweepRadius * cos(rad).toFloat()
                        val spokeEndY = center.y + sweepRadius * sin(rad).toFloat()
                        drawLine(
                            color = radarColor.copy(alpha = 0.12f),
                            start = center,
                            end = Offset(spokeEndX, spokeEndY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // 5 Concentric Range Rings with distance labels (0.2, 0.4, 0.6, 0.8, 1.0)
                    val ringFractions = listOf(0.2f, 0.4f, 0.6f, 0.8f, 1.0f)
                    val currentMaxKm = selectedRadiusKm
                    ringFractions.forEach { frac ->
                        val r = sweepRadius * frac
                        drawCircle(
                            color = radarColor.copy(alpha = 0.22f),
                            radius = r,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // Distance label along the north vertical axis (0.6k, 1.2k, 1.8k, 2.4k, 3.0k)
                        val km = currentMaxKm * frac
                        val label = when {
                            km >= 1000 -> String.format("%.0fK", km / 1000)
                            km >= 10 -> String.format("%.0fk", km)
                            else -> String.format("%.1fk", km)
                        }
                        val ringDistPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor(if (isPremium) "#FFD700" else "#00FF41")
                            textSize = 7.dp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.MONOSPACE
                            isAntiAlias = true
                            alpha = 200
                        }
                        drawContext.canvas.nativeCanvas.drawText(label, center.x, center.y - r + 8.dp.toPx(), ringDistPaint)
                    }

                    // Expanding Pulse Shockwave
                    drawCircle(
                        color = radarColor.copy(alpha = 0.35f * (1f - pulseWave)),
                        radius = sweepRadius * pulseWave,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Rotating Radar Sweep Line and Wedge Gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                Color.Transparent,
                                radarColor.copy(alpha = 0.05f),
                                radarColor.copy(alpha = 0.25f),
                                radarColor.copy(alpha = 0.70f)
                            ),
                            center = center
                        ),
                        startAngle = sweepAngle - 90f,
                        sweepAngle = 90f,
                        useCenter = true,
                        topLeft = Offset(center.x - sweepRadius, center.y - sweepRadius),
                        size = Size(sweepRadius * 2, sweepRadius * 2)
                    )

                    // Leading Sweep Line
                    val sweepRad = Math.toRadians((sweepAngle).toDouble())
                    val lineEndX = center.x + sweepRadius * cos(sweepRad).toFloat()
                    val lineEndY = center.y + sweepRadius * sin(sweepRad).toFloat()
                    drawLine(
                        color = if (isPremium) Color(0xFFFFD700) else Color(0xFF00FF41),
                        start = center,
                        end = Offset(lineEndX, lineEndY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Center Blip Beacon & Glow
                    drawCircle(
                        color = radarColor.copy(alpha = 0.3f),
                        radius = 12.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = if (isPremium) Color(0xFFFFD700) else radarColor,
                        radius = 4.5.dp.toPx(),
                        center = center
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 1.8.dp.toPx(),
                        center = center
                    )
                }

                // Plotting User & Post Blip Avatars inside the circular radar
                val blipSizeDp = 32.dp
                val blipRadiusPx = with(density) { 16.dp.toPx() }
                val maxBlipDist = sweepRadiusPx - blipRadiusPx - with(density) { 6.dp.toPx() }

                combinedItems.forEachIndexed { i, item ->
                    val angle = (i * 137.5f + 25f) % 360f
                    val distFactor = 0.22f + 0.68f * (((i * 37 + 19) % 100) / 100f)
                    val dist = blipRadiusPx + maxBlipDist * distFactor
                    val bx = centerPx + (dist * cos(Math.toRadians(angle.toDouble()))).toFloat()
                    val by = centerPx + (dist * sin(Math.toRadians(angle.toDouble()))).toFloat()

                    val avatarUrl = if (item is OtherUserEntity) item.avatarUrl else (item as PostEntity).userAvatar

                    Box(
                        modifier = Modifier
                            .offset(
                                x = with(density) { (bx - blipRadiusPx).toDp() },
                                y = with(density) { (by - blipRadiusPx).toDp() }
                            )
                            .size(blipSizeDp)
                            .clip(CircleShape)
                            .background(Color(0xFF021405))
                            .border(1.5.dp, if (item is OtherUserEntity && item.isVerified) Color(0xFFFFD700) else radarColor, CircleShape)
                            .clickable {
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                if (item is OtherUserEntity) {
                                    dispatchUserTarget = item
                                    onUserClick(item)
                                } else {
                                    onPostClick(item as PostEntity)
                                }
                            }
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Bottom Sync Badge inside Radar: • PULSE SYNC: 3KM • 1.2s
                Surface(
                    color = Color.Black.copy(alpha = 0.85f),
                    shape = RoundedCornerShape(100.dp),
                    border = BorderStroke(0.8.dp, radarColor.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isLocationEnabled) Color(0xFF00FF41) else Color.Red)
                        )
                        Text(
                            text = "PULSE SYNC: ${currentDistanceOption.shortLabel.uppercase()} • 1.2s",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // =====================================================================
        // 2. ALL CONTROLS POSITIONED STRICTLY BELOW THE RADAR SCREEN
        // =====================================================================

        // 1. Radar Online Card
        Surface(
            color = if (isLocationEnabled) Color(0xFF031405) else Color(0xFF1E1010),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                if (isLocationEnabled) Color(0xFF00FF41).copy(alpha = 0.5f) else Color(0xFFFF5252).copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp)
                .testTag("radar_online_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = if (isLocationEnabled) Color(0xFF0B2E10) else Color(0xFF3B1212),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isLocationEnabled) "Radar Online" else "Radar Offline",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252)
                        )
                        Text(
                            text = if (isLocationEnabled) "Scanning within ${currentDistanceOption.fullLabel}" else "Location radar disabled",
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }

                Button(
                    onClick = {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        onLocationToggle(!isLocationEnabled)
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLocationEnabled) Color(0xFF1B2E1D) else Color(0xFF00FF41),
                        contentColor = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFF020E04)
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).testTag("radar_power_toggle_button")
                ) {
                    Text(
                        text = if (isLocationEnabled) "Turn OFF" else "Turn ON",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // 2. RADAR BEACON: BROADCASTING (PassiveSonarGhostBanner)
        PassiveSonarGhostBanner(
            isGhostActive = isGhostActive,
            onToggleGhost = {
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                isGhostActive = !isGhostActive
                onToggleHidePreciseLocation?.invoke(isGhostActive)
            },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
        )

        // 3. Active Radar Perk Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF021B2B),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp)
                .testTag("active_radar_perk_card")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Active Perk",
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "ACTIVE RADAR PERK • 19h remaining",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Signal Aura Glow (+25%): Brightens your proximity",
                        fontSize = 9.5.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // 4. Master Control Pills Row (2D TOP-DOWN, NIGHT HUD, LIVE SCAN)
        Section3MasterControlBar(
            isGhostActive = isGhostActive,
            is3DPerspective = is3DPerspective,
            isDayTheme = isDayTheme,
            isBatterySaver = isBatterySaver,
            onToggleGhost = {
                isGhostActive = !isGhostActive
                onToggleHidePreciseLocation?.invoke(isGhostActive)
            },
            onTogglePerspective = { is3DPerspective = it },
            onToggleDayTheme = { isDayTheme = !isDayTheme },
            onToggleBatterySaver = { isBatterySaver = !isBatterySaver }
        )

        // 5. Telemetry Row (Heading, Vibrancy, Kalman Filter)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RealTimeCompassHeadingBadge(headingDeg = 86f)
            NeighborhoodVibrancyScoreBadge(score = 82)
            MovementKalmanFilterIndicator(isKalmanFiltered = true, speedKmh = 4.8f)
        }

        // 6. Unified Range Slider & Presets
        UnifiedRadarRangeSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            isPremium = userProfile.isVerified
        )

        // 7. HUD Themes & Signal Aura Selector Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "HUD THEME: .....",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = radarColor,
                    fontFamily = FontFamily.Monospace
                )
                RadarHudTheme.values().forEach { theme ->
                    val isSelected = currentHudTheme == theme
                    val isLocked = theme.isPremiumOnly && !isPremium
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) theme.primaryColor.copy(alpha = 0.25f) else Color.Transparent,
                        border = BorderStroke(
                            0.7.dp,
                            if (isSelected) theme.primaryColor else if (isLocked) Color.DarkGray else Color.Gray.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.clickable {
                            if (!isLocked) {
                                selectedTheme = theme
                            } else {
                                Toast.makeText(context, "Premium Theme: ${theme.title} 🔒", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text(
                            text = if (isLocked) "${theme.title.take(3)}🔒" else theme.title.take(4),
                            fontSize = 8.sp,
                            color = if (isSelected) theme.primaryColor else Color.Gray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = if (signalAuraEnabled) Color(0xFFFFD700).copy(alpha = 0.2f) else Color.Transparent,
                border = BorderStroke(0.8.dp, if (signalAuraEnabled) Color(0xFFFFD700) else Color.Gray),
                modifier = Modifier.clickable {
                    if (isPremium) {
                        signalAuraEnabled = !signalAuraEnabled
                    } else {
                        Toast.makeText(context, "Premium Feature: Signal Aura 🔒", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(
                    text = if (signalAuraEnabled) "AURA ✨" else "AURA OFF ⓘ",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (signalAuraEnabled) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // 8. Safe Haven & Event Geofence Markers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EmergencySafeHavenMarker(havenName = "Central Clinic", distanceKm = 0.8, onClick = {})
            EventGeoFenceOverlayTag(eventName = "Block Party", radiusMeters = 300, attendeesCount = 42)
        }

        // 9. LIVE RADAR: PRECISE COORDINATES Card (Hide Pin)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF031808),
            border = BorderStroke(1.dp, Color(0xFF00FF41).copy(alpha = 0.45f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .testTag("precise_coordinates_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0A2B11),
                        modifier = Modifier.size(30.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.SatelliteAlt,
                                contentDescription = null,
                                tint = Color(0xFF00FF41),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "LIVE RADAR: PRECISE COORDINATES ⓘ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00FF41),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Your exact street blip is broadcasting (Hover for details)",
                            fontSize = 9.5.sp,
                            color = Color.LightGray
                        )
                    }
                }

                Button(
                    onClick = {
                        isGhostActive = !isGhostActive
                        onToggleHidePreciseLocation?.invoke(isGhostActive)
                    },
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isGhostActive) Color(0xFF00E5FF) else Color(0xFF0F2615),
                        contentColor = if (isGhostActive) Color.Black else Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = if (isGhostActive) "Hidden 🛡️" else "Hide Pin",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Direct Radar Chat Dispatch sheet (when a blip is tapped)
        val currentTarget = dispatchUserTarget
        AnimatedVisibility(visible = currentTarget != null) {
            if (currentTarget != null) {
                DirectRadarChatDispatchSheet(
                    targetUser = currentTarget,
                    isPremiumViewer = isPremium,
                    onSendQuickGreeting = { dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}
