package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ripple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.tween
import kotlinx.coroutines.launch

import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.background
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.border

import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

/**
 * Animated Like Button featuring the user-requested unique '👌' hand symbol
 * with 3D perspective depth, vivid glowing burst, and spring bounce animation
 * across all sections (Post/Feed, Clips, Market, Studio).
 */
@Composable
fun AnimatedLikeButton(
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
    likesCount: Int? = null,
    symbolSize: TextUnit = 22.sp,
    touchTargetSize: Dp = 40.dp,
    showCount: Boolean = false,
    verticalOrientation: Boolean = false,
    labelColor: Color? = null,
    testTag: String = "animated_like_button"
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }
    val transY = remember { Animatable(0f) }
    val rotZ = remember { Animatable(0f) }
    val rotY = remember { Animatable(0f) }
    val rotX = remember { Animatable(0f) }
    val burstRadius = remember { Animatable(0f) }
    val burstAlpha = remember { Animatable(0f) }
    val density = LocalDensity.current.density
    val haptic = LocalHapticFeedback.current

    val handleClick = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        coroutineScope.launch {
            // Vivid 3D Shockwave & Particle Burst
            launch {
                burstAlpha.snapTo(0.9f)
                burstRadius.snapTo(0f)
                burstRadius.animateTo(
                    targetValue = 34f,
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                )
                burstAlpha.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(180)
                )
            }
            // Vertical 3D Jump & Pop
            launch {
                transY.animateTo(-14f, tween(100, easing = FastOutSlowInEasing))
                transY.animateTo(
                    targetValue = 0f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }
            // Dynamic Spring Bounce Scale
            launch {
                scale.animateTo(
                    targetValue = 1.6f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioHighBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                scale.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            // 3D Perspective Multi-Axis Spin & Tilt
            launch {
                rotY.animateTo(35f, tween(90))
                rotY.animateTo(-25f, tween(100))
                rotY.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
            launch {
                rotX.animateTo(-28f, tween(90))
                rotX.animateTo(18f, tween(100))
                rotX.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
            launch {
                rotZ.animateTo(-22f, tween(110))
                rotZ.animateTo(18f, tween(110))
                rotZ.animateTo(0f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
        }
        onLikeClick()
    }

    val handContent: @Composable () -> Unit = {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(symbolSize.value.dp + 16.dp)
        ) {
            // Radiant Multi-Hue Particle Burst & Shockwave Ring on Click
            if (burstAlpha.value > 0.01f) {
                // Expanding Radial Aura
                Box(
                    modifier = Modifier
                        .size(burstRadius.value.dp * 2)
                        .alpha(burstAlpha.value)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.8f),
                                    Color(0xFF00FFC2).copy(alpha = 0.5f),
                                    Color(0xFFFF4081).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )

                // Outer Shockwave Ring
                Box(
                    modifier = Modifier
                        .size(burstRadius.value.dp * 1.8f)
                        .alpha(burstAlpha.value)
                        .border(
                            width = 1.5.dp,
                            color = Color(0xFFFFD700).copy(alpha = burstAlpha.value),
                            shape = CircleShape
                        )
                )

                // Floating Star Sparkles
                Text(
                    text = "✨",
                    fontSize = 11.sp,
                    modifier = Modifier
                        .offset(
                            x = (burstRadius.value * 0.7f).dp,
                            y = (-burstRadius.value * 0.7f).dp
                        )
                        .alpha(burstAlpha.value)
                )
                Text(
                    text = "✨",
                    fontSize = 10.sp,
                    modifier = Modifier
                        .offset(
                            x = (-burstRadius.value * 0.65f).dp,
                            y = (-burstRadius.value * 0.65f).dp
                        )
                        .alpha(burstAlpha.value)
                )
            }

            // Persistent 3D Luminous Aura when Liked
            if (isLiked) {
                Box(
                    modifier = Modifier
                        .size(symbolSize.value.dp + 10.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFFD700).copy(alpha = 0.35f),
                                    Color(0xFF00FFC2).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                )
            }

            // High-Fidelity 3D Hand Symbol (Vivid & Tactile)
            Text(
                text = "👌",
                fontSize = symbolSize,
                modifier = Modifier
                    .alpha(if (isLiked) 1.0f else 0.82f)
                    .graphicsLayer {
                        this.scaleX = scale.value
                        this.scaleY = scale.value
                        this.translationY = transY.value * density
                        this.rotationZ = rotZ.value
                        this.rotationY = rotY.value
                        this.rotationX = rotX.value
                        this.cameraDistance = 16f * density
                    }
            )
        }
    }

    val countTextColor = labelColor ?: if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    if (verticalOrientation) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .defaultMinSize(minWidth = touchTargetSize, minHeight = touchTargetSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = touchTargetSize / 2),
                    onClick = handleClick
                )
                .testTag(testTag)
        ) {
            handContent()
            if (showCount && likesCount != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (likesCount >= 1000) String.format("%.1fk", likesCount / 1000.0) else likesCount.toString(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = countTextColor
                )
            }
        }
    } else {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier
                .defaultMinSize(minWidth = touchTargetSize, minHeight = touchTargetSize)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = touchTargetSize / 2),
                    onClick = handleClick
                )
                .testTag(testTag)
        ) {
            handContent()
            if (showCount && likesCount != null && likesCount > 0) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likesCount.toString(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = countTextColor
                )
            }
        }
    }
}

/**
 * Static Comment Action Button featuring the user-requested '✍️' symbol
 * strictly WITHOUT animation in all views.
 */
@Composable
fun CommentActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    commentsCount: Int? = null,
    symbolSize: TextUnit = 20.sp,
    touchTargetSize: Dp = 40.dp,
    showCount: Boolean = false,
    testTag: String = "comment_action_button"
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier
            .defaultMinSize(minWidth = touchTargetSize, minHeight = touchTargetSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = touchTargetSize / 2),
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        Text(
            text = "✍️",
            fontSize = symbolSize
        )
        if (showCount && commentsCount != null && commentsCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = commentsCount.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Big Pop Double-Tap Like Burst (👌)
 */
@Composable
fun DoubleTapLikeBurst(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val scale = remember { Animatable(0.2f) }
    val alpha = remember { Animatable(0.95f) }

    LaunchedEffect(visible) {
        scale.animateTo(
            targetValue = 1.4f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
        )
        scale.animateTo(1.1f)
        alpha.animateTo(0f)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "👌",
            fontSize = 72.sp,
            modifier = Modifier.scale(scale.value)
        )
    }
}
