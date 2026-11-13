package com.example.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Badge
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.auth.AuthResult
import com.example.auth.AuthUserState
import com.example.auth.FirebaseAuthService
import com.example.auth.findActivity
import com.example.ui.components.LocaliiiyAppLogoBadge
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.PasswordSecurityHelper
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar

enum class AuthScreenMode {
    LOGIN,
    REGISTER
}

private val registrationCountryCodes = listOf(
    "+91 (IN)", "+1 (US)", "+44 (UK)", "+971 (AE)", "+65 (SG)",
    "+61 (AU)", "+1 (CA)", "+49 (DE)", "+33 (FR)", "+81 (JP)",
    "+82 (KR)", "+880 (BD)", "+92 (PK)", "+94 (LK)", "+977 (NP)",
    "+60 (MY)", "+62 (ID)", "+63 (PH)", "+66 (TH)", "+84 (VN)",
    "+55 (BR)", "+52 (MX)", "+34 (ES)", "+39 (IT)", "+31 (NL)",
    "+27 (ZA)", "+234 (NG)", "+254 (KE)", "+20 (EG)", "+966 (SA)"
)

private val morningInterestConstellation = listOf(
    "Street Art", "Artisan Food", "Photography", "Tech & AI", "Cycling",
    "Live Music", "Urban Gardening", "Pet Neighbors", "Book Swap", "DIY & Tools",
    "Indie Cafes", "Filmmaking", "Civic Watch", "Mutual Aid", "Fitness",
    "Coffee & Hangouts", "Volunteering", "Gaming", "Nature & Parks", "Local Events"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    initialMode: AuthScreenMode = AuthScreenMode.LOGIN,
    securityReason: String? = null,
    invitedByHandle: String? = null,
    onDismiss: () -> Unit,
    onGhostSpectatorSuccess: () -> Unit = {},
    onScrubIdentity: () -> Unit = {},
    onAuthSuccess: (
        user: AuthUserState,
        username: String,
        fullName: String,
        neighborhood: String,
        avatarUrl: String,
        connectedInviter: String?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember(initialMode) { mutableStateOf(initialMode) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Core Sign-In & Registration Credentials
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Profile Photo (Optional gallery upload only — NO Space Profile Photo / Space ID / fake presets)
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) selectedAvatarUri = uri
    }

    // Full Name & @Username
    var fullName by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var isCheckingUsername by remember { mutableStateOf(false) }
    var isUsernameAvailable by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(username, mode) {
        if (mode != AuthScreenMode.REGISTER) return@LaunchedEffect
        val clean = username.trim().removePrefix("@").lowercase()
        if (clean.isBlank()) {
            isUsernameAvailable = null
            usernameError = null
            return@LaunchedEffect
        }
        if (clean.length < 3) {
            isUsernameAvailable = false
            usernameError = "At least 3 characters"
            return@LaunchedEffect
        }
        if (!clean.matches(Regex("^[a-z0-9_.]+$"))) {
            isUsernameAvailable = false
            usernameError = "Letters, numbers, _ and . only"
            return@LaunchedEffect
        }
        isCheckingUsername = true
        delay(350)
        val reserved = setOf("admin", "localiiiy", "root", "support", "official", "moderator", "system")
        val available = withContext(Dispatchers.IO) {
            if (clean in reserved) {
                false
            } else {
                try {
                    val doc = FirebaseAuthService.getFirestore(context)
                        .collection("usernames")
                        .document(clean)
                        .get()
                        .await()
                    !doc.exists()
                } catch (_: Exception) {
                    true
                }
            }
        }
        isCheckingUsername = false
        isUsernameAvailable = available
        usernameError = if (available) null else "@$clean is already taken"
    }

    // Play Store Shared Invitation Link Auto-Connection (ONLY populated via invite link; strictly vacant otherwise)
    var activeInviteHandle by remember(invitedByHandle) {
        mutableStateOf(invitedByHandle?.trim()?.removePrefix("@")?.lowercase()?.takeIf { it.isNotBlank() })
    }

    // Real Firebase Phone SMS OTP Verification (Zero Fake Sandbox OTPs)
    var selectedCountryCode by remember { mutableStateOf(registrationCountryCodes[0]) }
    var countryCodeExpanded by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var isSendingPhoneOtp by remember { mutableStateOf(false) }
    var phoneVerificationId by remember { mutableStateOf<String?>(null) }
    var phoneOtpInput by remember { mutableStateOf("") }
    var verifiedPhoneCredential by remember { mutableStateOf<PhoneAuthCredential?>(null) }
    var isPhoneVerified by remember { mutableStateOf(false) }
    var phoneOtpCountdown by remember { mutableIntStateOf(0) }
    var phoneStatusMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(phoneOtpCountdown) {
        if (phoneOtpCountdown > 0) {
            delay(1000)
            phoneOtpCountdown -= 1
        }
    }

    // Interest Constellation (Restored from Morning Model — Optional)
    var selectedInterests by remember { mutableStateOf(setOf("Street Art", "Artisan Food")) }

    // Neighborhood Visibility ("Show in Area" vs "Hide (Ghost)" Default)
    var startInGhostMode by remember { mutableStateOf(true) }

    // Date of Birth (13+ Age Gate)
    var dobString by remember { mutableStateOf("") }
    var calculatedAge by remember { mutableStateOf<Int?>(null) }
    val minimumAllowedAge = 13
    val isTeenAccount = calculatedAge != null && calculatedAge in 13..17

    LaunchedEffect(calculatedAge) {
        if (isTeenAccount) startInGhostMode = true
    }

    // Omnibus Legal Charter Checkboxes (Restored from Morning Model)
    var isOmnibusExpanded by remember { mutableStateOf(false) }
    var isOmnibusUnlocked by remember { mutableStateOf(true) }
    var chkLegalTerms by remember { mutableStateOf(false) }
    var chkLegalPrivacy by remember { mutableStateOf(false) }
    var chkLegalZeroTolerance by remember { mutableStateOf(false) }
    var showFullLegalModal by remember { mutableStateOf(false) }
    var showFullTermsModal by remember { mutableStateOf(false) }

    // Feedback & Loading
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Forgot Password
    var showForgotPassword by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var forgotStatus by remember { mutableStateOf<String?>(null) }
    var isSendingReset by remember { mutableStateOf(false) }

    val passwordStrength = remember(password) { calculatePasswordStrength(password) }
    val cleanNameLettersOnly = fullName.trim()
    val isNameValid = cleanNameLettersOnly.length >= 3 && cleanNameLettersOnly.matches(Regex("^[A-Za-z ]+$"))
    val isUsernameValid = isUsernameAvailable == true && username.trim().length >= 3
    val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val isPasswordValid = password.length >= 6 && password == confirmPassword
    val isDobValid = dobString.isNotBlank() && (calculatedAge ?: 0) >= minimumAllowedAge
    val allLegalChecked = chkLegalTerms && chkLegalPrivacy && chkLegalZeroTolerance

    val isRegistrationReady = isNameValid &&
        isUsernameValid &&
        isEmailValid &&
        isPasswordValid &&
        isDobValid &&
        allLegalChecked

    if (showFullLegalModal) {
        Dialog(
            onDismissRequest = { showFullLegalModal = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            LegalAgreementScreen(
                currentUsername = username.ifBlank { "new_neighbor" },
                onBackClick = { showFullLegalModal = false },
                onAcceptAgreement = {
                    isOmnibusUnlocked = true
                    chkLegalTerms = true
                    chkLegalPrivacy = true
                    chkLegalZeroTolerance = true
                    showFullLegalModal = false
                }
            )
        }
    }

    if (showFullTermsModal) {
        Dialog(
            onDismissRequest = { showFullTermsModal = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            TermsAndConditionsScreen(
                onNavigateBack = { showFullTermsModal = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LocaliiiyAppLogoBadge(size = 34.dp)
                        Column {
                            Text(
                                text = if (mode == AuthScreenMode.REGISTER) "Create Your Localiiiy Account" else "Sign In to Localiiiy",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.5.sp
                                ),
                                maxLines = 1
                            )
                            Text(
                                text = "Hyperlocal Community • Sovereign Privacy",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("auth_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onScrubIdentity()
                            statusSuccessMessage = "Local session cleared."
                        },
                        modifier = Modifier.testTag("auth_topbar_scrub_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Local Session",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        },
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!securityReason.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LocaliiiyPrimaryTeal.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = LocaliiiyPrimaryTeal, modifier = Modifier.size(18.dp))
                        Text(
                            text = securityReason,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Segmented Sign In / + Register Switcher (Exact Morning Model)
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (mode == AuthScreenMode.LOGIN) LocaliiiyPrimaryTeal else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .clickable {
                                mode = AuthScreenMode.LOGIN
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("auth_mode_login_tab")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Login,
                                contentDescription = null,
                                tint = if (mode == AuthScreenMode.LOGIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                color = if (mode == AuthScreenMode.LOGIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (mode == AuthScreenMode.REGISTER) LocaliiiyAccentMint else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 44.dp)
                            .clickable {
                                mode = AuthScreenMode.REGISTER
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("auth_mode_register_tab")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = if (mode == AuthScreenMode.REGISTER) Color(0xFF042417) else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+ Register",
                                fontWeight = FontWeight.Bold,
                                color = if (mode == AuthScreenMode.REGISTER) Color(0xFF042417) else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.85f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
                        Text(
                            text = errorMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            if (statusSuccessMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = LocaliiiyAccentMint.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = LocaliiiyAccentMint, modifier = Modifier.size(20.dp))
                        Text(
                            text = statusSuccessMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            if (mode == AuthScreenMode.REGISTER) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo (Optional Gallery Upload Only — NO Space Profile Photo, NO Space ID, NO Fake Presets)
                    Text(
                        text = "Profile Photo (Optional)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
                            .border(2.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.85f), CircleShape)
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                            .testTag("avatar_picker_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedAvatarUri != null) {
                            AsyncImage(
                                model = ImageRequest.Builder(context).data(selectedAvatarUri).crossfade(true).build(),
                                contentDescription = "Selected Profile Photo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val initial = fullName.trim().firstOrNull()?.uppercaseChar()?.toString()
                            if (initial != null) {
                                Text(initial, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LocaliiiyPrimaryTeal)
                            } else {
                                Icon(Icons.Outlined.AddAPhoto, contentDescription = "Add Photo", tint = LocaliiiyPrimaryTeal, modifier = Modifier.size(28.dp))
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            shape = RoundedCornerShape(100.dp),
                            border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.6f))
                        ) {
                            Icon(Icons.Outlined.AddAPhoto, contentDescription = null, modifier = Modifier.size(16.dp), tint = LocaliiiyAccentMint)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Upload From Gallery", color = LocaliiiyAccentMint, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        if (selectedAvatarUri != null) {
                            TextButton(onClick = { selectedAvatarUri = null }) {
                                Text("Remove", color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Full Name (Letters and spaces only, min 3 chars)
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { input ->
                            fullName = input
                            fullNameError = when {
                                input.isBlank() -> null
                                input.trim().length < 3 -> "Letters and spaces only (min 3 chars)"
                                !input.trim().matches(Regex("^[A-Za-z ]+$")) -> "Letters and spaces only"
                                else -> null
                            }
                        },
                        label = { Text("Full Name") },
                        leadingIcon = { Icon(Icons.Outlined.Badge, contentDescription = null) },
                        isError = fullNameError != null,
                        supportingText = {
                            Text(
                                text = fullNameError ?: "Letters and spaces only (min 3 chars)",
                                color = if (fullNameError != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_fullname_input")
                    )

                    // Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it.replace("@", "").trim().lowercase() },
                        label = { Text("Username") },
                        placeholder = { Text("your_handle") },
                        leadingIcon = { Text("@", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                        trailingIcon = {
                            when {
                                isCheckingUsername -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                isUsernameAvailable == true -> Icon(Icons.Default.CheckCircle, contentDescription = "Available", tint = LocaliiiyAccentMint)
                                isUsernameAvailable == false -> Icon(Icons.Default.Cancel, contentDescription = "Unavailable", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        isError = usernameError != null,
                        supportingText = {
                            when {
                                usernameError != null -> Text(usernameError!!, color = MaterialTheme.colorScheme.error)
                                isUsernameAvailable == true -> Text("✓ @$username is available!", color = LocaliiiyAccentMint)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_username_input")
                    )

                    // Email Address (Real Firebase Verification Link — NO Fake Sandbox Email OTP)
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) {
                                Text(emailError!!, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text(
                                    "Firebase sends a real verification link to your inbox after account creation.",
                                    fontSize = 11.sp
                                )
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    // Mobile Phone Number (Real Firebase PhoneAuthProvider SMS OTP — Zero Sandbox OTP)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(
                            1.dp,
                            if (isPhoneVerified) LocaliiiyAccentMint else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isPhoneVerified) Icons.Default.Verified else Icons.Outlined.PhoneAndroid,
                                    contentDescription = null,
                                    tint = if (isPhoneVerified) LocaliiiyAccentMint else LocaliiiyPrimaryTeal,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isPhoneVerified) "Mobile Phone Number (Verified via SMS)" else "Mobile Phone Number (Real SMS Verification)",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }

                            if (!isPhoneVerified) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            modifier = Modifier.clickable { countryCodeExpanded = true }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(selectedCountryCode, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                        DropdownMenu(
                                            expanded = countryCodeExpanded,
                                            onDismissRequest = { countryCodeExpanded = false }
                                        ) {
                                            registrationCountryCodes.forEach { code ->
                                                DropdownMenuItem(
                                                    text = { Text(code, fontSize = 13.sp) },
                                                    onClick = {
                                                        selectedCountryCode = code
                                                        countryCodeExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = { phoneNumber = it.filter { ch -> ch.isDigit() } },
                                        label = { Text("Mobile Number") },
                                        placeholder = { Text("9876543210") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    Button(
                                        onClick = {
                                            val digits = phoneNumber.filter { it.isDigit() }
                                            if (digits.length < 7) {
                                                phoneStatusMessage = "Enter a valid mobile number (7+ digits)."
                                                return@Button
                                            }
                                            val activity = context.findActivity()
                                            if (activity == null) {
                                                phoneStatusMessage = "Unable to access Activity for SMS verification."
                                                return@Button
                                            }
                                            val dialCode = selectedCountryCode.substringBefore(" ").trim()
                                            val e164 = "$dialCode$digits"
                                            errorMessage = null
                                            phoneStatusMessage = "Sending real SMS OTP via Firebase..."
                                            isSendingPhoneOtp = true

                                            FirebaseAuthService.verifyPhoneNumber(
                                                activity = activity,
                                                phoneNumberE164 = e164,
                                                onCodeSent = { verId, _ ->
                                                    isSendingPhoneOtp = false
                                                    phoneVerificationId = verId
                                                    phoneOtpCountdown = 60
                                                    phoneStatusMessage = "SMS OTP sent to $e164. Check your messages."
                                                },
                                                onInstantCredential = { cred ->
                                                    isSendingPhoneOtp = false
                                                    verifiedPhoneCredential = cred
                                                    isPhoneVerified = true
                                                    phoneStatusMessage = "Mobile number auto-verified!"
                                                },
                                                onVerificationFailed = { msg ->
                                                    isSendingPhoneOtp = false
                                                    phoneStatusMessage = msg
                                                }
                                            )
                                        },
                                        enabled = !isSendingPhoneOtp && phoneOtpCountdown == 0,
                                        shape = RoundedCornerShape(100.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                                    ) {
                                        if (isSendingPhoneOtp) {
                                            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                        } else {
                                            Text(
                                                text = if (phoneOtpCountdown > 0) "Resend SMS (${phoneOtpCountdown}s)" else "Send SMS OTP",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                if (phoneVerificationId != null) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = phoneOtpInput,
                                            onValueChange = { if (it.length <= 6 && it.all { ch -> ch.isDigit() }) phoneOtpInput = it },
                                            label = { Text("6-Digit SMS OTP") },
                                            placeholder = { Text("Enter 6-digit code") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                val verId = phoneVerificationId ?: return@Button
                                                if (phoneOtpInput.length < 6) {
                                                    phoneStatusMessage = "Enter the 6-digit SMS code."
                                                    return@Button
                                                }
                                                verifiedPhoneCredential = PhoneAuthProvider.getCredential(verId, phoneOtpInput.trim())
                                                isPhoneVerified = true
                                                phoneStatusMessage = "Mobile number verified! It will link on account creation."
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyAccentMint)
                                        ) {
                                            Text("Verify SMS", color = Color(0xFF042417), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            if (!phoneStatusMessage.isNullOrBlank()) {
                                Text(
                                    text = phoneStatusMessage!!,
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = if (isPhoneVerified) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Password & Confirm Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = passwordError?.let { msg -> { Text(msg) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    if (password.isNotEmpty()) {
                        PasswordStrengthMeter(strength = passwordStrength, password = password)
                    }

                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        leadingIcon = { Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = confirmPassword.isNotEmpty() && password != confirmPassword,
                        supportingText = {
                            if (confirmPassword.isNotEmpty() && password != confirmPassword) {
                                Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_confirm_password_input")
                    )

                    // Influencer Invite & Auto-Connection (ONLY when installed/opened via shared Play Store invite link; vacant otherwise)
                    if (!activeInviteHandle.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = LocaliiiyPrimaryTeal.copy(alpha = 0.12f),
                            border = BorderStroke(1.dp, LocaliiiyPrimaryTeal),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("play_invite_connection_card")
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(Icons.Default.PersonAddAlt1, contentDescription = null, tint = LocaliiiyPrimaryTeal, modifier = Modifier.size(20.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Play Store Invite Link • Auto-Connect @$activeInviteHandle",
                                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "You will automatically connect with @$activeInviteHandle after registration.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                IconButton(
                                    onClick = { activeInviteHandle = null },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove invite", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // NOTE: Home Community Anchor intentionally removed per user command.

                    // Interest Constellation (Restored from Morning Model)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.28f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Interest Constellation",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Select neighborhood circles (optional)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                morningInterestConstellation.forEach { topic ->
                                    val isSelected = topic in selectedInterests
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedInterests = if (isSelected) selectedInterests - topic else selectedInterests + topic
                                        },
                                        label = { Text(topic, fontSize = 11.5.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = LocaliiiyPrimaryTeal.copy(alpha = 0.2f),
                                            selectedLabelColor = LocaliiiyAccentMint
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Neighborhood Visibility (Restored from Morning Model: "Show in Area" vs "Hide (Ghost)")
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Neighborhood Visibility",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (!startInGhostMode && !isTeenAccount) LocaliiiyPrimaryTeal.copy(alpha = 0.16f) else Color.Transparent,
                                    border = BorderStroke(
                                        1.dp,
                                        if (!startInGhostMode && !isTeenAccount) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable(enabled = !isTeenAccount) { startInGhostMode = false }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Show in Area", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = LocaliiiyPrimaryTeal)
                                        Text("Visible on local radar", fontSize = 10.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (startInGhostMode || isTeenAccount) LocaliiiyAccentMint.copy(alpha = 0.16f) else Color.Transparent,
                                    border = BorderStroke(
                                        1.dp,
                                        if (startInGhostMode || isTeenAccount) LocaliiiyAccentMint else MaterialTheme.colorScheme.outlineVariant
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { startInGhostMode = true }
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("Hide (Ghost)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp, color = LocaliiiyAccentMint)
                                        Text(
                                            if (isTeenAccount) "Teen Lock (13–17)" else "Active (Default)",
                                            fontSize = 10.5.sp,
                                            color = LocaliiiyAccentMint
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Date of Birth (13+ Required)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = dobString,
                            onValueChange = {
                                dobString = it
                                val parts = it.split("/", "-", ".")
                                if (parts.size == 3) {
                                    val y = parts[2].toIntOrNull()
                                    val cy = Calendar.getInstance().get(Calendar.YEAR)
                                    if (y != null && y in 1920..cy) calculatedAge = cy - y
                                }
                            },
                            label = { Text("Date of Birth (13+)") },
                            placeholder = { Text("DD/MM/YYYY") },
                            isError = calculatedAge != null && calculatedAge!! < minimumAllowedAge,
                            supportingText = {
                                if (calculatedAge != null && calculatedAge!! < minimumAllowedAge) {
                                    Text("Must be at least 13 years old", color = MaterialTheme.colorScheme.error)
                                } else if (isTeenAccount) {
                                    Text("Teen Safety: Ghost Mode enforced", color = LocaliiiyPrimaryTeal)
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        )
                        FilledTonalButton(
                            onClick = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        dobString = String.format("%02d/%02d/%04d", day, month + 1, year)
                                        calculatedAge = Calendar.getInstance().get(Calendar.YEAR) - year
                                    },
                                    cal.get(Calendar.YEAR) - 20,
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(54.dp)
                        ) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick")
                        }
                    }

                    // Mandatory 36-Article Omnibus Legal Charter, NDA, Copyright & Privacy (Restored Morning Model)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Omnibus Legal Charter, NDA, Copyright & Privacy",
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "36 Master Articles • 18+ Radar Lock • LCC SHA-256 • Safe-Harbor",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.5.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                TextButton(onClick = {
                                    isOmnibusUnlocked = true
                                    isOmnibusExpanded = !isOmnibusExpanded
                                }) {
                                    Text(if (isOmnibusExpanded) "Hide" else "Unlock", fontWeight = FontWeight.Bold, color = LocaliiiyAccentMint)
                                }
                            }

                            AnimatedVisibility(visible = isOmnibusExpanded) {
                                Text(
                                    text = "• Ch. 1 (Art. 1–4 & 26): 13+ Minimum Age & Strict 18+ Adult Lock on Live Radar\n" +
                                        "• Ch. 2 (Art. 5–9 & 25): Developer Safe-Harbor (User Controls Radar/Ghost/Location)\n" +
                                        "• Ch. 3 (Art. 10–14): Safe-Haven Handshake Escrow\n" +
                                        "• Ch. 4 (Art. 15–19 & 27): Localiiiy Creator Copyright (LCC SHA-256 + pHash)\n" +
                                        "• Ch. 5 (Art. 20–24 & 28–36): Civic Tools, Anti-Cyberstalking NDA & Erasure (Localiiiy.Helpdesk@gmail.com)",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = "Full Terms ↗",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = LocaliiiyPrimaryTeal,
                                    modifier = Modifier.clickable { showFullTermsModal = true }
                                )
                                Text(
                                    text = "Omnibus Legal Charter & Privacy ↗",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = LocaliiiyPrimaryTeal,
                                    modifier = Modifier.clickable { showFullLegalModal = true }
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { chkLegalTerms = !chkLegalTerms }
                            ) {
                                Checkbox(
                                    checked = chkLegalTerms,
                                    onCheckedChange = { chkLegalTerms = it },
                                    colors = CheckboxDefaults.colors(checkedColor = LocaliiiyAccentMint),
                                    modifier = Modifier.testTag("auth_nda_checkbox_1")
                                )
                                Text(
                                    text = "I agree to the Binding NDA, 18+ Radar Restriction & Developer Non-Liability Safe-Harbor Covenant.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { chkLegalPrivacy = !chkLegalPrivacy }
                            ) {
                                Checkbox(
                                    checked = chkLegalPrivacy,
                                    onCheckedChange = { chkLegalPrivacy = it },
                                    colors = CheckboxDefaults.colors(checkedColor = LocaliiiyAccentMint),
                                    modifier = Modifier.testTag("auth_nda_checkbox_2")
                                )
                                Text(
                                    text = "I acknowledge that Radar ON/OFF, Location ON/OFF, Ghost Mode & Forensic Logs are my own choice and responsibility.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.Top,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { chkLegalZeroTolerance = !chkLegalZeroTolerance }
                            ) {
                                Checkbox(
                                    checked = chkLegalZeroTolerance,
                                    onCheckedChange = { chkLegalZeroTolerance = it },
                                    colors = CheckboxDefaults.colors(checkedColor = LocaliiiyAccentMint),
                                    modifier = Modifier.testTag("auth_nda_checkbox_3")
                                )
                                Text(
                                    text = "I accept all 36 Articles of the Omnibus Terms, Privacy Policy, LCC Copyright & Safe-Haven Charter.",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                    modifier = Modifier.padding(top = 10.dp)
                                )
                            }
                        }
                    }
                }
            } else {
                // SIGN IN MODE (Exact Morning Model — NO Google Sign-In)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email Address or @Username") },
                        placeholder = { Text("Enter your @username or email@domain.com") },
                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                        isError = emailError != null,
                        supportingText = emailError?.let { msg -> { Text(msg) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = passwordError?.let { msg -> { Text(msg) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            showForgotPassword = !showForgotPassword
                            forgotEmail = email
                            forgotStatus = null
                        }) {
                            Text(if (showForgotPassword) "Hide Reset" else "Forgot Password?", color = LocaliiiyPrimaryTeal)
                        }
                    }

                    AnimatedVisibility(
                        visible = showForgotPassword,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Reset Password via Firebase Auth", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                OutlinedTextField(
                                    value = forgotEmail,
                                    onValueChange = { forgotEmail = it },
                                    label = { Text("Registered Email") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (forgotStatus != null) {
                                    Text(forgotStatus!!, style = MaterialTheme.typography.bodySmall, color = LocaliiiyPrimaryTeal)
                                }
                                Button(
                                    onClick = {
                                        isSendingReset = true
                                        coroutineScope.launch {
                                            val res = FirebaseAuthService.sendPasswordResetEmail(forgotEmail)
                                            isSendingReset = false
                                            forgotStatus = res.getOrElse { it.message ?: "Failed to send reset link." }
                                        }
                                    },
                                    enabled = !isSendingReset,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    if (isSendingReset) CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                    else Text("Send Reset Link")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val isActionEnabled = if (mode == AuthScreenMode.LOGIN) !isLoading else !isLoading && isRegistrationReady
            Button(
                onClick = {
                    errorMessage = null
                    statusSuccessMessage = null
                    if (mode == AuthScreenMode.LOGIN) {
                        val rawInput = email.trim()
                        val isEmailFormat = android.util.Patterns.EMAIL_ADDRESS.matcher(rawInput).matches()
                        val isHandleFormat = rawInput.startsWith("@") || (!rawInput.contains("@") && rawInput.length >= 3)
                        if (rawInput.isBlank() || (!isEmailFormat && !isHandleFormat)) {
                            emailError = "Enter a valid email address or @username."
                            return@Button
                        }
                        if (password.isBlank()) {
                            passwordError = "Enter your password."
                            return@Button
                        }

                        isLoading = true
                        coroutineScope.launch {
                            var resolvedEmail = rawInput
                            if (!isEmailFormat) {
                                val cleanHandle = rawInput.removePrefix("@").lowercase()
                                val lookedUp = withContext(Dispatchers.IO) {
                                    try {
                                        val fs = FirebaseAuthService.getFirestore(context)
                                        val doc = fs.collection("usernames").document(cleanHandle).get().await()
                                        doc.getString("email") ?: doc.getString("uid")?.let { uid ->
                                            fs.collection("users").document(uid).get().await().getString("email")
                                        }
                                    } catch (_: Exception) {
                                        null
                                    }
                                }
                                if (!lookedUp.isNullOrBlank()) {
                                    resolvedEmail = lookedUp
                                } else {
                                    isLoading = false
                                    emailError = "Account '@$cleanHandle' not found. Try signing in with your email."
                                    return@launch
                                }
                            }

                            when (val res = FirebaseAuthService.signInWithEmail(resolvedEmail, password)) {
                                is AuthResult.Success -> {
                                    val profileMap = withContext(Dispatchers.IO) {
                                        try {
                                            FirebaseAuthService.getFirestore(context)
                                                .collection("users")
                                                .document(res.user.uid)
                                                .get()
                                                .await()
                                                .data
                                        } catch (_: Exception) {
                                            null
                                        }
                                    }
                                    isLoading = false
                                    val syncedUsername = (profileMap?.get("username") as? String)?.takeIf { it.isNotBlank() }
                                        ?: res.user.displayName
                                        ?: resolvedEmail.substringBefore("@")
                                    val syncedFullName = (profileMap?.get("displayName") as? String)?.takeIf { it.isNotBlank() }
                                        ?: res.user.displayName
                                        ?: syncedUsername
                                    val syncedNeighborhood = (profileMap?.get("neighborhood") as? String).orEmpty()
                                    val syncedAvatar = (profileMap?.get("avatarUrl") as? String).orEmpty()
                                    onAuthSuccess(res.user, syncedUsername, syncedFullName, syncedNeighborhood, syncedAvatar, null)
                                }
                                is AuthResult.Error -> {
                                    isLoading = false
                                    errorMessage = res.errorMessage
                                }
                            }
                        }
                    } else {
                        isLoading = true
                        coroutineScope.launch {
                            when (val res = FirebaseAuthService.signUpWithEmail(email, password, fullName)) {
                                is AuthResult.Success -> {
                                    PasswordSecurityHelper.savePassword(context, password)
                                    verifiedPhoneCredential?.let { cred ->
                                        val linkRes = FirebaseAuthService.linkPhoneCredentialToCurrentUser(cred)
                                        if (linkRes.isFailure) {
                                            Log.w("AuthScreen", "Phone credential link note: ${linkRes.exceptionOrNull()?.message}")
                                        }
                                    }
                                    val cleanHandle = username.trim().removePrefix("@").lowercase()
                                    val cleanEmail = email.trim().lowercase()
                                    val inviter = activeInviteHandle

                                    val firestoreError = withContext(Dispatchers.IO) {
                                        try {
                                            val fs = FirebaseAuthService.getFirestore(context)
                                            val usernameRef = fs.collection("usernames").document(cleanHandle)
                                            val userRef = fs.collection("users").document(res.user.uid)
                                            fs.runTransaction { tx ->
                                                val existing = tx.get(usernameRef)
                                                if (existing.exists()) {
                                                    throw IllegalStateException("Username @$cleanHandle was just taken.")
                                                }
                                                tx.set(
                                                    usernameRef,
                                                    mapOf(
                                                        "uid" to res.user.uid,
                                                        "username" to cleanHandle,
                                                        "email" to cleanEmail,
                                                        "createdAt" to System.currentTimeMillis()
                                                    )
                                                )
                                                tx.set(
                                                    userRef,
                                                    mapOf(
                                                        "uid" to res.user.uid,
                                                        "displayName" to fullName.trim().take(50),
                                                        "fullName" to fullName.trim().take(50),
                                                        "username" to cleanHandle,
                                                        "handle" to "@$cleanHandle",
                                                        "email" to cleanEmail,
                                                        "phone" to if (isPhoneVerified) "${selectedCountryCode.substringBefore(" ")}$phoneNumber" else "",
                                                        "avatarUrl" to (selectedAvatarUri?.toString() ?: ""),
                                                        "interests" to selectedInterests.toList(),
                                                        "visibilityMode" to if (startInGhostMode || isTeenAccount) "GHOST" else "SHOW",
                                                        "ageBracket" to if (isTeenAccount) "13_17_TEEN" else "18_PLUS",
                                                        "isGhostDefault" to (startInGhostMode || isTeenAccount),
                                                        "legalCharterAccepted" to true,
                                                        "platform" to "android",
                                                        "createdAt" to System.currentTimeMillis()
                                                    )
                                                )
                                            }.await()

                                            if (!inviter.isNullOrBlank()) {
                                                fs.collection("users").document(res.user.uid)
                                                    .collection("connections").document(inviter)
                                                    .set(
                                                        mapOf(
                                                            "connectedAt" to System.currentTimeMillis(),
                                                            "source" to "play_store_invite_link",
                                                            "status" to "mutual"
                                                        )
                                                    ).await()
                                            }
                                            null
                                        } catch (e: Exception) {
                                            Log.w("AuthScreen", "Firestore profile sync note: ${e.message}")
                                            e.message
                                        }
                                    }

                                    isLoading = false
                                    if (firestoreError != null && firestoreError.contains("just taken", ignoreCase = true)) {
                                        errorMessage = firestoreError
                                        return@launch
                                    }

                                    onAuthSuccess(
                                        res.user,
                                        cleanHandle,
                                        fullName.trim(),
                                        "",
                                        selectedAvatarUri?.toString() ?: "",
                                        inviter
                                    )
                                }
                                is AuthResult.Error -> {
                                    isLoading = false
                                    errorMessage = res.errorMessage
                                }
                            }
                        }
                    }
                },
                enabled = isActionEnabled,
                shape = RoundedCornerShape(100.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LocaliiiyPrimaryTeal,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag(if (mode == AuthScreenMode.LOGIN) "auth_login_button" else "auth_register_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (mode == AuthScreenMode.LOGIN) "Sign In to Localiiiy" else "Create Account",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Enter as Ghost Spectator (Restored Morning Button)
            OutlinedButton(
                onClick = onGhostSpectatorSuccess,
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.65f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .testTag("top_guest_spectator_button")
            ) {
                Text(
                    text = "Enter as Ghost Spectator • Zero GPS Footprint",
                    color = LocaliiiyAccentMint,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

data class PasswordStrengthInfo(
    val level: Int,
    val label: String,
    val color: Color
)

@Composable
private fun PasswordStrengthMeter(
    strength: PasswordStrengthInfo,
    password: String
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Password Strength", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(strength.label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = strength.color)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (i <= strength.level) strength.color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
                )
            }
        }
    }
}

private fun calculatePasswordStrength(password: String): PasswordStrengthInfo {
    var score = 0
    if (password.length >= 6) score++
    if (password.length >= 10) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() } || (password.any { it.isUpperCase() } && password.any { it.isLowerCase() })) score++
    return when {
        score <= 1 -> PasswordStrengthInfo(1, "Weak", Color(0xFFEF4444))
        score == 2 -> PasswordStrengthInfo(2, "Moderate", Color(0xFFF59E0B))
        score == 3 -> PasswordStrengthInfo(3, "Good", Color(0xFF10B981))
        else -> PasswordStrengthInfo(4, "Strong", Color(0xFF0D9488))
    }
}
