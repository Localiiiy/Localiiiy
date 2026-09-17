import os

fpath = "app/src/main/java/com/example/ui/components/radar/RadarSpatialDiscoveryComponents.kt"
with open(fpath, "r") as f:
    content = f.read()

new_slider = """@Composable
fun UnifiedRadarRangeSlider(
    currentRadiusKm: Double,
    onRadiusChange: (Double) -> Unit,
    isPremium: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    
    // Non-premium restricted to 50 KM
    val maxKm = if (isPremium) 20000.0 else 50.0
    val roundedKm = currentRadiusKm.coerceIn(1.0, maxKm)
    
    // Scale mapping for a smoother slider experience (logarithmic-like visually)
    // 0f -> 1.0 km, 1f -> maxKm
    val maxLog = kotlin.math.log10(maxKm)
    val currentLog = kotlin.math.log10(roundedKm).toFloat()
    
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = RadarPanelBg,
        border = BorderStroke(1.dp, RadarPhosphor.copy(alpha = 0.45f)),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
            .height(56.dp) // Compact Height
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ZoomIn,
                contentDescription = null,
                tint = RadarPhosphor,
                modifier = Modifier.size(18.dp)
            )
            
            androidx.compose.material3.Slider(
                value = currentLog,
                onValueChange = { logVal ->
                    val nextKm = kotlin.math.pow(10.0, logVal.toDouble()).coerceIn(1.0, maxKm)
                    onRadiusChange(nextKm)
                },
                valueRange = 0f..maxLog,
                modifier = Modifier.weight(1f),
                colors = androidx.compose.material3.SliderDefaults.colors(
                    thumbColor = RadarNeonGreen,
                    activeTrackColor = RadarNeonGreen,
                    inactiveTrackColor = RadarPhosphor.copy(alpha = 0.3f)
                )
            )
            
            Text(
                text = "${if (roundedKm >= 1000) String.format("%.0f", roundedKm/1000) + "K" else String.format("%.0f", roundedKm)} KM",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = RadarNeonGreen,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.widthIn(min = 45.dp),
                textAlign = TextAlign.End
            )
        }
    }
}
"""

start_idx = content.find("@Composable\nfun RadarScaleZoomSlider(")
if start_idx != -1:
    end_idx = content.find("@Composable\nfun RealTimeCompassHeadingBadge(", start_idx)
    if end_idx != -1:
        # replace the whole block + javadoc if any
        javadoc_idx = content.rfind("/**", 0, start_idx)
        if javadoc_idx != -1 and javadoc_idx > start_idx - 150:
            start_idx = javadoc_idx
        
        before = content[:start_idx]
        after = content[end_idx:]
        content = before + new_slider + after
        
        with open(fpath, "w") as f:
            f.write(content)
        print("Success")
    else:
        print("Could not find RealTimeCompassHeadingBadge")
else:
    print("Could not find RadarScaleZoomSlider")
