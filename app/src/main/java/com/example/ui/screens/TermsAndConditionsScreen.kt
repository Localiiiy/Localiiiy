package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold) },
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
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Localiiiy Terms of Service",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Last updated: October 2026",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                TermsSection(
                    title = "1. Acceptance of Terms",
                    content = "By creating an account on Localiiiy, you agree to these Terms. We reserve the right to suspend accounts that violate our community standards or these Terms."
                )
            }

            item {
                TermsSection(
                    title = "2. Marketplace & Digital Escrow",
                    content = "The Localiiiy Marketplace operates using a secure digital escrow system. When purchasing an item:\n\n• A 5% platform commission fee is automatically deducted from the total transaction to cover processing and escrow services.\n• Funds are securely held in escrow and only released to the seller's Creator Wallet upon physical inspection and buyer confirmation.\n• Localiiiy is not liable for disputes arising from misrepresented items once the physical handover is confirmed."
                )
            }

            item {
                TermsSection(
                    title = "3. Subscriptions & Monetization",
                    content = "• Localiiiy Premium is billed on a recurring monthly basis. Cancellations take effect at the end of the current billing cycle.\n• Boost & Sponsor campaigns are non-refundable once launched.\n• Ad Revenue from Rewarded Video Ads is distributed according to platform guidelines and contributes to Daily Check-in and Referral rewards."
                )
            }

            item {
                TermsSection(
                    title = "4. Creator Wallet & Payouts",
                    content = "• The minimum withdrawal threshold is $1,000 USD.\n• You must provide accurate payout details. We are not responsible for funds sent to incorrectly provided accounts."
                )
            }

            item {
                TermsSection(
                    title = "5. Privacy & Ghost Mode",
                    content = "While Ghost Mode (available via Localiiiy Premium) conceals your identity from other users on the radar, your data is still subject to our Privacy Policy for internal analytics and safety purposes."
                )
            }
        }
    }
}

@Composable
fun TermsSection(title: String, content: String) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 20.sp
        )
    }
}
