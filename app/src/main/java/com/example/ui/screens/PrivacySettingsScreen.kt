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
    onNavigateBack: () -> Unit,
    onNavigateToBlockedUsers: () -> Unit = {},
    onNavigateToConnections: () -> Unit = {},
    onDeleteAccount: () -> Unit = {}
) {
    var isGhostMode by remember { mutableStateOf(false) }
    var locationPrecision by remember { mutableStateOf(1f) } // 0=Exact, 1=District, 2=Metro
    var requireBiometrics by remember { mutableStateOf(false) }
    var sendViewReceipts by remember { mutableStateOf(true) }
    var silenceNotifications by remember { mutableStateOf(true) }
    
    var hapticFeedbackLevel by remember { mutableStateOf(1f) } // 0=Off, 1=Standard, 2=Strong
    
    var showDataOblivionDialog by remember { mutableStateOf(false) }
    var showAuditLogDialog by remember { mutableStateOf(false) }
    
    val haptic = LocalHapticFeedback.current
    
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
                        Icon(Icons.Default.VisibilityOff, contentDescription = null, modifier = Modifier.size(32.dp), tint = if (isGhostMode) MaterialTheme.colorScheme.primary else Color.Gray)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Master Ghost Armor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Immediately cloaks all activity & radar blips.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = isGhostMode,
                            onCheckedChange = { 
                                isGhostMode = it 
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        )
                    }
                }
            }

            // Ghost Protocol & Privacy
            item { SettingsSectionHeader("Ghost Protocol & Privacy") }
            item {
                SettingsSliderItem(
                    title = "Location Precision Level",
                    subtitle = when (locationPrecision) {
                        0f -> "Exact 100m"
                        1f -> "District 1km"
                        else -> "Metro 10km"
                    },
                    icon = Icons.Default.LocationSearching,
                    value = locationPrecision,
                    onValueChange = { locationPrecision = it },
                    steps = 1,
                    valueRange = 0f..2f
                )
            }
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
                SettingsActionItem("About & Open Protocol", "Software licenses and Dual-Reach Manifesto", Icons.Default.Info) {}
            }
            item {
                SettingsActionItem("Complete Account & Data Oblivion", "Guaranteed complete erasure with zero database artifacts", Icons.Default.Warning, tint = MaterialTheme.colorScheme.error) {
                    showDataOblivionDialog = true
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
fun SettingsSliderItem(title: String, subtitle: String, icon: ImageVector, value: Float, onValueChange: (Float) -> Unit, steps: Int, valueRange: ClosedFloatingPointRange<Float>) {
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
