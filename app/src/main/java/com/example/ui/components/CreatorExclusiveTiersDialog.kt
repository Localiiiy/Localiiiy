package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CreatorExclusiveTier
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency

@Composable
fun CreatorExclusiveTiersDialog(
    tiers: List<CreatorExclusiveTier>,
    currentCurrency: LocaliiiyCurrency = LocaliiiyCurrency.USD,
    isCreatorView: Boolean = true,
    subscribedTierIds: Set<String> = emptySet(),
    onUpdateTier: (CreatorExclusiveTier) -> Unit = {},
    onAddTier: (CreatorExclusiveTier) -> Unit = {},
    onToggleTierEnabled: (String, Boolean) -> Unit = { _, _ -> },
    onSubscribeTier: (CreatorExclusiveTier) -> Unit = {},
    onDismiss: () -> Unit
) {
    var showAddTierSheet by remember { mutableStateOf(false) }
    var editingTier by remember { mutableStateOf<CreatorExclusiveTier?>(null) }

    // Calculate MRR (Monthly Recurring Revenue)
    val totalSubscribers = tiers.sumOf { it.activeSubscribersCount }
    val totalGrossMRR = tiers.sumOf { it.monthlyPriceUSD * it.activeSubscribersCount }
    val netCreatorMRR = totalGrossMRR * 0.90
    val projectedARR = netCreatorMRR * 12

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 14.dp)
                .testTag("creator_exclusive_tiers_dialog"),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFFFD700).copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("👑", fontSize = 18.sp)
                            }
                        }
                        Column {
                            Text(
                                text = if (isCreatorView) "Exclusive Content Tiers" else "Creator Subscriber Tiers",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isCreatorView) "Monthly Recurring Revenue (MRR) Engine" else "Unlock exclusive videos, perks & 4K masterclasses",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_tiers_dialog_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // MRR Analytics Dashboard Card (Only in Creator View)
                    if (isCreatorView) {
                        item {
                            CreatorMRRSummaryCard(
                                totalSubscribers = totalSubscribers,
                                netMRR = netCreatorMRR,
                                projectedARR = projectedARR,
                                currentCurrency = currentCurrency
                            )
                        }
                    }

                    // Tiers List Header & Add Tier Action
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Membership Tiers (${tiers.count { it.isEnabled }})",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            if (isCreatorView) {
                                Button(
                                    onClick = { showAddTierSheet = true },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.testTag("add_custom_tier_btn")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Define Tier", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Tiers List
                    items(tiers, key = { it.id }) { tier ->
                        CreatorTierItemCard(
                            tier = tier,
                            currentCurrency = currentCurrency,
                            isCreatorView = isCreatorView,
                            isSubscribed = subscribedTierIds.contains(tier.id),
                            onToggleEnabled = { enabled -> onToggleTierEnabled(tier.id, enabled) },
                            onEditTier = { editingTier = tier },
                            onSubscribe = { onSubscribeTier(tier) }
                        )
                    }

                    // Monetization Maximization Tip
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF10B981).copy(alpha = 0.08f)
                            ),
                            border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("💰", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Revenue Maximization: 90% Creator Revenue Share",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF10B981)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Define at least 2 tiers (Supporter & VIP). Videos marked as 'Subscribers Only' will automatically prompt viewers to subscribe to one of your active tiers, generating reliable monthly income directly into your payout wallet.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // Modal to Add Custom Tier or Edit Existing Tier
    if (showAddTierSheet || editingTier != null) {
        val targetTier = editingTier
        DefineTierModal(
            existingTier = targetTier,
            onDismiss = {
                showAddTierSheet = false
                editingTier = null
            },
            onSave = { savedTier ->
                if (targetTier != null) {
                    onUpdateTier(savedTier)
                } else {
                    onAddTier(savedTier)
                }
                showAddTierSheet = false
                editingTier = null
            }
        )
    }
}

@Composable
private fun CreatorMRRSummaryCard(
    totalSubscribers: Int,
    netMRR: Double,
    projectedARR: Double,
    currentCurrency: LocaliiiyCurrency
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.35f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Monthly Recurring Revenue",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "90% Creator Share",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF10B981),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${CurrencyHelper.format(netMRR, currentCurrency)} / mo",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp
                ),
                color = Color(0xFFFFD700)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Active Subscribers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$totalSubscribers members", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Annualized Run-Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(CurrencyHelper.format(projectedARR, currentCurrency), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatorTierItemCard(
    tier: CreatorExclusiveTier,
    currentCurrency: LocaliiiyCurrency,
    isCreatorView: Boolean,
    isSubscribed: Boolean,
    onToggleEnabled: (Boolean) -> Unit,
    onEditTier: () -> Unit,
    onSubscribe: () -> Unit
) {
    val tierColor = Color(tier.badgeColorHex)
    val monthlyFormatted = CurrencyHelper.format(tier.monthlyPriceUSD, currentCurrency)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tier_item_${tier.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (tier.isEnabled) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        border = BorderStroke(
            width = if (isSubscribed) 2.dp else 1.dp,
            color = if (isSubscribed) Color(0xFF10B981) else tierColor.copy(alpha = 0.4f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Emoji + Name + Price + Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(tier.badgeSymbol, fontSize = 24.sp)
                    Column {
                        Text(
                            text = tier.name,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "$monthlyFormatted / month",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = tierColor
                        )
                    }
                }

                if (isCreatorView) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEditTier,
                            modifier = Modifier.size(32.dp).testTag("edit_tier_${tier.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Tier", modifier = Modifier.size(16.dp))
                        }
                        Switch(
                            checked = tier.isEnabled,
                            onCheckedChange = onToggleEnabled,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF10B981)
                            ),
                            modifier = Modifier.testTag("toggle_tier_${tier.id}")
                        )
                    }
                } else {
                    if (isSubscribed) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFF10B981).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFF10B981))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Subscribed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                            }
                        }
                    } else {
                        Button(
                            onClick = onSubscribe,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = tierColor),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("subscribe_tier_btn_${tier.id}")
                        ) {
                            Text("Join $monthlyFormatted", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                }
            }

            if (tier.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = tier.description,
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Perks List
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                tier.perks.forEach { perk ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = perk,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (isCreatorView) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${tier.activeSubscribersCount} active subscribers",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "MRR: ${CurrencyHelper.format(tier.monthlyPriceUSD * tier.activeSubscribersCount * 0.90, currentCurrency)} / mo",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFD700)
                    )
                }
            }
        }
    }
}

@Composable
private fun DefineTierModal(
    existingTier: CreatorExclusiveTier?,
    onDismiss: () -> Unit,
    onSave: (CreatorExclusiveTier) -> Unit
) {
    var tierName by remember { mutableStateOf(existingTier?.name ?: "") }
    var priceText by remember { mutableStateOf(existingTier?.monthlyPriceUSD?.toString() ?: "4.99") }
    var badgeSymbol by remember { mutableStateOf(existingTier?.badgeSymbol ?: "💎") }
    var description by remember { mutableStateOf(existingTier?.description ?: "") }
    var perkInput by remember { mutableStateOf("") }
    var perksList by remember {
        mutableStateOf(
            existingTier?.perks ?: listOf(
                "Exclusive 4K Studio Episodes",
                "Supporter Badge in Comments & Remarks",
                "Early Access Premieres"
            )
        )
    }

    val emojiChoices = listOf("🥉", "🥈", "🥇", "💎", "👑", "⚡", "🌟", "🚀")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (existingTier != null) "Edit Exclusive Tier" else "Define New Content Tier",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Select Badge Symbol", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            emojiChoices.forEach { emoji ->
                                Surface(
                                    shape = CircleShape,
                                    color = if (badgeSymbol == emoji) Color(0xFFFFD700).copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (badgeSymbol == emoji) BorderStroke(1.5.dp, Color(0xFFFFD700)) else null,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clickable { badgeSymbol = emoji }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(emoji, fontSize = 18.sp)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = tierName,
                            onValueChange = { tierName = it },
                            label = { Text("Tier Name (e.g. VIP Insider)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("tier_name_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            label = { Text("Monthly Price (USD $)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("tier_price_input")
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Short Pitch / Description") },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth().testTag("tier_desc_input")
                        )
                    }

                    item {
                        Text("Included Perks & Benefits", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = perkInput,
                                onValueChange = { perkInput = it },
                                placeholder = { Text("e.g. Private 4K Livestreams", fontSize = 12.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (perkInput.isNotBlank()) {
                                        perksList = perksList + perkInput.trim()
                                        perkInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Add")
                            }
                        }
                    }

                    items(perksList) { perk ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                                Text(perk, fontSize = 12.sp)
                            }
                            IconButton(
                                onClick = { perksList = perksList.filter { it != perk } },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove perk", modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        val parsedPrice = priceText.toDoubleOrNull() ?: 4.99
                        val newTier = existingTier?.copy(
                            name = tierName.ifBlank { "Custom Tier" },
                            monthlyPriceUSD = parsedPrice,
                            badgeSymbol = badgeSymbol,
                            description = description,
                            perks = perksList
                        ) ?: CreatorExclusiveTier(
                            id = "tier_${System.currentTimeMillis()}",
                            name = tierName.ifBlank { "Custom Tier" },
                            monthlyPriceUSD = parsedPrice,
                            badgeSymbol = badgeSymbol,
                            description = description,
                            perks = perksList,
                            activeSubscribersCount = 0,
                            isEnabled = true
                        )
                        onSave(newTier)
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF3366)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("save_tier_btn")
                ) {
                    Text(
                        text = if (existingTier != null) "Update Tier" else "Save & Launch Tier",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
