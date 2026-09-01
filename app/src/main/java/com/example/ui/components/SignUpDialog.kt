package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.legal.LegalPolicyRepository
import com.example.ui.theme.LocaliAccentMint
import com.example.ui.theme.LocaliDeepNavy
import com.example.ui.theme.LocaliPrimaryTeal

@Composable
fun SignUpDialog(
    onDismissRequest: () -> Unit,
    onOpenLegalPolicy: () -> Unit,
    onSignUpSuccess: (
        username: String,
        fullName: String,
        avatarUrl: String,
        bio: String,
        neighborhood: String,
        enableLocationRadar: Boolean,
        legalConsentTimestamp: Long
    ) -> Unit
) {
    val avatarPresets = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80"
    )

    var selectedAvatar by remember { mutableStateOf(avatarPresets[0]) }
    var username by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("Local creator & neighborhood explorer 🌿📸") }
    var neighborhood by remember { mutableStateOf("Capitol Hill, Seattle") }
    var enableLocationRadar by remember { mutableStateOf(true) }
    var hasAcceptedTerms by remember { mutableStateOf(false) }
    var hasAcknowledgedMarketplace by remember { mutableStateOf(true) }

    var usernameError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp)
                .testTag("signup_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header with icon and close
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
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = LocaliPrimaryTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = "Create Account",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 19.sp
                                )
                            )
                            Text(
                                text = "Join the verified Localiiiy community",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissRequest,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar Selector
                Text(
                    text = "Choose Profile Avatar",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(avatarPresets) { url ->
                        val isSelected = selectedAvatar == url
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .clickable { selectedAvatar = url }
                                .background(
                                    if (isSelected) LocaliPrimaryTeal.copy(alpha = 0.3f) else Color.Transparent
                                )
                                .padding(3.dp)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(url)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Avatar preset",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(16.dp)
                                        .background(LocaliPrimaryTeal, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Full Name
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        if (it.isNotBlank()) fullNameError = null
                    },
                    label = { Text("Full Name") },
                    placeholder = { Text("e.g., Maya Lin") },
                    isError = fullNameError != null,
                    supportingText = fullNameError?.let { { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_fullname_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Username
                OutlinedTextField(
                    value = username,
                    onValueChange = {
                        val cleaned = it.lowercase().replace(" ", "_").filter { char -> char.isLetterOrDigit() || char == '_' }
                        username = cleaned
                        if (cleaned.isNotBlank()) usernameError = null
                    },
                    label = { Text("Username") },
                    placeholder = { Text("e.g., maya_creates") },
                    prefix = { Text("@") },
                    isError = usernameError != null,
                    supportingText = usernameError?.let { { Text(it) } },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_username_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Neighborhood / City
                OutlinedTextField(
                    value = neighborhood,
                    onValueChange = { neighborhood = it },
                    label = { Text("Neighborhood / Area") },
                    placeholder = { Text("e.g., Capitol Hill, Seattle") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_neighborhood_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Bio
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Sparks") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("signup_bio_input")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Location Radar Consent Option (User Point 1)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { enableLocationRadar = !enableLocationRadar }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = enableLocationRadar,
                            onCheckedChange = { enableLocationRadar = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = LocaliPrimaryTeal
                            ),
                            modifier = Modifier.testTag("signup_location_switch")
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Proximity Radar 📡",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (enableLocationRadar) "Broadcast sparks & discover nearby creators within range" else "Radar disabled. You'll browse random algorithmic local feeds",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // MANDATORY Legal Acknowledgment & Consent Box (Crucial legal requirement!)
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (hasAcceptedTerms) LocaliPrimaryTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                    border = BorderStroke(
                        width = 1.2.dp,
                        color = if (hasAcceptedTerms) LocaliPrimaryTeal.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = hasAcceptedTerms,
                                onCheckedChange = { hasAcceptedTerms = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = LocaliPrimaryTeal,
                                    checkmarkColor = Color.White
                                ),
                                modifier = Modifier.testTag("signup_terms_checkbox")
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mandatory Legal Agreement & Privacy Consent",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    ),
                                    color = if (hasAcceptedTerms) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                )

                                Spacer(modifier = Modifier.height(2.dp))

                                Text(
                                    text = "I acknowledge that I have read, understand, and agree to the Localiiiy App Agreement, Community Charter & Global Privacy Policy (${LegalPolicyRepository.CURRENT_POLICY_VERSION}):",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Legal Highlights Bullet points
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    LegalBulletPoint("📍 Location Sharing is 100% individual choice (Radar on/off anytime; off-grid uses random algorithm).")
                                    LegalBulletPoint("🛡️ Zero Data Selling Guarantee (We never hide or sell data; compliant with global civil privacy laws).")
                                    LegalBulletPoint("🛍️ Peer-to-Peer Marketplace Liability Waiver (No wallet/bank storage; zero developer liability; meet neighbors safely in public).")
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = onOpenLegalPolicy,
                                    shape = RoundedCornerShape(100.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .height(32.dp)
                                        .testTag("signup_read_legal_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Description,
                                        contentDescription = null,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Read Full Legal Agreement & Privacy Policy 📄",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Sign Up Action Button
                Button(
                    onClick = {
                        if (fullName.isBlank()) {
                            fullNameError = "Please enter your name"
                            return@Button
                        }
                        if (username.isBlank()) {
                            usernameError = "Please enter a username"
                            return@Button
                        }
                        if (!hasAcceptedTerms) {
                            return@Button
                        }

                        onSignUpSuccess(
                            username.trim(),
                            fullName.trim(),
                            selectedAvatar,
                            bio.trim(),
                            neighborhood.trim(),
                            enableLocationRadar,
                            System.currentTimeMillis()
                        )
                    },
                    enabled = hasAcceptedTerms && username.isNotBlank() && fullName.isNotBlank(),
                    shape = RoundedCornerShape(100.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LocaliPrimaryTeal,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("signup_submit_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Accept & Create Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.5.sp
                    )
                }

                if (!hasAcceptedTerms) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "⚠️ Please check the legal agreement and privacy policy box above to complete account creation.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.5.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun LegalBulletPoint(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 10.5.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
