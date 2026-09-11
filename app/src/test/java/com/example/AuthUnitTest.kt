package com.example

import com.example.auth.AuthResult
import com.example.auth.FirebaseAuthService
import com.example.util.PasswordSecurityHelper
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthUnitTest {

    @Test
    fun testPasswordRequirements() {
        // Too short
        val shortResult = PasswordSecurityHelper.checkRequirements("Short1!")
        assertFalse("Passwords < 8 chars should fail", shortResult.isValid)

        // Missing number
        val noNumberResult = PasswordSecurityHelper.checkRequirements("NoNumberHere!")
        assertFalse("Password without numbers should fail", noNumberResult.isValid)

        // Missing symbol
        val noSymbolResult = PasswordSecurityHelper.checkRequirements("NoSymbol1234")
        assertFalse("Password without symbol should fail", noSymbolResult.isValid)

        // Valid password
        val validResult = PasswordSecurityHelper.checkRequirements(PasswordSecurityHelper.DEFAULT_PASSWORD)
        assertTrue("Valid password should pass criteria", validResult.isValid)
    }

    @Test
    fun testFirebaseAuthServiceValidation() = runBlocking {
        // Invalid email
        val invalidEmailResult = FirebaseAuthService.signInWithEmail("not-an-email", "StrongP@ssw0rd")
        assertTrue("Invalid email should return Error", invalidEmailResult is AuthResult.Error)

        // Blank password
        val blankPasswordResult = FirebaseAuthService.signInWithEmail("user@example.com", "   ")
        assertTrue("Blank password should return Error", blankPasswordResult is AuthResult.Error)

        // Weak password registration
        val weakRegResult = FirebaseAuthService.signUpWithEmail("newuser@example.com", "short", "New User")
        assertTrue("Weak registration password should return Error", weakRegResult is AuthResult.Error)
    }
}
