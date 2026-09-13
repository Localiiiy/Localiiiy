package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreatorEarningsSummary
import com.example.data.CreatorPayoutAccount
import com.example.data.PayoutTransaction
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    earnings: CreatorEarningsSummary,
    payoutAccount: CreatorPayoutAccount,
    payoutHistory: List<PayoutTransaction>,
    currentCurrency: LocaliiiyCurrency,
    onRequestPayout: (Double) -> Boolean,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var selectedTransactionForReceipt by remember { mutableStateOf<PayoutTransaction?>(null) }
    var historyFilter by remember { mutableStateOf("ALL") } // "ALL", "COMPLETED", "PROCESSING"

    val minPayoutThresholdUSD = payoutAccount.minimumPayoutUSD // $1,000.00
    val isEligibleForWithdrawal = earnings.availableBalanceUSD >= minPayoutThresholdUSD
    val progressToThreshold = (earnings.availableBalanceUSD / minPayoutThresholdUSD).coerceIn(0.0, 1.0).toFloat()

    val filteredHistory = remember(payoutHistory, historyFilter) {
        when (historyFilter) {
            "COMPLETED" -> payoutHistory.filter { it.status.equals("COMPLETED", ignoreCase = true) }
            "PROCESSING" -> payoutHistory.filter { it.status.equals("PROCESSING", ignoreCase = true) }
            else -> payoutHistory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Creator & Community Wallet", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "Track earnings, ad shares & transparent withdrawals",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .testTag("wallet_screen"),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Balance Card
            item {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("💰", fontSize = 18.sp)
                                Text(
                                    text = "AVAILABLE TO WITHDRAW",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF38BDF8),
                                    letterSpacing = 1.sp
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = Color(0xFF1E293B)
                            ) {
                                Text(
                                    text = currentCurrency.code,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = CurrencyHelper.format(earnings.availableBalanceUSD, currentCurrency),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        if (currentCurrency != LocaliiiyCurrency.USD) {
                            Text(
                                text = "≈ $${String.format(Locale.US, "%,.2f", earnings.availableBalanceUSD)} USD",
                                fontSize = 12.5.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Summary Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Gross Earned", fontSize = 10.5.sp, color = Color.Gray)
                                Text(
                                    CurrencyHelper.format(earnings.totalGrossEarnedUSD, currentCurrency),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Column {
                                Text("Pending Payouts", fontSize = 10.5.sp, color = Color.Gray)
                                Text(
                                    CurrencyHelper.format(earnings.pendingPayoutUSD, currentCurrency),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFBBF24)
                                )
                            }
                            Column {
                                Text("Lifetime Disbursed", fontSize = 10.5.sp, color = Color.Gray)
                                Text(
                                    CurrencyHelper.format(earnings.lifetimePayoutsUSD, currentCurrency),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4ADE80)
                                )
                            }
                        }
                    }
                }
            }

            // $1,000 Threshold Transparency Card
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isEligibleForWithdrawal) Color(0xFF0D2516) else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.5.dp,
                        if (isEligibleForWithdrawal) Color(0xFF00FF41) else MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(if (isEligibleForWithdrawal) "✅" else "🎯", fontSize = 20.sp)
                                Text(
                                    text = "$1,000 USD Payout Threshold",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = if (isEligibleForWithdrawal) Color(0xFF00FF41) else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (isEligibleForWithdrawal) Color(0xFF00FF41) else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (isEligibleForWithdrawal) "ELIGIBLE ✓" else "${(progressToThreshold * 100).toInt()}% READY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isEligibleForWithdrawal) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { progressToThreshold },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isEligibleForWithdrawal) Color(0xFF00FF41) else MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current: $${String.format(Locale.US, "%,.2f", earnings.availableBalanceUSD)} USD",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isEligibleForWithdrawal) Color(0xFF81C784) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Threshold: $1,000.00 USD",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Transparency Disclosure
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.Shield, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        "Why the $1,000.00 USD Minimum?",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.5.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "To safeguard creators against predatory intermediate bank fees, SWIFT correspondent charges, and currency conversion penalties, Localiiiy pools transfers into zero-fee institutional batches. This guarantees that 100% of your earnings reach your account without intermediary deduction.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Request Payout Button
                        Button(
                            onClick = { showWithdrawDialog = true },
                            enabled = isEligibleForWithdrawal,
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF00FF41),
                                contentColor = Color(0xFF011A05),
                                disabledContainerColor = Color(0xFF263238),
                                disabledContentColor = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("request_wallet_payout_btn")
                        ) {
                            Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEligibleForWithdrawal) "REQUEST WITHDRAWAL (TRANSFER FUNDS)" else "ACCUMULATE $1,000.00 TO WITHDRAW",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }

            // Earnings Breakdown by Stream
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Earnings Breakdown by Stream",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Real-time accruals across video ads, tips, and marketplace services",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            WalletStreamRow(
                                emoji = "📹",
                                title = "In-Stream Video Ads Share",
                                subtitle = "55% creator pool on clips & videos",
                                amountUSD = earnings.inStreamVideoAdUSD,
                                currency = currentCurrency
                            )
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            WalletStreamRow(
                                emoji = "📰",
                                title = "Hyperlocal Sponsored Ads",
                                subtitle = "55% revenue on neighborhood sponsored posts",
                                amountUSD = earnings.feedSponsoredAdUSD,
                                currency = currentCurrency
                            )
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            WalletStreamRow(
                                emoji = "💖",
                                title = "Fan Super Thanks & Micro-Tips",
                                subtitle = "100% creator pass-through (zero commission)",
                                amountUSD = earnings.superThanksTipsUSD,
                                currency = currentCurrency
                            )
                            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                            WalletStreamRow(
                                emoji = "🛍️",
                                title = "Marketplace Sales & Gigs",
                                subtitle = "Goods and local hourly services provided",
                                amountUSD = earnings.marketplaceSalesUSD,
                                currency = currentCurrency
                            )
                        }
                    }
                }
            }

            // Payout Method Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Registered Disbursement Destination", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "${payoutAccount.payoutMethod} • ${payoutAccount.accountHolderName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                payoutAccount.bankName,
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = Color(0xFF142B1A)
                        ) {
                            Text(
                                "VERIFIED",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00FF41),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Detailed History of Past Withdrawals
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Past Withdrawals History",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf("ALL", "COMPLETED", "PROCESSING").forEach { filter ->
                                val isSelected = historyFilter == filter
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { historyFilter = filter }
                                ) {
                                    Text(
                                        text = filter,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredHistory.isEmpty()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No withdrawal records matching filter.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        filteredHistory.forEach { tx ->
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedTransactionForReceipt = tx }
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = if (tx.status.equals("COMPLETED", ignoreCase = true)) Color(0xFF00FF41).copy(alpha = 0.15f)
                                        else Color(0xFFFBBF24).copy(alpha = 0.15f),
                                        modifier = Modifier.size(42.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = if (tx.status.equals("COMPLETED", ignoreCase = true)) Icons.Default.CheckCircle
                                                else Icons.Default.HourglassTop,
                                                contentDescription = null,
                                                tint = if (tx.status.equals("COMPLETED", ignoreCase = true)) Color(0xFF00FF41)
                                                else Color(0xFFFBBF24),
                                                modifier = Modifier.size(22.dp)
                                            )
                                        }
                                    }

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "$${String.format(Locale.US, "%,.2f", tx.amountUSD)} USD",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = if (tx.status.equals("COMPLETED", ignoreCase = true)) Color(0xFF00FF41).copy(alpha = 0.2f)
                                                else Color(0xFFFBBF24).copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = tx.status,
                                                    fontSize = 8.5.sp,
                                                    fontWeight = FontWeight.Black,
                                                    color = if (tx.status.equals("COMPLETED", ignoreCase = true)) Color(0xFF00FF41)
                                                    else Color(0xFFFBBF24),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${tx.payoutMethod} • ${formatDate(tx.timestamp)}",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Ref: ${tx.referenceId}",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "View Receipt",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Withdrawal Request Dialog
    if (showWithdrawDialog) {
        var withdrawAmountUSD by remember { mutableStateOf(earnings.availableBalanceUSD) }
        var isSubmitting by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { if (!isSubmitting) showWithdrawDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("💸", fontSize = 22.sp)
                    Text("Confirm Payout Request", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "You are requesting a withdrawal of earnings to your verified payout account.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Disbursement Amount", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                            Text(
                                "$${String.format(Locale.US, "%,.2f", withdrawAmountUSD)} USD",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "≈ ${CurrencyHelper.format(withdrawAmountUSD, currentCurrency)} (${currentCurrency.code})",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Destination Account", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.outline)
                            Text(payoutAccount.payoutMethod, fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                            Text(payoutAccount.accountIdentifier, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Text(
                        "Batch SWIFT/SEPA transfers typically settle within 1–2 business days. Zero fees deducted.",
                        fontSize = 10.5.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isSubmitting = true
                        val success = onRequestPayout(withdrawAmountUSD)
                        isSubmitting = false
                        showWithdrawDialog = false
                        if (success) {
                            Toast.makeText(
                                context,
                                "Withdrawal request of $${String.format(Locale.US, "%,.2f", withdrawAmountUSD)} USD submitted!",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Withdrawal failed. Balance must meet $1,000 USD threshold.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00FF41), contentColor = Color.Black)
                ) {
                    Text("Confirm & Disburse", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Receipt Detail Modal
    selectedTransactionForReceipt?.let { tx ->
        AlertDialog(
            onDismissRequest = { selectedTransactionForReceipt = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🧾", fontSize = 22.sp)
                    Text("Withdrawal Receipt", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (tx.status.equals("COMPLETED", ignoreCase = true)) Color(0xFF0D2516) else Color(0xFF2E1C05),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$${String.format(Locale.US, "%,.2f", tx.amountUSD)} USD",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                                color = if (tx.status.equals("COMPLETED", ignoreCase = true)) Color(0xFF00FF41) else Color(0xFFFBBF24)
                            )
                            Text(
                                text = "Status: ${tx.status}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    ReceiptRow("Transaction ID", tx.id)
                    ReceiptRow("Date & Time", formatDate(tx.timestamp))
                    ReceiptRow("Payout Method", tx.payoutMethod)
                    ReceiptRow("Target Currency", tx.targetCurrencyCode)
                    ReceiptRow("Disbursed Amount", "${tx.targetCurrencyCode} ${String.format(Locale.US, "%,.2f", tx.amountInLocalCurrency)}")
                    ReceiptRow("Settlement Type", "Zero-Fee Batch Rail (100% Creator)")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Reference ID", tx.referenceId)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Reference ID copied!", Toast.LENGTH_SHORT).show()
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Banking Reference ID", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.outline)
                            Text(tx.referenceId, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(16.dp))
                    }
                }
            },
            confirmButton = {
                Button(onClick = { selectedTransactionForReceipt = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun WalletStreamRow(
    emoji: String,
    title: String,
    subtitle: String,
    amountUSD: Double,
    currency: LocaliiiyCurrency
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(emoji, fontSize = 20.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(subtitle, fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                CurrencyHelper.format(amountUSD, currency),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
            if (currency != LocaliiiyCurrency.USD) {
                Text(
                    "$$amountUSD",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.outline)
        Text(value, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
    }
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
