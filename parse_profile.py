with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'r') as f:
    lines = f.readlines()
for i, line in enumerate(lines[:300]):
    print(f"{i}: {line.rstrip()}")
