import re

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "r") as f:
    text = f.read()

# Add to PostEntity
text = text.replace('val filterName: String = "Normal"\n)', 'val filterName: String = "Normal",\n    val creatorFollowers: Int = 0\n)')

# Add to ClipEntity
text = text.replace('val filterName: String = "Normal"\n) {', 'val filterName: String = "Normal",\n    val creatorFollowers: Int = 0\n) {')

# Add to MarketplaceItemEntity
text = text.replace('val sellerFullName: String,', 'val sellerFullName: String,\n    val creatorFollowers: Int = 0,')

# Add to StudioVideoEntity
text = text.replace('val creatorHandle: String,', 'val creatorHandle: String,\n    val creatorFollowers: Int = 0,')

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "w") as f:
    f.write(text)
