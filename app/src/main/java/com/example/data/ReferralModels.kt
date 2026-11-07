package com.example.data

data class InvitedNeighbor(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val joinedTimeAgo: String,
    val status: String,
    val bonusPointsAwarded: Int,
    val perkAwarded: String
)

data class ReferralMilestone(
    val inviteCount: Int,
    val title: String,
    val badgeEmoji: String,
    val bonusReward: String,
    val isAchieved: Boolean
)

data class NeighborReferralState(
    val userReferralCode: String = "LOCAL-INVITE",
    val referralLink: String = "https://localiiiy.web.app/join",
    val totalNeighborsInvited: Int = 0,
    val bonusPointsEarned: Int = 0,
    val activeBoostTitle: String = "No active referral boost",
    val boostActiveUntil: Long = 0L,
    val hasRedeemedFriendCode: Boolean = false,
    val redeemedReferrerName: String? = null,
    val invitedNeighbors: List<InvitedNeighbor> = emptyList()
) {
    fun getMilestones(): List<ReferralMilestone> = listOf(
        ReferralMilestone(
            inviteCount = 1,
            title = "First Neighbor Connected",
            badgeEmoji = "🤝",
            bonusReward = "+100 Points & 12h Radar Signal Booster",
            isAchieved = totalNeighborsInvited >= 1
        ),
        ReferralMilestone(
            inviteCount = 3,
            title = "Neighborhood Connector",
            badgeEmoji = "⚡",
            bonusReward = "48h 2x Radar Reach & Connector Badge",
            isAchieved = totalNeighborsInvited >= 3
        ),
        ReferralMilestone(
            inviteCount = 5,
            title = "Local Ambassador",
            badgeEmoji = "🎖️",
            bonusReward = "Permanent Golden Radar Ring & +250 Points",
            isAchieved = totalNeighborsInvited >= 5
        ),
        ReferralMilestone(
            inviteCount = 10,
            title = "Community Pillar",
            badgeEmoji = "🏛️",
            bonusReward = "$50 Credit towards $1,000 Payout Threshold & Earth Distribution Priority",
            isAchieved = totalNeighborsInvited >= 10
        )
    )
}
