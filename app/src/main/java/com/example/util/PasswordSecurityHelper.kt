package com.example.util

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64

object PasswordSecurityHelper {
    private const val PREFS_NAME = "Localiiiy_security_prefs"
    private const val KEY_PASSWORD_HASH = "user_account_password_hash"
    private const val KEY_PASSWORD_SALT = "user_account_password_salt"
    private const val KEY_LEGACY_PASSWORD = "user_account_password"
    const val DEFAULT_PASSWORD = "Localiiiy@2026"

    data class PasswordRequirementStatus(
        val hasMinLength: Boolean,
        val hasNumeric: Boolean,
        val hasSpecialChar: Boolean,
        val isValid: Boolean,
        val errorMessage: String?
    )

    /**
     * Enforces that passwords must:
     * 1. Be at least 8 characters long
     * 2. Include at least one numeric digit (0-9)
     * 3. Include at least one special character (!@#$%^&* etc.)
     */
    fun checkRequirements(password: String): PasswordRequirementStatus {
        val hasMinLength = password.length >= 8
        val hasNumeric = password.any { it.isDigit() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
        val isValid = hasMinLength && hasNumeric && hasSpecialChar

        val errorMessage = when {
            password.isBlank() -> "Password cannot be blank"
            !hasMinLength -> "Password must be at least 8 characters"
            !hasNumeric -> "Password must include at least one number (0-9)"
            !hasSpecialChar -> "Password must include at least one special character (!@#$%^&*...)"
            else -> null
        }

        return PasswordRequirementStatus(
            hasMinLength = hasMinLength,
            hasNumeric = hasNumeric,
            hasSpecialChar = hasSpecialChar,
            isValid = isValid,
            errorMessage = errorMessage
        )
    }

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun hashWithSalt(password: String, salt: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(salt)
        val digest = md.digest(password.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(digest, Base64.NO_WRAP)
    }

    private fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    fun savePassword(context: Context, newPassword: String) {
        val salt = generateSalt()
        val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
        val hash = hashWithSalt(newPassword, salt)
        getPrefs(context).edit()
            .putString(KEY_PASSWORD_HASH, hash)
            .putString(KEY_PASSWORD_SALT, saltBase64)
            .remove(KEY_LEGACY_PASSWORD) // Clean legacy plaintext
            .apply()
    }

    fun verifyOldPassword(context: Context, enteredOldPassword: String): Boolean {
        val prefs = getPrefs(context)
        val storedHash = prefs.getString(KEY_PASSWORD_HASH, null)
        val storedSaltBase64 = prefs.getString(KEY_PASSWORD_SALT, null)

        if (storedHash != null && storedSaltBase64 != null) {
            val salt = Base64.decode(storedSaltBase64, Base64.NO_WRAP)
            val computedHash = hashWithSalt(enteredOldPassword, salt)
            return computedHash == storedHash
        }

        // Check legacy unhashed or default fallback
        val legacy = prefs.getString(KEY_LEGACY_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
        val match = (enteredOldPassword == legacy)
        if (match) {
            // Auto-upgrade to secure salt & hash
            savePassword(context, enteredOldPassword)
        }
        return match
    }

    fun changePassword(context: Context, oldPassword: String, newPassword: String): Pair<Boolean, String> {
        if (!verifyOldPassword(context, oldPassword)) {
            return Pair(false, "Current (old) password is incorrect.")
        }
        val status = checkRequirements(newPassword)
        if (!status.isValid) {
            return Pair(false, status.errorMessage ?: "Password does not meet requirements.")
        }
        if (oldPassword == newPassword) {
            return Pair(false, "New password cannot be identical to the old password.")
        }
        savePassword(context, newPassword)
        return Pair(true, "Password securely changed and encrypted! 🔐")
    }
}
