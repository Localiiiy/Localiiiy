package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

/**
 * An eye-catching, fluid, cinematic opening splash animation for Localiiiy.
 * Features:
 * - Expanding multi-tier radar waves and glowing neighborhood pulses.
 * - Orbiting community beacon blips (neighbors, vibe pulses, local market items).
 * - Spring-scaled hero Localiiiy beacon logo with iridescent rotating gradient ring.
 * - Shimmering brand typography & live neighborhood HUD telemetry counter.
 * - Seamless automatic transition into the feed with tap-to-skip support.
 */
@Composable
fun LocaliiiyOpeningAnimation(
    onAnimationFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDismissing by remember { mutableStateOf(false) }
    var phase by remember { mutableIntStateOf(0) } // 0 = start, 1 = logo bounce, 2 = text & telemetry, 3 = ready

    // Timed phases for staggered cinematic choreography
    LaunchedEffect(Unit) {
        delay(100)
        phase = 1
        delay(500)
        phase = 2
        delay(700)
        phase = 3
        delay(1100) // Total ~2.4s duration
        isDismissing = true
        delay(400) // Allow exit animation to resolve
        onAnimationFinished()
    }

    // Infinite ambient animations (continuous while splash is visible)
    val infiniteTransition = rememberInfiniteTransition(label = "LocaliiiySplashInfinite")

    // Continuous smooth radar rotation
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RadarAngle"
    )

    // Pulsing radar ripples
    val rippleProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RippleProgress"
    )

    // Breathing glow aura
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "AuraScale"
    )

    // Gradient shimmer offset
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -300f,
        targetValue = 600f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ShimmerOffset"
    )

    // Staggered Spring Scale for the Hero Beacon Logo
    val logoScale by animateFloatAsState(
        targetValue = when {
            isDismissing -> 1.25f
            phase >= 1 -> 1f
            else -> 0.2f
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "LogoScale"
    )

    // Overall alpha for smooth entry and exit
    val splashAlpha by animateFloatAsState(
        targetValue = if (isDismissing) 0f else 1f,
        animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing),
        label = "SplashAlpha"
    )

    // Text & HUD alpha and slide
    val contentAlpha by animateFloatAsState(
        targetValue = if (phase >= 2 && !isDismissing) 1f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "ContentAlpha"
    )

    val contentOffset by animateFloatAsState(
        targetValue = if (phase >= 2) 0f else 40f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "ContentOffset"
    )

    // Tap to skip
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(splashAlpha)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF0F2B48),
                        Color(0xFF0B192C),
                        Color(0xFF040A14)
                    ),
                    center = Offset.Unspecified,
                    radius = 1200f
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                if (!isDismissing) {
                    isDismissing = true
                    onAnimationFinished()
                }
            }
            .testTag("localiiiy_opening_animation"),
        contentAlignment = Alignment.Center
    ) {
        // 1. Radar Ripples & Concentric Coordinate Grids Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f - 40.dp.toPx())
            val maxR = size.width * 0.75f

            // Concentric radar coordinate rings
            val ringAlphas = listOf(0.12f, 0.20f, 0.32f, 0.15f)
            for (i in 1..4) {
                val r = maxR * (i / 4f)
                drawCircle(
                    color = Color(0xFF00B4D8).copy(alpha = ringAlphas[i - 1]),
                    radius = r,
                    center = center,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = if (i % 2 == 1) PathEffect.dashPathEffect(floatArrayOf(12f, 12f), 0f) else null
                    )
                )
            }

            // Radar Axis Lines
            drawLine(
                color = Color(0xFF00E676).copy(alpha = 0.12f),
                start = Offset(center.x - maxR, center.y),
                end = Offset(center.x + maxR, center.y),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = Color(0xFF00E676).copy(alpha = 0.12f),
                start = Offset(center.x, center.y - maxR),
                end = Offset(center.x, center.y + maxR),
                strokeWidth = 1.dp.toPx()
            )

            // Dynamic Expanding Sonar Ripples (Tier 1 & Tier 2)
            val ripple1Radius = maxR * rippleProgress
            val ripple1Alpha = (1f - rippleProgress).coerceIn(0f, 0.6f)
            drawCircle(
                color = Color(0xFF00E676).copy(alpha = ripple1Alpha),
                radius = ripple1Radius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            val ripple2Progress = (rippleProgress + 0.5f) % 1f
            val ripple2Radius = maxR * ripple2Progress
            val ripple2Alpha = (1f - ripple2Progress).coerceIn(0f, 0.45f)
            drawCircle(
                color = Color(0xFFFF5722).copy(alpha = ripple2Alpha),
                radius = ripple2Radius,
                center = center,
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Radar Sweep Beam
            val sweepBrush = Brush.sweepGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0x0000B4D8),
                    Color(0x2200E676),
                    Color(0x6600E676)
                ),
                center = center
            )
            drawArc(
                brush = sweepBrush,
                startAngle = radarRotation,
                sweepAngle = 75f,
                useCenter = true,
                topLeft = Offset(center.x - maxR, center.y - maxR),
                size = androidx.compose.ui.geometry.Size(maxR * 2, maxR * 2)
            )

            // Orbiting Neighbor & Beacon Blips
            val blips = listOf(
                Triple(0.42f, 45f, Color(0xFF00E676)),   // Mint (Neighbor)
                Triple(0.65f, 160f, Color(0xFFFF5722)),  // Coral (Local Market)
                Triple(0.82f, 280f, Color(0xFFFFB703)),  // Amber (Live Vibe)
                Triple(0.55f, 325f, Color(0xFF00B4D8)),  // Cyan (Community Pulse)
                Triple(0.70f, 85f, Color(0xFFFF3366))    // Heart (Direct Message)
            )

            blips.forEach { (distFraction, baseAngle, color) ->
                val currentAngle = (baseAngle + radarRotation * 0.4f) % 360f
                val rad = Math.toRadians(currentAngle.toDouble())
                val r = maxR * distFraction
                val blipX = center.x + (r * cos(rad)).toFloat()
                val blipY = center.y + (r * sin(rad)).toFloat()

                // Glow ring
                drawCircle(
                    color = color.copy(alpha = 0.35f),
                    radius = 9.dp.toPx(),
                    center = Offset(blipX, blipY)
                )
                // Solid dot
                drawCircle(
                    color = color,
                    radius = 4.dp.toPx(),
                    center = Offset(blipX, blipY)
                )
            }
        }

        // 2. Center Content: Hero Beacon, Typography & Scanning HUD
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-20).dp)
        ) {
            // Hero Animated Beacon Logo
            Box(
                modifier = Modifier
                    .scale(logoScale * auraScale)
                    .size(110.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Soft Neon Glow
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00E676).copy(alpha = 0.4f),
                                    Color(0xFF00B4D8).copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Iridescent Rotating Gradient Outer Ring
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .rotate(radarRotation * 0.8f)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFFFF5722),
                                    Color(0xFFFFB703),
                                    Color(0xFF00E676),
                                    Color(0xFF00B4D8),
                                    Color(0xFFFF3366),
                                    Color(0xFFFF5722)
                                )
                            )
                        )
                )

                // Inner Deep Navy Glass Core
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF081528))
                        .border(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.6f),
                                    Color.White.copy(alpha = 0.1f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Emblem: Localiiiy Pin + Community Heart Icon
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Localiiiy Beacon",
                            tint = LocaliiiyAccentMint,
                            modifier = Modifier.size(38.dp)
                        )
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFF081528),
                            modifier = Modifier
                                .size(13.dp)
                                .offset(y = (-4).dp)
                        )
                    }
                }

                // Tiny Pulse Badge on Beacon
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(LocaliiiyAccentCoral)
                        .border(2.dp, Color(0xFF081528), CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Animated Typography "Localiiiy"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(contentAlpha)
                    .offset(y = contentOffset.dp)
            ) {
                // Wordmark with Dynamic Gradient Shimmer
                Text(
                    text = "Localiiiy",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Tagline & Badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = LocaliiiyAccentMint,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "HYPERLOCAL SOCIAL & MARKET",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        ),
                        color = LocaliiiyAccentMint
                    )
                    Icon(
                        imageVector = Icons.Default.Storefront,
                        contentDescription = null,
                        tint = LocaliiiyAccentMint,
                        modifier = Modifier.size(13.dp)
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Realtime Neighborhood Telemetry Scanner HUD Pill
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF00E676).copy(alpha = 0.4f),
                                Color(0xFF00B4D8).copy(alpha = 0.2f)
                            )
                        )
                    ),
                    modifier = Modifier.clip(RoundedCornerShape(100.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Flashing Live Indicator Dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(LocaliiiyAccentMint)
                        )

                        Text(
                            text = when {
                                phase < 2 -> "📡 Tuning into local radar..."
                                phase < 3 -> "📍 Scanning Pike Place • 3.0 km..."
                                else -> "✨ 48 Neighbors & 12 Bazaar Deals Live"
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtle Progress Indicator Bar
                Box(
                    modifier = Modifier
                        .width(140.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(if (phase >= 3) 1f else if (phase >= 2) 0.65f else 0.25f)
                            .clip(RoundedCornerShape(100.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(LocaliiiyAccentMint, LocaliiiyAccentCyan, LocaliiiyAccentCoral)
                                )
                            )
                    )
                }
            }
        }

        // Bottom Skip / Tap Hint
        Text(
            text = "Tap anywhere to explore",
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color.White.copy(alpha = 0.4f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 36.dp)
                .alpha(contentAlpha)
        )
    }
}
