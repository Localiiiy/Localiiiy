package com.example.data

enum class RadarPerkType {
    AURA_BRIGHTNESS,
    RANGE_BOOSTER,
    GOLDEN_BEACON,
    SPEED_SWEEP,
    HIGH_ALTITUDE_PING
}

data class RadarVisibilityPerk(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val perkType: RadarPerkType,
    val durationHours: Int,
    val activatedAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + (durationHours * 3600 * 1000L),
    val glowColorHex: Long = 0xFFFFD700
) {
    val isExpired: Boolean
        get() = System.currentTimeMillis() > expiresAt

    val remainingHours: Int
        get() = (((expiresAt - System.currentTimeMillis()).coerceAtLeast(0L)) / (1000 * 3600)).toInt()
}

data class CheckInStreakReward(
    val dayNumber: Int,
    val points: Int,
    val title: String,
    val perk: RadarVisibilityPerk?,
    val badgeTitle: String? = null,
    val badgeEmoji: String? = null,
    val isMilestone: Boolean = false
)

data class DailyCheckInState(
    val currentStreakDays: Int = 3,
    val lastCheckInTimestamp: Long = System.currentTimeMillis() - (26 * 3600 * 1000L),
    val hasCheckedInToday: Boolean = false,
    val totalPoints: Int = 450,
    val totalCheckInsCompleted: Int = 18,
    val activePerks: List<RadarVisibilityPerk> = listOf(
        RadarVisibilityPerk(
            id = "perk_starter_aura",
            name = "Signal Aura Glow (+25%)",
            description = "Brightens your proximity radar ping and amplifies visibility to active neighbors.",
            emoji = "✨",
            perkType = RadarPerkType.AURA_BRIGHTNESS,
            durationHours = 24,
            activatedAt = System.currentTimeMillis() - (4 * 3600 * 1000L),
            expiresAt = System.currentTimeMillis() + (20 * 3600 * 1000L),
            glowColorHex = 0xFF00E5FF
        )
    ),
    val unlockedStreakBadges: List<StreakBadge> = listOf(
        StreakBadge(
            id = "badge_scout",
            name = "Day 1 Signal Scout",
            emoji = "🧭",
            dayRequirement = 1,
            description = "Initiated daily local signal telemetry."
        ),
        StreakBadge(
            id = "badge_pulse",
            name = "Day 3 Neighborhood Pulse",
            emoji = "⚡",
            dayRequirement = 3,
            description = "Maintained a 3-day hyperlocal active streak."
        )
    )
)

data class StreakBadge(
    val id: String,
    val name: String,
    val emoji: String,
    val dayRequirement: Int,
    val description: String,
    val unlockedAt: Long = System.currentTimeMillis()
)

object CheckInRewardsConfig {
    fun get7DayRewards(): List<CheckInStreakReward> = listOf(
        CheckInStreakReward(
            dayNumber = 1,
            points = 50,
            title = "Day 1 Signal Scout",
            perk = null,
            badgeTitle = "Day 1 Signal Scout",
            badgeEmoji = "🧭",
            isMilestone = false
        ),
        CheckInStreakReward(
            dayNumber = 2,
            points = 75,
            title = "Day 2 Pulse Steady",
            perk = RadarVisibilityPerk(
                id = "perk_day_2",
                name = "Radar Sweep Speed (+15%)",
                description = "Faster sonar sweep refresh rate for 24 hours.",
                emoji = "🌀",
                perkType = RadarPerkType.SPEED_SWEEP,
                durationHours = 24,
                glowColorHex = 0xFF00FF41
            ),
            isMilestone = false
        ),
        CheckInStreakReward(
            dayNumber = 3,
            points = 120,
            title = "Day 3 Hyperlocal Beacon",
            perk = RadarVisibilityPerk(
                id = "perk_day_3",
                name = "Signal Aura Glow (+50%)",
                description = "Expands your beacon luminescence across neighbor radar displays.",
                emoji = "✨",
                perkType = RadarPerkType.AURA_BRIGHTNESS,
                durationHours = 24,
                glowColorHex = 0xFF00E5FF
            ),
            badgeTitle = "Day 3 Neighborhood Pulse",
            badgeEmoji = "⚡",
            isMilestone = true
        ),
        CheckInStreakReward(
            dayNumber = 4,
            points = 150,
            title = "Day 4 Horizon Scanner",
            perk = RadarVisibilityPerk(
                id = "perk_day_4",
                name = "High Altitude Ping",
                description = "Enables elevated signal reception across urban obstacles.",
                emoji = "📡",
                perkType = RadarPerkType.HIGH_ALTITUDE_PING,
                durationHours = 24,
                glowColorHex = 0xFFFF9800
            ),
            isMilestone = false
        ),
        CheckInStreakReward(
            dayNumber = 5,
            points = 220,
            title = "Day 5 Community Anchor",
            perk = RadarVisibilityPerk(
                id = "perk_day_5",
                name = "Proximity Range Extender (2x)",
                description = "Doubles the reach of your neighbor signal blips for 24 hours.",
                emoji = "🌐",
                perkType = RadarPerkType.RANGE_BOOSTER,
                durationHours = 24,
                glowColorHex = 0xFF9C27B0
            ),
            badgeTitle = "Day 5 Community Anchor",
            badgeEmoji = "⚓",
            isMilestone = true
        ),
        CheckInStreakReward(
            dayNumber = 6,
            points = 300,
            title = "Day 6 Celestial Scout",
            perk = RadarVisibilityPerk(
                id = "perk_day_6",
                name = "Supercharged Pulse Aura",
                description = "Maximum luminescence blip with chromatic sonar wave.",
                emoji = "💫",
                perkType = RadarPerkType.AURA_BRIGHTNESS,
                durationHours = 24,
                glowColorHex = 0xFFFF4081
            ),
            isMilestone = false
        ),
        CheckInStreakReward(
            dayNumber = 7,
            points = 600,
            title = "Day 7 Radar Master Beacon",
            perk = RadarVisibilityPerk(
                id = "perk_day_7",
                name = "Golden Radar Beacon & Ring",
                description = "Crowns your center blip with a rotating golden ring and priority blip glow.",
                emoji = "👑",
                perkType = RadarPerkType.GOLDEN_BEACON,
                durationHours = 48,
                glowColorHex = 0xFFFFD700
            ),
            badgeTitle = "Day 7 Radar Master Beacon",
            badgeEmoji = "👑",
            isMilestone = true
        )
    )
}
