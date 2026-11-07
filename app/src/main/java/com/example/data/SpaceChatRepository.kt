package com.example.data

import java.time.Instant
import java.time.temporal.ChronoUnit

object SpaceChatRepository {
    
    // Real user account creation timestamps
    private val accountCreationDates = mutableMapOf<String, Instant>()

    fun registerAccountCreation(userId: String, createdAt: Instant = Instant.now()) {
        if (userId.isNotBlank()) {
            accountCreationDates.putIfAbsent(userId, createdAt)
        }
    }

    // Zero-Spam Moderator Shield
    fun canSendMessage(userId: String): Boolean {
        val creationDate = accountCreationDates[userId] ?: return true
        val ageInHours = ChronoUnit.HOURS.between(creationDate, Instant.now())
        return ageInHours >= 24
    }
}
