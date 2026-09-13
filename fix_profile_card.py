import re

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'r') as f:
    content = f.read()

# 1. Update the "Localiiiy ID" text to bold, italic, underline, multi-color
old_header = """        // Header: Localiiiy ID
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
        )"""

new_header = """        // Header: Localiiiy ID
        Text(
            text = "LOCALIIIY ID",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally).graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                    }
                },
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF8B5CF6),
                        Color(0xFF3B82F6),
                        Color(0xFF10B981)
                    )
                ),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )"""

# Instead of the complex style, I'll use the proper text style with Brush.
new_header_clean = """        // Header: Localiiiy ID
        Text(
            text = "LOCALIIIY ID",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
            color = Color.Transparent,
            style = androidx.compose.ui.text.TextStyle(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF14B8A6), Color(0xFFF59E0B))
                ),
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                fontSize = 24.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        )"""

content = content.replace(old_header, new_header_clean)

# 2. Interchange Photo and QR, and increase size
old_row = """        // Main Details Row: Photo (Left) - Info (Center) - QR (Right)
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
        }"""

new_row = """        // Main Details Row: QR (Left) - Info (Center) - Photo (Right)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // QR Code (Left)
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { /* TODO: Show full QR */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.QrCode2,
                    contentDescription = "Scan to view space",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(76.dp)
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

            // Photo (Right)
            Box(
                modifier = Modifier
                    .size(105.dp)
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
        }"""
content = content.replace(old_row, new_row)

with open('app/src/main/java/com/example/ui/components/ProfileIdentityCard.kt', 'w') as f:
    f.write(content)
