package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.HapticHelper
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

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

    val bottomAxisFormatter = remember {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
            val idx = value.toInt()
            if (idx in days.indices) days[idx] else ""
        }
    }

    val startAxisFormatter = remember(selectedMetric) {
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _ ->
            when {
                value >= 1000f -> "${(value / 1000f).toInt()}k"
                else -> "${value.toInt()}"
            }
        }
    }

    // Weekly engagement points: Mon to Sun
    val interactionsData = remember {
        listOf(
            FloatEntry(0f, 420f),
            FloatEntry(1f, 580f),
            FloatEntry(2f, 750f),
            FloatEntry(3f, 690f),
            FloatEntry(4f, 1150f), // Fri peak
            FloatEntry(5f, 1380f), // Sat peak
            FloatEntry(6f, 960f)
        )
    }
    val viewsData = remember {
        listOf(
            FloatEntry(0f, 1200f),
            FloatEntry(1f, 1650f),
            FloatEntry(2f, 2100f),
            FloatEntry(3f, 1950f),
            FloatEntry(4f, 3400f),
            FloatEntry(5f, 4100f),
            FloatEntry(6f, 2800f)
        )
    }
    val connectionsData = remember {
        listOf(
            FloatEntry(0f, 14f),
            FloatEntry(1f, 22f),
            FloatEntry(2f, 35f),
            FloatEntry(3f, 28f),
            FloatEntry(4f, 64f),
            FloatEntry(5f, 78f),
            FloatEntry(6f, 45f)
        )
    }

    val chartModelProducer = remember { ChartEntryModelProducer(interactionsData) }

    LaunchedEffect(selectedMetric) {
        val entries = when (selectedMetric) {
            EngagementMetric.INTERACTIONS -> interactionsData
            EngagementMetric.VIEWS -> viewsData
            EngagementMetric.CONNECTIONS -> connectionsData
        }
        chartModelProducer.setEntries(entries)
    }

    val totalWeeklyStat = when (selectedMetric) {
        EngagementMetric.INTERACTIONS -> "5,930"
        EngagementMetric.VIEWS -> "17,200"
        EngagementMetric.CONNECTIONS -> "+286"
    }

    val growthPercentage = when (selectedMetric) {
        EngagementMetric.INTERACTIONS -> "+24.8%"
        EngagementMetric.VIEWS -> "+31.2%"
        EngagementMetric.CONNECTIONS -> "+18.5%"
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("profile_weekly_engagement_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.clickable {
                        isExpanded = !isExpanded
                        HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SELECTION)
                    }
                ) {
                    Surface(
                        color = selectedMetric.color.copy(alpha = 0.18f),
                        shape = CircleShape,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.TrendingUp,
                                contentDescription = null,
                                tint = selectedMetric.color,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Weekly Engagement Stats",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "Vico 7-day activity & reach curve",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(
                    onClick = onOpenFullAnalytics,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text(
                        text = "Full Insights ›",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    // Metric Selection Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        EngagementMetric.values().forEach { metric ->
                            val isSelected = selectedMetric == metric
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    HapticHelper.triggerHaptic(context, haptic, HapticHelper.HapticType.SELECTION)
                                    selectedMetric = metric
                                },
                                label = {
                                    Text(
                                        text = metric.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                leadingIcon = if (isSelected) {
                                    {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(metric.color, CircleShape)
                                        )
                                    }
                                } else null,
                                modifier = Modifier.height(30.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Key Metric Highlights
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = totalWeeklyStat,
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Total ${selectedMetric.title.lowercase()} this week",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            color = Color(0xFF2E7D32).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(0.8.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowUpward,
                                    contentDescription = null,
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(13.dp)
                                )
                                Text(
                                    text = "$growthPercentage vs last week",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vico Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("vico_weekly_engagement_graph")
                    ) {
                        Chart(
                            chart = lineChart(),
                            chartModelProducer = chartModelProducer,
                            startAxis = rememberStartAxis(valueFormatter = startAxisFormatter),
                            bottomAxis = rememberBottomAxis(valueFormatter = bottomAxisFormatter)
                        )
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
