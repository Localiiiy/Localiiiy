import re

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "r") as f:
    text = f.read()

target = """                    com.example.ui.components.PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onUserProfileClick = { onUserProfileClick(post.username) },
                        onLocationClick = {},
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage
                    )"""

replacement = """                    com.example.ui.components.PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onUserClick = { onUserProfileClick(post.username) }
                    )"""

text = text.replace(target, replacement)

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "w") as f:
    f.write(text)
