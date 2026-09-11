import re

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AirplanemodeActive
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.OtherUserEntity
import com.example.data.PostEntity
import com.example.data.ClipEntity
import com.example.data.MarketItemEntity
import com.example.data.UserProfileEntity
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LiveRadarComponent(
    isRefreshing: Boolean = false,
    userProfile: UserProfileEntity,
    nearbyUsers: List<OtherUserEntity>,
    nearbyPosts: List<PostEntity>,
    nearbyClips: List<ClipEntity> = emptyList(),
    nearbyMarketItems: List<MarketItemEntity> = emptyList(),
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
    val radarDark = Color(0xFF002200)
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

    Column(modifier = modifier.fillMaxWidth().background(Color.Black)) {
        // Radar Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .background(Color(0xFF051105))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            val planePainter = rememberVectorPainter(Icons.Default.AirplanemodeActive)
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
                
                // Blips for users/posts
                val combinedCount = minOf(nearbyUsers.size + nearbyPosts.size + nearbyClips.size + nearbyMarketItems.size, 12)
                for (i in 0 until combinedCount) {
                    val angle = (i * 137.5f) % 360f
                    val dist = (radius * 0.2f) + (radius * 0.7f * ((i * 17) % 100) / 100f)
                    val blipX = center.x + dist * cos(Math.toRadians(angle.toDouble())).toFloat()
                    val blipY = center.y + dist * sin(Math.toRadians(angle.toDouble())).toFloat()
                    
                    // Show blip if sweep has passed over it recently
                    val angleDiff = (sweepAngle - angle + 360) % 360
                    val alpha = if (angleDiff < 90) 1f - (angleDiff / 90f) else 0.2f
                    
                    translate(left = blipX - 12.dp.toPx(), top = blipY - 12.dp.toPx()) {
                        rotate(degrees = angle + 45f) {
                            with(planePainter) {
                                draw(size = Size(24.dp.toPx(), 24.dp.toPx()), alpha = alpha, colorFilter = ColorFilter.tint(Color.White))
                            }
                        }
                    }
                }
                
                // Center
                drawCircle(color = radarColor, radius = 4.dp.toPx(), center = center)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Nearby Items List
        Text(
            text = "Nearby Pulse Items",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = radarColor),
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        val allItems = remember(nearbyPosts, nearbyClips, nearbyMarketItems) {
            val list = mutableListOf<Any>()
            list.addAll(nearbyPosts.take(5))
            list.addAll(nearbyClips.take(5))
            list.addAll(nearbyMarketItems.take(5))
            list.shuffled()
        }
        
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allItems) { item ->
                when (item) {
                    is PostEntity -> {
                        NearbyCard(title = "Post by ${item.username}", icon = Icons.Default.Person, color = Color(0xFF00FF41))
                    }
                    is ClipEntity -> {
                        NearbyCard(title = "Clip by ${item.username}", icon = Icons.Default.PlayArrow, color = Color(0xFF00FF41))
                    }
                    is MarketItemEntity -> {
                        NearbyCard(title = item.title, icon = Icons.Default.Store, color = Color(0xFF00FF41))
                    }
                }
            }
        }
    }
}

@Composable
fun NearbyCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0A1F0A)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f)),
        modifier = Modifier.width(140.dp).height(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(color = color),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
"""

with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "w") as f:
    f.write(content)
