import re

with open("app/src/main/java/com/example/ui/components/PostCard.kt", "r") as f:
    text = f.read()

target = """                            Text(
                                text = post.username,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )"""

replacement = """                            Text(
                                text = post.username,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            CreatorBadgeIcon(followers = post.creatorFollowers, showText = false)"""

text = text.replace(target, replacement)

with open("app/src/main/java/com/example/ui/components/PostCard.kt", "w") as f:
    f.write(text)
