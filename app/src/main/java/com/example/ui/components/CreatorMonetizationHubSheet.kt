package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreatorEarningsSummary
import com.example.data.CreatorPayoutAccount
import com.example.data.PayoutTransaction
import com.example.data.PlatformAdRevenueMetrics
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliPrimaryTeal
import com.example.util.CurrencyHelper
import com.example.util.LocaliCurrency
import com.example.util.LocaliLanguage
import com.example.util.LocalizationHelper
import com.example.util.LocaliStringKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorMonetizationHubSheet(
    earnings: CreatorEarningsSummary,
    payoutAccount: CreatorPayoutAccount,
    payoutHistory: List<PayoutTransaction>,
    platformMetrics: PlatformAdRevenueMetrics,
    currentCurrency: LocaliCurrency,
    currentLanguage: LocaliLanguage,
    onOpenCurrencyLanguageSelector: () -> Unit,
    onRequestPayout: (Double) -> Boolean,
    onUpdatePayoutAccount: (CreatorPayoutAccount) -> Unit,
    onOpenBoostAdDialog: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var showPayoutDialog by remember { mutableStateOf(false) }
    var payoutAmountInput by remember { mutableStateOf("${earnings.availableBalanceUSD.toInt()}") }
    var payoutSuccessMessage by remember { mutableStateOf<String?>(null) }
    var selectedPayoutMethod by remember { mutableStateOf(payoutAccount.payoutMethod) }
    var showAccountEditDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("creator_monetization_hub_sheet")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header & World Status
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MonetizationOn,
                                contentDescription = null,
                                tint = Color(0xFFFF9E00),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Creator Monetization Hub",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "Worldwide Partner Program (YouTube & Instagram Standard)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Currency Switcher Quick Pill
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = LocaliPrimaryTeal.copy(alpha = 0.12f),
                        border = BorderStroke(1.dp, LocaliPrimaryTeal.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .clickable(onClick = onOpenCurrencyLanguageSelector)
                            .testTag("monetization_currency_quick_switch")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = currentCurrency.flag, fontSize = 13.sp)
                            Text(
                                text = currentCurrency.code,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = LocaliPrimaryTeal
                            )
                        }
                    }
                }
            }

            // 2. Revenue Sharing Model Banner (55% Creator / 45% Platform)
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Transparent,
                    border = BorderStroke(1.dp, Color(0xFFFF9E00).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            ),
                            shape = RoundedCornerShape(16.dp)
                        )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(LocaliAccentMint)
                                )
                                Text(
                                    text = "CREATOR PARTNER PROGRAM (CPP)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaliAccentMint,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFF22C55E).copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Active & Earning 🟢",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Global Ad Revenue Split Engine",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Every ad viewed or clicked across videos, reels, and feeds is split automatically: 55% to creators, 45% to Localiiiy global platform infrastructure.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Visual Split Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(22.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(0.55f)
                                    .fillMaxHeight()
                                    .background(Color(0xFF22C55E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Creators: 55%",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(0.45f)
                                    .fillMaxHeight()
                                    .background(LocaliPrimaryTeal),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Platform: 45%",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // 3. Creator Earnings Summary in Selected Currency
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "YOUR ESTIMATED EARNINGS (${currentCurrency.code})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = CurrencyHelper.format(earnings.totalGrossEarnedUSD, currentCurrency),
                                    style = MaterialTheme.typography.displaySmall.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Total Lifetime Gross Revenue",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Available to Withdraw",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = CurrencyHelper.format(earnings.availableBalanceUSD, currentCurrency),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(14.dp))

                        // 4 Revenue Streams Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RevenueStreamCard(
                                icon = Icons.Default.VideoLibrary,
                                title = "In-Stream Video Ads",
                                amount = CurrencyHelper.format(earnings.inStreamVideoAdUSD, currentCurrency),
                                sub = "Pre-roll & mid-roll",
                                modifier = Modifier.weight(1f)
                            )
                            RevenueStreamCard(
                                icon = Icons.Default.DynamicFeed,
                                title = "Feed Sponsored Ads",
                                amount = CurrencyHelper.format(earnings.feedSponsoredAdUSD, currentCurrency),
                                sub = "Native feed units",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RevenueStreamCard(
                                icon = Icons.Default.Favorite,
                                title = "Super Thanks Tips",
                                amount = CurrencyHelper.format(earnings.superThanksTipsUSD, currentCurrency),
                                sub = "100% direct fan tips",
                                modifier = Modifier.weight(1f)
                            )
                            RevenueStreamCard(
                                icon = Icons.Default.Storefront,
                                title = "Marketplace Sales",
                                amount = CurrencyHelper.format(earnings.marketplaceSalesUSD, currentCurrency),
                                sub = "Direct peer sales",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Payout action button
                        Button(
                            onClick = { showPayoutDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("request_payout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Request Payout (${CurrencyHelper.format(earnings.availableBalanceUSD, currentCurrency)})",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 4. Worldwide Payout Rails & Setup
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "WORLDWIDE PAYOUT GATEWAY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LocaliPrimaryTeal,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Supported in 195+ countries",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            TextButton(onClick = { showAccountEditDialog = true }) {
                                Text("Configure", fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Active Method Display
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
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
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccountBalance,
                                        contentDescription = null,
                                        tint = LocaliPrimaryTeal,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Column {
                                        Text(
                                            text = payoutAccount.payoutMethod.replace("_", " "),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "${payoutAccount.accountIdentifier} • ${payoutAccount.taxComplianceStatus}",
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color(0xFF22C55E).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "Verified ✓",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Supported Rails: Stripe Connect (130+ currencies), PayPal Worldwide, SWIFT / IBAN Bank Wire, UPI (India), Pix (Brazil), and SEPA (EU).",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 5. Global Advertiser / Boost Post Card (App Owner & Creator Ad Spend)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Boost Posts & Launch Worldwide Ads",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Promote content to millions worldwide. Local businesses & creators can target regions in any currency.",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Button(
                                onClick = onOpenBoostAdDialog,
                                shape = RoundedCornerShape(100.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LocaliPrimaryTeal),
                                modifier = Modifier.testTag("open_boost_ad_button")
                            ) {
                                Text("+ Boost Ad", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // 6. Platform Global Revenue Transparency (How App Owner & Network Monetize)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "LOCALIIIY WORLDWIDE NETWORK STATS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlatformStatTile(
                                title = "Gross Global Ad Spend",
                                value = CurrencyHelper.format(platformMetrics.grossAdRevenueWorldwideUSD, currentCurrency),
                                modifier = Modifier.weight(1f)
                            )
                            PlatformStatTile(
                                title = "Creator Payouts (55%)",
                                value = CurrencyHelper.format(platformMetrics.creatorsDisbursedUSD, currentCurrency),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PlatformStatTile(
                                title = "Platform Net (45%)",
                                value = CurrencyHelper.format(platformMetrics.platformNetCommissionUSD, currentCurrency),
                                modifier = Modifier.weight(1f)
                            )
                            PlatformStatTile(
                                title = "Monetized Countries",
                                value = "${platformMetrics.countriesMonetized} Nations",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // 7. Recent Payout History
            item {
                Text(
                    text = "RECENT PAYOUT DISBURSEMENTS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                if (payoutHistory.isEmpty()) {
                    Text(
                        text = "No previous payouts yet. Your earnings will accumulate until withdrawal.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        payoutHistory.forEach { tx ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = "${tx.id} • ${tx.payoutMethod}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        val dateStr = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(tx.timestamp))
                                        Text(
                                            text = "$dateStr • Ref: ${tx.referenceId}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "+${CurrencyHelper.format(tx.amountUSD, currentCurrency)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = Color(0xFF2E7D32)
                                        )
                                        Text(
                                            text = tx.status,
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (tx.status == "COMPLETED") Color(0xFF2E7D32) else Color(0xFFFF9E00)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Payout Request Confirmation Dialog
    if (showPayoutDialog) {
        AlertDialog(
            onDismissRequest = { showPayoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = null,
                    tint = Color(0xFF2E7D32)
                )
            },
            title = { Text("Request Worldwide Payout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Transfer available earnings directly to your verified ${payoutAccount.payoutMethod.replace("_", " ")} account (${payoutAccount.accountIdentifier}).",
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = payoutAmountInput,
                        onValueChange = { payoutAmountInput = it },
                        label = { Text("Amount in USD ($)") },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("payout_amount_input")
                    )

                    val parsed = payoutAmountInput.toDoubleOrNull() ?: 0.0
                    val converted = CurrencyHelper.format(parsed, currentCurrency)
                    Text(
                        text = "Recipient receives approx: $converted in ${currentCurrency.code}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LocaliPrimaryTeal
                    )

                    Text(
                        text = "• Minimum threshold: $50.00 USD\n• Payout rail: ${payoutAccount.bankName}\n• Tax status: ${payoutAccount.taxComplianceStatus}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = payoutAmountInput.toDoubleOrNull() ?: 0.0
                        val success = onRequestPayout(amount)
                        if (success) {
                            showPayoutDialog = false
                            payoutSuccessMessage = "Payout of ${CurrencyHelper.format(amount, currentCurrency)} successfully initiated!"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier.testTag("confirm_payout_submit_button")
                ) {
                    Text("Confirm Transfer", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPayoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Payout Account Configuration Dialog
    if (showAccountEditDialog) {
        var tempMethod by remember { mutableStateOf(payoutAccount.payoutMethod) }
        var tempHolder by remember { mutableStateOf(payoutAccount.accountHolderName) }
        var tempIdentifier by remember { mutableStateOf(payoutAccount.accountIdentifier) }
        var tempBank by remember { mutableStateOf(payoutAccount.bankName) }

        val methodOptions = listOf(
            "STRIPE_CONNECT" to "Stripe Connect Global (130+ Currencies)",
            "PAYPAL_WORLDWIDE" to "PayPal Worldwide (200+ Countries)",
            "BANK_WIRE_SWIFT" to "International Wire (SWIFT / IBAN)",
            "UPI_INDIA" to "UPI (India Instant Transfers)",
            "PIX_BRAZIL" to "Pix (Brazil Central Bank)",
            "SEPA_EUROPE" to "SEPA (European Direct Bank)"
        )

        AlertDialog(
            onDismissRequest = { showAccountEditDialog = false },
            title = { Text("Configure Worldwide Payout Rail") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Select your preferred payment processor for receiving creator ad revenue and fan tips worldwide:",
                        fontSize = 12.sp
                    )

                    methodOptions.forEach { (key, label) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { tempMethod = key }
                        ) {
                            RadioButton(
                                selected = (tempMethod == key),
                                onClick = { tempMethod = key }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = label, fontSize = 12.sp)
                        }
                    }

                    OutlinedTextField(
                        value = tempHolder,
                        onValueChange = { tempHolder = it },
                        label = { Text("Account Holder / Legal Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempIdentifier,
                        onValueChange = { tempIdentifier = it },
                        label = { Text("Account ID / Email / IBAN / UPI ID") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = tempBank,
                        onValueChange = { tempBank = it },
                        label = { Text("Bank Name / Branch") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdatePayoutAccount(
                            payoutAccount.copy(
                                payoutMethod = tempMethod,
                                accountHolderName = tempHolder,
                                accountIdentifier = tempIdentifier,
                                bankName = tempBank
                            )
                        )
                        showAccountEditDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliPrimaryTeal)
                ) {
                    Text("Save Payout Method", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAccountEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun RevenueStreamCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    amount: String,
    sub: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocaliPrimaryTeal,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = amount,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = sub,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun PlatformStatTile(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = value,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = title,
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
