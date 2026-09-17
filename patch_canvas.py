import os

fpath = "app/src/main/java/com/example/ui/components/LiveRadarComponent.kt"
with open(fpath, "r") as f:
    content = f.read()

# 1. Rings based on selectedRadiusKm
old_rings = """                    // Concentric Acoustic Range Rings (0.5km, 2km, 10km, 50km, 500km bands)
                    val acousticRings = listOf(0.20f, 0.40f, 0.60f, 0.80f, 1.0f)
                    val acousticLabels = listOf("0.5km", "2km", "10km", "50km", "500km")
                    acousticRings.forEachIndexed { idx, frac ->"""

new_rings = """                    // Concentric Acoustic Range Rings synced to slider
                    val acousticRings = listOf(0.20f, 0.40f, 0.60f, 0.80f, 1.0f)
                    val currentMaxKm = selectedRadiusKm
                    val acousticLabels = acousticRings.map { frac -> 
                        val km = currentMaxKm * frac
                        if (km >= 1000) String.format("%.0fK", km/1000) else String.format("%.1fkm", km)
                    }
                    acousticRings.forEachIndexed { idx, frac ->"""
content = content.replace(old_rings, new_rings)

# 2. Sleek Canvas Geometry (Semi-transparent neon vectors instead of heavy solid lines)
old_grid = """                    for (deg in 0 until 360 step 45) {
                        val rad = Math.toRadians(deg.toDouble())
                        val spokeEndX = center.x + radius * cos(rad).toFloat()
                        val spokeEndY = center.y + radius * sin(rad).toFloat()
                        drawLine(
                            color = radarColor.copy(alpha = 0.18f),
                            start = center,
                            end = Offset(spokeEndX, spokeEndY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }"""

new_grid = """                    for (deg in 0 until 360 step 30) { // More spokes for tactical look
                        val rad = Math.toRadians(deg.toDouble())
                        val spokeEndX = center.x + radius * cos(rad).toFloat()
                        val spokeEndY = center.y + radius * sin(rad).toFloat()
                        drawLine(
                            color = radarColor.copy(alpha = 0.08f), // semi-transparent neon vectors
                            start = center,
                            end = Offset(spokeEndX, spokeEndY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }"""
content = content.replace(old_grid, new_grid)

old_crosshairs = """                    // Crosshairs
                    drawLine(radarColor.copy(alpha = 0.45f), start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())
                    drawLine(radarColor.copy(alpha = 0.45f), start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())"""

new_crosshairs = """                    // Crosshairs (lighter neon vectors)
                    drawLine(radarColor.copy(alpha = 0.15f), start = Offset(center.x, 0f), end = Offset(center.x, size.height), strokeWidth = 1.dp.toPx())
                    drawLine(radarColor.copy(alpha = 0.15f), start = Offset(0f, center.y), end = Offset(size.width, center.y), strokeWidth = 1.dp.toPx())"""
content = content.replace(old_crosshairs, new_crosshairs)

# 3. Compass degree markers position
old_labels = """                    // Glowing North Chevron Pointer
                    val northChevron = Path().apply {
                        moveTo(center.x, center.y - radius + 3.dp.toPx())
                        lineTo(center.x - 5.dp.toPx(), center.y - radius + 11.dp.toPx())
                        lineTo(center.x + 5.dp.toPx(), center.y - radius + 11.dp.toPx())
                        close()
                    }
                    drawPath(northChevron, color = Color(0xFF39FF14))

                    // Cardinal Coordinate Labels
                    drawContext.canvas.nativeCanvas.drawText("N 000°", center.x, center.y - radius + textMargin + 9.dp.toPx(), northPaint)
                    drawContext.canvas.nativeCanvas.drawText("S 180°", center.x, center.y + radius - textMargin + 2.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("W 270°", center.x - radius + textMargin * 1.6f, center.y + 4.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("E 090°", center.x + radius - textMargin * 1.6f, center.y + 4.dp.toPx(), textPaint)"""

new_labels = """                    // Glowing North Chevron Pointer (shifted out)
                    val northChevron = Path().apply {
                        moveTo(center.x, center.y - radius - 5.dp.toPx())
                        lineTo(center.x - 5.dp.toPx(), center.y - radius - 11.dp.toPx())
                        lineTo(center.x + 5.dp.toPx(), center.y - radius - 11.dp.toPx())
                        close()
                    }
                    drawPath(northChevron, color = Color(0xFF39FF14))

                    // Cardinal Coordinate Labels (shifted completely outside sweep circle)
                    drawContext.canvas.nativeCanvas.drawText("N 000°", center.x, center.y - radius - textMargin, northPaint)
                    drawContext.canvas.nativeCanvas.drawText("S 180°", center.x, center.y + radius + textMargin + 6.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("W 270°", center.x - radius - textMargin - 8.dp.toPx(), center.y + 4.dp.toPx(), textPaint)
                    drawContext.canvas.nativeCanvas.drawText("E 090°", center.x + radius + textMargin + 8.dp.toPx(), center.y + 4.dp.toPx(), textPaint)"""
content = content.replace(old_labels, new_labels)

# 4. Remove blips from Radar sweep when Ghost Mode or Offline (handled in Screen, but let's double check if we need Canvas fixes for Premium User Blips glowing aura)
# "Priority Radar Placement: Highlight premium user blips on the HUD with an animated glowing "Signal Aura" ring to increase local visibility."
# "Premium Radar Visual Styling: Grant premium users custom HUD themes (e.g., Neon Gold, Cyan Cyberpunk), metallic blip borders, and multi-color sweep trails."

with open(fpath, "w") as f:
    f.write(content)

