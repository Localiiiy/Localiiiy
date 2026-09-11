import re

with open("app/src/main/java/com/example/data/InitialData.kt", "r") as f:
    text = f.read()

text = text.replace("            creatorFollowers = 120000000,\n", "")
text = text.replace("            creatorFollowers = 55000000,\n", "")

# Now add them safely.
# Post 1
text = text.replace('PostEntity(\n            id = 1,\n            username = "elena.design",\n', 'PostEntity(\n            id = 1,\n            username = "elena.design",\n            creatorFollowers = 120000000,\n')

# Post 2
text = text.replace('PostEntity(\n            id = 2,\n            username = "marcus.brew",\n', 'PostEntity(\n            id = 2,\n            username = "marcus.brew",\n            creatorFollowers = 55000000,\n')

# Clip 1
text = text.replace('ClipEntity(\n            id = 1,\n            username = "elena.design",\n', 'ClipEntity(\n            id = 1,\n            username = "elena.design",\n            creatorFollowers = 120000000,\n')

# MarketItem 1
text = text.replace('MarketplaceItemEntity(\n            id = 1,\n', 'MarketplaceItemEntity(\n            id = 1,\n            creatorFollowers = 55000000,\n')

# StudioVideo 1
text = text.replace('StudioVideoEntity(\n            id = 1,\n', 'StudioVideoEntity(\n            id = 1,\n            creatorFollowers = 120000000,\n')

with open("app/src/main/java/com/example/data/InitialData.kt", "w") as f:
    f.write(text)
