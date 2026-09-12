import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

new_progress = '''        // Live Video Progress Bar with Chapters
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .align(Alignment.BottomCenter)
                .graphicsLayer { alpha = if (isCinemaMode) 0f else 1f }
        ) {
            val isHyperlocalAudio = (clip.distanceKm ?: 99.0) <= 3.0
            val barColor = if (isHyperlocalAudio) Color(0xFFFF9800) else Color.White
            
            LinearProgressIndicator(
                progress = { videoProgress.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(2.dp)
                    .align(Alignment.BottomCenter),
                color = barColor,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            
            // Chapter markers (Mock positions)
            val chapterPositions = listOf(0.2f, 0.5f, 0.8f)
            chapterPositions.forEach { pos ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth(pos)
                        .align(Alignment.BottomStart)
                ) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .align(Alignment.CenterEnd)
                    )
                }
            }
        }'''

old_progress = '''        // Live Video Progress Bar
        LinearProgressIndicator(
            progress = { videoProgress.value },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .align(Alignment.BottomCenter),
            color = Color.White,
            trackColor = Color.White.copy(alpha = 0.2f)
        )'''

content = content.replace(old_progress, new_progress)

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
