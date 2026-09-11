import re

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

target1 = """                    Text(text = "Clip by ${clip.username}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)"""
replacement1 = """                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Clip by ${clip.username}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        CreatorBadgeIcon(followers = clip.creatorFollowers, showText = false)
                    }"""
text = text.replace(target1, replacement1)

target2 = """                    Text(text = item.sellerFullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)"""
replacement2 = """                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = item.sellerFullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(4.dp))
                        CreatorBadgeIcon(followers = item.sellerFollowers, showText = false)
                    }"""
text = text.replace(target2, replacement2)

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
