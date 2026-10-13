package com.example.mandate.model

enum class SecurityLevel(val label: String, val severityColorHex: Long) {
    PEACEFUL("Stable & Controlled", 0xFF2E7D32),
    TENSION("Ethnic & Economic Tension", 0xFFF57F17),
    NATIONWIDE_STRIKE("General Workers Strike", 0xFFE65100),
    YOUTH_UPRISING("Mass #EndWahala Street Uprising", 0xFFD32F2F),
    CURFEW("Military Dusk-to-Dawn Curfew", 0xFFB71C1C)
}

enum class CrisisType(
    val title: String,
    val shortDesc: String,
    val angerImpact: Int = 15,
    val iconEmoji: String = "⚠️",
    val primarySector: String = "Nationwide"
) {
    FUEL_SCARCITY(
        title = "Fuel Subsidy Removal & Scarcity",
        shortDesc = "Pump price jumped to ₦950/L; transport fares triple across Lakos & Abuka",
        angerImpact = 22,
        iconEmoji = "⛽",
        primarySector = "Transport & Motor-Parks"
    ),
    NARA_CRASH(
        title = "Nara Currency Crash",
        shortDesc = "Exchange rate hits ₦1,650/$; food and medicine import costs double overnight",
        angerImpact = 18,
        iconEmoji = "💸",
        primarySector = "Market Traders & Importers"
    ),
    STUDENT_STRIKE(
        title = "6-Month University Strike (ASUU)",
        shortDesc = "Campuses shut down indefinitely; millions of frustrated youths flood streets",
        angerImpact = 16,
        iconEmoji = "🎓",
        primarySector = "Students & Youth Comrades"
    ),
    POLICE_BRUTALITY(
        title = "Anti-Cult Squad Extortion Outrage",
        shortDesc = "Viral video of student harassment triggers massive roadblock bonfires",
        angerImpact = 24,
        iconEmoji = "🚨",
        primarySector = "Jobless & Tech Youth"
    ),
    GRID_COLLAPSE(
        title = "National Electric Grid Collapse",
        shortDesc = "Zero megawatts nationwide; sound of 10 million diesel generators chokes cities",
        angerImpact = 14,
        iconEmoji = "⚡",
        primarySector = "Small Businesses & Artisans"
    ),
    BANDITRY_EXPEDITION(
        title = "Interstate Highway Sieges",
        shortDesc = "Interstate bus passengers hijacked along Kaduna-Abuka expressway",
        angerImpact = 15,
        iconEmoji = "🛡️",
        primarySector = "Interstate Commuters"
    ),
    FLOOD_DISASTER(
        title = "River Niger Farmland Inundation",
        shortDesc = "Food production hub submerged; yam and cassava prices soar 70%",
        angerImpact = 12,
        iconEmoji = "🌾",
        primarySector = "Farmers & Market Women"
    ),
    FAKE_NEWS_VIRAL(
        title = "WhatsApp Viral Military Rumour",
        shortDesc = "Doctored broadcast audio claims armoured tanks are rolling into the capital",
        angerImpact = 10,
        iconEmoji = "📢",
        primarySector = "Civil Society"
    )
}

data class SectorTension(
    val sectorName: String,
    val angerLevel: Int, // 0 - 100
    val grievanceSummary: String,
    val iconEmoji: String
)

data class MacroEconomy(
    val angerIndex: Int = 42, // 0-100: triggers uprisings when > 70
    val naraToDollar: Double = 1250.0,
    val oilBarrelPrice: Double = 82.5,
    val fuelPumpPrice: Int = 680,
    val securityLevel: SecurityLevel = SecurityLevel.PEACEFUL,
    val treasuryReservesBillionUsd: Double = 33.5,
    val nationalDebtTrillionNara: Double = 87.0,
    val activeCrises: List<CrisisType> = listOf(
        CrisisType.FUEL_SCARCITY,
        CrisisType.STUDENT_STRIKE
    ),
    val studentAnger: Int = 55,
    val traderAnger: Int = 45,
    val transportAnger: Int = 60,
    val youthAnger: Int = 58,
    val workerAnger: Int = 38
) {
    val nationalStabilityPercentage: Int
        get() = (100 - angerIndex).coerceIn(0, 100)

    val coupRiskPercentage: Int
        get() = when {
            angerIndex >= 85 -> 78
            angerIndex >= 70 -> 45
            angerIndex >= 50 -> 20
            else -> 6
        }

    val riotRiskPercentage: Int
        get() = when {
            angerIndex >= 80 -> 88
            angerIndex >= 65 -> 62
            angerIndex >= 45 -> 34
            else -> 10
        }

    val sectors: List<SectorTension>
        get() = listOf(
            SectorTension(
                sectorName = "Students & SUG Unions",
                angerLevel = studentAnger,
                grievanceSummary = "Campus closures, stalled graduation dates, and hostel dilapidation",
                iconEmoji = "🎓"
            ),
            SectorTension(
                sectorName = "Transport & Danfo Drivers",
                angerLevel = transportAnger,
                grievanceSummary = "Petrol pump prices at ₦$fuelPumpPrice/L and multiple extortion checkpoints",
                iconEmoji = "🚌"
            ),
            SectorTension(
                sectorName = "Jobless & Tech Youth",
                angerLevel = youthAnger,
                grievanceSummary = "High cost of living, police harassment, and crypto banking bans",
                iconEmoji = "📱"
            ),
            SectorTension(
                sectorName = "Market Traders & Women",
                angerLevel = traderAnger,
                grievanceSummary = "Nara devaluation ($1 = ₦${naraToDollar.toInt()}) eroding sales margins",
                iconEmoji = "🏪"
            ),
            SectorTension(
                sectorName = "Civil Servants & Labour",
                angerLevel = workerAnger,
                grievanceSummary = "Minimum wage purchasing power wiped out by inflation",
                iconEmoji = "💼"
            )
        )
}
