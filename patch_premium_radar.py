import os

fpath = "app/src/main/java/com/example/ui/components/LiveRadarComponent.kt"
with open(fpath, "r") as f:
    content = f.read()

# 1. Premium Visual Styles
old_colors = """    val radarColor = Color(0xFF00FF41)
    val gridColor = Color(0xFF005511)"""

new_colors = """    val isPremium = userProfile.isPremiumSubscribed
    val radarColor = if (isPremium) Color(0xFFFFD700) else Color(0xFF00FF41) // Neon Gold for premium
    val gridColor = if (isPremium) Color(0xFF8B6508) else Color(0xFF005511)
    val sweepColors = if (isPremium) {
        listOf(radarColor.copy(alpha = 0f), Color(0xFFFF00FF).copy(alpha = 0.5f), radarColor) // Cyberpunk multi-color
    } else {
        listOf(radarColor.copy(alpha = 0f), radarColor.copy(alpha = 0.4f), radarColor)
    }"""
content = content.replace(old_colors, new_colors)

# Update Sweep Brush to use sweepColors
old_sweep_brush = """                    // Sweep Brush
                    val sweepBrush = Brush.sweepGradient(
                        colors = listOf(
                            radarColor.copy(alpha = 0f),
                            radarColor.copy(alpha = 0.4f),
                            radarColor
                        ),
                        center = center
                    )"""

new_sweep_brush = """                    // Sweep Brush
                    val sweepBrush = Brush.sweepGradient(
                        colors = sweepColors,
                        center = center
                    )"""
content = content.replace(old_sweep_brush, new_sweep_brush)

# Premium Radar Range limit fix for UnifiedRadarRangeSlider
old_slider = """        // Section 3.7: Unified Range Slider (Premium Gated)
        UnifiedRadarRangeSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            isPremium = userProfile.isPremium
        )"""

new_slider = """        // Section 3.7: Unified Range Slider (Premium Gated)
        UnifiedRadarRangeSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            isPremium = userProfile.isPremiumSubscribed
        )"""
content = content.replace(old_slider, new_slider)

with open(fpath, "w") as f:
    f.write(content)
