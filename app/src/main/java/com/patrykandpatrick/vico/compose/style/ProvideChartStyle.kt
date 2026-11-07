package com.patrykandpatrick.vico.compose.style

import androidx.compose.runtime.Composable

@Composable
fun ProvideChartStyle(
    chartStyle: Any? = null,
    content: @Composable () -> Unit
) {
    content()
}
