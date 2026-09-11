import re

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "r") as f:
    text = f.read()

# Header: username with badge
target1 = """                    Text(
                        text = userProfile.username,"""
replacement1 = """                    Text(
                        text = userProfile.username,"""

# Actually, it might be better to add the badge after the verified icon, or near fullName.
# Let's add it near fullName (line 353).
target2 = """                        Text(
                            text = userProfile.fullName,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (userProfile.category.isNotBlank()) {"""

replacement2 = """                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.fullName,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            com.example.ui.components.CreatorBadgeIcon(followers = userProfile.followersCount, showText = true)
                        }
                        if (userProfile.category.isNotBlank()) {"""

text = text.replace(target2, replacement2)

with open("app/src/main/java/com/example/ui/screens/ProfileScreen.kt", "w") as f:
    f.write(text)
