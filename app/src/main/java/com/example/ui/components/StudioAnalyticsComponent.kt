package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.component.shape.shader.fromBrush
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.component.shape.shader.DynamicShaders
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun StudioAnalyticsComponent(modifier: Modifier = Modifier) {
    val viewsModelProducer = remember { ChartEntryModelProducer(generateViewsData()) }
    val subsModelProducer = remember { ChartEntryModelProducer(generateSubsData()) }

    val daysFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        "Day ${value.toInt()}"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Studio Analytics",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Audience Retention / Video Views (Bar Chart)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Weekly Video Views",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Chart(
                    chart = columnChart(),
                    chartModelProducer = viewsModelProducer,
                    startAxis = startAxis(),
                    bottomAxis = bottomAxis(valueFormatter = daysFormatter),
                    modifier = Modifier.height(200.dp)
                )
            }
        }

        // Subscriber Growth (Line Chart)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Subscriber Growth Trend",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Chart(
                    chart = lineChart(),
                    chartModelProducer = subsModelProducer,
                    startAxis = startAxis(),
                    bottomAxis = bottomAxis(valueFormatter = daysFormatter),
                    modifier = Modifier.height(200.dp)
                )
            }
        }
    }
}

private fun generateViewsData(): List<FloatEntry> {
    return listOf(
        FloatEntry(1f, 150f),
        FloatEntry(2f, 320f),
        FloatEntry(3f, 410f),
        FloatEntry(4f, 290f),
        FloatEntry(5f, 650f),
        FloatEntry(6f, 890f),
        FloatEntry(7f, 1200f)
    )
}

private fun generateSubsData(): List<FloatEntry> {
    return listOf(
        FloatEntry(1f, 10f),
        FloatEntry(2f, 25f),
        FloatEntry(3f, 42f),
        FloatEntry(4f, 75f),
        FloatEntry(5f, 120f),
        FloatEntry(6f, 210f),
        FloatEntry(7f, 350f)
    )
}
