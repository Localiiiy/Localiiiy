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
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.radar.*
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
    val radarColor = Color(0xFF00FF41)
    val gridColor = Color(0xFF005511)
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
                            text = if (isLocationEnabled) "LIVE RADAR ACTIVE" else "RADAR INACTIVE (OFF-GRID)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isLocationEnabled) Color(0xFF00FF41) else Color(0xFFFF5252),
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = if (isLocationEnabled) {
                                "Scanning signals & neighbors within ${currentDistanceOption.fullLabel}"
                            } else {
                                "Radar is OFF • Tap to activate nearby signal scanner"
                            },
                            fontSize = 10.sp,
                            color = Color.LightGray
                        )
                    }
                }

                Button(
                    onClick = { onLocationToggle(!isLocationEnabled) },
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
                isGhostActive = !isGhostActive
                onToggleHidePreciseLocation?.invoke(isGhostActive)
            },
            onTogglePerspective = { is3DPerspective = it },
            onToggleDayTheme = { isDayTheme = !isDayTheme },
            onToggleBatterySaver = { isBatterySaver = !isBatterySaver }
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

        // Section 3.7: Radar Scale Zoom & Increase / Decrease Controller
        RadarScaleZoomSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { onRadiusChange(it) }
        )

        // (Soundscape feature removed from radar as requested)

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
        // Radar Privacy & Range Quick Bar
        Surface(
            color = Color(0xFF031405),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (hidePreciseLocationOnRadar) shieldCyan.copy(alpha = 0.5f) else Color(0xFF005511)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
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
                                text = if (hidePreciseLocationOnRadar) "GHOST MODE: PRECISE PIN HIDDEN" else "LIVE RADAR: PRECISE COORDINATES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (hidePreciseLocationOnRadar) shieldCyan else Color(0xFF00FF41),
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                            Text(
                                text = if (hidePreciseLocationOnRadar) {
                                    "Simulated as $radarObfuscatedRange far out"
                                } else {
                                    "Your exact street blip is broadcasting"
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
        // Radar Screen with Dynamic Touch Pinch-to-Zoom
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            val radarDiameterDp = minOf(maxWidth, 340.dp)
            val density = androidx.compose.ui.platform.LocalDensity.current
            val radarRadiusPx = with(density) { (radarDiameterDp / 2).toPx() }
            val centerPx = radarRadiusPx

            val combinedCount = minOf(nearbyUsers.size + nearbyPosts.size, 10)
            val combinedItems = remember(nearbyUsers, nearbyPosts) {
                (nearbyUsers + nearbyPosts).take(combinedCount)
            }

            // Radar circular display strictly clipped to CircleShape with touch gesture zoom
            Box(
                modifier = Modifier
                    .size(radarDiameterDp)
                    .clip(CircleShape)
                    .background(Color(0xFF020E04))
                    .border(2.5.dp, Brush.radialGradient(listOf(radarColor, Color(0xFF00AA29), Color(0xFF00330D))), CircleShape)
                    .pointerInput(selectedRadiusKm) {
                        detectTransformGestures { _, _, zoom, _ ->
                            if (zoom != 1f) {
                                val nextRadius = (selectedRadiusKm / zoom).coerceIn(1.0, 500.0)
                                onRadiusChange(nextRadius)
                            }
                        }
                    }
            ) {
                // 1. Canvas with grid, concentric rings, crosshairs, precise coordinate markers and rotating sweep
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.minDimension / 2
                    
                    // Draw Grid
                    val step = size.width / 10
                    for (i in 0..10) {
                        drawLine(gridColor.copy(alpha = 0.3f), start = Offset(i * step, 0f), end = Offset(i * step, size.height), strokeWidth = 1.dp.toPx())
                    }
                    val stepY = size.height / 10
                    for (i in 0..10) {
                        drawLine(gridColor.copy(alpha = 0.3f), start = Offset(0f, i * stepY), end = Offset(size.width, i * stepY), strokeWidth = 1.dp.toPx())
                    }
                    
                    // Draw Concentric Range Rings (25%, 50%, 75%, 100%)
                    for (i in 1..4) {
                        drawCircle(
                            color = radarColor.copy(alpha = 0.45f),
                            radius = radius * (i / 4f),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Scaled distance annotations on range rings
                    val ringDistPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#00FF41")
                        textSize = 8.5.dp.toPx()
                        textAlign = android.graphics.Paint.Align.LEFT
                        typeface = android.graphics.Typeface.MONOSPACE
                        isAntiAlias = true
                        alpha = 190
                    }
                    val ring1Km = String.format("%.1f", selectedRadiusKm * 0.25)
                    val ring2Km = String.format("%.1f", selectedRadiusKm * 0.50)
                    val ring3Km = String.format("%.1f", selectedRadiusKm * 0.75)
                    val ring4Km = String.format("%.0f", selectedRadiusKm)
                    drawContext.canvas.nativeCanvas.drawText("${ring1Km}k", center.x + 4.dp.toPx(), center.y - radius * 0.25f - 2.dp.toPx(), ringDistPaint)
                    drawContext.canvas.nativeCanvas.drawText("${ring2Km}k", center.x + 4.dp.toPx(), center.y - radius * 0.50f - 2.dp.toPx(), ringDistPaint)
                    drawContext.canvas.nativeCanvas.drawText("${ring3Km}k", center.x + 4.dp.toPx(), center.y - radius * 0.75f - 2.dp.toPx(), ringDistPaint)
                    drawContext.canvas.nativeCanvas.drawText("${ring4Km}KM", center.x + 4.dp.toPx(), center.y - radius + 25.dp.toPx(), ringDistPaint)

                    // Draw Precise Cardinal Coordinates on rim (N, E, W, S)
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#00FF41")
                        textSize = 10.dp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.MONOSPACE
                        isFakeBoldText = true
                        isAntiAlias = true
                        alpha = 230
                    }
                    val northPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.parseColor("#39FF14")
                        textSize = 11.dp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.MONOSPACE
                        isFakeBoldText = true
                        isAntiAlias = true
                        alpha = 255
                    }
                    val textMargin = 14.dp.toPx()

                    // Glowing North Chevron Pointer
                    val northChevron = Path().apply {
                        moveTo(center.x, center.y - radius + 3.dp.toPx())
                        lineTo(center.x - 5.dp.toPx(), center.y - radius + 11.dp.toPx())
                        lineTo(center.x + 5.dp.toPx(), center.y - radius + 11.dp.toPx())
                        close()
                    }
                    drawPath(northChevron, color = Color(0xFF39FF14))

                    // Cardinal Coordinate Labels
                    drawContext.canvas.nativeCanvas.drawText("N 000°", center.x, center.y - radius + textMargin + 9.dp.toPx(), northPaint)
                    drawContext.canvas.nativeCanvas.drawText("S 180°", center.x, center.y + radius - textMargin + 2.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("W 270°", center.x - radius + textMargin * 1.6f, center.y + 4.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("E 090°", center.x + radius - textMargin * 1.6f, center.y + 4.dp.toPx(), textPaint)

                    // 12 Azimuth Degree Ticks around the outer rim
                    for (deg in 0 until 360 step 30) {
                        val rad = Math.toRadians(deg.toDouble())
                        val isCardinal = deg % 90 == 0
                        val tickLen = if (isCardinal) 8.dp.toPx() else 4.dp.toPx()
                        val tickColor = if (isCardinal) Color(0xFF39FF14) else radarColor.copy(alpha = 0.45f)
                        val startX = center.x + (radius - tickLen) * cos(rad).toFloat()
                        val startY = center.y + (radius - tickLen) * sin(rad).toFloat()
                        val endX = center.x + radius * cos(rad).toFloat()
                        val endY = center.y + radius * sin(rad).toFloat()
                        drawLine(
                            color = tickColor,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isCardinal) 2.dp.toPx() else 1.dp.toPx()
                        )
                    }

                    // --- Dynamic Visual Pulse Waves (Synced with user's distance range setting) ---
                    val pulseColor = if (hidePreciseLocationOnRadar) shieldCyan else radarColor
                    val pulseProgressList = listOf(pulse1, pulse2, pulse3)
                    for (progress in pulseProgressList) {
                        if (progress > 0.01f) {
                            val waveRadius = radius * progress
                            val fadeAlpha = ((1f - progress) * (if (hidePreciseLocationOnRadar) 0.70f else 0.85f)).coerceIn(0f, 1f)
                            
                            // Glowing shockwave ring expanding outwards
                            drawCircle(
                                color = pulseColor.copy(alpha = fadeAlpha),
                                radius = waveRadius,
                                center = center,
                                style = Stroke(width = (4f * (1f - progress)).coerceAtLeast(1.2f).dp.toPx())
                            )
                            // Soft radial gradient aura wave behind leading edge
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(
                                        pulseColor.copy(alpha = fadeAlpha * 0.22f),
                                        pulseColor.copy(alpha = fadeAlpha * 0.04f),
                                        Color.Transparent
                                    ),
                                    center = center,
                                    radius = waveRadius.coerceAtLeast(1f)
                                ),
                                radius = waveRadius,
                                center = center
                            )
                        }
                    }

                    // Crosshairs
                    drawLine(radarColor.copy(alpha = 0.45f), start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())
                    drawLine(radarColor.copy(alpha = 0.45f), start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())
                    
                    // Section 3.3: Activity Heatmap Glow Nodes Canvas Layer
                    val heatmapList = listOf(
                        Offset(center.x + radius * 0.35f, center.y - radius * 0.25f) to 0.9f,
                        Offset(center.x - radius * 0.45f, center.y + radius * 0.30f) to 0.75f,
                        Offset(center.x + radius * 0.15f, center.y + radius * 0.50f) to 0.6f
                    )
                    heatmapList.forEach { (offset, intensity) ->
                        val nodeRadius = (35.dp.toPx() * intensity).coerceIn(20.dp.toPx(), 55.dp.toPx())
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFF3333).copy(alpha = 0.45f * intensity),
                                    Color(0xFFFF9900).copy(alpha = 0.25f * intensity),
                                    Color.Transparent
                                ),
                                center = offset,
                                radius = nodeRadius
                            ),
                            center = offset,
                            radius = nodeRadius
                        )
                    }

                    // Sweep Beam
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(Color.Transparent, radarColor.copy(alpha = 0.08f), radarColor.copy(alpha = 0.65f)),
                            center = center
                        ),
                        startAngle = sweepAngle - 90f,
                        sweepAngle = 90f,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                    drawLine(
                        color = radarColor,
                        start = center,
                        end = Offset(
                            x = center.x + radius * cos(Math.toRadians(sweepAngle.toDouble())).toFloat(),
                            y = center.y + radius * sin(Math.toRadians(sweepAngle.toDouble())).toFloat()
                        ),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Center Pulse Beacon Ping (Synced heartbeat glow)
                    val centerPulseGlow = ((1f - pulse1) * 0.45f).coerceAtLeast(0f)
                    drawCircle(
                        color = pulseColor.copy(alpha = centerPulseGlow),
                        radius = (14.dp.toPx() + 8.dp.toPx() * (1f - pulse1)),
                        center = center
                    )

                    // Center Blip
                    if (hidePreciseLocationOnRadar) {
                        // Masked / Shielded indicator
                        drawCircle(color = shieldCyan.copy(alpha = 0.35f), radius = 12.dp.toPx(), center = center)
                        drawCircle(color = shieldCyan, radius = 6.dp.toPx(), center = center)
                        drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = center)
                    } else {
                        drawCircle(color = radarColor, radius = 5.dp.toPx(), center = center)
                        drawCircle(color = Color.White, radius = 2.dp.toPx(), center = center)
                    }
                }

                // Pulse Sync Telemetry Tag
                Surface(
                    color = Color.Black.copy(alpha = 0.65f),
                    shape = RoundedCornerShape(100.dp),
                    border = BorderStroke(0.7.dp, if (hidePreciseLocationOnRadar) shieldCyan.copy(alpha = 0.6f) else radarColor.copy(alpha = 0.6f)),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
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
                
                // 2. Miniature Cards plotted strictly INSIDE this CircleShape Box
                val blipCardSizeDp = 34.dp
                val blipCardRadiusPx = with(density) { 17.dp.toPx() }
                val maxDist = radarRadiusPx - blipCardRadiusPx - with(density) { 6.dp.toPx() }
                val minDist = blipCardRadiusPx + with(density) { 12.dp.toPx() }

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
                    val borderGlow = if (isSwept) radarColor else radarColor.copy(alpha = 0.4f)
                    
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
                            .size(blipCardSizeDp)
                            .clip(CircleShape)
                            .background(Color(0xFF031405))
                            .border(1.5.dp, borderGlow, CircleShape)
                            .clickable {
                                if (item is OtherUserEntity) {
                                    dispatchUserTarget = item
                                    onUserClick(item)
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
        
        // (Fixed range filters 5KM, 50KM, Earth removed as requested - users use dynamic scale +/- zoom controls above)

        // Section 3.6: Landmark Geo-Portal Beacons Tray
        LandmarkGeoPortalBeaconsTray(
            beacons = sampleGeoBeacons,
            selectedBeaconId = selectedBeaconId,
            onSelectBeacon = { beacon ->
                selectedBeaconId = if (selectedBeaconId == beacon.id) null else beacon.id
            },
            modifier = Modifier.padding(top = 4.dp)
        )

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
                    onSendQuickGreeting = { _ -> dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}
