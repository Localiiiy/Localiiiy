with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

# Fix annotations
text = text.replace("@OptIn(ExperimentalMaterial3Api::class)\n@Composable\n@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun PulseFeedComponent", "@OptIn(ExperimentalMaterial3Api::class)\n@Composable\nfun PulseFeedComponent")

# Fix properties
text = text.replace("clip.thumbnailUrl", "clip.mediaUrl")
text = text.replace("clip.userAvatarUrl", "clip.userAvatar")
text = text.replace("clip.description", "clip.caption")
text = text.replace("item.imageUrls.firstOrNull()", "item.imageUrl")

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
