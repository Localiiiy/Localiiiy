import re

with open("app/src/main/java/com/example/data/InitialData.kt", "r") as f:
    text = f.read()

# Elena is id=1
# Marcus is id=2
# Let's just do a simple replacement for the Posts.
text = text.replace('PostEntity(\n            id = 1,\n            username = "elena.design",', 'PostEntity(\n            id = 1,\n            username = "elena.design",\n            creatorFollowers = 120000000,')
text = text.replace('PostEntity(\n            id = 2,\n            username = "marcus.brew",', 'PostEntity(\n            id = 2,\n            username = "marcus.brew",\n            creatorFollowers = 55000000,')
text = text.replace('ClipEntity(\n            id = 1,\n            username = "elena.design",', 'ClipEntity(\n            id = 1,\n            username = "elena.design",\n            creatorFollowers = 120000000,')

with open("app/src/main/java/com/example/data/InitialData.kt", "w") as f:
    f.write(text)
