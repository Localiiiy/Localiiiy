import os

fpath = "app/src/main/java/com/example/ui/components/LiveRadarComponent.kt"
with open(fpath, "r") as f:
    content = f.read()

# Replace the ProximityDialArc and RadarScaleZoomSlider calls
old_controls = """        // Section 3.7: Multi-Radius Geographic Arc Dial & Radar Scale Zoom
        ProximityDialArc(
            selectedRadiusKm = selectedRadiusKm,
            onRadiusSelected = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        RadarScaleZoomSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = {
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(it)
            }
        )"""

new_controls = """        // Section 3.7: Unified Range Slider (Premium Gated)
        UnifiedRadarRangeSlider(
            currentRadiusKm = selectedRadiusKm,
            onRadiusChange = { radius ->
                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.TextHandleMove)
                onRadiusChange(radius)
            },
            isPremium = userProfile.isPremium
        )"""

content = content.replace(old_controls, new_controls)

# Add Tactical Messages inbox button
old_top_bar_actions = """        // Custom Localiiiy Transparent Top Bar Overlay
        LocaliiiyTopBar(
            title = "",
            isTransparent = true,"""

new_top_bar_actions = """        // Custom Localiiiy Transparent Top Bar Overlay
        LocaliiiyTopBar(
            title = "",
            isTransparent = true,
            actions = {
                IconButton(onClick = { /* Open Tactical Messages */ }) {
                    Icon(
                        imageVector = Icons.Outlined.Message,
                        contentDescription = "Tactical Messages",
                        tint = Color.White
                    )
                }
            },"""

content = content.replace(old_top_bar_actions, new_top_bar_actions)

with open(fpath, "w") as f:
    f.write(content)
