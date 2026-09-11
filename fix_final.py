with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
for i, line in enumerate(lines):
    if i == 1348: # which is line 1349
        new_lines.append(line)
        new_lines.append("                                )\n")
    elif "comments.add(0, newCommentText.trim())" in line:
        new_lines.append(line.replace("comments.add(0, newCommentText.trim())", "comments = listOf(newCommentText.trim()) + comments"))
    else:
        new_lines.append(line)

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "w") as f:
    f.writelines(new_lines)
