import os

fpath = "app/src/main/java/com/example/ui/components/radar/RadarSpatialDiscoveryComponents.kt"
with open(fpath, "r") as f:
    content = f.read()

content = content.replace(
    "val nextKm = kotlin.math.pow(10.0, logVal.toDouble()).coerceIn(1.0, maxKm)",
    "val nextKm = Math.pow(10.0, logVal.toDouble()).coerceIn(1.0, maxKm)"
)

content = content.replace(
    "valueRange = 0f..maxLog,",
    "valueRange = 0f..maxLog.toFloat(),"
)

with open(fpath, "w") as f:
    f.write(content)

