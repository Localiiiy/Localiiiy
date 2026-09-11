package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Information & Badges",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            item {
                Text(
                    text = "APP PAGES & TERMINOLOGY",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoTerm("Pulse Feed", "The primary chronological stream of Posts, short-form Clips, and Marketplace items from your connected neighbors.")
                InfoTerm("Live Radar", "A spatial mapping interface that visually pings users and activities happening in your immediate physical radius.")
                InfoTerm("Studio", "The dedicated space for long-form video content creation and consumption, focused on high-engagement viewing and monetization.")
                InfoTerm("Marketplace", "A decentralized local hub to buy, sell, or trade goods directly with neighbors in your proximity.")
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "CREATOR BADGES & TIERS",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Badges are displayed next to usernames across the app (in Space, Search, Radar, etc.) based on your connection/connection milestones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                BadgeTierItem(10_000, "A simple single cross-star vector.")
                BadgeTierItem(100_000, "An upward-pointing minimalist rocket vector line.")
                BadgeTierItem(500_000, "A planet icon with a single satellite dot.")
                BadgeTierItem(1_000_000, "A massive gas giant planet silhouette.")
                BadgeTierItem(5_000_000, "A sleek minimalist circle surrounded by an intense corona glow.")
                BadgeTierItem(10_000_000, "A stylized digital cloud formation grid.")
                BadgeTierItem(50_000_000, "A sharp, high-tech spiral layout.")
                BadgeTierItem(100_000_000, "The highest honor: Localiiiy app logo icon. Our best creator badge.")
                BadgeTierItem(500_000_000, "An overhead view of a massive system array.")
                BadgeTierItem(1_000_000_000, "The mathematical infinity loop styled like an orbital path.")
            }
        }
    }
}

@Composable
fun InfoTerm(term: String, definition: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = term, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = definition, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun BadgeTierItem(threshold: Int, description: String) {
    val tier = BadgeTier.getTier(threshold)
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.Top
    ) {
        CreatorBadgeIcon(followers = threshold, showText = true, modifier = Modifier.padding(top = 2.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Unlocks at ${formatNumber(threshold)} connections", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(text = description, fontSize = 13.sp)
        }
    }
}

private fun formatNumber(num: Int): String {
    return when {
        num >= 1_000_000_000 -> "${num / 1_000_000_000}B"
        num >= 1_000_000 -> "${num / 1_000_000}M"
        num >= 1_000 -> "${num / 1_000}K"
        else -> num.toString()
    }
}
