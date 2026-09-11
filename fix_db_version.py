import re

with open("app/src/main/java/com/example/data/AppDatabase.kt", "r") as f:
    text = f.read()

# Update version = 10 to version = 11 (or dynamically find the version and increment it)
text = re.sub(r'version\s*=\s*\d+', 'version = 11', text)

with open("app/src/main/java/com/example/data/AppDatabase.kt", "w") as f:
    f.write(text)
