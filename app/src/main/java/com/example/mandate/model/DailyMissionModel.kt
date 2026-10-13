package com.example.mandate.model

/**
 * Categories of daily life tasks representing the core duality of Nigerian existence:
 * Family obligations (clan, wives, children, elders, black tax) vs
 * Political hustle (godfathers, motor parks, media, delegates, caucus) vs
 * National crises (strikes, shortages, youth unrest).
 */
enum class DailyMissionCategory(
    val title: String,
    val iconEmoji: String,
    val colorHex: Long
) {
    FAMILY_OBLIGATION(
        title = "Family Obligation",
        iconEmoji = "🏡",
        colorHex = 0xFF00E676 // Emerald Green
    ),
    POLITICAL_HUSTLE(
        title = "Political Hustle",
        iconEmoji = "🏛️",
        colorHex = 0xFFFFD700 // Naija Gold
    ),
    NATIONAL_CRISIS(
        title = "National Crisis Duty",
        iconEmoji = "🚨",
        colorHex = 0xFFFF3D00 // Alert Crimson
    )
}

/**
 * Lifecycle status of each daily mission task.
 */
enum class MissionStatus {
    AVAILABLE,
    IN_PROGRESS,
    COMPLETED,
    CLAIMED
}

/**
 * Individual daily mission tracking tasks, requirements, rewards, and locations.
 */
data class DailyMission(
    val id: String,
    val title: String,
    val category: DailyMissionCategory,
    val briefDescription: String,
    val targetStateName: String,
    val targetLocationName: String,
    val iconEmoji: String,
    val pidginFlavor: String,
    val costNaira: Long = 0L,
    val staminaCost: Int = 15,
    val rewardNaira: Long = 0L,
    val rewardClout: Int = 0,
    val rewardFamilyLoyalty: Int = 0,
    val rewardGodfatherFavor: Int = 0,
    val rewardConscienceDelta: Int = 0,
    val currentProgress: Int = 0,
    val maxProgress: Int = 1,
    val status: MissionStatus = MissionStatus.AVAILABLE
) {
    val isCompletedOrClaimed: Boolean
        get() = status == MissionStatus.COMPLETED || status == MissionStatus.CLAIMED
}

/**
 * Multi-tier Nigerian financial wallet:
 * Cash on Hand (₦) for street survival & Danfo fares,
 * Bank Account (₦) for high-level politics & digital transfers,
 * Domiciliary USD ($) to hedge against Naira inflation.
 */
data class CurrencyWallet(
    val cashNaira: Long = 65_000L,
    val bankNaira: Long = 420_000L,
    val domUsd: Long = 500L,
    val bdcExchangeRate: Long = 1_550L, // ₦ to $1
    val dailyCompoundGenCost: Long = 5_500L,
    val dailyCompoundFoodCost: Long = 12_000L,
    val dailyBlackTaxAllowance: Long = 8_000L
) {
    val totalNetWorthNaira: Long
        get() = cashNaira + bankNaira + (domUsd * bdcExchangeRate)

    val dailyLivingCostTotal: Long
        get() = dailyCompoundGenCost + dailyCompoundFoodCost + dailyBlackTaxAllowance
}

/**
 * Tracking the passage of time across the days of the week in Nigeria.
 */
enum class DayOfWeekName(val displayName: String, val shortName: String, val specialVibe: String) {
    MONDAY("Monday", "MON", "Morning rush; Sit-At-Home risk in South-East markets"),
    TUESDAY("Tuesday", "TUE", "Full commercial swing across all trade corridors"),
    WEDNESDAY("Wednesday", "WED", "Midweek political caucus & federal cabinet meetings"),
    THURSDAY("Thursday", "THU", "Market wholesale supply arrivals & haulage rush"),
    FRIDAY("Friday", "FRI", "Juma'at prayers, afternoon traffic gridlock & early Owambe vibes"),
    SATURDAY("Saturday", "SAT", "Mega Owambe weddings, political rallies & high-society parties"),
    SUNDAY("Sunday", "SUN", "Thanksgiving church services & quiet compound family meetings")
}
