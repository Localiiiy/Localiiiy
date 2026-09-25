package com.example.data

data class InvitedNeighbor(
    val id: String,
    val name: String,
    val username: String,
    val avatarUrl: String,
    val joinedTimeAgo: String,
    val status: String, // "Active Neighbor", "Verified Resident", "Streak Leader"
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
    val userReferralCode: String = "LOCAL-ALEX-7821",
    val referralLink: String = "https://localiiiy.web.app/join/LOCAL-ALEX-7821",
    val totalNeighborsInvited: Int = 4,
    val bonusPointsEarned: Int = 450,
    val activeBoostTitle: String = "2x Radar Reach Booster (36h left)",
    val boostActiveUntil: Long = System.currentTimeMillis() + (36 * 3600 * 1000L),
    val hasRedeemedFriendCode: Boolean = false,
    val redeemedReferrerName: String? = null,
    val invitedNeighbors: List<InvitedNeighbor> = listOf(
        InvitedNeighbor(
            id = "inv_1",
            name = "Marcus Chen",
            username = "@marcus_c",
            avatarUrl = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=200&auto=format&fit=crop&q=80",
            joinedTimeAgo = "2 days ago",
            status = "Verified Resident 🏘️",
            bonusPointsAwarded = 100,
            perkAwarded = "+12h Radar Pulse Boost"
        ),
        InvitedNeighbor(
            id = "inv_2",
            name = "Elena Rostova",
            username = "@elena_local",
            avatarUrl = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=200&auto=format&fit=crop&q=80",
            joinedTimeAgo = "4 days ago",
            status = "Streak Leader 🔥",
            bonusPointsAwarded = 100,
            perkAwarded = "+12h Radar Pulse Boost"
        ),
        InvitedNeighbor(
            id = "inv_3",
            name = "Derrick Vance",
            username = "@dvance_tech",
            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&auto=format&fit=crop&q=80",
            joinedTimeAgo = "1 week ago",
            status = "Active Neighbor ✨",
            bonusPointsAwarded = 125,
            perkAwarded = "48h 2x Radar Reach"
        ),
        InvitedNeighbor(
            id = "inv_4",
            name = "Priya Sharma",
            username = "@priya_design",
            avatarUrl = "https://images.unsplash.com/photo-1524504388940-b1c1722653e1?w=200&auto=format&fit=crop&q=80",
            joinedTimeAgo = "2 weeks ago",
            status = "Certified Provider 💼",
            bonusPointsAwarded = 125,
            perkAwarded = "Community Connector Badge"
        )
    )
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
