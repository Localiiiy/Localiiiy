with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "r") as f:
    text = f.read()

text = text.replace('val filterName: String = "Normal",\n) {', 'val filterName: String = "Normal",\n    val creatorFollowers: Int = 0\n) {')

with open("app/src/main/java/com/example/data/LocaliiiyModels.kt", "w") as f:
    f.write(text)
