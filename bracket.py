import sys

with open('app/src/main/java/com/example/ui/screens/ClipsScreen.kt', 'r') as f:
    content = f.read()

count = 0
for i, c in enumerate(content):
    if c == '{':
        count += 1
    elif c == '}':
        count -= 1
        if count < 0:
            print(f"Extra closing bracket found at index {i}")
            print(content[i-100:i+100])
            break
print("Final count:", count)
