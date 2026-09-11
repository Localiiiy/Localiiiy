package com.example.util

import android.content.Context
import android.content.SharedPreferences

object PasswordSecurityHelper {
    private const val PREFS_NAME = "Localiiiy_security_prefs"
    private const val KEY_PASSWORD = "user_account_password"
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

    fun getStoredPassword(context: Context): String {
        return getPrefs(context).getString(KEY_PASSWORD, DEFAULT_PASSWORD) ?: DEFAULT_PASSWORD
    }

    fun savePassword(context: Context, newPassword: String) {
        getPrefs(context).edit().putString(KEY_PASSWORD, newPassword).apply()
    }

    fun verifyOldPassword(context: Context, enteredOldPassword: String): Boolean {
        val current = getStoredPassword(context)
        return enteredOldPassword == current
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
        return Pair(true, "Password changed successfully! 🔐")
    }
}
