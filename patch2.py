import re

with open("app/src/main/java/com/example/ui/screens/ClipsScreen.kt", "r") as f:
    text = f.read()

# Add the import
text = text.replace("import androidx.compose.ui.Modifier", "import androidx.compose.ui.Modifier\nimport com.example.ui.components.AdBannerComponent")

# Find the ClipItem block
target = """            val isCurrentPage = pagerState.currentPage == page

            ClipItem(
                clip = clip,"""

replacement = """            val isCurrentPage = pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
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
            }
"""

text = text.replace("""            ClipItem(
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
            )""", replacement.replace("""            val isCurrentPage = pagerState.currentPage == page

            Box(modifier = Modifier.fillMaxSize()) {
""", ""))

with open("app/src/main/java/com/example/ui/screens/ClipsScreen.kt", "w") as f:
    f.write(text)
