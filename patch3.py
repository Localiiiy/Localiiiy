import re

with open("app/src/main/java/com/example/ui/screens/ClipsScreen.kt", "r") as f:
    text = f.read()

target = """                ClipItem(
                    clip = clip,
                    isCurrentPage = isCurrentPage,
                    isSoundMuted = isSoundMuted,
                    onToggleSound = onToggleSound,
                    onLikeClip = { onLikeClip(clip) },
                    onCommentClip = { onCommentClip(clip) },
                    onShareClip = { onShareClip(clip) },
                    onSaveClip = { onSaveClip(clip) },
                    onFollowToggle = { onFollowToggle(clip) },
                    onUserProfileClick = { onUserProfileClick(clip.username) },
                    onReportClip = { reason -> onReportClip?.invoke(clip, reason) },
                    onBlockCreator = { onBlockCreator?.invoke(clip.username) }
                )
                
                // Show AdBanner on every 4th clip, placed near the bottom
                if (page > 0 && page % 4 == 0 && isCurrentPage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .padding(bottom = 120.dp) // Avoid obscuring the bottom actions
                    ) {
                        AdBannerComponent()
                    }
                }"""

replacement = """            Box(modifier = Modifier.fillMaxSize()) {
                ClipItem(
                    clip = clip,
                    isCurrentPage = isCurrentPage,
                    isSoundMuted = isSoundMuted,
                    onToggleSound = onToggleSound,
                    onLikeClip = { onLikeClip(clip) },
                    onCommentClip = { onCommentClip(clip) },
                    onShareClip = { onShareClip(clip) },
                    onSaveClip = { onSaveClip(clip) },
                    onFollowToggle = { onFollowToggle(clip) },
                    onUserProfileClick = { onUserProfileClick(clip.username) },
                    onReportClip = { reason -> onReportClip?.invoke(clip, reason) },
                    onBlockCreator = { onBlockCreator?.invoke(clip.username) }
                )
                
                // Show AdBanner on every 4th clip, placed near the bottom
                if (page > 0 && page % 4 == 0 && isCurrentPage) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(androidx.compose.ui.Alignment.BottomCenter)
                            .padding(bottom = 120.dp) // Avoid obscuring the bottom actions
                    ) {
                        AdBannerComponent()
                    }
                }
            }"""

text = text.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/ClipsScreen.kt", "w") as f:
    f.write(text)
