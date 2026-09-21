package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.LocaliiiyAccentCoral
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal

/**
 * High-definition app logo badge for Localiiiy.
 * Placed prominently in the top bar before the word "Localiiiy".
 */
@Composable
fun LocaliiiyAppLogoBadge(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "AppLogoPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val ringRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ringRotation"
    )

    Box(
        modifier = modifier
            .size(size)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        // Outer glowing halo
        Box(
            modifier = Modifier
                .size(size)
                .scale(pulseGlow)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            LocaliiiyAccentMint.copy(alpha = 0.35f),
                            LocaliiiyPrimaryTeal.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Iridescent rotating gradient ring
        Box(
            modifier = Modifier
                .size(size * 0.92f)
                .rotate(ringRotation)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(
                            LocaliiiyAccentCoral,
                            Color(0xFFFFB703),
                            LocaliiiyAccentMint,
                            LocaliiiyPrimaryTeal,
                            Color(0xFF6366F1),
                            LocaliiiyAccentCoral
                        )
                    )
                )
        )

        // Deep Navy / Dark Inner core
        Box(
            modifier = Modifier
                .size(size * 0.76f)
                .clip(CircleShape)
                .background(Color(0xFF081528))
                .border(1.dp, Color.White.copy(alpha = 0.35f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // App Vector Icon (ic_launcher_foreground)
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Localiiiy App Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .scale(1.5f)
            )
        }

        // Coral live beacon dot on top-right
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 1.dp, y = (-1).dp)
                .size(size * 0.26f)
                .clip(CircleShape)
                .background(LocaliiiyAccentCoral)
                .border(1.dp, Color(0xFF081528), CircleShape)
        )
    }
}
