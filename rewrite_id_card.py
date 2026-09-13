import re

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'r') as f:
    content = f.read()

# Change the signature
content = re.sub(
    r'fun ProfileIdentityCard\(([\s\S]*?)postsCount: Int,([\s\S]*?)clipsCount: Int,',
    r'fun ProfileIdentityCard(\1postsCount: Int,\2clipsCount: Int,\n    marketItemsCount: Int = 0,\n    studioVideosCount: Int = 0,',
    content
)

# Re-design the card layout
new_layout = """
        // Header: Localiiiy ID
        Text(
            text = "Localiiiy ID",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Main Details Row: Photo (Left) - Info (Center) - QR (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Photo (Left)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { /* TODO: Change avatar */ }
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(userProfile.avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = userProfile.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().padding(2.dp).clip(RoundedCornerShape(14.dp))
                )
            }

            // Info (Center)
            Column(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = userProfile.fullName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (userProfile.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified",
                            tint = EditorialVerified,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Text(
                    text = "@${userProfile.username}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = userProfile.neighborhood,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // QR Code (Right)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { /* TODO: Show full QR */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Scan to view space",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bio
        Text(
            text = userProfile.bio,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 20.sp
            ),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (userProfile.website.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "🔗 ${userProfile.website}",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stats Grid
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IdentityStatColumn(count = postsCount.toString(), label = "Posts")
                IdentityStatColumn(count = clipsCount.toString(), label = "Clips")
                IdentityStatColumn(count = marketItemsCount.toString(), label = "Market")
                IdentityStatColumn(count = studioVideosCount.toString(), label = "Studio")
                IdentityStatColumn(count = "${userProfile.neighborsCount}", label = "Connections")
                IdentityStatColumn(count = "${userProfile.followingCount}", label = "Connected")
            }
        }
"""

content = re.sub(
    r'// Header: Logo/Type[\s\S]*?IdentityStatColumn\(count = "\$\{userProfile.followingCount\}", label = "Connected"\)\s*}\s*}\s*',
    new_layout,
    content
)

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'w') as f:
    f.write(content)
