import re

with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "r") as f:
    text = f.read()

# Update signature
old_sig = """fun PulseFeedComponent(
    posts: List<PostEntity>,
    clips: List<ClipEntity> = emptyList(),
    stories: List<StoryEntity>,"""
new_sig = """import com.example.data.MarketplaceItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PulseFeedComponent(
    posts: List<PostEntity>,
    clips: List<ClipEntity> = emptyList(),
    marketplaceItems: List<MarketplaceItemEntity> = emptyList(),
    stories: List<StoryEntity>,"""
text = text.replace(old_sig, new_sig, 1)

# Add to combinedFeedItems
old_combined = """    val combinedFeedItems = remember(posts, clips) {
        (posts + clips).sortedByDescending {"""
new_combined = """    val combinedFeedItems = remember(posts, clips, marketplaceItems) {
        (posts + clips + marketplaceItems).sortedByDescending {"""
text = text.replace(old_combined, new_combined, 1)

# Add timestamp
old_sort = """                is PostEntity -> it.timestamp
                is ClipEntity -> it.timestamp
                else -> 0L"""
new_sort = """                is PostEntity -> it.timestamp
                is ClipEntity -> it.timestamp
                is MarketplaceItemEntity -> it.timestamp
                else -> 0L"""
text = text.replace(old_sort, new_sort, 1)

# Add to keys
old_key = """                        is PostEntity -> "post_${item.id}"
                        is ClipEntity -> "clip_${item.id}"
                        else -> "unknown_$index\""""
new_key = """                        is PostEntity -> "post_${item.id}"
                        is ClipEntity -> "clip_${item.id}"
                        is MarketplaceItemEntity -> "market_${item.id}"
                        else -> "unknown_$index\""""
text = text.replace(old_key, new_key, 1)

# Fix items UI
old_ui = """                        is ClipEntity -> {
                            // Temporary Text instead of non-existent PulseClipCard to avoid errors, or implement them.
                            Card(modifier = Modifier.fillMaxWidth().padding(16.dp).clickable { onUserProfileClick(item.username) }) {
                                Text("Clip by ${item.username}", modifier = Modifier.padding(16.dp))
                            }
                        }"""
new_ui = """                        is ClipEntity -> {
                            PulseClipCard(clip = item, onClick = { onUserProfileClick(item.username) })
                        }
                        is MarketplaceItemEntity -> {
                            PulseMarketItemCard(item = item, onClick = { onUserProfileClick(item.sellerUsername) })
                        }"""
text = text.replace(old_ui, new_ui, 1)

# Append custom cards
new_cards = """
@Composable
fun PulseClipCard(clip: ClipEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                AsyncImage(
                    model = coil.request.ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                        .data(clip.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Clip thumbnail",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.Center).size(48.dp)
                )
            }
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = clip.userAvatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = "Clip by ${clip.username}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                    Text(text = clip.description, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                }
            }
        }
    }
}

@Composable
fun PulseMarketItemCard(item: MarketplaceItemEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrls.firstOrNull(),
                contentDescription = null,
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)).background(Color.Gray),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "from ${item.sellerUsername}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "$${item.price}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
"""
text += new_cards

# Need to make sure import for Icons.Default.PlayArrow and coil are there. Wait, they might already be.
with open("app/src/main/java/com/example/ui/components/PulseFeedComponent.kt", "w") as f:
    f.write(text)
