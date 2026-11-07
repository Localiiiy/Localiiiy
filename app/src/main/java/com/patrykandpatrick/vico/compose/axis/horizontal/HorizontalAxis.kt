package com.patrykandpatrick.vico.compose.axis.horizontal

import androidx.compose.runtime.Composable
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter

@Composable
fun rememberBottomAxis(
    valueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>? = null
): Any? = valueFormatter

@Composable
fun bottomAxis(
    valueFormatter: AxisValueFormatter<AxisPosition.Horizontal.Bottom>? = null
): Any? = valueFormatter
