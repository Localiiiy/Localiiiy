import re

with open('app/src/main/java/com/example/ui/components/LiveRadarComponent.kt', 'r') as f:
    content = f.read()

# I need to add N, E, W, S labels to the radar canvas.
# Look for where the Canvas is drawn.
# Around `Canvas(modifier = Modifier.fillMaxSize()) { ... drawCircle ... }`
# And add zooming. Wait, for zooming: `var scale by remember { mutableFloatStateOf(1f) }` and `Modifier.pointerInput`?
# "In radar, N,E,W,S, 0, 90, 180, 270 degre mention and user can increase and decrees radar scale zoom by touch as per KM given in Radar section."

new_canvas_draw = """
                    // Radar Grid and Degrees
                    val textPaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.argb(180, 0, 255, 0)
                        textSize = 28f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    
                    val textOffset = 20.dp.toPx()
                    drawContext.canvas.nativeCanvas.drawText("N 0°", center.x, center.y - radarRadiusPx + textOffset, textPaint)
                    drawContext.canvas.nativeCanvas.drawText("S 180°", center.x, center.y + radarRadiusPx - textOffset / 2, textPaint)
                    
                    textPaint.textAlign = android.graphics.Paint.Align.LEFT
                    drawContext.canvas.nativeCanvas.drawText("E 90°", center.x + radarRadiusPx - textOffset * 2, center.y, textPaint)
                    
                    textPaint.textAlign = android.graphics.Paint.Align.RIGHT
                    drawContext.canvas.nativeCanvas.drawText("W 270°", center.x - radarRadiusPx + textOffset * 2, center.y, textPaint)

                    // Draw Sweep
"""

content = re.sub(
    r'// Draw Sweep',
    new_canvas_draw,
    content
)

# And for zooming, I'll add `var zoomScale by remember { mutableFloatStateOf(1f) }` and `Modifier.graphicsLayer { scaleX = zoomScale; scaleY = zoomScale }.pointerInput(Unit) { detectTransformGestures { _, _, zoom, _ -> zoomScale = (zoomScale * zoom).coerceIn(0.5f, 3f) } }`
# Wait, I'll need to import detectTransformGestures and graphics.nativeCanvas.

# Let's add imports
content = content.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport androidx.compose.ui.graphics.nativeCanvas\nimport androidx.compose.foundation.gestures.detectTransformGestures\nimport androidx.compose.ui.input.pointer.pointerInput\nimport androidx.compose.ui.graphics.graphicsLayer")

# Add the modifier to the Box containing the Canvas
content = re.sub(
    r'modifier = Modifier\s*\.fillMaxWidth\(\)\s*\.aspectRatio\(1f\)',
    r'''
                var zoomScale by remember { mutableFloatStateOf(1f) }
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = zoomScale
                        scaleY = zoomScale
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            zoomScale = (zoomScale * zoom).coerceIn(0.5f, 4f)
                        }
                    }
''',
    content
)

with open('app/src/main/java/com/example/ui/components/LiveRadarComponent.kt', 'w') as f:
    f.write(content)
