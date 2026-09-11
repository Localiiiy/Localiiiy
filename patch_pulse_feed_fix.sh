sed -i '91,$d' app/src/main/java/com/example/ui/components/PulseFeedComponent.kt
cat << 'INNER_EOF' >> app/src/main/java/com/example/ui/components/PulseFeedComponent.kt
    val combinedPosts = posts

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        state = refreshState,
        modifier = modifier.fillMaxSize().testTag("pulse_feed_pull_to_refresh")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // 1. Stories Tray
            item {
                StoriesTray(
                    stories = stories,
                    userProfile = userProfile,
                    onStoryClick = onStoryClick,
                    onAddStoryClick = onAddStoryClick
                )
            }

            // 2. Pulse Hyperlocal Bar & Proximity Filter Pills
            item {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Text(
                        text = "Hyperlocal Pulse Feed",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(1.0, 3.0, 5.0, 10.0)) { radius ->
                            FilterChip(
                                selected = selectedRadius == radius,
                                onClick = { 
                                    selectedRadius = radius
                                    onRadiusFilterChange(radius)
                                },
                                label = { Text("${radius.toInt()} km") }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "${combinedPosts.size} Pulses nearby", style = MaterialTheme.typography.bodySmall)
                }
            }

            // 3. Posts
            itemsIndexed(combinedPosts) { index, post ->
                PostCard(
                    post = post,
                    onLikeClick = { onLikePost(post) },
                    onCommentClick = { onCommentPost(post) },
                    onShareClick = { onSharePost(post) },
                    onSaveClick = { onSavePost(post) },
                    onUserClick = { onUserProfileClick(post.username) }
                )
                
                // Ads
                if (index > 0 && index % 5 == 0 && sponsoredAds.isNotEmpty()) {
                    val ad = sponsoredAds[(index / 5) % sponsoredAds.size]
                    SponsoredAdCard(
                        ad = ad,
                        onImpression = { onAdImpression(ad.id) },
                        onClick = { onAdClick(ad.id) }
                    )
                }
            }

            item {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                    TextButton(onClick = {}) {
                        Text("Expand to All Pulses", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
INNER_EOF
