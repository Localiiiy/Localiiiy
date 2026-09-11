with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt") as f:
    lines = f.readlines()
balance = 0
for i, line in enumerate(lines):
    for c in line:
        if c == '{': balance += 1
        elif c == '}': balance -= 1
    if balance < 0:
        print(f"Unmatched closing brace at line {i+1}: {line}")
        break
print(f"Final balance: {balance}")
