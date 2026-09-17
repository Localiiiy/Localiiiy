package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EnergeticPulseBadge
import com.example.ui.components.HashAnchorItem
import com.example.ui.components.HashLinkAnchorNavBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onNavigateBack: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedAnchor by remember { mutableStateOf("#overview") }

    val anchors = remember {
        listOf(
            HashAnchorItem(id = "#overview", label = "Overview", targetIndex = 0),
            HashAnchorItem(id = "#acceptance", label = "1. Acceptance", targetIndex = 1),
            HashAnchorItem(id = "#escrow", label = "2. Escrow & Market", targetIndex = 2),
            HashAnchorItem(id = "#monetization", label = "3. Monetization", targetIndex = 3),
            HashAnchorItem(id = "#wallet", label = "4. Payouts", targetIndex = 4),
            HashAnchorItem(id = "#privacy", label = "5. Ghost Privacy", targetIndex = 5)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("Terms & Protocols", fontWeight = FontWeight.Bold)
                        EnergeticPulseBadge(text = "VERIFIED v2.4", color = Color(0xFF00FF66))
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Hash-Link Smooth Scrolling Anchor Bar
            HashLinkAnchorNavBar(
                anchors = anchors,
                selectedAnchorId = selectedAnchor,
                onAnchorClick = { anchor ->
                    selectedAnchor = anchor.id
                    coroutineScope.launch {
                        listState.animateScrollToItem(index = anchor.targetIndex)
                    }
                },
                modifier = Modifier.background(MaterialTheme.colorScheme.surface)
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("terms_lazy_column"),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 0: Overview
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth().testTag("section_overview")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Localiiiy Community & Protocol Terms",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Last updated: October 2026 • Smooth navigation enabled",
                                fontSize = 11.5.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Welcome to Localiiiy. Tap any hash-link anchor above to smoothly navigate between governance sections. All platform actions are protected by cryptographic integrity checks.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                // Section 1: Acceptance
                item {
                    TermsSectionCard(
                        anchorId = "#acceptance",
                        title = "1. Acceptance of Terms",
                        content = "By creating an account on Localiiiy, you agree to these Terms. We reserve the right to suspend accounts that violate our community standards or these Terms."
                    )
                }

                // Section 2: Marketplace & Digital Escrow
                item {
                    TermsSectionCard(
                        anchorId = "#escrow",
                        title = "2. Marketplace & Digital Escrow",
                        content = "The Localiiiy Marketplace operates using a secure digital escrow system. When purchasing an item:\n\n• A 5% platform commission fee is automatically deducted from the total transaction to cover processing and escrow services.\n• Funds are securely held in escrow and only released to the seller's Creator Wallet upon physical inspection and buyer confirmation.\n• Localiiiy is not liable for disputes arising from misrepresented items once the physical handover is confirmed."
                    )
                }

                // Section 3: Subscriptions & Monetization
                item {
                    TermsSectionCard(
                        anchorId = "#monetization",
                        title = "3. Subscriptions & Monetization",
                        content = "• Localiiiy Premium is billed on a recurring monthly basis. Cancellations take effect at the end of the current billing cycle.\n• Boost & Sponsor campaigns are non-refundable once launched.\n• Ad Revenue from Rewarded Video Ads is distributed according to platform guidelines and contributes to Daily Check-in and Referral rewards."
                    )
                }

                // Section 4: Creator Wallet & Payouts
                item {
                    TermsSectionCard(
                        anchorId = "#wallet",
                        title = "4. Creator Wallet & Payouts",
                        content = "• The minimum withdrawal threshold is $1,000 USD.\n• You must provide accurate payout details. We are not responsible for funds sent to incorrectly provided accounts."
                    )
                }

                // Section 5: Privacy & Ghost Mode
                item {
                    TermsSectionCard(
                        anchorId = "#privacy",
                        title = "5. Privacy & Ghost Mode",
                        content = "While Ghost Mode (available via Localiiiy Premium) conceals your identity from other users on the radar, your data is still subject to our Privacy Policy for internal analytics and safety purposes."
                    )
                }
            }
        }
    }
}

@Composable
fun TermsSectionCard(
    anchorId: String,
    title: String,
    content: String
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth().testTag("terms_section_${anchorId.removePrefix("#")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = anchorId,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}
