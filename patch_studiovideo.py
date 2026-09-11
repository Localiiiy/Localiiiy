import re

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "r") as f:
    text = f.read()

text = text.replace("val creatorHandle: String,", "val creatorHandle: String,\n    val creatorFollowers: Int = 0,")

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "w") as f:
    f.write(text)
