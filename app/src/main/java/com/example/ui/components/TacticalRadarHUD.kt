package com.example.ui.components

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.UserProfileEntity
import com.example.util.LocationHelper
import kotlin.math.*

// High-Tech Military / Sci-Fi Phosphor Palette (as in attached tactical live radar reference)
val TacticalPhosphorGreen = Color(0xFF00FF66)
val TacticalBrightGreen = Color(0xFF14FF00)
val TacticalNeonGreen = Color(0xFF39FF14)
val TacticalDarkGreen = Color(0xFF003B16)
val TacticalGridGreen = Color(0x3300FF66)
val TacticalGridGreenStrong = Color(0x7700FF66)
val TacticalRadarBg = Color(0xFF010A04)
val TacticalPanelBg = Color(0xFF020E06)
val TacticalAlertRed = Color(0xFFFF2A2A)
val TacticalTextGreen = Color(0xFF00FF88)
val TacticalWorldMapFill = Color(0x3500FF66)
val TacticalWorldMapStroke = Color(0x8014FF00)

/**
 * 3D Holographic Rotating Wireframe Globe Widget (Top-Left of reference radar).
 * Displays latitude parallels, rotating longitudinal meridians, and outer azimuth compass ring.
 */
@Composable
fun TacticalWireframeGlobe(
    modifier: Modifier = Modifier,
    globeRotationAngle: Float = 0f
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f * 0.82f

        // Outer Azimuth Rim with compass tick marks
        drawCircle(
            color = TacticalPhosphorGreen.copy(alpha = 0.8f),
            radius = radius * 1.12f,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Outer rim tick marks (every 15 degrees)
        for (deg in 0 until 360 step 15) {
            val rad = Math.toRadians(deg.toDouble())
            val isMajor = deg % 45 == 0
            val rInner = if (isMajor) radius * 1.05f else radius * 1.08f
            val rOuter = radius * 1.15f
            drawLine(
                color = TacticalPhosphorGreen.copy(alpha = if (isMajor) 0.9f else 0.5f),
                start = Offset(
                    center.x + (rInner * cos(rad)).toFloat(),
                    center.y + (rInner * sin(rad)).toFloat()
                ),
                end = Offset(
                    center.x + (rOuter * cos(rad)).toFloat(),
                    center.y + (rOuter * sin(rad)).toFloat()
                ),
                strokeWidth = (if (isMajor) 1.5.dp else 0.8.dp).toPx()
            )
        }

        // Main Globe Outer Sphere
        drawCircle(
            color = TacticalPhosphorGreen,
            radius = radius,
            center = center,
            style = Stroke(width = 1.4.dp.toPx())
        )

        // Subtle globe inner glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(TacticalPhosphorGreen.copy(alpha = 0.15f), Color.Transparent),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )

        // Latitude Parallels (horizontal ellipses)
        val latFractions = listOf(0.35f, 0.65f, 0.85f)
        latFractions.forEach { frac ->
            val yOffset = radius * frac
            val arcW = sqrt(radius * radius - yOffset * yOffset) * 2
            val arcH = radius * 0.25f * (1f - frac * 0.5f)

            // North and South parallels
            drawOval(
                color = TacticalPhosphorGreen.copy(alpha = 0.45f),
                topLeft = Offset(center.x - arcW / 2f, center.y - yOffset - arcH / 2f),
                size = Size(arcW, arcH),
                style = Stroke(width = 0.8.dp.toPx())
            )
            drawOval(
                color = TacticalPhosphorGreen.copy(alpha = 0.45f),
                topLeft = Offset(center.x - arcW / 2f, center.y + yOffset - arcH / 2f),
                size = Size(arcW, arcH),
                style = Stroke(width = 0.8.dp.toPx())
            )
        }

        // Equator (Center line)
        drawLine(
            color = TacticalPhosphorGreen.copy(alpha = 0.7f),
            start = Offset(center.x - radius, center.y),
            end = Offset(center.x + radius, center.y),
            strokeWidth = 1.dp.toPx()
        )

        // Rotating Longitude Meridians (6 vertical ellipses whose width oscillates with globeRotationAngle)
        for (i in 0 until 6) {
            val meridianPhase = (globeRotationAngle + (i * 30f)) % 180f
            val rad = Math.toRadians(meridianPhase.toDouble())
            val wFraction = cos(rad).toFloat() // ranges from -1 to 1

            if (abs(wFraction) > 0.05f) {
                val ellipseWidth = radius * 2 * abs(wFraction)
                drawOval(
                    color = TacticalPhosphorGreen.copy(alpha = 0.5f + 0.3f * abs(wFraction)),
                    topLeft = Offset(center.x - ellipseWidth / 2f, center.y - radius),
                    size = Size(ellipseWidth, radius * 2),
                    style = Stroke(width = 0.9.dp.toPx())
                )
            }
        }

        // Prime vertical meridian
        drawLine(
            color = TacticalPhosphorGreen.copy(alpha = 0.75f),
            start = Offset(center.x, center.y - radius),
            end = Offset(center.x, center.y + radius),
            strokeWidth = 1.dp.toPx()
        )
    }
}

/**
 * Dynamic Audio / Oscilloscope Frequency Waveform (as seen on left vertical bar and bottom right in reference).
 */
@Composable
fun TacticalWaveformOscilloscope(
    isVertical: Boolean = false,
    wavePhase: Float = 0f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        if (isVertical) {
            // Vertical Oscilloscope scan band
            val centerX = w / 2f

            // Axis scale ruler on left side
            val tickCount = 18
            for (i in 0..tickCount) {
                val y = h * (i.toFloat() / tickCount)
                val isMajor = i % 3 == 0
                val tickLen = if (isMajor) 7.dp.toPx() else 3.5.dp.toPx()
                drawLine(
                    color = TacticalPhosphorGreen.copy(alpha = if (isMajor) 0.8f else 0.4f),
                    start = Offset(2.dp.toPx(), y),
                    end = Offset(2.dp.toPx() + tickLen, y),
                    strokeWidth = (if (isMajor) 1.2.dp else 0.8.dp).toPx()
                )
            }

            // Central vertical guideline
            drawLine(
                color = TacticalGridGreen,
                start = Offset(centerX, 0f),
                end = Offset(centerX, h),
                strokeWidth = 0.8.dp.toPx()
            )

            // Dynamic oscillating audio wave path
            val path = Path()
            val points = 60
            for (step in 0..points) {
                val y = h * (step.toFloat() / points)
                // Combine 3 sine frequencies for realistic seismic/voice frequency profile
                val envelope = sin((step.toFloat() / points) * Math.PI).toFloat() // taper edges
                val sin1 = sin(Math.toRadians(((step * 18.0) + wavePhase * 360.0))).toFloat()
                val sin2 = sin(Math.toRadians(((step * 45.0) - wavePhase * 720.0))).toFloat() * 0.4f
                val deltaX = (sin1 + sin2) * (w * 0.42f) * envelope

                val x = centerX + deltaX
                if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = TacticalBrightGreen,
                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            )
        } else {
            // Horizontal Oscilloscope scan band
            val centerY = h / 2f

            // Bottom axis scale ticks
            val tickCount = 20
            for (i in 0..tickCount) {
                val x = w * (i.toFloat() / tickCount)
                val isMajor = i % 4 == 0
                val tickLen = if (isMajor) 6.dp.toPx() else 3.dp.toPx()
                drawLine(
                    color = TacticalPhosphorGreen.copy(alpha = if (isMajor) 0.8f else 0.4f),
                    start = Offset(x, h - 2.dp.toPx()),
                    end = Offset(x, h - 2.dp.toPx() - tickLen),
                    strokeWidth = (if (isMajor) 1.2.dp else 0.8.dp).toPx()
                )
            }

            // Central horizontal axis
            drawLine(
                color = TacticalGridGreen,
                start = Offset(0f, centerY),
                end = Offset(w, centerY),
                strokeWidth = 0.8.dp.toPx()
            )

            // Dynamic horizontal oscillating audio waveform
            val path = Path()
            val points = 80
            for (step in 0..points) {
                val x = w * (step.toFloat() / points)
                val envelope = sin((step.toFloat() / points) * Math.PI).toFloat()
                val sin1 = sin(Math.toRadians(((step * 24.0) + wavePhase * 540.0))).toFloat()
                val sin2 = sin(Math.toRadians(((step * 50.0) - wavePhase * 1080.0))).toFloat() * 0.35f
                val deltaY = (sin1 + sin2) * (h * 0.42f) * envelope

                val y = centerY + deltaY
                if (step == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = TacticalBrightGreen,
                style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Tactical Signal Telemetry Equalizer Bars (Bottom-Left in reference image).
 * Displays jumping segmented green level bars.
 */
@Composable
fun TacticalEqualizerSpectrum(
    animProgress: Float = 0f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val barCount = 10
        val barWidth = (size.width - ((barCount + 1) * 3.dp.toPx())) / barCount
        val maxHeight = size.height - 4.dp.toPx()

        for (i in 0 until barCount) {
            val left = (i * (barWidth + 3.dp.toPx())) + 2.dp.toPx()
            // Height fluctuation per bar based on animProgress and bar index
            val wave = sin(animProgress * Math.PI * 2 + (i * 0.7)).toFloat()
            val heightFraction = ((wave * 0.35f) + 0.60f).coerceIn(0.2f, 1.0f)
            val barHeight = maxHeight * heightFraction

            // Bar background container
            drawRect(
                color = TacticalPhosphorGreen.copy(alpha = 0.15f),
                topLeft = Offset(left, size.height - maxHeight),
                size = Size(barWidth, maxHeight),
                style = Stroke(width = 0.8.dp.toPx())
            )

            // Active filled bar
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(TacticalBrightGreen, TacticalPhosphorGreen),
                    startY = size.height - barHeight,
                    endY = size.height
                ),
                topLeft = Offset(left, size.height - barHeight),
                size = Size(barWidth, barHeight)
            )

            // Peak tick indicator at the top of each bar
            drawLine(
                color = TacticalBrightGreen,
                start = Offset(left - 1.dp.toPx(), size.height - barHeight - 2.dp.toPx()),
                end = Offset(left + barWidth + 1.dp.toPx(), size.height - barHeight - 2.dp.toPx()),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

/**
 * Directional Sci-Fi Chevrons (`>>>>>>` or `<<<<<<`).
 */
@Composable
fun TacticalDirectionChevrons(
    isFacingRight: Boolean = true,
    chevronCount: Int = 6,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val h = size.height
        val w = size.width
        val stepX = w / chevronCount

        for (i in 0 until chevronCount) {
            val xBase = i * stepX
            val path = Path()
            if (isFacingRight) {
                path.moveTo(xBase, 0f)
                path.lineTo(xBase + stepX * 0.7f, h / 2f)
                path.lineTo(xBase, h)
            } else {
                path.moveTo(xBase + stepX * 0.7f, 0f)
                path.lineTo(xBase, h / 2f)
                path.lineTo(xBase + stepX * 0.7f, h)
            }
            drawPath(
                path = path,
                color = TacticalPhosphorGreen.copy(alpha = 0.85f),
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }
    }
}

/**
 * Heading Compass Dial (Right side miniature azimuth instrument).
 */
@Composable
fun TacticalAzimuthDial(
    headingDeg: Float = 45f,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = min(size.width, size.height) / 2f * 0.85f

        // Outer dial ring
        drawCircle(
            color = TacticalPhosphorGreen,
            radius = radius,
            center = center,
            style = Stroke(width = 1.2.dp.toPx())
        )

        // Circular ticks (every 10 degrees)
        for (deg in 0 until 360 step 10) {
            val rad = Math.toRadians(deg.toDouble())
            val isMajor = deg % 30 == 0
            val rIn = if (isMajor) radius * 0.75f else radius * 0.85f
            drawLine(
                color = TacticalPhosphorGreen.copy(alpha = if (isMajor) 0.9f else 0.45f),
                start = Offset(center.x + (rIn * cos(rad)).toFloat(), center.y + (rIn * sin(rad)).toFloat()),
                end = Offset(center.x + (radius * cos(rad)).toFloat(), center.y + (radius * sin(rad)).toFloat()),
                strokeWidth = (if (isMajor) 1.4.dp else 0.8.dp).toPx()
            )
        }

        // Center needle
        val needleRad = Math.toRadians(headingDeg.toDouble() - 90.0)
        val needleEnd = Offset(
            center.x + (radius * 0.7f * cos(needleRad)).toFloat(),
            center.y + (radius * 0.7f * sin(needleRad)).toFloat()
        )
        drawLine(
            color = TacticalAlertRed,
            start = center,
            end = needleEnd,
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Center pivot
        drawCircle(color = TacticalPhosphorGreen, radius = 3.dp.toPx(), center = center)
    }
}

/**
 * Coordinate Telemetry Registry Table (as shown in reference image: CODE | LAT | LONG).
 */
@Composable
fun TacticalTelemetryTable(
    users: List<OtherUserEntity>,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = TacticalPanelBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, TacticalPhosphorGreen.copy(alpha = 0.6f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TacticalDarkGreen.copy(alpha = 0.4f))
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("CODE", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TacticalBrightGreen, fontFamily = FontFamily.Monospace)
                Text("LAT", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TacticalBrightGreen, fontFamily = FontFamily.Monospace)
                Text("LONG", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TacticalBrightGreen, fontFamily = FontFamily.Monospace)
                Text("KM", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TacticalBrightGreen, fontFamily = FontFamily.Monospace)
            }

            // Display up to 5 detected users or telemetry samples
            val displayUsers = if (users.isNotEmpty()) users.take(5) else listOf(
                OtherUserEntity(username = "ASE", fullName = "Atlas Alpha", avatarUrl = "", bio = "", locationName = "Sector 1", latitude = 47.608, longitude = -122.335, distanceKm = 1.2),
                OtherUserEntity(username = "HPP", fullName = "Hyperion", avatarUrl = "", bio = "", locationName = "Sector 2", latitude = 47.618, longitude = -122.345, distanceKm = 2.4),
                OtherUserEntity(username = "OOY", fullName = "Orion Orbit", avatarUrl = "", bio = "", locationName = "Sector 3", latitude = 47.598, longitude = -122.315, distanceKm = 3.1),
                OtherUserEntity(username = "TRE", fullName = "Terra Explorer", avatarUrl = "", bio = "", locationName = "Sector 4", latitude = 47.628, longitude = -122.355, distanceKm = 4.8),
                OtherUserEntity(username = "PPO", fullName = "Pulse Beacon", avatarUrl = "", bio = "", locationName = "Sector 5", latitude = 47.588, longitude = -122.305, distanceKm = 5.0)
            )

            displayUsers.forEachIndexed { idx, u ->
                val code = u.username.take(3).uppercase()
                val latStr = String.format("%.2f", u.latitude)
                val lngStr = String.format("%.2f", u.longitude)
                val distStr = String.format("%.1f", u.distanceKm)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 1.5.dp, horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(code, fontSize = 7.5.sp, color = TacticalTextGreen, fontFamily = FontFamily.Monospace)
                    Text(latStr, fontSize = 7.5.sp, color = TacticalTextGreen.copy(alpha = 0.85f), fontFamily = FontFamily.Monospace)
                    Text(lngStr, fontSize = 7.5.sp, color = TacticalTextGreen.copy(alpha = 0.85f), fontFamily = FontFamily.Monospace)
                    Text(distStr, fontSize = 7.5.sp, color = TacticalBrightGreen, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
            }
        }
    }
}

/**
 * High-Tech World Map Continent Silhouette inside the circular radar (as in reference image).
 * Renders North America, South America, Eurasia, Africa, and Australia in glowing green phosphor.
 */
fun DrawScope.drawTacticalWorldMap(
    center: Offset,
    radius: Float,
    fillColor: Color = TacticalWorldMapFill,
    strokeColor: Color = TacticalWorldMapStroke
) {
    fun toOffset(normX: Float, normY: Float): Offset {
        return Offset(center.x + (normX * radius), center.y + (normY * radius))
    }

    // 1. North America & Greenland
    val naPath = Path().apply {
        val start = toOffset(-0.70f, -0.42f)
        moveTo(start.x, start.y)
        listOf(
            -0.58f to -0.48f,
            -0.42f to -0.52f,
            -0.24f to -0.58f, // Greenland
            -0.16f to -0.54f,
            -0.20f to -0.42f,
            -0.24f to -0.25f, // East Coast
            -0.28f to -0.10f, // Florida
            -0.38f to -0.06f, // Gulf
            -0.34f to 0.06f,  // Central America
            -0.44f to -0.02f, // Mexico West
            -0.58f to -0.18f, // California
            -0.66f to -0.32f, // Pacific NW
            -0.70f to -0.42f  // Alaska
        ).forEach { (x, y) ->
            val pt = toOffset(x, y)
            lineTo(pt.x, pt.y)
        }
        close()
    }
    drawPath(naPath, fillColor)
    drawPath(naPath, strokeColor, style = Stroke(width = 0.9.dp.toPx()))

    // 2. South America
    val saPath = Path().apply {
        val start = toOffset(-0.30f, 0.08f)
        moveTo(start.x, start.y)
        listOf(
            -0.22f to 0.10f,
            -0.10f to 0.22f, // Brazil Bulge
            -0.14f to 0.40f,
            -0.20f to 0.62f, // Argentina
            -0.24f to 0.72f, // Cape Horn
            -0.28f to 0.50f, // Chile
            -0.34f to 0.26f, // Peru
            -0.30f to 0.08f
        ).forEach { (x, y) ->
            val pt = toOffset(x, y)
            lineTo(pt.x, pt.y)
        }
        close()
    }
    drawPath(saPath, fillColor)
    drawPath(saPath, strokeColor, style = Stroke(width = 0.9.dp.toPx()))

    // 3. Europe & Africa
    val eaPath = Path().apply {
        val start = toOffset(-0.04f, -0.44f) // UK/North Sea
        moveTo(start.x, start.y)
        listOf(
            0.06f to -0.48f, // Scandinavia
            0.12f to -0.40f,
            0.02f to -0.30f, // France
            -0.08f to -0.22f, // Spain
            -0.06f to -0.12f, // Morocco
            0.12f to -0.12f, // Egypt / North Africa
            0.22f to 0.06f,  // Horn of Africa
            0.18f to 0.26f,  // East Africa
            0.08f to 0.56f,  // South Africa
            0.00f to 0.36f,  // West Coast
            -0.06f to 0.12f, // Gulf of Guinea
            -0.14f to 0.02f, // Senegal bulge
            -0.04f to -0.16f,
            -0.04f to -0.44f
        ).forEach { (x, y) ->
            val pt = toOffset(x, y)
            lineTo(pt.x, pt.y)
        }
        close()
    }
    drawPath(eaPath, fillColor)
    drawPath(eaPath, strokeColor, style = Stroke(width = 0.9.dp.toPx()))

    // 4. Eurasia (Asia, Russia, China, India)
    val asiaPath = Path().apply {
        val start = toOffset(0.18f, -0.48f)
        moveTo(start.x, start.y)
        listOf(
            0.42f to -0.52f, // Siberia
            0.68f to -0.44f, // Kamchatka
            0.62f to -0.28f, // Japan/Korea
            0.50f to -0.14f, // East China
            0.44f to 0.06f,  // SE Asia
            0.30f to -0.04f, // India
            0.18f to -0.10f, // Middle East
            0.18f to -0.32f,
            0.18f to -0.48f
        ).forEach { (x, y) ->
            val pt = toOffset(x, y)
            lineTo(pt.x, pt.y)
        }
        close()
    }
    drawPath(asiaPath, fillColor)
    drawPath(asiaPath, strokeColor, style = Stroke(width = 0.9.dp.toPx()))

    // 5. Australia & Oceania
    val ausPath = Path().apply {
        val start = toOffset(0.48f, 0.28f)
        moveTo(start.x, start.y)
        listOf(
            0.62f to 0.38f, // East Australia
            0.58f to 0.54f, // Sydney / South
            0.48f to 0.52f, // Adelaide
            0.40f to 0.44f, // West Australia
            0.48f to 0.28f
        ).forEach { (x, y) ->
            val pt = toOffset(x, y)
            lineTo(pt.x, pt.y)
        }
        close()
    }
    drawPath(ausPath, fillColor)
    drawPath(ausPath, strokeColor, style = Stroke(width = 0.9.dp.toPx()))
}

/**
 * Top-Right 6x6 Digital Pixel Heat Matrix Widget (as in reference image top-right).
 */
@Composable
fun TacticalPixelHeatMatrix(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val rows = 6
        val cols = 6
        val pad = 2.dp.toPx()
        val tileW = (size.width - ((cols - 1) * pad)) / cols
        val tileH = (size.height - ((rows - 1) * pad)) / rows

        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val x = c * (tileW + pad)
                val y = r * (tileH + pad)
                // Pixel intensity pattern
                val isBright = (r + c * 2) % 3 == 0 || (r == 0 && c == cols - 1) || (r == rows - 1 && c == 0)
                val isMedium = (r * c) % 2 == 1
                val color = when {
                    isBright -> TacticalBrightGreen
                    isMedium -> TacticalPhosphorGreen.copy(alpha = 0.7f)
                    else -> TacticalDarkGreen.copy(alpha = 0.4f)
                }
                drawRect(
                    color = color,
                    topLeft = Offset(x, y),
                    size = Size(tileW, tileH)
                )
            }
        }
    }
}

/**
 * Main Central Tactical Live Radar Canvas:
 * - Full 360-degree compass rim with degree tick marks and degree numbers (000°, 010°, 020°... 350°)
 * - Glowing phosphor World Map continent silhouettes under the grid
 * - Polar coordinate grid with 8 concentric rings and 36 radial spoke lines
 * - High-tech luminous sweep beam with phosphor fade trail
 * - Center precision crosshairs and bullseye
 * - Target bracket reticles [ • ]
 */
@Composable
fun TacticalRadarGridCanvas(
    sweepAngle: Float,
    isLocationEnabled: Boolean,
    inspectUser: OtherUserEntity?,
    userProfile: UserProfileEntity,
    selectedRadiusKm: Double,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = (min(size.width, size.height) / 2f) * 0.78f

        // 0. Background Cartesian Grid Lines (Military CRT avionics grid)
        val gridSize = 22.dp.toPx()
        var gx = center.x % gridSize
        while (gx < size.width) {
            drawLine(
                color = TacticalGridGreen.copy(alpha = 0.18f),
                start = Offset(gx, 0f),
                end = Offset(gx, size.height),
                strokeWidth = 0.6.dp.toPx()
            )
            gx += gridSize
        }
        var gy = center.y % gridSize
        while (gy < size.height) {
            drawLine(
                color = TacticalGridGreen.copy(alpha = 0.18f),
                start = Offset(0f, gy),
                end = Offset(size.width, gy),
                strokeWidth = 0.6.dp.toPx()
            )
            gy += gridSize
        }

        // 1. World Map Continents glowing beneath the radar grid!
        drawTacticalWorldMap(center = center, radius = maxRadius)

        // 2. Outer Compass Rim (with degree ticks and labels)
        val outerRimRadius = maxRadius * 1.15f

        // Outer circular border with double rim
        drawCircle(
            color = TacticalPhosphorGreen,
            radius = outerRimRadius,
            center = center,
            style = Stroke(width = 1.6.dp.toPx())
        )
        drawCircle(
            color = TacticalPhosphorGreen.copy(alpha = 0.45f),
            radius = outerRimRadius + 2.5.dp.toPx(),
            center = center,
            style = Stroke(width = 0.8.dp.toPx())
        )

        // Compass tick marks around outer rim (every 2.5 degrees)
        for (deg in 0 until 360 step 5) {
            val rad = Math.toRadians(deg.toDouble())
            val isMajor10 = deg % 10 == 0
            val isMajor30 = deg % 30 == 0
            val tickLen = if (isMajor30) 7.dp.toPx() else if (isMajor10) 4.5.dp.toPx() else 2.5.dp.toPx()

            val rIn = outerRimRadius - tickLen
            val rOut = outerRimRadius
            drawLine(
                color = TacticalPhosphorGreen.copy(alpha = if (isMajor30) 1f else if (isMajor10) 0.75f else 0.4f),
                start = Offset(center.x + (rIn * cos(rad)).toFloat(), center.y + (rIn * sin(rad)).toFloat()),
                end = Offset(center.x + (rOut * cos(rad)).toFloat(), center.y + (rOut * sin(rad)).toFloat()),
                strokeWidth = (if (isMajor30) 1.5.dp else if (isMajor10) 1.dp else 0.7.dp).toPx()
            )
        }

        // Native canvas degree labels (000°, 010°, 020° ... 350° every 10 or 20 degrees)
        val textPaint = android.graphics.Paint().apply {
            color = android.graphics.Color.parseColor("#14FF00")
            textSize = 7.5.dp.toPx()
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.MONOSPACE
        }

        for (deg in 0 until 360 step 10) {
            val rad = Math.toRadians((deg - 90).toDouble())
            val labelR = outerRimRadius + 8.5.dp.toPx()
            val lx = center.x + (labelR * cos(rad)).toFloat()
            val ly = center.y + (labelR * sin(rad)).toFloat() + (2.5.dp.toPx())
            
            val degText = when (deg) {
                0 -> "N 0°"
                90 -> "E 90°"
                180 -> "S 180°"
                270 -> "W 270°"
                else -> String.format("%03d", deg)
            }
            
            drawContext.canvas.nativeCanvas.drawText(degText, lx, ly, textPaint)
        }

        // 3. Concentric Polar Grid Rings (8 concentric distance rings)
        val ringCount = 8
        for (i in 1..ringCount) {
            val r = maxRadius * (i.toFloat() / ringCount)
            val isOuter = i == ringCount
            val isMajorRing = i % 2 == 0 || isOuter
            drawCircle(
                color = if (isOuter) TacticalBrightGreen else if (isMajorRing) TacticalPhosphorGreen.copy(alpha = 0.85f) else TacticalGridGreenStrong,
                radius = r,
                center = center,
                style = Stroke(
                    width = (if (isOuter) 1.5.dp else if (isMajorRing) 1.dp else 0.6.dp).toPx(),
                    pathEffect = if (!isMajorRing) PathEffect.dashPathEffect(floatArrayOf(3f, 3f), 0f) else null
                )
            )
        }

        // 4. Radial Spoke Coordinate Lines (36 polar lines = every 10 degrees)
        for (deg in 0 until 360 step 10) {
            val rad = Math.toRadians(deg.toDouble())
            val isCardinal = deg % 90 == 0
            val isMajorDiagonal = deg % 45 == 0
            val isThirty = deg % 30 == 0
            val spokeColor = if (isCardinal) TacticalBrightGreen.copy(alpha = 0.9f)
            else if (isThirty || isMajorDiagonal) TacticalPhosphorGreen.copy(alpha = 0.6f)
            else TacticalGridGreen.copy(alpha = 0.35f)

            drawLine(
                color = spokeColor,
                start = center,
                end = Offset(
                    center.x + (maxRadius * cos(rad)).toFloat(),
                    center.y + (maxRadius * sin(rad)).toFloat()
                ),
                strokeWidth = (if (isCardinal) 1.2.dp else 0.7.dp).toPx()
            )
        }

        // 5. Center Precision Crosshairs (Crosshair reticle at coordinate 0,0)
        val crossLen = 16.dp.toPx()
        drawLine(
            color = TacticalNeonGreen,
            start = Offset(center.x - crossLen, center.y),
            end = Offset(center.x + crossLen, center.y),
            strokeWidth = 1.5.dp.toPx()
        )
        drawLine(
            color = TacticalNeonGreen,
            start = Offset(center.x, center.y - crossLen),
            end = Offset(center.x, center.y + crossLen),
            strokeWidth = 1.5.dp.toPx()
        )
        drawCircle(
            color = TacticalBrightGreen,
            radius = 3.5.dp.toPx(),
            center = center
        )

        // 6. Rotating Radar Sweep Beam with Luminous Phosphor Glow & Flare
        if (isLocationEnabled) {
            val sweepBrush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x0000FF66),
                    Color(0x2500FF66),
                    Color(0x6500FF66),
                    Color(0xD014FF00) // sharp bright leading flare
                ),
                center = center
            )

            drawArc(
                brush = sweepBrush,
                startAngle = sweepAngle - 65f,
                sweepAngle = 65f,
                useCenter = true,
                topLeft = Offset(center.x - maxRadius, center.y - maxRadius),
                size = Size(maxRadius * 2, maxRadius * 2)
            )

            // Leading bright vector line of the sweep beam
            val leadingRad = Math.toRadians(sweepAngle.toDouble())
            drawLine(
                color = TacticalNeonGreen,
                start = center,
                end = Offset(
                    center.x + (maxRadius * cos(leadingRad)).toFloat(),
                    center.y + (maxRadius * sin(leadingRad)).toFloat()
                ),
                strokeWidth = 2.dp.toPx()
            )
        }

        // 7. Target Lock Vector Line if user is inspecting a specific blip
        if (inspectUser != null) {
            val u = inspectUser
            val bearing = LocationHelper.calculateBearing(userProfile.latitude, userProfile.longitude, u.latitude, u.longitude)
            val angleRad = Math.toRadians(bearing - 90.0)
            val fraction = (u.distanceKm / selectedRadiusKm).coerceIn(0.2, 0.92)
            val targetOffset = Offset(
                center.x + (maxRadius * fraction * cos(angleRad)).toFloat(),
                center.y + (maxRadius * fraction * sin(angleRad)).toFloat()
            )

            // Glowing green dashed vector line to locked target
            drawLine(
                color = TacticalBrightGreen,
                start = center,
                end = targetOffset,
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Outer Target Lock Square Reticle
            val sqSize = 22.dp.toPx()
            drawRect(
                color = TacticalBrightGreen,
                topLeft = Offset(targetOffset.x - sqSize, targetOffset.y - sqSize),
                size = Size(sqSize * 2, sqSize * 2),
                style = Stroke(width = 1.5.dp.toPx())
            )
            // Lock circle
            drawCircle(
                color = TacticalBrightGreen.copy(alpha = 0.25f),
                radius = sqSize * 1.25f,
                center = targetOffset
            )
        }
    }
}
