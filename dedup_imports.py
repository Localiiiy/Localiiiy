with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
seen_imports = set()
for line in lines:
    stripped = line.strip()
    if stripped.startswith('import '):
        if stripped in seen_imports:
            continue
        seen_imports.add(stripped)
    new_lines.append(line)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.writelines(new_lines)
