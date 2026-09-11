with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if i == 1330:  # 1331 is 1-indexed, i=1330 is '                    Text(\n'
        # Let's verify
        if "Text(" in line:
            pass # skip it
            continue
    elif i == 1345: # 1346 is '                                Text('
        # add the closing brace for Row
        new_lines.append(line)
        new_lines.append("                }\n")
        continue
    new_lines.append(line)

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "w") as f:
    f.writelines(new_lines)
