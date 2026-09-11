package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Radar
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.util.LocationHelper
import kotlin.math.*

/**
 * Tactical Live Radar Interface matching the high-tech military / sci-fi HUD aesthetic.
 * Features:
 * - 360-degree compass rim with degree tick marks and bearings
 * - 5 Concentric circular polar range grid rings and 24 radial coordinate lines
 * - Dynamic rotating phosphor green sweep beam with beam flare
 * - Top-left 3D wireframe rotating globe with latitude/longitude lines
 * - Dynamic audio oscilloscope frequency waveforms (vertical & horizontal)
 * - Telemetry signal equalizer spectrum bars
 * - Real-time target coordinate table (CODE | LAT | LONG | KM)
 * - Heading compass dial and directional sci-fi chevrons
 * - Target bracket reticles [ • ] with live sonar ping animation
 */
@Composable
fun TacticalRadarView(
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity>,
    nearbyPosts: List<PostEntity>,
    selectedRadiusKm: Double = 3.0,
    isLocationEnabled: Boolean = true,
    isPrivateAccount: Boolean = false,
    onRadiusChange: (Double) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onUserClick: (OtherUserEntity) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    onWaveAtUser: (OtherUserEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBlipType by remember { mutableStateOf("ALL") } // "ALL", "PEOPLE", "POSTS"
    var inspectUser by remember { mutableStateOf<OtherUserEntity?>(null) }
    var inspectPost by remember { mutableStateOf<PostEntity?>(null) }

    // Animations
    val infiniteTransition = rememberInfiniteTransition(label = "TacticalRadarTransition")

    // Continuous 360 sweep rotation
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    // Rotating 3D wireframe globe angle
    val globeRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GlobeRotation"
    )

    // Oscilloscope audio frequency wave phase
    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WavePhase"
    )

    // Telemetry Equalizer bar movement
    val eqProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "EqualizerProgress"
    )

    // Sonar ping pulse wave expansion
    val pingPulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PingPulse"
    )

    // Filter items based on radius
    val filteredUsers = remember(nearbyUsers, selectedRadiusKm, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else nearbyUsers.filter { it.distanceKm <= selectedRadiusKm }
    }

    val filteredPosts = remember(nearbyPosts, selectedRadiusKm, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else nearbyPosts.filter { (it.distanceKm ?: 999.0) <= selectedRadiusKm }
    }

    val currentRadiusOption = remember(selectedRadiusKm) {
        RadarRadiusPresets.ALL_OPTIONS.firstOrNull { it.km == selectedRadiusKm }
            ?: RadarRadiusOption(selectedRadiusKm, "${selectedRadiusKm.toInt()}km", "${selectedRadiusKm.toInt()} KM", "Custom", "📍")
    }

    // Outer Tactical HUD Shell
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = TacticalRadarBg,
        border = BorderStroke(1.5.dp, TacticalPhosphorGreen.copy(alpha = 0.8f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("tactical_live_radar_view")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // ==========================================
            // TOP HEADER: HUD TITLE, CHEVRONS & TELEMETRY
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isLocationEnabled) TacticalBrightGreen else TacticalAlertRed)
                    )
                    Text(
                        text = "LIVE RADAR // PROXIMITY HUD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = TacticalBrightGreen,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Chevrons and Telemetry Frequency
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TacticalDirectionChevrons(
                        isFacingRight = true,
                        chevronCount = 5,
                        modifier = Modifier.size(width = 32.dp, height = 10.dp)
                    )
                    Text(
                        text = "142.85 MHz",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalPhosphorGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TacticalDarkGreen.copy(alpha = 0.6f),
                        border = BorderStroke(0.8.dp, TacticalPhosphorGreen)
                    ) {
                        Text(
                            text = if (isLocationEnabled) "ONLINE" else "OFFLINE",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLocationEnabled) TacticalBrightGreen else TacticalAlertRed,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // =======================================================
            // TOP ROW: 3D WIREFRAME GLOBE + SCAN TELEMETRY CONTROLS
            // =======================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Top-Left 3D Wireframe Rotating Globe (from attached reference image)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TacticalWireframeGlobe(
                        globeRotationAngle = globeRotation,
                        modifier = Modifier.size(54.dp)
                    )
                    Column {
                        Text(
                            text = "SECTOR SCAN: ${currentRadiusOption.shortLabel.uppercase()}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalBrightGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "POS: ${String.format("%.2f", userProfile.latitude)}°N, ${String.format("%.2f", abs(userProfile.longitude))}°W",
                            fontSize = 8.sp,
                            color = TacticalPhosphorGreen.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${filteredUsers.size} BEACONS • ${filteredPosts.size} PULSES",
                            fontSize = 8.sp,
                            color = TacticalPhosphorGreen.copy(alpha = 0.8f),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Top-Right: Digital Pixel Heat Matrix + Filter Pill Selector
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TacticalPixelHeatMatrix(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .border(0.8.dp, TacticalPhosphorGreen.copy(alpha = 0.6f), RoundedCornerShape(3.dp))
                            .background(TacticalPanelBg)
                            .padding(2.dp)
                    )

                    // Filter Pill Selector (ALL, PEOPLE, POSTS)
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        listOf("ALL", "PEOPLE", "POSTS").forEach { type ->
                            val isSelected = selectedBlipType == type
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSelected) TacticalBrightGreen else TacticalPanelBg,
                                border = BorderStroke(0.8.dp, TacticalPhosphorGreen),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .clickable { selectedBlipType = type }
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else TacticalTextGreen,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // =========================================================================
            // MAIN MIDDLE SECTION: LEFT OSCILLOSCOPE + RADAR CANVAS + RIGHT TELEMETRY
            // =========================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // LEFT: Vertical Audio / Frequency Oscilloscope Scan Band (from reference image)
                Column(
                    modifier = Modifier
                        .width(28.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FREQ",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalBrightGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    TacticalWaveformOscilloscope(
                        isVertical = true,
                        wavePhase = wavePhase,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                    Text(
                        text = "kHz",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalPhosphorGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                // CENTER: Main Circular Proximity Radar Box with Interactive Blips
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(TacticalPanelBg)
                        .border(1.dp, TacticalPhosphorGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Central Radar Polar Grid, Compass Rim & Sweeping Laser Cone
                    TacticalRadarGridCanvas(
                        sweepAngle = sweepAngle,
                        isLocationEnabled = isLocationEnabled,
                        inspectUser = inspectUser,
                        userProfile = userProfile,
                        selectedRadiusKm = selectedRadiusKm,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 2. Center User Anchor (You are Here)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, TacticalBrightGreen, CircleShape)
                            .background(TacticalRadarBg),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(userProfile.avatarUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "You",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )
                    }

                    // 3. Interactive Blips with Tactical Reticles [ • ]
                    if (isLocationEnabled) {
                        val radarRadiusDp = 105.dp // approximate radius of radar inner area

                        if (selectedBlipType == "ALL" || selectedBlipType == "PEOPLE") {
                            filteredUsers.forEach { user ->
                                val bearing = LocationHelper.calculateBearing(
                                    userProfile.latitude, userProfile.longitude,
                                    user.latitude, user.longitude
                                )
                                val angleRad = Math.toRadians(bearing - 90.0)
                                val distanceFraction = (user.distanceKm / selectedRadiusKm).coerceIn(0.18, 0.88)
                                val radiusPx = (radarRadiusDp.value * distanceFraction).toFloat()

                                val xOffsetDp = (radiusPx * cos(angleRad)).toInt().dp
                                val yOffsetDp = (radiusPx * sin(angleRad)).toInt().dp

                                // Beam passing check for sonar ping glow
                                val normalizedSweep = (sweepAngle + 360f) % 360f
                                val normalizedBearing = bearing.toFloat()
                                val isBeamOver = abs(normalizedSweep - normalizedBearing) < 22f ||
                                        abs(normalizedSweep - normalizedBearing - 360f) < 22f
                                val isTargetSelected = inspectUser?.username == user.username

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .offset(x = xOffsetDp, y = yOffsetDp)
                                        .clickable {
                                            inspectUser = if (inspectUser?.username == user.username) null else user
                                            inspectPost = null
                                        }
                                        .testTag("radar_tactical_user_${user.username}")
                                ) {
                                    // Tactical Aircraft Fighter Icon (oriented along bearing as in reference image)
                                    Icon(
                                        imageVector = Icons.Default.Flight,
                                        contentDescription = "Tactical Aircraft",
                                        tint = if (isTargetSelected) TacticalAlertRed else if (isBeamOver) TacticalBrightGreen else TacticalNeonGreen,
                                        modifier = Modifier
                                            .size(13.dp)
                                            .rotate(bearing.toFloat() - 90f)
                                    )

                                    // Tactical Target Reticle Box [ • ]
                                    Box(
                                        modifier = Modifier.size(if (isBeamOver) 30.dp else 24.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Sci-Fi Bracket Corners Canvas
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val bracketLen = 5.dp.toPx()
                                            val bColor = if (isTargetSelected) TacticalAlertRed
                                            else if (isBeamOver) TacticalBrightGreen
                                            else TacticalPhosphorGreen

                                            // Top-Left corner [
                                            drawLine(bColor, Offset(0f, 0f), Offset(bracketLen, 0f), strokeWidth = 1.2.dp.toPx())
                                            drawLine(bColor, Offset(0f, 0f), Offset(0f, bracketLen), strokeWidth = 1.2.dp.toPx())

                                            // Top-Right corner ]
                                            drawLine(bColor, Offset(size.width, 0f), Offset(size.width - bracketLen, 0f), strokeWidth = 1.2.dp.toPx())
                                            drawLine(bColor, Offset(size.width, 0f), Offset(size.width, bracketLen), strokeWidth = 1.2.dp.toPx())

                                            // Bottom-Left corner [
                                            drawLine(bColor, Offset(0f, size.height), Offset(bracketLen, size.height), strokeWidth = 1.2.dp.toPx())
                                            drawLine(bColor, Offset(0f, size.height), Offset(0f, size.height - bracketLen), strokeWidth = 1.2.dp.toPx())

                                            // Bottom-Right corner ]
                                            drawLine(bColor, Offset(size.width, size.height), Offset(size.width - bracketLen, size.height), strokeWidth = 1.2.dp.toPx())
                                            drawLine(bColor, Offset(size.width, size.height), Offset(size.width, size.height - bracketLen), strokeWidth = 1.2.dp.toPx())
                                        }

                                        // Center Blip Avatar
                                        Box(
                                            modifier = Modifier
                                                .size(if (isBeamOver) 22.dp else 18.dp)
                                                .clip(CircleShape)
                                                .background(TacticalRadarBg)
                                                .border(1.dp, if (isBeamOver) TacticalBrightGreen else TacticalPhosphorGreen, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            AsyncImage(
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(user.avatarUrl)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = user.username,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(if (isBeamOver) 20.dp else 16.dp)
                                                    .clip(CircleShape)
                                            )
                                        }
                                    }

                                    // Mini Tactical Coordinate Tag
                                    Surface(
                                        shape = RoundedCornerShape(2.dp),
                                        color = Color.Black.copy(alpha = 0.85f),
                                        border = BorderStroke(0.6.dp, TacticalPhosphorGreen.copy(alpha = 0.6f)),
                                        modifier = Modifier.padding(top = 1.dp)
                                    ) {
                                        Text(
                                            text = "${String.format("%.1f", user.distanceKm)}k",
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBeamOver) TacticalBrightGreen else TacticalTextGreen,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.padding(horizontal = 2.dp, vertical = 0.5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Nearby Pulse Posts (Cyan/Diamond blips)
                        if (selectedBlipType == "ALL" || selectedBlipType == "POSTS") {
                            filteredPosts.forEach { post ->
                                val dist = post.distanceKm ?: 1.0
                                val pseudoBearing = (post.id.hashCode() % 360).toDouble()
                                val angleRad = Math.toRadians(pseudoBearing - 90.0)
                                val distanceFraction = (dist / selectedRadiusKm).coerceIn(0.20, 0.86)
                                val radiusPx = (radarRadiusDp.value * distanceFraction).toFloat()

                                val xOffsetDp = (radiusPx * cos(angleRad)).toInt().dp
                                val yOffsetDp = (radiusPx * sin(angleRad)).toInt().dp

                                Box(
                                    modifier = Modifier
                                        .offset(x = xOffsetDp, y = yOffsetDp)
                                        .size(16.dp)
                                        .clickable {
                                            inspectPost = post
                                            inspectUser = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val path = Path().apply {
                                            moveTo(size.width / 2f, 0f)
                                            lineTo(size.width, size.height / 2f)
                                            lineTo(size.width / 2f, size.height)
                                            lineTo(0f, size.height / 2f)
                                            close()
                                        }
                                        drawPath(path, color = TacticalBrightGreen)
                                    }
                                    Text("P", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(2.dp))

                // RIGHT: Telemetry Registry Table & Azimuth Compass Dial (from reference image)
                Column(
                    modifier = Modifier
                        .width(78.dp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Miniature Heading Compass Dial
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "AZIMUTH",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            color = TacticalBrightGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        TacticalAzimuthDial(
                            headingDeg = sweepAngle,
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    // Tactical Target Registry Table
                    TacticalTelemetryTable(
                        users = filteredUsers,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                    )

                    // Target Status Alert Box
                    Surface(
                        shape = RoundedCornerShape(2.dp),
                        color = TacticalDarkGreen.copy(alpha = 0.5f),
                        border = BorderStroke(0.8.dp, TacticalPhosphorGreen),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (inspectUser != null) "TARGET LOCKED" else "RADAR SCAN",
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (inspectUser != null) TacticalBrightGreen else TacticalPhosphorGreen,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ====================================================================
            // BOTTOM ROW: EQUALIZER SPECTRUM + CHEVRONS + HORIZONTAL OSCILLOSCOPE
            // ====================================================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Bottom-Left Telemetry Equalizer Bars (from reference image)
                Column(
                    modifier = Modifier
                        .width(105.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SIGNAL SPECTRUM",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalBrightGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    TacticalEqualizerSpectrum(
                        animProgress = eqProgress,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                }

                // Center Direction Chevrons `<<<<<<`
                TacticalDirectionChevrons(
                    isFacingRight = false,
                    chevronCount = 6,
                    modifier = Modifier.size(width = 38.dp, height = 12.dp)
                )

                // Bottom-Right Horizontal Oscilloscope Waveform (from reference image)
                Column(
                    modifier = Modifier
                        .width(115.dp)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "SONAR SCAN BAND",
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalBrightGreen,
                        fontFamily = FontFamily.Monospace
                    )
                    TacticalWaveformOscilloscope(
                        isVertical = false,
                        wavePhase = wavePhase,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // ==================================================================
            // SCAN RANGE PRESETS (1km, 3km, 5km, 10km, 50km, Country, Earth)
            // ==================================================================
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "RANGE SCALE SELECTOR",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalPhosphorGreen,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "CURRENT: ${currentRadiusOption.shortLabel.uppercase()}",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = TacticalBrightGreen,
                        fontFamily = FontFamily.Monospace
                    )
                }

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(RadarRadiusPresets.ALL_OPTIONS) { option ->
                        val isSelected = selectedRadiusKm == option.km
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isSelected) TacticalBrightGreen else TacticalPanelBg,
                            border = BorderStroke(1.dp, if (isSelected) TacticalBrightGreen else TacticalPhosphorGreen.copy(alpha = 0.5f)),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onRadiusChange(option.km) }
                        ) {
                            Text(
                                text = "${option.icon} ${option.shortLabel}",
                                fontSize = 9.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = if (isSelected) Color.Black else TacticalTextGreen,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // ===============================================================
    // TARGET INSPECTION OVERLAY CARD (When a neighbor blip is tapped)
    // ===============================================================
    if (inspectUser != null) {
        val user = inspectUser!!
        val bearing = LocationHelper.calculateBearing(userProfile.latitude, userProfile.longitude, user.latitude, user.longitude)
        val compassDir = LocationHelper.getCompassDirection(bearing)
        val travelTime = LocationHelper.getEstimatedTravelTime(user.distanceKm)

        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = TacticalPanelBg,
            border = BorderStroke(1.5.dp, TacticalBrightGreen),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("tactical_inspected_user_card")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, TacticalBrightGreen, CircleShape)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = user.username,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Column {
                            Text(
                                text = user.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TacticalBrightGreen,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "@${user.username} • ${user.locationName}",
                                fontSize = 10.sp,
                                color = TacticalTextGreen.copy(alpha = 0.8f),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Target bearing & distance telemetry badge
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = TacticalDarkGreen.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, TacticalPhosphorGreen)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "$compassDir ${bearing.toInt()}°",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalBrightGreen,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "${String.format("%.2f", user.distanceKm)} KM",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TacticalTextGreen,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action buttons: Wave, Profile, Dismiss
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onWaveAtUser(user) },
                        colors = ButtonDefaults.buttonColors(containerColor = TacticalBrightGreen),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    ) {
                        Text("👋 Wave", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black, fontFamily = FontFamily.Monospace)
                    }

                    OutlinedButton(
                        onClick = { onUserClick(user) },
                        border = BorderStroke(1.dp, TacticalPhosphorGreen),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    ) {
                        Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TacticalBrightGreen, fontFamily = FontFamily.Monospace)
                    }

                    IconButton(
                        onClick = { inspectUser = null },
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TacticalPhosphorGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
