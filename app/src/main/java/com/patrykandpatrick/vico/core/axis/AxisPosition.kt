package com.patrykandpatrick.vico.core.axis

sealed class AxisPosition {
    sealed class Horizontal : AxisPosition() {
        object Bottom : Horizontal()
        object Top : Horizontal()
    }
    sealed class Vertical : AxisPosition() {
        object Start : Vertical()
        object End : Vertical()
    }
}
