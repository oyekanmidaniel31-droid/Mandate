package com.example.mandate.model

enum class PadiArchetype(val title: String, val traitSummary: String) {
    RIDE_OR_DIE("Ride-or-Die", "Will carry your secrets to the grave, ready for motor-park brawls"),
    RICH_PADI("Rich Padi", "Heir to oil blocks or private jets. Always spraying mint cash at clubs"),
    ACTIVIST("Activist / Comrade", "Fierce voice against injustice. Leads protests, suspicious of power"),
    GOSSIP_BLOGGER("Gossip / Media Eye", "Runs viral blogs and street gossip. Knows who is sleeping with whom"),
    SNAKE_RIVAL("Ambitious Snake", "Smiles to your face while keeping notes to leak to the opposition")
}

data class Padi(
    val id: String,
    val name: String,
    val nickname: String,
    val archetype: PadiArchetype,
    val loyalty: Int, // 0 - 100
    val netWorth: Long,
    val occupation: String,
    val bio: String,
    val isEstranged: Boolean = false,
    val isDeceased: Boolean = false,
    val memoryOfPlayer: String = "Remembers when you two shared cold gala and pure water in Lakos traffic.",
    val happiness: Int = when (archetype) {
        PadiArchetype.RIDE_OR_DIE -> 85
        PadiArchetype.RICH_PADI -> 75
        PadiArchetype.ACTIVIST -> 65
        PadiArchetype.GOSSIP_BLOGGER -> 60
        PadiArchetype.SNAKE_RIVAL -> 45
    },
    val recentInteractions: List<String> = emptyList()
) {
    fun toTrackRecord(): RelationshipTrackRecord {
        val tier = RelationshipTier.fromScores(
            happiness = happiness,
            loyalty = loyalty,
            isEstranged = isEstranged,
            isHostileRival = archetype == PadiArchetype.SNAKE_RIVAL && loyalty < 45
        )
        val (hMood, hEmoji) = RelationshipMetrics.getHappinessMood(happiness)
        val (lStance, lEmoji) = RelationshipMetrics.getLoyaltyStance(loyalty)
        return RelationshipTrackRecord(
            id = id,
            name = "$nickname ($name)",
            roleOrSubtitle = archetype.title,
            isFamily = false,
            happiness = happiness,
            loyalty = loyalty,
            tier = tier,
            moodLabel = hMood,
            moodEmoji = hEmoji,
            loyaltyLabel = lStance,
            loyaltyEmoji = lEmoji,
            summaryGist = archetype.traitSummary,
            activeDemands = "Memory: \"$memoryOfPlayer\"",
            strategicBenefit = when (archetype) {
                PadiArchetype.RIDE_OR_DIE -> "Commands street syndicates, park agberos, and physical protection."
                PadiArchetype.RICH_PADI -> "Provides multi-million Naira private loans and luxury Owambe prestige."
                PadiArchetype.ACTIVIST -> "Mobilizes student unions, protests, and defends against legal charges."
                PadiArchetype.GOSSIP_BLOGGER -> "Can kill scandalous blog leaks or weaponize viral exposés against rivals."
                PadiArchetype.SNAKE_RIVAL -> "Infiltrates opposing political ward meetings, but demands watching."
            },
            recentMilestones = recentInteractions.ifEmpty {
                listOf("Solidarity pact active across Lagos streets")
            },
            avatarEmoji = when (archetype) {
                PadiArchetype.RIDE_OR_DIE -> "🤛"
                PadiArchetype.RICH_PADI -> "🍾"
                PadiArchetype.ACTIVIST -> "✊"
                PadiArchetype.GOSSIP_BLOGGER -> "📸"
                PadiArchetype.SNAKE_RIVAL -> "🐍"
            },
            padi = this
        )
    }
}
