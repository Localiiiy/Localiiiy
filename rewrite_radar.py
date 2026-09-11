content = """package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.MarketplaceItemEntity
import com.example.data.UserProfileEntity
import kotlin.math.cos
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
    onRadiusChange: (Double) -> Unit = {},
    onLocationToggle: (Boolean) -> Unit = {},
    onPrivateToggle: (Boolean) -> Unit = {},
    onOpenPrivacySettings: () -> Unit = {},
    onUserClick: (OtherUserEntity) -> Unit = {},
    onPostClick: (PostEntity) -> Unit = {},
    onWaveAtUser: (OtherUserEntity) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val radarColor = Color(0xFF00FF41)
    val gridColor = Color(0xFF005511)
    
    val infiniteTransition = rememberInfiniteTransition()
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    val ranges = listOf(
        RadarRange("1km", 1.0),
        RadarRange("3km", 3.0),
        RadarRange("5km", 5.0),
        RadarRange("10km", 10.0),
        RadarRange("50km", 50.0),
        RadarRange("100km", 100.0),
        RadarRange("500km", 500.0),
        RadarRange("Country", 3000.0),
        RadarRange("Earth", 20000.0)
    )

    Column(modifier = modifier.fillMaxWidth().background(Color.Black)) {
        // Radar Screen
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color(0xFF051105))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val canvasWidth = maxWidth
            val canvasHeight = maxHeight
            
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = size.minDimension / 2
                
                // Draw Grid
                val step = size.width / 10
                for (i in 0..10) {
                    drawLine(gridColor.copy(alpha = 0.3f), start = Offset(i * step, 0f), end = Offset(i * step, size.height), strokeWidth = 1.dp.toPx())
                }
                val stepY = size.height / 10
                for (i in 0..10) {
                    drawLine(gridColor.copy(alpha = 0.3f), start = Offset(0f, i * stepY), end = Offset(size.width, i * stepY), strokeWidth = 1.dp.toPx())
                }
                
                // Draw Concentric Circles
                for (i in 1..4) {
                    drawCircle(
                        color = radarColor.copy(alpha = 0.5f),
                        radius = radius * (i / 4f),
                        center = center,
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
                
                // Crosshairs
                drawLine(radarColor.copy(alpha = 0.5f), start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())
                drawLine(radarColor.copy(alpha = 0.5f), start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())
                
                // Sweep
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(Color.Transparent, radarColor.copy(alpha = 0.1f), radarColor.copy(alpha = 0.6f)),
                        center = center
                    ),
                    startAngle = sweepAngle - 90f,
                    sweepAngle = 90f,
                    useCenter = true,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )
                drawLine(
                    color = radarColor,
                    start = center,
                    end = Offset(
                        x = center.x + radius * cos(Math.toRadians(sweepAngle.toDouble())).toFloat(),
                        y = center.y + radius * sin(Math.toRadians(sweepAngle.toDouble())).toFloat()
                    ),
                    strokeWidth = 2.dp.toPx()
                )
                
                // Center
                drawCircle(color = radarColor, radius = 4.dp.toPx(), center = center)
            }
            
            // Miniature cards of users/posts from inside the Radar circle
            val combinedCount = minOf(nearbyUsers.size + nearbyPosts.size, 12)
            val combinedItems = (nearbyUsers + nearbyPosts).take(combinedCount)
            
            val boxWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { canvasWidth.toPx() }
            val boxHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { canvasHeight.toPx() }
            val radiusPx = minOf(boxWidthPx, boxHeightPx) / 2
            val centerX = boxWidthPx / 2
            val centerY = boxHeightPx / 2

            combinedItems.forEachIndexed { i, item ->
                val angle = (i * 137.5f) % 360f
                val dist = (radiusPx * 0.2f) + (radiusPx * 0.7f * ((i * 17) % 100) / 100f)
                val blipX = centerX + dist * cos(Math.toRadians(angle.toDouble())).toFloat()
                val blipY = centerY + dist * sin(Math.toRadians(angle.toDouble())).toFloat()
                
                // Calculate opacity based on radar sweep
                val angleDiff = (sweepAngle - angle + 360) % 360
                val alpha = if (angleDiff < 90) 1f else 0.4f
                
                val avatarUrl = when (item) {
                    is OtherUserEntity -> item.avatarUrl
                    is PostEntity -> item.userAvatar
                    else -> ""
                }
                
                Box(
                    modifier = Modifier
                        .offset(
                            x = with(androidx.compose.ui.platform.LocalDensity.current) { (blipX - (16.dp.toPx())).toDp() },
                            y = with(androidx.compose.ui.platform.LocalDensity.current) { (blipY - (16.dp.toPx())).toDp() }
                        )
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(radarColor.copy(alpha = alpha * 0.5f))
                        .clickable {
                            if (item is OtherUserEntity) onUserClick(item)
                            else if (item is PostEntity) onPostClick(item)
                        }
                        .padding(2.dp)
                ) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop,
                        alpha = alpha
                    )
                }
            }
        }
        
        // Range Options
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(ranges) { range ->
                val isSelected = selectedRadiusKm == range.valueKm
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) radarColor else Color.DarkGray,
                    modifier = Modifier.clickable { onRadiusChange(range.valueKm) }
                ) {
                    Text(
                        text = range.label,
                        color = if (isSelected) Color.Black else Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
"""
with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "w") as f:
    f.write(content)
