import re

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

# Add the import
text = text.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport com.example.ui.components.AdBannerComponent")

# Find the itemsIndexed block
target = """                itemsIndexed(items = combinedFeedItems, key = { index, item -> 
                    when(item) {
                        is PostEntity -> "post_${item.id}"
                        is ClipEntity -> "clip_${item.id}"
                        is StudioVideoEntity -> "studio_${item.id}"
                        else -> "unknown_$index"
                    }
                }) { index, item ->
                    when (item) {"""

replacement = """                itemsIndexed(items = combinedFeedItems, key = { index, item -> 
                    when(item) {
                        is PostEntity -> "post_${item.id}"
                        is ClipEntity -> "clip_${item.id}"
                        is StudioVideoEntity -> "studio_${item.id}"
                        else -> "unknown_$index"
                    }
                }) { index, item ->
                    if (index > 0 && index % 3 == 0) {
                        AdBannerComponent()
                    }
                    when (item) {"""

text = text.replace(target, replacement)

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
