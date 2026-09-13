import re

with open('app/src/main/java/com/example/ui/components/LocaliiiyTopBar.kt', 'r') as f:
    content = f.read()

# Replace the Direct Community Chat Box
new_chat_icon = """
                // Direct Community Chat
                Box {
                    IconButton(
                        onClick = onDirectMessagesClick,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .testTag("top_bar_messages_button")
                    ) {
                        Text(
                            text = "💬",
                            fontSize = 20.sp
                        )
                    }
                    if (hasUnreadMessages) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .offset(x = (-4).dp, y = 4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
"""

content = re.sub(
    r'// Direct Community Chat[\s\S]*?\}',
    new_chat_icon,
    content,
    flags=re.MULTILINE
)

with open('app/src/main/java/com/example/ui/components/LocaliiiyTopBar.kt', 'w') as f:
    f.write(content)
