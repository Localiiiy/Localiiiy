import os

fpath = "app/src/main/java/com/example/ui/components/LiveRadarComponent.kt"
with open(fpath, "r") as f:
    content = f.read()

# 1. Update Click listener for blips
old_click = """                            .clickable {
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                if (item is OtherUserEntity) {
                                    dispatchUserTarget = item
                                    onUserClick(item)
                                } else if (item is PostEntity) {
                                    onPostClick(item)
                                }
                            }"""

new_click = """                            .clickable {
                                HapticHelper.triggerHaptic(context, haptic, androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                if (item is OtherUserEntity) {
                                    dispatchUserTarget = item
                                    if (isPremium) {
                                        onUserClick(item)
                                    }
                                } else if (item is PostEntity) {
                                    onPostClick(item)
                                }
                            }"""
content = content.replace(old_click, new_click)

# 2. Update DirectRadarChatDispatchCard usage
old_dispatch = """                DirectRadarChatDispatchCard(
                    targetUser = currentTarget,
                    onSendQuickGreeting = { _ -> dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                )"""

new_dispatch = """                DirectRadarChatDispatchCard(
                    targetUser = currentTarget,
                    isPremiumViewer = isPremium,
                    onSendQuickGreeting = { _ -> dispatchUserTarget = null },
                    onClose = { dispatchUserTarget = null },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                )"""
content = content.replace(old_dispatch, new_dispatch)

with open(fpath, "w") as f:
    f.write(content)
