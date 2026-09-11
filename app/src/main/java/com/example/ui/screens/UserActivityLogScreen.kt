package com.example.ui.screens

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Article
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.UserActivityEntity
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ActivityLogCategory(val label: String, val icon: ImageVector) {
    ALL("All Activities", Icons.Outlined.Checklist),
    RADAR("Radar & Proximity", Icons.Outlined.Radar),
    SOCIAL("Feed & Social", Icons.Outlined.FavoriteBorder),
    MARKET("Marketplace", Icons.Outlined.ShoppingBag),
    STUDIO("Studio & Clips", Icons.Outlined.VideoLibrary),
    SECURITY("Security & Privacy", Icons.Outlined.Security)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserActivityLogScreen(
    activities: List<UserActivityEntity>,
    onBackClick: () -> Unit,
    onDeleteActivity: (Long) -> Unit,
    onClearAllActivities: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedCategory by remember { mutableStateOf(ActivityLogCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    // Filtered activities
    val filteredActivities = remember(activities, selectedCategory, searchQuery) {
        activities.filter { act ->
            val matchesCategory = when (selectedCategory) {
                ActivityLogCategory.ALL -> true
                ActivityLogCategory.RADAR -> act.activityType.contains("RADAR") || act.activityType.contains("PULSE") || act.activityType.contains("WAVE")
                ActivityLogCategory.SOCIAL -> act.activityType.contains("POST") || act.activityType.contains("COMMENT") || act.activityType.contains("FOLLOW") || act.activityType.contains("WAVE")
                ActivityLogCategory.MARKET -> act.activityType.contains("MARKET") || act.activityType.contains("ITEM")
                ActivityLogCategory.STUDIO -> act.activityType.contains("STUDIO") || act.activityType.contains("CLIP") || act.activityType.contains("VIDEO") || act.activityType.contains("DRAFT")
                ActivityLogCategory.SECURITY -> act.activityType.contains("PRIVACY") || act.activityType.contains("SECURITY") || act.activityType.contains("PASSWORD")
            }

            val query = searchQuery.trim().lowercase()
            val matchesQuery = query.isEmpty() ||
                    act.targetTitle.lowercase().contains(query) ||
                    act.activityType.lowercase().contains(query) ||
                    (act.extraDetails?.lowercase()?.contains(query) == true)

            matchesCategory && matchesQuery
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Clear User Activity Log?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "This will permanently remove your on-device activity history. As per the app's NDA and transparency policies, audit records are stored locally for your own review. Are you sure you want to clear them?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllActivities()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_clear_activity_log_button")
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showClearConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_clear_activity_log_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "User Activity Log",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Transparency & Audit Trail • NDA Policy",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("activity_log_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Export Audit Trail
                    IconButton(
                        onClick = {
                            val auditSummary = buildString {
                                appendLine("=== LOCALIY AUDIT TRAIL EXPORT ===")
                                appendLine("Timestamp: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())}")
                                appendLine("Policy: NDA Confidentiality & User Transparency Guarantee (§4.2)")
                                appendLine("Total Recorded Events: ${activities.size}")
                                appendLine("===================================\n")
                                activities.take(30).forEachIndexed { index, act ->
                                    val time = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()).format(Date(act.timestamp))
                                    appendLine("[${index + 1}] $time | ${act.activityType}")
                                    appendLine("     Action: ${act.targetTitle}")
                                    act.extraDetails?.let { appendLine("     Details: $it") }
                                    appendLine()
                                }
                            }

                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "Localiiiy User Activity Audit Trail")
                                putExtra(Intent.EXTRA_TEXT, auditSummary)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Export Activity Log"))
                        },
                        modifier = Modifier.testTag("export_activity_log_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export Audit Log",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Clear All button
                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("clear_activity_log_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = "Clear All Activities",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier.testTag("user_activity_log_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Transparency & NDA Policy Notice Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = LocaliiiyPrimaryTeal.copy(alpha = 0.08f)
                    ),
                    border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = LocaliiiyPrimaryTeal.copy(alpha = 0.18f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.Shield,
                                        contentDescription = null,
                                        tint = LocaliiiyPrimaryTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Audit Trail & Transparency Assurance",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Confidentiality Charter §4.2 • NDA Policy Compliant",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = LocaliiiyPrimaryTeal
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "To guarantee 100% transparency under the app's NDA and user security framework, this log maintains an auditable record of your local interactions, radar updates, content saves, and privacy changes. All logs remain strictly on your device.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BadgePill(icon = Icons.Outlined.Lock, label = "100% On-Device")
                            BadgePill(icon = Icons.Outlined.VerifiedUser, label = "Zero Brokering")
                            BadgePill(icon = Icons.Outlined.Visibility, label = "User Auditable")
                        }
                    }
                }
            }

            // Metric Summary Row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = "Logged Events",
                        value = activities.size.toString(),
                        icon = Icons.Outlined.History,
                        color = LocaliiiyPrimaryTeal,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Radar Audits",
                        value = activities.count { it.activityType.contains("RADAR") || it.activityType.contains("PULSE") }.toString(),
                        icon = Icons.Outlined.Radar,
                        color = LocaliiiyDeepNavy,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Social Sparks",
                        value = activities.count { it.activityType.contains("LIKED") || it.activityType.contains("SAVED") }.toString(),
                        icon = Icons.Outlined.FavoriteBorder,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Search Filter Field
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search activity, target, or details...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("activity_log_search_field")
                )
            }

            // Category Filter Pills
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ActivityLogCategory.values()) { cat ->
                        val isSelected = selectedCategory == cat
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(100.dp),
                            modifier = Modifier.testTag("activity_filter_${cat.name.lowercase()}")
                        )
                    }
                }
            }

            // Activity Log List Items
            if (filteredActivities.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Outlined.EventNote,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (searchQuery.isNotBlank()) "No activities match \"$searchQuery\"" else "No activities recorded yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Interactions within Pulse, Radar, Studio, and Market will appear here automatically.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(filteredActivities, key = { it.id }) { activity ->
                    ActivityLogItemCard(
                        activity = activity,
                        onDelete = { onDeleteActivity(activity.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityLogItemCard(
    activity: UserActivityEntity,
    onDelete: () -> Unit
) {
    val config = remember(activity.activityType) {
        getActivityTypeConfig(activity.activityType)
    }

    val formattedTime = remember(activity.timestamp) {
        formatRelativeTime(activity.timestamp)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activity_item_${activity.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = config.containerColor,
                modifier = Modifier.size(42.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = config.icon,
                        contentDescription = null,
                        tint = config.tintColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Details Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Type Badge
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = config.badgeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = config.friendlyName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            color = config.badgeColor,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }

                    // Timestamp
                    Text(
                        text = formattedTime,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Target Title
                Text(
                    text = activity.targetTitle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.5.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                // Extra Details
                if (!activity.extraDetails.isNullOrBlank()) {
                    Text(
                        text = activity.extraDetails,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 11.5.sp,
                            lineHeight = 15.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Thumbnail or Delete Button
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(IntrinsicSize.Min)
            ) {
                if (!activity.targetPreviewUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(activity.targetPreviewUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Activity preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_activity_${activity.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Delete record",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun BadgePill(
    icon: ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocaliiiyPrimaryTeal,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private data class ActivityConfig(
    val friendlyName: String,
    val icon: ImageVector,
    val tintColor: Color,
    val containerColor: Color,
    val badgeColor: Color
)

private fun getActivityTypeConfig(type: String): ActivityConfig {
    return when {
        type.contains("RADAR") || type.contains("PULSE") -> ActivityConfig(
            friendlyName = "Radar & Pulse",
            icon = Icons.Outlined.Radar,
            tintColor = LocaliiiyPrimaryTeal,
            containerColor = LocaliiiyPrimaryTeal.copy(alpha = 0.15f),
            badgeColor = LocaliiiyPrimaryTeal
        )
        type.contains("WAVE") -> ActivityConfig(
            friendlyName = "Neighbor Wave",
            icon = Icons.Outlined.WavingHand,
            tintColor = Color(0xFF0288D1),
            containerColor = Color(0xFF0288D1).copy(alpha = 0.15f),
            badgeColor = Color(0xFF0288D1)
        )
        type.contains("LIKED") -> ActivityConfig(
            friendlyName = "Liked Content",
            icon = Icons.Default.Favorite,
            tintColor = Color(0xFFE91E63),
            containerColor = Color(0xFFE91E63).copy(alpha = 0.15f),
            badgeColor = Color(0xFFE91E63)
        )
        type.contains("SAVED") -> ActivityConfig(
            friendlyName = "Saved Item",
            icon = Icons.Default.Bookmark,
            tintColor = Color(0xFFFF9800),
            containerColor = Color(0xFFFF9800).copy(alpha = 0.15f),
            badgeColor = Color(0xFFFF9800)
        )
        type.contains("MARKET") -> ActivityConfig(
            friendlyName = "Marketplace",
            icon = Icons.Outlined.ShoppingBag,
            tintColor = Color(0xFF4CAF50),
            containerColor = Color(0xFF4CAF50).copy(alpha = 0.15f),
            badgeColor = Color(0xFF4CAF50)
        )
        type.contains("STUDIO") || type.contains("CLIP") || type.contains("VIDEO") || type.contains("DRAFT") -> ActivityConfig(
            friendlyName = "Studio & Clips",
            icon = Icons.Outlined.Movie,
            tintColor = Color(0xFF9C27B0),
            containerColor = Color(0xFF9C27B0).copy(alpha = 0.15f),
            badgeColor = Color(0xFF9C27B0)
        )
        type.contains("PRIVACY") || type.contains("SECURITY") || type.contains("PASSWORD") -> ActivityConfig(
            friendlyName = "Security & Privacy",
            icon = Icons.Outlined.Security,
            tintColor = Color(0xFF00ACC1),
            containerColor = Color(0xFF00ACC1).copy(alpha = 0.15f),
            badgeColor = Color(0xFF00ACC1)
        )
        else -> ActivityConfig(
            friendlyName = "Activity",
            icon = Icons.Outlined.History,
            tintColor = Color.Gray,
            containerColor = Color.Gray.copy(alpha = 0.15f),
            badgeColor = Color.Gray
        )
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        diff < 0 -> "Just now"
        seconds < 60 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        hours < 24 -> "${hours}h ago"
        days < 7 -> "${days}d ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}
