package com.example.data

import java.time.Instant
import java.time.temporal.ChronoUnit

object SpaceChatRepository {
    
    // Mock user creation dates
    private val accountCreationDates = mutableMapOf<String, Instant>()
    
    init {
        // Mock current user as an older account
        accountCreationDates["currentUser"] = Instant.now().minus(30, ChronoUnit.DAYS)
        // Mock new spam account
        accountCreationDates["newSpammer"] = Instant.now().minus(2, ChronoUnit.HOURS)
    }

    // Zero-Spam Moderator Shield
    fun canSendMessage(userId: String): Boolean {
        val creationDate = accountCreationDates.getOrDefault(userId, Instant.now())
        val ageInHours = ChronoUnit.HOURS.between(creationDate, Instant.now())
        
        // Gating message sending permissions for accounts younger than 24 hours
        return ageInHours >= 24
    }
}
