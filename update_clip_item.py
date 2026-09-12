import re

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

# Replace the "👌" with "❤️" in showBigHeart
content = content.replace('text = "👌",', 'text = "❤️",')
content = content.replace('symbolText = "👌",', 'symbolText = "❤️",')

# Add "React" button and fix save icon to use "❤️" instead of 👌 if needed
react_btn = '''
            // Quick Reply Ephemeral Video Reaction
            ClipActionButton(
                icon = Icons.Default.Videocam,
                label = "React",
                onClick = { /* Launch circular camera */ },
                testTag = "clip_react_button_${clip.id}"
            )
'''
content = content.replace('// Share / Relay Action', react_btn + '\n            // Share / Relay Action')

# Add 'Save Video' to DropdownMenu
save_video_item = '''
                    DropdownMenuItem(
                        text = { Text("Save Video ⬇️") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        onClick = {
                            showMenu = false
                            // Download with Creator Attribution Watermark
                            onSaveClip()
                        }
                    )
'''
content = content.replace('DropdownMenuItem(\n                        text = { Text("Report Clip 🚩") }', save_video_item + '\n                    DropdownMenuItem(\n                        text = { Text("Report Clip 🚩") }')

# Dual-Reach Indicator & Featured Gear
dual_reach = '''
            // Dual-Reach Overlay Indicator & Featured Gear
            Row(
                modifier = Modifier.padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val isCitySeed = (clip.distanceKm ?: 99.0) <= 50.0
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = if (isCitySeed) "📍 City Seed" else "🌐 Earth Reach",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCitySeed) Color(0xFF00C853) else Color(0xFF2979FF),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    modifier = Modifier.clickable { /* Open market */ }
                ) {
                    Text(
                        text = "🛍️ Featured Gear",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB703),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                
                if ((clip.distanceKm ?: 99.0) <= 3.0) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(1.dp, Color(0xFFFF9800).copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "🔊 Hyperlocal Audio",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF9800),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
'''
content = content.replace('// Creator Row', dual_reach + '\n            // Creator Row')

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(content)
