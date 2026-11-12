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
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.ui.draw.rotate
import coil.compose.AsyncImage
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.ui.components.radar.*
import com.example.util.HapticHelper
import com.example.util.LocationHelper
import com.example.util.ShareHelper
import kotlin.math.cos
import kotlin.math.roundToInt
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
    radarObfuscatedRange: String = "3KM",
    radarCountryName: String = "India",
    detectedStateName: String? = null,
    isPremiumSubscribed: Boolean = false,
    selectedHudThemeName: String = "PHOSPHOR_GREEN",
    onSelectHudTheme: (String) -> Unit = {},
    onUnlockPremiumClick: () -> Unit = {},
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
    val context = LocalContext.current
    val initialGeo = remember(userProfile.latitude, userProfile.longitude, userProfile.locationName, radarCountryName, detectedStateName) {
        val inferred = LocationHelper.inferStateAndCountryFromCoordinates(
            lat = userProfile.latitude,
            lng = userProfile.longitude,
            locationName = userProfile.locationName,
            fallbackCountry = radarCountryName
        )
        Pair(detectedStateName?.takeIf { it.isNotBlank() } ?: inferred.first, inferred.second)
    }
    var resolvedStateName by remember(initialGeo) { mutableStateOf(initialGeo.first) }
    var resolvedCountryName by remember(initialGeo) { mutableStateOf(initialGeo.second) }

    LaunchedEffect(userProfile.latitude, userProfile.longitude, userProfile.locationName, radarCountryName) {
        val pair = LocationHelper.resolveStateAndCountry(
            context = context,
            lat = userProfile.latitude,
            lng = userProfile.longitude,
            locationName = userProfile.locationName,
            fallbackCountry = radarCountryName
        )
        if (detectedStateName.isNullOrBlank()) {
            resolvedStateName = pair.first
        }
        resolvedCountryName = pair.second
    }

    val isPremium = isPremiumSubscribed
    val restoredTheme = remember(selectedHudThemeName) {
        RadarHudTheme.entries.firstOrNull { it.name.equals(selectedHudThemeName, ignoreCase = true) }
            ?: RadarHudTheme.PHOSPHOR_GREEN
    }
    var selectedTheme by remember(restoredTheme) { mutableStateOf(restoredTheme) }
    var signalAuraEnabled by remember(isPremium) { mutableStateOf(isPremium) }
    val currentHudTheme = if (selectedTheme.isPremiumOnly && !isPremium) RadarHudTheme.PHOSPHOR_GREEN else selectedTheme
    val radarColor = currentHudTheme.primaryColor
    val gridColor = currentHudTheme.gridColor

    val currentDistanceOption = remember(selectedRadiusKm, resolvedCountryName, resolvedStateName) {
        SystematicDistanceScale.findOption(
            km = selectedRadiusKm,
            countryName = resolvedCountryName,
            stateName = resolvedStateName
        )
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

    var isGhostActive by remember(hidePreciseLocationOnRadar) { mutableStateOf(hidePreciseLocationOnRadar) }
    var showQrIdentityDialog by remember { mutableStateOf(false) }
    var qrSelectedUsername by remember { mutableStateOf(userProfile.username) }
    var qrSelectedFullName by remember { mutableStateOf(userProfile.fullName) }
    var broadcastingRadius by remember(radarObfuscatedRange) { mutableStateOf(radarObfuscatedRange) }
    var is3DPerspective by remember { mutableStateOf(false) }
    var isDayTheme by remember { mutableStateOf(false) }
    var isBatterySaver by remember { mutableStateOf(false) }
    var isRangeBoostActive by remember { mutableStateOf(false) }
    var isBroadcastEnhanceActive by remember { mutableStateOf(false) }
    var isManualSonarActive by remember { mutableStateOf(false) }
    var beaconUpdateIntervalSeconds by remember { mutableIntStateOf(3) }
    var selectedMapOverlay by remember { mutableStateOf("Grid") } // Grid, Topo, Heatmap
    var showSafetyAlerts by remember { mutableStateOf(true) }
    var showOnlyVerifiedResidents by remember { mutableStateOf(false) }
    var isKalmanFilterActive by remember { mutableStateOf(true) }
    var dispatchUserTarget by remember { mutableStateOf<OtherUserEntity?>(null) }

    // Live Compass Heading (SensorManager + Live Radar Azimuth Tracking so it is never stuck on E86°)
    var sensorAzimuthDeg by remember { mutableStateOf<Float?>(null) }
    var hasDynamicSensorMovement by remember { mutableStateOf(false) }
    var isHeadingLocked by remember { mutableStateOf(false) }
    var manualCalibrationOffsetDeg by remember { mutableIntStateOf(0) }
    var lockedHeadingDeg by remember { mutableIntStateOf(0) }

    DisposableEffect(context) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        val gravity = FloatArray(3)
        val geomagnetic = FloatArray(3)
        var hasGravity = false
        var hasMag = false
        var initialAzimuth: Float? = null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                when (event.sensor.type) {
                    Sensor.TYPE_ROTATION_VECTOR -> {
                        val rotationMatrix = FloatArray(9)
                        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(rotationMatrix, orientation)
                        val deg = ((Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f)
                        if (initialAzimuth == null) {
                            initialAzimuth = deg
                        } else if (kotlin.math.abs(deg - initialAzimuth!!) > 2.5f) {
                            hasDynamicSensorMovement = true
                        }
                        sensorAzimuthDeg = deg
                    }
                    Sensor.TYPE_ACCELEROMETER -> {
                        System.arraycopy(event.values, 0, gravity, 0, 3)
                        hasGravity = true
                    }
                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                        hasMag = true
                    }
                }
                if (hasGravity && hasMag && event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        val deg = ((Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f)
                        if (initialAzimuth == null) {
                            initialAzimuth = deg
                        } else if (kotlin.math.abs(deg - initialAzimuth!!) > 2.5f) {
                            hasDynamicSensorMovement = true
                        }
                        sensorAzimuthDeg = deg
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            if (accelSensor != null) sensorManager?.registerListener(listener, accelSensor, SensorManager.SENSOR_DELAY_UI)
            if (magSensor != null) sensorManager?.registerListener(listener, magSensor, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val liveHeadingDeg = remember(sweepAngle, sensorAzimuthDeg, hasDynamicSensorMovement, isHeadingLocked, manualCalibrationOffsetDeg, lockedHeadingDeg) {
        val rawDeg = when {
            isHeadingLocked -> lockedHeadingDeg
            hasDynamicSensorMovement && sensorAzimuthDeg != null -> sensorAzimuthDeg!!.roundToInt()
            else -> sweepAngle.roundToInt()
        }
        ((rawDeg + manualCalibrationOffsetDeg) % 360 + 360) % 360
    }

    val liveCardinalDirection = remember(liveHeadingDeg) {
        when (((liveHeadingDeg + 22) % 360) / 45) {
            0 -> "N"
            1 -> "NE"
            2 -> "E"
            3 -> "SE"
            4 -> "S"
            5 -> "SW"
            6 -> "W"
            else -> "NW"
        }
    }
    val formattedHeadingText = remember(liveCardinalDirection, liveHeadingDeg, isHeadingLocked) {
        val degStr = String.format("%03d°", liveHeadingDeg)
        if (isHeadingLocked) "LOCKED: $liveCardinalDirection $degStr" else "HEADING: $liveCardinalDirection $degStr"
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.Black)
    ) {
        // =====================================================================
        // 1. ACTIVE RADAR PERK CARD (Positioned at top)
        // =====================================================================
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF021B2B),
            border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .testTag("active_radar_perk_card")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Active Perk",
                    tint = if (isRangeBoostActive) Color(0xFF00E5FF) else Color(0xFFFFD700),
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isRangeBoostActive) "RF AMPLIFIER BOOST • +10 KM ACTIVE" else "ACTIVE RADAR PERK • 19h remaining",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = if (isRangeBoostActive) "High-gain radar transponder active (+10km coverage radius)" else "Signal Aura Glow (+25%): Brightens your proximity nodes",
                        fontSize = 9.sp,
                        color = Color.LightGray
                    )
                }
            }
        }

        // =====================================================================
        // 2. ACTIONABLE CONTROL LINE 1 (Shifted Directly ABOVE Radar Canvas)
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Button 1: 2D TOP-DOWN / 3D PITCH 45° Toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (is3DPerspective) RadarPhosphor.copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (is3DPerspective) RadarNeonGreen else Color(0xFF263238)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        is3DPerspective = !is3DPerspective
                        Toast.makeText(context, if (is3DPerspective) "3D Perspective View (45° Pitch)" else "2D Top-Down Tactical View", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_perspective_mode_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (is3DPerspective) Icons.Default.ViewInAr else Icons.Default.Layers,
                        contentDescription = "Perspective",
                        tint = if (is3DPerspective) RadarNeonGreen else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (is3DPerspective) "3D PITCH 45°" else "2D TOP-DOWN",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (is3DPerspective) RadarNeonGreen else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 2: Manual Sonar Pulse Trigger
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isManualSonarActive) Color(0xFF00E5FF).copy(alpha = 0.3f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isManualSonarActive) Color(0xFF00E5FF) else Color(0xFF00838F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        isManualSonarActive = true
                        Toast.makeText(context, "📡 Manual Sonar Pulse broadcasted across 50 KM!", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_manual_sonar_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Sonar Pulse",
                        tint = if (isManualSonarActive) Color(0xFF00E5FF) else Color(0xFF4DD0E1),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isManualSonarActive) "PING BROADCASTED" else "SONAR PULSE",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isManualSonarActive) Color(0xFF00E5FF) else Color(0xFF4DD0E1),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 3: Range Booster (+10km Perk)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isRangeBoostActive) Color(0xFFFFD700).copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isRangeBoostActive) Color(0xFFFFD700) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        isRangeBoostActive = !isRangeBoostActive
                        val nextR = if (isRangeBoostActive) (selectedRadiusKm + 10.0).coerceAtMost(50.0) else (selectedRadiusKm - 10.0).coerceAtLeast(1.0)
                        onRadiusChange(nextR)
                        Toast.makeText(context, if (isRangeBoostActive) "⚡ Range Boost Active: +10km RF range" else "Standard RF Range", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_range_booster_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "Range Boost",
                        tint = if (isRangeBoostActive) Color(0xFFFFD700) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isRangeBoostActive) "RANGE BOOST (+10km)" else "+10km BOOST",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isRangeBoostActive) Color(0xFFFFD700) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 4: Far-Reach Privacy Shield (Reveals KM Distance Options)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isPrivateAccount || isGhostActive) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isPrivateAccount || isGhostActive) Color(0xFF00E5FF) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        if (isPrivateAccount) {
                            Toast.makeText(context, "👻 Ghost Mode Active: Transmitting & receiving user details disabled", Toast.LENGTH_SHORT).show()
                        } else {
                            HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                            isGhostActive = !isGhostActive
                            onToggleHidePreciseLocation?.invoke(isGhostActive)
                            Toast.makeText(
                                context,
                                if (isGhostActive) "📡 Far-Reach Transmit ON: Visible farther than $broadcastingRadius"
                                else "📡 Signal Transmitting: Visible to all radars",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .testTag("radar_privacy_shield_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isPrivateAccount) Icons.Default.VisibilityOff else if (isGhostActive) Icons.Default.Radar else Icons.Default.Sensors,
                        contentDescription = "Far-Reach Transmit",
                        tint = if (isPrivateAccount || isGhostActive) Color(0xFF00E5FF) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isPrivateAccount) "👻 GHOST MODE" else if (isGhostActive) "FAR-REACH: $broadcastingRadius" else "TRANSMIT: ALL",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isPrivateAccount || isGhostActive) Color(0xFF00E5FF) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 5: Signal Transmit Enhancement
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isBroadcastEnhanceActive) Color(0xFF10B981).copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isBroadcastEnhanceActive) Color(0xFF10B981) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        isBroadcastEnhanceActive = !isBroadcastEnhanceActive
                        Toast.makeText(context, if (isBroadcastEnhanceActive) "📶 Ultra-Wide Signal Transmit Active" else "Standard Signal Transmit", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_broadcast_signal_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Podcasts,
                        contentDescription = "Signal Transmit",
                        tint = if (isBroadcastEnhanceActive) Color(0xFF10B981) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isBroadcastEnhanceActive) "SIGNAL ULTRA" else "SIGNAL BOOST",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBroadcastEnhanceActive) Color(0xFF10B981) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 6: Night / Day Tactical HUD
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isDayTheme) Color(0xFFE2E8F0) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isDayTheme) Color(0xFF0F766E) else RadarPhosphor.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clickable {
                        isDayTheme = !isDayTheme
                        Toast.makeText(context, if (isDayTheme) "Day Tactical HUD" else "Night Phosphor HUD", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_hud_theme_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
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
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDayTheme) Color(0xFF0F766E) else RadarPhosphor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 7: Battery Saver Toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isBatterySaver) Color(0xFFEAB308).copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isBatterySaver) Color(0xFFEAB308) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        isBatterySaver = !isBatterySaver
                        Toast.makeText(context, if (isBatterySaver) "Battery Saver: Scan rate halved" else "Normal Radar Sweep Rate", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_battery_saver_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.BatterySaver,
                        contentDescription = "Battery Saver",
                        tint = if (isBatterySaver) Color(0xFFEAB308) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isBatterySaver) "ECO SCAN" else "FULL POWER",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isBatterySaver) Color(0xFFEAB308) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // =====================================================================
        // 3. ACTIONABLE CONTROL LINE 2 (Telemetry & Overlays, Shifted ABOVE Canvas)
        // =====================================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Button 1: Live Compass Heading (Dynamic Sensor + Sweep Azimuth, Tappable to Lock/Calibrate)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isHeadingLocked) Color(0xFF00E5FF).copy(alpha = 0.22f) else Color(0xFF0A121D),
                border = BorderStroke(1.dp, if (isHeadingLocked) Color(0xFF00E5FF) else Color(0xFF00E5FF).copy(alpha = 0.4f)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        if (!isHeadingLocked) {
                            lockedHeadingDeg = liveHeadingDeg
                            isHeadingLocked = true
                            Toast.makeText(context, "🧭 Compass locked at $liveCardinalDirection ${String.format("%03d°", liveHeadingDeg)} (Tap again for live tracking)", Toast.LENGTH_SHORT).show()
                        } else {
                            isHeadingLocked = false
                            manualCalibrationOffsetDeg = (manualCalibrationOffsetDeg + 45) % 360
                            Toast.makeText(context, "🧭 Live Gyro & Radar Azimuth Tracking Active", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .testTag("radar_heading_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Heading",
                        tint = Color(0xFF00E5FF),
                        modifier = Modifier
                            .size(13.dp)
                            .rotate(liveHeadingDeg.toFloat())
                    )
                    Text(
                        text = formattedHeadingText,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 2: Dynamic Beacon Update Interval (Cycles 1s, 3s, 5s, 10s)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0A121D),
                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        beaconUpdateIntervalSeconds = when (beaconUpdateIntervalSeconds) {
                            1 -> 3
                            3 -> 5
                            5 -> 10
                            else -> 1
                        }
                        Toast.makeText(context, "Beacon Refresh Rate: ${beaconUpdateIntervalSeconds}s interval", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_beacon_interval_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Update,
                        contentDescription = "Update Interval",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "INTERVAL: ${beaconUpdateIntervalSeconds}s",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 3: Map Overlays Toggle (Grid, Topo, Heatmap)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0A121D),
                border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        selectedMapOverlay = when (selectedMapOverlay) {
                            "Grid" -> "Topo"
                            "Topo" -> "Heatmap"
                            else -> "Grid"
                        }
                        Toast.makeText(context, "Tactical Overlay: $selectedMapOverlay Mode", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_map_overlay_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = "Map Overlay",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "MAP: $selectedMapOverlay",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF38BDF8),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 4: Crowdsourced Safety Alerts Overlay Toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (showSafetyAlerts) Color(0xFFEF4444).copy(alpha = 0.2f) else Color(0xFF0A121D),
                border = BorderStroke(1.dp, if (showSafetyAlerts) Color(0xFFEF4444) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        showSafetyAlerts = !showSafetyAlerts
                        Toast.makeText(context, if (showSafetyAlerts) "🚨 Safety Alerts Overlay: 3 nearby hazards visible" else "Safety Hazard Overlay Hidden", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_safety_alerts_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Safety Alerts",
                        tint = if (showSafetyAlerts) Color(0xFFEF4444) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (showSafetyAlerts) "ALERTS: 3 PINS" else "ALERTS OFF",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showSafetyAlerts) Color(0xFFEF4444) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 5: Verified Resident Markers Only
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (showOnlyVerifiedResidents) Color(0xFF3B82F6).copy(alpha = 0.25f) else Color(0xFF0A121D),
                border = BorderStroke(1.dp, if (showOnlyVerifiedResidents) Color(0xFF3B82F6) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        showOnlyVerifiedResidents = !showOnlyVerifiedResidents
                        Toast.makeText(context, if (showOnlyVerifiedResidents) "Showing Verified Resident Blips Only" else "Showing All Neighbor Blips", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_verified_residents_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified Residents",
                        tint = if (showOnlyVerifiedResidents) Color(0xFF3B82F6) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (showOnlyVerifiedResidents) "VERIFIED ONLY" else "ALL RESIDENTS",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showOnlyVerifiedResidents) Color(0xFF3B82F6) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 6: Active Nodes Counter
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0A121D),
                border = BorderStroke(1.dp, Color(0xFF8B5CF6).copy(alpha = 0.45f)),
                modifier = Modifier.testTag("radar_nodes_counter_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Active Nodes",
                        tint = Color(0xFF8B5CF6),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "${nearbyUsers.size + nearbyPosts.size} NODES",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 7: Kalman Filter Toggle
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isKalmanFilterActive) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF0A121D),
                border = BorderStroke(1.dp, if (isKalmanFilterActive) Color(0xFF10B981) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        isKalmanFilterActive = !isKalmanFilterActive
                        Toast.makeText(context, if (isKalmanFilterActive) "Kalman GPS smoothing filter enabled" else "Raw GPS coordinates mode", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_kalman_filter_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoMode,
                        contentDescription = "Kalman Filter",
                        tint = if (isKalmanFilterActive) Color(0xFF10B981) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isKalmanFilterActive) "KALMAN: ON" else "KALMAN: RAW",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isKalmanFilterActive) Color(0xFF10B981) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // =====================================================================
        // 3.5 QUICK RANGE & DETECTED STATE/COUNTRY SELECTOR BAR ABOVE RADAR
        // =====================================================================
        val quickRangeItems = remember(resolvedStateName, resolvedCountryName) {
            listOf(
                Pair("1km", 1.0),
                Pair("3km", 3.0),
                Pair("5km", 5.0),
                Pair("10km", 10.0),
                Pair("50km", 50.0),
                Pair("🏛️ $resolvedStateName", 500.0),
                Pair("🏳️ $resolvedCountryName", 5000.0),
                Pair("🌍 Earth", 20000.0)
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 3.dp)
                .testTag("radar_quick_range_bar"),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF061B10),
                border = BorderStroke(0.8.dp, radarColor.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "RANGE:",
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black,
                    color = radarColor,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            quickRangeItems.forEach { (label, kmVal) ->
                val isSelected = kotlin.math.abs(selectedRadiusKm - kmVal) < 0.6 ||
                    (kmVal >= 500.0 && kotlin.math.abs(selectedRadiusKm - kmVal) < 400.0)
                val isLocked = kmVal <= 10.0 && !isPremium
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) radarColor.copy(alpha = 0.25f) else Color(0xFF0A121D),
                    border = BorderStroke(
                        1.dp,
                        if (isSelected) radarColor else if (isLocked) Color(0xFFFFD700).copy(alpha = 0.5f) else radarColor.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                        if (isLocked) {
                            Toast.makeText(context, "👑 Titan Premium: Unlock $label Hyperlocal Radar Precision!", Toast.LENGTH_SHORT).show()
                            onUnlockPremiumClick()
                        } else {
                            onRadiusChange(kmVal)
                        }
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            text = if (isLocked) "$label 👑" else label,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                            color = if (isSelected) radarColor else if (isLocked) Color(0xFFFFD700) else Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // =====================================================================
        // 4. ENERGETIC TACTICAL RADAR VIEWPORT + SIDE TELEMETRY HUD OVERLAYS
        // =====================================================================
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            val radarDiameterDp = minOf(maxWidth - 20.dp, 316.dp)
            val density = LocalDensity.current
            val radarRadiusPx = with(density) { (radarDiameterDp / 2).toPx() }
            val centerPx = radarRadiusPx
            val bezelThicknessPx = with(density) { 22.dp.toPx() }
            val sweepRadiusPx = radarRadiusPx - bezelThicknessPx

            // Available users & market items within selected KM range, sorted strictly by KM (nearest first)
            val kmSortedUsersInRange = remember(nearbyUsers, selectedRadiusKm, isLocationEnabled, isPrivateAccount, showOnlyVerifiedResidents) {
                if (!isLocationEnabled || isPrivateAccount) emptyList()
                else {
                    val base = if (showOnlyVerifiedResidents) nearbyUsers.filter { it.isVerified } else nearbyUsers
                    base.filter { it.distanceKm <= selectedRadiusKm }.sortedBy { it.distanceKm }
                }
            }
            val kmSortedMarketInRange = remember(nearbyMarketItems, selectedRadiusKm, isLocationEnabled) {
                if (!isLocationEnabled) emptyList()
                else nearbyMarketItems.filter { (it.distanceKm ?: 1.2) <= selectedRadiusKm }.sortedBy { it.distanceKm ?: 1.2 }
            }

            // Combine KM-sorted users, posts, market items, and clips for sonar node reveal
            val combinedItems = remember(kmSortedUsersInRange, kmSortedMarketInRange, nearbyPosts, nearbyClips, isLocationEnabled, selectedRadiusKm) {
                if (!isLocationEnabled) emptyList<Any>()
                else {
                    buildList<Any> {
                        addAll(kmSortedUsersInRange.take(8))
                        addAll(nearbyPosts.filter { (it.distanceKm ?: 1.0) <= selectedRadiusKm }.take(4))
                        addAll(kmSortedMarketInRange.take(3))
                        addAll(nearbyClips.filter { (it.distanceKm ?: 1.0) <= selectedRadiusKm }.take(2))
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // UNCLUTTERED CENTER CIRCULAR RADAR DISPLAY (No overlapping corner boxes)
                Box(
                    modifier = Modifier
                        .size(radarDiameterDp)
                        .clip(CircleShape)
                        .background(if (isDayTheme) Color(0xFF042212) else Color(0xFF020E04))
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
                        val sweepRadius = outerRimRadius - 22.dp.toPx()

                        // Outer Bezel Rim
                        drawCircle(color = if (isDayTheme) Color(0xFF042212) else Color(0xFF020E04), radius = outerRimRadius, center = center)
                        drawCircle(
                            color = radarColor.copy(alpha = 0.55f),
                            radius = outerRimRadius - 1.5.dp.toPx(),
                            center = center,
                            style = Stroke(width = 1.6.dp.toPx())
                        )

                        // 36 Azimuth Degree Tick Marks around the Outer Bezel
                        for (deg in 0 until 360 step 10) {
                            val rad = Math.toRadians(deg.toDouble())
                            val isMajor = deg % 90 == 0
                            val tickOuter = outerRimRadius - 3.dp.toPx()
                            val tickInner = if (isMajor) sweepRadius else outerRimRadius - 8.dp.toPx()
                            drawLine(
                                color = if (isMajor) radarColor else radarColor.copy(alpha = 0.35f),
                                start = Offset(center.x + tickInner * cos(rad).toFloat(), center.y + tickInner * sin(rad).toFloat()),
                                end = Offset(center.x + tickOuter * cos(rad).toFloat(), center.y + tickOuter * sin(rad).toFloat()),
                                strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                            )
                        }

                        // Inner Sweep Border Ring
                        drawCircle(
                            color = radarColor.copy(alpha = 0.85f),
                            radius = sweepRadius,
                            center = center,
                            style = Stroke(width = 1.8.dp.toPx())
                        )

                        // Polar Acoustic Radial Spokes (45° intervals)
                        for (deg in 0 until 360 step 45) {
                            val rad = Math.toRadians(deg.toDouble())
                            val spokeEndX = center.x + sweepRadius * cos(rad).toFloat()
                            val spokeEndY = center.y + sweepRadius * sin(rad).toFloat()
                            drawLine(
                                color = radarColor.copy(alpha = 0.16f),
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
                                color = radarColor.copy(alpha = 0.24f),
                                radius = r,
                                center = center,
                                style = Stroke(width = 1.dp.toPx())
                            )

                            val km = currentMaxKm * frac
                            val label = when {
                                km >= 1000 -> String.format("%.0fK", km / 1000)
                                km >= 10 -> String.format("%.0fk", km)
                                else -> String.format("%.1fk", km)
                            }
                            val ringDistPaint = android.graphics.Paint().apply {
                                color = android.graphics.Color.parseColor(if (isPremium) "#FFD700" else "#00FF41")
                                textSize = 7.5.dp.toPx()
                                textAlign = android.graphics.Paint.Align.CENTER
                                typeface = android.graphics.Typeface.MONOSPACE
                                isAntiAlias = true
                                alpha = 215
                            }
                            drawContext.canvas.nativeCanvas.drawText(label, center.x + 14.dp.toPx(), center.y - r + 9.dp.toPx(), ringDistPaint)
                        }

                        // Expanding Pulse Shockwave
                        drawCircle(
                            color = radarColor.copy(alpha = 0.4f * (1f - pulseWave)),
                            radius = sweepRadius * pulseWave,
                            center = center,
                            style = Stroke(width = 1.8.dp.toPx())
                        )

                        // Recurring Sonar Sweep Trail (18 smooth angular slices rotating with sweepAngle)
                        val sliceCount = 18
                        val sliceSweep = 90f / sliceCount
                        for (s in 0 until sliceCount) {
                            val progress = (s + 1).toFloat() / sliceCount
                            val sliceAlpha = (progress * progress * 0.55f).coerceIn(0.02f, 0.55f)
                            drawArc(
                                color = radarColor.copy(alpha = sliceAlpha),
                                startAngle = sweepAngle - 90f + (s * sliceSweep),
                                sweepAngle = sliceSweep + 0.6f,
                                useCenter = true,
                                topLeft = Offset(center.x - sweepRadius, center.y - sweepRadius),
                                size = Size(sweepRadius * 2, sweepRadius * 2)
                            )
                        }

                        // Sonar Sweep Node Reveal Rings (illuminates nearby nodes as the beam passes over them)
                        val blipRadiusCanvasPx = 13.dp.toPx()
                        val maxBlipDistCanvas = sweepRadius - blipRadiusCanvasPx - 8.dp.toPx()
                        combinedItems.forEachIndexed { idx, nodeItem ->
                            val itemKm = when (nodeItem) {
                                is OtherUserEntity -> nodeItem.distanceKm
                                is PostEntity -> nodeItem.distanceKm ?: 1.0
                                is MarketplaceItemEntity -> nodeItem.distanceKm ?: 1.2
                                is ClipEntity -> nodeItem.distanceKm ?: 1.5
                                else -> 1.0
                            }
                            val normDist = (itemKm / selectedRadiusKm.coerceAtLeast(1.0)).toFloat().coerceIn(0.18f, 0.92f)
                            val nodeAngle = (idx * 137.5f + 35f) % 360f
                            val nodeDist = blipRadiusCanvasPx + maxBlipDistCanvas * normDist
                            val nx = center.x + (nodeDist * cos(Math.toRadians(nodeAngle.toDouble()))).toFloat()
                            val ny = center.y + (nodeDist * sin(Math.toRadians(nodeAngle.toDouble()))).toFloat()
                            val angularLag = ((sweepAngle - nodeAngle) % 360f + 360f) % 360f

                            if (angularLag <= 110f) {
                                val pingProgress = (angularLag / 110f).coerceIn(0f, 1f)
                                val pingRadius = blipRadiusCanvasPx * (1.05f + pingProgress * 1.45f)
                                drawCircle(
                                    color = radarColor.copy(alpha = (1f - pingProgress) * 0.85f),
                                    radius = pingRadius,
                                    center = Offset(nx, ny),
                                    style = Stroke(width = 1.6.dp.toPx())
                                )
                                drawCircle(
                                    color = radarColor.copy(alpha = (1f - pingProgress) * 0.26f),
                                    radius = blipRadiusCanvasPx * 1.3f,
                                    center = Offset(nx, ny)
                                )
                            }
                        }

                        // Leading Sonar Sweep Beam Line
                        val sweepRad = Math.toRadians((sweepAngle).toDouble())
                        val lineEndX = center.x + sweepRadius * cos(sweepRad).toFloat()
                        val lineEndY = center.y + sweepRadius * sin(sweepRad).toFloat()
                        drawLine(
                            color = if (isPremium) Color(0xFFFFD700) else radarColor,
                            start = center,
                            end = Offset(lineEndX, lineEndY),
                            strokeWidth = 2.4.dp.toPx()
                        )

                        // Center Blip Beacon & Glow
                        drawCircle(
                            color = radarColor.copy(alpha = 0.35f),
                            radius = 13.dp.toPx(),
                            center = center
                        )
                        drawCircle(
                            color = if (isPremium) Color(0xFFFFD700) else radarColor,
                            radius = 5.dp.toPx(),
                            center = center
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = center
                        )
                    }

                    // =================================================================
                    // HIGH-VISIBILITY CARDINAL DIRECTION BEZEL BADGES (N, S, E, W)
                    // =================================================================
                    Surface(
                        color = Color(0xDD021206),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, radarColor.copy(alpha = 0.85f)),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 5.dp)
                            .testTag("radar_cardinal_north")
                    ) {
                        Text(
                            text = "▲ N 000°",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPremium) Color(0xFFFFD700) else Color(0xFF00FF41),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 1.5.dp)
                        )
                    }

                    Surface(
                        color = Color(0xDD021206),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, radarColor.copy(alpha = 0.85f)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 26.dp)
                            .testTag("radar_cardinal_south")
                    ) {
                        Text(
                            text = "▼ S 180°",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPremium) Color(0xFFFFD700) else Color(0xFF00FF41),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                        )
                    }

                    Surface(
                        color = Color(0xEE021608),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF00E5FF)),
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 6.dp)
                            .testTag("radar_cardinal_west")
                    ) {
                        Text(
                            text = "◀ W 270°",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = Color(0xEE021608),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.2.dp, Color(0xFF00E5FF)),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 6.dp)
                            .testTag("radar_cardinal_east")
                    ) {
                        Text(
                            text = "E 090° ▶",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00E5FF),
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    // Plotting Clean Profile Icons & Category Icons Inside Radar (Positioned by KM distance, no text clutter!)
                    val blipSizeDp = 26.dp
                    val blipRadiusPx = with(density) { 13.dp.toPx() }
                    val maxBlipDist = sweepRadiusPx - blipRadiusPx - with(density) { 14.dp.toPx() }

                    combinedItems.forEachIndexed { i, item ->
                        val itemKm = when (item) {
                            is OtherUserEntity -> item.distanceKm
                            is PostEntity -> item.distanceKm ?: 1.0
                            is MarketplaceItemEntity -> item.distanceKm ?: 1.2
                            is ClipEntity -> item.distanceKm ?: 1.5
                            else -> 1.0
                        }
                        val normalizedDist = (itemKm / selectedRadiusKm.coerceAtLeast(1.0)).toFloat().coerceIn(0.18f, 0.92f)
                        val angle = (i * 137.5f + 35f) % 360f
                        val dist = blipRadiusPx + maxBlipDist * normalizedDist
                        val bx = centerPx + (dist * cos(Math.toRadians(angle.toDouble()))).toFloat()
                        val by = centerPx + (dist * sin(Math.toRadians(angle.toDouble()))).toFloat()

                        // Sonar Sweep Reveal Calculation: Node lights up when sweepAngle passes over `angle`
                        val angularLag = ((sweepAngle - angle) % 360f + 360f) % 360f
                        val isSonarRevealed = angularLag <= 85f
                        val sonarRevealAlpha = (0.32f + 0.68f * (1f - (angularLag / 315f).coerceIn(0f, 1f))).coerceIn(0.32f, 1f)
                        val sonarPulseScale = if (angularLag <= 65f) {
                            1.0f + 0.22f * (1f - angularLag / 65f)
                        } else {
                            1.0f
                        }

                        val avatarUrl = when (item) {
                            is OtherUserEntity -> item.avatarUrl
                            is PostEntity -> item.userAvatar
                            is MarketplaceItemEntity -> item.imageUrl
                            is ClipEntity -> item.userAvatar
                            else -> ""
                        }
                        val blipBorderColor = when (item) {
                            is OtherUserEntity -> if (item.isVerified) Color(0xFFFFD700) else radarColor
                            is PostEntity -> radarColor
                            is MarketplaceItemEntity -> Color(0xFFFF9800)
                            is ClipEntity -> Color(0xFF00E5FF)
                            else -> radarColor
                        }

                        Box(
                            modifier = Modifier
                                .offset(
                                    x = with(density) { (bx - blipRadiusPx).toDp() },
                                    y = with(density) { (by - blipRadiusPx).toDp() }
                                )
                                .size(blipSizeDp * sonarPulseScale)
                                .then(androidx.compose.ui.Modifier.graphicsLayer(alpha = sonarRevealAlpha))
                                .clip(CircleShape)
                                .background(Color(0xFF021405))
                                .border(
                                    width = if (isSonarRevealed) 2.2.dp else 1.4.dp,
                                    color = if (isSonarRevealed) Color.White else blipBorderColor,
                                    shape = CircleShape
                                )
                                .clickable {
                                    HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                    when (item) {
                                        is OtherUserEntity -> {
                                            dispatchUserTarget = item
                                            onUserClick(item)
                                        }
                                        is PostEntity -> {
                                            onPostClick(item)
                                        }
                                        is MarketplaceItemEntity -> {
                                            Toast.makeText(context, "🛍️ ${item.title} ($${item.price.toInt()}) • ${item.distanceKm ?: 1.2} km away", Toast.LENGTH_SHORT).show()
                                        }
                                        is ClipEntity -> {
                                            Toast.makeText(context, "🎬 Clip by @${item.username} • ${item.distanceKm ?: 1.0} km away", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                        ) {
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Radar Node Icon",
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            // Tiny corner category icon dot (User / Market / Clip) instead of text
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(blipBorderColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = when (item) {
                                        is MarketplaceItemEntity -> Icons.Default.ShoppingBag
                                        is ClipEntity -> Icons.Default.PlayArrow
                                        else -> Icons.Default.Person
                                    },
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(6.dp)
                                )
                            }
                        }
                    }

                    // Bottom Sync Badge inside Radar
                    Surface(
                        color = Color.Black.copy(alpha = 0.88f),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(0.8.dp, radarColor.copy(alpha = 0.65f)),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 5.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isPrivateAccount) Color(0xFF00E5FF) else if (isLocationEnabled) Color(0xFF00FF41) else Color.Red)
                            )
                            Text(
                                text = if (isPrivateAccount) "👻 GHOST MODE • RF SILENT" else "${kmSortedUsersInRange.size} USERS • $resolvedStateName, $resolvedCountryName",
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPrivateAccount) Color(0xFF00E5FF) else Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // =====================================================================
        // 4.5 KM-SORTED AVAILABLE USERS PROFILE ICONS & COMPACT MARKET ICONS
        // Sorted strictly nearest-to-farthest within the user's selected KM range
        // (No self-QR code shown on radar; uses profile icons instead of clutter)
        // =====================================================================
        val sortedRadarUsers = remember(nearbyUsers, selectedRadiusKm, isLocationEnabled, isPrivateAccount, showOnlyVerifiedResidents) {
            if (!isLocationEnabled || isPrivateAccount) emptyList()
            else {
                val list = if (showOnlyVerifiedResidents) nearbyUsers.filter { it.isVerified } else nearbyUsers
                list.filter { it.distanceKm <= selectedRadiusKm }.sortedBy { it.distanceKm }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 5.dp)
                .testTag("radar_energetic_telemetry_strip"),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Compact KM Sort Header Icon Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF041E0C),
                border = BorderStroke(1.dp, radarColor.copy(alpha = 0.65f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = "Sorted by KM",
                        tint = radarColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "≤${if (selectedRadiusKm >= 10) selectedRadiusKm.toInt() else String.format("%.0f", selectedRadiusKm)}KM",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Black,
                        color = radarColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // KM-Sorted Available User Profile Icons (Nearest -> Farthest)
            sortedRadarUsers.forEach { neighbor ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable {
                            dispatchUserTarget = neighbor
                            onUserClick(neighbor)
                        }
                ) {
                    Box(contentAlignment = Alignment.BottomCenter) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF052010))
                                .border(
                                    1.6.dp,
                                    if (neighbor.isVerified) Color(0xFFFFD700) else Color(0xFF00FF41),
                                    CircleShape
                                )
                        ) {
                            AsyncImage(
                                model = neighbor.avatarUrl,
                                contentDescription = neighbor.username,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xEE021408),
                            border = BorderStroke(0.7.dp, radarColor.copy(alpha = 0.8f))
                        ) {
                            Text(
                                text = "${String.format("%.1f", neighbor.distanceKm)}km",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF00FF41),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                            )
                        }
                    }
                }
            }

            // Compact Market Icon Avatars (Sorted by KM within selected range)
            nearbyMarketItems
                .filter { (it.distanceKm ?: 1.2) <= selectedRadiusKm }
                .sortedBy { it.distanceKm ?: 1.2 }
                .take(4)
                .forEach { item ->
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "🛍️ ${item.title} ($${item.price.toInt()}) • ${item.distanceKm ?: 1.2} km", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF261604))
                                .border(1.5.dp, Color(0xFFFF9800), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = item.title,
                                tint = Color(0xFFFFB300),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xEE1F1102),
                            border = BorderStroke(0.7.dp, Color(0xFFFF9800))
                        ) {
                            Text(
                                text = "${String.format("%.1f", item.distanceKm ?: 1.2)}km",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFCC80),
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 0.5.dp)
                            )
                        }
                    }
                }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // =====================================================================
        // 5. CONSOLIDATED CONTROLS BELOW THE RADAR SCREEN
        // (Duplicate 'Radar Online' & 'Precise Coordinates' cards removed)
        // =====================================================================

        // 1. SIGNAL TRANSMIT & FAR-REACH PRIVACY SELECTOR (3KM, 5KM, 10KM, 50KM, 500KM, State, Country)
        PassiveSonarGhostBanner(
            isGhostActive = isGhostActive,
            onToggleGhost = {
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                isGhostActive = !isGhostActive
                onToggleHidePreciseLocation?.invoke(isGhostActive)
            },
            broadcastingRadius = broadcastingRadius,
            onBroadcastingRadiusChange = { nextRange ->
                broadcastingRadius = nextRange
                onSelectObfuscatedRange?.invoke(nextRange)
            },
            detectedStateName = resolvedStateName,
            detectedCountryName = resolvedCountryName,
            isPremium = isPremium,
            isStealthGhostMode = isPrivateAccount,
            onUnlockPremiumClick = onUnlockPremiumClick,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
        )

        // 2. Unified Range Slider & Presets (with Detected State & Country Name)
        UnifiedRadarRangeSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            isPremium = isPremium,
            detectedStateName = resolvedStateName,
            detectedCountryName = resolvedCountryName,
            onUnlockPremiumClick = onUnlockPremiumClick
        )

        // 3. HUD Themes & Signal Aura Selector Bar (Persisted to Firestore)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "HUD THEME:",
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
                            if (isSelected) theme.primaryColor else if (isLocked) Color(0xFFFFD700).copy(alpha = 0.45f) else Color.Gray.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.clickable {
                            if (!isLocked) {
                                selectedTheme = theme
                                onSelectHudTheme(theme.name)
                            } else {
                                Toast.makeText(context, "👑 Titan Premium HUD Theme: ${theme.title}. Opening upgrade options...", Toast.LENGTH_SHORT).show()
                                onUnlockPremiumClick()
                            }
                        }
                    ) {
                        Text(
                            text = if (isLocked) "${theme.title.take(3)}👑" else theme.title.take(4),
                            fontSize = 8.sp,
                            color = if (isSelected) theme.primaryColor else if (isLocked) Color(0xFFFFD700) else Color.Gray,
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
                        Toast.makeText(context, "👑 Titan Premium Feature: Signal Aura Glow", Toast.LENGTH_SHORT).show()
                        onUnlockPremiumClick()
                    }
                }
            ) {
                Text(
                    text = if (signalAuraEnabled) "AURA ✨" else "AURA 👑",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (signalAuraEnabled) Color(0xFFFFD700) else Color.Gray,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // 3.8. RADAR VISITOR TELEMETRY ("WHO VIEWED MY RADAR SIGNAL") & CITY-TELEPORT TRAVEL MODE
        var teleportedCity by remember { mutableStateOf<String?>(null) }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0A141E),
            border = BorderStroke(
                1.dp,
                if (isPremium) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color(0xFFFFD700).copy(alpha = 0.55f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 3.dp)
                .clickable {
                    if (!isPremium) {
                        Toast.makeText(context, "👑 Titan Premium: Unlock full Radar Visitor List & City-Teleport!", Toast.LENGTH_SHORT).show()
                        onUnlockPremiumClick()
                    }
                }
                .testTag("radar_visitor_telemetry_card")
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "👀", fontSize = 13.sp)
                        Column {
                            Text(
                                text = when {
                                    isPrivateAccount -> "👻 GHOST MODE ACTIVE • RF TRANSPONDER SILENT"
                                    nearbyUsers.isEmpty() -> "WHO VIEWED YOUR RADAR SIGNAL"
                                    else -> "${nearbyUsers.size} LOCALS ON YOUR RADAR FREQUENCY"
                                },
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isPrivateAccount || isPremium) Color(0xFF00E5FF) else Color(0xFFFFD700),
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = when {
                                    isPrivateAccount -> "Transmitting & receiving other users' radar details is disabled in Ghost / Read-Only Mode."
                                    nearbyUsers.isEmpty() -> {
                                        if (isPremium) "Live Radar Analytics active • Real-time signal viewers appear here"
                                        else "Upgrade to Localiiiy Pro to see real-time neighbors who scanned your signal"
                                    }
                                    isPremium -> "Active: " + nearbyUsers.take(3).joinToString(" • ") { "@${it.username} (${String.format("%.1f", it.distanceKm)}km)" }
                                    else -> "Upgrade to Pro to reveal ${nearbyUsers.size} nearby signal viewers"
                                },
                                fontSize = 8.5.sp,
                                color = Color.LightGray
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isPremium) Color(0xFF00E676).copy(alpha = 0.2f) else Color(0xFFFFD700),
                        modifier = Modifier.clickable {
                            if (!isPremium) onUnlockPremiumClick()
                        }
                    ) {
                        Text(
                            text = if (isPremium) "UNLOCKED ✓" else "👑 UNLOCK",
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isPremium) Color(0xFF00E676) else Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                Spacer(modifier = Modifier.height(6.dp))

                // City-Teleport ("Travel Mode") + ₹29 24h Spotlight Micro-Boost
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (teleportedCity != null) "✈️ TELEPORT: $teleportedCity" else "✈️ CITY TELEPORT:",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF80D8FF),
                        fontFamily = FontFamily.Monospace
                    )
                    listOf("Mumbai", "Bangalore", "Delhi", "Dubai", "London").forEach { city ->
                        val isCityActive = teleportedCity == city
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (isCityActive) Color(0xFF00E5FF).copy(alpha = 0.25f) else Color(0xFF132232),
                            border = BorderStroke(
                                0.8.dp,
                                if (isCityActive) Color(0xFF00E5FF) else Color(0xFF80D8FF).copy(alpha = 0.35f)
                            ),
                            modifier = Modifier.clickable {
                                if (isPremium) {
                                    teleportedCity = if (isCityActive) null else city
                                    Toast.makeText(
                                        context,
                                        if (teleportedCity != null) "✈️ Signal Teleported to $city Radar!" else "📡 Returned to Local GPS Radar",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context, "👑 Titan Premium Travel Mode: Teleport your signal to $city!", Toast.LENGTH_SHORT).show()
                                    onUnlockPremiumClick()
                                }
                            }
                        ) {
                            Text(
                                text = if (isPremium) city else "$city 👑",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCityActive) Color(0xFF00E5FF) else Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF332404),
                        border = BorderStroke(0.8.dp, Color(0xFFFF9100)),
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "⚡ 24-Hour Radar Spotlight Boost (₹29 / $0.49) — Opening Titan Hub...", Toast.LENGTH_SHORT).show()
                            onUnlockPremiumClick()
                        }
                    ) {
                        Text(
                            text = "⚡ ₹29 24h Boost",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFFFB300),
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 4. Safe Haven, Tool #2 (Sponsored Map Pins & Coupons ₹199/d) & Tool #9 (Paid Event QR Pass ₹99)
        var showSponsoredPinDialog by remember { mutableStateOf(false) }
        var showPaidEventQrDialog by remember { mutableStateOf(false) }
        var hasBookedEventPass by remember { mutableStateOf(false) }
        var customSponsoredBusiness by remember { mutableStateOf("Coastal Brew Cafe ($resolvedStateName) • 20% OFF") }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 3.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tool #2: Sponsored Map Pin & Local Cafe Coupon
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF281D05),
                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                modifier = Modifier
                    .clickable { showSponsoredPinDialog = true }
                    .testTag("radar_sponsored_map_pin_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "📍", fontSize = 11.sp)
                    Text(
                        text = "SPONSORED PIN: $customSponsoredBusiness",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFFD700),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Tool #9: Hyperlocal Paid Event & Meetup QR Pass Ticketing
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (hasBookedEventPass) Color(0xFF06321D) else Color(0xFF1A1033),
                border = BorderStroke(1.dp, if (hasBookedEventPass) Color(0xFF00E676) else Color(0xFFD500F9)),
                modifier = Modifier
                    .clickable { showPaidEventQrDialog = true }
                    .testTag("radar_paid_event_qr_pass_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🎟️", fontSize = 11.sp)
                    Text(
                        text = if (hasBookedEventPass) {
                            "QR PASS ACTIVE: $resolvedStateName Creator & DJ Meet ✓"
                        } else {
                            "EVENT PASS: $resolvedStateName Sunset DJ & Creator Meet (₹99 QR)"
                        },
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (hasBookedEventPass) Color(0xFF00E676) else Color(0xFFEA80FC),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            EmergencySafeHavenMarker(havenName = "Central Clinic ($resolvedStateName)", distanceKm = 0.8, onClick = {})
            EventGeoFenceOverlayTag(eventName = "$resolvedStateName Community Meet", radiusMeters = 300, attendeesCount = 42)
        }

        // Dialog for Tool #2: Sponsored Map Pin & Local Cafe Coupon + Drop Business Pin (₹199/day)
        if (showSponsoredPinDialog) {
            var newShopTitle by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showSponsoredPinDialog = false },
                containerColor = Color(0xFF0D141C),
                titleContentColor = Color(0xFFFFD700),
                textContentColor = Color.White,
                title = {
                    Text("📍 Sponsored Radar Map Pins & Coupons", fontWeight = FontWeight.Black, fontSize = 16.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1F1805),
                            border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("☕ ACTIVE LOCAL COUPON PIN (0.6 KM)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD700))
                                Text(customSponsoredBusiness, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                Text("Show code LOCAL20 at counter for 20% off craft coffee & snacks!", fontSize = 11.sp, color = Color.LightGray)
                            }
                        }
                        HorizontalDivider(color = Color.DarkGray)
                        Text(
                            text = "Own a local cafe, gym, or shop? Drop your own Glowing Gold Radar Pin for ₹199/day (100% Platform Ad Revenue):",
                            fontSize = 11.5.sp,
                            color = Color(0xFFB0BEC5)
                        )
                        OutlinedTextField(
                            value = newShopTitle,
                            onValueChange = { newShopTitle = it },
                            placeholder = { Text("e.g., Panaji Artisan Bakery • 25% OFF", fontSize = 11.sp, color = Color.Gray) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFD700),
                                unfocusedBorderColor = Color.Gray
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newShopTitle.isNotBlank()) {
                                customSponsoredBusiness = newShopTitle.trim()
                                Toast.makeText(context, "📍 Sponsored Radar Pin Live! (₹199/day billed • 100% Developer Revenue)", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "🎟️ Coupon Code LOCAL20 copied! Redeem at $customSponsoredBusiness", Toast.LENGTH_SHORT).show()
                            }
                            showSponsoredPinDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700), contentColor = Color.Black)
                    ) {
                        Text(if (newShopTitle.isNotBlank()) "Launch Pin (₹199/d)" else "Copy Coupon LOCAL20", fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSponsoredPinDialog = false }) {
                        Text("Close", color = Color.LightGray)
                    }
                }
            )
        }

        // Dialog for Tool #9: Hyperlocal Paid Event & Meetup QR Pass (90% Host / 10% Platform Fee)
        if (showPaidEventQrDialog) {
            AlertDialog(
                onDismissRequest = { showPaidEventQrDialog = false },
                containerColor = Color(0xFF100B1C),
                titleContentColor = Color(0xFFEA80FC),
                textContentColor = Color.White,
                title = {
                    Text("🎟️ Hyperlocal Event QR Pass Ticketing", fontWeight = FontWeight.Black, fontSize = 16.sp)
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "$resolvedStateName Sunset DJ, Rooftop Jam & Creator Meetup",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = "📍 300m Radar Geofence • Tonight 7:00 PM • 42 Locals Attending",
                            fontSize = 11.5.sp,
                            color = Color(0xFF00E5FF)
                        )
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1D1430),
                            border = BorderStroke(1.dp, Color(0xFFD500F9).copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("💰 Revenue Split: ₹89.10 (90%) to Event Host • ₹9.90 (10%) to Platform", fontSize = 10.5.sp, color = Color(0xFFFFD700), fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (hasBookedEventPass) "✅ QR PASS CODE: LCL-GOA-8841-VIP (Verified at Geofence Gate)" else "Includes 1x Complimentary Drink + VIP Creator Networking Access",
                                    fontSize = 11.sp,
                                    color = if (hasBookedEventPass) Color(0xFF00E676) else Color.LightGray,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            hasBookedEventPass = true
                            Toast.makeText(context, "🎟️ QR Event Pass Unlocked! (₹89.10 to Host • ₹9.90 Platform Fee)", Toast.LENGTH_LONG).show()
                            showPaidEventQrDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD500F9), contentColor = Color.White)
                    ) {
                        Text(if (hasBookedEventPass) "View QR Pass ✓" else "Buy QR Pass (₹99)", fontWeight = FontWeight.Black)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showPaidEventQrDialog = false }) {
                        Text("Close", color = Color.LightGray)
                    }
                }
            )
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

    // Interactive Tactical Radar QR Profile Modal
    if (showQrIdentityDialog) {
        val geohashCode = remember(userProfile.latitude, userProfile.longitude) {
            LocationHelper.toCoarseGeohash(
                if (userProfile.latitude != 0.0) userProfile.latitude else 15.399,
                if (userProfile.longitude != 0.0) userProfile.longitude else 73.812
            ).uppercase()
        }
        AlertDialog(
            onDismissRequest = { showQrIdentityDialog = false },
            containerColor = Color(0xFF04140A),
            titleContentColor = Color(0xFF00FF41),
            textContentColor = Color.White,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.QrCode2, contentDescription = null, tint = Color(0xFF00E5FF))
                    Column {
                        Text(
                            text = "LOCALIIIY RADAR QR PASS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF00E5FF)
                        )
                        Text(
                            text = "@$qrSelectedUsername • $resolvedStateName, $resolvedCountryName",
                            fontSize = 11.sp,
                            color = Color(0xFF00FF41),
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Rendered Tactical Matrix QR Box
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = BorderStroke(3.dp, Color(0xFF00E5FF)),
                        modifier = Modifier.size(168.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize().padding(14.dp)) {
                            val gridSize = 9
                            val cellSize = size.minDimension / gridSize
                            val seed = qrSelectedUsername.hashCode()
                            for (r in 0 until gridSize) {
                                for (c in 0 until gridSize) {
                                    val isCornerFinder = (r < 3 && c < 3) || (r < 3 && c >= gridSize - 3) || (r >= gridSize - 3 && c < 3)
                                    val bit = ((seed shr ((r * gridSize + c) % 28)) and 1) == 1
                                    if (isCornerFinder || bit) {
                                        drawRect(
                                            color = if (isCornerFinder) Color(0xFF031B10) else Color.Black,
                                            topLeft = Offset(c * cellSize, r * cellSize),
                                            size = Size(cellSize * 0.92f, cellSize * 0.92f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Text(
                        text = "NODE GEOHASH: #$geohashCode • SHA-256 VERIFIED",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Scan this Localiiiy Radar QR to connect instantly with @$qrSelectedUsername ($qrSelectedFullName) in $resolvedStateName, $resolvedCountryName.",
                        fontSize = 11.5.sp,
                        color = Color.LightGray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        ShareHelper.launchNativeShare(
                            context = context,
                            shareText = "Connect with @$qrSelectedUsername on Localiiiy Radar ($resolvedStateName, $resolvedCountryName)! Node #$geohashCode — https://localiiiy.web.app/user/$qrSelectedUsername",
                            chooserTitle = "Share Radar QR Pass"
                        )
                        showQrIdentityDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF), contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share QR Link", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showQrIdentityDialog = false }) {
                    Text("Close", color = Color.LightGray)
                }
            }
        )
    }
}
