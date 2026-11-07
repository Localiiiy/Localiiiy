package com.patrykandpatrick.vico.compose.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry

@Composable
fun Chart(
    chart: String,
    model: ChartEntryModel,
    modifier: Modifier = Modifier,
    startAxis: Any? = null,
    bottomAxis: Any? = null
) {
    RenderCanvasChart(
        chartType = chart,
        entries = model.entries,
        modifier = modifier
    )
}

@Composable
fun Chart(
    chart: String,
    chartModelProducer: ChartEntryModelProducer,
    modifier: Modifier = Modifier,
    startAxis: Any? = null,
    bottomAxis: Any? = null
) {
    RenderCanvasChart(
        chartType = chart,
        entries = chartModelProducer.currentEntries,
        modifier = modifier
    )
}

@Composable
private fun RenderCanvasChart(
    chartType: String,
    entries: List<FloatEntry>,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(0xFF00BFA5)
    Canvas(modifier = modifier.fillMaxSize().padding(8.dp)) {
        if (entries.isEmpty()) return@Canvas
        val values = entries.map { it.y }
        val maxVal = (values.maxOrNull() ?: 1f).coerceAtLeast(1f)
        val minVal = (values.minOrNull() ?: 0f).coerceAtMost(maxVal - 1f)
        val range = (maxVal - minVal).coerceAtLeast(1f)

        if (chartType == "COLUMN") {
            val slotWidth = size.width / values.size
            val barWidth = (slotWidth * 0.6f).coerceAtLeast(4.dp.toPx())
            values.forEachIndexed { index, value ->
                val barHeight = (value / maxVal) * size.height
                val x = index * slotWidth + (slotWidth - barWidth) / 2f
                val y = size.height - barHeight
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(x, y),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        } else {
            val stepX = if (values.size > 1) size.width / (values.size - 1) else size.width
            val path = Path()
            values.forEachIndexed { index, value ->
                val x = index * stepX
                val normalizedY = (value - minVal) / range
                val y = size.height - (normalizedY * size.height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            }
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
            values.forEachIndexed { index, value ->
                val x = index * stepX
                val normalizedY = (value - minVal) / range
                val y = size.height - (normalizedY * size.height)
                drawCircle(color = primaryColor, radius = 4.dp.toPx(), center = Offset(x, y))
            }
        }
    }
}
