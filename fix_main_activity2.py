with open("app/src/main/java/com/example/MainActivity.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if "onOpenDataAnalysis" in line and 400 < i < 500:
        continue # Skip it in StudioScreen block
    new_lines.append(line)

with open("app/src/main/java/com/example/MainActivity.kt", "w") as f:
    f.writelines(new_lines)
