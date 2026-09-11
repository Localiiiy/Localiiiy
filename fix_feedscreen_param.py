with open("app/src/main/java/com/example/ui/screens/FeedScreen.kt", "r") as f:
    text = f.read()

text = text.replace("clips: List<com.example.data.ClipEntity> = emptyList(),", "clips: List<com.example.data.ClipEntity> = emptyList(),\n    marketplaceItems: List<com.example.data.MarketplaceItemEntity> = emptyList(),")

with open("app/src/main/java/com/example/ui/screens/FeedScreen.kt", "w") as f:
    f.write(text)
