import re

with open("app/src/main/java/com/example/data/InitialData.kt", "r") as f:
    text = f.read()

# I will revert all creatorFollowers
text = text.replace("            creatorFollowers = 120000000,\n", "")
text = text.replace("            creatorFollowers = 55000000,\n", "")
text = text.replace("            creatorFollowers = 5500000,\n", "")
text = text.replace("            creatorFollowers = 550000,\n", "")

# I will add it only to PostEntity and ClipEntity
# Let's find "val starterPosts = listOf(" and replace carefully
