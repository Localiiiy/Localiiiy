package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class BadgeTier(val threshold: Int, val title: String, val emoji: String, val color: Color) {
    INFINITY(1_000_000_000, "INFINITY", "♾️", Color(0xFF9C27B0)),
    ZENITH(500_000_000, "ZENITH", "🛸", Color(0xFF673AB7)),
    LOCALIIIY(100_000_000, "LOCALIIIY", "💠", Color(0xFF00BFA5)),
    VORTEX(50_000_000, "VORTEX", "🌀", Color(0xFFE91E63)),
    NEBULA(10_000_000, "NEBULA", "🌌", Color(0xFF3F51B5)),
    SOLAR(5_000_000, "SOLAR", "☀️", Color(0xFFFF9800)),
    TITAN(1_000_000, "TITAN", "🪐", Color(0xFFFF5722)),
    ORBIT(500_000, "ORBIT", "🛰️", Color(0xFF00BCD4)),
    AERO(100_000, "AERO", "🚀", Color(0xFF2196F3)),
    SPARK(10_000, "SPARK", "✨", Color(0xFFFFC107)),
    NONE(0, "", "", Color.Transparent);

    companion object {
        fun getTier(followers: Int): BadgeTier {
            return values().firstOrNull { followers >= it.threshold } ?: NONE
        }
    }
}

@Composable
fun CreatorBadgeIcon(followers: Int, modifier: Modifier = Modifier, showText: Boolean = false) {
    val tier = BadgeTier.getTier(followers)
    if (tier == BadgeTier.NONE) return

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(tier.color.copy(alpha = 0.15f))
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = tier.emoji, fontSize = 12.sp)
        if (showText) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = tier.title,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                color = tier.color
            )
        }
    }
}
