package com.example.mandate.model

/**
 * Tracks the player's core three resource pillars:
 * 1. Influence: Political capital, street clout, godfather favor, media spin control.
 * 2. Wealth: Physical cash, bank savings, foreign exchange (USD), net worth, daily burn.
 * 3. Family Loyalty: Clan solidarity, elders' blessing/approval, black tax burden, dependents.
 */
data class InfluenceMetrics(
    val politicalCapital: Int = 25,       // 0 - 100: institutional power
    val streetClout: Int = 40,             // 0 - 100: motor park and grassroots mobilization
    val godfatherFavor: Int = 35,          // 0 - 100: kingmaker standing
    val mediaSpinControl: Int = 30,        // 0 - 100: press headlines & narrative control
    val lastDelta: Int = 0,
    val lastDeltaReason: String = "Grassroots ward caucus alignment"
) {
    val compositeScore: Int
        get() = ((politicalCapital * 0.35) + (streetClout * 0.30) + (godfatherFavor * 0.20) + (mediaSpinControl * 0.15)).toInt().coerceIn(0, 100)

    val tierTitle: String
        get() = when {
            compositeScore >= 80 -> "Villa Mandate Kingmaker"
            compositeScore >= 60 -> "State Political Heavyweight"
            compositeScore >= 40 -> "Ward Council Enforcer"
            compositeScore >= 20 -> "Grassroots Mobilizer"
            else -> "Street Hustler"
        }

    val tierEmoji: String
        get() = when {
            compositeScore >= 80 -> "👑"
            compositeScore >= 60 -> "🏛️"
            compositeScore >= 40 -> "💼"
            compositeScore >= 20 -> "🎤"
            else -> "🛵"
        }
}

data class WealthMetrics(
    val cashNaira: Long = 45_000L,
    val bankNaira: Long = 250_000L,
    val domUsd: Long = 500L,
    val bdcRate: Long = 1_650L,
    val dailyBurnNaira: Long = 18_500L,
    val lastDeltaNaira: Long = 0L,
    val lastDeltaReason: String = "Daily living and transport logistics"
) {
    val netWorthNaira: Long
        get() = cashNaira + bankNaira + (domUsd * bdcRate)

    val wealthTier: String
        get() = when {
            netWorthNaira >= 500_000_000L -> "Billionaire Oligarch"
            netWorthNaira >= 50_000_000L -> "High-Net-Worth Chieftain"
            netWorthNaira >= 5_000_000L -> "Comfortable Politico"
            netWorthNaira >= 500_000L -> "Rising Middle-Class"
            else -> "Struggling Street Earner"
        }

    val wealthEmoji: String
        get() = when {
            netWorthNaira >= 500_000_000L -> "🏦"
            netWorthNaira >= 50_000_000L -> "💎"
            netWorthNaira >= 5_000_000L -> "💼"
            netWorthNaira >= 500_000L -> "💵"
            else -> "🪙"
        }
}

data class FamilyLoyaltyMetrics(
    val clanLoyaltyScore: Int = 65,       // 0 - 100: general compound solidarity
    val eldersApprovalScore: Int = 58,    // 0 - 100: council of elders backing
    val blackTaxBurdenIndex: Int = 45,    // 0 - 100: relative financial pressure from dependents
    val activeDependentsCount: Int = 8,
    val pendingBlackTaxAmount: Long = 85_000L,
    val lastDelta: Int = 0,
    val lastDeltaReason: String = "Family hospital bill assistance"
) {
    val solidarityTier: String
        get() = when {
            clanLoyaltyScore >= 80 -> "Unbreakable Dynasty"
            clanLoyaltyScore >= 60 -> "Solid Kinship Pillar"
            clanLoyaltyScore >= 40 -> "Cautious Extended Ties"
            clanLoyaltyScore >= 20 -> "Strained Clan Disputes"
            else -> "Family Rebellion Risk"
        }

    val solidarityEmoji: String
        get() = when {
            clanLoyaltyScore >= 80 -> "🛡️"
            clanLoyaltyScore >= 60 -> "👨‍👩‍👧‍👦"
            clanLoyaltyScore >= 40 -> "🤝"
            clanLoyaltyScore >= 20 -> "⚠️"
            else -> "💔"
        }
}

data class PlayerResources(
    val influence: InfluenceMetrics = InfluenceMetrics(),
    val wealth: WealthMetrics = WealthMetrics(),
    val familyLoyalty: FamilyLoyaltyMetrics = FamilyLoyaltyMetrics(),
    val resourceAuditLog: List<String> = listOf(
        "Compound allegiance established with elders' blessings.",
        "Opened commercial bank account and mobile wallet.",
        "Joined party grassroots ward mobilization committee."
    ),
    val lastActionTimestamp: Long = System.currentTimeMillis()
)
