import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    text = f.read()

old_call = """                MainNavigationTab.FEED -> {
                    val allClips by viewModel.allClips.collectAsState()
                    FeedScreen(
                        clips = allClips,
                        posts = posts,"""
new_call = """                MainNavigationTab.FEED -> {
                    val allClips by viewModel.allClips.collectAsState()
                    FeedScreen(
                        clips = allClips,
                        marketplaceItems = marketplaceItems,
                        posts = posts,"""
text = text.replace(old_call, new_call, 1)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(text)
