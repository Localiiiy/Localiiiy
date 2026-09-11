package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.SafetyLogManager
import com.example.util.SafetyLogRecord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafetyLogsViewerSheet(
    onDismiss: () -> Unit,
    onExportCurrentChat: (() -> Unit)? = null,
    activeChatContact: String? = null
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var logsList by remember { mutableStateOf(SafetyLogManager.listSafetyLogs(context)) }
    var selectedLogContent by remember { mutableStateOf<Pair<String, String>?>(null) } // Pair(Title, Text)
    var verificationStatusMap by remember { mutableStateOf<Map<String, Boolean>>(emptyMap()) }

    fun refreshList() {
        logsList = SafetyLogManager.listSafetyLogs(context)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxHeight(0.92f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Safety Log Archive",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        ) {
                            Text(
                                text = "INTERNAL STORAGE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Non-tampered, timestamped digital evidence records (/safety_logs/)",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (onExportCurrentChat != null && activeChatContact != null) {
                    FilledTonalButton(
                        onClick = {
                            onExportCurrentChat()
                            refreshList()
                        },
                        shape = RoundedCornerShape(100.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(imageVector = Icons.Outlined.Shield, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Export Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Standards & Compliance Banner
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Digital Evidence Preservation Standard",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Aligned with ISO/IEC 27037 standards. All exported records are cryptographically sealed with SHA-256 digests and timestamped directly from internal SQLite storage for tamper-proof verification.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (logsList.isEmpty()) {
                // Empty State
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.FolderSpecial,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No Safety Logs in Internal Storage",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "To preserve a secure, tamper-proof record of any chat with an offender or contact, open the chat thread and tap the 'Export Safety Log' shield icon.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "${logsList.size} Sealed Evidence Record${if (logsList.size > 1) "s" else ""}",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(logsList, key = { it.fileName }) { record ->
                        SafetyLogItemCard(
                            record = record,
                            onVerify = {
                                val isValid = SafetyLogManager.verifyIntegrity(context, record.fileName)
                                verificationStatusMap = verificationStatusMap + (record.fileName to isValid)
                                Toast.makeText(
                                    context,
                                    if (isValid) "SHA-256 Match! Record is 100% genuine & non-tampered ✅" else "Hash mismatch! Record altered ❌",
                                    Toast.LENGTH_LONG
                                ).show()
                            },
                            isVerifiedCustom = verificationStatusMap[record.fileName],
                            onViewJson = {
                                val content = SafetyLogManager.readSafetyLogContent(context, record.fileName)
                                selectedLogContent = Pair("Raw Evidence Payload (${record.fileName})", content)
                            },
                            onViewCertificate = {
                                val content = SafetyLogManager.readSafetyLogContent(context, record.certificateFileName)
                                selectedLogContent = Pair("Statutory Evidence Certificate", content)
                            },
                            onShare = {
                                SafetyLogManager.shareSafetyLog(context, record.fileName)
                            },
                            onDelete = {
                                SafetyLogManager.deleteSafetyLog(context, record.fileName)
                                refreshList()
                                Toast.makeText(context, "Log deleted from internal storage", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
            }
        }
    }

    // Detail Inspection Modal
    selectedLogContent?.let { (title, text) ->
        AlertDialog(
            onDismissRequest = { selectedLogContent = null },
            title = {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = text,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 15.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    clipboardManager.setText(AnnotatedString(text))
                    Toast.makeText(context, "Copied to clipboard 📋", Toast.LENGTH_SHORT).show()
                }) {
                    Text("Copy Evidence")
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
private fun SafetyLogItemCard(
    record: SafetyLogRecord,
    onVerify: () -> Unit,
    isVerifiedCustom: Boolean?,
    onViewJson: () -> Unit,
    onViewCertificate: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Subject: @${record.targetUsername}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${record.formattedTimestamp} • ${record.messagesCount} msgs • ${record.fileSizeBytes / 1024} KB",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SHA-256 Digest Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SHA-256: ${record.sha256Checksum.take(16)}...${record.sha256Checksum.takeLast(8)}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )

                    // Verification Pill
                    val isGood = isVerifiedCustom ?: record.isIntegrityVerified
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isGood) Color(0xFF2E7D32).copy(alpha = 0.15f) else MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.clickable { onVerify() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isGood) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (isGood) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isGood) "VERIFIED NON-TAMPERED" else "UNVERIFIED",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isGood) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onViewCertificate,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Article, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Certificate", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onViewJson,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Code, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("JSON Log", fontSize = 11.sp)
                }

                Button(
                    onClick = onShare,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                ) {
                    Icon(imageVector = Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share / Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
