package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Column(modifier = modifier.fillMaxWidth().background(Color.Black)) {
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

                // In-Radar Far-Out Range Picker (3k, 10K, 100k, 500K, Country, Earth, Galaxy)
                if (hidePreciseLocationOnRadar && onSelectObfuscatedRange != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "SIMULATE DISTANCE:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = shieldCyan,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(systematicRanges) { opt ->
                            val isSel = radarObfuscatedRange.equals(opt.key, ignoreCase = true) ||
                                    radarObfuscatedRange.equals(opt.shortLabel, ignoreCase = true)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSel) shieldCyan else Color(0xFF0F2613),
                                border = androidx.compose.foundation.BorderStroke(0.8.dp, if (isSel) shieldCyan else Color(0xFF005511)),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { onSelectObfuscatedRange(opt.key) }
                            ) {
                                Text(
                                    text = opt.shortLabel,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
        // Radar Screen
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

            // Radar circular display strictly clipped to CircleShape
            Box(
                modifier = Modifier
                    .size(radarDiameterDp)
                    .clip(CircleShape)
                    .background(Color(0xFF020E04))
                    .border(2.dp, Brush.radialGradient(listOf(radarColor, Color(0xFF00AA29), Color(0xFF00330D))), CircleShape)
            ) {
                // 1. Canvas with grid, concentric rings, crosshairs, and rotating sweep
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
                    
                    // Draw Concentric Circles
                    for (i in 1..4) {
                        drawCircle(
                            color = radarColor.copy(alpha = 0.45f),
                            radius = radius * (i / 4f),
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
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
                val combinedCount = minOf(nearbyUsers.size + nearbyPosts.size, 10)
                val combinedItems = (nearbyUsers + nearbyPosts).take(combinedCount)
                
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
                                if (item is OtherUserEntity) onUserClick(item)
                                else if (item is PostEntity) onPostClick(item)
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
        
        // Systematic Range Options Bar (3KM, 5KM, 50KM, 100KM, 500KM, 1000KM, Country, Earth, Galaxy)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp)
                .testTag("radar_systematic_ranges_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(systematicRanges) { opt ->
                val isSelected = kotlin.math.abs(selectedRadiusKm - opt.km) < 0.1 ||
                        (opt.key == "COUNTRY" && selectedRadiusKm == SystematicDistanceScale.COUNTRY_DEFAULT_KM) ||
                        (opt.key == "EARTH" && selectedRadiusKm == SystematicDistanceScale.EARTH_KM) ||
                        (opt.key == "GALAXY" && selectedRadiusKm == SystematicDistanceScale.GALAXY_KM)
                val activeBg = if (hidePreciseLocationOnRadar) shieldCyan else radarColor
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) activeBg else Color(0xFF162518),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) Color.White.copy(alpha = 0.8f) else Color(0xFF005511)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onRadiusChange(opt.km) }
                        .testTag("radar_range_pill_${opt.key.lowercase()}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = opt.icon, fontSize = 11.sp)
                        Text(
                            text = opt.shortLabel,
                            color = if (isSelected) Color.Black else Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
