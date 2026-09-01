package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Navigation
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
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
import androidx.compose.ui.platform.testTag
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
import com.example.ui.theme.LocaliAccentCoral
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal
import com.example.util.LocationHelper
import kotlin.math.*

/**
 * Enhanced Proximity Radar component.
 * Integrates a live styled Google Map of the respected location behind the radar scanning sweep,
 * advanced polar bearing calculations, ping wave proximity highlights, target locks,
 * and scalable range limits from 1 KM up to Country (5,000 KM) and Earth / Global (20,000 KM).
 */
@Composable
fun LiveRadarComponent(
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
    onPulseLinkUser: (OtherUserEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedBlipType by remember { mutableStateOf("ALL") } // "ALL", "PEOPLE", "POSTS"
    var inspectUser by remember { mutableStateOf<OtherUserEntity?>(null) }
    var inspectPost by remember { mutableStateOf<PostEntity?>(null) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var mapTheme by remember { mutableStateOf(MapVisualTheme.DARK_RADAR) }
    var showMapLayer by remember { mutableStateOf(true) }

    // Proximity Radar sweep rotation animation
    val infiniteTransition = rememberInfiniteTransition(label = "RadarSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseScale"
    )

    // Filter items based on selected radius (1km to 20,000km)
    val filteredUsers = remember(nearbyUsers, selectedRadiusKm, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else nearbyUsers.filter { it.distanceKm <= selectedRadiusKm }
    }

    val filteredPosts = remember(nearbyPosts, selectedRadiusKm, isLocationEnabled) {
        if (!isLocationEnabled) emptyList()
        else nearbyPosts.filter { (it.distanceKm ?: 999.0) <= selectedRadiusKm }
    }

    // Determine current radius option info
    val currentRadiusOption = remember(selectedRadiusKm) {
        RadarRadiusPresets.ALL_OPTIONS.firstOrNull { it.km == selectedRadiusKm }
            ?: RadarRadiusOption(selectedRadiusKm, "${selectedRadiusKm.toInt()}km", "${selectedRadiusKm.toInt()} KM", "Custom", "📍")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("live_radar_component")
    ) {
        // Main Proximity Radar Container
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = LocaliDeepNavy,
            shadowElevation = 10.dp,
            border = BorderStroke(1.5.dp, LocaliPrimaryTeal.copy(alpha = 0.45f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header: Title, Telemetry, Map Toggle, Privacy Controls
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
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isLocationEnabled && !isPrivateAccount) LocaliAccentMint else LocaliAccentCoral)
                        )
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = "PROXIMITY RADAR",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    color = Color.White
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = LocaliPrimaryTeal.copy(alpha = 0.3f),
                                    modifier = Modifier.padding(start = 2.dp)
                                ) {
                                    Text(
                                        text = "${currentRadiusOption.icon} ${currentRadiusOption.shortLabel}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = LocaliAccentMint,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (!isLocationEnabled) "Proximity Scan Disabled (Off Grid)"
                                else if (isPrivateAccount) "Ghost Mode • Hidden from Strangers"
                                else "${filteredUsers.size} neighbors • ${filteredPosts.size} pulses (${userProfile.locationName})",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Header Controls: Map Layer Toggle & Privacy Shield
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Map Toggle
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = if (showMapLayer) LocaliPrimaryTeal.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.1f),
                            border = BorderStroke(0.8.dp, if (showMapLayer) LocaliAccentMint else Color.Transparent),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { showMapLayer = !showMapLayer }
                                .testTag("radar_map_layer_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Map,
                                    contentDescription = "Toggle Map",
                                    tint = if (showMapLayer) LocaliAccentMint else Color.White.copy(alpha = 0.6f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (showMapLayer) "MAP ON" else "GRID",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Privacy Button
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color.White.copy(alpha = 0.12f),
                            modifier = Modifier
                                .clip(RoundedCornerShape(100.dp))
                                .clickable { showPrivacyDialog = true }
                                .testTag("radar_privacy_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (!isLocationEnabled || isPrivateAccount) Icons.Outlined.Lock else Icons.Outlined.Public,
                                    contentDescription = "Privacy Shield",
                                    tint = if (!isLocationEnabled || isPrivateAccount) LocaliAccentCoral else LocaliAccentMint,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (!isLocationEnabled) "OFF" else if (isPrivateAccount) "PRIV" else "LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // The Circular Proximity Radar Box with Live Google Map Background
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF04101E))
                        .testTag("radar_canvas_container"),
                    contentAlignment = Alignment.Center
                ) {
                    // 1. Live Google Map Layer directly behind radar
                    if (showMapLayer) {
                        LiveGoogleMapBackground(
                            latitude = userProfile.latitude,
                            longitude = userProfile.longitude,
                            locationName = userProfile.locationName,
                            radiusKm = selectedRadiusKm,
                            mapTheme = mapTheme,
                            onThemeChange = { mapTheme = it },
                            showMapControls = true,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    // 2. Proximity Radar Rings, Sweep Beam, and Target Vectors Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2f, size.height / 2f)
                        val maxRadius = (min(size.width, size.height) / 2f) * 0.88f

                        // Concentric Range Rings (33%, 66%, 100%)
                        val ringColors = listOf(
                            LocaliAccentMint.copy(alpha = 0.18f),
                            LocaliAccentMint.copy(alpha = 0.28f),
                            LocaliAccentMint.copy(alpha = 0.45f)
                        )

                        for (i in 1..3) {
                            val r = maxRadius * (i / 3f)
                            drawCircle(
                                color = ringColors[i - 1],
                                radius = r,
                                center = center,
                                style = Stroke(
                                    width = 1.2.dp.toPx(),
                                    pathEffect = if (i < 3) PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f) else null
                                )
                            )
                        }

                        // Compass Axes Lines (N-S, E-W, NE-SW, NW-SE diagonal vectors)
                        val axisColor = LocaliPrimaryTeal.copy(alpha = 0.22f)
                        drawLine(axisColor, Offset(center.x - maxRadius, center.y), Offset(center.x + maxRadius, center.y), strokeWidth = 1.dp.toPx())
                        drawLine(axisColor, Offset(center.x, center.y - maxRadius), Offset(center.x, center.y + maxRadius), strokeWidth = 1.dp.toPx())

                        val diag = maxRadius * 0.7071f
                        val diagColor = LocaliPrimaryTeal.copy(alpha = 0.12f)
                        drawLine(diagColor, Offset(center.x - diag, center.y - diag), Offset(center.x + diag, center.y + diag), strokeWidth = 0.8.dp.toPx())
                        drawLine(diagColor, Offset(center.x - diag, center.y + diag), Offset(center.x + diag, center.y - diag), strokeWidth = 0.8.dp.toPx())

                        // 3. Dynamic Rotating Sweep Beam with Proximity Sonar Glow
                        if (isLocationEnabled) {
                            val sweepBrush = Brush.sweepGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x00028090),
                                    Color(0x2502C39A),
                                    Color(0x8002C39A)
                                ),
                                center = center
                            )

                            drawArc(
                                brush = sweepBrush,
                                startAngle = sweepAngle,
                                sweepAngle = 75f,
                                useCenter = true,
                                topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                                size = Size(maxRadius * 2, maxRadius * 2)
                            )
                        }

                        // 4. Target Lock Vector Line if user is inspecting a specific blip
                        if (inspectUser != null) {
                            val u = inspectUser!!
                            val bearing = LocationHelper.calculateBearing(userProfile.latitude, userProfile.longitude, u.latitude, u.longitude)
                            val angleRad = Math.toRadians(bearing - 90.0)
                            val fraction = (u.distanceKm / selectedRadiusKm).coerceIn(0.2, 0.92)
                            val targetOffset = Offset(
                                center.x + (maxRadius * fraction * cos(angleRad)).toFloat(),
                                center.y + (maxRadius * fraction * sin(angleRad)).toFloat()
                            )

                            // Glowing vector line to locked target
                            drawLine(
                                color = LocaliAccentCoral,
                                start = center,
                                end = targetOffset,
                                strokeWidth = 2.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                            // Lock Reticle
                            drawCircle(
                                color = LocaliAccentCoral.copy(alpha = 0.35f),
                                radius = 22.dp.toPx(),
                                center = targetOffset
                            )
                            drawCircle(
                                color = LocaliAccentCoral,
                                radius = 24.dp.toPx(),
                                center = targetOffset,
                                style = Stroke(1.5.dp.toPx())
                            )
                        }
                    }

                    // 3. User Central Position (You Are Here Anchor)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .border(2.dp, LocaliAccentMint, CircleShape)
                            .background(LocaliDeepNavy),
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
                                .size(30.dp)
                                .clip(CircleShape)
                        )
                    }

                    // 4. Interactive Proximity Radar Blips
                    if (isLocationEnabled) {
                        // User Blips
                        if (selectedBlipType == "ALL" || selectedBlipType == "PEOPLE") {
                            filteredUsers.forEach { user ->
                                val bearing = LocationHelper.calculateBearing(
                                    userProfile.latitude, userProfile.longitude,
                                    user.latitude, user.longitude
                                )
                                val angleRad = Math.toRadians(bearing - 90.0)
                                val distanceFraction = (user.distanceKm / selectedRadiusKm).coerceIn(0.2, 0.90)
                                val radiusPx = (112.dp.value * distanceFraction).toFloat()

                                val xOffsetDp = (radiusPx * cos(angleRad)).toInt().dp
                                val yOffsetDp = (radiusPx * sin(angleRad)).toInt().dp

                                // Check if sweep beam is actively passing over this entity (sonar ping effect)
                                val normalizedSweep = (sweepAngle + 360f) % 360f
                                val normalizedBearing = bearing.toFloat()
                                val isBeamOver = abs(normalizedSweep - normalizedBearing) < 25f ||
                                                 abs(normalizedSweep - normalizedBearing - 360f) < 25f

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .offset(x = xOffsetDp, y = yOffsetDp)
                                        .clickable {
                                            inspectUser = user
                                            inspectPost = null
                                        }
                                        .testTag("radar_user_blip_${user.username}")
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(if (isBeamOver) 34.dp else 28.dp)
                                            .clip(CircleShape)
                                            .border(
                                                width = if (isBeamOver) 2.5.dp else if (user.isFriend) 1.8.dp else 1.2.dp,
                                                color = if (isBeamOver) Color.White else if (user.isFriend) LocaliAccentMint else LocaliAccentCoral,
                                                shape = CircleShape
                                            )
                                            .background(LocaliDeepNavy),
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
                                                .size(if (isBeamOver) 28.dp else 22.dp)
                                                .clip(CircleShape)
                                        )
                                    }

                                    // Distance Mini-Badge under blip
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color.Black.copy(alpha = 0.75f),
                                        modifier = Modifier.padding(top = 1.dp)
                                    ) {
                                        Text(
                                            text = LocationHelper.formatDistanceLabel(user.distanceKm).replace(" away", ""),
                                            fontSize = 7.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isBeamOver) LocaliAccentMint else Color.White,
                                            modifier = Modifier.padding(horizontal = 3.dp, vertical = 0.5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Moment / Post Blips
                        if (selectedBlipType == "ALL" || selectedBlipType == "POSTS") {
                            filteredPosts.forEach { post ->
                                val postLat = post.latitude ?: (userProfile.latitude + 0.005)
                                val postLng = post.longitude ?: (userProfile.longitude + 0.005)
                                val bearing = LocationHelper.calculateBearing(
                                    userProfile.latitude, userProfile.longitude,
                                    postLat, postLng
                                )
                                val angleRad = Math.toRadians(bearing - 90.0)
                                val dist = post.distanceKm ?: 1.0
                                val distanceFraction = (dist / selectedRadiusKm).coerceIn(0.25, 0.90)
                                val radiusPx = (112.dp.value * distanceFraction).toFloat()

                                val xOffsetDp = (radiusPx * cos(angleRad)).toInt().dp
                                val yOffsetDp = (radiusPx * sin(angleRad)).toInt().dp

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = LocaliPrimaryTeal,
                                    border = BorderStroke(1.dp, Color.White),
                                    modifier = Modifier
                                        .offset(x = xOffsetDp, y = yOffsetDp)
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .clickable {
                                            inspectPost = post
                                            inspectUser = null
                                        }
                                        .testTag("radar_post_blip_${post.id}")
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(post.mediaUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = post.caption,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    } else {
                        // Location Disabled State Banner
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.Black.copy(alpha = 0.85f),
                            border = BorderStroke(1.dp, LocaliAccentCoral),
                            modifier = Modifier.padding(20.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(14.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOff,
                                    contentDescription = null,
                                    tint = LocaliAccentCoral,
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Location Radar Disabled",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "Enable location sharing to scan proximity beacons and explore live community pulses.",
                                    fontSize = 10.5.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onLocationToggle(true) },
                                    colors = ButtonDefaults.buttonColors(containerColor = LocaliPrimaryTeal),
                                    shape = RoundedCornerShape(100.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Turn On Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Cardinal Direction Points (N, S, E, W)
                    Text(
                        text = "N 0°",
                        color = LocaliAccentMint.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 4.dp)
                    )
                    Text(
                        text = "S 180°",
                        color = LocaliAccentMint.copy(alpha = 0.6f),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                    )
                    Text(
                        text = "E 90°",
                        color = LocaliAccentMint.copy(alpha = 0.6f),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 4.dp)
                    )
                    Text(
                        text = "W 270°",
                        color = LocaliAccentMint.copy(alpha = 0.6f),
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Extended KM Options Slider / Chips (1km, 3km, 5km, 10km, 50km, 100km, 500km, 1000km, Country, Earth)
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SCAN RANGE SCALE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f),
                            letterSpacing = 0.5.sp
                        )

                        // Filter Blip Type: ALL, PEOPLE, POSTS
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            listOf("ALL", "PEOPLE", "POSTS").forEach { type ->
                                val isSelected = selectedBlipType == type
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isSelected) LocaliPrimaryTeal else Color.White.copy(alpha = 0.08f),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(100.dp))
                                        .clickable { selectedBlipType = type }
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.5.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Horizontally scrollable extended range pills (1km up to Country & Earth)
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("radar_radius_preset_row"),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(RadarRadiusPresets.ALL_OPTIONS) { option ->
                            val isSelected = selectedRadiusKm == option.km
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) LocaliAccentMint else Color.White.copy(alpha = 0.12f),
                                border = if (isSelected) BorderStroke(1.dp, Color.White) else null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { onRadiusChange(option.km) }
                                    .testTag("radar_radius_${option.km.toInt()}km")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(option.icon, fontSize = 10.sp)
                                    Text(
                                        text = option.shortLabel,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                        color = if (isSelected) LocaliDeepNavy else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Proximity Target Lock & Inspection HUD Card (When a user blip is tapped)
        if (inspectUser != null) {
            val user = inspectUser!!
            val bearing = LocationHelper.calculateBearing(userProfile.latitude, userProfile.longitude, user.latitude, user.longitude)
            val compassDir = LocationHelper.getCompassDirection(bearing)
            val travelTime = LocationHelper.getEstimatedTravelTime(user.distanceKm)

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.5.dp, LocaliAccentMint),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("radar_inspected_user_card")
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Top Row: User Avatar, Name, Distance & Bearing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(user.avatarUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = user.username,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, LocaliAccentMint, CircleShape)
                            )
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = user.fullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (user.isVerified) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Verified",
                                            tint = LocaliPrimaryTeal,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "@${user.username} • ${user.landmark ?: user.locationName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Proximity Telemetry Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = LocaliPrimaryTeal.copy(alpha = 0.18f),
                            border = BorderStroke(1.dp, LocaliAccentMint.copy(alpha = 0.5f))
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = "🧭 $compassDir ${bearing.toInt()}°",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaliAccentMint
                                )
                                Text(
                                    text = LocationHelper.formatDistanceLabel(user.distanceKm),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Proximity Details & Estimated Travel Bar
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Navigation,
                                    contentDescription = "Navigation",
                                    tint = LocaliPrimaryTeal,
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "Est. travel: $travelTime",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Open Directions in Google Maps
                            Text(
                                text = "Open in Maps ↗",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocaliPrimaryTeal,
                                modifier = Modifier.clickable {
                                    try {
                                        val uri = Uri.parse("google.navigation:q=${user.latitude},${user.longitude}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, uri).apply {
                                            setPackage("com.google.android.apps.maps")
                                        }
                                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(mapIntent)
                                        } else {
                                            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${user.latitude},${user.longitude}")
                                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                        }
                                    } catch (_: Exception) {
                                        val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${user.latitude},${user.longitude}")
                                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Action Buttons (Wave, Pulse Link, View Profile)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { onWaveAtUser(user) },
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(34.dp)
                        ) {
                            Text("Wave 👋", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onPulseLinkUser(user) },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (user.isFollowing) LocaliAccentMint.copy(alpha = 0.25f) else LocaliPrimaryTeal,
                                contentColor = if (user.isFollowing) LocaliAccentMint else Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(34.dp)
                        ) {
                            Text(
                                text = if (user.isFollowing) "In Orbit ⚡" else "+ Pulse Link",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                onUserClick(user)
                                inspectUser = null
                            },
                            shape = RoundedCornerShape(100.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(34.dp)
                        ) {
                            Text("Profile", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Proximity Post Inspection Card (When a post blip is tapped)
        if (inspectPost != null) {
            val post = inspectPost!!
            val postLat = post.latitude ?: userProfile.latitude
            val postLng = post.longitude ?: userProfile.longitude
            val bearing = LocationHelper.calculateBearing(userProfile.latitude, userProfile.longitude, postLat, postLng)
            val compassDir = LocationHelper.getCompassDirection(bearing)

            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                border = BorderStroke(1.5.dp, LocaliAccentMint),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onPostClick(post)
                        inspectPost = null
                    }
                    .testTag("radar_inspected_post_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(54.dp)
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(post.mediaUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = post.caption,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "📍 ${post.landmark ?: post.location ?: "Nearby"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "($compassDir • ${LocationHelper.formatDistanceLabel(post.distanceKm)})",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = post.caption,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open Pulse",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }

    // Comprehensive Privacy & Location Controls Modal Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Radar & Privacy Settings",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "You have full control over your visibility on Locali's Proximity Radar and nearby discovery feed.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                    // 1. Enable / Disable Location Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Live Location Radar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Scan nearby creators, landmarks, and pulses within your selected radius up to Earth scale.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isLocationEnabled,
                            onCheckedChange = { onLocationToggle(it) },
                            modifier = Modifier.testTag("radar_location_switch")
                        )
                    }

                    // 2. Private / Ghost Mode Setting
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Private Account (Ghost Mode)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Hide your profile from strangers on the public radar. Only approved friends can see your distance.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isPrivateAccount,
                            onCheckedChange = { onPrivateToggle(it) },
                            modifier = Modifier.testTag("radar_private_account_switch")
                        )
                    }

                    HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

                    OutlinedButton(
                        onClick = {
                            showPrivacyDialog = false
                            onOpenPrivacySettings()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("radar_manage_privacy_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("More Privacy & Security Settings", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showPrivacyDialog = false },
                    modifier = Modifier.testTag("radar_privacy_confirm_button")
                ) {
                    Text("Done", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        )
    }
}
