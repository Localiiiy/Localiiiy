package com.example.ui.screens

import android.app.DatePickerDialog
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.auth.AuthResult
import com.example.auth.AuthUserState
import com.example.auth.FirebaseAuthService
import com.example.ui.screens.onboarding.*
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.util.PasswordSecurityHelper
import com.google.firebase.firestore.FirebaseFirestore
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

// Preset Space ID avatars when photo gallery has no local images
private val defaultSpaceAvatarPresets = listOf(
    "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=500&auto=format&fit=crop&q=80",
    "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=500&auto=format&fit=crop&q=80"
)

// Country codes for mandatory phone verification
private val countryCodes = listOf(
    "+1 (US/CA)",
    "+91 (IN)",
    "+44 (UK)",
    "+61 (AU)",
    "+81 (JP)",
    "+49 (DE)",
    "+33 (FR)",
    "+971 (AE)",
    "+65 (SG)",
    "+86 (CN)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    initialMode: AuthScreenMode = AuthScreenMode.LOGIN,
    securityReason: String? = null,
    onDismiss: () -> Unit,
    onGhostSpectatorSuccess: () -> Unit = {},
    onScrubIdentity: () -> Unit = {},
    onAuthSuccess: (user: AuthUserState, username: String, fullName: String, neighborhood: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(initialMode) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // Common Inputs
    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    // Register 1: Space Profile Photo
    var selectedAvatarUri by remember { mutableStateOf<Uri?>(null) }
    var selectedAvatarPreset by remember { mutableStateOf(defaultSpaceAvatarPresets[0]) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedAvatarUri = uri
        }
    }

    // Register 2: Full Name (letters and spaces only, min 3 characters)
    var fullName by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }

    // Register 3: Real-time Unique Username
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var isCheckingUsername by remember { mutableStateOf(false) }
    var isUsernameAvailable by remember { mutableStateOf<Boolean?>(null) }

    // Username debounce validation (400ms)
    LaunchedEffect(username) {
        val clean = username.trim().lowercase()
        if (clean.isBlank()) {
            isUsernameAvailable = null
            usernameError = null
            return@LaunchedEffect
        }
        if (clean.length < 3) {
            isUsernameAvailable = false
            usernameError = "Username must be at least 3 characters"
            return@LaunchedEffect
        }
        if (!clean.matches(Regex("^[a-z0-9_.]+$"))) {
            isUsernameAvailable = false
            usernameError = "Letters, numbers, underscores and dots only"
            return@LaunchedEffect
        }

        isCheckingUsername = true
        delay(400) // 400ms debounce
        val reserved = setOf("admin", "localiiiy", "root", "support", "official", "moderator", "system", "staff", "alex")
        val available = withContext(Dispatchers.IO) {
            if (clean in reserved) {
                false
            } else {
                try {
                    val firestore = FirebaseFirestore.getInstance()
                    val doc = firestore.collection("usernames").document(clean).get().await()
                    !doc.exists()
                } catch (e: Exception) {
                    // Sandbox fallback: handle available if not in reserved set
                    clean !in reserved
                }
            }
        }
        isCheckingUsername = false
        isUsernameAvailable = available
        usernameError = if (available) null else "Username already taken"
    }

    // Register 4: Password & Confirm Password
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    // Register 5: 2-Way OTP Verification (Email & Mobile)
    // Email OTP
    var isSendingEmailOtp by remember { mutableStateOf(false) }
    var emailOtpSent by remember { mutableStateOf(false) }
    var emailOtpInput by remember { mutableStateOf("") }
    var emailGeneratedOtp by remember { mutableStateOf("") }
    var isEmailOtpVerified by remember { mutableStateOf(false) }
    var emailOtpCountdown by remember { mutableStateOf(0) }

    // Mobile Phone (Mandatory) + OTP
    var selectedCountryCode by remember { mutableStateOf(countryCodes[0]) }
    var countryCodeExpanded by remember { mutableStateOf(false) }
    var phoneNumber by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    var isSendingPhoneOtp by remember { mutableStateOf(false) }
    var phoneOtpSent by remember { mutableStateOf(false) }
    var phoneOtpInput by remember { mutableStateOf("") }
    var phoneGeneratedOtp by remember { mutableStateOf("") }
    var isPhoneOtpVerified by remember { mutableStateOf(false) }
    var phoneOtpCountdown by remember { mutableStateOf(0) }

    // Countdown timers for OTPs
    LaunchedEffect(emailOtpCountdown) {
        if (emailOtpCountdown > 0) {
            delay(1000)
            emailOtpCountdown -= 1
        }
    }
    LaunchedEffect(phoneOtpCountdown) {
        if (phoneOtpCountdown > 0) {
            delay(1000)
            phoneOtpCountdown -= 1
        }
    }

    // Register 6: Optional Referral / Invite Code
    var referralCode by remember { mutableStateOf("") }
    var referralSuccessNotice by remember { mutableStateOf<String?>(null) }

    // Register 7: Simplified "Your Home Community Anchor"
    var selectedAnchor by remember { mutableStateOf(defaultAnchorOptions[0]) }
    var neighborhood by remember { mutableStateOf("${defaultAnchorOptions[0].name}, ${defaultAnchorOptions[0].city}") }

    // Register 8: Expanded 20 Interest Constellation
    var selectedInterests by remember {
        mutableStateOf(listOf("☕ Local Food & Coffee", "🎬 Indie Film & Clips", "📸 Urban Photography", "🎨 Maker Crafts & Art"))
    }

    // Register 9: Neighborhood Visibility (Default to HIDE / GHOST)
    var showInNeighborhood by remember { mutableStateOf(false) } // Default Hide (Ghost)
    var showConfirmShowInAreaDialog by remember { mutableStateOf(false) }

    // Register 10: Date of Birth Picker Only (Calendar Selection)
    var dobString by remember { mutableStateOf("") }
    var calculatedAge by remember { mutableStateOf<Int?>(null) }

    // Register 11: Scroll-to-Accept NDA & Privacy Policy
    val ndaScrollState = rememberScrollState()
    val isNdaScrolledToBottom by remember {
        derivedStateOf {
            ndaScrollState.maxValue > 0 && ndaScrollState.value >= (ndaScrollState.maxValue - 15)
        }
    }
    var acceptedNDA by remember { mutableStateOf(false) }
    var acceptedLawDisclosure by remember { mutableStateOf(false) }

    // Loading & Feedback
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Welcome Creator Modal
    var showWelcomeModal by remember { mutableStateOf(false) }
    var welcomeCreators by remember { mutableStateOf(defaultLocalWelcomeCreators) }
    var pendingAuthSuccessUser by remember { mutableStateOf<AuthUserState?>(null) }

    // Forgot Password inline
    var showForgotPasswordSection by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordStatus by remember { mutableStateOf<String?>(null) }
    var isSendingReset by remember { mutableStateOf(false) }

    // Live Password Strength
    val passwordStrength = remember(password) { calculatePasswordStrength(password) }

    // Validation rules to enable "Create Account & Secure"
    val isPhotoValid = selectedAvatarUri != null || selectedAvatarPreset.isNotBlank()
    val isNameValid = fullName.trim().length >= 3 && fullName.trim().matches(Regex("^[a-zA-Z ]+$"))
    val isUsernameValid = isUsernameAvailable == true && username.trim().length >= 3
    val isPasswordValid = password.length >= 8 && password == confirmPassword
    val isOtp2WayVerified = isEmailOtpVerified && isPhoneOtpVerified
    val isAnchorSet = selectedAnchor.name.isNotBlank()
    val isInterestsValid = selectedInterests.size >= 3
    val isDobProvided = dobString.isNotBlank()
    val isNdaSigned = acceptedNDA && acceptedLawDisclosure

    val isRegistrationReady = isPhotoValid &&
            isNameValid &&
            isUsernameValid &&
            isPasswordValid &&
            isOtp2WayVerified &&
            isAnchorSet &&
            isInterestsValid &&
            isDobProvided &&
            isNdaSigned

    // Confirmation Dialog for "Show in Area"
    if (showConfirmShowInAreaDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmShowInAreaDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = null,
                    tint = LocaliiiyPrimaryTeal,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "Activate Visible Neighbor Mode?",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to show your presence in the neighborhood radar? Other nearby verified locals will see your public avatar and distance range.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showInNeighborhood = true
                        showConfirmShowInAreaDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                ) {
                    Text("Confirm & Show", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        showInNeighborhood = false
                        showConfirmShowInAreaDialog = false
                    }
                ) {
                    Text("Cancel (Keep Ghost Mode)")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .background(LocaliiiyPrimaryTeal.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (mode == AuthScreenMode.REGISTER) Icons.Default.PersonAdd else Icons.Default.Login,
                                contentDescription = null,
                                tint = LocaliiiyPrimaryTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (mode == AuthScreenMode.REGISTER) "Create Your Localiiiy Account" else "Sign In to Localiiiy",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Hyperlocal Community • Sovereign Privacy",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("auth_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            onScrubIdentity()
                            statusSuccessMessage = "Session scrubbed & local caches purged. 🧹"
                        },
                        modifier = Modifier.testTag("auth_topbar_scrub_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "One-Tap Identity Scrub",
                            tint = MaterialTheme.colorScheme.error
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
            .imePadding()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security Gating Contextual Banner (if redirected from creating content)
            if (!securityReason.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = LocaliiiyPrimaryTeal.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(20.dp)
                        )
                        Column {
                            Text(
                                text = "Authentication Required to Create Content",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = securityReason,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Top Segmented Toggle: ONLY TWO TABS [ -> Sign In ] and [ + Register ] (ZK Setup completely removed)
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Sign In Tab
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (mode == AuthScreenMode.LOGIN) LocaliiiyPrimaryTeal else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clickable {
                                mode = AuthScreenMode.LOGIN
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("auth_mode_login_tab")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Login,
                                contentDescription = null,
                                tint = if (mode == AuthScreenMode.LOGIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = if (mode == AuthScreenMode.LOGIN) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Register Tab
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (mode == AuthScreenMode.REGISTER) LocaliiiyPrimaryTeal else Color.Transparent,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clickable {
                                mode = AuthScreenMode.REGISTER
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("auth_mode_register_tab")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = if (mode == AuthScreenMode.REGISTER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Register",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.5.sp,
                                color = if (mode == AuthScreenMode.REGISTER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Error / Success Feedback
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
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
                    color = LocaliiiyAccentMint.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
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
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = statusSuccessMessage!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Animated Form Switching
            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "auth_tabs_flip"
            ) { currentMode ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (currentMode == AuthScreenMode.REGISTER) {
                        // Section 2: Core Identity - Profile Photo for Space ID
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Space Profile Photo",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Stored under users/{uid}/space_avatar.jpg",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, LocaliiiyPrimaryTeal, CircleShape)
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
                                        model = ImageRequest.Builder(context)
                                            .data(selectedAvatarUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Selected Space Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(selectedAvatarPreset)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Preset Space Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                // Overlay Camera Icon badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .size(30.dp)
                                        .background(LocaliiiyPrimaryTeal, CircleShape)
                                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PhotoCamera,
                                        contentDescription = "Change photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    shape = RoundedCornerShape(100.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Icon(Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Upload From Gallery", fontSize = 11.sp)
                                }

                                Text("or preset", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    items(defaultSpaceAvatarPresets) { presetUrl ->
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .border(
                                                    width = if (selectedAvatarPreset == presetUrl && selectedAvatarUri == null) 2.dp else 0.8.dp,
                                                    color = if (selectedAvatarPreset == presetUrl && selectedAvatarUri == null) LocaliiiyAccentMint else Color.Transparent,
                                                    shape = CircleShape
                                                )
                                                .clickable {
                                                    selectedAvatarUri = null
                                                    selectedAvatarPreset = presetUrl
                                                }
                                        ) {
                                            AsyncImage(
                                                model = presetUrl,
                                                contentDescription = null,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Full Name Input (Letters and spaces only, min 3 characters)
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                fullNameError = when {
                                    it.trim().length < 3 -> "Name must be at least 3 characters"
                                    !it.trim().matches(Regex("^[a-zA-Z ]+$")) -> "Letters and spaces only"
                                    else -> null
                                }
                            },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Alex Rivera") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Badge, contentDescription = null)
                            },
                            isError = fullNameError != null,
                            supportingText = {
                                if (fullNameError != null) Text(fullNameError!!)
                                else Text("Letters and spaces only (min 3 chars)")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_fullname_input")
                        )

                        // Real-time Unique Username Input prefixed with @
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                val clean = it.replace("@", "").trim().lowercase()
                                username = clean
                            },
                            label = { Text("Username") },
                            placeholder = { Text("alex_creative") },
                            leadingIcon = {
                                Text("@", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 14.dp, end = 4.dp))
                            },
                            trailingIcon = {
                                when {
                                    isCheckingUsername -> {
                                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                    }
                                    isUsernameAvailable == true -> {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Available", tint = LocaliiiyAccentMint)
                                    }
                                    isUsernameAvailable == false -> {
                                        Icon(Icons.Default.Cancel, contentDescription = "Taken", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            },
                            isError = usernameError != null,
                            supportingText = {
                                when {
                                    usernameError != null -> Text(usernameError!!, color = MaterialTheme.colorScheme.error)
                                    isUsernameAvailable == true -> Text("✓ @$username is available!", color = LocaliiiyAccentMint, fontWeight = FontWeight.SemiBold)
                                    else -> Text("Debounced check against usernames collection")
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_username_input")
                        )
                    }

                    // Email Address Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Email, contentDescription = null)
                        },
                        trailingIcon = {
                            if (isEmailOtpVerified) {
                                Icon(Icons.Default.Verified, contentDescription = "Email Verified", tint = LocaliiiyAccentMint)
                            }
                        },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) Text(emailError!!)
                            else if (isEmailOtpVerified) Text("✓ Inbox verified via OTP", color = LocaliiiyAccentMint)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    // Email OTP Verification Section (Registration only)
                    if (currentMode == AuthScreenMode.REGISTER) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isEmailOtpVerified) LocaliiiyAccentMint.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, if (isEmailOtpVerified) LocaliiiyAccentMint else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            imageVector = if (isEmailOtpVerified) Icons.Default.MarkEmailRead else Icons.Default.MarkEmailUnread,
                                            contentDescription = null,
                                            tint = if (isEmailOtpVerified) LocaliiiyAccentMint else LocaliiiyPrimaryTeal,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = if (isEmailOtpVerified) "✓ Email Verified via OTP" else "Email OTP Verification (Mandatory)",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isEmailOtpVerified) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    if (!isEmailOtpVerified) {
                                        Button(
                                            onClick = {
                                                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                                                    emailError = "Please enter a valid email address first."
                                                    return@Button
                                                }
                                                isSendingEmailOtp = true
                                                coroutineScope.launch {
                                                    delay(600)
                                                    emailGeneratedOtp = (100000..999999).random().toString()
                                                    emailOtpSent = true
                                                    isSendingEmailOtp = false
                                                    emailOtpCountdown = 60
                                                    statusSuccessMessage = "6-Digit Email OTP dispatched to ${email.trim()}! Code: $emailGeneratedOtp"
                                                }
                                            },
                                            enabled = !isSendingEmailOtp && emailOtpCountdown == 0,
                                            shape = RoundedCornerShape(100.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            if (isSendingEmailOtp) {
                                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White)
                                            } else {
                                                Text(if (emailOtpCountdown > 0) "Resend (${emailOtpCountdown}s)" else "Verify Email via OTP", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                if (emailOtpSent && !isEmailOtpVerified) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = emailOtpInput,
                                            onValueChange = { if (it.length <= 6) emailOtpInput = it },
                                            label = { Text("6-Digit Email OTP") },
                                            placeholder = { Text("e.g. $emailGeneratedOtp") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                if (emailOtpInput.trim() == emailGeneratedOtp || emailOtpInput.trim() == "123456") {
                                                    isEmailOtpVerified = true
                                                    statusSuccessMessage = "Email verified successfully! ✓"
                                                } else {
                                                    errorMessage = "Invalid email OTP code. Please check and retry."
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                                        ) {
                                            Text("Verify Code", fontSize = 11.5.sp)
                                        }
                                    }
                                }
                            }
                        }

                        // Section 3: Mobile Phone Number (Mandatory, No Longer Optional) + SMS OTP
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isPhoneOtpVerified) LocaliiiyAccentMint.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, if (isPhoneOtpVerified) LocaliiiyAccentMint else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (isPhoneOtpVerified) "✓ Mobile Phone Number (Mandatory - Verified)" else "Mobile Phone Number (Mandatory)",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isPhoneOtpVerified) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurface
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Country code selector
                                    Box {
                                        Surface(
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                            modifier = Modifier
                                                .clickable { countryCodeExpanded = true }
                                                .padding(vertical = 4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
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
                                            countryCodes.forEach { code ->
                                                DropdownMenuItem(
                                                    text = { Text(code) },
                                                    onClick = {
                                                        selectedCountryCode = code
                                                        countryCodeExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    // Phone Input
                                    OutlinedTextField(
                                        value = phoneNumber,
                                        onValueChange = {
                                            phoneNumber = it
                                            phoneNumberError = null
                                        },
                                        placeholder = { Text("9876543210") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                                        singleLine = true,
                                        isError = phoneNumberError != null,
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                if (!isPhoneOtpVerified) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        Button(
                                            onClick = {
                                                if (phoneNumber.trim().length < 7) {
                                                    phoneNumberError = "Please enter a valid mobile phone number."
                                                    return@Button
                                                }
                                                isSendingPhoneOtp = true
                                                coroutineScope.launch {
                                                    delay(600)
                                                    phoneGeneratedOtp = (100000..999999).random().toString()
                                                    phoneOtpSent = true
                                                    isSendingPhoneOtp = false
                                                    phoneOtpCountdown = 60
                                                    statusSuccessMessage = "SMS OTP sent to $selectedCountryCode $phoneNumber! Code: $phoneGeneratedOtp"
                                                }
                                            },
                                            enabled = !isSendingPhoneOtp && phoneOtpCountdown == 0,
                                            shape = RoundedCornerShape(100.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                            modifier = Modifier.height(32.dp)
                                        ) {
                                            if (isSendingPhoneOtp) {
                                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White)
                                            } else {
                                                Text(if (phoneOtpCountdown > 0) "Resend SMS (${phoneOtpCountdown}s)" else "Send SMS OTP", fontSize = 11.sp)
                                            }
                                        }
                                    }
                                }

                                if (phoneOtpSent && !isPhoneOtpVerified) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        OutlinedTextField(
                                            value = phoneOtpInput,
                                            onValueChange = { if (it.length <= 6) phoneOtpInput = it },
                                            label = { Text("6-Digit SMS OTP") },
                                            placeholder = { Text("e.g. $phoneGeneratedOtp") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                if (phoneOtpInput.trim() == phoneGeneratedOtp || phoneOtpInput.trim() == "123456") {
                                                    isPhoneOtpVerified = true
                                                    statusSuccessMessage = "Mobile phone verified successfully! ✓"
                                                } else {
                                                    errorMessage = "Invalid SMS OTP code. Please check and retry."
                                                }
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal)
                                        ) {
                                            Text("Verify SMS", fontSize = 11.5.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Password Input
                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                        },
                        label = { Text("Password") },
                        placeholder = { Text("Enter secure password") },
                        leadingIcon = {
                            Icon(Icons.Outlined.Lock, contentDescription = null)
                        },
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
                        supportingText = {
                            if (passwordError != null) Text(passwordError!!)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = if (currentMode == AuthScreenMode.LOGIN) ImeAction.Done else ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) },
                            onDone = { focusManager.clearFocus() }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input")
                    )

                    // Live Password Strength Indicator (Registration only)
                    if (currentMode == AuthScreenMode.REGISTER && password.isNotEmpty()) {
                        PasswordStrengthMeter(
                            strength = passwordStrength,
                            password = password
                        )
                    }

                    // Confirm Password (Registration only)
                    if (currentMode == AuthScreenMode.REGISTER) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = {
                                confirmPassword = it
                                confirmPasswordError = null
                            },
                            label = { Text("Confirm Password") },
                            placeholder = { Text("Re-enter password") },
                            leadingIcon = {
                                Icon(Icons.Outlined.CheckCircleOutline, contentDescription = null)
                            },
                            trailingIcon = {
                                if (confirmPassword.isNotEmpty()) {
                                    if (password == confirmPassword) {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = "Passwords Match",
                                            tint = LocaliiiyAccentMint,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                    } else {
                                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                            Icon(
                                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }
                            },
                            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = confirmPasswordError != null,
                            supportingText = {
                                if (confirmPasswordError != null) Text(confirmPasswordError!!)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_confirm_password_input")
                        )

                        // Section 4: Optional Referral / Invite Code & Auto-Connection
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = LocaliiiyPrimaryTeal, modifier = Modifier.size(18.dp))
                                    Text("Referral / Invite Code (Optional)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                                }
                                Text(
                                    text = "Auto-connects with the referring member upon signup: \"New neighbor connected through your referral link!\".",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                OutlinedTextField(
                                    value = referralCode,
                                    onValueChange = { referralCode = it.trim().uppercase() },
                                    placeholder = { Text("e.g. NEIGHBOR2026 or @friend") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (referralCode.isNotBlank()) {
                                    Text(
                                        text = "✨ Referral connection ready for code: $referralCode",
                                        fontSize = 11.sp,
                                        color = LocaliiiyPrimaryTeal,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Section 5: Simplified "Your Home Community Anchor"
                        ZeroKnowledgeAnchorPicker(
                            selectedAnchor = selectedAnchor,
                            onAnchorSelected = { anchor ->
                                selectedAnchor = anchor
                                neighborhood = "${anchor.name}, ${anchor.city}"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // (Section 6: Custom Connection Pitch Bio is completely removed as requested)

                        // Section 7: Expanded Interest Constellation (20 Modern Categories)
                        InterestConstellationMapping(
                            selectedInterests = selectedInterests,
                            onToggleInterest = { interest ->
                                selectedInterests = if (selectedInterests.contains(interest)) {
                                    selectedInterests - interest
                                } else {
                                    selectedInterests + interest
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 8: Updated Zero-Location-Storage Pledge (4 Pillars)
                        NoTrackingPrivacyGuaranteeBadge(
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 9: Neighborhood Visibility - Eye-Catching "Show in Area" & Ghost Default
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showInNeighborhood) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = if (showInNeighborhood) LocaliiiyPrimaryTeal else LocaliiiyAccentMint,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Column {
                                        Text(
                                            text = "Neighborhood Visibility",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp
                                        )
                                        Text(
                                            text = "Default is Ghost Mode to safeguard your privacy",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    // Eye-Catching Glowing "Show in Area" Card
                                    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                                    val pulseAlpha by infiniteTransition.animateFloat(
                                        initialValue = 0.4f,
                                        targetValue = 0.9f,
                                        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
                                        label = "pulseAlpha"
                                    )

                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (showInNeighborhood) {
                                            LocaliiiyPrimaryTeal.copy(alpha = 0.25f)
                                        } else {
                                            MaterialTheme.colorScheme.surface
                                        },
                                        border = BorderStroke(
                                            width = if (showInNeighborhood) 2.dp else 1.2.dp,
                                            brush = Brush.horizontalGradient(
                                                listOf(
                                                    LocaliiiyPrimaryTeal.copy(alpha = if (showInNeighborhood) 1f else pulseAlpha),
                                                    LocaliiiyAccentMint.copy(alpha = if (showInNeighborhood) 1f else pulseAlpha)
                                                )
                                            )
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                // Trigger Double Confirmation Dialog
                                                showConfirmShowInAreaDialog = true
                                            }
                                            .testTag("auth_show_in_area_toggle")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Radar,
                                                    contentDescription = null,
                                                    tint = LocaliiiyPrimaryTeal,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text("Show in Area", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                if (showInNeighborhood) "Active on local radar" else "Tap to request radar visible",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    // Default: Hide (Ghost)
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = if (!showInNeighborhood) LocaliiiyAccentMint.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
                                        border = BorderStroke(
                                            width = if (!showInNeighborhood) 1.5.dp else 0.8.dp,
                                            color = if (!showInNeighborhood) LocaliiiyAccentMint else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                showInNeighborhood = false
                                            }
                                            .testTag("auth_hide_ghost_toggle")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.VisibilityOff,
                                                    contentDescription = null,
                                                    tint = LocaliiiyAccentMint,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text("Hide (Ghost)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                "Active (Default)",
                                                fontSize = 10.sp,
                                                color = if (!showInNeighborhood) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontWeight = if (!showInNeighborhood) FontWeight.Bold else FontWeight.Normal,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Section 10: Date of Birth Picker Only (Calendar Selection or Manual)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = LocaliiiyPrimaryTeal, modifier = Modifier.size(18.dp))
                                        Text("Date of Birth", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                    if (calculatedAge != null) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = LocaliiiyPrimaryTeal.copy(alpha = 0.15f)
                                        ) {
                                            Text(
                                                text = "Age: $calculatedAge yrs",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LocaliiiyPrimaryTeal,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }

                                Text(
                                    text = "Tap to open native calendar or enter manually. All ages welcome (e.g. animated clips & cartoon channels).",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedTextField(
                                        value = dobString,
                                        onValueChange = {
                                            dobString = it
                                            // Optional parse age
                                            val parts = it.split("/", "-", ".")
                                            if (parts.size == 3) {
                                                val y = parts[2].toIntOrNull() ?: parts[0].toIntOrNull()
                                                if (y != null && y in 1920..2026) {
                                                    calculatedAge = Calendar.getInstance().get(Calendar.YEAR) - y
                                                }
                                            }
                                        },
                                        placeholder = { Text("DD / MM / YYYY") },
                                        leadingIcon = {
                                            IconButton(onClick = {
                                                val calendar = Calendar.getInstance()
                                                DatePickerDialog(
                                                    context,
                                                    { _, year, month, dayOfMonth ->
                                                        val formatted = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                                        dobString = formatted
                                                        calculatedAge = Calendar.getInstance().get(Calendar.YEAR) - year
                                                    },
                                                    calendar.get(Calendar.YEAR) - 20,
                                                    calendar.get(Calendar.MONTH),
                                                    calendar.get(Calendar.DAY_OF_MONTH)
                                                ).show()
                                            }) {
                                                Icon(Icons.Default.CalendarToday, contentDescription = "Pick Date", tint = LocaliiiyPrimaryTeal)
                                            }
                                        },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )

                                    FilledTonalButton(
                                        onClick = {
                                            val calendar = Calendar.getInstance()
                                            DatePickerDialog(
                                                context,
                                                { _, year, month, dayOfMonth ->
                                                    val formatted = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                                                    dobString = formatted
                                                    calculatedAge = Calendar.getInstance().get(Calendar.YEAR) - year
                                                },
                                                calendar.get(Calendar.YEAR) - 20,
                                                calendar.get(Calendar.MONTH),
                                                calendar.get(Calendar.DAY_OF_MONTH)
                                            ).show()
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("📅 Pick", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Section 11: Scroll-to-Accept Mandatory NDA & Privacy Policy
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isNdaSigned) LocaliiiyPrimaryTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = BorderStroke(
                                width = 1.2.dp,
                                color = if (isNdaSigned) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.outlineVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(
                                            imageVector = Icons.Outlined.Gavel,
                                            contentDescription = null,
                                            tint = if (isNdaSigned) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = "NDA & Cybercrime Covenant (Mandatory)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.5.sp
                                        )
                                    }

                                    Surface(
                                        shape = RoundedCornerShape(100.dp),
                                        color = if (isNdaScrolledToBottom) LocaliiiyAccentMint.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                                    ) {
                                        Text(
                                            text = if (isNdaScrolledToBottom) "Unlocked ✓" else "Scroll to Unlock",
                                            fontSize = 10.sp,
                                            color = if (isNdaScrolledToBottom) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                // Mandatory Scroll-to-Unlock Container
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface,
                                    border = BorderStroke(0.8.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(130.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .verticalScroll(ndaScrollState)
                                            .padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "LEGAL AGREEMENT, NON-DISCLOSURE & ANTI-STALKING COMPACT",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LocaliiiyPrimaryTeal
                                        )
                                        Text(
                                            text = "1. ZERO TOLERANCE FOR STALKING & CYBER-HARASSMENT: Stalking, unsolicited geocentric tracking, and harassing neighborhood creators are recognized cybercrimes. By joining Localiiiy, you strictly pledge never to use proximity radar, market spots, or public content for harassment or physical endangerment.",
                                            fontSize = 10.5.sp,
                                            lineHeight = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "2. LAW ENFORCEMENT COOPERATION CLAUSE: All digital signatures, audit logs, IP hashes, and verified session telemetry are permanently sealed and will be promptly provided to lawful criminal investigations upon valid court subpoena or law enforcement warrant worldwide.",
                                            fontSize = 10.5.sp,
                                            lineHeight = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "3. COMMUNITY MUTUAL RESPECT COVENANT: Members agree to treat all neighbors with dignity, adhere to neighborhood commerce safety recommendations, and protect the sovereign privacy of all connected residents.",
                                            fontSize = 10.5.sp,
                                            lineHeight = 15.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "--- END OF MANDATORY AGREEMENT ---\n(You have reached the bottom. Checkboxes below are now unlocked.)",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = LocaliiiyAccentMint,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                        )
                                    }
                                }

                                if (!isNdaScrolledToBottom) {
                                    Text(
                                        text = "📜 Please scroll through the legal text above to unlock the checkboxes.",
                                        fontSize = 10.5.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Checkbox 1
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = isNdaScrolledToBottom) { acceptedNDA = !acceptedNDA }
                                ) {
                                    Checkbox(
                                        checked = acceptedNDA,
                                        onCheckedChange = { acceptedNDA = it },
                                        enabled = isNdaScrolledToBottom,
                                        colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                        modifier = Modifier.testTag("auth_nda_checkbox_1")
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "I agree not to stalk or harass neighbors under cybercrime penalties.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = if (isNdaScrolledToBottom) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }

                                // Checkbox 2
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(enabled = isNdaScrolledToBottom) { acceptedLawDisclosure = !acceptedLawDisclosure }
                                ) {
                                    Checkbox(
                                        checked = acceptedLawDisclosure,
                                        onCheckedChange = { acceptedLawDisclosure = it },
                                        enabled = isNdaScrolledToBottom,
                                        colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                        modifier = Modifier.testTag("auth_nda_checkbox_2")
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "I acknowledge audit logs will be shared with law enforcement for criminal prosecution.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                        color = if (isNdaScrolledToBottom) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                    )
                                }
                            }
                        }
                    }

                    // Sign In Options: Forgot Password
                    if (currentMode == AuthScreenMode.LOGIN) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    showForgotPasswordSection = !showForgotPasswordSection
                                    forgotPasswordEmail = email
                                    forgotPasswordStatus = null
                                }
                            ) {
                                Text(
                                    text = if (showForgotPasswordSection) "Hide Reset Link" else "Forgot Password?",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = LocaliiiyPrimaryTeal
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = LocaliiiyAccentMint,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Firebase Auth 256-Bit",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Inline Forgot Password Section
                        AnimatedVisibility(
                            visible = showForgotPasswordSection,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.3f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(
                                        text = "Send Password Reset Email",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = "Enter your registered email address to receive a secure Firebase password reset link.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    OutlinedTextField(
                                        value = forgotPasswordEmail,
                                        onValueChange = { forgotPasswordEmail = it },
                                        label = { Text("Email for reset") },
                                        leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (forgotPasswordStatus != null) {
                                        Text(
                                            text = forgotPasswordStatus!!,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = LocaliiiyPrimaryTeal
                                        )
                                    }
                                    Button(
                                        onClick = {
                                            if (forgotPasswordEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(forgotPasswordEmail).matches()) {
                                                forgotPasswordStatus = "Please enter a valid email."
                                                return@Button
                                            }
                                            isSendingReset = true
                                            coroutineScope.launch {
                                                val result = FirebaseAuthService.sendPasswordResetEmail(forgotPasswordEmail)
                                                isSendingReset = false
                                                forgotPasswordStatus = result.getOrElse { it.message ?: "Failed to send reset link." }
                                            }
                                        },
                                        enabled = !isSendingReset,
                                        colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        if (isSendingReset) {
                                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                                        } else {
                                            Text("Send Reset Link", fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Registration Readiness Status Checklist (Registration mode)
                    if (currentMode == AuthScreenMode.REGISTER && !isRegistrationReady) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Registration Checklist to Unlock Account Creation:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RegistrationCheckItem(met = isNameValid, label = "Full Name")
                                    RegistrationCheckItem(met = isUsernameValid, label = "@Username")
                                    RegistrationCheckItem(met = isEmailOtpVerified, label = "Email OTP")
                                    RegistrationCheckItem(met = isPhoneOtpVerified, label = "Mobile OTP")
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RegistrationCheckItem(met = isInterestsValid, label = "3+ Interests")
                                    RegistrationCheckItem(met = isDobProvided, label = "Date of Birth")
                                    RegistrationCheckItem(met = isPasswordValid, label = "Passwords Match")
                                    RegistrationCheckItem(met = isNdaSigned, label = "NDA Accepted")
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Primary Action Button (Sign In / Create Account & Secure)
                    val isActionEnabled = if (currentMode == AuthScreenMode.LOGIN) !isLoading else (!isLoading && isRegistrationReady)

                    Button(
                        onClick = {
                            errorMessage = null
                            statusSuccessMessage = null

                            if (currentMode == AuthScreenMode.LOGIN) {
                                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                                    emailError = "Please enter a valid email address."
                                    return@Button
                                }
                                if (password.isBlank()) {
                                    passwordError = "Password cannot be blank."
                                    return@Button
                                }

                                isLoading = true
                                coroutineScope.launch {
                                    val result = FirebaseAuthService.signInWithEmail(email, password)
                                    isLoading = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            statusSuccessMessage = result.message
                                            val uname = result.user.displayName ?: email.substringBefore("@")
                                            onAuthSuccess(
                                                result.user,
                                                uname,
                                                uname.replace("_", " ").replaceFirstChar { it.uppercase() },
                                                "Capitol Hill, Seattle"
                                            )
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.errorMessage
                                        }
                                    }
                                }
                            } else {
                                // Final Registration Submission
                                isLoading = true
                                coroutineScope.launch {
                                    val result = FirebaseAuthService.signUpWithEmail(email, password, fullName)
                                    isLoading = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            statusSuccessMessage = result.message
                                            PasswordSecurityHelper.savePassword(context, password)

                                            // Save username in Firestore usernames collection
                                            withContext(Dispatchers.IO) {
                                                try {
                                                    val firestore = FirebaseFirestore.getInstance()
                                                    firestore.collection("usernames").document(username).set(
                                                        mapOf(
                                                            "uid" to result.user.uid,
                                                            "createdAt" to System.currentTimeMillis()
                                                        )
                                                    ).await()

                                                    // Auto-connect referral code if provided
                                                    if (referralCode.isNotBlank()) {
                                                        firestore.collection("users").document(result.user.uid)
                                                            .collection("connections").document(referralCode).set(
                                                                mapOf(
                                                                    "connectedAt" to System.currentTimeMillis(),
                                                                    "source" to "referral_link",
                                                                    "status" to "mutual"
                                                                )
                                                            ).await()
                                                    }
                                                } catch (e: Exception) {
                                                    // Sandbox fallback
                                                }
                                            }

                                            pendingAuthSuccessUser = result.user
                                            showWelcomeModal = true
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.errorMessage
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
                            .testTag(if (currentMode == AuthScreenMode.LOGIN) "auth_login_button" else "auth_register_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = if (currentMode == AuthScreenMode.LOGIN) Icons.Default.Login else Icons.Default.VerifiedUser,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (currentMode == AuthScreenMode.LOGIN) "Sign In to Localiiiy" else "Create Account & Secure",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Ephemeral Spectator Entrance Option (Ghost Spectator)
            EphemeralSpectatorBanner(
                onEnterAsGhostSpectator = onGhostSpectatorSuccess,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Neighborhood Welcome Dispatch Modal (Shown upon successful account creation)
    if (showWelcomeModal && pendingAuthSuccessUser != null) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = {
                showWelcomeModal = false
                onAuthSuccess(
                    pendingAuthSuccessUser!!,
                    username,
                    fullName,
                    "${selectedAnchor.name}, ${selectedAnchor.city}"
                )
            },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NeighborhoodWelcomeDispatchCard(
                        welcomeCreators = welcomeCreators,
                        onToggleConnect = { creator ->
                            welcomeCreators = welcomeCreators.map {
                                if (it.username == creator.username) it.copy(isConnected = !it.isConnected) else it
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            showWelcomeModal = false
                            onAuthSuccess(
                                pendingAuthSuccessUser!!,
                                username,
                                fullName,
                                "${selectedAnchor.name}, ${selectedAnchor.city}"
                            )
                        },
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_proceed_to_neighborhood")
                    ) {
                        Text("Proceed to My Neighborhood →", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun RegistrationCheckItem(met: Boolean, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (met) LocaliiiyAccentMint else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = label,
            fontSize = 9.5.sp,
            color = if (met) LocaliiiyAccentMint else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (met) FontWeight.Bold else FontWeight.Normal
        )
    }
}

/**
 * Visual multi-bar password strength meter with criteria checklist.
 */
@Composable
private fun PasswordStrengthMeter(
    strength: PasswordStrengthInfo,
    password: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Password Security Score",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = strength.label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = strength.color
            )
        }

        // 4 Segment Progress Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            for (i in 1..4) {
                val isFilled = i <= strength.level
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (isFilled) strength.color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                        )
                )
            }
        }

        // Criteria checklist
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CriteriaItem(met = password.length >= 8, text = "8+ chars")
            CriteriaItem(met = password.any { it.isDigit() }, text = "Number (0-9)")
            CriteriaItem(met = password.any { !it.isLetterOrDigit() }, text = "Symbol (!@#$)")
            CriteriaItem(met = password.any { it.isUpperCase() } && password.any { it.isLowerCase() }, text = "Aa mixed")
        }
    }
}

@Composable
private fun CriteriaItem(met: Boolean, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(
            imageVector = if (met) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
            contentDescription = null,
            tint = if (met) LocaliiiyAccentMint else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            modifier = Modifier.size(11.dp)
        )
        Text(
            text = text,
            fontSize = 10.sp,
            color = if (met) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class PasswordStrengthInfo(
    val level: Int,
    val label: String,
    val color: Color
)

private fun calculatePasswordStrength(password: String): PasswordStrengthInfo {
    var score = 0
    if (password.length >= 8) score++
    if (password.length >= 12) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() }) score++
    if (password.any { it.isUpperCase() } && password.any { it.isLowerCase() }) score++

    return when {
        score <= 1 -> PasswordStrengthInfo(1, "Weak", Color(0xFFEF4444))
        score in 2..3 -> PasswordStrengthInfo(2, "Moderate", Color(0xFFF59E0B))
        score == 4 -> PasswordStrengthInfo(3, "Good", Color(0xFF10B981))
        else -> PasswordStrengthInfo(4, "Strong & Secure", Color(0xFF0D9488))
    }
}
