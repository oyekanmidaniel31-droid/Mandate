package com.example.mandate.model

enum class KinshipRole(val title: String) {
    MOTHER("Biological Mother"),
    FATHER("Father / Head of Household"),
    CO_WIFE("Rival Step-Mother"),
    HALF_SIBLING("Half-Sibling"),
    FULL_SIBLING("Full Sibling"),
    VILLAGE_ELDER("Village Elder")
}

data class FamilyMember(
    val id: String,
    val name: String,
    val role: KinshipRole,
    val affection: Int, // 0 - 100
    val demands: String,
    val isAlive: Boolean = true,
    val happiness: Int = affection, // 0 - 100
    val loyalty: Int = when (role) {
        KinshipRole.MOTHER -> 95
        KinshipRole.FULL_SIBLING -> 82
        KinshipRole.FATHER -> 75
        KinshipRole.HALF_SIBLING -> 60
        KinshipRole.VILLAGE_ELDER -> 65
        KinshipRole.CO_WIFE -> 35
    },
    val recentInteractions: List<String> = emptyList()
) {
    fun toTrackRecord(): RelationshipTrackRecord {
        val tier = RelationshipTier.fromScores(
            happiness = happiness,
            loyalty = loyalty,
            isHostileRival = role == KinshipRole.CO_WIFE && loyalty < 45
        )
        val (hMood, hEmoji) = RelationshipMetrics.getHappinessMood(happiness)
        val (lStance, lEmoji) = RelationshipMetrics.getLoyaltyStance(loyalty)
        return RelationshipTrackRecord(
            id = id,
            name = name,
            roleOrSubtitle = role.title,
            isFamily = true,
            happiness = happiness,
            loyalty = loyalty,
            tier = tier,
            moodLabel = hMood,
            moodEmoji = hEmoji,
            loyaltyLabel = lStance,
            loyaltyEmoji = lEmoji,
            summaryGist = when (role) {
                KinshipRole.MOTHER -> "Pours out daily maternal prayers and fasts for your protection in politics."
                KinshipRole.FATHER -> "Demands excellence, moral discipline, and preservation of clan land."
                KinshipRole.CO_WIFE -> "Watches household resource allocations with sharp eyes, easily provoked."
                KinshipRole.FULL_SIBLING -> "Staunch confidant who keeps quiet on family secrets and backs your plays."
                KinshipRole.HALF_SIBLING -> "Prone to comparison, looking for financial lifelines or sponsorship."
                KinshipRole.VILLAGE_ELDER -> "Custodians of village ancestral legitimacy; controls community ward votes."
            },
            activeDemands = demands,
            strategicBenefit = when (role) {
                KinshipRole.MOTHER -> "Spiritual immunity & +10 Conscience resilience during moral crises."
                KinshipRole.FATHER -> "Patriarchal legitimacy & direct backing from traditional rulers."
                KinshipRole.CO_WIFE -> "Neutralizing her enmity prevents domestic sabotage and poison rumors."
                KinshipRole.FULL_SIBLING -> "Loyal campaign aide who manages private wealth and trusted couriers."
                KinshipRole.HALF_SIBLING -> "Extends grassroots network across youth wards and trade unions."
                KinshipRole.VILLAGE_ELDER -> "Mass clan delegate endorsement in party gubernatorial primaries."
            },
            recentMilestones = recentInteractions.ifEmpty {
                listOf("Maintains ancestral ties in the family compound")
            },
            avatarEmoji = when (role) {
                KinshipRole.MOTHER -> "👵"
                KinshipRole.FATHER -> "👴"
                KinshipRole.CO_WIFE -> "💅"
                KinshipRole.FULL_SIBLING -> "🧑"
                KinshipRole.HALF_SIBLING -> "👦"
                KinshipRole.VILLAGE_ELDER -> "👑"
            },
            familyMember = this
        )
    }
}

data class BlackTaxRequest(
    val id: String,
    val fromMemberName: String,
    val reason: String,
    val amountRequested: Long,
    val pidginPlea: String,
    val consequencesIfIgnored: String
)
