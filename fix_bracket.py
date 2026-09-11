with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

def fix(c):
    count = 0
    for i, char in enumerate(c):
        if char == '{':
            count += 1
        elif char == '}':
            count -= 1
            if count < 0:
                print(f"Removing extra closing bracket at index {i}")
                return c[:i] + c[i+1:]
    return c

new_content = content
while True:
    newer_content = fix(new_content)
    if newer_content == new_content:
        break
    new_content = newer_content

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'w') as f:
    f.write(new_content)
