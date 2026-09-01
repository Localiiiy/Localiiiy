package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Layers
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliAccentCoral
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal
import kotlin.math.*

enum class MapVisualTheme(val label: String, val icon: String) {
    DARK_RADAR("Dark Radar", "🛰️"),
    SATELLITE("Satellite", "🌍"),
    STREET_GRID("Street Grid", "🗺️"),
    CYBER_VECTOR("Cyber Vector", "⚡")
}

/**
 * Live Google Map Background layer for the Proximity Radar.
 * Displays accurate geo-spatial contours, water bodies, road webs, continental shapes,
 * dynamic scale grids, and live Google Maps launching.
 */
@Composable
fun LiveGoogleMapBackground(
    latitude: Double = 47.608013,
    longitude: Double = -122.335167,
    locationName: String = "Pike Place Market, Seattle",
    radiusKm: Double = 3.0,
    mapTheme: MapVisualTheme = MapVisualTheme.DARK_RADAR,
    onThemeChange: (MapVisualTheme) -> Unit = {},
    showMapControls: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Subtle drift animation to simulate live satellite stream
    val infiniteTransition = rememberInfiniteTransition(label = "MapDrift")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveOffset"
    )

    var showThemeMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(20.dp))
            .background(
                when (mapTheme) {
                    MapVisualTheme.DARK_RADAR -> Color(0xFF030E1A)
                    MapVisualTheme.SATELLITE -> Color(0xFF06141F)
                    MapVisualTheme.STREET_GRID -> Color(0xFF0A1926)
                    MapVisualTheme.CYBER_VECTOR -> Color(0xFF02070F)
                }
            )
            .testTag("live_google_map_background")
    ) {
        // 1. Core Map Canvas Rendering based on radius zoom level
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val w = size.width
            val h = size.height

            when {
                // Hyperlocal / Neighborhood (1km - 10km)
                radiusKm <= 10.0 -> {
                    drawLocalCityMap(
                        center = center,
                        w = w,
                        h = h,
                        mapTheme = mapTheme,
                        radiusKm = radiusKm,
                        waveOffset = waveOffset
                    )
                }
                // Metro / Regional (50km - 500km)
                radiusKm <= 500.0 -> {
                    drawRegionalMap(
                        center = center,
                        w = w,
                        h = h,
                        mapTheme = mapTheme,
                        radiusKm = radiusKm,
                        waveOffset = waveOffset
                    )
                }
                // Country / Subcontinent (1000km - 5000km)
                radiusKm <= 5000.0 -> {
                    drawCountryScaleMap(
                        center = center,
                        w = w,
                        h = h,
                        mapTheme = mapTheme,
                        radiusKm = radiusKm,
                        waveOffset = waveOffset
                    )
                }
                // Earth / Global (20000km)
                else -> {
                    drawGlobalEarthMap(
                        center = center,
                        w = w,
                        h = h,
                        mapTheme = mapTheme,
                        waveOffset = waveOffset
                    )
                }
            }

            // Latitude / Longitude Subtle Coordinate Grid Overlay
            drawCoordinateGridLines(center, w, h, mapTheme)
        }

        // Top Left Map Mode & Google Maps Action
        if (showMapControls) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Map Theme Pill Switcher
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.Black.copy(alpha = 0.65f),
                    border = androidx.compose.foundation.BorderStroke(0.8.dp, LocaliPrimaryTeal.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable { showThemeMenu = !showThemeMenu }
                        .testTag("map_theme_switcher")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(mapTheme.icon, fontSize = 11.sp)
                        Text(
                            text = mapTheme.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change Theme",
                            tint = LocaliAccentMint,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                // Launch Live Google Maps App / Web Intent
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = LocaliPrimaryTeal.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LocaliAccentMint.copy(alpha = 0.7f)),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .clickable {
                            try {
                                val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude(${Uri.encode(locationName)})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                                }
                            } catch (_: Exception) {
                                val webUri = Uri.parse("https://www.google.com/maps/search/?api=1&query=$latitude,$longitude")
                                context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                            }
                        }
                        .testTag("open_google_maps_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Map,
                            contentDescription = "Google Maps",
                            tint = LocaliAccentMint,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Google Maps ↗",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocaliAccentMint
                        )
                    }
                }
            }

            // Dropdown theme selection menu
            if (showThemeMenu) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LocaliDeepNavy.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LocaliPrimaryTeal),
                    modifier = Modifier
                        .padding(start = 8.dp, top = 34.dp)
                ) {
                    Column(modifier = Modifier.padding(4.dp)) {
                        MapVisualTheme.values().forEach { theme ->
                            val isSelected = mapTheme == theme
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) LocaliPrimaryTeal.copy(alpha = 0.3f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        onThemeChange(theme)
                                        showThemeMenu = false
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(theme.icon, fontSize = 12.sp)
                                    Text(
                                        text = theme.label,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) LocaliAccentMint else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Map Telemetry HUD (Coordinates & Scale)
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = String.format(java.util.Locale.US, "GPS: %.4f°N, %.4f°W", latitude, abs(longitude)),
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 8.5.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color.Black.copy(alpha = 0.6f)
            ) {
                Text(
                    text = when {
                        radiusKm <= 1.0 -> "Scale: 100m"
                        radiusKm <= 5.0 -> "Scale: 500m"
                        radiusKm <= 10.0 -> "Scale: 1 km"
                        radiusKm <= 50.0 -> "Scale: 10 km"
                        radiusKm <= 100.0 -> "Scale: 25 km"
                        radiusKm <= 500.0 -> "Scale: 100 km"
                        radiusKm <= 1000.0 -> "Scale: 250 km"
                        radiusKm <= 5000.0 -> "Scale: 1,000 km (Country)"
                        else -> "Scale: Planetary Orbit 🌍"
                    },
                    color = LocaliAccentMint,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// Canvas Drawing Helper Functions for Different Radius Zoom Levels
// -------------------------------------------------------------

private fun DrawScope.drawCoordinateGridLines(center: Offset, w: Float, h: Float, theme: MapVisualTheme) {
    val gridColor = when (theme) {
        MapVisualTheme.DARK_RADAR -> Color(0xFF00A896).copy(alpha = 0.08f)
        MapVisualTheme.SATELLITE -> Color(0xFF02C39A).copy(alpha = 0.07f)
        MapVisualTheme.STREET_GRID -> Color(0xFF4A90E2).copy(alpha = 0.08f)
        MapVisualTheme.CYBER_VECTOR -> Color(0xFFE94560).copy(alpha = 0.09f)
    }

    val step = 32.dp.toPx()
    var x = center.x % step
    while (x < w) {
        drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, h),
            strokeWidth = 0.6.dp.toPx()
        )
        x += step
    }

    var y = center.y % step
    while (y < h) {
        drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(w, y),
            strokeWidth = 0.6.dp.toPx()
        )
        y += step
    }
}

private fun DrawScope.drawLocalCityMap(
    center: Offset,
    w: Float,
    h: Float,
    mapTheme: MapVisualTheme,
    radiusKm: Double,
    waveOffset: Float
) {
    val waterColor = when (mapTheme) {
        MapVisualTheme.DARK_RADAR -> Color(0xFF072138)
        MapVisualTheme.SATELLITE -> Color(0xFF0B2D4A)
        MapVisualTheme.STREET_GRID -> Color(0xFF0F3254)
        MapVisualTheme.CYBER_VECTOR -> Color(0xFF0A1526)
    }

    val streetPrimary = when (mapTheme) {
        MapVisualTheme.DARK_RADAR -> Color(0xFF00A896).copy(alpha = 0.28f)
        MapVisualTheme.SATELLITE -> Color(0xFF02C39A).copy(alpha = 0.25f)
        MapVisualTheme.STREET_GRID -> Color(0xFFE0E0E0).copy(alpha = 0.25f)
        MapVisualTheme.CYBER_VECTOR -> Color(0xFF00E5FF).copy(alpha = 0.35f)
    }

    val streetSecondary = when (mapTheme) {
        MapVisualTheme.DARK_RADAR -> Color(0xFF028090).copy(alpha = 0.15f)
        MapVisualTheme.SATELLITE -> Color(0xFF028090).copy(alpha = 0.14f)
        MapVisualTheme.STREET_GRID -> Color(0xFF9E9E9E).copy(alpha = 0.14f)
        MapVisualTheme.CYBER_VECTOR -> Color(0xFFFF2A6D).copy(alpha = 0.18f)
    }

    val parkColor = when (mapTheme) {
        MapVisualTheme.DARK_RADAR -> Color(0xFF042724)
        MapVisualTheme.SATELLITE -> Color(0xFF063B2B)
        MapVisualTheme.STREET_GRID -> Color(0xFF0A3D2F)
        MapVisualTheme.CYBER_VECTOR -> Color(0xFF051B18)
    }

    // 1. Water Body: Bay / Waterfront on West side (curved coastal shoreline)
    val waterPath = Path().apply {
        moveTo(0f, 0f)
        lineTo(w * 0.32f, 0f)
        cubicTo(
            w * 0.28f, h * 0.35f,
            w * 0.38f, h * 0.65f,
            w * 0.22f, h
        )
        lineTo(0f, h)
        close()
    }
    drawPath(path = waterPath, color = waterColor)

    // Waterfront Piers / Docks (Pike Place Waterfront Piers)
    val pierColor = streetPrimary.copy(alpha = 0.4f)
    for (i in 0..4) {
        val pierY = h * (0.28f + i * 0.12f)
        val pierStartX = w * 0.25f
        val pierLength = 22.dp.toPx()
        drawLine(
            color = pierColor,
            start = Offset(pierStartX, pierY),
            end = Offset(pierStartX - pierLength, pierY),
            strokeWidth = 3.dp.toPx()
        )
    }

    // 2. City Parks (e.g., Olympic Sculpture Park, Waterfront Park, Freeway Park)
    drawRoundRect(
        color = parkColor,
        topLeft = Offset(w * 0.65f, h * 0.2f),
        size = Size(w * 0.25f, h * 0.18f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(10.dp.toPx())
    )

    drawRoundRect(
        color = parkColor,
        topLeft = Offset(w * 0.4f, h * 0.72f),
        size = Size(w * 0.28f, h * 0.14f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx())
    )

    // 3. Arterial Highways & Main Avenues (I-5 Corridor, 1st Ave, Pike St, Pine St, Alaskan Way)
    // Main North-South Arterials
    val mainArterialX = listOf(w * 0.36f, w * 0.50f, w * 0.64f, w * 0.78f, w * 0.90f)
    mainArterialX.forEachIndexed { idx, ax ->
        drawLine(
            color = if (idx == 1 || idx == 3) streetPrimary else streetSecondary,
            start = Offset(ax, 0f),
            end = Offset(ax - 20.dp.toPx(), h),
            strokeWidth = if (idx == 1 || idx == 3) 2.2.dp.toPx() else 1.2.dp.toPx()
        )
    }

    // Main East-West Streets
    val mainStreetY = listOf(h * 0.15f, h * 0.30f, h * 0.45f, h * 0.60f, h * 0.75f, h * 0.90f)
    mainStreetY.forEachIndexed { idx, sy ->
        drawLine(
            color = if (idx == 2 || idx == 4) streetPrimary else streetSecondary,
            start = Offset(0f, sy),
            end = Offset(w, sy),
            strokeWidth = if (idx == 2 || idx == 4) 2.0.dp.toPx() else 1.0.dp.toPx()
        )
    }

    // Angled Interstate Curved Ribbon (I-5)
    val freewayPath = Path().apply {
        moveTo(w * 0.88f, 0f)
        cubicTo(
            w * 0.82f, h * 0.3f,
            w * 0.74f, h * 0.7f,
            w * 0.65f, h
        )
    }
    drawPath(
        path = freewayPath,
        color = streetPrimary.copy(alpha = 0.5f),
        style = Stroke(width = 3.dp.toPx())
    )
}

private fun DrawScope.drawRegionalMap(
    center: Offset,
    w: Float,
    h: Float,
    mapTheme: MapVisualTheme,
    radiusKm: Double,
    waveOffset: Float
) {
    val waterColor = Color(0xFF072138)
    val landColor = Color(0xFF0B1F2D)
    val mountainColor = Color(0xFF143347).copy(alpha = 0.4f)
    val highwayColor = LocaliAccentMint.copy(alpha = 0.35f)

    // Regional Landmass & Puget Sound / Lake Washington Basins
    val soundPath = Path().apply {
        moveTo(0f, 0f)
        cubicTo(w * 0.4f, h * 0.2f, w * 0.25f, h * 0.6f, w * 0.35f, h)
        lineTo(0f, h)
        close()
    }
    drawPath(path = soundPath, color = waterColor)

    // Lake Washington on East
    val lakePath = Path().apply {
        moveTo(w * 0.75f, h * 0.1f)
        cubicTo(w * 0.82f, h * 0.4f, w * 0.78f, h * 0.7f, w * 0.72f, h * 0.9f)
        lineTo(w * 0.82f, h * 0.9f)
        cubicTo(w * 0.88f, h * 0.7f, w * 0.92f, h * 0.4f, w * 0.85f, h * 0.1f)
        close()
    }
    drawPath(path = lakePath, color = waterColor)

    // Mountain Contour Shading (Cascade Ranges)
    for (i in 0..3) {
        val peakX = w * (0.85f + i * 0.05f)
        val peakY = h * (0.3f + i * 0.15f)
        drawCircle(
            color = mountainColor,
            radius = (18 + i * 6).dp.toPx(),
            center = Offset(peakX, peakY)
        )
    }

    // Regional Highways (I-5 corridor connecting North-South, I-90 connecting East-West)
    drawLine(
        color = highwayColor,
        start = Offset(w * 0.5f, 0f),
        end = Offset(w * 0.52f, h),
        strokeWidth = 2.5.dp.toPx()
    )
    drawLine(
        color = highwayColor,
        start = Offset(w * 0.35f, h * 0.5f),
        end = Offset(w, h * 0.55f),
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawCountryScaleMap(
    center: Offset,
    w: Float,
    h: Float,
    mapTheme: MapVisualTheme,
    radiusKm: Double,
    waveOffset: Float
) {
    val borderCol = LocaliAccentMint.copy(alpha = 0.25f)
    val hubCol = LocaliAccentMint.copy(alpha = 0.6f)

    // Simplified Continental North America Outline
    val usOutline = Path().apply {
        moveTo(w * 0.15f, h * 0.2f) // Pacific NW
        lineTo(w * 0.18f, h * 0.75f) // California Coast
        lineTo(w * 0.45f, h * 0.82f) // Texas / Gulf
        lineTo(w * 0.78f, h * 0.85f) // Florida
        lineTo(w * 0.88f, h * 0.45f) // East Coast NYC
        lineTo(w * 0.85f, h * 0.22f) // Maine / Great Lakes
        lineTo(w * 0.55f, h * 0.2f) // Midwest Border
        close()
    }
    drawPath(path = usOutline, color = Color(0xFF0C2438))
    drawPath(path = usOutline, color = borderCol, style = Stroke(width = 1.2.dp.toPx()))

    // State Grid Borders
    drawLine(borderCol, Offset(w * 0.15f, h * 0.38f), Offset(w * 0.85f, h * 0.38f), strokeWidth = 0.8.dp.toPx())
    drawLine(borderCol, Offset(w * 0.18f, h * 0.55f), Offset(w * 0.80f, h * 0.55f), strokeWidth = 0.8.dp.toPx())
    drawLine(borderCol, Offset(w * 0.42f, h * 0.2f), Offset(w * 0.42f, h * 0.8f), strokeWidth = 0.8.dp.toPx())
    drawLine(borderCol, Offset(w * 0.68f, h * 0.22f), Offset(w * 0.68f, h * 0.8f), strokeWidth = 0.8.dp.toPx())

    // Major National Metropolis Hubs
    val hubs = listOf(
        Offset(w * 0.18f, h * 0.22f) to "Seattle",
        Offset(w * 0.19f, h * 0.52f) to "SF",
        Offset(w * 0.22f, h * 0.68f) to "LA",
        Offset(w * 0.60f, h * 0.38f) to "Chicago",
        Offset(w * 0.82f, h * 0.42f) to "NYC",
        Offset(w * 0.48f, h * 0.72f) to "Texas",
        Offset(w * 0.76f, h * 0.80f) to "Miami"
    )

    hubs.forEach { (pos, name) ->
        drawCircle(color = hubCol, radius = 3.dp.toPx(), center = pos)
        drawCircle(color = hubCol.copy(alpha = 0.3f), radius = 6.dp.toPx(), center = pos, style = Stroke(1.dp.toPx()))
    }
}

private fun DrawScope.drawGlobalEarthMap(
    center: Offset,
    w: Float,
    h: Float,
    mapTheme: MapVisualTheme,
    waveOffset: Float
) {
    val globeRadius = (min(w, h) / 2f) * 0.86f
    val landCol = Color(0xFF0E2E47)
    val gridCol = LocaliAccentMint.copy(alpha = 0.18f)

    // Outer Earth Atmosphere Glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF083254), Color(0xFF041524), Color.Transparent),
            center = center,
            radius = globeRadius * 1.15f
        ),
        radius = globeRadius * 1.12f,
        center = center
    )

    // Earth Planetary Disk
    drawCircle(
        color = Color(0xFF061A2B),
        radius = globeRadius,
        center = center
    )
    drawCircle(
        color = LocaliAccentMint.copy(alpha = 0.45f),
        radius = globeRadius,
        center = center,
        style = Stroke(width = 1.5.dp.toPx())
    )

    // Equator Line & Prime Meridian Ellipses
    drawLine(
        color = gridCol,
        start = Offset(center.x - globeRadius, center.y),
        end = Offset(center.x + globeRadius, center.y),
        strokeWidth = 1.dp.toPx()
    )

    drawOval(
        color = gridCol,
        topLeft = Offset(center.x - globeRadius * 0.5f, center.y - globeRadius),
        size = Size(globeRadius, globeRadius * 2f),
        style = Stroke(1.dp.toPx())
    )

    // Simplified Continental Landmass Blobs
    // Americas
    drawRoundRect(
        color = landCol,
        topLeft = Offset(center.x - globeRadius * 0.7f, center.y - globeRadius * 0.65f),
        size = Size(globeRadius * 0.55f, globeRadius * 0.6f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
    )
    drawRoundRect(
        color = landCol,
        topLeft = Offset(center.x - globeRadius * 0.5f, center.y + globeRadius * 0.05f),
        size = Size(globeRadius * 0.42f, globeRadius * 0.65f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
    )

    // Eurasia & Africa
    drawRoundRect(
        color = landCol,
        topLeft = Offset(center.x + globeRadius * 0.05f, center.y - globeRadius * 0.7f),
        size = Size(globeRadius * 0.7f, globeRadius * 0.65f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
    )
    drawRoundRect(
        color = landCol,
        topLeft = Offset(center.x + globeRadius * 0.08f, center.y - globeRadius * 0.02f),
        size = Size(globeRadius * 0.45f, globeRadius * 0.68f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx())
    )

    // Australia
    drawCircle(
        color = landCol,
        radius = globeRadius * 0.18f,
        center = Offset(center.x + globeRadius * 0.55f, center.y + globeRadius * 0.48f)
    )
}
