package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf

import com.example.data.UserProfileEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataAnalysisSheet(
    userProfile: UserProfileEntity,
    onDismiss: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All Time") }
    val filters = listOf("Today", "Last 7 Days", "Last 30 Days", "Calendar Year", "All Time")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Data Analysis & Insights",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Filters
            ScrollableTabRow(
                selectedTabIndex = filters.indexOf(selectedFilter),
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                divider = {}
            ) {
                filters.forEachIndexed { index, filter ->
                    Tab(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        text = {
                            Text(
                                text = filter,
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AnalyticsSection(
                        title = "Account Reach Data",
                        icon = Icons.Default.Public,
                        color = Color(0xFF00BFA5)
                    ) {
                        val reachData = when (selectedFilter) {
                            "Today" -> entryModelOf(10f, 20f, 15f, 40f, 35f, 50f)
                            "Last 7 Days" -> entryModelOf(50f, 80f, 120f, 90f, 150f, 200f, 180f)
                            "Last 30 Days" -> entryModelOf(400f, 450f, 500f, 600f, 550f, 700f)
                            "Calendar Year" -> entryModelOf(10f, 15f, 12f, 20f, 25f, 30f, 28f, 35f, 40f, 42f, 48f, 50f)
                            else -> entryModelOf(50f, 60f, 70f, 85f, 100f, 120f, 140f)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Chart(
                                chart = lineChart(),
                                model = reachData,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Total Profile Visits", value = "124,532", modifier = Modifier.weight(1f))
                            StatTile(title = "Global Reach", value = "14.2M", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "New Connections", value = "+4,120", modifier = Modifier.weight(1f))
                            StatTile(title = "Engagement Rate", value = "18.4%", modifier = Modifier.weight(1f))
                        }
                    }
                }
                
                item {
                    AnalyticsSection(
                        title = "Marketplace Connections",
                        icon = Icons.Default.Storefront,
                        color = Color(0xFFFF7043)
                    ) {
                        val marketData = when (selectedFilter) {
                            "Today" -> entryModelOf(2f, 4f, 3f, 6f, 5f)
                            "Last 7 Days" -> entryModelOf(10f, 15f, 12f, 20f, 25f, 30f, 28f)
                            "Last 30 Days" -> entryModelOf(50f, 60f, 55f, 80f, 90f, 100f)
                            "Calendar Year" -> entryModelOf(200f, 250f, 300f, 400f, 500f, 600f, 700f, 800f, 900f, 100f, 110f, 120f)
                            else -> entryModelOf(100f, 150f, 200f, 250f, 300f, 350f, 400f)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Chart(
                                chart = columnChart(),
                                model = marketData,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Items Viewed", value = "8,430", modifier = Modifier.weight(1f))
                            StatTile(title = "Approaches/Chats", value = "342", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Items Saved", value = "1,204", modifier = Modifier.weight(1f))
                            StatTile(title = "Successful Meets", value = "48", modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    AnalyticsSection(
                        title = "Studio Video Performance",
                        icon = Icons.Default.VideoLibrary,
                        color = Color(0xFF7E57C2)
                    ) {
                        val studioData = when (selectedFilter) {
                            "Today" -> entryModelOf(5f, 10f, 8f, 15f)
                            "Last 7 Days" -> entryModelOf(20f, 40f, 35f, 60f, 80f, 90f, 100f)
                            "Last 30 Days" -> entryModelOf(100f, 150f, 200f, 250f, 220f, 300f)
                            "Calendar Year" -> entryModelOf(50f, 70f, 90f, 120f, 150f, 180f, 200f, 250f, 280f, 300f, 320f, 350f)
                            else -> entryModelOf(200f, 300f, 400f, 500f, 600f, 750f, 800f)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
                            Chart(
                                chart = lineChart(),
                                model = studioData,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis()
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Total Views", value = "2.4M", modifier = Modifier.weight(1f))
                            StatTile(title = "Total Likes", value = "112K", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Hours Watched", value = "45,200", modifier = Modifier.weight(1f))
                            StatTile(title = "Avg View Duration", value = "4m 12s", modifier = Modifier.weight(1f))
                        }
                    }
                }

                item {
                    AnalyticsSection(
                        title = "Neighborhood & Proximity",
                        icon = Icons.Default.Radar,
                        color = Color(0xFF29B6F6)
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Connected Neighbors", value = "156", modifier = Modifier.weight(1f))
                            StatTile(title = "Radar Pings", value = "4,022", modifier = Modifier.weight(1f))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            StatTile(title = "Avg Distance (KM)", value = "1.4 km", modifier = Modifier.weight(1f))
                            StatTile(title = "Local Interactions", value = "890", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSection(
    title: String,
    icon: ImageVector,
    color: Color,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun StatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
