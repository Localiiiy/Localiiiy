import re

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "r") as f:
    text = f.read()

target1 = """                                    Text(
                                        text = video.creatorFullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (video.isVerified) {"""

replacement1 = """                                    Text(
                                        text = video.creatorFullName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    com.example.ui.components.CreatorBadgeIcon(followers = video.creatorFollowers)
                                    if (video.isVerified) {"""

text = text.replace(target1, replacement1)

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "w") as f:
    f.write(text)
