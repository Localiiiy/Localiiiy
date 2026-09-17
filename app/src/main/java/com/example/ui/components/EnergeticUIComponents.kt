package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.util.HapticHelper

/**
 * High-Energy Tooltip Box that responds to mouse cursor hover on web/emulator
 * and tap/long-press on mobile touch screens.
 */
@Composable
fun EnergeticTooltipBox(
    title: String,
    description: String,
    bullets: List<String> = emptyList(),
    icon: ImageVector = Icons.Default.Info,
    accentColor: Color = Color(0xFF00FF66),
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isHovered by remember { mutableStateOf(false) }
    var isToggledByTap by remember { mutableStateOf(false) }
    val showTooltip = isHovered || isToggledByTap
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Enter -> isHovered = true
                            PointerEventType.Exit -> isHovered = false
                        }
                    }
                }
            }
    ) {
        content()

        if (showTooltip) {
            Popup(
                alignment = Alignment.TopCenter,
                offset = IntOffset(0, -10),
                onDismissRequest = {
                    isHovered = false
                    isToggledByTap = false
                },
                properties = PopupProperties(focusable = false, dismissOnClickOutside = true)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF07140B),
                    border = BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(accentColor, accentColor.copy(alpha = 0.4f), Color(0xFF003311))
                        )
                    ),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .widthIn(min = 260.dp, max = 330.dp)
                        .padding(8.dp)
                        .clickable {
                            HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                            isToggledByTap = false
                            isHovered = false
                        }
                        .testTag("energetic_tooltip_popup")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        // Header
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = accentColor.copy(alpha = 0.2f),
                                border = BorderStroke(0.5.dp, accentColor)
                            ) {
                                Text(
                                    text = "INFO ⚡",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = accentColor,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = description,
                            fontSize = 10.5.sp,
                            color = Color(0xFFD0DCD2),
                            lineHeight = 15.sp
                        )

                        if (bullets.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            bullets.forEach { bullet ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 2.dp),
                                    verticalAlignment = Alignment.Top,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "•",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = accentColor
                                    )
                                    Text(
                                        text = bullet,
                                        fontSize = 10.sp,
                                        color = Color(0xFFAAB8AE),
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Tap or move cursor away to dismiss",
                            fontSize = 8.5.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Pre-configured tooltip for Master Ghost Mode
 */
@Composable
fun MasterGhostModeTooltip(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    EnergeticTooltipBox(
        title = "Master Ghost Mode (Stealth)",
        description = "Completely severs your RF beacon from the live proximity radar sweep while preserving your full discovery capabilities.",
        bullets = listOf(
            "Observation Only: You see nearby creators & pulses, but you are 100% invisible on radar.",
            "Zero Telemetry: No GPS or beacon broadcast leaves your device.",
            "Free for All: 100% free forever for every member of our community."
        ),
        icon = Icons.Default.VisibilityOff,
        accentColor = Color(0xFF00E5FF),
        modifier = modifier,
        content = content
    )
}

/**
 * Pre-configured tooltip for Audience Matrix
 */
@Composable
fun AudienceMatrixTooltip(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    EnergeticTooltipBox(
        title = "Audience Distribution Matrix",
        description = "Localiiiy's proprietary dual-velocity engine balances hyperlocal physical grounding with worldwide viral scaling.",
        bullets = listOf(
            "Neighbor (0–5 km): Guaranteed 100 organic impressions to physical neighbors in your geohash.",
            "City Pulse (50 km): Regional metropolitan reach across connected district hubs.",
            "Earth Wave (Worldwide): Unlocks algorithmic velocity to 195+ countries without follower gatekeepers."
        ),
        icon = Icons.Default.Public,
        accentColor = Color(0xFFFFD700),
        modifier = modifier,
        content = content
    )
}

/**
 * Hash-link anchor item representation for seamless in-app navigation
 */
data class HashAnchorItem(
    val id: String, // e.g., "#terms", "#escrow", "#ghost-mode"
    val label: String,
    val icon: ImageVector? = null,
    val targetIndex: Int = 0
)

/**
 * Smooth-Scrolling Hash-Link Anchor Navigation Bar
 * Allows users to jump smoothly between sections with animated indicators and tactile feedback.
 */
@Composable
fun HashLinkAnchorNavBar(
    anchors: List<HashAnchorItem>,
    selectedAnchorId: String,
    onAnchorClick: (HashAnchorItem) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF00FF66)
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    Surface(
        color = Color(0xFF030D05),
        border = BorderStroke(0.8.dp, accentColor.copy(alpha = 0.25f)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .testTag("hash_link_anchor_nav_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "NAV:",
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = accentColor,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                modifier = Modifier.padding(end = 2.dp)
            )

            anchors.forEach { anchor ->
                val isSelected = anchor.id == selectedAnchorId
                val animatedScale by animateFloatAsState(
                    targetValue = if (isSelected) 1.05f else 1.0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "anchor_scale"
                )

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) accentColor.copy(alpha = 0.22f) else Color(0xFF0A1F0D),
                    border = BorderStroke(
                        width = if (isSelected) 1.2.dp else 0.7.dp,
                        color = if (isSelected) accentColor else Color(0xFF1B3820)
                    ),
                    modifier = Modifier
                        .scale(animatedScale)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            HapticHelper.triggerHaptic(
                                context,
                                haptic,
                                androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove
                            )
                            onAnchorClick(anchor)
                        }
                        .testTag("anchor_tag_${anchor.id.replace("#", "")}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (anchor.icon != null) {
                            Icon(
                                imageVector = anchor.icon,
                                contentDescription = null,
                                tint = if (isSelected) accentColor else Color.Gray,
                                modifier = Modifier.size(11.dp)
                            )
                        }
                        Text(
                            text = anchor.id,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                            color = if (isSelected) accentColor else Color(0xFFAAAAAA),
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                        )
                        Text(
                            text = anchor.label,
                            fontSize = 9.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else Color(0xFFCCCCCC)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dynamic energetic audio/kinetic frequency equalizer bar
 * Adds visual rhythm and motion to feeds and radar displays
 */
@Composable
fun EnergeticLiveSoundWave(
    modifier: Modifier = Modifier,
    barCount: Int = 5,
    color: Color = Color(0xFF00FF66)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sound_wave")
    val animProgress by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sound_height"
    )

    Row(
        modifier = modifier.height(14.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = listOf(0.4f, 0.9f, 0.6f, 1.0f, 0.5f)
        for (i in 0 until barCount) {
            val baseH = heights[i % heights.size]
            val actualH = (baseH * animProgress).coerceIn(0.2f, 1.0f)
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .fillMaxHeight(actualH)
                    .clip(RoundedCornerShape(1.dp))
                    .background(color)
            )
        }
    }
}

/**
 * Energetic Pulsing Live Badge with animated ring
 */
@Composable
fun EnergeticPulseBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF00FF66)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_badge")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = color.copy(alpha = 0.15f),
        border = BorderStroke(0.8.dp, color),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(color)
            )
            Text(
                text = text,
                fontSize = 8.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
            )
        }
    }
}
