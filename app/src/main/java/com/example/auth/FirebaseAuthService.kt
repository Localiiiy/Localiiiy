package com.example.auth

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.example.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

data class AuthUserState(
    val uid: String,
    val email: String?,
    val displayName: String?,
    val isAnonymous: Boolean = false
)

sealed interface AuthResult {
    data class Success(val user: AuthUserState, val message: String) : AuthResult
    data class Error(val errorMessage: String) : AuthResult
}

tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Production Firebase Authentication service for Email/Password, Real Email Verification,
 * and Real Firebase Phone SMS OTP (`PhoneAuthProvider`).
 */
object FirebaseAuthService {
    private const val TAG = "FirebaseAuthService"

    private fun safeLog(message: String) {
        try {
            Log.w(TAG, message)
        } catch (_: Throwable) {
        }
    }

    val authInstance: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Throwable) {
            safeLog("FirebaseAuth instance error: ${e.message}")
            null
        }

    fun getFirestore(context: Context): FirebaseFirestore {
        val dbId = try {
            val appCtx = context.applicationContext
            val resId = appCtx.resources.getIdentifier("firestore_database_id", "string", appCtx.packageName)
            if (resId != 0) appCtx.getString(resId) else ""
        } catch (_: Throwable) {
            ""
        }
        return if (dbId.isNotBlank()) {
            FirebaseFirestore.getInstance(dbId)
        } else {
            FirebaseFirestore.getInstance()
        }
    }

    private val _currentUserState = MutableStateFlow<AuthUserState?>(null)
    val currentUserState: StateFlow<AuthUserState?> = _currentUserState.asStateFlow()

    init {
        try {
            val auth = authInstance
            if (auth != null) {
                updateUserState(auth.currentUser)
                auth.addAuthStateListener { firebaseAuth ->
                    updateUserState(firebaseAuth.currentUser)
                }
            }
        } catch (e: Throwable) {
            safeLog("Failed to attach auth state listener: ${e.message}")
        }
    }

    private fun updateUserState(firebaseUser: FirebaseUser?) {
        _currentUserState.value = firebaseUser?.let {
            AuthUserState(
                uid = it.uid,
                email = it.email,
                displayName = it.displayName,
                isAnonymous = it.isAnonymous
            )
        }
    }

    private fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return try {
            val pattern = android.util.Patterns.EMAIL_ADDRESS
            if (pattern != null) pattern.matcher(email).matches()
            else email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        } catch (_: Throwable) {
            email.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
        }
    }

    fun mapAuthError(e: Throwable): String {
        val raw = e.message.orEmpty()
        if (raw.contains("App Check", ignoreCase = true) || raw.contains("ATTEMPT_TO_USE_UNCONFIGURED_APP_CHECK", ignoreCase = true)) {
            return "Firebase App Check token error. In Firebase Console → App Check → APIs, set Authentication and Cloud Firestore to 'Unenforce' (or register Play Integrity with your Play App Signing SHA-256)."
        }
        val code = (e as? FirebaseAuthException)?.errorCode.orEmpty()
        return when {
            code == "ERROR_OPERATION_NOT_ALLOWED" || raw.contains("OPERATION_NOT_ALLOWED", ignoreCase = true) ->
                "This sign-in provider is disabled in Firebase Console. Go to Firebase Console → Authentication → Sign-in method to enable Email/Password or Phone."
            e is FirebaseAuthUserCollisionException || code == "ERROR_EMAIL_ALREADY_IN_USE" ->
                "An account with this email already exists. Please Sign In instead."
            e is FirebaseAuthWeakPasswordException || code == "ERROR_WEAK_PASSWORD" ->
                "Password is too weak. Please use at least 6 characters."
            e is FirebaseAuthInvalidUserException ->
                "No account found with this email."
            e is FirebaseAuthInvalidCredentialsException ->
                "Incorrect email or password."
            raw.contains("BILLING_NOT_ENABLED", ignoreCase = true) ->
                "SMS OTP requires Firebase Blaze billing or Play Integrity setup in Firebase Console. You can still register with Email & Password."
            raw.contains("TOO_MANY_REQUESTS", ignoreCase = true) || raw.contains("unusual activity", ignoreCase = true) ->
                "Too many attempts from this device/number. Please wait a few minutes and try again."
            raw.contains("network", ignoreCase = true) ->
                "Network error. Please check your internet connection and try again."
            else -> raw.ifBlank { "Authentication failed. Please try again." }
        }
    }

    suspend fun signInWithEmail(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (!isValidEmail(cleanEmail)) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (cleanPassword.isBlank()) {
            return@withContext AuthResult.Error("Please enter your password.")
        }

        val auth = authInstance
            ?: return@withContext AuthResult.Error("Firebase is not initialized.")

        try {
            val taskResult = auth.signInWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val user = taskResult.user
                ?: return@withContext AuthResult.Error("Authentication succeeded but user profile was not returned.")
            val state = AuthUserState(
                uid = user.uid,
                email = user.email,
                displayName = user.displayName ?: cleanEmail.substringBefore("@")
            )
            _currentUserState.value = state
            AuthResult.Success(state, "Signed in successfully!")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase sign-in error", e)
            AuthResult.Error(mapAuthError(e))
        }
    }

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()
        val cleanName = displayName.trim()

        if (!isValidEmail(cleanEmail)) {
            return@withContext AuthResult.Error("Please enter a valid email address.")
        }
        if (cleanPassword.length < 6) {
            return@withContext AuthResult.Error("Password must be at least 6 characters long.")
        }

        val auth = authInstance
            ?: return@withContext AuthResult.Error("Firebase is not initialized.")

        try {
            val taskResult = auth.createUserWithEmailAndPassword(cleanEmail, cleanPassword).await()
            val user = taskResult.user
                ?: return@withContext AuthResult.Error("Failed to create user account.")

            if (cleanName.isNotBlank()) {
                try {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(cleanName)
                        .build()
                    user.updateProfile(profileUpdates).await()
                } catch (pe: Exception) {
                    Log.w(TAG, "Could not update user display name: ${pe.message}")
                }
            }

            var verificationNote = ""
            try {
                user.sendEmailVerification().await()
                verificationNote = " A real verification link was sent to $cleanEmail."
            } catch (ve: Exception) {
                Log.w(TAG, "Email verification dispatch failed: ${ve.message}")
            }

            val state = AuthUserState(
                uid = user.uid,
                email = user.email,
                displayName = cleanName.ifBlank { user.displayName ?: cleanEmail.substringBefore("@") }
            )
            _currentUserState.value = state
            AuthResult.Success(state, "Account created successfully!$verificationNote")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase sign-up error", e)
            AuthResult.Error(mapAuthError(e))
        }
    }

    suspend fun resendVerificationEmail(): Result<String> = withContext(Dispatchers.IO) {
        val user = authInstance?.currentUser
            ?: return@withContext Result.failure(IllegalStateException("No signed-in user."))
        try {
            user.sendEmailVerification().await()
            Result.success("Verification email sent to ${user.email ?: "your inbox"}.")
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Result<String> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        if (!isValidEmail(cleanEmail)) {
            return@withContext Result.failure(IllegalArgumentException("Please enter a valid email address."))
        }
        val auth = authInstance
            ?: return@withContext Result.failure(IllegalStateException("Firebase Auth is not initialized."))
        try {
            auth.sendPasswordResetEmail(cleanEmail).await()
            Result.success("Password reset link sent to $cleanEmail.")
        } catch (e: Exception) {
            Log.w(TAG, "Send password reset failed: ${e.message}")
            Result.failure(Exception(mapAuthError(e)))
        }
    }

    fun verifyPhoneNumber(
        activity: Activity,
        phoneNumberE164: String,
        onCodeSent: (String, PhoneAuthProvider.ForceResendingToken) -> Unit,
        onInstantCredential: (PhoneAuthCredential) -> Unit,
        onVerificationFailed: (String) -> Unit
    ) {
        val auth = authInstance
        if (auth == null) {
            onVerificationFailed("Firebase Auth is not initialized.")
            return
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumberE164)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    onInstantCredential(credential)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e(TAG, "Phone verification failed", e)
                    onVerificationFailed(mapAuthError(e))
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    onCodeSent(verificationId, token)
                }
            })
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun linkPhoneCredentialToCurrentUser(credential: PhoneAuthCredential): Result<Unit> =
        withContext(Dispatchers.IO) {
            val user = authInstance?.currentUser
                ?: return@withContext Result.failure(IllegalStateException("User not signed in yet."))
            try {
                user.linkWithCredential(credential).await()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(Exception(mapAuthError(e)))
            }
        }

    suspend fun signInWithPhoneCredential(credential: PhoneAuthCredential): AuthResult =
        withContext(Dispatchers.IO) {
            val auth = authInstance
                ?: return@withContext AuthResult.Error("Firebase Auth is not initialized.")
            try {
                val res = auth.signInWithCredential(credential).await()
                val user = res.user
                    ?: return@withContext AuthResult.Error("Phone authentication did not return a user.")
                val state = AuthUserState(
                    uid = user.uid,
                    email = user.email,
                    displayName = user.displayName ?: user.phoneNumber
                )
                _currentUserState.value = state
                AuthResult.Success(state, "Phone number verified & signed in!")
            } catch (e: Exception) {
                AuthResult.Error(mapAuthError(e))
            }
        }

    fun isEmailVerified(): Boolean {
        val user = authInstance?.currentUser
        return user?.let {
            if (it.email != null) it.isEmailVerified else true
        } ?: false
    }

    suspend fun reloadUser() {
        authInstance?.currentUser?.reload()?.await()
        updateUserState(authInstance?.currentUser)
    }

    fun signOut() {
        try {
            authInstance?.signOut()
        } catch (e: Exception) {
            Log.w(TAG, "Error signing out from Firebase: ${e.message}")
        }
        _currentUserState.value = null
    }
}
