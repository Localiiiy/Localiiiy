package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.legal.LegalCategory
import com.example.data.legal.LegalClause
import com.example.data.legal.LegalConsentRecord
import com.example.data.legal.LegalPolicyRepository
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalAgreementScreen(
    currentUsername: String = "alex_creative",
    consentRecord: LegalConsentRecord = LegalPolicyRepository.defaultConsentRecord,
    onBackClick: () -> Unit,
    onAcceptAgreement: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LegalCategory.ALL) }
    var expandedClauseIds by remember {
        mutableStateOf(
            setOf(
                "clause_1_location_sovereignty",
                "clause_5_intellectual_property",
                "clause_10_nda_confidentiality_cybercrime",
                "clause_11_post_making_dual_reach_policy"
            )
        )
    }
    var showCopySnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var isAccepted by remember { mutableStateOf(consentRecord.isConsentActive) }

    val filteredClauses = remember(searchQuery, selectedCategory) {
        LegalPolicyRepository.clauses.filter { clause ->
            val matchesCategory = (selectedCategory == LegalCategory.ALL || clause.category == selectedCategory)
            val matchesSearch = searchQuery.isBlank() ||
                    clause.title.contains(searchQuery, ignoreCase = true) ||
                    clause.plainSummary.contains(searchQuery, ignoreCase = true) ||
                    clause.legalText.contains(searchQuery, ignoreCase = true) ||
                    clause.clauseNumber.contains(searchQuery, ignoreCase = true) ||
                    clause.anchorId.contains(searchQuery, ignoreCase = true) ||
                    clause.subSections.any { sub ->
                        sub.subNumber.contains(searchQuery, ignoreCase = true) ||
                                sub.subTitle.contains(searchQuery, ignoreCase = true) ||
                                sub.content.contains(searchQuery, ignoreCase = true)
                    }
            matchesCategory && matchesSearch
        }
    }

    fun copyToClipboard(text: String, label: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        snackbarMessage = "Copied to clipboard 📋"
        showCopySnackbar = true
    }

    fun copyEntirePolicy() {
        val fullDoc = LegalPolicyRepository.buildFullOmnibusDocumentText(
            currentUsername = currentUsername,
            isAccepted = isAccepted
        )
        copyToClipboard(fullDoc, "Localiiiy Master Omnibus Legal Charter")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Omnibus Legal Charter, NDA & Privacy",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.5.sp
                            )
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isAccepted) LocaliiiyAccentMint else MaterialTheme.colorScheme.tertiary)
                            )
                            Text(
                                text = "${LegalPolicyRepository.CURRENT_POLICY_VERSION} • 36 Master Articles",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("legal_screen_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { copyEntirePolicy() },
                        modifier = Modifier.testTag("legal_copy_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy Full Omnibus Agreement",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = if (isAccepted) LocaliiiyAccentMint.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isAccepted) Icons.Default.VerifiedUser else Icons.Outlined.Gavel,
                                    contentDescription = null,
                                    tint = if (isAccepted) LocaliiiyAccentMint else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAccepted) "Omnibus Charter & NDA Signed" else "Action Required: Review & Acknowledge",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isAccepted) "Signed for @$currentUsername • 36 Articles Verified in Room DB" else "Tap below to certify your legal understanding of all 36 Articles",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Button(
                            onClick = {
                                isAccepted = true
                                onAcceptAgreement()
                                snackbarMessage = "Omnibus Legal Agreement, NDA & Privacy Policy Accepted! ⚖️"
                                showCopySnackbar = true
                            },
                            shape = RoundedCornerShape(100.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAccepted) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.testTag("legal_accept_confirm_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAccepted) "Acknowledged" else "I Agree",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("legal_agreement_screen")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Banner
            item {
                Spacer(modifier = Modifier.height(4.dp))
                LegalHeroHeaderCard(
                    currentUsername = currentUsername,
                    consentRecord = consentRecord,
                    isAccepted = isAccepted
                )
            }

            // Interactive Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search 0km–Earth algorithm, LCC copyright, NDA, 2-Way OTP, $1k payout...",
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear search",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("legal_search_field")
                )
            }

            // Category Filter Chips
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(LegalCategory.values()) { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = {
                                Text(
                                    text = "${category.iconEmoji} ${category.title}",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = LocaliiiyPrimaryTeal.copy(alpha = 0.2f),
                                selectedLabelColor = LocaliiiyPrimaryTeal
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = if (isSelected) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant
                            )
                        )
                    }
                }
            }

            // Quick High-Level Pillars (Engaging Visual Cards for User's Core Points)
            if (searchQuery.isBlank() && selectedCategory == LegalCategory.ALL) {
                item {
                    Text(
                        text = "CORE ARCHITECTURE & SOVEREIGNTY PILLARS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LegalPillarMiniCard(
                            emoji = "📍",
                            title = "100% Free Ghost & 1–500km Mask",
                            description = "Broadcast radar only when you choose. Master Ghost Mode is 100% free with 1km–500km GPS obfuscation.",
                            modifier = Modifier.weight(1f)
                        )
                        LegalPillarMiniCard(
                            emoji = "🌍",
                            title = "0 km to Earth Dual-Reach",
                            description = "Every post/clip starts at 0 km (Street) and scales organically through City to Earth (20,000 km).",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LegalPillarMiniCard(
                            emoji = "©️",
                            title = "Localiiiy Copyright & LCC",
                            description = "All Rights Reserved vs. LCC, SHA-256/pHash detection, 7-day counter-notice, 3-Strike rule & 100% revenue rerouting.",
                            modifier = Modifier.weight(1f)
                        )
                        LegalPillarMiniCard(
                            emoji = "💰",
                            title = "55% Ad Split & $1k Payout",
                            description = "55% ad share, 100% fan tips (0% fee), dynamic revenue counters & $1,000 USD (or local equivalent) min withdrawal.",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LegalPillarMiniCard(
                        emoji = "🛍️",
                        title = "Safe-Haven 5% Escrow (0% Pro) & P2P Market Waiver",
                        description = "5% Digital Escrow (0% for Premium Pro Titan) released upon physical inspection, or direct P2P bulletin with zero developer financial liability. Meet safely in public!",
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    LegalPillarMiniCard(
                        emoji = "⚖️",
                        title = "Binding Anti-Cyberstalking NDA, /safety_logs/ & Data Oblivion",
                        description = "2-Way OTP verified registration, ISO/IEC 27037 SHA-256 forensic logs shared with law enforcement upon criminal stalking, plus 1-tap Complete Account & Data Oblivion.",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Clauses Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "MASTER ARTICLES & SUBSECTIONS (${filteredClauses.size} OF ${LegalPolicyRepository.clauses.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TextButton(
                        onClick = {
                            expandedClauseIds = if (expandedClauseIds.size == filteredClauses.size) {
                                emptySet()
                            } else {
                                filteredClauses.map { it.id }.toSet()
                            }
                        },
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.height(24.dp)
                    ) {
                        Text(
                            text = if (expandedClauseIds.size == filteredClauses.size) "Collapse All" else "Expand All (${filteredClauses.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Clauses List
            items(filteredClauses, key = { it.id }) { clause ->
                val isExpanded = expandedClauseIds.contains(clause.id)
                LegalClauseCard(
                    clause = clause,
                    isExpanded = isExpanded,
                    onToggleExpand = {
                        expandedClauseIds = if (isExpanded) {
                            expandedClauseIds - clause.id
                        } else {
                            expandedClauseIds + clause.id
                        }
                    },
                    onCopyClause = {
                        val subText = if (clause.subSections.isNotEmpty()) {
                            "\n\nSubsections:\n" + clause.subSections.joinToString("\n") { "• [${it.subNumber}] ${it.subTitle}: ${it.content}" }
                        } else ""
                        copyToClipboard(
                            "${clause.clauseNumber}: ${clause.title}\n\nSummary:\n${clause.plainSummary}\n\nLegal Terms:\n${clause.legalText}$subText",
                            clause.title
                        )
                    }
                )
            }

            // Bottom Legal Footer & Certificate Proof
            item {
                LegalDatabaseCertificateCard(
                    currentUsername = currentUsername,
                    consentRecord = consentRecord,
                    isAccepted = isAccepted
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showCopySnackbar) {
        Snackbar(
            action = {
                TextButton(onClick = { showCopySnackbar = false }) {
                    Text("OK", color = LocaliiiyAccentMint, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = LocaliiiyDeepNavy,
            contentColor = Color.White,
            modifier = Modifier
                .padding(16.dp)
                .testTag("legal_snackbar")
        ) {
            Text(text = snackbarMessage, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LegalHeroHeaderCard(
    currentUsername: String,
    consentRecord: LegalConsentRecord,
    isAccepted: Boolean
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = LocaliiiyPrimaryTeal.copy(alpha = 0.18f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "⚖️", fontSize = 12.sp)
                        Text(
                            text = "OMNIBUS CHARTER, NDA, COPYRIGHT & PRIVACY",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            color = LocaliiiyPrimaryTeal
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(100.dp),
                    color = if (isAccepted) LocaliiiyAccentMint.copy(alpha = 0.2f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = if (isAccepted) "ACTIVE & SIGNED" else "PENDING SIGNATURE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isAccepted) LocaliiiyAccentMint else MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Complete Registration-to-Oblivion Platform Charter",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "This Master Omnibus Instrument governs every module of Localiiiy: 2-Way OTP Registration, Pulse, 24h Stories, 9:16 Clips, 0 km to Earth (20,000 km) Dual-Reach Algorithm, Space Radar, 1–500 km GPS Obfuscation, 100% Free Ghost Mode, 4K Studio, Localiiiy Copyright (All Rights Reserved vs. LCC, SHA-256/pHash, 7-Day Counter-Notice, 3-Strike Rule), Safe-Haven 5% Escrow Marketplace, 55% Ad Share & $1,000 USD Equivalent Payouts, Binding Anti-Cyberstalking NDA, ISO/IEC 27037 /safety_logs/, and Complete Account & Data Oblivion.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 16.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LegalPillarMiniCard(
    emoji: String,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = emoji, fontSize = 16.sp)
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 11.sp,
                    lineHeight = 14.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun LegalClauseCard(
    clause: LegalClause,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCopyClause: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (clause.isHighlighted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            width = if (clause.isHighlighted) 1.2.dp else 1.dp,
            color = if (clause.isHighlighted) LocaliiiyPrimaryTeal.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("clause_card_${clause.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header Row: Clause Number & Title + Expand Icon
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleExpand),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            text = "${clause.category.iconEmoji} ${clause.clauseNumber}",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = clause.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.5.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse" else "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Plain Language TL;DR Box
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Text(text = "💡", fontSize = 14.sp)
                    Column {
                        Text(
                            text = "PLAIN LANGUAGE SUMMARY (TL;DR)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 9.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = clause.plainSummary,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.5.sp,
                                lineHeight = 15.5.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Expandable Formal Legal Text + Subsections
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    Text(
                        text = "FORMAL CONTRACTUAL CLAUSE & ARTICLES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = clause.legalText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                fontFamily = FontFamily.Monospace
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    if (clause.subSections.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "DETAILED SUBSECTIONS & PROTOCOL SPECIFICATIONS (${clause.subSections.size})",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp,
                                letterSpacing = 0.6.sp
                            ),
                            color = LocaliiiyPrimaryTeal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            clause.subSections.forEach { sub ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(0.8.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "§ ${sub.subNumber} — ${sub.subTitle}",
                                            style = MaterialTheme.typography.labelMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.5.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = sub.content,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 11.sp,
                                                lineHeight = 15.5.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onCopyClause,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Copy Article & Subsections",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalDatabaseCertificateCard(
    currentUsername: String,
    consentRecord: LegalConsentRecord,
    isAccepted: Boolean
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = LocaliiiyPrimaryTeal,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Local Room SQLite Legal Certificate",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "• Omnibus Version: ${LegalPolicyRepository.CURRENT_POLICY_VERSION}\n" +
                        "• Articles & Clauses: ${LegalPolicyRepository.clauses.size} Master Articles (Registration -> Oblivion)\n" +
                        "• Signatory Account: @$currentUsername\n" +
                        "• Verification Timestamp: ${consentRecord.getFormattedDate()}\n" +
                        "• Cryptographic Proof: ${consentRecord.consentProofHash.take(36)}...\n" +
                        "• Local Room DB Record: Encrypted & Verified (ID: 1)\n" +
                        "• Jurisdiction: Universal Civil Privacy & Individual Sovereignty",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
