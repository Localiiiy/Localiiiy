package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

enum class EngagementMetric(val title: String, val unit: String, val color: Color) {
    INTERACTIONS("Interactions", "pts", Color(0xFF00BFA5)),
    VIEWS("Views", "views", Color(0xFFFF7A00)),
    CONNECTIONS("Connections", "connected", Color(0xFF38BDF8))
}

@Composable
fun ProfileWeeklyEngagementChart(
    modifier: Modifier = Modifier,
    onOpenFullAnalytics: () -> Unit = {}
) {
    var selectedMetric by remember { mutableStateOf(EngagementMetric.INTERACTIONS) }
    var isExpanded by remember { mutableStateOf(true) }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    // Weekly engagement points: Mon to Sun
    val interactionsData = remember { listOf(420f, 580f, 750f, 690f, 1150f, 1380f, 960f) }
    val viewsData = remember { listOf(1200f, 1650f, 2100f, 1950f, 3400f, 4100f, 2800f) }
    val connectionsData = remember { listOf(14f, 22f, 35f, 28f, 64f, 78f, 45f) }

    val currentPoints = when (selectedMetric) {
        EngagementMetric.INTERACTIONS -> interactionsData
        EngagementMetric.VIEWS -> viewsData
        EngagementMetric.CONNECTIONS -> connectionsData
    }

    val totalWeeklyStat = when (selectedMetric) {
        EngagementMetric.INTERACTIONS -> "5,930"
        EngagementMetric.VIEWS -> "17,200"
        EngagementMetric.CONNECTIONS -> "+286"
    }

    val growthPercentage = when (selectedMetric) {
        EngagementMetric.INTERACTIONS -> "+24.8%"
        EngagementMetric.VIEWS -> "+38.2%"
        EngagementMetric.CONNECTIONS -> "+19.4%"
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_weekly_engagement_chart")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = selectedMetric.color.copy(alpha = 0.2f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Timeline,
                                contentDescription = null,
                                tint = selectedMetric.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "7-Day Hyperlocal Pulse",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Grounded community reach & analytics",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onOpenFullAnalytics,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInFull,
                            contentDescription = "Full Analytics",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isExpanded = !isExpanded
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(14.dp))

                    // Metric Switcher Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EngagementMetric.values().forEach { metric ->
                            val isSelected = metric == selectedMetric
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) metric.color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                border = if (isSelected) BorderStroke(1.2.dp, metric.color) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        selectedMetric = metric
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = metric.title,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) metric.color else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Total metric summary badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = totalWeeklyStat,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = selectedMetric.unit,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = selectedMetric.color,
                                modifier = Modifier.padding(bottom = 3.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF00E676).copy(alpha = 0.15f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    tint = Color(0xFF00E676),
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = growthPercentage,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF00E676)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Native Canvas Line Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("weekly_engagement_graph")
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            if (currentPoints.size < 2) return@Canvas
                            val maxVal = currentPoints.maxOrNull() ?: 1f
                            val minVal = currentPoints.minOrNull() ?: 0f
                            val range = (maxVal - minVal).coerceAtLeast(1f)
                            val stepX = size.width / (currentPoints.size - 1)
                            val path = Path()

                            currentPoints.forEachIndexed { i, pt ->
                                val x = i * stepX
                                val y = size.height - ((pt - minVal) / range) * (size.height * 0.75f) - (size.height * 0.12f)
                                if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                                drawCircle(
                                    color = selectedMetric.color,
                                    radius = 4.dp.toPx(),
                                    center = Offset(x, y)
                                )
                            }
                            drawPath(path, color = selectedMetric.color, style = Stroke(width = 3.dp.toPx()))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Daily breakdown indicator pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        days.forEachIndexed { idx, day ->
                            val isPeak = idx == 5 // Saturday peak
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isPeak) selectedMetric.color.copy(alpha = 0.25f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                                    border = if (isPeak) BorderStroke(1.dp, selectedMetric.color) else null,
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = day.first().toString(),
                                            fontSize = 9.sp,
                                            fontWeight = if (isPeak) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isPeak) selectedMetric.color else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
