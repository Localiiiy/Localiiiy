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
    var broadcastingRadius by remember { mutableStateOf("3k") }
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

            // Button 4: Privacy Shield (Cloak)
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isGhostActive) Color(0xFFFF9800).copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isGhostActive) Color(0xFFFF9800) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        isGhostActive = !isGhostActive
                        onToggleHidePreciseLocation?.invoke(isGhostActive)
                        Toast.makeText(context, if (isGhostActive) "🛡️ Privacy Shield Active: Radar masked" else "Radar Visibility Unmasked", Toast.LENGTH_SHORT).show()
                    }
                    .testTag("radar_privacy_shield_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isGhostActive) Icons.Default.Shield else Icons.Default.ShieldMoon,
                        contentDescription = "Privacy Shield",
                        tint = if (isGhostActive) Color(0xFFFF9800) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isGhostActive) "SHIELD ACTIVE" else "PRIVACY SHIELD",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGhostActive) Color(0xFFFF9800) else Color.LightGray,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Button 5: Broadcast Signal Enhancement
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isBroadcastEnhanceActive) Color(0xFF10B981).copy(alpha = 0.25f) else Color(0xFF0D1520),
                border = BorderStroke(1.dp, if (isBroadcastEnhanceActive) Color(0xFF10B981) else Color(0xFF37474F)),
                modifier = Modifier
                    .clickable {
                        HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        isBroadcastEnhanceActive = !isBroadcastEnhanceActive
                        Toast.makeText(context, if (isBroadcastEnhanceActive) "📶 Ultra-Wide Beacon Transmit Active" else "Standard Beacon Transmit", Toast.LENGTH_SHORT).show()
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
                        contentDescription = "Broadcast Signal",
                        tint = if (isBroadcastEnhanceActive) Color(0xFF10B981) else Color.LightGray,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = if (isBroadcastEnhanceActive) "BROADCAST ULTRA" else "BROADCAST SIGNAL",
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
        // 4. CIRCULAR RADAR SCREEN (Now Positioned Immediately Below Both Control Lines)
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
            val combinedItems = remember(nearbyUsers, nearbyPosts, isLocationEnabled, isGhostActive, showOnlyVerifiedResidents) {
                if (!isLocationEnabled || isGhostActive) emptyList()
                else {
                    val users = if (showOnlyVerifiedResidents) nearbyUsers.filter { it.isVerified } else nearbyUsers
                    (users + nearbyPosts).take(combinedCount)
                }
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
            broadcastingRadius = broadcastingRadius,
            onBroadcastingRadiusChange = { broadcastingRadius = it },
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 3.dp)
        )

        // 5. Unified Range Slider & Presets
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
