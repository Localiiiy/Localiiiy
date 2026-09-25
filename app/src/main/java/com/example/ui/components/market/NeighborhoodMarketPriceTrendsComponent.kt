package com.example.ui.components.market

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf

data class NeighborhoodPriceTrendItem(
    val id: String,
    val name: String,
    val emoji: String,
    val category: String,
    val unit: String,
    val basePriceUSD: Double,
    // 30 days of daily price fluctuations (Day 1 to Day 30)
    val dailyPricesUSD: List<Double>,
    val insight: String
)

val sampleNeighborhoodTrendItems = listOf(
    NeighborhoodPriceTrendItem(
        id = "eggs_milk",
        name = "Farm Eggs & Milk",
        emoji = "🥚",
        category = "Farm & Fresh",
        unit = "1 Dozen + 1L",
        basePriceUSD = 4.20,
        dailyPricesUSD = listOf(
            4.60, 4.55, 4.50, 4.45, 4.50, 4.40, 4.35, 4.30, 4.25, 4.20,
            4.15, 4.10, 4.05, 4.15, 4.20, 4.25, 4.10, 4.00, 3.95, 3.90,
            3.95, 4.00, 4.05, 4.10, 4.15, 4.20, 4.25, 4.30, 4.25, 4.20
        ),
        insight = "Prices eased -8.7% after regional dairy logistics returned to normal. Great time to buy local!"
    ),
    NeighborhoodPriceTrendItem(
        id = "bike_tuneup",
        name = "Bicycle Tune-Up",
        emoji = "🚲",
        category = "Bikes & Scooters",
        unit = "Standard Service",
        basePriceUSD = 42.00,
        dailyPricesUSD = listOf(
            38.00, 38.00, 39.00, 39.50, 40.00, 40.00, 41.00, 41.50, 42.00, 42.00,
            43.00, 44.00, 44.50, 45.00, 45.00, 44.00, 43.50, 43.00, 42.50, 42.00,
            42.00, 43.00, 43.50, 44.00, 45.00, 44.50, 43.00, 42.50, 42.00, 42.00
        ),
        insight = "Workshop capacity peaked mid-month due to sunny weather. Rates stabilized at $42.00."
    ),
    NeighborhoodPriceTrendItem(
        id = "sourdough",
        name = "Artisan Sourdough",
        emoji = "🥖",
        category = "Farm & Fresh",
        unit = "Fresh Baked Loaf",
        basePriceUSD = 3.80,
        dailyPricesUSD = listOf(
            3.50, 3.50, 3.60, 3.60, 3.70, 3.70, 3.75, 3.80, 3.80, 3.90,
            3.90, 4.00, 4.00, 3.95, 3.90, 3.85, 3.80, 3.80, 3.75, 3.70,
            3.70, 3.75, 3.80, 3.80, 3.85, 3.85, 3.90, 3.85, 3.80, 3.80
        ),
        insight = "Local bakery grain flour stabilized. Steady neighborhood demand with weekend promos."
    ),
    NeighborhoodPriceTrendItem(
        id = "phones",
        name = "Refurbished Phones",
        emoji = "📱",
        category = "Mobiles & Tech",
        unit = "Grade A Handset",
        basePriceUSD = 135.00,
        dailyPricesUSD = listOf(
            150.00, 148.00, 147.00, 145.00, 144.00, 142.00, 140.00, 139.00, 138.00, 137.00,
            136.00, 135.00, 135.00, 134.00, 133.00, 132.00, 131.00, 130.00, 132.00, 133.00,
            134.00, 134.00, 135.00, 136.00, 135.00, 134.00, 135.00, 136.00, 135.00, 135.00
        ),
        insight = "Gradual -10.0% decline over 30 days as new generation devices arrived in neighborhood stores."
    ),
    NeighborhoodPriceTrendItem(
        id = "honey",
        name = "Raw Local Honey",
        emoji = "🍯",
        category = "Farm & Fresh",
        unit = "500g Jar",
        basePriceUSD = 9.50,
        dailyPricesUSD = listOf(
            9.00, 9.00, 9.10, 9.10, 9.20, 9.20, 9.25, 9.30, 9.40, 9.50,
            9.50, 9.60, 9.70, 9.75, 9.80, 9.80, 9.70, 9.65, 9.60, 9.55,
            9.50, 9.50, 9.55, 9.60, 9.60, 9.55, 9.50, 9.50, 9.50, 9.50
        ),
        insight = "High apiary yield kept prices steady with a gentle +5.5% harvest premium."
    ),
    NeighborhoodPriceTrendItem(
        id = "coffee_table",
        name = "Solid Wood Table",
        emoji = "🪵",
        category = "Furniture & Living",
        unit = "Reclaimed Wood",
        basePriceUSD = 28.00,
        dailyPricesUSD = listOf(
            32.00, 31.50, 31.00, 30.50, 30.00, 29.50, 29.00, 29.00, 28.50, 28.00,
            27.50, 27.00, 26.50, 26.00, 26.50, 27.00, 27.50, 28.00, 28.50, 29.00,
            28.50, 28.00, 27.50, 27.00, 27.50, 28.00, 28.50, 28.00, 28.00, 28.00
        ),
        insight = "Artisan timber repurposing surplus lowered costs by -12.5% for local buyers."
    )
)

enum class TrendTimeSpan(val days: Int, val label: String) {
    SEVEN_DAYS(7, "7 Days"),
    FOURTEEN_DAYS(14, "14 Days"),
    THIRTY_DAYS(30, "30 Days (Full)")
}

@Composable
fun NeighborhoodMarketPriceTrendsComponent(
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(true) }
    var selectedItemId by remember { mutableStateOf("eggs_milk") }
    var selectedSpan by remember { mutableStateOf(TrendTimeSpan.THIRTY_DAYS) }

    val currentItem = remember(selectedItemId) {
        sampleNeighborhoodTrendItems.firstOrNull { it.id == selectedItemId } ?: sampleNeighborhoodTrendItems.first()
    }

    // Slice prices based on selected span
    val activePricesUSD = remember(currentItem, selectedSpan) {
        val total = currentItem.dailyPricesUSD.size
        val count = selectedSpan.days.coerceAtMost(total)
        currentItem.dailyPricesUSD.takeLast(count)
    }

    // Calculate metrics
    val startPrice = activePricesUSD.firstOrNull() ?: currentItem.basePriceUSD
    val latestPrice = activePricesUSD.lastOrNull() ?: currentItem.basePriceUSD
    val minPrice = activePricesUSD.minOrNull() ?: currentItem.basePriceUSD
    val maxPrice = activePricesUSD.maxOrNull() ?: currentItem.basePriceUSD
    val avgPrice = if (activePricesUSD.isNotEmpty()) activePricesUSD.average() else currentItem.basePriceUSD

    val percentageChange = if (startPrice > 0) ((latestPrice - startPrice) / startPrice) * 100.0 else 0.0
    val isPriceUp = percentageChange >= 0

    // Build Vico Chart Entry Model
    val chartModel = remember(activePricesUSD, currentCurrency) {
        val convertedPoints = activePricesUSD.mapIndexed { index, priceUSD ->
            val displayValue = CurrencyHelper.convertFromUSD(priceUSD, currentCurrency).toFloat()
            (index.toFloat() to displayValue)
        }
        val pairs: Array<Pair<Float, Float>> = convertedPoints.toTypedArray()
        entryModelOf(*pairs)
    }

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)),
        shadowElevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("neighborhood_market_price_trends_card")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title, 30-Day Badge & Expand/Collapse
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = "Price Trends",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Market Price Trends",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "30-DAY INDEX",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Fluctuations for common neighborhood essentials",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Toggle Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Item selector chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleNeighborhoodTrendItems.forEach { item ->
                            val isSelected = item.id == selectedItemId
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(100.dp))
                                    .clickable { selectedItemId = item.id }
                                    .testTag("trend_item_chip_${item.id}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                                ) {
                                    Text(text = item.emoji, fontSize = 13.sp)
                                    Text(
                                        text = item.name,
                                        fontSize = 11.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selected Item Stat Bar & Percentage Change
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = currentItem.emoji,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = currentItem.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "(${currentItem.unit})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "Current avg: ${CurrencyHelper.format(latestPrice, currentCurrency)}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Trend Badge (+/- %)
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isPriceUp) Color(0xFFEF4444).copy(alpha = 0.12f) else Color(0xFF10B981).copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, if (isPriceUp) Color(0xFFEF4444).copy(alpha = 0.4f) else Color(0xFF10B981).copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPriceUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    tint = if (isPriceUp) Color(0xFFDC2626) else Color(0xFF059669),
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "${if (isPriceUp) "+" else ""}${String.format("%.1f", percentageChange)}% (${selectedSpan.label})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPriceUp) Color(0xFFDC2626) else Color(0xFF059669)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Time Range Filter Pills (7 Days | 14 Days | 30 Days)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TrendTimeSpan.values().forEach { span ->
                            val isSpanSelected = selectedSpan == span
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSpanSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSpanSelected) MaterialTheme.colorScheme.secondary else Color.Transparent
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedSpan = span }
                                    .testTag("trend_span_${span.days}")
                            ) {
                                Text(
                                    text = span.label,
                                    textAlign = TextAlign.Center,
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSpanSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSpanSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Vico Line Chart Container
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .padding(vertical = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Chart(
                                chart = lineChart(),
                                model = chartModel,
                                startAxis = rememberStartAxis(),
                                bottomAxis = rememberBottomAxis(),
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3-Metric Summary Cards: Low, High, 30-Day Avg
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricSmallCard(
                            label = "Lowest",
                            value = CurrencyHelper.format(minPrice, currentCurrency),
                            icon = Icons.Default.ArrowDownward,
                            color = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )
                        MetricSmallCard(
                            label = "Highest",
                            value = CurrencyHelper.format(maxPrice, currentCurrency),
                            icon = Icons.Default.ArrowUpward,
                            color = Color(0xFFEF4444),
                            modifier = Modifier.weight(1f)
                        )
                        MetricSmallCard(
                            label = "Average",
                            value = CurrencyHelper.format(avgPrice, currentCurrency),
                            icon = Icons.Default.Balance,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hyperlocal Buyer / Seller Insight
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Insight",
                                tint = Color(0xFFF59E0B),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = currentItem.insight,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricSmallCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
