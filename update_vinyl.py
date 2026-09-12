import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

old_vinyl = '''            // Rotating Audio Vinyl Disc
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    .rotate(if (isPlaying) rotationAngle else 0f),'''

new_vinyl = '''            // Rotating Audio Vinyl Disc
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
                    .border(2.dp, Color.White.copy(alpha = 0.8f), CircleShape)
                    .rotate(if (isPlaying) rotationAngle else 0f)
                    .clickable { showSoundtrackSheet = true },'''

content = content.replace(old_vinyl, new_vinyl)

soundtrack_sheet = '''
    if (showSoundtrackSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showSoundtrackSheet = false },
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Text(
                    text = "${clip.soundTitle} • ${clip.soundArtist}",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "342 clips recorded with this ambient stem nearby",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Mock grid of clips would go here
                Box(modifier = Modifier.fillMaxWidth().height(200.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Text("Related Clips Grid", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
'''
content = content.replace('    // UGC Compliance: Report Clip Dialog', soundtrack_sheet + '\n    // UGC Compliance: Report Clip Dialog')

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
