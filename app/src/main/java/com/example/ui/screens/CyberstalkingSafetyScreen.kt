package com.example.ui.screens
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CyberstalkingEvidenceVault
import com.example.data.CyberstalkingIncidentEntity
import com.example.data.OtherUserEntity
import com.example.util.SafetyLogManager
import com.example.util.SafetyLogRecord
import java.util.UUID

enum class CyberstalkingScreenTab(val title: String, val iconEmoji: String) {
    REPORT("Report & Ban", "⚖️"),
    DIRECTORY("Evidence Vault", "🔒"),
    SAFETY_LOGS("Safety Logs", "📁"),
    LEGAL_GUIDE("Universal Policy", "🛡️")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberstalkingSafetyScreen(
    currentUsername: String,
    incidents: List<CyberstalkingIncidentEntity>,
    otherUsers: List<OtherUserEntity>,
    onRecordIncident: (CyberstalkingIncidentEntity) -> Unit,
    prefilledTargetUsername: String? = null,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val vault = remember { CyberstalkingEvidenceVault(context) }
    var selectedTab by remember { mutableStateOf(CyberstalkingScreenTab.REPORT) }

    // Report form state
    var accusedUsername by remember { mutableStateOf(prefilledTargetUsername ?: "") }
    var offenseCategory by remember { mutableStateOf("PERSISTENT_ELECTRONIC_CONTACT") }
    var offenseLegalTitle by remember { mutableStateOf("Persistent Electronic Contact & Harassment (Anti-Stalking)") }
    var incidentDescription by remember { mutableStateOf("") }
    var evidencePayload by remember { mutableStateOf("") }
    var isAffirmationChecked by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdIncidentId by remember { mutableStateOf<String?>(null) }

    val offenseOptions = listOf(
        Pair("PERSISTENT_ELECTRONIC_CONTACT", "Persistent Electronic Contact & Harassment (Anti-Stalking)"),
        Pair("UNAUTHORIZED_LOCATION_MONITORING", "Unauthorized Radar / Geolocation Surveillance"),
        Pair("THROWAWAY_EVASION", "Evasion of User Block via Secondary / Throwaway Accounts"),
        Pair("DIGITAL_PRIVACY_VIOLATION", "Violation of Bodily & Digital Privacy"),
        Pair("INTIMIDATION_OBSCENE_TRANSMISSION", "Intimidation, Threats & Obscene Electronic Transmission"),
        Pair("CROSS_BORDER_CYBERSTALKING", "Cross-Border Cyberstalking & Coercive Control")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Zero-Tolerance Anti-Stalking",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.errorContainer
                            ) {
                                Text(
                                    text = "ZERO TOLERANCE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Permanent Block & Immutable Records Vault",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("cyberstalking_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Selector Row
            PrimaryTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                CyberstalkingScreenTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(tab.iconEmoji, fontSize = 14.sp)
                                Text(
                                    text = if (tab == CyberstalkingScreenTab.DIRECTORY && incidents.isNotEmpty()) {
                                        "${tab.title} (${incidents.size})"
                                    } else if (tab == CyberstalkingScreenTab.SAFETY_LOGS) {
                                        val count = SafetyLogManager.listSafetyLogs(context).size
                                        if (count > 0) "${tab.title} ($count)" else tab.title
                                    } else tab.title,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    )
                }
            }

            when (selectedTab) {
                CyberstalkingScreenTab.REPORT -> {
                    ReportAndBanTab(
                        currentUsername = currentUsername,
                        accusedUsername = accusedUsername,
                        onAccusedUsernameChange = { accusedUsername = it },
                        otherUsers = otherUsers,
                        offenseOptions = offenseOptions,
                        selectedOffense = offenseCategory,
                        onSelectOffense = { cat, title ->
                            offenseCategory = cat
                            offenseLegalTitle = title
                        },
                        incidentDescription = incidentDescription,
                        onIncidentDescriptionChange = { incidentDescription = it },
                        evidencePayload = evidencePayload,
                        onEvidencePayloadChange = { evidencePayload = it },
                        isAffirmationChecked = isAffirmationChecked,
                        onAffirmationChange = { isAffirmationChecked = it },
                        onSubmit = {
                            if (accusedUsername.isBlank()) {
                                Toast.makeText(context, "Please enter or select the accused username", Toast.LENGTH_SHORT).show()
                                return@ReportAndBanTab
                            }
                            if (incidentDescription.isBlank()) {
                                Toast.makeText(context, "Please describe the cyberstalking incident", Toast.LENGTH_SHORT).show()
                                return@ReportAndBanTab
                            }
                            if (!isAffirmationChecked) {
                                Toast.makeText(context, "Please confirm the legal truthfulness declaration", Toast.LENGTH_SHORT).show()
                                return@ReportAndBanTab
                            }

                            val incidentId = UUID.randomUUID().toString()
                            val now = System.currentTimeMillis()
                            val cleanEvidence = if (evidencePayload.isNotBlank()) evidencePayload.trim()
                            else "Description: ${incidentDescription.trim()}\nTimestamp: $now\nTarget: @$accusedUsername\nReporter: @$currentUsername"

                            val rawSealString = "$incidentId|$now|$currentUsername|$accusedUsername|$offenseCategory|$cleanEvidence"
                            val sha256 = CyberstalkingIncidentEntity.calculateSha256(rawSealString)
                            val deviceModel = "${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL} (Android OS ${android.os.Build.VERSION.RELEASE})"

                            val certText = CyberstalkingIncidentEntity.generateStatutoryEvidenceCertificate(
                                incidentId = incidentId,
                                timestamp = now,
                                complainant = currentUsername,
                                accused = accusedUsername.trim(),
                                offenseTitle = offenseLegalTitle,
                                evidencePayload = cleanEvidence,
                                sha256Hash = sha256,
                                deviceInfo = deviceModel
                            )

                            val matchedUser = otherUsers.find { it.username.equals(accusedUsername.trim(), ignoreCase = true) }
                            val newIncident = CyberstalkingIncidentEntity(
                                id = incidentId,
                                timestamp = now,
                                complainantUsername = currentUsername,
                                accusedUsername = accusedUsername.trim(),
                                accusedDisplayName = matchedUser?.fullName ?: "@${accusedUsername.trim()}",
                                accusedAvatarUrl = matchedUser?.avatarUrl ?: "",
                                offenseCategory = offenseCategory,
                                offenseLegalTitle = offenseLegalTitle,
                                incidentDescription = incidentDescription.trim(),
                                rawEvidencePayload = cleanEvidence,
                                digitalSignatureSha256 = sha256,
                                statutoryEvidenceCertificateText = certText,
                                isPermanentBlocked = true,
                                deviceHardwareFingerprint = deviceModel,
                                status = "LOCKED_IMMUTABLE"
                            )

                            // 1. Record in immutable Room database
                            onRecordIncident(newIncident)
                            // 2. Write immutable file to app directory vault
                            vault.writeImmutableRecordToFile(newIncident)

                            createdIncidentId = incidentId
                            showSuccessDialog = true

                            // Reset inputs
                            accusedUsername = ""
                            incidentDescription = ""
                            evidencePayload = ""
                            isAffirmationChecked = false
                        }
                    )
                }

                CyberstalkingScreenTab.DIRECTORY -> {
                    ImmutableVaultDirectoryTab(
                        incidents = incidents,
                        onSwitchToReportTab = { selectedTab = CyberstalkingScreenTab.REPORT }
                    )
                }

                CyberstalkingScreenTab.SAFETY_LOGS -> {
                    SafetyLogsDirectoryTab()
                }

                CyberstalkingScreenTab.LEGAL_GUIDE -> {
                    LegalGuidanceAndHelplineTab()
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🔒", fontSize = 24.sp)
                    Text("Offender Permanently Banned & Evidence Locked", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "1. ZERO TOLERANCE BAN: The accused account is permanently blocked from contacting you, viewing your profile, or appearing on your radar.",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "2. IMMUTABLE DIRECTORY: The incident log has been cryptographically signed with SHA-256 and locked in the on-device vault. It cannot be edited or deleted.",
                        fontSize = 13.sp
                    )
                    Text(
                        text = "3. STATUTORY EVIDENCE CERTIFICATE: An immutable electronic evidence certificate conforming to international cyber discovery standards is generated for law enforcement, police, and judicial proceedings.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        selectedTab = CyberstalkingScreenTab.DIRECTORY
                    },
                    modifier = Modifier.testTag("dialog_view_vault_button")
                ) {
                    Text("View Immutable Vault")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSuccessDialog = false }) {
                    Text("Done")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: Report & Permanent Ban
// -------------------------------------------------------------
@Composable
private fun ReportAndBanTab(
    currentUsername: String,
    accusedUsername: String,
    onAccusedUsernameChange: (String) -> Unit,
    otherUsers: List<OtherUserEntity>,
    offenseOptions: List<Pair<String, String>>,
    selectedOffense: String,
    onSelectOffense: (String, String) -> Unit,
    incidentDescription: String,
    onIncidentDescriptionChange: (String) -> Unit,
    evidencePayload: String,
    onEvidencePayloadChange: (String) -> Unit,
    isAffirmationChecked: Boolean,
    onAffirmationChange: (Boolean) -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Zero Tolerance Warning Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("⚖️", fontSize = 20.sp)
                        Text(
                            text = "Zero Tolerance Legal Policy",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    Text(
                        text = "Cyberstalking is a serious criminal offense under universal penal statutes, cyber protection laws, and electronic communications acts worldwide. Localiiiy strictly enforces zero tolerance across all countries.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Submitting this form immediately executes an irrevocable permanent ban against the offender and creates an immutable electronic record that cannot be changed by anyone.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "1. Accused Offender Information",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = accusedUsername,
                onValueChange = onAccusedUsernameChange,
                label = { Text("Accused Username (e.g. shadow_stalker)") },
                placeholder = { Text("Enter username without @") },
                leadingIcon = { Text("👤", modifier = Modifier.padding(start = 12.dp)) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_accused_username")
            )

            // Quick Select from known users
            if (otherUsers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Or quick-select recent contact / neighbor:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    otherUsers.take(4).forEach { u ->
                        FilterChip(
                            selected = accusedUsername.equals(u.username, ignoreCase = true),
                            onClick = { onAccusedUsernameChange(u.username) },
                            label = { Text("@${u.username}", fontSize = 11.sp) },
                            leadingIcon = { Text("🚫", fontSize = 10.sp) }
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "2. Specific Statutory Offense Category",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                offenseOptions.forEach { (key, title) ->
                    val isSelected = selectedOffense == key
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectOffense(key, title) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectOffense(key, title) }
                            )
                            Text(
                                text = title,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "3. Detailed Description of Stalking / Harassment",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = incidentDescription,
                onValueChange = onIncidentDescriptionChange,
                placeholder = { Text("Describe dates, repeated contact despite disinterest, physical or online monitoring, intimidation, or block evasion attempts...", fontSize = 12.5.sp) },
                modifier = Modifier.fillMaxWidth().height(120.dp).testTag("input_incident_description"),
                maxLines = 6
            )
        }

        item {
            Text(
                text = "4. Electronic Evidence Excerpts (Optional / Highly Recommended)",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Paste relevant chat transcripts, timestamps, or radar alert notes. This data will be cryptographically locked into the statutory electronic evidence certificate.",
                fontSize = 11.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = evidencePayload,
                onValueChange = onEvidencePayloadChange,
                placeholder = { Text("Example:\n[2026-09-08 14:22] Repeated wave requests after being asked to stop\n[2026-09-08 15:10] Sent unsolicited remarks on multiple posts...", fontSize = 12.sp) },
                modifier = Modifier.fillMaxWidth().height(100.dp).testTag("input_evidence_payload"),
                maxLines = 5
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        checked = isAffirmationChecked,
                        onCheckedChange = onAffirmationChange,
                        modifier = Modifier.testTag("checkbox_truth_affirmation")
                    )
                    Column {
                        Text(
                            text = "Solemn Affirmation & Evidence Custody Declaration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "I solemnly affirm under penalty of applicable penal and cybercrime laws worldwide that the information and electronic records provided herein are true, accurate, and unmanipulated. I authorize the immutable generation of an official electronic evidence certificate for law enforcement prosecution.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = onSubmit,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_permanent_ban_button")
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "🔒 Execute Permanent Ban & Lock Immutable Record",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -------------------------------------------------------------
// TAB 2: Immutable Vault Directory (Non-Modifiable Records)
// -------------------------------------------------------------
@Composable
private fun ImmutableVaultDirectoryTab(
    incidents: List<CyberstalkingIncidentEntity>,
    onSwitchToReportTab: () -> Unit
) {
    val context = LocalContext.current
    var expandedIncidentId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("🔒", fontSize = 20.sp)
                            Text(
                                text = "Immutable Evidence Vault",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "${incidents.size} Records Locked",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = "Directory Directory Path: /cyberstalking_immutable_records (Stored as read-only cryptographic files + SQLite append-only rows). These records CANNOT be altered, rewritten, or deleted.",
                        fontSize = 11.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        if (incidents.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("🛡️", fontSize = 42.sp)
                        Text(
                            text = "No Cyberstalking Incidents Recorded",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Localiiiy maintains a zero-tolerance policy. If anyone harasses or surveils you, report them immediately to generate a permanent legal certificate.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onSwitchToReportTab) {
                            Text("Report Offense & Ban Offender")
                        }
                    }
                }
            }
        } else {
            items(incidents) { inc ->
                val isExpanded = expandedIncidentId == inc.id
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth().testTag("incident_record_card_${inc.id}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Top Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "@${inc.accusedUsername}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = MaterialTheme.colorScheme.errorContainer
                                    ) {
                                        Text(
                                            text = "PERMANENTLY BANNED",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onErrorContainer,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = inc.offenseLegalTitle,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Recorded: ${inc.getFormattedDate()}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(onClick = {
                                expandedIncidentId = if (isExpanded) null else inc.id
                            }) {
                                Icon(
                                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Expand Certificate"
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // SHA-256 Digest Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                                Text(
                                    text = "SHA-256: ${inc.digitalSignatureSha256.take(24)}...",
                                    fontSize = 10.5.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "SEALED",
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        // Description preview
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Incident Summary: ${inc.incidentDescription}",
                            fontSize = 12.sp,
                            maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Expanded Certificate View
                        AnimatedVisibility(visible = isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                            ) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Text(
                                    text = "Official Statutory Electronic Evidence Certificate:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF1E1E1E),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = inc.statutoryEvidenceCertificateText,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.5.sp,
                                        color = Color(0xFFE0E0E0),
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(10.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Statutory Electronic Evidence Certificate", inc.statutoryEvidenceCertificateText)
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "Certificate copied to clipboard for Law Enforcement / Court filing", Toast.LENGTH_LONG).show()
                                        },
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Copy Certificate", fontSize = 11.5.sp)
                                    }

                                    Button(
                                        onClick = {
                                            try {
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://cybercrime.gov.in"))
                                                context.startActivity(intent)
                                            } catch (e: Exception) {
                                                Toast.makeText(context, "Open cybercrime.gov.in in browser", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text("Report to 1930 Portal", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
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

// -------------------------------------------------------------
// TAB 3: Global Legal Guidance & Helplines
// -------------------------------------------------------------
@Composable
private fun LegalGuidanceAndHelplineTab() {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Statutory Cyber Harassment Laws",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Every citizen has the legal right to digital dignity and bodily freedom from online surveillance. Understand your protections below:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        item {
            LawClauseCard(
                jurisdiction = "🌐 UNIVERSAL JURISDICTION",
                statuteTitle = "Universal Anti-Cyberstalking Zero-Tolerance Framework",
                penaltyText = "Global criminalization. Immediate account suspension, hardware ban & police referral.",
                details = "Prohibits using electronic communication services to surveil, harass, intimidate, stalk, or place any individual in distress or fear of safety. Enforced worldwide with permanent hardware-level device bans."
            )
        }

        item {
            LawClauseCard(
                jurisdiction = "⚖️ INTERNATIONAL EVIDENCE",
                statuteTitle = "ISO/IEC 27037 Digital Evidence Preservation Protocols",
                penaltyText = "Statutory SHA-256 sealed audit trails & tamper-proof local logs.",
                details = "Requires continuous cryptographic verification of electronic records, preserving digital chains of custody to ensure immediate evidentiary admissibility in judicial courts and cybercrime tribunals worldwide."
            )
        }

        item {
            LawClauseCard(
                jurisdiction = "🔒 GLOBAL DATA PROTECTION",
                statuteTitle = "Universal Privacy Sovereignty & Data Minimization Standards",
                penaltyText = "Strict zero-tolerance confidentiality, anti-surveillance & location rights.",
                details = "Mandates that personal telemetric and location data cannot be mined or weaponized. Immediate protective isolation is granted to complainants upon initial report submission."
            )
        }

        item {
            LawClauseCard(
                jurisdiction = "🛡️ ZERO TOLERANCE",
                statuteTitle = "Permanent Account Suspension & Hardware Device Blacklisting",
                penaltyText = "Definitive permanent suspension with zero right of appeal.",
                details = "Worldwide enforcement across platform instances: substantiated offenders have their accounts permanently deactivated and device hardware identifiers blacklisted against future re-registration."
            )
        }

        item {
            LawClauseCard(
                jurisdiction = "📁 INTERNAL STORAGE",
                statuteTitle = "Safety Log Directory & Cryptographic Export Vault",
                penaltyText = "Local internal storage (/safety_logs/) with SHA-256 digital seals.",
                details = "Allows users to export direct chat histories and forensic metadata securely for their personal records and legal evidence submission, guaranteeing non-tampered, timestamped data preservation."
            )
        }

        item {
            Text(
                text = "Emergency Response Helplines",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                HelplineCard(
                    title = "Universal Emergency Services (Police / Immediate Danger)",
                    number = "112 (Universal Worldwide) • 911 • 999",
                    website = null,
                    onCallClick = {
                        try {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:112"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Dial 112, 911, or local emergency", Toast.LENGTH_SHORT).show()
                        }
                    }
                )

                HelplineCard(
                    title = "Global Cybercrime Reporting & Police Liaison",
                    number = "National Cyber Crime Units & CERT Organizations",
                    website = "interpol.int/Crimes/Cybercrime",
                    onCallClick = {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.interpol.int/Crimes/Cybercrime"))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Visit national cybercrime portal", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SafetyLogsDirectoryTab() {
    val context = LocalContext.current
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    var logsList by remember { mutableStateOf(SafetyLogManager.listSafetyLogs(context)) }
    var selectedLogContent by remember { mutableStateOf<Pair<String, String>?>(null) }
    var verificationStatusMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    fun refreshList() {
        logsList = SafetyLogManager.listSafetyLogs(context)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Internal Storage Safety Log Directory",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Stored at /data/user/0/.../files/safety_logs/ • Sealed with SHA-256 hashes to guarantee non-tampered digital evidence preservation for your personal records or court filing.",
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }

        if (logsList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Outlined.FolderSpecial,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Safety Logs in Internal Storage",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Export chat histories with contacts or offenders by opening any chat thread and tapping the 🛡️ Export Safety Log button.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        } else {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${logsList.size} Exported Evidence Log${if (logsList.size > 1) "s" else ""}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    TextButton(onClick = { refreshList() }) {
                        Text("Refresh", fontSize = 11.sp)
                    }
                }
            }

            items(logsList, key = { it.fileName }) { record ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Fingerprint,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Participant: @${record.targetUsername}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = "${record.formattedTimestamp} • ${record.messagesCount} messages",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            IconButton(
                                onClick = {
                                    SafetyLogManager.deleteSafetyLog(context, record.fileName)
                                    refreshList()
                                    Toast.makeText(context, "Log removed from internal storage", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(26.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // SHA-256 seal & verification
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SHA-256: ${record.sha256Checksum.take(14)}...${record.sha256Checksum.takeLast(6)}",
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                    fontSize = 9.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f)
                                )

                                val isGood = verificationStatusMap[record.fileName] ?: record.isIntegrityVerified
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (isGood) androidx.compose.ui.graphics.Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer,
                                    modifier = Modifier.clickable {
                                        val valid = SafetyLogManager.verifyIntegrity(context, record.fileName)
                                        verificationStatusMap = verificationStatusMap + (record.fileName to valid)
                                        Toast.makeText(
                                            context,
                                            if (valid) "SHA-256 Validated! Non-tampered genuine record ✅" else "Hash mismatch! Record altered ❌",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                ) {
                                    Text(
                                        text = if (isGood) "NON-TAMPERED ✅" else "CHECK HASH",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isGood) androidx.compose.ui.graphics.Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val content = SafetyLogManager.readSafetyLogContent(context, record.certificateFileName)
                                    selectedLogContent = Pair("Statutory Certificate", content)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                            ) {
                                Text("Certificate", fontSize = 10.5.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val content = SafetyLogManager.readSafetyLogContent(context, record.fileName)
                                    selectedLogContent = Pair("Raw JSON Log", content)
                                },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                            ) {
                                Text("JSON Log", fontSize = 10.5.sp)
                            }

                            Button(
                                onClick = { SafetyLogManager.shareSafetyLog(context, record.fileName) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(30.dp)
                            ) {
                                Text("Share", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    selectedLogContent?.let { (title, text) ->
        AlertDialog(
            onDismissRequest = { selectedLogContent = null },
            title = { Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 380.dp)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = 11.sp,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                        lineHeight = 14.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(text))
                    Toast.makeText(context, "Copied to clipboard 📋", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedLogContent = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun LawClauseCard(
    jurisdiction: String,
    statuteTitle: String,
    penaltyText: String,
    details: String
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = statuteTitle, fontWeight = FontWeight.Bold, fontSize = 13.5.sp, modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = jurisdiction,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Text(
                text = "Statutory Punishment: $penaltyText",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.error
            )
            Text(
                text = details,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun HelplineCard(
    title: String,
    number: String,
    website: String?,
    onCallClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "Helpline: $number", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                if (website != null) {
                    Text(text = "Portal: $website", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            FilledTonalButton(onClick = onCallClick) {
                Icon(Icons.Default.Phone, contentDescription = "Call", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Dial", fontSize = 12.sp)
            }
        }
    }
}
