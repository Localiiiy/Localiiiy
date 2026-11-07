package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.cos
import kotlin.math.sin

/**
 * PROMPT 5: MULTI-RADIUS GEOGRAPHIC DIAL
 * Persistent arc-shaped Proximity Dial allowing users to manually drag or tap
 * across distinct bands: "Walking Distance (<1km)", "Neighborhood (3km)", "50 KM",
 * "State", "Country", and "Global".
 */
data class DiscoveryBand(
    val id: String,
    val label: String,
    val shortLabel: String,
    val km: Double,
    val icon: String,
    val description: String
)

val DISCOVERY_BANDS = listOf(
    DiscoveryBand("WALKING", "Walking Distance (<1km)", "<1 KM", 1.0, "🚶", "Pedestrian range within 1 km"),
    DiscoveryBand("NEIGHBORHOOD", "Neighborhood (3km)", "3 KM", 3.0, "📍", "Immediate neighborhood within 3 km"),
    DiscoveryBand("50KM", "50 KM", "50 KM", 50.0, "🚗", "Metro & commuting area within 50 km"),
    DiscoveryBand("STATE", "State", "State", 250.0, "🌲", "Statewide & provincial community reach"),
    DiscoveryBand("COUNTRY", "Country", "Country", 5000.0, "🗺️", "Nationwide discovery radius"),
    DiscoveryBand("GLOBAL", "Global", "Global", 20000.0, "🌍", "Worldwide planetary reach")
)

@Composable
fun ProximityDialArc(
    selectedRadiusKm: Double?,
    onRadiusSelected: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeBandIndex = remember(selectedRadiusKm) {
        if (selectedRadiusKm == null) 1
        else {
            val idx = DISCOVERY_BANDS.indices.minByOrNull {
                kotlin.math.abs(DISCOVERY_BANDS[it].km - selectedRadiusKm)
            } ?: 1
            idx
        }
    }

    val activeBand = DISCOVERY_BANDS[activeBandIndex]
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 4.dp,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("proximity_dial_arc_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Header: Proximity Dial Title & Active Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(activeBand.icon, fontSize = 14.sp)
                    }
                    Column {
                        Text(
                            text = "Geocentric Proximity Dial",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Text(
                            text = activeBand.label,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = if (activeBand.km >= 20000.0) "EARTH" else "${activeBand.km.toInt()} KM",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Arc Sweep Visual Track
            val animatedFraction by animateFloatAsState(
                targetValue = activeBandIndex.toFloat() / (DISCOVERY_BANDS.size - 1).toFloat(),
                animationSpec = spring(),
                label = "arc_dial_fraction"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                            val targetIndex = (fraction * (DISCOVERY_BANDS.size - 1))
                                .toInt()
                                .coerceIn(0, DISCOVERY_BANDS.size - 1)
                            onRadiusSelected(DISCOVERY_BANDS[targetIndex].km)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp)
                ) {
                    val width = size.width
                    val midY = size.height / 2f

                    // Base Arc Line
                    drawLine(
                        color = surfaceVariant,
                        start = Offset(16.dp.toPx(), midY),
                        end = Offset(width - 16.dp.toPx(), midY),
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Active Glowing Progress Segment
                    val activeEnd = 16.dp.toPx() + (width - 32.dp.toPx()) * animatedFraction
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(primaryColor.copy(alpha = 0.6f), primaryColor)
                        ),
                        start = Offset(16.dp.toPx(), midY),
                        end = Offset(activeEnd, midY),
                        strokeWidth = 6.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Node Ticks for the 6 Bands
                    DISCOVERY_BANDS.indices.forEach { index ->
                        val nodeFraction = index.toFloat() / (DISCOVERY_BANDS.size - 1).toFloat()
                        val nodeX = 16.dp.toPx() + (width - 32.dp.toPx()) * nodeFraction
                        val isPassed = index <= activeBandIndex
                        drawCircle(
                            color = if (isPassed) primaryColor else surfaceVariant,
                            radius = if (index == activeBandIndex) 6.dp.toPx() else 3.5.dp.toPx(),
                            center = Offset(nodeX, midY)
                        )
                        if (index == activeBandIndex) {
                            drawCircle(
                                color = Color.White,
                                radius = 2.5.dp.toPx(),
                                center = Offset(nodeX, midY)
                            )
                        }
                    }
                }
            }

            // Quick Tap Band Pills
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DISCOVERY_BANDS.forEachIndexed { index, band ->
                    val isSelected = index == activeBandIndex
                    val pillBg by animateColorAsState(
                        targetValue = if (isSelected) primaryColor else Color.Transparent,
                        label = "pill_bg_$index"
                    )
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        label = "pill_text_$index"
                    )

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = pillBg,
                        border = if (!isSelected) androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ) else null,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onRadiusSelected(band.km) }
                            .testTag("dial_band_${band.id}")
                    ) {
                        Text(
                            text = band.shortLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = textColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}
