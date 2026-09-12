package com.example.ui.components.feed

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PostEntity
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Section 2.1: Tactile Tri-Dial Feed Lens
 * Curved haptic lens switching between 'NEIGHBOR (5km)', 'CITY (50km)', and 'EARTH (Global)'
 * with smooth crossfading animations.
 */
@Composable
fun TactileTriDialFeedLens(
    selectedDial: String,
    onDialSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val dials = listOf(
        Triple("NEIGHBOR", "5km", Icons.Default.NearMe),
        Triple("CITY", "50km", Icons.Default.LocationCity),
        Triple("EARTH", "Global", Icons.Default.Public)
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("tactile_tri_dial_lens"),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            dials.forEach { (scopeKey, distanceLabel, icon) ->
                val isSelected = selectedDial == scopeKey

                val bgBrush = if (isSelected) {
                    when (scopeKey) {
                        "NEIGHBOR" -> Brush.horizontalGradient(listOf(LocaliiiyPrimaryTeal, LocaliiiyAccentMint))
                        "CITY" -> Brush.horizontalGradient(listOf(LocaliiiyPrimary, LocaliiiyAccentCyan))
                        else -> Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF8B5CF6)))
                    }
                } else {
                    Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Transparent,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(bgBrush)
                        .clickable { onDialSelected(scopeKey) }
                        .padding(vertical = 8.dp, horizontal = 6.dp)
                        .testTag("tri_dial_${scopeKey.lowercase()}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = scopeKey,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = distanceLabel,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) Color.White.copy(alpha = 0.9f) else MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

/**
 * Section 2.2: Guaranteed Local Impression Counter
 * Displays confirmed physical neighbors who viewed the post before global velocity kicked in.
 */
@Composable
fun LocalSeedDeliveryBadge(
    localSeedCount: Int = 142,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.testTag("local_seed_badge"),
        shape = RoundedCornerShape(100.dp),
        color = LocaliiiyAccentMint.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = LocaliiiyAccentMint,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = "✓ $localSeedCount Local Neighbors Seeded",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (MaterialTheme.colorScheme.surface.hashCode() < 0) Color(0xFF00E676) else LocaliiiyPrimaryDark
            )
        }
    }
}

/**
 * Section 2.4: Decaying Flash Pulse Cards
 * Ephemeral neighborhood announcements with real-time countdown progress ring and glowing border.
 */
@Composable
fun EphemeralPulseCard(
    post: PostEntity,
    totalDurationHours: Int = 12,
    remainingMinutesInitial: Int = 420,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remainingMinutes by remember { mutableIntStateOf(remainingMinutesInitial) }
    val totalMinutes = totalDurationHours * 60
    val progress = (remainingMinutes.toFloat() / totalMinutes).coerceIn(0f, 1f)

    // Glowing border transition
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_decay_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    if (remainingMinutes <= 0) {
        LaunchedEffect(Unit) { onDismiss() }
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("ephemeral_pulse_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(
                listOf(
                    LocaliiiyTertiary.copy(alpha = glowAlpha),
                    LocaliiiyAccentCoral.copy(alpha = glowAlpha)
                )
            )
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                    // Countdown Ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(32.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxSize(),
                            color = LocaliiiyTertiary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeWidth = 3.dp,
                        )
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = LocaliiiyTertiary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FLASH PULSE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = LocaliiiyTertiaryDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = LocaliiiyTertiary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Expires in ${remainingMinutes / 60}h ${remainingMinutes % 60}m",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaliiiyTertiaryDark,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "@${post.username} • ${post.landmark ?: post.location ?: "Nearby"}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dismiss Flash Pulse",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.caption,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NearMe,
                            contentDescription = null,
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = "Hyperlocal Broadcast",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = LocaliiiyPrimaryTeal
                        )
                    }
                }

                Text(
                    text = "Self-destructs upon timer expiry",
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

/**
 * Section 2.7: Dual Velocity Progress Meter
 * Visualizes post transition from local saturation (0-100%) to global virality multiplier.
 */
@Composable
fun DualVelocityProgressMeter(
    localSaturation: Float = 0.88f, // 0.0 to 1.0 (88%)
    globalMultiplier: Float = 3.4f,  // 3.4x algorithmic distribution
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dual_velocity_meter"),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Distribution Velocity",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "${(localSaturation * 100).toInt()}% Saturation • ${globalMultiplier}x Global",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = LocaliiiyPrimaryTeal
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Two-stage progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                // Local Saturation Bar
                Box(
                    modifier = Modifier
                        .weight(localSaturation.coerceAtLeast(0.01f))
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(LocaliiiyPrimaryTeal, LocaliiiyAccentMint)
                            )
                        )
                )
                // Global Virality Multiplier Bar
                val globalWeight = ((globalMultiplier / 5.0f) * 0.5f).coerceIn(0.05f, 0.5f)
                Box(
                    modifier = Modifier
                        .weight(globalWeight)
                        .fillMaxHeight()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF6366F1), Color(0xFFEC4899))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "🌱 Local Physical Seed",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "🚀 Algorithmic Global Expansion",
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Section 2.9: Ghost Mode Feed Watermark
 * Subtle floating watermark pill 'Ghost Browsing Active • Untracked'
 * auto-dismissing after 3 seconds or on demand.
 */
@Composable
fun GhostModeFeedWatermark(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(isVisible) {
        if (isVisible) {
            delay(3500)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 },
        exit = fadeOut(tween(400)) + slideOutVertically(tween(400)) { it / 2 },
        modifier = modifier
    ) {
        Surface(
            shape = RoundedCornerShape(100.dp),
            color = Color(0xFF0F172A).copy(alpha = 0.92f),
            border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.4f)),
            shadowElevation = 6.dp,
            modifier = Modifier
                .padding(bottom = 76.dp)
                .testTag("ghost_mode_watermark_pill")
                .clickable { onDismiss() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VisibilityOff,
                    contentDescription = null,
                    tint = LocaliiiyAccentMint,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Ghost Browsing Active • Untracked",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Surface(
                    shape = CircleShape,
                    color = LocaliiiyAccentMint.copy(alpha = 0.2f),
                    modifier = Modifier.size(8.dp)
                ) {}
            }
        }
    }
}

/**
 * Section 2.10: Local Event Alert Highlighting (Priority Banner)
 * Pinned to top of the local feed with warning styling for urgent community notices or weather alerts.
 */
@Composable
fun PriorityBanner(
    title: String = "⚠️ Community Emergency Notice: Flash Flood Advisory",
    description: String = "Coarse radius: Capitol Hill / Central District. Avoid low-lying street crossings.",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("pulse_priority_banner"),
        shape = RoundedCornerShape(16.dp),
        color = LocaliiiyAccentCoral.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, LocaliiiyAccentCoral.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "Alert",
                tint = LocaliiiyAccentCoral,
                modifier = Modifier.size(22.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 12.5.sp
                    ),
                    color = LocaliiiyAccentCoral
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss Alert",
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/**
 * Section 2.11: Interactive Community Poll Pulses
 * Hyperlocal opinion polls restricted to verified nearby residents with real-time percentage progress bars.
 */
@Composable
fun PulsePollCard(
    question: String = "Should the neighborhood turn 12th Ave into a weekend pedestrian bazaar?",
    options: List<String> = listOf("Yes, full pedestrian bazaar 🎉", "Keep vehicle access open 🚗", "Trial on Sunday only ☀️"),
    initialVotes: List<Int> = listOf(78, 14, 32),
    onVote: (Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var hasVoted by remember { mutableStateOf(false) }
    var selectedOptionIndex by remember { mutableIntStateOf(-1) }
    var votes by remember { mutableStateOf(initialVotes) }

    val totalVotes = remember(votes) { votes.sum().coerceAtLeast(1) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("pulse_poll_card"),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        tonalElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Poll,
                        contentDescription = null,
                        tint = LocaliiiyPrimaryTeal,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "HYPERLOCAL POLL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = LocaliiiyPrimaryTeal
                    )
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = LocaliiiyAccentMint.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = LocaliiiyAccentMint,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "Local Neighbors Only",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocaliiiyPrimaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = question,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Poll Options
            options.forEachIndexed { index, optionText ->
                val voteCount = votes.getOrElse(index) { 0 }
                val percentage = (voteCount.toFloat() / totalVotes * 100).toInt()
                val isUserSelection = selectedOptionIndex == index

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isUserSelection) LocaliiiyPrimaryTeal.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(
                        1.dp,
                        if (isUserSelection) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            if (!hasVoted) {
                                selectedOptionIndex = index
                                hasVoted = true
                                val updated = votes.toMutableList()
                                updated[index] = updated[index] + 1
                                votes = updated
                                onVote(index)
                            }
                        }
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Background Fill Percentage if voted
                        if (hasVoted) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(percentage / 100f)
                                    .fillMaxHeight()
                                    .background(LocaliiiyPrimaryTeal.copy(alpha = 0.18f))
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = optionText,
                                fontSize = 12.5.sp,
                                fontWeight = if (isUserSelection) FontWeight.Bold else FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            if (hasVoted) {
                                Text(
                                    text = "$percentage%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = LocaliiiyPrimaryTeal
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "🗳️ ${totalVotes} verified neighbor votes • Anti-bot geographic validation active",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Section 2.13: Pull-to-Sonar Feed Refresh Indicator
 * Customized concentric expanding sonar rings that pulse green when refreshing local community content.
 */
@Composable
fun ConcentricSonarRefreshIndicator(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier
) {
    if (!isRefreshing) return

    val infiniteTransition = rememberInfiniteTransition(label = "sonar_sweep")
    val sonarRing1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar1"
    )
    val sonarRing2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sonar2"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        color = Color.Transparent
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(52.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Concentric Sonar Ring 1
                    drawCircle(
                        color = LocaliiiyAccentMint.copy(alpha = (1f - sonarRing1).coerceIn(0f, 0.7f)),
                        radius = size.minDimension / 2f * sonarRing1,
                        style = Stroke(width = 2.dp.toPx())
                    )
                    // Concentric Sonar Ring 2
                    drawCircle(
                        color = LocaliiiyPrimaryTeal.copy(alpha = (1f - sonarRing2).coerceIn(0f, 0.7f)),
                        radius = size.minDimension / 2f * sonarRing2,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = LocaliiiyAccentMint,
                    modifier = Modifier.size(14.dp)
                ) {}
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sweeping Local Radar Pulses...",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = LocaliiiyPrimaryTeal
            )
        }
    }
}

/**
 * Section 2.14: Creator Tip & Support Jar Modal Sheet
 * Direct peer-to-peer micro-tips with zero platform commission.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorTipSupportJarSheet(
    creatorName: String,
    onDismiss: () -> Unit,
    onTipSent: (Double) -> Unit
) {
    var selectedAmount by remember { mutableDoubleStateOf(3.0) }
    var customAmountText by remember { mutableStateOf("") }
    var tipSentSuccess by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = LocaliiiyTertiary.copy(alpha = 0.15f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = LocaliiiyTertiary,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Support @$creatorName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Direct peer-to-peer micro-support • 100% goes to creator • Zero platform commission",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Preset Buttons
            val presets = listOf(1.0, 3.0, 5.0, 10.0)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { amount ->
                    val isSelected = selectedAmount == amount && customAmountText.isEmpty()
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) LocaliiiyTertiary else MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, if (isSelected) LocaliiiyTertiaryDark else MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                selectedAmount = amount
                                customAmountText = ""
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = "$${amount.toInt()}",
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val finalAmount = customAmountText.toDoubleOrNull() ?: selectedAmount
                    onTipSent(finalAmount)
                    tipSentSuccess = true
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyTertiary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_confirm_creator_tip")
            ) {
                Icon(Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Send $${(customAmountText.toDoubleOrNull() ?: selectedAmount).toInt()} Tip to @$creatorName", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * Section 2.15: Audio Voice Pulse Cards
 * Audio-first snippets with animated sound wave scrubber, play/pause controls, and duration counters for 60s voice dispatches.
 */
@Composable
fun AudioVoicePulseCard(
    post: PostEntity,
    durationSeconds: Int = 54,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableFloatStateOf(0.35f) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("audio_voice_pulse_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
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
                    Surface(
                        shape = CircleShape,
                        color = LocaliiiyPrimaryTeal.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = null,
                                tint = LocaliiiyPrimaryTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "60s Local Voice Dispatch",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "@${post.username} • ${post.landmark ?: "Locality"}",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = "0:${durationSeconds - (currentProgress * durationSeconds).toInt()} remaining",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocaliiiyPrimaryTeal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Waveform scrubber + Play button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = { isPlaying = !isPlaying },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(LocaliiiyPrimaryTeal)
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Custom Waveform Bar Visualizer Canvas
                Canvas(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clickable {
                            currentProgress = ((10..90).random() / 100f)
                        }
                ) {
                    val barCount = 28
                    val spacing = size.width / barCount
                    for (i in 0 until barCount) {
                        val progressFraction = i.toFloat() / barCount
                        val isPlayed = progressFraction <= currentProgress
                        val heightMultiplier = (sin(i * 0.7f) * 0.4f + 0.6f).coerceIn(0.2f, 1f)
                        val barHeight = size.height * heightMultiplier

                        drawLine(
                            color = if (isPlayed) LocaliiiyPrimaryTeal else Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(x = i * spacing + spacing / 2, y = (size.height - barHeight) / 2),
                            end = Offset(x = i * spacing + spacing / 2, y = (size.height + barHeight) / 2),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            if (post.caption.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = post.caption,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Section 2.16: Hyperlocal Bookmark Shelves (Save to geographic folders)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HyperlocalBookmarkShelfSheet(
    post: PostEntity,
    onDismiss: () -> Unit,
    onFolderSelected: (String) -> Unit
) {
    val folders = listOf("Local Food & Artisan Coffee ☕", "Art Finds & Galleries 🎨", "Civic Tech & Events 🏛️", "Weekend Market Spots 🛍️", "General Neighborhood Gems 📍")
    var selectedFolder by remember { mutableStateOf(folders.first()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .navigationBarsPadding()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = LocaliiiyPrimaryTeal)
                Text(
                    text = "Save to Hyperlocal Shelf",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            folders.forEach { folder ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedFolder == folder) LocaliiiyPrimaryTeal.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, if (selectedFolder == folder) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedFolder = folder }
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = folder, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        if (selectedFolder == folder) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = LocaliiiyPrimaryTeal, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onFolderSelected(selectedFolder)
                    onDismiss()
                },
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_confirm_save_shelf")
            ) {
                Text("Save Pulse to Shelf", fontWeight = FontWeight.Bold)
            }
        }
    }
}
