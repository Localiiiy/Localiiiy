package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliiiyPrimaryTeal

@Composable
fun NeighborhoodPulseTrendsComponent() {
    val trendPoints = remember {
        listOf(15f, 40f, 25f, 60f, 50f)
    }
    val categories = listOf("Coffee", "Hiking", "Market", "Music", "Art")

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Neighborhood Pulse Trends",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Local engagement spikes in the last 24 hours",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                if (trendPoints.size < 2) return@Canvas
                val maxVal = trendPoints.maxOrNull() ?: 1f
                val minVal = trendPoints.minOrNull() ?: 0f
                val range = (maxVal - minVal).coerceAtLeast(1f)
                val stepX = size.width / (trendPoints.size - 1)
                val path = Path()

                trendPoints.forEachIndexed { i, pt ->
                    val x = i * stepX
                    val y = size.height - ((pt - minVal) / range) * (size.height * 0.7f) - (size.height * 0.15f)
                    if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    drawCircle(
                        color = Color(0xFF00BFA5),
                        radius = 4.dp.toPx(),
                        center = androidx.compose.ui.geometry.Offset(x, y)
                    )
                }
                drawPath(path, color = Color(0xFF00BFA5), style = Stroke(width = 3.dp.toPx()))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                categories.forEach { cat ->
                    Text(cat, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
