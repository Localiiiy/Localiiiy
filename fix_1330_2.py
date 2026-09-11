with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if i == 1344: # Text(
        new_lines.append("                }\n")
        new_lines.append(line)
    elif i == 1345: # }
        continue # delete the wrong }
    else:
        new_lines.append(line)

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "w") as f:
    f.writelines(new_lines)
