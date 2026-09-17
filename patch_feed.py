import os

fpath = "app/src/main/java/com/example/ui/components/feed/PulseFeedDualReachComponents.kt"
with open(fpath, "r") as f:
    content = f.read()

# Fix 1: AudioVoicePulseCard Padding
content = content.replace(
"""    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag("audio_voice_pulse_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {""",
"""    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 2.dp)
            .testTag("audio_voice_pulse_card_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {"""
)

# Fix 2: AudioWaveform visualizer bounds
content = content.replace(
"""                        val heightMultiplier = (sin(i * 0.7f) * 0.4f + 0.6f).coerceIn(0.2f, 1f)
                        val barHeight = size.height * heightMultiplier
                        drawLine(
                            color = if (isPlayed) LocaliiiyPrimaryTeal else Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(x = i * spacing + spacing / 2, y = (size.height - barHeight) / 2),
                            end = Offset(x = i * spacing + spacing / 2, y = (size.height + barHeight) / 2),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )""",
"""                        val heightMultiplier = (sin(i * 0.7f) * 0.35f + 0.5f).coerceIn(0.2f, 0.85f)
                        val barHeight = size.height * heightMultiplier
                        val strokePx = 3.dp.toPx()
                        drawLine(
                            color = if (isPlayed) LocaliiiyPrimaryTeal else Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(x = i * spacing + spacing / 2, y = (size.height - barHeight) / 2 + strokePx / 2),
                            end = Offset(x = i * spacing + spacing / 2, y = (size.height + barHeight) / 2 - strokePx / 2),
                            strokeWidth = strokePx,
                            cap = StrokeCap.Round
                        )"""
)

with open(fpath, "w") as f:
    f.write(content)

fpath2 = "app/src/main/java/com/example/ui/components/AdBannerComponent.kt"
with open(fpath2, "r") as f:
    content2 = f.read()

content2 = content2.replace(
"""            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),""",
"""            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),"""
)

with open(fpath2, "w") as f:
    f.write(content2)

fpath3 = "app/src/main/java/com/example/ui/components/PostCard.kt"
with open(fpath3, "r") as f:
    content3 = f.read()
content3 = content3.replace(
"""        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 6.dp)""",
"""        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 2.dp)"""
)
with open(fpath3, "w") as f:
    f.write(content3)

