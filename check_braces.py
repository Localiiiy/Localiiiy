with open("app/src/main/java/com/example/MainActivity.kt") as f:
    text = f.read()
balance = 0
for i, c in enumerate(text):
    if c == '{': balance += 1
    elif c == '}': balance -= 1
    if balance < 0:
        print(f"Unmatched closing brace at index {i}")
        break
print(f"Final balance: {balance}")
