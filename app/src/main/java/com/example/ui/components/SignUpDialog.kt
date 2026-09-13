package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.legal.LegalPolicyRepository
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.PasswordSecurityHelper

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
        legalConsentTimestamp: Long,
        password: String
    ) -> Unit
) {
    val avatarPresets = listOf(
        "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
        "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=500&auto=format&fit=crop&q=80"
    )

    var step by remember { mutableStateOf(1) } // 1: Info, 2: OTP, 3: Profile

    // Step 1 State
    var email by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    
    val passwordReq = remember(password) {
        PasswordSecurityHelper.checkRequirements(password)
    }
    val passwordsMatch = remember(password, confirmPassword) {
        confirmPassword.isNotEmpty() && password == confirmPassword
    }
    
    // Step 2 State
    var otp by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf<String?>(null) }

    // Step 3 State
    var selectedAvatar by remember { mutableStateOf(avatarPresets[0]) }
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var bio by remember { mutableStateOf("Local creator & neighborhood explorer 🌿📸") }
    var neighborhood by remember { mutableStateOf("Capitol Hill, Seattle") }
    var enableLocationRadar by remember { mutableStateOf(true) }
    var hasAcceptedNDA by remember { mutableStateOf(false) }
    var hasAcceptedLawDisclosure by remember { mutableStateOf(false) }
    var hasVerifiedAge by remember { mutableStateOf(false) }
    var showFullNdaDialog by remember { mutableStateOf(false) }

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
                            color = LocaliiiyPrimaryTeal.copy(alpha = 0.18f),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = LocaliiiyPrimaryTeal,
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
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Step $step of 3",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismissRequest) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                if (step == 1) {
                    // Step 1: Basic Info
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it; fullNameError = null },
                        label = { Text("Full Name") },
                        isError = fullNameError != null,
                        supportingText = { if (fullNameError != null) Text(fullNameError!!) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Address (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        label = { Text("Password *") },
                        singleLine = true,
                        isError = passwordError != null,
                        supportingText = { if (passwordError != null) Text(passwordError!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_password_field"),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = { Text("Confirm Password *") },
                        singleLine = true,
                        isError = confirmPasswordError != null,
                        supportingText = { if (confirmPasswordError != null) Text(confirmPasswordError!!) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_confirm_password_field"),
                        leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    imageVector = if (showConfirmPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showConfirmPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Password Requirements Status
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Password Requirements (Special character & Numeric):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordReq.hasMinLength) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (passwordReq.hasMinLength) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "At least 8 characters",
                                    fontSize = 10.5.sp,
                                    color = if (passwordReq.hasMinLength) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordReq.hasNumeric) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (passwordReq.hasNumeric) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Includes at least one numeric digit (0-9)",
                                    fontSize = 10.5.sp,
                                    color = if (passwordReq.hasNumeric) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (passwordReq.hasSpecialChar) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (passwordReq.hasSpecialChar) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = "Includes at least one special character (!@#\$%^&*...)",
                                    fontSize = 10.5.sp,
                                    color = if (passwordReq.hasSpecialChar) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (confirmPassword.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = if (passwordsMatch) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                        contentDescription = null,
                                        tint = if (passwordsMatch) LocaliiiyAccentMint else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (passwordsMatch) "Passwords match" else "Passwords do not match",
                                        fontSize = 10.5.sp,
                                        color = if (passwordsMatch) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (fullName.isBlank()) {
                                fullNameError = "Please enter your full name"
                                return@Button
                            }
                            if (mobile.isBlank() && email.isBlank()) {
                                fullNameError = "Please enter mobile or email"
                                return@Button
                            }
                            val status = PasswordSecurityHelper.checkRequirements(password)
                            if (!status.isValid) {
                                passwordError = status.errorMessage
                                return@Button
                            }
                            if (password != confirmPassword) {
                                confirmPasswordError = "Passwords do not match"
                                return@Button
                            }
                            step = 2
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                    ) {
                        Text("Send OTP", fontWeight = FontWeight.Bold)
                    }
                } else if (step == 2) {
                    // Step 2: OTP
                    Text(
                        text = "Verification Code",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "We sent a code to ${if(mobile.isNotBlank()) mobile else email}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = otp,
                        onValueChange = { otp = it; otpError = null },
                        label = { Text("Enter OTP") },
                        isError = otpError != null,
                        supportingText = { if (otpError != null) Text(otpError!!) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { step = 1 },
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("Back")
                        }
                        Button(
                            onClick = {
                                if (otp.length >= 4) {
                                    step = 3
                                } else {
                                    otpError = "Invalid OTP"
                                }
                            },
                            modifier = Modifier.weight(1f).height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                        ) {
                            Text("Verify", fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    // Step 3: Profile Setup
                    Text("Select Avatar", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(avatarPresets) { avatar ->
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(avatar)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = "Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .clickable { selectedAvatar = avatar }
                                    .border(
                                        width = if (selectedAvatar == avatar) 3.dp else 0.dp,
                                        color = if (selectedAvatar == avatar) LocaliiiyPrimaryTeal else Color.Transparent,
                                        shape = CircleShape
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it; usernameError = null },
                        label = { Text("Username") },
                        isError = usernameError != null,
                        supportingText = { if (usernameError != null) Text(usernameError!!) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Text("@", modifier = Modifier.padding(start = 12.dp)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = neighborhood,
                        onValueChange = { neighborhood = it },
                        label = { Text("Neighborhood") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) }
                    )
                    
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    // Mandatory NDA & Confidentiality Agreement Card
                    val isAllLegalAccepted = hasAcceptedNDA && hasAcceptedLawDisclosure && hasVerifiedAge
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isAllLegalAccepted) LocaliiiyPrimaryTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                        border = BorderStroke(
                            width = 1.2.dp,
                            color = if (isAllLegalAccepted) LocaliiiyPrimaryTeal.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("signup_nda_agreement_card")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Gavel,
                                    contentDescription = null,
                                    tint = if (isAllLegalAccepted) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Mandatory Legal & Age Verification",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.5.sp
                                    ),
                                    color = if (isAllLegalAccepted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Age Gate Verification Checkbox
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { hasVerifiedAge = !hasVerifiedAge }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = hasVerifiedAge,
                                    onCheckedChange = { hasVerifiedAge = it },
                                    colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                    modifier = Modifier.testTag("signup_age_checkbox")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "I verify that I am at least 18 years of age. I understand this app may contain user-generated content strictly for adults.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            // Crime Deterrence Legal Notice Box
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "⚖️ Strict Anti-Cybercrime & Legal Deterrence:",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "• Stalking neighbours is a severe cyber crime under municipal and international law.\n• You agree never to use this app for illegal activities, stalking, or crimes.\n• If found, all details, audit logs, GPS timestamps, and records will be immediately preserved and shared with concerned law enforcement authorities for investigation and are fully valid as evidence in a court of law.\n• Violations carry severe punishment as per the penal laws of your country. We are firm about the law and respect law and order globally.",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontSize = 10.5.sp,
                                            lineHeight = 15.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Acknowledgment Checkbox 1: Stalking & Illegal Activities
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { hasAcceptedNDA = !hasAcceptedNDA }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = hasAcceptedNDA,
                                    onCheckedChange = { hasAcceptedNDA = it },
                                    colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                    modifier = Modifier.testTag("signup_nda_checkbox_1")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "I agree not to stalk neighbours as it is a recognized cyber crime, and never use Localiiiy for illegal activities or crime.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            // Acknowledgment Checkbox 2: Law Enforcement Sharing & Court Admissibility
                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { hasAcceptedLawDisclosure = !hasAcceptedLawDisclosure }
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = hasAcceptedLawDisclosure,
                                    onCheckedChange = { hasAcceptedLawDisclosure = it },
                                    colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                    modifier = Modifier.testTag("signup_nda_checkbox_2")
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "I acknowledge that any illegal conduct or cyberstalking logs will be shared with concerned law enforcement as per law for investigation, valid in court with severe punishment under the laws of my country.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedButton(
                                onClick = onOpenLegalPolicy,
                                shape = RoundedCornerShape(100.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(34.dp)
                                    .testTag("signup_read_nda_button")
                            ) {
                                Icon(Icons.Outlined.Article, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Read Full NDA & Legal Agreement (Sec 10.0) 📜", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            if (username.isBlank()) {
                                usernameError = "Please enter a username"
                                return@Button
                            }
                            if (!isAllLegalAccepted) {
                                return@Button
                            }
                            onSignUpSuccess(
                                username.trim(),
                                fullName.trim(),
                                selectedAvatar,
                                bio.trim(),
                                neighborhood.trim(),
                                enableLocationRadar,
                                System.currentTimeMillis(),
                                password.trim()
                            )
                        },
                        enabled = isAllLegalAccepted && username.isNotBlank(),
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("signup_create_account_button")
                    ) {
                        Text("Create Account & Sign NDA", fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                    }
                }
            }
        }
    }
}
