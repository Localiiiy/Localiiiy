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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.legal.LegalPolicyRepository
import com.example.data.legal.LegalSubSection
import com.example.ui.components.EnergeticPulseBadge
import com.example.ui.components.HashAnchorItem
import com.example.ui.components.HashLinkAnchorNavBar
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsAndConditionsScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var selectedAnchor by remember { mutableStateOf("#overview") }
    var searchQuery by remember { mutableStateOf("") }
    val allClauses = remember { LegalPolicyRepository.clauses }
    var expandedIds by remember { mutableStateOf(allClauses.map { it.id }.toSet()) }
    var copiedBanner by remember { mutableStateOf<String?>(null) }

    val filteredClauses = remember(searchQuery, allClauses) {
        if (searchQuery.isBlank()) {
            allClauses
        } else {
            allClauses.filter { clause ->
                clause.title.contains(searchQuery, ignoreCase = true) ||
                        clause.plainSummary.contains(searchQuery, ignoreCase = true) ||
                        clause.legalText.contains(searchQuery, ignoreCase = true) ||
                        clause.clauseNumber.contains(searchQuery, ignoreCase = true) ||
                        clause.subSections.any {
                            it.subTitle.contains(searchQuery, ignoreCase = true) ||
                                    it.content.contains(searchQuery, ignoreCase = true)
                        }
            }
        }
    }

    val anchors = remember(filteredClauses) {
        buildList {
            add(HashAnchorItem(id = "#overview", label = "Overview (All 36)", targetIndex = 0))
            filteredClauses.forEachIndexed { idx, clause ->
                val shortNum = clause.clauseNumber.replace("Section ", "Sec ")
                add(
                    HashAnchorItem(
                        id = clause.anchorId,
                        label = "$shortNum ${clause.category.iconEmoji}",
                        targetIndex = idx + 1
                    )
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Column {
                            Text("Omnibus Terms & Protocols", fontWeight = FontWeight.Bold, fontSize = 16.5.sp)
                            Text(
                                text = "18+ Radar Lock -> LCC SHA-256 Hash -> Developer Safe-Harbor -> 15 Civic Tools -> Oblivion",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        EnergeticPulseBadge(text = "36 ARTICLES", color = Color(0xFF00FF66))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val fullText = LegalPolicyRepository.buildFullOmnibusDocumentText()
                            clipboard.setPrimaryClip(ClipData.newPlainText("Localiiiy Omnibus Terms", fullText))
                            copiedBanner = "Copied all 36 Master Articles & Subsections to clipboard! 📋"
                        },
                        modifier = Modifier.testTag("terms_copy_all_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy All Terms",
                            tint = LocaliiiyPrimaryTeal
                        )
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

            if (copiedBanner != null) {
                Surface(
                    color = LocaliiiyAccentMint.copy(alpha = 0.16f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = copiedBanner!!,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocaliiiyAccentMint
                        )
                        TextButton(onClick = { copiedBanner = null }) {
                            Text("Dismiss", fontSize = 11.sp)
                        }
                    }
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .testTag("terms_lazy_column"),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 0: Overview + Search + Expand/Collapse Controls
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("section_overview")
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Localiiiy Master Omnibus Terms, NDA, Copyright & Dual-Reach Protocol",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Version: ${LegalPolicyRepository.CURRENT_POLICY_VERSION} • Updated: ${LegalPolicyRepository.LAST_UPDATED_DATE}",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Welcome to Localiiiy's complete legal charter covering every screen and lifecycle stage from Account Registration (2-Way Email & Mobile OTP), Home Community Anchor, 20 Interest Constellations, and 100% Free Master Ghost Mode to the 0 km to Earth (20,000 km) Dual-Reach Algorithm, Pulse Feed, 24h Stories, 9:16 Clips, Space Radar, 4K Studio, Localiiiy Copyright (All Rights Reserved vs. LCC, SHA-256/pHash, 7-Day Counter-Notice, 3-Strike Rule), Safe-Haven 5% Digital Escrow Marketplace, Creator Monetization ($1,000 USD or local equivalent minimum withdrawal), Binding Anti-Cyberstalking NDA, ISO/IEC 27037 /safety_logs/ Vault, and Complete Account & Data Oblivion.",
                                fontSize = 12.5.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 18.5.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = {
                                    Text("Filter articles (e.g. 0 km, Copyright LCC, Escrow, $1,000, NDA, Oblivion)...", fontSize = 12.sp)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = "Search Terms", modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("terms_search_field")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Showing ${filteredClauses.size} Master Articles",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = {
                                        expandedIds = if (expandedIds.size == filteredClauses.size) {
                                            emptySet()
                                        } else {
                                            filteredClauses.map { it.id }.toSet()
                                        }
                                    }
                                ) {
                                    Text(
                                        text = if (expandedIds.size == filteredClauses.size) "Collapse Subsections" else "Expand All Subsections",
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = LocaliiiyPrimaryTeal
                                    )
                                }
                            }
                        }
                    }
                }

                // All 24 Master Sections from LegalPolicyRepository
                itemsIndexed(filteredClauses, key = { _, clause -> clause.id }) { _, clause ->
                    val isExpanded = expandedIds.contains(clause.id)
                    TermsSectionCard(
                        anchorId = clause.anchorId,
                        clauseNumber = clause.clauseNumber,
                        categoryBadge = "${clause.category.iconEmoji} ${clause.category.title}",
                        title = "${clause.clauseNumber}: ${clause.title}",
                        plainSummary = clause.plainSummary,
                        content = clause.legalText,
                        subSections = clause.subSections,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedIds = if (isExpanded) expandedIds - clause.id else expandedIds + clause.id
                        }
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
    content: String,
    clauseNumber: String = "",
    categoryBadge: String = "",
    plainSummary: String = "",
    subSections: List<LegalSubSection> = emptyList(),
    isExpanded: Boolean = true,
    onToggleExpand: () -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("terms_section_${anchorId.removePrefix("#")}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() }
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (categoryBadge.isNotBlank()) {
                        Text(
                            text = categoryBadge,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = LocaliiiyPrimaryTeal
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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
                    IconButton(onClick = onToggleExpand, modifier = Modifier.size(26.dp)) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Collapse" else "Expand"
                        )
                    }
                }
            }

            if (plainSummary.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
                    border = BorderStroke(0.8.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "💡 EXECUTIVE SUMMARY (TL;DR)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Black,
                            color = LocaliiiyPrimaryTeal
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = plainSummary,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 16.5.sp
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = content,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 17.5.sp,
                            modifier = Modifier.padding(10.dp)
                        )
                    }

                    if (subSections.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "SUBSECTIONS & TECHNICAL PROTOCOLS (${subSections.size})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = LocaliiiyPrimaryTeal
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            subSections.forEach { sub ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f),
                                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = "§ ${sub.subNumber} — ${sub.subTitle}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = sub.content,
                                            fontSize = 11.5.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            lineHeight = 16.sp
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
}
