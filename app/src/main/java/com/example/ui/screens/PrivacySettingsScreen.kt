package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    privacySettings: com.example.data.PrivacySettingsEntity? = null,
    onUpdatePrivacySettings: ((com.example.data.PrivacySettingsEntity) -> Unit)? = null,
    dailyCheckInState: com.example.data.DailyCheckInState = com.example.data.DailyCheckInState(),
    onClaimCheckIn: () -> Unit = {},
    referralState: com.example.data.NeighborReferralState = com.example.data.NeighborReferralState(),
    onRedeemFriendCode: (String) -> Boolean = { false },
    creatorEarnings: com.example.data.CreatorEarningsSummary = com.example.data.CreatorEarningsSummary(),
    payoutAccount: com.example.data.CreatorPayoutAccount = com.example.data.CreatorPayoutAccount(),
    payoutHistory: List<com.example.data.PayoutTransaction> = emptyList(),
    currentCurrency: com.example.util.LocaliiiyCurrency = com.example.util.LocaliiiyCurrency.USD,
    onRequestPayout: (Double) -> Boolean = { false },
    onSubscribeToPremium: ((Boolean) -> Unit)? = null,
    onCancelPremium: (() -> Unit)? = null,
    onNavigateBack: () -> Unit,
    onNavigateToBlockedUsers: () -> Unit = {},
    onNavigateToConnections: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    val currentSettings = privacySettings ?: com.example.data.InitialData.defaultPrivacySettings
    var isGhostMode by remember(privacySettings) { mutableStateOf(currentSettings.isGhostMode) }
    var locationPrecisionKm by remember(privacySettings) { mutableStateOf(currentSettings.locationPrecisionKm) }
    var hideLocation by remember(privacySettings) { mutableStateOf(currentSettings.hideLocation) }
    var hideProfilePicture by remember(privacySettings) { mutableStateOf(currentSettings.hideProfilePicture) }
    var hidePosts by remember(privacySettings) { mutableStateOf(currentSettings.hidePosts) }
    var hideClips by remember(privacySettings) { mutableStateOf(currentSettings.hideClips) }
    var hideEmail by remember(privacySettings) { mutableStateOf(currentSettings.hideEmailAddress) }
    var hideMobileNumber by remember(privacySettings) { mutableStateOf(currentSettings.hideMobileNumber) }
    var hideAddress by remember(privacySettings) { mutableStateOf(currentSettings.hideAddress) }
    var hideInNeighborhood by remember(privacySettings) { mutableStateOf(currentSettings.hideInNeighborhood) }
    var allowDirectCalls by remember(privacySettings) { mutableStateOf(currentSettings.allowDirectCallsFromConnections) }

    fun syncSettings(
        ghost: Boolean = isGhostMode,
        precision: Float = locationPrecisionKm,
        hideLoc: Boolean = hideLocation,
        hidePic: Boolean = hideProfilePicture,
        hideP: Boolean = hidePosts,
        hideC: Boolean = hideClips,
        hideMail: Boolean = hideEmail,
        hidePhone: Boolean = hideMobileNumber,
        hideAddr: Boolean = hideAddress,
        hideNeigh: Boolean = hideInNeighborhood,
        calls: Boolean = allowDirectCalls
    ) {
        onUpdatePrivacySettings?.invoke(
            currentSettings.copy(
                isGhostMode = ghost,
                locationPrecisionKm = precision,
                hideLocation = hideLoc,
                hideProfilePicture = hidePic,
                hidePosts = hideP,
                hideClips = hideC,
                hideEmailAddress = hideMail,
                hideMobileNumber = hidePhone,
                hideAddress = hideAddr,
                hideInNeighborhood = hideNeigh,
                hidePreciseLocationOnRadar = ghost || hideLoc,
                allowDirectCallsFromConnections = calls
            )
        )
    }

    var requireBiometrics by remember { mutableStateOf(false) }
    var sendViewReceipts by remember { mutableStateOf(true) }
    var silenceNotifications by remember { mutableStateOf(true) }
    
    var hapticFeedbackLevel by remember { mutableStateOf(1f) } // 0=Off, 1=Standard, 2=Strong
    
    var showDataOblivionDialog by remember { mutableStateOf(false) }
    var showAuditLogDialog by remember { mutableStateOf(false) }
    var showTutorialScreen by remember { mutableStateOf(false) }
    var showWalletScreen by remember { mutableStateOf(false) }
    var showDailyCheckInScreen by remember { mutableStateOf(false) }
    var showReferralScreen by remember { mutableStateOf(false) }
    var showPremiumScreen by remember { mutableStateOf(false) }
    var showTermsScreen by remember { mutableStateOf(false) }
    var showDisplayScaleSheet by remember { mutableStateOf(false) }
    
    val haptic = LocalHapticFeedback.current

    if (showTutorialScreen) {
        AppTutorialGuideScreen(
            onNavigateBack = { showTutorialScreen = false }
        )
    } else if (showWalletScreen) {
        WalletScreen(
            earnings = creatorEarnings,
            payoutAccount = payoutAccount,
            payoutHistory = payoutHistory,
            currentCurrency = currentCurrency,
            onRequestPayout = onRequestPayout,
            onNavigateBack = { showWalletScreen = false }
        )
    } else if (showDailyCheckInScreen) {
        DailyCheckInScreen(
            checkInState = dailyCheckInState,
            onClaimCheckIn = onClaimCheckIn,
            onNavigateBack = { showDailyCheckInScreen = false }
        )
    } else if (showReferralScreen) {
        ReferralSystemScreen(
            referralState = referralState,
            onRedeemFriendCode = onRedeemFriendCode,
            onNavigateBack = { showReferralScreen = false }
        )
    } else if (showPremiumScreen) {
        PremiumSubscriptionScreen(
            isSubscribed = currentSettings.isPremiumSubscribed,
            currentCurrency = currentCurrency,
            onNavigateBack = { showPremiumScreen = false },
            onSubscribe = { isAnnual ->
                onSubscribeToPremium?.invoke(isAnnual)
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                showPremiumScreen = false
            },
            onCancelSubscription = {
                onCancelPremium?.invoke()
                showPremiumScreen = false
            }
        )
    } else if (showTermsScreen) {
        TermsAndConditionsScreen(onNavigateBack = { showTermsScreen = false })
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Settings & Privacy", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // TOP OF PRIVACY: 100% FREE PASSIVE GHOST SHIELD & STEALTH MODE (Distinct Incognito Look)
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0C1929),
                        border = BorderStroke(
                            1.5.dp,
                            if (isGhostMode) Color(0xFF00E5FF) else Color(0xFF1E3A5F)
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp)
                        ) {
                            // Top Tag
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = Color(0xFF00E5FF).copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.5f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Shield,
                                            contentDescription = null,
                                            tint = Color(0xFF00E5FF),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Text(
                                            text = "100% FREE PRIVACY • ZERO PAYWALL",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF00E5FF),
                                            letterSpacing = 0.5.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isGhostMode,
                                    onCheckedChange = { enabled ->
                                        isGhostMode = enabled
                                        syncSettings(ghost = enabled)
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = Color(0xFF00E5FF),
                                        uncheckedThumbColor = Color.LightGray,
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isGhostMode) Color(0xFF00E5FF).copy(alpha = 0.2f) else Color(0xFF1E293B),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.VisibilityOff,
                                            contentDescription = null,
                                            modifier = Modifier.size(26.dp),
                                            tint = if (isGhostMode) Color(0xFF00E5FF) else Color.LightGray
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Master Ghost & Passive Mode",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Browse completely in passive stealth. Watch clips, scroll reels, browse stories, and buy or sell in the marketplace without exposing your identity, radar coordinates, or view receipts to other users.",
                                        fontSize = 12.sp,
                                        color = Color(0xFFB0C4DE),
                                        lineHeight = 16.5.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Live Status Bar
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isGhostMode) Color(0xFF052B33) else Color(0xFF132030),
                                border = BorderStroke(
                                    1.dp,
                                    if (isGhostMode) Color(0xFF00E5FF).copy(alpha = 0.6f) else Color(0xFF263C57)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(
                                                color = if (isGhostMode) Color(0xFF00E5FF) else Color(0xFF78909C),
                                                shape = CircleShape
                                            )
                                    )
                                    Text(
                                        text = if (isGhostMode)
                                            "STEALTH ACTIVE: Radar presence cloaked & reel views are anonymous"
                                        else
                                            "RADAR ACTIVE: Broadcasting approximate location to neighbors",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isGhostMode) Color(0xFFE0F7FA) else Color(0xFFB0BEC5)
                                    )
                                }
                            }
                        }
                    }
                }

                // Growth & Daily Engagement Header
                item { SettingsSectionHeader("Community & Growth Perks") }

                // Localiiiy Premium Pro (₹299/mo / $2.99) Action Item
                item {
                    SettingsActionItem(
                        title = "Localiiiy Premium Pro (₹299/mo / $2.99) 👑",
                        subtitle = if (currentSettings.isPremiumSubscribed)
                            "Active Titan Member ✓ • 5x Boost, 0% Market Escrow, Gold Halo & 4K Studio"
                        else
                            "Unlock 5x Feed Boost, 0% Safe-Haven Market Escrow, Gold Badge & Radar Sonar",
                        icon = Icons.Default.WorkspacePremium,
                        onClick = { showPremiumScreen = true }
                    )
                }

                // Daily Check-in & Radar Perks Item
                item {
                    SettingsActionItem(
                        title = "Daily Check-in & Radar Perks",
                        subtitle = "${dailyCheckInState.currentStreakDays}-Day Streak 🔥 • ${if (!dailyCheckInState.hasCheckedInToday) "Reward available today! Claim 24h radar perk" else "Claimed today ✓ Next reward unlocks tomorrow"}",
                        icon = Icons.Default.Stars,
                        onClick = { showDailyCheckInScreen = true }
                    )
                }

                // Referral & Neighbor Invite Item
                item {
                    SettingsActionItem(
                        title = "Invite Neighbors & Growth Loop",
                        subtitle = "Code: ${referralState.userReferralCode} • ${referralState.totalNeighborsInvited} neighbors joined • 2x Radar Reach",
                        icon = Icons.Default.GroupAdd,
                        onClick = { showReferralScreen = true }
                    )
                }

            // Location Precision & Obfuscation (up to 500KM)
            item { SettingsSectionHeader("Location Precision & GPS Obfuscation") }
            item {
                SettingsSliderItem(
                    title = "Location Precision Level (Up to 500 KM)",
                    subtitle = when {
                        locationPrecisionKm <= 2f -> "Exact Street (~${(locationPrecisionKm * 500).toInt()}m)"
                        locationPrecisionKm <= 10f -> "Neighborhood Level (${locationPrecisionKm.toInt()} km)"
                        locationPrecisionKm <= 50f -> "City & District (${locationPrecisionKm.toInt()} km)"
                        locationPrecisionKm <= 150f -> "Metropolitan Zone (${locationPrecisionKm.toInt()} km)"
                        locationPrecisionKm <= 350f -> "State / Province (${locationPrecisionKm.toInt()} km)"
                        else -> "Max Privacy Mask (${locationPrecisionKm.toInt()} km / 500 km)"
                    },
                    icon = Icons.Default.LocationSearching,
                    value = locationPrecisionKm,
                    onValueChange = { 
                        locationPrecisionKm = it
                        syncSettings(precision = it)
                    },
                    valueRange = 1f..500f
                )
            }
            item {
                SettingsToggleItem(
                    title = "Hide Location Completely",
                    subtitle = "Never display coordinates or pins on local radar or public feeds",
                    icon = Icons.Default.WrongLocation,
                    checked = hideLocation || isGhostMode
                ) {
                    hideLocation = it
                    syncSettings(hideLoc = it)
                }
            }
            item {
                SettingsToggleItem(
                    title = "Hide in Neighborhood",
                    subtitle = "Remove yourself from local neighborhood lists and discovery spaces",
                    icon = Icons.Default.HolidayVillage,
                    checked = hideInNeighborhood || isGhostMode
                ) {
                    hideInNeighborhood = it
                    syncSettings(hideNeigh = it)
                }
            }
            item {
                SettingsToggleItem(
                    title = "Hide Street Address & Physical Area",
                    subtitle = "Conceal street names, building numbers and specific area markers",
                    icon = Icons.Default.PinDrop,
                    checked = hideAddress || isGhostMode
                ) {
                    hideAddress = it
                    syncSettings(hideAddr = it)
                }
            }

            // Identity & Profile Privacy Controls
            item { SettingsSectionHeader("Profile & Content Privacy Controls") }
            item {
                SettingsToggleItem(
                    title = "Hide Profile Picture",
                    subtitle = "Replace profile photo with generic anonymous avatar for unconnected users",
                    icon = Icons.Default.AccountCircle,
                    checked = hideProfilePicture || isGhostMode
                ) {
                    hideProfilePicture = it
                    syncSettings(hidePic = it)
                }
            }
            item {
                SettingsToggleItem(
                    title = "Hide Posts",
                    subtitle = "Restrict your pulse posts strictly to mutual connections only",
                    icon = Icons.Default.Article,
                    checked = hidePosts || isGhostMode
                ) {
                    hidePosts = it
                    syncSettings(hideP = it)
                }
            }
            item {
                SettingsToggleItem(
                    title = "Hide Clips",
                    subtitle = "Keep your video clips invisible to public neighborhood discovery",
                    icon = Icons.Default.VideoLibrary,
                    checked = hideClips || isGhostMode
                ) {
                    hideClips = it
                    syncSettings(hideC = it)
                }
            }
            item {
                SettingsToggleItem(
                    title = "Hide Email Address",
                    subtitle = "Conceal your email on your profile and Localiiiy ID card",
                    icon = Icons.Default.Mail,
                    checked = hideEmail || isGhostMode
                ) {
                    hideEmail = it
                    syncSettings(hideMail = it)
                }
            }
            item {
                SettingsToggleItem(
                    title = "Hide Mobile Number",
                    subtitle = "Conceal phone number from public cards, search & contact sync",
                    icon = Icons.Default.Phone,
                    checked = hideMobileNumber || isGhostMode
                ) {
                    hideMobileNumber = it
                    syncSettings(hidePhone = it)
                }
            }

            // Ghost Protocol Schedules & Receipts
            item { SettingsSectionHeader("Ghost Protocol & Privacy") }
            item {
                SettingsActionItem("Erase Location Footprint", "Clears all cached GPS coordinates with haptic confirmation", Icons.Default.DeleteSweep) {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            }
            item {
                SettingsToggleItem("Radar Invisibility Schedule", "Automatically activate Ghost Mode (10 PM - 7 AM)", Icons.Default.Nightlight, true) {}
            }
            item {
                SettingsToggleItem("Silence Notifications", "Mute push banners while Ghost Mode is active", Icons.Default.NotificationsOff, silenceNotifications) { silenceNotifications = it }
            }
            item {
                SettingsToggleItem("Send View Receipts", "Let others see when you view public stories", Icons.Default.ReceiptLong, sendViewReceipts && !isGhostMode) { sendViewReceipts = it }
            }

            // Security & Connections
            item { SettingsSectionHeader("Security & Call Boundaries") }
            item {
                SettingsToggleItem(
                    title = "Allow Direct Calls from Connections",
                    subtitle = "Enable incoming audio & video calls from mutual connections. When OFF, direct calls are blocked.",
                    icon = Icons.Default.Call,
                    checked = allowDirectCalls
                ) {
                    allowDirectCalls = it
                    syncSettings(calls = it)
                }
            }
            item {
                SettingsActionItem("Connection Social Graph Manager", "Manage mutual connections and shared spaces", Icons.Default.People, onClick = onNavigateToConnections)
            }
            item {
                SettingsActionItem("Blocked & Muted Boundaries", "Complete two-way invisibility", Icons.Default.Block, onClick = onNavigateToBlockedUsers)
            }
            item {
                SettingsToggleItem("Biometric Chat Vault", "Require fingerprint/PIN before opening DMs", Icons.Default.Fingerprint, requireBiometrics) { requireBiometrics = it }
            }
            item {
                SettingsToggleItem("Connected-Only Inbound Messaging", "Restrict DMs strictly to mutual connections", Icons.Default.MarkEmailUnread, true) {}
            }

            // Data & Storage
            item { SettingsSectionHeader("Data, Storage & Network") }
            item {
                SettingsToggleItem(
                    title = "Cellular Data Saver & Video Auto-Play",
                    subtitle = "Stream clips in standard definition on mobile data to conserve bandwidth",
                    icon = Icons.Default.DataUsage,
                    checked = false
                ) {}
            }
            item {
                SettingsToggleItem(
                    title = "P2P Radar Mesh Offline Cache",
                    subtitle = "Cache local point-of-interest markers for instantaneous offline map discovery",
                    icon = Icons.Default.Sensors,
                    checked = true
                ) {}
            }
            item {
                SettingsActionItem("Data Saver & Media Cache", "Current disk usage: 142MB. Tap to clear cache.", Icons.Default.Storage) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
            }
            item {
                SettingsActionItem("Export Local Archive", "Export saved posts and drafts into an encrypted ZIP", Icons.Default.Archive) {}
            }
            item {
                SettingsActionItem("Privacy Transparency Log", "View zero-tracking analytics audit log", Icons.Default.ListAlt) { showAuditLogDialog = true }
            }

            // Appearance & Haptics
            item { SettingsSectionHeader("Appearance & Customization") }
            item {
                SettingsActionItem(
                    "App Display Size & Scaling",
                    "Adapt interface scaling to your phone display (Current: ${currentSettings.appDisplayScale})",
                    Icons.Default.AspectRatio
                ) {
                    showDisplayScaleSheet = true
                }
            }
            item {
                SettingsActionItem("Dynamic Material 3 Color Theme Engine", "Select Emerald, Amber, Cyan, Rose, or Noir", Icons.Default.Palette) {}
            }
            item {
                SettingsToggleItem("Decoy App Icon", "Swap launcher icon to stealth 'Calculator'", Icons.Default.Calculate, false) {}
            }
            item {
                SettingsSliderItem(
                    title = "Audio Haptic Feedback Calibration",
                    subtitle = "Vibration strength across UI actions",
                    icon = Icons.Default.Vibration,
                    value = hapticFeedbackLevel,
                    onValueChange = { hapticFeedbackLevel = it },
                    steps = 1,
                    valueRange = 0f..2f
                )
            }

            // About & Danger Zone
            item { SettingsSectionHeader("Platform") }
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Platform Patron", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text("Support our 100% tracker-free pledge with optional micro-patronage.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.8f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = {}) { Text("Become a Patron") }
                    }
                }
            }
            item {
                SettingsActionItem(
                    "App Tutorial, Badges & Monetization",
                    "Visual guide with images, badge tiers & $1000 minimum payout rules",
                    Icons.Default.School,
                    onClick = { showTutorialScreen = true }
                )
            }
            item {
                SettingsActionItem(
                    "Terms & Conditions",
                    "Read platform rules, escrow policy, and fees",
                    Icons.Default.Gavel,
                    onClick = { showTermsScreen = true }
                )
            }
            item {
                SettingsActionItem("About & Open Protocol", "Software licenses and Dual-Reach Manifesto", Icons.Default.Info) {}
            }
            item {
                SettingsActionItem("Complete Account & Data Oblivion", "Guaranteed complete erasure with zero database artifacts", Icons.Default.Warning, tint = MaterialTheme.colorScheme.error) {
                    showDataOblivionDialog = true
                }
            }
        }
    }
    }

    if (showDataOblivionDialog) {
        AlertDialog(
            onDismissRequest = { showDataOblivionDialog = false },
            title = { Text("Complete Data Oblivion") },
            text = { Text("Are you absolutely sure? This will execute a cryptographic Room database wipe and clear all shared preferences. This cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { 
                        showDataOblivionDialog = false
                        onDeleteAccount()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Purge Data & Delete Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDataOblivionDialog = false }) { Text("Cancel") }
            }
        )
    }
    
    if (showAuditLogDialog) {
        AlertDialog(
            onDismissRequest = { showAuditLogDialog = false },
            title = { Text("Privacy Transparency Log") },
            text = { 
                LazyColumn {
                    items(5) {
                        Text("[${System.currentTimeMillis()}] Event logged locally. No telemetry broadcasted.", fontSize = 10.sp)
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAuditLogDialog = false }) { Text("Close") }
            }
        )
    }

    if (showDisplayScaleSheet) {
        com.example.ui.components.AppDisplaySizeSheet(
            currentScaleKey = currentSettings.appDisplayScale,
            onSelectScale = { newScale ->
                onUpdatePrivacySettings?.invoke(currentSettings.copy(appDisplayScale = newScale))
            },
            onDismiss = { showDisplayScaleSheet = false }
        )
    }
}

@Composable
fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
fun SettingsToggleItem(title: String, subtitle: String, icon: ImageVector, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("neighbor_card"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Justify
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsActionItem(title: String, subtitle: String, icon: ImageVector, tint: Color = MaterialTheme.colorScheme.onSurfaceVariant, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("neighbor_card"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = if (tint != MaterialTheme.colorScheme.onSurfaceVariant) tint else MaterialTheme.colorScheme.onSurface)
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Justify
            )
        }
    }
}

@Composable
fun SettingsSliderItem(title: String, subtitle: String, icon: ImageVector, value: Float, onValueChange: (Float) -> Unit, steps: Int = 0, valueRange: ClosedFloatingPointRange<Float>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag("neighbor_card")
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Justify
                )
            }
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            steps = steps,
            valueRange = valueRange,
            modifier = Modifier.padding(start = 40.dp)
        )
    }
}
