import re

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "r") as f:
    text = f.read()

import_statement = "import com.example.ui.components.PulseClipCard\nimport com.example.ui.components.PulseMarketItemCard\n"
if "PulseClipCard" not in text:
    text = text.replace("import com.example.ui.components.PostCard", import_statement + "import com.example.ui.components.PostCard")

insertion_point = """                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(landmarks) { (landmark, dist) ->
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier.clickable { searchQuery = landmark }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = landmark,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = dist,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }"""

radar_feed_ui = """
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nearby Pulse Items",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val combinedRadarItems = remember(posts, clips, marketplaceItems) {
                    (posts + clips + marketplaceItems).sortedByDescending {
                        when (it) {
                            is PostEntity -> it.timestamp
                            is ClipEntity -> it.timestamp
                            is MarketplaceItemEntity -> it.timestamp
                            else -> 0L
                        }
                    }
                }
                
                combinedRadarItems.forEach { item ->
                    when (item) {
                        is PostEntity -> {
                            PostCard(
                                post = item,
                                currentUserId = userProfile.id,
                                onLikeClick = { onLikePost(item) },
                                onCommentClick = { onCommentPost(item) },
                                onShareClick = { onSharePost(item) },
                                onUserClick = { onUserProfileClick(item.username) }
                            )
                        }
                        is ClipEntity -> {
                            PulseClipCard(clip = item, onClick = { onUserProfileClick(item.username) })
                        }
                        is MarketplaceItemEntity -> {
                            PulseMarketItemCard(item = item, onClick = { onUserProfileClick(item.sellerUsername) })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
"""

if "combinedRadarItems" not in text:
    text = text.replace(insertion_point, insertion_point + radar_feed_ui)

with open("app/src/main/java/com/example/ui/screens/ExploreScreen.kt", "w") as f:
    f.write(text)
