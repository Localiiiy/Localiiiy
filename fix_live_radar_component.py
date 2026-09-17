import os

fpath = "app/src/main/java/com/example/ui/components/LiveRadarComponent.kt"
with open(fpath, "r") as f:
    content = f.read()

old_call = """                DirectRadarChatDispatchSheet(
                    targetUser = currentTarget,
                    onSendQuickGreeting = { _ -> dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                )"""

new_call = """                DirectRadarChatDispatchSheet(
                    targetUser = currentTarget,
                    isPremiumViewer = isPremium,
                    onSendQuickGreeting = { _ -> dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                )"""
content = content.replace(old_call, new_call)

with open(fpath, "w") as f:
    f.write(content)
