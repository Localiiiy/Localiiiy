import re

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "r") as f:
    text = f.read()

# remove all creatorFollowers
text = text.replace("    val creatorFollowers: Int = 0,\n", "")
text = text.replace("    val creatorFollowers: Int = 0\n", "")

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "w") as f:
    f.write(text)
