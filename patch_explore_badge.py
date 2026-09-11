import re

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "r") as f:
    text = f.read()

target = """                                        Text(
                                            text = "@${post.username}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            maxLines = 1
                                        )"""

replacement = """                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "@${post.username}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            com.example.ui.components.CreatorBadgeIcon(followers = post.creatorFollowers, showText = false)
                                        }"""

text = text.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "w") as f:
    f.write(text)
