package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType

object HapticHelper {
    enum class HapticType {
        SELECTION,
        SUCCESS,
        WARNING,
        IMPACT
    }

    fun triggerHaptic(
        context: Context? = null,
        hapticFeedback: HapticFeedback? = null,
        type: HapticType
    ) {
        val composeType = when (type) {
            HapticType.SELECTION -> HapticFeedbackType.TextHandleMove
            HapticType.SUCCESS -> HapticFeedbackType.LongPress
            HapticType.WARNING -> HapticFeedbackType.LongPress
            HapticType.IMPACT -> HapticFeedbackType.LongPress
        }
        triggerHaptic(context, hapticFeedback, composeType)
    }

    fun triggerHaptic(
        context: Context? = null,
        hapticFeedback: HapticFeedback? = null,
        type: HapticFeedbackType = HapticFeedbackType.LongPress
    ) {
        // 1. Trigger Compose local haptic feedback
        try {
            hapticFeedback?.performHapticFeedback(type)
        } catch (_: Exception) {}

        // 2. Hardware vibrator fallback for tactile response
        if (context != null) {
            try {
                val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                    vibratorManager?.defaultVibrator
                } else {
                    @Suppress("DEPRECATION")
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                }
                if (vibrator?.hasVibrator() == true) {
                    val duration = if (type == HapticFeedbackType.TextHandleMove) 20L else 45L
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                            VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE)
                        )
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(duration)
                    }
                }
            } catch (_: Exception) {}
        }
    }
}
