with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "r") as f:
    text = f.read()

old_logic = """            val boxWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { canvasWidth.toPx() }
            val boxHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { canvasHeight.toPx() }
            val radiusPx = minOf(boxWidthPx, boxHeightPx) / 2
            val centerX = boxWidthPx / 2
            val centerY = boxHeightPx / 2

            combinedItems.forEachIndexed { i, item ->
                val angle = (i * 137.5f) % 360f
                val dist = (radiusPx * 0.2f) + (radiusPx * 0.7f * ((i * 17) % 100) / 100f)
                val blipX = centerX + dist * cos(Math.toRadians(angle.toDouble())).toFloat()
                val blipY = centerY + dist * sin(Math.toRadians(angle.toDouble())).toFloat()"""

new_logic = """            val boxWidthPx = with(androidx.compose.ui.platform.LocalDensity.current) { canvasWidth.toPx() }
            val boxHeightPx = with(androidx.compose.ui.platform.LocalDensity.current) { canvasHeight.toPx() }
            val paddingPx = with(androidx.compose.ui.platform.LocalDensity.current) { 16.dp.toPx() }
            val radarRadiusPx = (minOf(boxWidthPx, boxHeightPx) - (paddingPx * 2)) / 2
            val centerX = boxWidthPx / 2
            val centerY = boxHeightPx / 2

            combinedItems.forEachIndexed { i, item ->
                val angle = (i * 137.5f) % 360f
                // Ensure dist is strictly inside the radar circle (max 0.8f of radarRadiusPx to keep avatars inside)
                val dist = (radarRadiusPx * 0.2f) + (radarRadiusPx * 0.6f * ((i * 17) % 100) / 100f)
                val blipX = centerX + dist * cos(Math.toRadians(angle.toDouble())).toFloat()
                val blipY = centerY + dist * sin(Math.toRadians(angle.toDouble())).toFloat()"""

if old_logic in text:
    text = text.replace(old_logic, new_logic)
    with open("app/src/main/java/com/example/ui/components/LiveRadarComponent.kt", "w") as f:
        f.write(text)
    print("Successfully updated radar bounds!")
else:
    print("Could not find old logic block.")
