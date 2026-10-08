package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CreatorExclusiveTier
import com.example.data.CreatorEarningsSummary
import com.example.data.CreatorPayoutAccount
import com.example.data.DraftClipEntity
import com.example.data.MerchantBountyEntity
import com.example.data.PayoutTransaction
import com.example.data.PlatformAdRevenueMetrics
import com.example.data.UserProfileEntity
import com.example.ui.components.studio.MerchantBountyBoardDialog
import com.example.ui.components.studio.UnifiedDraftVaultDialog
import com.example.ui.screens.WalletScreen
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorMonetizationHubSheet(
    userProfile: UserProfileEntity,
    earnings: CreatorEarningsSummary,
    payoutAccount: CreatorPayoutAccount,
    payoutHistory: List<PayoutTransaction>,
    platformMetrics: PlatformAdRevenueMetrics,
    currentCurrency: LocaliiiyCurrency,
    currentLanguage: LocaliiiyLanguage,
    bounties: List<MerchantBountyEntity> = emptyList(),
    drafts: List<DraftClipEntity> = emptyList(),
    exclusiveTiers: List<CreatorExclusiveTier> = emptyList(),
    onUpdateTier: (CreatorExclusiveTier) -> Unit = {},
    onAddTier: (CreatorExclusiveTier) -> Unit = {},
    onToggleTierEnabled: (String, Boolean) -> Unit = { _, _ -> },
    onClaimBounty: ((MerchantBountyEntity) -> Unit)? = null,
    onRouteDraftToClips: ((DraftClipEntity) -> Unit)? = null,
    onRouteDraftToMarket: ((DraftClipEntity) -> Unit)? = null,
    onRouteDraftToPulse: ((DraftClipEntity) -> Unit)? = null,
    onDeleteDraft: ((DraftClipEntity) -> Unit)? = null,
    onOpenCurrencyLanguageSelector: () -> Unit,
    onRequestPayout: (Double) -> Boolean,
    onUpdatePayoutAccount: (CreatorPayoutAccount) -> Unit,
    onOpenBoostAdDialog: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var showFullWalletScreen by remember { mutableStateOf(false) }
    var showEditPayoutDialog by remember { mutableStateOf(false) }
    var showBountyBoardInMonetization by remember { mutableStateOf(false) }
    var showDraftVaultInMonetization by remember { mutableStateOf(false) }
    var showTiersDialog by remember { mutableStateOf(false) }

    if (showTiersDialog) {
        CreatorExclusiveTiersDialog(
            tiers = exclusiveTiers,
            currentCurrency = currentCurrency,
            isCreatorView = true,
            onUpdateTier = onUpdateTier,
            onAddTier = onAddTier,
            onToggleTierEnabled = onToggleTierEnabled,
            onDismiss = { showTiersDialog = false }
        )
    }

    if (showBountyBoardInMonetization) {
        MerchantBountyBoardDialog(
            bounties = bounties,
            onClaimBounty = { bounty ->
                onClaimBounty?.invoke(bounty)
                showBountyBoardInMonetization = false
            },
            onDismiss = { showBountyBoardInMonetization = false }
        )
    }

    if (showDraftVaultInMonetization) {
        UnifiedDraftVaultDialog(
            drafts = drafts,
            onRouteDraftToClips = { draft ->
                onRouteDraftToClips?.invoke(draft)
                showDraftVaultInMonetization = false
            },
            onRouteDraftToMarket = { draft ->
                onRouteDraftToMarket?.invoke(draft)
                showDraftVaultInMonetization = false
            },
            onRouteDraftToPulse = { draft ->
                onRouteDraftToPulse?.invoke(draft)
                showDraftVaultInMonetization = false
            },
            onDeleteDraft = { draft -> onDeleteDraft?.invoke(draft) },
            onDismiss = { showDraftVaultInMonetization = false }
        )
    }

    if (showFullWalletScreen) {
        Dialog(
            onDismissRequest = { showFullWalletScreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                WalletScreen(
                    earnings = earnings,
                    payoutAccount = payoutAccount,
                    payoutHistory = payoutHistory,
                    currentCurrency = currentCurrency,
                    onRequestPayout = onRequestPayout,
                    onNavigateBack = { showFullWalletScreen = false }
                )
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("creator_monetization_hub_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(bottom = 36.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Monetization Hub",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Creator Revenue & Universal Disbursals",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            // Global Currency Switcher Banner
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenCurrencyLanguageSelector() }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = currentCurrency.flag, fontSize = 20.sp)
                            Column {
                                Text(
                                    text = "Active Currency: ${currentCurrency.code} (${currentCurrency.symbol})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${currentCurrency.currencyName} • 160+ World Currencies Supported",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
            
            // Section: Creator & Community Wallet Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("creator_community_wallet_card")
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AccountBalanceWallet,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Creator & Community Wallet",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            // Dynamic Threshold Badge with no awkward vertical text wrapping
                            val isEligible = earnings.availableBalanceUSD >= payoutAccount.minimumPayoutUSD
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isEligible) Color(0xFF00E676).copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondaryContainer,
                                border = BorderStroke(1.dp, if (isEligible) Color(0xFF00E676) else MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Text(
                                    text = if (isEligible) "WITHDRAWAL READY" else "$1,000 THRESHOLD",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isEligible) Color(0xFF00E676) else MaterialTheme.colorScheme.onSecondaryContainer,
                                    maxLines = 1,
                                    softWrap = false,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Available Creator Balance",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = CurrencyHelper.format(earnings.availableBalanceUSD, currentCurrency),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Base Amount: $${String.format(java.util.Locale.US, "%,.2f", earnings.availableBalanceUSD)} USD",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar towards $1,000 minimum
                        val progressFraction = (earnings.availableBalanceUSD / payoutAccount.minimumPayoutUSD).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        val minPayoutFormatted = CurrencyHelper.format(payoutAccount.minimumPayoutUSD, currentCurrency)
                        val canWithdraw = earnings.availableBalanceUSD >= payoutAccount.minimumPayoutUSD

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onRequestPayout(earnings.availableBalanceUSD) },
                                enabled = canWithdraw,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = if (canWithdraw) "Request Disbursal" else "Min $minPayoutFormatted",
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }

                            OutlinedButton(
                                onClick = { showFullWalletScreen = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(imageVector = Icons.Default.ReceiptLong, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Full Wallet Ledger", fontSize = 12.sp, maxLines = 1, softWrap = false)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Minimum withdrawal baseline is $1,000.00 USD ($minPayoutFormatted in ${currentCurrency.code}) for fraud prevention & creator tax protection.",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Section: Hyperlocal Revenue Projection Tool (Requested Feature)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hyperlocal_revenue_calculator_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📊", fontSize = 22.sp)
                            Column {
                                Text(
                                    text = "Hyperlocal Revenue Projection Tool",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Project potential monthly earnings based on your local network",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Slider 1: Connection Count
                        val initialConnections = if (userProfile.connectionsCount > 0) userProfile.connectionsCount.toFloat() else 1480f
                        var connections by remember { mutableFloatStateOf(initialConnections) }
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Projected Connections:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format(java.util.Locale.US, "%,.0f", connections),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Slider(
                                value = connections,
                                onValueChange = { connections = it },
                                valueRange = 50f..15000f,
                                steps = 299, // step is 50
                                colors = SliderDefaults.colors(
                                    thumbColor = MaterialTheme.colorScheme.primary,
                                    activeTrackColor = MaterialTheme.colorScheme.primary
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Slider 2: Conversion Rate (1% to 5%)
                        var conversionRate by remember { mutableFloatStateOf(2.5f) }
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Conversion Rate (e.g. Market Sales):", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = String.format(java.util.Locale.US, "%.1f%%", conversionRate),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF10B981)
                                )
                            }
                            Slider(
                                value = conversionRate,
                                onValueChange = { conversionRate = it },
                                valueRange = 1.0f..5.0f,
                                steps = 7, // step is 0.5%
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFF10B981),
                                    activeTrackColor = Color(0xFF10B981)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Slider 3: Avg Transaction Value
                        var avgOrderValue by remember { mutableFloatStateOf(25f) }
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Avg Transaction Value:", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(
                                    text = CurrencyHelper.format(avgOrderValue.toDouble(), currentCurrency),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFFC084FC)
                                )
                            }
                            Slider(
                                value = avgOrderValue,
                                onValueChange = { avgOrderValue = it },
                                valueRange = 5f..150f,
                                steps = 29, // step is $5
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFC084FC),
                                    activeTrackColor = Color(0xFFC084FC)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Calculations
                        val marketQty = Math.round(connections * (conversionRate / 100f))
                        val marketRevUSD = marketQty * avgOrderValue.toDouble()

                        val tipsQty = Math.round(connections * ((conversionRate * 0.5f) / 100f))
                        val tipsRevUSD = tipsQty * (avgOrderValue.toDouble() * 0.4)

                        val totalCombinedUSD = marketRevUSD + tipsRevUSD

                        // Live results layout
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.05f),
                                border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("MARKET SALES", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = CurrencyHelper.format(marketRevUSD, currentCurrency),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF10B981)
                                    )
                                    Text("$marketQty items sold", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFC084FC).copy(alpha = 0.05f),
                                border = BorderStroke(1.dp, Color(0xFFC084FC).copy(alpha = 0.15f))
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("CREATOR TIPS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = CurrencyHelper.format(tipsRevUSD, currentCurrency),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFC084FC)
                                    )
                                    Text("$tipsQty tips received", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Total profit banner
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("COMBINED PROJECTED INCOME", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(
                                        text = CurrencyHelper.format(totalCombinedUSD, currentCurrency),
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Text("🚀", fontSize = 24.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Estimates are calculated using standard 1-5% local network conversion baselines. Creators retain 90% for fan tips and 100% directly for Escrow Marketplace Handshakes.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            // Section: Revenue Breakdown
            item {
                Text(
                    text = "Revenue Streams Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Highlighted Exclusive Subscriber Tiers MRR Card
                val netMRR = exclusiveTiers.sumOf { it.monthlyPriceUSD * it.activeSubscribersCount } * 0.90
                val totalSubs = exclusiveTiers.sumOf { it.activeSubscribersCount }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showTiersDialog = true }
                        .testTag("monetization_subscriber_tiers_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFD700).copy(alpha = 0.10f)
                    ),
                    border = BorderStroke(1.dp, Color(0xFFFFD700).copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("👑", fontSize = 22.sp)
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Subscriber Tiers (MRR)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFFD700)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = Color(0xFF10B981).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "90% RevShare",
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF10B981),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "$totalSubs active recurring subscribers • ${exclusiveTiers.count { it.isEnabled }} defined tiers",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${CurrencyHelper.format(netMRR, currentCurrency)}/mo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFFD700)
                            )
                            Text(
                                text = "Manage Tiers ⚙️",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    RevenueStreamCard(
                        title = "Ad Revenue",
                        amount = CurrencyHelper.format(earnings.inStreamVideoAdUSD + earnings.feedSponsoredAdUSD, currentCurrency),
                        modifier = Modifier.weight(1f)
                    )
                    RevenueStreamCard(
                        title = "Fan Tips",
                        amount = CurrencyHelper.format(earnings.superThanksTipsUSD, currentCurrency),
                        modifier = Modifier.weight(1f)
                    )
                    RevenueStreamCard(
                        title = "Marketplace",
                        amount = CurrencyHelper.format(earnings.marketplaceSalesUSD, currentCurrency),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            
            // Section: Bounties & Creator Draft Vault
            item {
                Text(
                    text = "Bounties & Creator Vault",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showBountyBoardInMonetization = true }
                            .testTag("monetization_bounties_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF10B981).copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🎯", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Bounties Board",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF10B981),
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "${bounties.count { !it.isClaimed }} active dispatches",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { showDraftVaultInMonetization = true }
                            .testTag("monetization_vault_card"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📁", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Draft Vault",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1
                                )
                            }
                            Text(
                                text = "${drafts.size} saved drafts",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            // Section: Advertising & Promotion
            item {
                Text(
                    text = "Advertising & Promotion",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Boost & Sponsor Dashboard", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Promote market listings or buy a glowing sponsored pin on the Live Radar.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onOpenBoostAdDialog,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSecondaryContainer, contentColor = MaterialTheme.colorScheme.secondaryContainer),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Open Boost Dashboard")
                        }
                    }
                }
            }
            
            // Section: Universal Payout Rails & Banking
            item {
                Text(
                    text = "Universal Payout Rails & Banking",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = when {
                                                payoutAccount.payoutMethod.contains("PAYPAL", ignoreCase = true) -> Icons.Default.Payment
                                                payoutAccount.payoutMethod.contains("BANK", ignoreCase = true) || payoutAccount.payoutMethod.contains("SWIFT", ignoreCase = true) -> Icons.Default.AccountBalance
                                                payoutAccount.payoutMethod.contains("UPI", ignoreCase = true) -> Icons.Default.QrCode
                                                else -> Icons.Default.CreditCard
                                            },
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = when (payoutAccount.payoutMethod) {
                                            "STRIPE_CONNECT" -> "Stripe Connect Worldwide"
                                            "PAYPAL_WORLDWIDE" -> "PayPal Worldwide Instant Disbursal"
                                            "BANK_WIRE_SWIFT" -> "Global Bank Wire / SWIFT / IBAN"
                                            "UPI_INDIA" -> "UPI Instant Auto-Pay (India)"
                                            "PIX_BRAZIL" -> "Pix Instant Key (Brazil)"
                                            "WISE_MULTICURRENCY" -> "Wise Multi-Currency Account"
                                            "SEPA_EUROPE" -> "SEPA Direct Credit (Europe)"
                                            else -> payoutAccount.payoutMethod
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    )
                                    Text(
                                        text = payoutAccount.accountIdentifier,
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            TextButton(onClick = { showEditPayoutDialog = true }) {
                                Text("Configure")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(10.dp))

                        // Supported rails pills
                        Text(
                            text = "Supported Worldwide Payout Rails:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val rails = listOf("🏦 Bank Wire", "💳 Stripe Connect", "🅿️ PayPal", "🌐 Wise", "🇮🇳 UPI", "🇧🇷 Pix", "🇪🇺 SEPA", "🇬🇧 UK FPS")
                            items(rails) { rail ->
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Text(
                                        text = rail,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Configure Payout Rail Dialog
    if (showEditPayoutDialog) {
        var selectedMethod by remember { mutableStateOf(payoutAccount.payoutMethod) }
        var accountId by remember { mutableStateOf(payoutAccount.accountIdentifier) }
        var accountName by remember { mutableStateOf(payoutAccount.accountHolderName) }
        var bankName by remember { mutableStateOf(payoutAccount.bankName) }
        var swiftCode by remember { mutableStateOf(payoutAccount.swiftOrBic) }

        AlertDialog(
            onDismissRequest = { showEditPayoutDialog = false },
            title = {
                Text(
                    text = "Configure Universal Payout Rail",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Select your preferred international disbursal destination:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Method chips
                    val methods = listOf(
                        "STRIPE_CONNECT" to "💳 Stripe Connect",
                        "PAYPAL_WORLDWIDE" to "🅿️ PayPal",
                        "BANK_WIRE_SWIFT" to "🏦 SWIFT / IBAN",
                        "WISE_MULTICURRENCY" to "🌐 Wise",
                        "UPI_INDIA" to "🇮🇳 UPI",
                        "PIX_BRAZIL" to "🇧🇷 Pix",
                        "SEPA_EUROPE" to "🇪🇺 SEPA"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(methods) { (methodKey, label) ->
                            FilterChip(
                                selected = selectedMethod == methodKey,
                                onClick = { selectedMethod = methodKey },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = accountName,
                        onValueChange = { accountName = it },
                        label = { Text("Account Holder Full Legal Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = accountId,
                        onValueChange = { accountId = it },
                        label = {
                            Text(
                                when (selectedMethod) {
                                    "PAYPAL_WORLDWIDE" -> "PayPal Email Address"
                                    "BANK_WIRE_SWIFT" -> "IBAN or Account Number"
                                    "UPI_INDIA" -> "UPI ID (e.g., name@okaxis)"
                                    "PIX_BRAZIL" -> "Pix Key (CPF/CNPJ/Email/Phone)"
                                    "WISE_MULTICURRENCY" -> "Wise Registered Email"
                                    else -> "Email / Account Identifier"
                                }
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (selectedMethod == "BANK_WIRE_SWIFT" || selectedMethod == "SEPA_EUROPE") {
                        OutlinedTextField(
                            value = bankName,
                            onValueChange = { bankName = it },
                            label = { Text("Bank Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = swiftCode,
                            onValueChange = { swiftCode = it },
                            label = { Text("SWIFT / BIC Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdatePayoutAccount(
                            payoutAccount.copy(
                                payoutMethod = selectedMethod,
                                accountIdentifier = accountId.ifBlank { payoutAccount.accountIdentifier },
                                accountHolderName = accountName.ifBlank { payoutAccount.accountHolderName },
                                bankName = bankName.ifBlank { payoutAccount.bankName },
                                swiftOrBic = swiftCode.ifBlank { payoutAccount.swiftOrBic }
                            )
                        )
                        showEditPayoutDialog = false
                    }
                ) {
                    Text("Save & Bind Rail")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditPayoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RevenueStreamCard(title: String, amount: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = amount, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

