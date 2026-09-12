package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.auth.AuthResult
import com.example.auth.AuthUserState
import com.example.auth.FirebaseAuthService
import com.example.ui.theme.LocaliiiyAccentMint
import com.example.ui.theme.LocaliiiyDeepNavy
import com.example.ui.theme.LocaliiiyPrimaryTeal
import com.example.ui.screens.onboarding.*
import com.example.util.PasswordSecurityHelper
import kotlinx.coroutines.launch

enum class AuthScreenMode {
    LOGIN,
    REGISTER,
    ZERO_KNOWLEDGE_ONBOARDING
}

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

    // Register Inputs
    var fullName by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf("") }
    var usernameError by remember { mutableStateOf<String?>(null) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var neighborhood by remember { mutableStateOf("Capitol Hill, Seattle") }
    var acceptedNDA by remember { mutableStateOf(false) }
    var acceptedLawDisclosure by remember { mutableStateOf(false) }
    var showFullNdaModal by remember { mutableStateOf(false) }

    // Section 1: Zero-Knowledge & Identity Setup State
    var selectedAnchor by remember { mutableStateOf(defaultAnchorOptions[0]) }
    var ghostAlias by remember { mutableStateOf("MetroSparrow-842") }
    var activePersona by remember { mutableStateOf("CREATOR") }
    var localCircles by remember { mutableStateOf(defaultLocalCircles) }
    var isBiometricVaultEnabled by remember { mutableStateOf(false) }
    var isProximityScanning by remember { mutableStateOf(false) }
    var selectedInterests by remember { mutableStateOf(listOf("Local Food & Coffee", "Indie Film & Clips", "Urban Photography", "Maker Crafts")) }
    var startInGhostMode by remember { mutableStateOf(false) }
    var publicHeadline by remember { mutableStateOf("Visual storyteller & local explorer 🌿📸") }
    var connectionValue by remember { mutableStateOf("Sharing neighborhood hidden spots, equipment lending & local collaborations.") }
    var birthYear by remember { mutableStateOf(2000) }
    var creatorCategory by remember { mutableStateOf("Filmmaker 🎬") }
    var selectedTheme by remember { mutableStateOf(OnboardingThemePalette.NEON_CYBER) }
    var isPanicCloakEnabled by remember { mutableStateOf(true) }
    var hasAcceptedCovenant by remember { mutableStateOf(true) }
    var referralCode by remember { mutableStateOf("") }
    var welcomeCreators by remember { mutableStateOf(defaultLocalWelcomeCreators) }
    var showWelcomeModal by remember { mutableStateOf(false) }
    var pendingAuthSuccessUser by remember { mutableStateOf<AuthUserState?>(null) }

    // Status & Loading State
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var statusSuccessMessage by remember { mutableStateOf<String?>(null) }

    // Forgot Password Inline Expansion
    var showForgotPasswordSection by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordStatus by remember { mutableStateOf<String?>(null) }
    var isSendingReset by remember { mutableStateOf(false) }

    // Password Strength live calculation
    val passwordStrength = remember(password) {
        calculatePasswordStrength(password)
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
                                .size(32.dp)
                                .background(LocaliiiyPrimaryTeal.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = LocaliiiyPrimaryTeal,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Firebase Authentication",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Securing User-Generated Content",
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
                            contentDescription = "Close Authentication"
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
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Security Gating Contextual Banner (if redirected from creating content)
            if (!securityReason.isNullOrBlank()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = LocaliiiyPrimaryTeal.copy(alpha = 0.12f),
                    border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = LocaliiiyPrimaryTeal,
                            modifier = Modifier.size(22.dp)
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

            // Section 1: Ephemeral Spectator Entrance Banner
            EphemeralSpectatorBanner(
                onEnterAsGhostSpectator = {
                    onGhostSpectatorSuccess()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Top Visual Emblem
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                LocaliiiyAccentMint.copy(alpha = 0.35f),
                                Color.Transparent
                            )
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(LocaliiiyPrimaryTeal, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (mode) {
                            AuthScreenMode.LOGIN -> Icons.Default.LockPerson
                            AuthScreenMode.REGISTER -> Icons.Default.VerifiedUser
                            AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING -> Icons.Default.Shield
                        },
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = when (mode) {
                    AuthScreenMode.LOGIN -> "Welcome to Localiiiy"
                    AuthScreenMode.REGISTER -> "Join Verified Neighborhood"
                    AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING -> "Zero-Knowledge Identity Setup"
                },
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when (mode) {
                    AuthScreenMode.LOGIN -> "Sign in with your Firebase credentials to manage your posts and pulses."
                    AuthScreenMode.REGISTER -> "Register with email to publish clips, broadcast radar pulses, and secure your content."
                    AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING -> "Configure dual personas, fuzzy geohash anchors, and sovereign privacy dials."
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Tri-Mode Pill Switcher
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
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
                            .height(38.dp)
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
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sign In",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
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
                            .height(38.dp)
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
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Register",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (mode == AuthScreenMode.REGISTER) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Zero-Knowledge Tab
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = if (mode == AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING) LocaliiiyPrimaryTeal else Color.Transparent,
                        modifier = Modifier
                            .weight(1.1f)
                            .height(38.dp)
                            .clickable {
                                mode = AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING
                                errorMessage = null
                                statusSuccessMessage = null
                            }
                            .testTag("auth_mode_zk_tab")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = if (mode == AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ZK Setup",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = if (mode == AuthScreenMode.ZERO_KNOWLEDGE_ONBOARDING) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error / Success feedback cards
            if (errorMessage != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
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
                        .padding(bottom = 14.dp)
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

            // Main Form Content
            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(180))
                },
                label = "auth_form_mode"
            ) { currentMode ->
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (currentMode != AuthScreenMode.LOGIN) {
                        // Full Name
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = {
                                fullName = it
                                fullNameError = null
                            },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Alex Rivera") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Badge, contentDescription = null)
                            },
                            isError = fullNameError != null,
                            supportingText = {
                                if (fullNameError != null) Text(fullNameError!!)
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_fullname_input")
                        )

                        // Username
                        OutlinedTextField(
                            value = username,
                            onValueChange = {
                                val clean = it.replace("@", "").trim().lowercase()
                                username = clean
                                usernameError = null
                            },
                            label = { Text("Username") },
                            placeholder = { Text("alex_creative") },
                            leadingIcon = {
                                Text("@", fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(start = 14.dp, end = 4.dp))
                            },
                            isError = usernameError != null,
                            supportingText = {
                                if (usernameError != null) Text(usernameError!!)
                                else Text("Unique handle for community posts & clips")
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_username_input")
                        )
                    }

                    // Email Address
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
                            if (email.isNotEmpty()) {
                                IconButton(onClick = { email = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear email")
                                }
                            }
                        },
                        isError = emailError != null,
                        supportingText = {
                            if (emailError != null) Text(emailError!!)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input")
                    )

                    // Password
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

                    // Live Password Strength Indicator (For Registration)
                    if (currentMode != AuthScreenMode.LOGIN && password.isNotEmpty()) {
                        PasswordStrengthMeter(
                            strength = passwordStrength,
                            password = password
                        )
                    }

                    if (currentMode != AuthScreenMode.LOGIN) {
                        // Confirm Password
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

                        // Section 1.2: Zero-Knowledge Anchor Selection (Fuzzy Geohash Coarse Bounds)
                        ZeroKnowledgeAnchorPicker(
                            selectedAnchor = selectedAnchor,
                            onAnchorSelected = { anchor ->
                                selectedAnchor = anchor
                                neighborhood = "${anchor.name}, ${anchor.city}"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.3 & 1.6: Dual-Persona Setup & Localized Alias Generator
                        DualPersonaCardSetup(
                            creatorHandle = username.ifEmpty { "alex_creator" },
                            onCreatorHandleChange = { username = it },
                            ghostAlias = ghostAlias,
                            onGhostAliasChange = { ghostAlias = it },
                            onRegenerateGhostAlias = {
                                val prefixes = listOf("MetroSparrow", "BayFalcon", "TimberLynx", "HarborSeal", "HighlandHawk", "CascadeFox", "UrbanOwl", "CanyonWolf")
                                ghostAlias = "${prefixes.random()}-${(100..999).random()}"
                            },
                            activePersona = activePersona,
                            onPersonaToggle = { activePersona = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        LocalizedAliasGenerator(
                            currentAlias = ghostAlias,
                            onAliasGenerated = { ghostAlias = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.16: Creator Category Certification
                        CreatorCategoryCertification(
                            selectedCategory = creatorCategory,
                            onCategorySelected = { creatorCategory = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.14: Custom Connection Pitch Bio
                        CustomConnectionPitchBioField(
                            headline = publicHeadline,
                            onHeadlineChange = { publicHeadline = it },
                            connectionValue = connectionValue,
                            onConnectionValueChange = { connectionValue = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.11: Interest Constellation Mapping
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

                        // Section 1.5: No-Tracking Privacy Guarantee Badge
                        NoTrackingPrivacyGuaranteeBadge(
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.7: Creator Distribution Primer Carousel
                        CreatorDistributionPrimerCarousel(
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.4: Mutual Connection Rings Genesis
                        MutualConnectionRingsCard(
                            circles = localCircles,
                            onToggleConnection = { circle ->
                                localCircles = localCircles.map {
                                    if (it.id == circle.id) it.copy(isConnected = !it.isConnected) else it
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.12: Ghost Mode Default Pre-Selection
                        GhostModePreSelectionToggle(
                            isGhostModeDefault = startInGhostMode,
                            onToggle = { startInGhostMode = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.15: Age-Appropriate Geographic Gating
                        AgeAppropriateGatingCard(
                            birthYear = birthYear,
                            onBirthYearChange = { birthYear = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.17: Dynamic Visual Theme Selection
                        DynamicVisualThemeSelector(
                            selectedTheme = selectedTheme,
                            onThemeSelected = { selectedTheme = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.8: Biometric Vault Protection
                        BiometricVaultProtectionCard(
                            isBiometricVaultEnabled = isBiometricVaultEnabled,
                            onToggleBiometricVault = { isBiometricVaultEnabled = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.18: Stealth Quick-Exit Gesture Setup ("Panic Cloak")
                        StealthQuickExitGestureCard(
                            isPanicCloakEnabled = isPanicCloakEnabled,
                            onToggle = { isPanicCloakEnabled = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.10: Proximity Connection Discovery
                        ProximityConnectionDiscoveryView(
                            isScanning = isProximityScanning,
                            onStartScan = { isProximityScanning = !isProximityScanning },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.20: Seamless Deep-Link Referral Handshake
                        SeamlessDeepLinkReferralField(
                            referralCode = referralCode,
                            onReferralCodeChange = { referralCode = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.19: Terms of Respect & Anti-Harassment Compact
                        TermsOfRespectCompactCard(
                            hasAccepted = hasAcceptedCovenant,
                            onToggleAcceptance = { hasAcceptedCovenant = it },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Section 1.9: One-Tap Identity Scrub
                        OneTapIdentityScrubButton(
                            onScrubIdentity = {
                                onScrubIdentity()
                                statusSuccessMessage = "Session data purged and caches cleared. 🧹✨"
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Mandatory NDA & Confidentiality Agreement Card
                        val isAllNdaAccepted = acceptedNDA && acceptedLawDisclosure
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isAllNdaAccepted) LocaliiiyPrimaryTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f),
                            border = BorderStroke(
                                width = 1.2.dp,
                                color = if (isAllNdaAccepted) LocaliiiyPrimaryTeal.copy(alpha = 0.5f) else MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                                .testTag("auth_nda_agreement_card")
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Gavel,
                                        contentDescription = null,
                                        tint = if (isAllNdaAccepted) LocaliiiyPrimaryTeal else MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "NDA & Confidentiality Agreement (Mandatory)",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        ),
                                        color = if (isAllNdaAccepted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Text(
                                            text = "⚖️ Strict Anti-Cybercrime & Legal Deterrence:",
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "• Stalking neighbours is a recognized cyber crime.\n• Agree not to use app for illegal activities, stalking, or crimes.\n• All details, logs, GPS timestamps, and communications will be preserved and shared with concerned law enforcement as per law for investigation and valid in court.\n• Violations carry severe punishment under the laws of your country. We respect law and order globally.",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontSize = 10.5.sp,
                                                lineHeight = 14.5.sp
                                            ),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { acceptedNDA = !acceptedNDA }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = acceptedNDA,
                                        onCheckedChange = { acceptedNDA = it },
                                        colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                        modifier = Modifier.testTag("auth_nda_checkbox_1")
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "I agree not to stalk neighbours as it is a recognized cyber crime, and never use Localiiiy for illegal activities or crime.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.Top,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { acceptedLawDisclosure = !acceptedLawDisclosure }
                                        .padding(vertical = 3.dp)
                                ) {
                                    Checkbox(
                                        checked = acceptedLawDisclosure,
                                        onCheckedChange = { acceptedLawDisclosure = it },
                                        colors = CheckboxDefaults.colors(checkedColor = LocaliiiyPrimaryTeal),
                                        modifier = Modifier.testTag("auth_nda_checkbox_2")
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "I acknowledge all details, logs, and telemetry will be shared with law enforcement for court prosecution with severe punishment under my country's laws.",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, lineHeight = 15.sp),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Sign In Options: Forgot Password & Remember Me
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

                    Spacer(modifier = Modifier.height(6.dp))

                    // Primary Action Button (Sign In / Register)
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
                                                "Seattle, WA"
                                            )
                                        }
                                        is AuthResult.Error -> {
                                            errorMessage = result.errorMessage
                                        }
                                    }
                                }
                            } else {
                                // Registration Validation
                                if (fullName.isBlank()) {
                                    fullNameError = "Please enter your name."
                                    return@Button
                                }
                                if (username.isBlank() || username.length < 3) {
                                    usernameError = "Username must be at least 3 characters."
                                    return@Button
                                }
                                if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                                    emailError = "Please enter a valid email address."
                                    return@Button
                                }
                                val check = PasswordSecurityHelper.checkRequirements(password)
                                if (!check.isValid) {
                                    passwordError = check.errorMessage
                                    return@Button
                                }
                                if (password != confirmPassword) {
                                    confirmPasswordError = "Passwords do not match."
                                    return@Button
                                }
                                if (!acceptedNDA || !acceptedLawDisclosure) {
                                    errorMessage = "Please read and accept both NDA & Cybercrime Legal Disclosures to create an account."
                                    return@Button
                                }

                                isLoading = true
                                coroutineScope.launch {
                                    val result = FirebaseAuthService.signUpWithEmail(email, password, fullName)
                                    isLoading = false
                                    when (result) {
                                        is AuthResult.Success -> {
                                            statusSuccessMessage = result.message
                                            PasswordSecurityHelper.savePassword(context, password)
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
                        enabled = !isLoading,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LocaliiiyPrimaryTeal),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
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
                                imageVector = if (currentMode == AuthScreenMode.LOGIN) Icons.Default.Login else Icons.Default.LockPerson,
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

                    // Quick Demo / Testing Shortcut Button
                    OutlinedButton(
                        onClick = {
                            email = "alex.rivera@Localiiiy.app"
                            password = PasswordSecurityHelper.DEFAULT_PASSWORD
                            if (currentMode == AuthScreenMode.REGISTER) {
                                fullName = "Alex Rivera"
                                username = "alex_creative"
                                confirmPassword = PasswordSecurityHelper.DEFAULT_PASSWORD
                            }
                            coroutineScope.launch {
                                isLoading = true
                                val result = FirebaseAuthService.signInWithEmail(
                                    "alex.rivera@Localiiiy.app",
                                    PasswordSecurityHelper.DEFAULT_PASSWORD
                                )
                                isLoading = false
                                when (result) {
                                    is AuthResult.Success -> {
                                        onAuthSuccess(result.user, "alex_creative", "Alex Rivera", "Pike Place Market, Seattle")
                                    }
                                    is AuthResult.Error -> {
                                        // Demo auto-fallback
                                        val mockUser = AuthUserState(
                                            uid = "demo_alex_1",
                                            email = "alex.rivera@Localiiiy.app",
                                            displayName = "Alex Rivera"
                                        )
                                        onAuthSuccess(mockUser, "alex_creative", "Alex Rivera", "Pike Place Market, Seattle")
                                    }
                                }
                            }
                        },
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, LocaliiiyAccentMint.copy(alpha = 0.5f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("auth_demo_quick_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = LocaliiiyAccentMint,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "⚡ Quick Demo Creator (@alex_creative)",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Security Trust Guarantee Badges
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "🔒 Security & Content Integrity Standard",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "All user-generated posts, clips, and marketplace listings are digitally signed with your Firebase UID to prevent impersonation, unauthorized scraping, and community spam.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SecurityPillTag(icon = Icons.Default.Shield, label = "Firebase Auth")
                        SecurityPillTag(icon = Icons.Default.VpnKey, label = "256-Bit Signed")
                        SecurityPillTag(icon = Icons.Default.ShareLocation, label = "Privacy Guard")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Section 1.13: Neighborhood Welcome Dispatch Modal (Shown upon successful account creation)
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
private fun SecurityPillTag(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, LocaliiiyPrimaryTeal.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = LocaliiiyPrimaryTeal,
                modifier = Modifier.size(12.dp)
            )
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
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
