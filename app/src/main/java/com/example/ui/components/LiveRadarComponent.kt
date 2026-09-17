package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.util.HapticHelper
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.radar.*
import com.example.ui.components.MasterGhostModeTooltip
import com.example.ui.components.EnergeticTooltipBox
import com.example.ui.components.EnergeticLiveSoundWave
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
    val isPremium = userProfile.isVerified
    var selectedTheme by remember { mutableStateOf(if (isPremium) RadarHudTheme.NEON_GOLD else RadarHudTheme.PHOSPHOR_GREEN) }
    var signalAuraEnabled by remember { mutableStateOf(isPremium) }
    val currentHudTheme = if (isPremium) selectedTheme else RadarHudTheme.PHOSPHOR_GREEN
    val radarColor = currentHudTheme.primaryColor
    val gridColor = currentHudTheme.gridColor
    val sweepColors = if (isPremium) {
        listOf(radarColor.copy(alpha = 0f), radarColor.copy(alpha = 0.25f), radarColor)
    } else {
        listOf(radarColor.copy(alpha = 0f), radarColor.copy(alpha = 0.35f), radarColor)
    }
    val shieldCyan = Color(0xFF00E5FF)

    val currentDistanceOption = remember(selectedRadiusKm, radarCountryName) {
        SystematicDistanceScale.findOption(selectedRadiusKm, radarCountryName)
    }
    val pulseDurationMs = currentDistanceOption.pulseDurationMs
    
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweeper")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweepAngle"
    )

    // Visual 'pulse' animation synced with user's distance range setting
    val pulseTransition = rememberInfiniteTransition(label = "RadarPulseAnimation")
    val pulse1 by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse1"
    )
    val pulse2 by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDurationMs, delayMillis = pulseDurationMs / 3, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse2"
    )
    val pulse3 by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = pulseDurationMs, delayMillis = (pulseDurationMs * 2) / 3, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse3"
    )

    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current
    val context = androidx.compose.ui.platform.LocalContext.current

    val systematicRanges = remember(radarCountryName) {
        SystematicDistanceScale.getOptions(radarCountryName)
    }

    var showRangePickerInRadar by remember { mutableStateOf(false) }

    // Section 3: Spatial Sonar & Live Radar Discovery State
    var isGhostActive by remember { mutableStateOf(hidePreciseLocationOnRadar) }
    var is3DPerspective by remember { mutableStateOf(false) }
    var isDayTheme by remember { mutableStateOf(false) }
    var isBatterySaver by remember { mutableStateOf(false) }
    var sweepSpeedMultiplier by remember { mutableStateOf(1.0f) }
    var isSoundscapePlaying by remember { mutableStateOf(false) }
    var isOfflineCached by remember { mutableStateOf(true) }
    var isProximityPingEnabled by remember { mutableStateOf(true) }
    var selectedBeaconId by remember { mutableStateOf<String?>(null) }
    var dispatchUserTarget by remember { mutableStateOf<OtherUserEntity?>(null) }

    // Sample Section 3 Landmark Geo-Portal Beacons
    val sampleGeoBeacons = remember {
        listOf(
            GeoPortalBeacon("b1", "Market Square Hub", "Civic", 0.4, 18, "432 Hz Ambient", 88, 37.7749, -122.4194),
            GeoPortalBeacon("b2", "Pier Waterfront Park", "Nature", 1.2, 34, "528 Hz Solfeggio", 94, 37.7849, -122.4094),
            GeoPortalBeacon("b3", "Arts & Sound District", "Culture", 2.1, 27, "440 Hz Pulse", 76, 37.7649, -122.4294),
            GeoPortalBeacon("b4", "Tech Innovation Plaza", "Hub", 3.5, 12, "639 Hz Drone", 65, 37.7549, -122.4394)
        )
    }

    Column(modifier = modifier.fillMaxWidth().background(if (isDayTheme) Color(0xFFF8FAFC) else Color.Black)) {
        // Section 3.0: Master Radar Power & Broadcast Controller (Exclusive to Radar Page)
        Surface(
            color = if (isLocationEnabled) Color(0xFF031405) else Color(0xFF1E1010),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(
                1.dp,
                if (isLocationEnabled) Color(0xFF00FF41).copy(alpha = 0.6f) else Color(0xFFFF5252).copy(alpha = 0.5f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .testTag("radar_master_power_card")
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
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
                        color = if (isLocationEnabled) Color(0xFF00FF41).copy(alpha = 0.2f) else Color(0xFFFF5252).copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = if (isLocationEnabled) "📡" else "🛑",
                                fontSize = 16.sp
                            )
                        }
                    }
                    Column {
                        Text(
                            text = if (isLocationEnabled) "Radar Online" else "Radar Offline",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isLocationEnabled) {
                                "Scanning within ${currentDistanceOption.fullLabel}"
                            } else {
                                "Tap to activate nearby scanner"
                            },
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
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("radar_page_power_toggle_btn")
                ) {
                    Text(
                        text = if (isLocationEnabled) "Turn OFF" else "Turn Radar ON 📡",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Section 3.2: Passive Sonar Ghost Cloak Mode Banner
        PassiveSonarGhostBanner(
            isGhostActive = isGhostActive,
            onToggleGhost = {
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                isGhostActive = !isGhostActive
                onToggleHidePreciseLocation?.invoke(isGhostActive)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        // Active Gamified Radar Visibility Perk HUD Banner
        if (activeRadarPerk != null) {
            val perkColor = Color(activeRadarPerk.glowColorHex)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                color = perkColor.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, perkColor.copy(alpha = 0.7f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(activeRadarPerk.emoji, fontSize = 20.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "ACTIVE RADAR PERK",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = perkColor,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "• ${activeRadarPerk.remainingHours}h remaining",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                        Text(
                            text = "${activeRadarPerk.name}: ${activeRadarPerk.description}",
                            fontSize = 11.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Section 3.20: Master Tactical HUD Control Bar (3D Pitch, Day/Night HUD, Battery Saver)
        Section3MasterControlBar(
            isGhostActive = isGhostActive,
            is3DPerspective = is3DPerspective,
            isDayTheme = isDayTheme,
            isBatterySaver = isBatterySaver,
            onToggleGhost = {
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                isGhostActive = !isGhostActive
                onToggleHidePreciseLocation?.invoke(isGhostActive)
            },
            onTogglePerspective = { 
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                is3DPerspective = it 
            },
            onToggleDayTheme = { 
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                isDayTheme = !isDayTheme 
            },
            onToggleBatterySaver = { 
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                isBatterySaver = !isBatterySaver 
            }
        )

        // Section 3.8 & 3.16 & 3.17: Heading, Kalman Filter & Vibrancy Telemetry Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RealTimeCompassHeadingBadge(headingDeg = sweepAngle)
            NeighborhoodVibrancyScoreBadge(score = 82)
            MovementKalmanFilterIndicator(isKalmanFiltered = true, speedKmh = 4.6f)
            LocationJitterMaskIndicator(isJitterEnabled = isGhostActive, jitterRadiusMeters = 200)
            OfflineRadarCacheBadge(isOfflineCached = isOfflineCached, onSyncCache = { isOfflineCached = !isOfflineCached })
            ProximityPingNotificationsToggle(
                isProximityPingEnabled = isProximityPingEnabled,
                onToggle = { isProximityPingEnabled = !isProximityPingEnabled }
            )
        }

        // Section 3.7: Unified Range Slider (Premium Gated)
        UnifiedRadarRangeSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            isPremium = userProfile.isVerified
        )

        // Premium HUD Themes & Signal Aura Selector Bar with Energetic Motion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "HUD THEME:",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = radarColor,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                EnergeticLiveSoundWave(color = radarColor, modifier = Modifier.padding(end = 4.dp))
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
                            HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            if (isLocked) {
                                android.widget.Toast.makeText(context, "Premium Feature: Upgrade to unlock Cyberpunk Gold, Electric Cyan & more 🔒", android.widget.Toast.LENGTH_SHORT).show()
                            } else {
                                selectedTheme = theme
                            }
                        }
                    ) {
                        Text(
                            text = if (isLocked) "${theme.title.take(3)}🔒" else theme.title.take(4),
                            fontSize = 8.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                            color = if (isSelected) theme.primaryColor else if (isLocked) Color.Gray else Color.LightGray,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Signal Aura Toggle with Hover & Tap Tooltip
            EnergeticTooltipBox(
                title = "Signal Aura (Boost)",
                description = "Expands your beacon visual presence by +25% across neighbor radars with dynamic glowing gold sweep rings for maximum discovery.",
                accentColor = Color(0xFFFFD700)
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (signalAuraEnabled) Color(0xFFFFD700).copy(alpha = 0.2f) else Color.Transparent,
                    border = BorderStroke(0.8.dp, if (signalAuraEnabled) Color(0xFFFFD700) else Color.Gray),
                    modifier = Modifier.clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        if (!isPremium) {
                            android.widget.Toast.makeText(context, "Premium Feature: Signal Aura Glow (+25%) 🔒", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            signalAuraEnabled = !signalAuraEnabled
                        }
                    }
                ) {
                    Text(
                        text = if (signalAuraEnabled) "AURA +25% ✨ ⓘ" else "AURA OFF ⓘ",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (signalAuraEnabled) Color(0xFFFFD700) else Color.Gray,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Section 3.10 & 3.13: Safe Haven & Event Geofence Indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            EmergencySafeHavenMarker(
                havenName = "Central Clinic",
                distanceKm = 0.8,
                onClick = {}
            )
            EventGeoFenceOverlayTag(
                eventName = "Block Party",
                radiusMeters = 300,
                attendeesCount = 42
            )
            RadarSweepVelocityController(
                sweepSpeedMultiplier = sweepSpeedMultiplier,
                onSpeedChange = { sweepSpeedMultiplier = it }
            )
        }
        // Radar Privacy & Range Quick Bar with Master Ghost Mode Tooltip
        MasterGhostModeTooltip(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
            Surface(
                color = Color(0xFF031405),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (hidePreciseLocationOnRadar) shieldCyan.copy(alpha = 0.5f) else Color(0xFF005511)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (hidePreciseLocationOnRadar) shieldCyan.copy(alpha = 0.2f) else Color(0xFF00FF41).copy(alpha = 0.2f),
                                modifier = Modifier.size(26.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = if (hidePreciseLocationOnRadar) "🛡️" else "📡",
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = if (hidePreciseLocationOnRadar) "GHOST MODE: PRECISE PIN HIDDEN ⓘ" else "LIVE RADAR: PRECISE COORDINATES ⓘ",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hidePreciseLocationOnRadar) shieldCyan else Color(0xFF00FF41),
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                Text(
                                    text = if (hidePreciseLocationOnRadar) {
                                        "Simulated as $radarObfuscatedRange far out (Hover for details)"
                                    } else {
                                        "Your exact street blip is broadcasting (Hover for details)"
                                    },
                                    fontSize = 10.sp,
                                    color = Color.LightGray
                                )
                            }
                        }

                        // Quick Toggle Button
                        if (onToggleHidePreciseLocation != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = if (hidePreciseLocationOnRadar) shieldCyan else Color(0xFF1B2E1D),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onToggleHidePreciseLocation(!hidePreciseLocationOnRadar) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (hidePreciseLocationOnRadar) "Hidden 🛡️" else "Hide Pin",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (hidePreciseLocationOnRadar) Color.Black else Color.White
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFF1B2E1D),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { onOpenPrivacySettings() }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Settings ⚙️",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
        // Radar Screen with Dynamic Touch Pinch-to-Zoom
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val radarDiameterDp = minOf(maxWidth, 340.dp)
            val density = androidx.compose.ui.platform.LocalDensity.current
            val radarRadiusPx = with(density) { (radarDiameterDp / 2).toPx() }
            val centerPx = radarRadiusPx
            val bezelThicknessPx = with(density) { 24.dp.toPx() }
            val sweepRadiusPx = radarRadiusPx - bezelThicknessPx

            val combinedCount = minOf(nearbyUsers.size + nearbyPosts.size, 10)
            // Section 3.4 Live Status Visibility Gate: Blips cleared when offline or ghost mode active
            val combinedItems = remember(nearbyUsers, nearbyPosts, isLocationEnabled, isGhostActive) {
                if (!isLocationEnabled || isGhostActive) emptyList()
                else (nearbyUsers + nearbyPosts).take(combinedCount)
            }

            // Radar circular display strictly clipped to CircleShape with touch gesture zoom
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
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            }
                        )
                    }
            ) {
                // 1. Canvas with outer bezel compass, concentric rings, crosshairs, and rotating sweep
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val outerRimRadius = size.minDimension / 2
                    val sweepRadius = outerRimRadius - 22.dp.toPx()

                    // Outer Bezel Rim Track
                    drawCircle(
                        color = Color(0xFF020E04),
                        radius = outerRimRadius,
                        center = center
                    )
                    drawCircle(
                        color = radarColor.copy(alpha = 0.5f),
                        radius = outerRimRadius - 1.dp.toPx(),
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )

                    // Inner Sweep Circle Border
                    drawCircle(
                        color = radarColor.copy(alpha = 0.85f),
                        radius = sweepRadius,
                        center = center,
                        style = Stroke(width = 1.8.dp.toPx())
                    )

                    // North Glowing Chevron in Bezel
                    val northChevron = Path().apply {
                        moveTo(center.x, center.y - outerRimRadius + 2.dp.toPx())
                        lineTo(center.x - 4.dp.toPx(), center.y - outerRimRadius + 8.dp.toPx())
                        lineTo(center.x + 4.dp.toPx(), center.y - outerRimRadius + 8.dp.toPx())
                        close()
                    }
                    drawPath(northChevron, color = radarColor)

                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor(
                            if (isPremium) "#FFD700" else "#00FF41"
                        )
                        textSize = 8.5.dp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.MONOSPACE
                        isFakeBoldText = true
                        isAntiAlias = true
                        alpha = 235
                    }

                    // Cardinal Coordinates strictly in Outer Bezel
                    drawContext.canvas.nativeCanvas.drawText("N 000°", center.x, center.y - sweepRadius - 5.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("S 180°", center.x, center.y + sweepRadius + 14.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("W 270°", center.x - sweepRadius - 11.dp.toPx(), center.y + 3.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("E 090°", center.x + sweepRadius + 11.dp.toPx(), center.y + 3.dp.toPx(), textPaint)

                    // Azimuth Degree Ticks around the outer rim
                    for (deg in 0 until 360 step 30) {
                        if (deg % 90 == 0) continue
                        val rad = Math.toRadians(deg.toDouble())
                        val tickStart = outerRimRadius - 5.dp.toPx()
                        val tickEnd = outerRimRadius - 2.dp.toPx()
                        val startX = center.x + tickStart * cos(rad).toFloat()
                        val startY = center.y + tickStart * sin(rad).toFloat()
                        val endX = center.x + tickEnd * cos(rad).toFloat()
                        val endY = center.y + tickEnd * sin(rad).toFloat()
                        drawLine(
                            color = radarColor.copy(alpha = 0.4f),
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Polar Acoustic Radial Spokes (inside sweep circle)
                    for (deg in 0 until 360 step 30) {
                        val rad = Math.toRadians(deg.toDouble())
                        val spokeEndX = center.x + sweepRadius * cos(rad).toFloat()
                        val spokeEndY = center.y + sweepRadius * sin(rad).toFloat()
                        drawLine(
                            color = radarColor.copy(alpha = 0.09f),
                            start = center,
                            end = Offset(spokeEndX, spokeEndY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Concentric Acoustic Range Rings synced to selectedRadiusKm
                    val acousticRings = listOf(0.20f, 0.40f, 0.60f, 0.80f, 1.0f)
                    val currentMaxKm = selectedRadiusKm
                    acousticRings.forEachIndexed { idx, frac ->
                        val r = sweepRadius * frac
                        drawCircle(
                            color = radarColor.copy(alpha = 0.25f + (idx * 0.08f)),
                            radius = r,
                            center = center,
                            style = Stroke(width = if (idx == acousticRings.lastIndex) 1.5.dp.toPx() else 1.dp.toPx())
                        )

                        // Scaled distance annotations on range rings
                        val km = currentMaxKm * frac
                        val label = when {
                            km >= 1000 -> String.format("%.0fK", km / 1000)
                            km >= 10 -> String.format("%.0fk", km)
                            else -> String.format("%.1fk", km)
                        }
                        val ringDistPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.parseColor(
                                if (isPremium) "#FFD700" else "#00FF41"
                            )
                            textSize = 7.5.dp.toPx()
                            textAlign = android.graphics.Paint.Align.LEFT
                            typeface = android.graphics.Typeface.MONOSPACE
                            isAntiAlias = true
                            alpha = 195
                        }
                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            center.x + 3.dp.toPx(),
                            center.y - r - 2.dp.toPx(),
                            ringDistPaint
                        )
                    }

                    // Dynamic Pulse Shockwaves (Synced with range)
                    val pulseColor = if (hidePreciseLocationOnRadar) shieldCyan else radarColor
                    val pulseProgressList = listOf(pulse1, pulse2, pulse3)
                    for (progress in pulseProgressList) {
                        if (progress > 0.01f) {
                            val waveRadius = sweepRadius * progress
                            val fadeAlpha = ((1f - progress) * (if (hidePreciseLocationOnRadar) 0.65f else 0.80f)).coerceIn(0f, 1f)
                            drawCircle(
                                color = pulseColor.copy(alpha = fadeAlpha * 0.45f),
                                radius = waveRadius,
                                center = center,
                                style = Stroke(width = 1.5.dp.toPx())
                            )
                        }
                    }

                    // Crosshairs
                    drawLine(radarColor.copy(alpha = 0.12f), start = Offset(center.x, center.y - sweepRadius), end = Offset(center.x, center.y + sweepRadius), strokeWidth = 1.dp.toPx())
                    drawLine(radarColor.copy(alpha = 0.12f), start = Offset(center.x - sweepRadius, center.y), end = Offset(center.x + sweepRadius, center.y), strokeWidth = 1.dp.toPx())

                    // Sweep Beam
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.Transparent, radarColor.copy(alpha = 0.08f), radarColor.copy(alpha = 0.65f)),
                            center = center
                        ),
                        startAngle = sweepAngle - 90f,
                        sweepAngle = 90f,
                        useCenter = true,
                        topLeft = Offset(center.x - sweepRadius, center.y - sweepRadius),
                        size = Size(sweepRadius * 2, sweepRadius * 2)
                    )
                    drawLine(
                        color = radarColor,
                        start = center,
                        end = Offset(
                            x = center.x + sweepRadius * cos(Math.toRadians(sweepAngle.toDouble())).toFloat(),
                            y = center.y + sweepRadius * sin(Math.toRadians(sweepAngle.toDouble())).toFloat()
                        ),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Signal Aura (Center User Pulse for Premium)
                    if (signalAuraEnabled) {
                        drawCircle(
                            color = Color(0xFFFFD700).copy(alpha = 0.28f * (1f - pulse1)),
                            radius = (16.dp.toPx() + 10.dp.toPx() * (1f - pulse1)),
                            center = center
                        )
                    }

                    // Center Pulse Beacon Ping
                    val centerPulseGlow = ((1f - pulse1) * 0.45f).coerceAtLeast(0f)
                    drawCircle(
                        color = pulseColor.copy(alpha = centerPulseGlow),
                        radius = (12.dp.toPx() + 6.dp.toPx() * (1f - pulse1)),
                        center = center
                    )

                    // Center Blip
                    if (hidePreciseLocationOnRadar) {
                        drawCircle(color = shieldCyan.copy(alpha = 0.35f), radius = 10.dp.toPx(), center = center)
                        drawCircle(color = shieldCyan, radius = 5.dp.toPx(), center = center)
                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = center)
                    } else {
                        if (isPremium) {
                            drawCircle(color = Color(0xFFFFD700), radius = 6.dp.toPx(), center = center)
                            drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = center)
                        } else {
                            drawCircle(color = radarColor, radius = 5.dp.toPx(), center = center)
                            drawCircle(color = Color.White, radius = 2.dp.toPx(), center = center)
                        }
                    }
                }

                // Dynamic Radar Scale Calibration Pill (Relocated to BottomCenter to avoid colliding with North)
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(100.dp),
                    border = BorderStroke(0.8.dp, if (hidePreciseLocationOnRadar) shieldCyan.copy(alpha = 0.6f) else radarColor.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .testTag("radar_pulse_sync_badge")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(if (hidePreciseLocationOnRadar) shieldCyan else radarColor)
                        )
                        Text(
                            text = "PULSE SYNC: ${currentDistanceOption.shortLabel} • ${(pulseDurationMs / 1000.0)}s",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                    }
                }
                
                // 2. Miniature Cards plotted strictly INSIDE the sweep circle
                val blipCardSizeDp = 34.dp
                val blipCardRadiusPx = with(density) { 17.dp.toPx() }
                val maxDist = sweepRadiusPx - blipCardRadiusPx - with(density) { 4.dp.toPx() }
                val minDist = blipCardRadiusPx + with(density) { 10.dp.toPx() }

                combinedItems.forEachIndexed { i, item ->
                    val angle = (i * 137.5f) % 360f
                    val distFactor = 0.18f + 0.78f * (((i * 29 + 17) % 100) / 100f)
                    val dist = minDist + (maxDist - minDist) * distFactor
                    
                    val blipCenterX = centerPx + dist * cos(Math.toRadians(angle.toDouble())).toFloat()
                    val blipCenterY = centerPx + dist * sin(Math.toRadians(angle.toDouble())).toFloat()
                    
                    val offsetX = blipCenterX - blipCardRadiusPx
                    val offsetY = blipCenterY - blipCardRadiusPx
                    
                    // Calculate opacity and highlight based on radar sweep
                    val angleDiff = (sweepAngle - angle + 360f) % 360f
                    val isSwept = angleDiff < 45f
                    val alpha = if (isSwept) 1f else (0.45f + 0.25f * (1f - (angleDiff / 360f)))
                    val scale = if (isSwept) 1.15f else 1f
                    val isPremiumBlip = (item is OtherUserEntity && item.isVerified) || (item is PostEntity && item.isVerified)
                    val actualBorderGlow = if (isPremiumBlip) Color(0xFFFFD700) else (if (isSwept) radarColor else radarColor.copy(alpha = 0.4f))
                    val borderSize = if (isPremiumBlip) 2.dp else 1.5.dp
                    
                    val avatarUrl = when (item) {
                        is OtherUserEntity -> item.avatarUrl
                        is PostEntity -> item.userAvatar
                        else -> ""
                    }
                    
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(
                                x = with(density) { offsetX.toDp() },
                                y = with(density) { offsetY.toDp() }
                            )
                            .size(if (isPremiumBlip) blipCardSizeDp + 6.dp else blipCardSizeDp)
                            .let {
                                if (isPremiumBlip && signalAuraEnabled) {
                                    it.background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0xFFFFD700).copy(alpha = 0.45f), Color.Transparent)
                                        ),
                                        CircleShape
                                    )
                                } else it
                            }
                            .clip(CircleShape)
                            .background(Color(0xFF031405))
                            .border(
                                borderSize,
                                if (isPremiumBlip) Brush.sweepGradient(listOf(Color(0xFFFFD700), Color(0xFFFFF8DC), Color(0xFFDAA520), Color(0xFFFFD700)))
                                else Brush.linearGradient(listOf(actualBorderGlow, actualBorderGlow)),
                                CircleShape
                            )
                            .clickable {
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                if (item is OtherUserEntity) {
                                    dispatchUserTarget = item
                                    if (isPremium) {
                                        onUserClick(item)
                                    }
                                } else if (item is PostEntity) {
                                    onPostClick(item)
                                }
                            }
                            .padding(2.dp)
                    ) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Radar blip",
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            alpha = alpha
                        )
                    }
                }
            }
        }

        // Section 3.15: Direct Tactical Chat Dispatch Sheet if Target Blip clicked
        val currentTarget = dispatchUserTarget
        AnimatedVisibility(
            visible = currentTarget != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            if (currentTarget != null) {
                DirectRadarChatDispatchSheet(
                    targetUser = currentTarget,
                    isPremiumViewer = isPremium,
                    onSendQuickGreeting = { _ -> dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}
