import re

with open("app/src/main/java/com/example/data/InitialData.kt", "r") as f:
    text = f.read()

# Let's give Elena (id=1) 120M followers
text = text.replace('username = "elena.design",', 'username = "elena.design",\n            creatorFollowers = 120000000,')
# Give Marcus (id=2) 55M followers
text = text.replace('username = "marcus.brew",', 'username = "marcus.brew",\n            creatorFollowers = 55000000,')
# Give Sarah (id=3) 5M followers
text = text.replace('username = "sarah.vintage",', 'username = "sarah.vintage",\n            creatorFollowers = 5500000,')
# Give David (id=4) 550k followers
text = text.replace('username = "david.hikes",', 'username = "david.hikes",\n            creatorFollowers = 550000,')

with open("app/src/main/java/com/example/data/InitialData.kt", "w") as f:
    f.write(text)
