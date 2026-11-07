package com.patrykandpatrick.vico.compose.axis.vertical

import androidx.compose.runtime.Composable
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter

@Composable
fun rememberStartAxis(
    valueFormatter: AxisValueFormatter<AxisPosition.Vertical.Start>? = null
): Any? = valueFormatter

@Composable
fun startAxis(
    valueFormatter: AxisValueFormatter<AxisPosition.Vertical.Start>? = null
): Any? = valueFormatter
