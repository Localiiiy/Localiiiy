with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "r") as f:
    lines = f.readlines()

new_lines = []
skip = False
for i, line in enumerate(lines):
    if i == 1330:
        new_lines.append(line)
        new_lines.append('                    Text(\n')
        new_lines.append('                        text = video.creatorFullName,\n')
        new_lines.append('                        fontSize = 12.sp,\n')
        new_lines.append('                        fontWeight = FontWeight.Medium,\n')
        new_lines.append('                        color = MaterialTheme.colorScheme.onSurfaceVariant\n')
        new_lines.append('                    )\n')
        new_lines.append('                    if (video.isCreatorVerified) {\n')
        new_lines.append('                        Icon(\n')
        new_lines.append('                            imageVector = Icons.Default.CheckCircle,\n')
        new_lines.append('                            contentDescription = "Verified Creator",\n')
        new_lines.append('                            tint = MaterialTheme.colorScheme.primary,\n')
        new_lines.append('                            modifier = Modifier.size(14.dp)\n')
        new_lines.append('                        )\n')
        new_lines.append('                    }\n')
        skip = True
    elif i == 1345:
        skip = False
    elif not skip:
        new_lines.append(line)

with open("app/src/main/java/com/example/ui/screens/StudioScreen.kt", "w") as f:
    f.writelines(new_lines)
