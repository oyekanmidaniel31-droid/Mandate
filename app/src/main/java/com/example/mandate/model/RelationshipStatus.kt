package com.example.mandate.model

/**
 * Categorical status tier reflecting the qualitative bond between the player
 * and their clan family member or street syndicate padi.
 */
enum class RelationshipTier(
    val title: String,
    val tag: String,
    val badgeColorHex: Long,
    val description: String,
    val iconEmoji: String
) {
    DEVOTED_ALLIANCE(
        title = "Ride-or-Die / Blood Alliance",
        tag = "🛡️ ALLIED",
        badgeColorHex = 0xFF2E7D32,
        description = "Will stand through brawls, police arrests, and electoral wars without flinching.",
        iconEmoji = "🛡️"
    ),
    WARM_CONFIDANT(
        title = "Warm Confidant",
        tag = "🤝 INNER CIRCLE",
        badgeColorHex = 0xFF1976D2,
        description = "High trust and mutual respect; shares private family secrets and political intelligence.",
        iconEmoji = "🤝"
    ),
    CORDIAL_PARTNER(
        title = "Cordial & Respectful",
        tag = "✨ CORDIAL",
        badgeColorHex = 0xFF00796B,
        description = "Solid domestic and social standing; enjoys visits, advice, and festive greetings.",
        iconEmoji = "✨"
    ),
    TRANSACTIONAL(
        title = "Transactional Dependency",
        tag = "⚖️ TRANSACTIONAL",
        badgeColorHex = 0xFFE65100,
        description = "Expects continuous cash remittances, party contracts, or black tax before offering cooperation.",
        iconEmoji = "⚖️"
    ),
    STRAINED_SUSPICIOUS(
        title = "Strained & Suspicious",
        tag = "⚠️ STRAINED",
        badgeColorHex = 0xFFC2185B,
        description = "Grumbling over unpaid remittances or perceived arrogance; harboring covert grievances.",
        iconEmoji = "⚠️"
    ),
    BITTER_RIVAL(
        title = "Hostile Rivalry",
        tag = "🔥 HOSTILE",
        badgeColorHex = 0xFFB71C1C,
        description = "Actively plotting counter-moves, leaking spicy gossips to bloggers, or funding rivals.",
        iconEmoji = "🔥"
    ),
    ESTRANGED(
        title = "Estranged / Cold Cut",
        tag = "🧊 ESTRANGED",
        badgeColorHex = 0xFF455A64,
        description = "Refuses regular greetings. Deep emotional rift following ignored emergencies or public humiliation.",
        iconEmoji = "🧊"
    );

    companion object {
        fun fromScores(
            happiness: Int,
            loyalty: Int,
            isEstranged: Boolean = false,
            isHostileRival: Boolean = false
        ): RelationshipTier {
            if (isEstranged) return ESTRANGED
            if (isHostileRival && loyalty < 45) return BITTER_RIVAL
            val average = (happiness + loyalty) / 2
            return when {
                average >= 80 && loyalty >= 80 -> DEVOTED_ALLIANCE
                average >= 68 && loyalty >= 60 -> WARM_CONFIDANT
                happiness < 40 && loyalty < 40 -> STRAINED_SUSPICIOUS
                happiness < 48 && loyalty >= 50 -> TRANSACTIONAL
                loyalty < 35 -> BITTER_RIVAL
                else -> CORDIAL_PARTNER
            }
        }
    }
}

/**
 * Metric analysis helpers for display across HUD overlays.
 */
object RelationshipMetrics {
    fun getHappinessMood(happiness: Int): Pair<String, String> = when {
        happiness >= 85 -> "Overjoyed & Blessed" to "😊"
        happiness >= 70 -> "Content & Peaceful" to "🙂"
        happiness >= 50 -> "Neutral / Observing" to "😐"
        happiness >= 30 -> "Restless & Grumbling" to "😟"
        else -> "Bitter & Vexed" to "😡"
    }

    fun getLoyaltyStance(loyalty: Int): Pair<String, String> = when {
        loyalty >= 85 -> "Unshakable Ride-or-Die" to "🛡️"
        loyalty >= 70 -> "Faithful & Dependable" to "🤝"
        loyalty >= 50 -> "Guarded & Cautious" to "👀"
        loyalty >= 30 -> "Wavering & Calculating" to "⚠️"
        else -> "Compromised / Snitching" to "🐍"
    }
}

/**
 * Unified data representation used by Relationship Tracker UI Overlays,
 * bridging both Family Members and Syndicate Padis into a synchronized model.
 */
data class RelationshipTrackRecord(
    val id: String,
    val name: String,
    val roleOrSubtitle: String,
    val isFamily: Boolean,
    val happiness: Int,
    val loyalty: Int,
    val tier: RelationshipTier,
    val moodLabel: String,
    val moodEmoji: String,
    val loyaltyLabel: String,
    val loyaltyEmoji: String,
    val summaryGist: String,
    val activeDemands: String,
    val strategicBenefit: String,
    val recentMilestones: List<String> = emptyList(),
    val avatarEmoji: String = if (isFamily) "🏡" else "🤝",
    val familyMember: FamilyMember? = null,
    val padi: Padi? = null
)
