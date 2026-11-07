package com.patrykandpatrick.vico.core.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

data class FloatEntry(val x: Float, val y: Float)

data class ChartEntryModel(val entries: List<FloatEntry>)

fun entryModelOf(vararg values: Number): ChartEntryModel =
    ChartEntryModel(values.mapIndexed { idx, v -> FloatEntry(idx.toFloat(), v.toFloat()) })

fun entryModelOf(vararg pairs: Pair<Number, Number>): ChartEntryModel =
    ChartEntryModel(pairs.map { (x, y) -> FloatEntry(x.toFloat(), y.toFloat()) })

class ChartEntryModelProducer(initialEntries: List<FloatEntry> = emptyList()) {
    var currentEntries by mutableStateOf(initialEntries)
        private set

    fun setEntries(newEntries: List<FloatEntry>) {
        currentEntries = newEntries
    }
}
