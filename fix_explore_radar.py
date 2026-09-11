import re

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "r") as f:
    text = f.read()

target = """                // Recent Pulses Detected on Radar
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {"""

replacement = """                Spacer(modifier = Modifier.height(16.dp))
                // Full Detail Radar Feed
                filteredPosts.forEach { post ->
                    com.example.ui.components.PostCard(
                        post = post,
                        onLikeClick = { onLikePost(post) },
                        onCommentClick = { onCommentPost(post) },
                        onShareClick = { onSharePost(post) },
                        onSaveClick = { onSavePost(post) },
                        onUserProfileClick = { onUserProfileClick(post.username) },
                        onLocationClick = {},
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                clips.filter { it.distanceKm != null && it.distanceKm <= (localRadiusKm ?: 1000.0) }.forEach { clip ->
                    com.example.ui.components.PulseClipCard(
                        clip = clip,
                        onClick = { onUserProfileClick(clip.username) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                marketplaceItems.forEach { item ->
                    com.example.ui.components.PulseMarketItemCard(
                        item = item,
                        onClick = { onUserProfileClick(item.sellerUsername) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        ExploreViewMode.GRID -> {"""

# We need to exactly match and replace the right block.
# Since the regex might be tricky, let's just do a string split.
before, rest = text.split("                // Recent Pulses Detected on Radar", 1)
_, after = rest.split("        ExploreViewMode.GRID -> {", 1)

new_text = before + replacement + "\n" + after

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "w") as f:
    f.write(new_text)
