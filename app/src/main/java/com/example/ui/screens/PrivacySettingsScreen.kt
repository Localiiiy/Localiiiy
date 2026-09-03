package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PrivacySettingsEntity
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    privacySettings: PrivacySettingsEntity,
    onBackClick: () -> Unit,
    onUpdateLocationRadar: (Boolean) -> Unit,
    onUpdatePrivateAccount: (Boolean) -> Unit,
    onUpdateNearbyDiscovery: (Boolean) -> Unit,
    onUpdatePreciseLocation: (Boolean) -> Unit,
    onUpdateNearbyWaves: (Boolean) -> Unit,
    onUpdateActiveStatus: (Boolean) -> Unit,
    onUpdateReadReceipts: (Boolean) -> Unit,
    onUpdateCommentsPrivacy: (String) -> Unit,
    onUpdateDirectMessagesPrivacy: (String) -> Unit,
    onUpdateTagsAndMentionsPrivacy: (String) -> Unit,
    onUpdateHideMomentsFromStrangers: (Boolean) -> Unit,
    onUpdatePostResharing: (Boolean) -> Unit,
    onUpdateSensitiveContentFilter: (String) -> Unit,
    onOpenLegalPolicy: () -> Unit = {},
    onResetDefaults: () -> Unit = {},
    onDeleteAccount: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showPrivateAccountConfirmDialog by remember { mutableStateOf(false) }
    var pendingPrivateState by remember { mutableStateOf(false) }

    var showCommentsDialog by remember { mutableStateOf(false) }
    var showMessagesDialog by remember { mutableStateOf(false) }
    var showTagsDialog by remember { mutableStateOf(false) }
    var showSensitiveDialog by remember { mutableStateOf(false) }
    var showBlockedAccountsDialog by remember { mutableStateOf(false) }
    var showDeleteAccountConfirmDialog by remember { mutableStateOf(false) }
    var deleteAccountConfirmationText by remember { mutableStateOf("") }

    // Mock blocked accounts list for user management
    var blockedUsers by remember {
        mutableStateOf(
            listOf(
                Pair("spam_bot_99", "Blocked 2 days ago"),
                Pair("crypto_promos", "Blocked 1 week ago")
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Privacy & Security",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
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
                                    .background(LocaliAccentMint)
                            )
                            Text(
                                text = "Room DB Persistent",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("privacy_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = onResetDefaults,
                        modifier = Modifier.testTag("privacy_reset_button")
                    ) {
                        Text(
                            text = "Reset",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .testTag("privacy_settings_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Local Room Database Status Banner
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Local Privacy Shield Active",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = "All preferences are saved to your device's local Room SQLite database for guaranteed confidentiality.",
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // SECTION 1: HYPERLOCAL & LOCATION PRIVACY
            PrivacySectionHeader(
                title = "Hyperlocal & Location Privacy",
                icon = Icons.Outlined.Radar
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Master Location Radar Toggle
                    PrivacyToggleItem(
                        icon = if (privacySettings.isLocationRadarEnabled) Icons.Default.LocationOn else Icons.Default.LocationOff,
                        title = "Live Location Radar",
                        subtitle = if (privacySettings.isLocationRadarEnabled) "Broadcasting within 10 km live radar circle" else "Location off. You are hidden from neighborhood scans",
                        checked = privacySettings.isLocationRadarEnabled,
                        iconTint = if (privacySettings.isLocationRadarEnabled) LocaliPrimaryTeal else MaterialTheme.colorScheme.error,
                        testTag = "toggle_location_radar_switch",
                        onCheckedChange = onUpdateLocationRadar
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Precise vs Approximate Location
                    PrivacyToggleItem(
                        icon = Icons.Outlined.MyLocation,
                        title = "Precise Distance Sharing",
                        subtitle = if (privacySettings.preciseLocationSharing) "Shows exact distance (e.g., 0.6 km away)" else "Shows approximate neighborhood name only",
                        checked = privacySettings.preciseLocationSharing,
                        enabled = privacySettings.isLocationRadarEnabled,
                        testTag = "toggle_precise_location_switch",
                        onCheckedChange = onUpdatePreciseLocation
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Nearby Discovery Toggle
                    PrivacyToggleItem(
                        icon = Icons.Outlined.Explore,
                        title = "Nearby Discovery Feed",
                        subtitle = "Allow nearby creators to find your posts in local hotspots",
                        checked = privacySettings.allowNearbyDiscovery,
                        enabled = privacySettings.isLocationRadarEnabled,
                        testTag = "toggle_nearby_discovery_switch",
                        onCheckedChange = onUpdateNearbyDiscovery
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Proximity Waves Toggle
                    PrivacyToggleItem(
                        icon = Icons.Outlined.WavingHand,
                        title = "Neighbor Proximity Waves (👋)",
                        subtitle = "Allow nearby creators on radar to send quick hello waves",
                        checked = privacySettings.allowNearbyWaves,
                        enabled = privacySettings.isLocationRadarEnabled,
                        testTag = "toggle_nearby_waves_switch",
                        onCheckedChange = onUpdateNearbyWaves
                    )
                }
            }

            // SECTION 2: ACCOUNT PRIVACY & VISIBILITY
            PrivacySectionHeader(
                title = "Account Visibility & Ghost Mode",
                icon = Icons.Outlined.Lock
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Private Account (Ghost Mode)
                    PrivacyToggleItem(
                        icon = if (privacySettings.isPrivateAccount) Icons.Default.Lock else Icons.Default.Public,
                        title = "Private Account (Ghost Mode)",
                        subtitle = if (privacySettings.isPrivateAccount) "Only approved connections can see your profile & posts" else "Anyone on Localiiiy can view your public content",
                        checked = privacySettings.isPrivateAccount,
                        iconTint = if (privacySettings.isPrivateAccount) LocaliAccentMint else LocaliPrimaryTeal,
                        testTag = "toggle_private_account_switch",
                        onCheckedChange = { targetState ->
                            pendingPrivateState = targetState
                            showPrivateAccountConfirmDialog = true
                        }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Show Active Status
                    PrivacyToggleItem(
                        icon = Icons.Outlined.Visibility,
                        title = "Activity Status",
                        subtitle = "Allow people you follow & message to see when you were last active",
                        checked = privacySettings.showActiveStatus,
                        testTag = "toggle_active_status_switch",
                        onCheckedChange = onUpdateActiveStatus
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Read Receipts
                    PrivacyToggleItem(
                        icon = Icons.Outlined.DoneAll,
                        title = "Direct Message Read Receipts",
                        subtitle = "Let message senders see when you have viewed their chat",
                        checked = privacySettings.readReceiptsEnabled,
                        testTag = "toggle_read_receipts_switch",
                        onCheckedChange = onUpdateReadReceipts
                    )
                }
            }

            // SECTION 3: SOCIAL INTERACTIONS & PERMISSIONS
            PrivacySectionHeader(
                title = "Interactions & Mentions",
                icon = Icons.AutoMirrored.Filled.Chat
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Comments Permission Selector
                    PrivacyChoiceItem(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        title = "Comments",
                        currentValue = formatOptionLabel(privacySettings.allowCommentsFrom),
                        testTag = "choice_comments_privacy",
                        onClick = { showCommentsDialog = true }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Direct Messages Permission Selector
                    PrivacyChoiceItem(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        title = "Direct Messages",
                        currentValue = formatOptionLabel(privacySettings.allowDirectMessagesFrom),
                        testTag = "choice_messages_privacy",
                        onClick = { showMessagesDialog = true }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Mentions & Tags Selector
                    PrivacyChoiceItem(
                        icon = Icons.Outlined.AlternateEmail,
                        title = "Tags & Mentions",
                        currentValue = formatOptionLabel(privacySettings.allowTagsAndMentions),
                        testTag = "choice_tags_privacy",
                        onClick = { showTagsDialog = true }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Hide Moments from Strangers
                    PrivacyToggleItem(
                        icon = Icons.Outlined.AutoStories,
                        title = "Hide Moments from Strangers",
                        subtitle = "Only share 24h stories with mutual neighborhood friends",
                        checked = privacySettings.hideMomentsFromStrangers,
                        testTag = "toggle_hide_moments_switch",
                        onCheckedChange = onUpdateHideMomentsFromStrangers
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Allow Post Resharing
                    PrivacyToggleItem(
                        icon = Icons.Outlined.Share,
                        title = "Allow Post Resharing",
                        subtitle = "Allow other users to reshare your posts to their stories",
                        checked = privacySettings.allowPostResharing,
                        testTag = "toggle_post_resharing_switch",
                        onCheckedChange = onUpdatePostResharing
                    )
                }
            }

            // SECTION 4: SAFETY & CONTENT MODERATION
            PrivacySectionHeader(
                title = "Safety & Content Moderation",
                icon = Icons.Outlined.Shield
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    // Sensitive Content Control
                    PrivacyChoiceItem(
                        icon = Icons.Outlined.FilterCenterFocus,
                        title = "Sensitive Content Control",
                        currentValue = privacySettings.sensitiveContentFilter.lowercase().replaceFirstChar { it.uppercase() },
                        testTag = "choice_sensitive_content",
                        onClick = { showSensitiveDialog = true }
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Blocked Accounts Manager
                    PrivacyChoiceItem(
                        icon = Icons.Outlined.Block,
                        title = "Blocked Accounts",
                        currentValue = "${blockedUsers.size} accounts",
                        testTag = "choice_blocked_accounts",
                        onClick = { showBlockedAccountsDialog = true }
                    )
                }
            }

            // SECTION 5: LEGAL CHARTER & PRIVACY POLICY
            PrivacySectionHeader(
                title = "Legal Agreement & Privacy Policy",
                icon = Icons.Outlined.Gavel
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
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
                                color = LocaliPrimaryTeal.copy(alpha = 0.18f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = LocaliPrimaryTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Column {
                                Text(
                                    text = "App Agreement & Community Charter",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Policy v2026.9.1-EN • Binding Legal Terms",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = LocaliAccentMint.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "VERIFIED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp
                                ),
                                color = LocaliAccentMint,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Covers: 📍 100% Location Choice & Off-Grid Browsing • 🛡️ Zero Data Brokering Guarantee • 🛍️ Marketplace Peer-to-Peer Zero Liability & Cyber Fraud Advisory • 🤝 Community Standards.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 15.5.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = onOpenLegalPolicy,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LocaliPrimaryTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("privacy_open_legal_policy_button")
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Article,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Read Full Legal Agreement & Privacy Policy 📄",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // CARD 9: ACCOUNT DELETION & DATA PURGE (Google Play & Apple Requirement)
            // ==========================================
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("privacy_card_account_deletion")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Account Deletion & Right to Be Forgotten",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "In compliance with Google Play Developer Policy and Apple App Store Review Guideline 5.1.1(v), you can permanently delete your account and all associated personal data (posts, clips, marketplace listings, messages, and location history) at any time.",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp, lineHeight = 15.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            deleteAccountConfirmationText = ""
                            showDeleteAccountConfirmDialog = true
                        },
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .testTag("privacy_delete_account_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Permanently Delete Account & Data ⚠️",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // DIALOG: Confirm Private Account Change
    if (showPrivateAccountConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPrivateAccountConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = if (pendingPrivateState) Icons.Default.Lock else Icons.Default.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = if (pendingPrivateState) "Switch to Private Account?" else "Switch to Public Account?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = if (pendingPrivateState)
                        "When your account is private, only people you approve can see your posts, stories, and active location on the Live Radar. Your current followers won't be affected."
                    else
                        "Anyone on Localiiiy will be able to discover your posts in local hotspots and view your profile on the neighborhood radar.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdatePrivateAccount(pendingPrivateState)
                        showPrivateAccountConfirmDialog = false
                    },
                    modifier = Modifier.testTag("confirm_private_switch_button")
                ) {
                    Text(if (pendingPrivateState) "Switch to Private" else "Switch to Public")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPrivateAccountConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Permanent Account Deletion & Right to Be Forgotten
    if (showDeleteAccountConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountConfirmDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.DeleteForever,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "Delete Account Permanently?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.error
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "This action is irreversible. In accordance with Apple Store Guideline 5.1.1(v) & Google Play Policies, proceeding will permanently erase:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "• All your published posts & stories\n• All uploaded clips & saved bookmarks\n• All marketplace classifieds & active listings\n• All chat histories & location beacons\n• Profile credentials & device tokens",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "To confirm, please type DELETE below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    OutlinedTextField(
                        value = deleteAccountConfirmationText,
                        onValueChange = { deleteAccountConfirmationText = it },
                        placeholder = { Text("Type DELETE") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("delete_account_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (deleteAccountConfirmationText.trim().equals("DELETE", ignoreCase = true)) {
                            showDeleteAccountConfirmDialog = false
                            onDeleteAccount()
                        }
                    },
                    enabled = deleteAccountConfirmationText.trim().equals("DELETE", ignoreCase = true),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.testTag("confirm_delete_account_button")
                ) {
                    Text("Delete Everything", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // DIALOG: Comments Permission
    if (showCommentsDialog) {
        PrivacyOptionsDialog(
            title = "Allow Comments From",
            currentOption = privacySettings.allowCommentsFrom,
            options = listOf(
                Triple("EVERYONE", "Everyone", "Anyone on Localiiiy can comment on your posts"),
                Triple("PEOPLE_YOU_FOLLOW", "People You Follow", "Only accounts you follow can comment"),
                Triple("NO_ONE", "Off", "Turn off comments across all posts")
            ),
            onSelect = { option ->
                onUpdateCommentsPrivacy(option)
                showCommentsDialog = false
            },
            onDismiss = { showCommentsDialog = false }
        )
    }

    // DIALOG: Direct Messages Permission
    if (showMessagesDialog) {
        PrivacyOptionsDialog(
            title = "Allow Direct Messages From",
            currentOption = privacySettings.allowDirectMessagesFrom,
            options = listOf(
                Triple("EVERYONE", "Everyone", "Receive direct messages from anyone in your locality"),
                Triple("PEOPLE_YOU_FOLLOW", "People You Follow", "Only receive messages from accounts you follow"),
                Triple("NO_ONE", "Off", "Disable new message requests")
            ),
            onSelect = { option ->
                onUpdateDirectMessagesPrivacy(option)
                showMessagesDialog = false
            },
            onDismiss = { showMessagesDialog = false }
        )
    }

    // DIALOG: Tags & Mentions Permission
    if (showTagsDialog) {
        PrivacyOptionsDialog(
            title = "Who Can Tag & Mention You",
            currentOption = privacySettings.allowTagsAndMentions,
            options = listOf(
                Triple("EVERYONE", "Everyone", "Anyone can tag or mention @${privacySettings.id}"),
                Triple("PEOPLE_YOU_FOLLOW", "People You Follow", "Only people you follow can tag you"),
                Triple("NO_ONE", "No One", "Don't allow anyone to tag or mention you")
            ),
            onSelect = { option ->
                onUpdateTagsAndMentionsPrivacy(option)
                showTagsDialog = false
            },
            onDismiss = { showTagsDialog = false }
        )
    }

    // DIALOG: Sensitive Content Control
    if (showSensitiveDialog) {
        PrivacyOptionsDialog(
            title = "Sensitive Content Control",
            currentOption = privacySettings.sensitiveContentFilter,
            options = listOf(
                Triple("STANDARD", "Standard (Recommended)", "You may see some sensitive content in Explore and Reels"),
                Triple("STRICT", "Strict", "Filter out more sensitive photos, videos, and keywords"),
                Triple("LESS", "Less", "See more varied content across neighborhood pulses")
            ),
            onSelect = { option ->
                onUpdateSensitiveContentFilter(option)
                showSensitiveDialog = false
            },
            onDismiss = { showSensitiveDialog = false }
        )
    }

    // DIALOG: Blocked Accounts Manager
    if (showBlockedAccountsDialog) {
        AlertDialog(
            onDismissRequest = { showBlockedAccountsDialog = false },
            title = {
                Text(
                    text = "Blocked Accounts (${blockedUsers.size})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (blockedUsers.isEmpty()) {
                        Text(
                            text = "No blocked accounts. Your radar is clean! ✨",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        blockedUsers.forEach { (username, date) ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "@$username",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = date,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    FilledTonalButton(
                                        onClick = {
                                            blockedUsers = blockedUsers.filter { it.first != username }
                                        },
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Unblock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBlockedAccountsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun PrivacySectionHeader(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = LocaliPrimaryTeal,
            modifier = Modifier.size(17.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PrivacyToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    enabled: Boolean = true,
    iconTint: Color = MaterialTheme.colorScheme.primary,
    testTag: String = "",
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = if (enabled) 0.12f else 0.05f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) iconTint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.5.sp,
                    color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    lineHeight = 15.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (enabled) 0.85f else 0.5f)
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            modifier = if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier
        )
    }
}

@Composable
private fun PrivacyChoiceItem(
    icon: ImageVector,
    title: String,
    currentValue: String,
    testTag: String = "",
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .then(if (testTag.isNotBlank()) Modifier.testTag(testTag) else Modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = LocaliPrimaryTeal.copy(alpha = 0.12f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = LocaliPrimaryTeal,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Text(
                text = title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = currentValue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun PrivacyOptionsDialog(
    title: String,
    currentOption: String,
    options: List<Triple<String, String, String>>,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { (key, label, desc) ->
                    val isSelected = currentOption.equals(key, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(key) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelect(key) }
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = label,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}

private fun formatOptionLabel(option: String): String {
    return when (option.uppercase()) {
        "EVERYONE" -> "Everyone"
        "PEOPLE_YOU_FOLLOW", "FOLLOWERS" -> "People You Follow"
        "NO_ONE", "OFF" -> "Off"
        else -> option
    }
}
