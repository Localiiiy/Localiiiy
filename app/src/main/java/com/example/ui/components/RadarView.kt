package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.data.model.RadarItem
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RadarView(
    items: List<RadarItem>,
    modifier: Modifier = Modifier,
    onItemClick: (RadarItem) -> Unit
) {
    var selectedItem by remember { mutableStateOf<RadarItem?>(items.firstOrNull()) }

    val infiniteTransition = rememberInfiniteTransition(label = "radar_sweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sweep_angle"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(16.dp)
                .clip(CircleShape)
                .background(Color(0xFF0D1B1E))
                .testTag("radar_canvas_container"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val maxRadius = size.width / 2f - 16.dp.toPx()

                val rings = listOf(0.33f, 0.66f, 1.0f)
                rings.forEach { ratio ->
                    drawCircle(
                        color = Color(0xFF0FA958).copy(alpha = 0.25f),
                        radius = maxRadius * ratio,
                        center = center,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }

                drawLine(
                    color = Color(0xFF0FA958).copy(alpha = 0.2f),
                    start = Offset(center.x - maxRadius, center.y),
                    end = Offset(center.x + maxRadius, center.y),
                    strokeWidth = 1.dp.toPx()
                )
                drawLine(
                    color = Color(0xFF0FA958).copy(alpha = 0.2f),
                    start = Offset(center.x, center.y - maxRadius),
                    end = Offset(center.x, center.y + maxRadius),
                    strokeWidth = 1.dp.toPx()
                )

                val sweepRad = Math.toRadians(sweepAngle.toDouble())
                val endX = center.x + maxRadius * cos(sweepRad).toFloat()
                val endY = center.y + maxRadius * sin(sweepRad).toFloat()

                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF0FA958), Color(0xFF0FA958).copy(alpha = 0.1f)),
                        start = center,
                        end = Offset(endX, endY)
                    ),
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 3.dp.toPx()
                )

                drawCircle(
                    color = Color(0xFF0FA958),
                    radius = 6.dp.toPx(),
                    center = center
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = center
                )
            }

            items.forEach { item ->
                val rad = Math.toRadians(item.angleDegrees.toDouble())
                val normDist = (item.distanceMeters.toFloat() / 700f).coerceIn(0.2f, 0.85f)

                val xOffsetPct = (normDist * cos(rad)).toFloat()
                val yOffsetPct = (normDist * sin(rad)).toFloat()

                val categoryColor = when (item.category) {
                    "Event" -> Color(0xFFFF9100)
                    "Spot" -> Color(0xFF00E5FF)
                    "Market" -> Color(0xFFFF4081)
                    else -> Color(0xFF76FF03)
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(
                            start = if (xOffsetPct > 0) (xOffsetPct * 120).dp else 0.dp,
                            end = if (xOffsetPct < 0) (-xOffsetPct * 120).dp else 0.dp,
                            top = if (yOffsetPct > 0) (yOffsetPct * 120).dp else 0.dp,
                            bottom = if (yOffsetPct < 0) (-yOffsetPct * 120).dp else 0.dp
                        )
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(categoryColor.copy(alpha = 0.25f))
                        .clickable {
                            selectedItem = item
                            onItemClick(item)
                        }
                        .testTag("radar_blip_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = categoryColor,
                        modifier = Modifier.size(16.dp)
                    ) {}
                }
            }
        }

        selectedItem?.let { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("radar_selected_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = item.imageUrl,
                        contentDescription = item.title,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${item.distanceMeters}m away • ${item.category}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = item.snippet,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}
