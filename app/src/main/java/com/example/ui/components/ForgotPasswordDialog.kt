package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text("Reset Password", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (successMessage != null) {
                    Text(
                        text = successMessage!!,
                        color = LocaliiiyPrimaryTeal,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = "Enter the email address associated with your account, and we'll send you a secure link to reset your password via Firebase.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; errorMessage = null },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        isError = errorMessage != null,
                        supportingText = { if (errorMessage != null) Text(errorMessage!!) }
                    )
                }
            }
        },
        confirmButton = {
            if (successMessage == null) {
                Button(
                    onClick = {
                        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                            errorMessage = "Please enter a valid email address"
                            return@Button
                        }
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
                                successMessage = "Password reset link sent to $email."
                                isLoading = false
                            } catch (e: Exception) {
                                errorMessage = e.message ?: "Failed to send reset email. Please try again."
                                isLoading = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text("Send Reset Link")
                    }
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                ) {
                    Text("OK")
                }
            }
        },
        dismissButton = {
            if (successMessage == null && !isLoading) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
