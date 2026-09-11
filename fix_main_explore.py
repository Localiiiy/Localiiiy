import re

with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    text = f.read()

target = """                MainNavigationTab.EXPLORE -> {
                    ExploreScreen(
                        posts = posts,"""
replacement = """                MainNavigationTab.EXPLORE -> {
                    val allClips by viewModel.allClips.collectAsState()
                    ExploreScreen(
                        clips = allClips,
                        marketplaceItems = marketplaceItems,
                        posts = posts,"""
text = text.replace(target, replacement)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.write(text)
