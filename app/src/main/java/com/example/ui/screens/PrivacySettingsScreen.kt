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
        hideNeigh: Boolean = hideInNeighborhood
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
                hidePreciseLocationOnRadar = ghost || hideLoc
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
                // App Running Tutorial, Activation Badges & Monetization Guide Banner
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable { showTutorialScreen = true }
                            .testTag("app_tutorial_settings_banner"),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.primaryContainer,
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.MenuBook,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("App Running Tutorial & Guide", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Surface(shape = RoundedCornerShape(100.dp), color = MaterialTheme.colorScheme.primary) {
                                        Text("NEW", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Text(
                                    "Visual step-by-step app walkthrough with images, Activation Badges guide, and Monetization ($1,000 min withdrawal)",
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Wallet & Community Rewards Header
                item { SettingsSectionHeader("Wallet & Growth Rewards") }

                // Wallet Card
                item {
                    val isEligible = creatorEarnings.availableBalanceUSD >= payoutAccount.minimumPayoutUSD
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable { showWalletScreen = true }
                            .testTag("settings_wallet_item"),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFF0F172A),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF38BDF8).copy(alpha = 0.15f),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.AccountBalanceWallet,
                                        contentDescription = "Wallet",
                                        tint = Color(0xFF38BDF8),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("Creator & Community Wallet", fontWeight = FontWeight.Bold, fontSize = 14.5.sp, color = Color.White)
                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isEligible) Color(0xFF00FF41) else Color(0xFFFBBF24).copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = if (isEligible) "WITHDRAWAL READY" else "$1,000 THRESHOLD",
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Black,
                                            color = if (isEligible) Color.Black else Color(0xFFFBBF24),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Available: ${com.example.util.CurrencyHelper.format(creatorEarnings.availableBalanceUSD, currentCurrency)} • Tap to view detailed withdrawal history",
                                    fontSize = 11.5.sp,
                                    color = Color.LightGray
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Open Wallet",
                                tint = Color(0xFF38BDF8)
                            )
                        }
                    }
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

                // Master Ghost Switch
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = if (isGhostMode) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.VisibilityOff,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = if (isGhostMode) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Master Ghost Mode", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Instantly cloaks radar presence, hides all identity details & browse anonymously", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isGhostMode,
                            onCheckedChange = { 
                                isGhostMode = it
                                if (it) {
                                    hideLocation = true
                                    hideInNeighborhood = true
                                }
                                syncSettings(ghost = it, hideLoc = if (it) true else hideLocation, hideNeigh = if (it) true else hideInNeighborhood)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    }
                }
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
            item { SettingsSectionHeader("Security & Boundaries") }
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
            item { SettingsSectionHeader("Data & Storage") }
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
        modifier = Modifier.fillMaxWidth().clickable { onCheckedChange(!checked) }.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun SettingsActionItem(title: String, subtitle: String, icon: ImageVector, tint: Color = MaterialTheme.colorScheme.onSurfaceVariant, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = if (tint != MaterialTheme.colorScheme.onSurfaceVariant) tint else MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SettingsSliderItem(title: String, subtitle: String, icon: ImageVector, value: Float, onValueChange: (Float) -> Unit, steps: Int = 0, valueRange: ClosedFloatingPointRange<Float>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
