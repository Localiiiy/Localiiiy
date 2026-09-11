package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.PasswordSecurityHelper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onChangeSuccess: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showOldPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val newReq = remember(newPassword) {
        PasswordSecurityHelper.checkRequirements(newPassword)
    }
    val passwordsMatch = remember(newPassword, confirmPassword) {
        confirmPassword.isNotEmpty() && newPassword == confirmPassword
    }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "Change Password",
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Update old credentials to new",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (successMessage != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = LocaliiiyAccentMint.copy(alpha = 0.15f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = LocaliiiyAccentMint,
                                modifier = Modifier.size(22.dp)
                            )
                            Text(
                                text = successMessage!!,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                } else {
                    Text(
                        text = "Enter your current password followed by your new password containing numbers and special characters.",
                        fontSize = 12.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 17.sp
                    )

                    // 1. Current / Old Password
                    OutlinedTextField(
                        value = oldPassword,
                        onValueChange = {
                            oldPassword = it
                            errorMessage = null
                        },
                        label = { Text("Current (Old) Password") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("change_password_old_field"),
                        leadingIcon = {
                            Icon(Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showOldPassword = !showOldPassword }) {
                                Icon(
                                    imageVector = if (showOldPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showOldPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (showOldPassword) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    // 2. New Password
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = {
                            newPassword = it
                            errorMessage = null
                        },
                        label = { Text("New Password") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("change_password_new_field"),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(20.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { showNewPassword = !showNewPassword }) {
                                Icon(
                                    imageVector = if (showNewPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showNewPassword) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (showNewPassword) VisualTransformation.None else PasswordVisualTransformation()
                    )

                    // 3. Confirm New Password
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            errorMessage = null
                        },
                        label = { Text("Confirm New Password") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("change_password_confirm_field"),
                        leadingIcon = {
                            Icon(Icons.Default.LockClock, contentDescription = null, modifier = Modifier.size(20.dp))
                        },
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

                    // Requirements Checklist
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "Password Requirements:",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            RequirementItem(
                                text = "At least 8 characters",
                                isMet = newReq.hasMinLength
                            )
                            RequirementItem(
                                text = "Includes at least one numeric digit (0-9)",
                                isMet = newReq.hasNumeric
                            )
                            RequirementItem(
                                text = "Includes at least one special character (!@#$%...)",
                                isMet = newReq.hasSpecialChar
                            )
                            if (confirmPassword.isNotEmpty()) {
                                RequirementItem(
                                    text = "Passwords match",
                                    isMet = passwordsMatch
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (successMessage != null) {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                ) {
                    Text("Done")
                }
            } else {
                Button(
                    onClick = {
                        if (oldPassword.isBlank()) {
                            errorMessage = "Please enter your current (old) password."
                            return@Button
                        }
                        if (!PasswordSecurityHelper.verifyOldPassword(context, oldPassword.trim())) {
                            errorMessage = "Current (old) password does not match our records."
                            return@Button
                        }
                        val validation = PasswordSecurityHelper.checkRequirements(newPassword)
                        if (!validation.isValid) {
                            errorMessage = validation.errorMessage ?: "New password does not meet requirements."
                            return@Button
                        }
                        if (oldPassword.trim() == newPassword.trim()) {
                            errorMessage = "New password cannot be the same as your old password."
                            return@Button
                        }
                        if (newPassword != confirmPassword) {
                            errorMessage = "New password and confirmation do not match."
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch {
                            val (success, message) = PasswordSecurityHelper.changePassword(
                                context = context,
                                oldPassword = oldPassword.trim(),
                                newPassword = newPassword.trim()
                            )
                            if (success) {
                                // Try updating Firebase password if logged in
                                try {
                                    FirebaseAuth.getInstance().currentUser?.updatePassword(newPassword.trim())?.await()
                                } catch (_: Exception) {}

                                successMessage = message
                                isLoading = false
                                onChangeSuccess(message)
                            } else {
                                errorMessage = message
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                    enabled = !isLoading && oldPassword.isNotBlank() && newPassword.isNotBlank() && confirmPassword.isNotBlank(),
                    modifier = Modifier.testTag("change_password_submit_button")
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Update Password", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        dismissButton = {
            if (successMessage == null) {
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading
                ) {
                    Text("Cancel")
                }
            }
        }
    )
}

@Composable
private fun RequirementItem(text: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = if (isMet) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (isMet) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = text,
            fontSize = 11.sp,
            color = if (isMet) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
