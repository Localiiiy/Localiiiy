import re

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "r") as f:
    text = f.read()

# Add to PostEntity
text = text.replace("val sharesCount: Int = 0,", "val sharesCount: Int = 0,\n    val creatorFollowers: Int = 0,")

# Add to ClipEntity
text = text.replace("val sharesCount: Int = 120,", "val sharesCount: Int = 120,\n    val creatorFollowers: Int = 0,")

# Add to MarketplaceItemEntity
text = text.replace("val sellerFullName: String,", "val sellerFullName: String,\n    val sellerFollowers: Int = 0,")

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "w") as f:
    f.write(text)
