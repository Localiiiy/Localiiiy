package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CreatorEarningsSummary
import com.example.data.CreatorPayoutAccount
import com.example.data.PayoutTransaction
import com.example.data.PlatformAdRevenueMetrics
import com.example.util.CurrencyHelper
import com.example.util.LocaliiiyCurrency
import com.example.util.LocaliiiyLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatorMonetizationHubSheet(
    earnings: CreatorEarningsSummary,
    payoutAccount: CreatorPayoutAccount,
    payoutHistory: List<PayoutTransaction>,
    platformMetrics: PlatformAdRevenueMetrics,
    currentCurrency: LocaliiiyCurrency,
    currentLanguage: LocaliiiyLanguage,
    onOpenCurrencyLanguageSelector: () -> Unit,
    onRequestPayout: (Double) -> Boolean,
    onUpdatePayoutAccount: (CreatorPayoutAccount) -> Unit,
    onOpenBoostAdDialog: () -> Unit,
    onDismissRequest: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
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
                        text = "Monetization Hub",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }
            
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Available Balance", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = CurrencyHelper.format(earnings.availableBalanceUSD, currentCurrency),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onRequestPayout(earnings.availableBalanceUSD) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Request Payout")
                        }
                    }
                }
            }
            
            item {
                Text("Revenue Streams", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
                }
            }
            
            item {
                Text("Payout Account", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = payoutAccount.payoutMethod, fontWeight = FontWeight.Bold)
                            Text(text = payoutAccount.accountIdentifier, fontSize = 12.sp)
                        }
                        TextButton(onClick = { /* Edit */ }) {
                            Text("Edit")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RevenueStreamCard(title: String, amount: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = amount, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
