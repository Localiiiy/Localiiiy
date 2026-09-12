import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

old_caption = '''            // Clip Caption with Location & Landmark Tag
            Text(
                text = clip.caption,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Color.White,
                maxLines = if (isCaptionExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { isCaptionExpanded = !isCaptionExpanded }
                    .padding(bottom = 8.dp)
            )'''

new_caption = '''            // Clip Caption with Location & Landmark Tag
            Text(
                text = clip.caption + " ...more",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { showCaptionsSheet = true }
                    .padding(bottom = 8.dp)
            )'''

content = content.replace(old_caption, new_caption)

# Insert BottomSheet logic at the end of ClipItem
bottom_sheet = '''
    if (showCaptionsSheet) {
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = { showCaptionsSheet = false },
            scrimColor = Color.Transparent,
            containerColor = Color.Black.copy(alpha = 0.65f),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Caption", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = clip.caption,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp)
                )
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
'''
content = content.replace('    // UGC Compliance: Report Clip Dialog', bottom_sheet + '\n    // UGC Compliance: Report Clip Dialog')

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
