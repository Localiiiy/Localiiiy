package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Animated Like Button featuring the user-requested unique '👌' hand symbol
 * with responsive spring-scale bounce animation across all screens (Clips, Feed, Market, Studio).
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
    testTag: String = "animated_like_button"
) {
    val coroutineScope = rememberCoroutineScope()
    val scale = remember { Animatable(1f) }

    val handleClick = {
        coroutineScope.launch {
            scale.animateTo(
                targetValue = 1.38f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
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
        onLikeClick()
    }

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
        Text(
            text = "👌",
            fontSize = symbolSize,
            modifier = Modifier.scale(scale.value)
        )
        if (showCount && likesCount != null && likesCount > 0) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = likesCount.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
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
