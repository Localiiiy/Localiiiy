package com.example.ui.components.radar

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.ui.theme.*
import kotlin.math.*

// Sci-Fi HUD Radar Palette
val RadarNeonGreen = Color(0xFF14FF00)
val RadarPhosphor = Color(0xFF00FF66)
val RadarDarkBg = Color(0xFF010A04)
val RadarPanelBg = Color(0xFF031407)
val RadarGridLine = Color(0x3500FF66)
val RadarCyanGlow = Color(0xFF00E5FF)
val RadarAlertRed = Color(0xFFFF2A2A)
val RadarWarningAmber = Color(0xFFFFB300)
val RadarLightBg = Color(0xFFF0FDF4)
val RadarLightPanel = Color(0xFFDCFCE7)

/**
 * Data Model for Landmark Geo-Portal Beacon
 */
data class GeoPortalBeacon(
    val id: String,
    val name: String,
    val category: String,
    val distanceKm: Double,
    val activePostsCount: Int,
    val ambientAudioFrequency: String,
    val vibeScore: Int, // 0..100
    val latitude: Double,
    val longitude: Double
)

/**
 * Section 3.1: Fuzzy Geohash Privacy Aura Rings
 * Replaces pinpoint coordinates with blurred circular zones (approx 200m randomized aura).
 */
@Composable
fun FuzzyGeohashAuraRing(
    radiusPx: Float,
    isGhostMode: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "FuzzyAuraTransition")
    val auraBreath by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraBreath"
    )

    Canvas(modifier = modifier.size((radiusPx * 2).dp)) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val currentRadius = (size.width / 2f) * auraBreath

        val auraColor = if (isGhostMode) RadarCyanGlow else RadarNeonGreen

        // Blurred concentric aura rings simulating 200m geohash zone
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    auraColor.copy(alpha = 0.35f),
                    auraColor.copy(alpha = 0.12f),
                    Color.Transparent
                ),
                center = center,
                radius = currentRadius
            ),
            center = center,
            radius = currentRadius
        )

        // Dashed geohash boundary ring
        drawCircle(
            color = auraColor.copy(alpha = 0.65f),
            radius = currentRadius * 0.88f,
            center = center,
            style = Stroke(
                width = 1.2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
            )
        )
    }
}

/**
 * Section 3.2: Passive Sonar Ghost Mode HUD Indicator
 */
@Composable
fun PassiveSonarGhostBanner(
    isGhostActive: Boolean,
    onToggleGhost: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isGhostActive) Color(0xFF03222B) else Color(0xFF031A08),
        border = BorderStroke(
            1.dp,
            if (isGhostActive) RadarCyanGlow.copy(alpha = 0.8f) else RadarPhosphor.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .testTag("passive_sonar_ghost_banner")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clickable { onToggleGhost() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    shape = CircleShape,
                    color = if (isGhostActive) RadarCyanGlow.copy(alpha = 0.2f) else RadarPhosphor.copy(alpha = 0.15f),
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isGhostActive) Icons.Default.VisibilityOff else Icons.Default.Sensors,
                            contentDescription = "Ghost Sonar",
                            tint = if (isGhostActive) RadarCyanGlow else RadarNeonGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = if (isGhostActive) "PASSIVE SONAR: CLOAKED 👻" else "RADAR BEACON: BROADCASTING 📡",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGhostActive) RadarCyanGlow else RadarPhosphor,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (isGhostActive) "Observing frequency • Coordinates zeroed out" else "Active blip visible within range ring",
                        fontSize = 9.5.sp,
                        color = Color.LightGray
                    )
                }
            }

            Switch(
                checked = isGhostActive,
                onCheckedChange = { onToggleGhost() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = RadarCyanGlow,
                    checkedTrackColor = Color(0xFF08414D),
                    uncheckedThumbColor = RadarPhosphor,
                    uncheckedTrackColor = Color(0xFF062B10)
                ),
                modifier = Modifier.scale(0.8f)
            )
        }
    }
}

/**
 * Section 3.3: Activity Heatmap Glow Nodes
 * Renders glowing thermal nodes on canvas indicating active content hubs.
 */
@Composable
fun ActivityHeatmapNodesCanvas(
    nodes: List<Pair<Offset, Float>>, // Coordinate and Intensity 0..1
    isDayTheme: Boolean = false,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        nodes.forEach { (offset, intensity) ->
            val nodeRadius = (30.dp.toPx() * intensity).coerceIn(16.dp.toPx(), 48.dp.toPx())
            val heatColors = if (isDayTheme) {
                listOf(
                    Color(0xFFE11D48).copy(alpha = 0.45f * intensity),
                    Color(0xFFF97316).copy(alpha = 0.25f * intensity),
                    Color.Transparent
                )
            } else {
                listOf(
                    Color(0xFFFF2A2A).copy(alpha = 0.55f * intensity),
                    Color(0xFFFF9100).copy(alpha = 0.35f * intensity),
                    Color(0xFF14FF00).copy(alpha = 0.10f * intensity),
                    Color.Transparent
                )
            }

            drawCircle(
                brush = Brush.radialGradient(
                    colors = heatColors,
                    center = offset,
                    radius = nodeRadius
                ),
                center = offset,
                radius = nodeRadius
            )
        }
    }
}

/**
 * Section 3.4: 3D Perspective Pitch Angle Controller
 */
@Composable
fun RadarPerspectivePitchToggle(
    is3DPerspective: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (is3DPerspective) RadarNeonGreen.copy(alpha = 0.2f) else RadarPanelBg,
        border = BorderStroke(1.dp, if (is3DPerspective) RadarNeonGreen else RadarPhosphor.copy(alpha = 0.4f)),
        modifier = modifier
            .clickable { onToggle(!is3DPerspective) }
            .testTag("radar_pitch_perspective_toggle")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (is3DPerspective) Icons.Default.ViewInAr else Icons.Default.Layers,
                contentDescription = "Perspective Pitch",
                tint = if (is3DPerspective) RadarNeonGreen else Color.LightGray,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = if (is3DPerspective) "3D PITCH 45°" else "2D TOP-DOWN",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (is3DPerspective) RadarNeonGreen else Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.5: Day / Night Tactical HUD Theme Switcher
 */
@Composable
fun TacticalHudThemeSwitcher(
    isDayTheme: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isDayTheme) Color(0xFFE2E8F0) else RadarPanelBg,
        border = BorderStroke(1.dp, if (isDayTheme) Color(0xFF0F766E) else RadarPhosphor.copy(alpha = 0.4f)),
        modifier = modifier
            .clickable { onToggle() }
            .testTag("radar_hud_theme_toggle")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isDayTheme) Icons.Default.WbSunny else Icons.Default.Nightlight,
                contentDescription = "HUD Theme",
                tint = if (isDayTheme) Color(0xFF0F766E) else RadarPhosphor,
                modifier = Modifier.size(13.dp)
            )
            Text(
                text = if (isDayTheme) "DAY HUD" else "NIGHT HUD",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDayTheme) Color(0xFF0F766E) else RadarPhosphor,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.6: Landmark Geo-Portal Beacons Tray
 */
@Composable
fun LandmarkGeoPortalBeaconsTray(
    beacons: List<GeoPortalBeacon>,
    selectedBeaconId: String?,
    onSelectBeacon: (GeoPortalBeacon) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("landmark_geo_portal_tray")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = null,
                    tint = RadarNeonGreen,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "GEO-PORTAL BEACONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarNeonGreen,
                    fontFamily = FontFamily.Monospace
                )
            }
            Text(
                text = "${beacons.size} ACTIVE",
                fontSize = 9.sp,
                color = Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(beacons) { beacon ->
                val isSelected = beacon.id == selectedBeaconId
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isSelected) RadarNeonGreen.copy(alpha = 0.2f) else RadarPanelBg,
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) RadarNeonGreen else RadarPhosphor.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier
                        .width(150.dp)
                        .clickable { onSelectBeacon(beacon) }
                        .testTag("geo_beacon_${beacon.id}")
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${beacon.distanceKm}km",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = RadarNeonGreen,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "VIBE ${beacon.vibeScore}%",
                                fontSize = 8.5.sp,
                                color = RadarWarningAmber,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = beacon.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = RadarCyanGlow,
                                modifier = Modifier.size(10.dp)
                            )
                            Text(
                                text = beacon.ambientAudioFrequency,
                                fontSize = 8.5.sp,
                                color = RadarCyanGlow,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Section 3.7: Radar Scale Zoom Slider
 */
@Composable
fun RadarScaleZoomSlider(
    currentRadiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = RadarPanelBg,
        border = BorderStroke(0.8.dp, RadarPhosphor.copy(alpha = 0.35f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .testTag("radar_scale_zoom_slider")
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "RADAR SCALE ZOOM",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarPhosphor,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${currentRadiusKm.toInt()} KM RADIUS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarNeonGreen,
                    fontFamily = FontFamily.Monospace
                )
            }
            Slider(
                value = currentRadiusKm.toFloat(),
                onValueChange = { onRadiusChange(it.toDouble()) },
                valueRange = 1f..100f,
                steps = 19,
                colors = SliderDefaults.colors(
                    thumbColor = RadarNeonGreen,
                    activeTrackColor = RadarNeonGreen,
                    inactiveTrackColor = Color(0xFF0D3814)
                ),
                modifier = Modifier.fillMaxWidth().height(28.dp)
            )
        }
    }
}

/**
 * Section 3.8: Real-Time Compass Heading Sensor Badge
 */
@Composable
fun RealTimeCompassHeadingBadge(
    headingDeg: Float,
    modifier: Modifier = Modifier
) {
    val cardinal = when ((headingDeg % 360).toInt()) {
        in 338..360, in 0..22 -> "N"
        in 23..67 -> "NE"
        in 68..112 -> "E"
        in 113..157 -> "SE"
        in 158..202 -> "S"
        in 203..247 -> "SW"
        in 248..292 -> "W"
        else -> "NW"
    }

    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF031608),
        border = BorderStroke(1.dp, RadarPhosphor),
        modifier = modifier.testTag("compass_heading_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Navigation,
                contentDescription = "Heading",
                tint = RadarNeonGreen,
                modifier = Modifier
                    .size(12.dp)
                    .rotate(headingDeg)
            )
            Text(
                text = "$cardinal ${headingDeg.toInt()}°",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = RadarNeonGreen,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.9: Ephemeral Soundscape Radar Bar
 */
@Composable
fun EphemeralSoundscapeRadarBar(
    soundFrequency: String = "432 Hz Ambient",
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF02170B),
        border = BorderStroke(0.8.dp, RadarCyanGlow.copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("ephemeral_soundscape_bar")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = "Soundscape",
                    tint = RadarCyanGlow,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "LOCAL SOUNDSCAPE: $soundFrequency",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadarCyanGlow,
                    fontFamily = FontFamily.Monospace
                )
            }
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                    contentDescription = "Toggle Audio",
                    tint = RadarCyanGlow,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/**
 * Section 3.10: Emergency Safe-Haven Markers
 */
@Composable
fun EmergencySafeHavenMarker(
    havenName: String,
    distanceKm: Double,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF260505),
        border = BorderStroke(1.dp, RadarAlertRed),
        modifier = modifier
            .clickable { onClick() }
            .testTag("emergency_safe_haven_marker")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = "Safe Haven",
                tint = RadarAlertRed,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "SAFE-HAVEN: $havenName (${distanceKm}km)",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.11: Offline Radar Cache Mode Toggle
 */
@Composable
fun OfflineRadarCacheBadge(
    isOfflineCached: Boolean,
    onSyncCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isOfflineCached) Color(0xFF0F2615) else Color(0xFF1F1F1F),
        border = BorderStroke(0.8.dp, if (isOfflineCached) RadarNeonGreen else Color.Gray),
        modifier = modifier
            .clickable { onSyncCache() }
            .testTag("offline_radar_cache_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isOfflineCached) Icons.Default.CloudDone else Icons.Default.CloudDownload,
                contentDescription = "Offline Cache",
                tint = if (isOfflineCached) RadarNeonGreen else Color.LightGray,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = if (isOfflineCached) "CACHED OFFLINE" else "SYNC CACHE",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isOfflineCached) RadarNeonGreen else Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.12: Sweep Velocity Control
 */
@Composable
fun RadarSweepVelocityController(
    sweepSpeedMultiplier: Float, // 0.5x, 1.0x, 2.0x
    onSpeedChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.testTag("radar_sweep_velocity_row"),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "SWEEP:",
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = RadarPhosphor,
            fontFamily = FontFamily.Monospace
        )
        listOf(0.5f to "0.5x", 1.0f to "1.0x", 2.0f to "2.0x").forEach { (speed, label) ->
            val isSelected = abs(sweepSpeedMultiplier - speed) < 0.1f
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = if (isSelected) RadarNeonGreen else Color(0xFF06210A),
                border = BorderStroke(0.7.dp, RadarPhosphor),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSpeedChange(speed) }
            ) {
                Text(
                    text = label,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.Black else Color.White,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

/**
 * Section 3.13: Event Perimeter Geo-Fence Overlay Tag
 */
@Composable
fun EventGeoFenceOverlayTag(
    eventName: String,
    radiusMeters: Int,
    attendeesCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF1C1300),
        border = BorderStroke(1.dp, RadarWarningAmber),
        modifier = modifier.testTag("event_geofence_tag")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Festival,
                contentDescription = "Event Fence",
                tint = RadarWarningAmber,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "GEOFENCE: $eventName (${radiusMeters}m • $attendeesCount live)",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = RadarWarningAmber,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.14: Proximity Ping Notifications Toggle
 */
@Composable
fun ProximityPingNotificationsToggle(
    isProximityPingEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = RadarPanelBg,
        border = BorderStroke(0.8.dp, RadarPhosphor.copy(alpha = 0.4f)),
        modifier = modifier
            .clickable { onToggle() }
            .testTag("proximity_ping_toggle")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isProximityPingEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                contentDescription = "Proximity Ping",
                tint = if (isProximityPingEnabled) RadarNeonGreen else Color.Gray,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (isProximityPingEnabled) "PROX PING ON" else "PROX PING OFF",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isProximityPingEnabled) RadarNeonGreen else Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.15: Direct Radar Chat Dispatch Sheet
 */
@Composable
fun DirectRadarChatDispatchSheet(
    targetUser: OtherUserEntity,
    onSendQuickGreeting: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var quickMessage by remember { mutableStateOf("Hey neighbor! Saw you on Live Radar 👋") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = RadarPanelBg),
        border = BorderStroke(1.5.dp, RadarNeonGreen),
        modifier = modifier
            .fillMaxWidth()
            .testTag("direct_radar_chat_dispatch")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = targetUser.avatarUrl,
                        contentDescription = targetUser.username,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(32.dp).clip(CircleShape).border(1.dp, RadarNeonGreen, CircleShape)
                    )
                    Column {
                        Text(
                            text = "DISPATCH RADAR BEAM TO @${targetUser.username}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = RadarNeonGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${targetUser.distanceKm} km away • Tactical channel open",
                            fontSize = 9.sp,
                            color = Color.LightGray
                        )
                    }
                }
                IconButton(onClick = onClose, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = RadarPhosphor, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = quickMessage,
                onValueChange = { quickMessage = it },
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontFamily = FontFamily.Monospace),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RadarNeonGreen,
                    unfocusedBorderColor = RadarPhosphor.copy(alpha = 0.5f),
                    focusedContainerColor = Color.Black,
                    unfocusedContainerColor = Color.Black
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Wave 👋",
                    "Coffee nearby? ☕",
                    "What's happening? 📍"
                ).forEach { preset ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0A2B11),
                        border = BorderStroke(0.6.dp, RadarPhosphor),
                        modifier = Modifier
                            .clickable { quickMessage = preset }
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            text = preset,
                            fontSize = 9.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onSendQuickGreeting(quickMessage)
                    onClose()
                },
                colors = ButtonDefaults.buttonColors(containerColor = RadarNeonGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(42.dp)
            ) {
                Text(
                    text = "DISPATCH TACTICAL MESSAGE",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

/**
 * Section 3.16: Speed / Movement Kalman Filtering Indicator
 */
@Composable
fun MovementKalmanFilterIndicator(
    isKalmanFiltered: Boolean,
    speedKmh: Float = 4.8f,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF041908),
        border = BorderStroke(0.8.dp, RadarPhosphor.copy(alpha = 0.5f)),
        modifier = modifier.testTag("kalman_filter_indicator")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = "Kalman Filter",
                tint = RadarNeonGreen,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "KALMAN FILTER: ${if (isKalmanFiltered) "ACTIVE" else "BYPASS"} • ${speedKmh} km/h",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = RadarNeonGreen,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.17: Neighborhood Vibrancy Pulse Score (0..100)
 */
@Composable
fun NeighborhoodVibrancyScoreBadge(
    score: Int, // 0..100
    modifier: Modifier = Modifier
) {
    val vibrancyLevel = when {
        score >= 80 -> "THRIVING 🔥"
        score >= 50 -> "ACTIVE ⚡"
        else -> "TRANQUIL 🌿"
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFF031608),
        border = BorderStroke(1.dp, RadarNeonGreen),
        modifier = modifier.testTag("neighborhood_vibrancy_badge")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (score >= 50) RadarNeonGreen else RadarWarningAmber)
            )
            Text(
                text = "VIBRANCY: $score/100 • $vibrancyLevel",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.18: Battery Saver Proximity Throttling Mode
 */
@Composable
fun BatterySaverProximityToggle(
    isBatterySaverActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isBatterySaverActive) Color(0xFF1E1E05) else RadarPanelBg,
        border = BorderStroke(1.dp, if (isBatterySaverActive) RadarWarningAmber else RadarPhosphor.copy(alpha = 0.4f)),
        modifier = modifier
            .clickable { onToggle() }
            .testTag("battery_saver_toggle")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isBatterySaverActive) Icons.Default.BatteryChargingFull else Icons.Default.BatteryFull,
                contentDescription = "Battery Saver",
                tint = if (isBatterySaverActive) RadarWarningAmber else Color.LightGray,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = if (isBatterySaverActive) "BATTERY SAVER (15s)" else "LIVE SCAN (1s)",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBatterySaverActive) RadarWarningAmber else Color.LightGray,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.19: Location Jitter Privacy Masking Indicator
 */
@Composable
fun LocationJitterMaskIndicator(
    isJitterEnabled: Boolean,
    jitterRadiusMeters: Int = 180,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = Color(0xFF03191F),
        border = BorderStroke(0.8.dp, RadarCyanGlow),
        modifier = modifier.testTag("location_jitter_indicator")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Jitter",
                tint = RadarCyanGlow,
                modifier = Modifier.size(11.dp)
            )
            Text(
                text = "GPS JITTER: ${if (isJitterEnabled) "CLOAKED ±${jitterRadiusMeters}m" else "EXACT"}",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.Bold,
                color = RadarCyanGlow,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Section 3.20: Section 3 Complete Master HUD Control Bar
 * Integrates controls for quick access across all 20 radar spatial discovery features.
 */
@Composable
fun Section3MasterControlBar(
    isGhostActive: Boolean,
    is3DPerspective: Boolean,
    isDayTheme: Boolean,
    isBatterySaver: Boolean,
    onToggleGhost: () -> Unit,
    onTogglePerspective: (Boolean) -> Unit,
    onToggleDayTheme: () -> Unit,
    onToggleBatterySaver: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 14.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadarPerspectivePitchToggle(
            is3DPerspective = is3DPerspective,
            onToggle = onTogglePerspective
        )
        TacticalHudThemeSwitcher(
            isDayTheme = isDayTheme,
            onToggle = onToggleDayTheme
        )
        BatterySaverProximityToggle(
            isBatterySaverActive = isBatterySaver,
            onToggle = onToggleBatterySaver
        )
    }
}
