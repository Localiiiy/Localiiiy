package com.patrykandpatrick.vico.core.axis.formatter

import com.patrykandpatrick.vico.core.axis.AxisPosition

fun interface AxisValueFormatter<P : AxisPosition> {
    fun formatValue(value: Float, chartValues: Any?): CharSequence
}
