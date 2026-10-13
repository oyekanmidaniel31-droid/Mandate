package com.example.mandate.model

/**
 * The Six Geopolitical Zones of the Federal Republic of Nigeria.
 */
enum class GeopoliticalZone(
    val title: String,
    val shortCode: String,
    val zoneDescription: String,
    val primaryColorHex: Long
) {
    SOUTH_WEST(
        title = "South-West Zone",
        shortCode = "SW",
        zoneDescription = "Commercial heartland, megacities, Yoruba cultural dynasty, cocoa & tech innovation.",
        primaryColorHex = 0xFFFFD700 // Gold
    ),
    FEDERAL_CAPITAL(
        title = "Federal Capital Territory",
        shortCode = "FCT",
        zoneDescription = "Seat of power, Aso Rock Villa, National Assembly, federal ministries & diplomatic quarters.",
        primaryColorHex = 0xFF00E676 // Presidential Emerald
    ),
    SOUTH_SOUTH(
        title = "South-South Zone (Niger Delta)",
        shortCode = "SS",
        zoneDescription = "Oil platforms, coastal mangrove creeks, refinery hubs & maritime resource battles.",
        primaryColorHex = 0xFF00B0FF // Petroleum Cyan
    ),
    SOUTH_EAST(
        title = "South-East Zone",
        shortCode = "SE",
        zoneDescription = "Vibrant commerce, manufacturing empires, Onitsha Main Market & indigenous enterprise.",
        primaryColorHex = 0xFFFF9100 // Trade Amber
    ),
    NORTH_WEST(
        title = "North-West Zone",
        shortCode = "NW",
        zoneDescription = "Ancient trans-Saharan trade corridors, groundnut pyramids, textile mills & massive voting blocs.",
        primaryColorHex = 0xFFE040FB // Dynastic Purple
    ),
    NORTH_CENTRAL(
        title = "North-Central Zone (Middle Belt)",
        shortCode = "NC",
        zoneDescription = "Food basket of the nation, temperate plateaus, mining fields, hydro dams & fertile plains.",
        primaryColorHex = 0xFF76FF03 // Agrarian Lime
    ),
    NORTH_EAST(
        title = "North-East Zone",
        shortCode = "NE",
        zoneDescription = "Lake Chad basin trade routes, livestock corridors, savannah frontiers & humanitarian resilience.",
        primaryColorHex = 0xFFFF5252 // Frontier Coral
    )
}

/**
 * Key states of Nigeria with their distinctive regional crisis, economic power, and political weight.
 */
data class NigeriaState(
    val id: String,
    val name: String,
    val hubCity: String,
    val zone: GeopoliticalZone,
    val flagEmoji: String,
    val primaryCrisis: CrisisType,
    val crisisNarrative: String,
    val dangerLevel: Int, // 0 - 100
    val delegateCount: Int, // Political primary delegates
    val primaryLandmark: String,
    val streetVibeSummary: String,
    val roadDanfoFareNaira: Long,
    val airFlightFareNaira: Long,
    val districtLandmarkCode: String
)

/**
 * Iconic States across Nigeria's federation with their authentic crises and culture.
 */
object NigeriaFederation {
    val LAGOS_STATE = NigeriaState(
        id = "STATE_LAGOS",
        name = "Lagos State",
        hubCity = "Ikeja / Victoria Island",
        zone = GeopoliticalZone.SOUTH_WEST,
        flagEmoji = "🌊",
        primaryCrisis = CrisisType.FUEL_SCARCITY,
        crisisNarrative = "Gridlock petrol queues stretching 3km along Ikorodu Road; Agbero transport unions hike Danfo fares by 250%.",
        dangerLevel = 45,
        delegateCount = 110,
        primaryLandmark = "Lekki-Ikoyi Toll Bridge & Computer Village",
        streetVibeSummary = "High-octane commercial scramble, sirens, fuji beats & endless yellow Danfo convoys.",
        roadDanfoFareNaira = 0L, // Home base
        airFlightFareNaira = 0L,
        districtLandmarkCode = "LAGOS_COMMERCIAL"
    )

    val FCT_ABUJA = NigeriaState(
        id = "STATE_ABUJA",
        name = "FCT Abuja",
        hubCity = "Three Arms Zone / Maitama",
        zone = GeopoliticalZone.FEDERAL_CAPITAL,
        flagEmoji = "🏛️",
        primaryCrisis = CrisisType.GRID_COLLAPSE,
        crisisNarrative = "National electricity grid collapses for the 4th time this month; deafening diesel generator hum surrounds federal ministries and embassies.",
        dangerLevel = 30,
        delegateCount = 75,
        primaryLandmark = "Aso Rock Presidential Villa & National Assembly",
        streetVibeSummary = "Wide smooth boulevards, heavily tinted black SUV convoys, whispers of caucus intrigue in Transcorp corridors.",
        roadDanfoFareNaira = 14_000L,
        airFlightFareNaira = 95_000L,
        districtLandmarkCode = "ABUJA_VILLA"
    )

    val RIVERS_STATE = NigeriaState(
        id = "STATE_RIVERS",
        name = "Rivers State",
        hubCity = "Port Harcourt",
        zone = GeopoliticalZone.SOUTH_SOUTH,
        flagEmoji = "🛢️",
        primaryCrisis = CrisisType.NARA_CRASH,
        crisisNarrative = "Crude oil pipeline bunkering and soot pollution trigger maritime creek confrontations; multinational oil barges under naval patrol.",
        dangerLevel = 65,
        delegateCount = 95,
        primaryLandmark = "Trans-Amadi Industrial Complex & Bonny River Pier",
        streetVibeSummary = "Bole & roasted fish aromas, heavily armed MOPOL gunboats in creeks, heavy oil-industry swagger.",
        roadDanfoFareNaira = 18_000L,
        airFlightFareNaira = 115_000L,
        districtLandmarkCode = "PH_OIL_CREEKS"
    )

    val KANO_STATE = NigeriaState(
        id = "STATE_KANO",
        name = "Kano State",
        hubCity = "Kano Municipal",
        zone = GeopoliticalZone.NORTH_WEST,
        flagEmoji = "🕌",
        primaryCrisis = CrisisType.NARA_CRASH,
        crisisNarrative = "Cross-border currency volatility and Sahelian food grain hoarding cause price spikes in Sabon Gari market; traders demand central intervention.",
        dangerLevel = 50,
        delegateCount = 135,
        primaryLandmark = "Ancient Kano City Walls & Sabon Gari Market",
        streetVibeSummary = "Textile rolls stacked to ceilings, fragrant spice alleys, honking tricycles & powerful political emirs.",
        roadDanfoFareNaira = 22_000L,
        airFlightFareNaira = 125_000L,
        districtLandmarkCode = "KANO_SABON_GARI"
    )

    val ANAMBRA_STATE = NigeriaState(
        id = "STATE_ANAMBRA",
        name = "Anambra State",
        hubCity = "Onitsha / Awka",
        zone = GeopoliticalZone.SOUTH_EAST,
        flagEmoji = "📦",
        primaryCrisis = CrisisType.POLICE_BRUTALITY,
        crisisNarrative = "Monday Sit-At-Home lockdown paralyses West Africa's biggest market; billions of Naira in retail trade hemorrhaged as heavy trailers queue at Niger Bridge.",
        dangerLevel = 60,
        delegateCount = 85,
        primaryLandmark = "River Niger Bridge & Onitsha Main Market",
        streetVibeSummary = "Wholesale electronics empires, relentless merchant bargaining, motor spare parts hubs & church bell chimes.",
        roadDanfoFareNaira = 16_000L,
        airFlightFareNaira = 105_000L,
        districtLandmarkCode = "ONITSHA_BRIDGE"
    )

    val BENUE_PLATEAU = NigeriaState(
        id = "STATE_BENUE",
        name = "Benue & Plateau",
        hubCity = "Makurdi / Jos",
        zone = GeopoliticalZone.NORTH_CENTRAL,
        flagEmoji = "🌾",
        primaryCrisis = CrisisType.FLOOD_DISASTER,
        crisisNarrative = "Farmland flooding and communal pastoralist disputes choke yam and maize shipments destined for Southern urban centers; food inflation hits 38%.",
        dangerLevel = 55,
        delegateCount = 80,
        primaryLandmark = "River Benue Food Basin & Shere Hills Jos",
        streetVibeSummary = "Fresh harvest yam pyramids, cool mountain breeze, bustling produce depots & traditional clan elders.",
        roadDanfoFareNaira = 17_000L,
        airFlightFareNaira = 110_000L,
        districtLandmarkCode = "BENUE_FOOD_BASKET"
    )

    val BORNO_STATE = NigeriaState(
        id = "STATE_BORNO",
        name = "Borno State",
        hubCity = "Maiduguri",
        zone = GeopoliticalZone.NORTH_EAST,
        flagEmoji = "🛡️",
        primaryCrisis = CrisisType.BANDITRY_EXPEDITION,
        crisisNarrative = "Remnant border skirmishes around Lake Chad basin; military checkpoints escort humanitarian grain convoys amidst drought heat.",
        dangerLevel = 75,
        delegateCount = 70,
        primaryLandmark = "Shehu of Borno Palace & Monday Market",
        streetVibeSummary = "Desert winds, resilient street vendors, fortified security outposts, camel caravans & warm tea gatherings.",
        roadDanfoFareNaira = 28_000L,
        airFlightFareNaira = 140_000L,
        districtLandmarkCode = "MAIDUGURI_SAVANNAH"
    )

    val OYO_STATE = NigeriaState(
        id = "STATE_OYO",
        name = "Oyo State",
        hubCity = "Ibadan",
        zone = GeopoliticalZone.SOUTH_WEST,
        flagEmoji = "🏛️",
        primaryCrisis = CrisisType.STUDENT_STRIKE,
        crisisNarrative = "Premier University students lead massive highway blockades over 6-month ASUU strike; Ibadan political godfathers convene emergency council at Bodija.",
        dangerLevel = 40,
        delegateCount = 90,
        primaryLandmark = "Mapo Hall & Bodija Food Emporium",
        streetVibeSummary = "Ancient rusty brown roofs, deep political godfather pedigree, sweet amala bakkas & talking drum salutes.",
        roadDanfoFareNaira = 4_500L,
        airFlightFareNaira = 65_000L,
        districtLandmarkCode = "IBADAN_MAPO"
    )

    val KADUNA_STATE = NigeriaState(
        id = "STATE_KADUNA",
        name = "Kaduna & Zamfara",
        hubCity = "Kaduna Central",
        zone = GeopoliticalZone.NORTH_WEST,
        flagEmoji = "🚂",
        primaryCrisis = CrisisType.BANDITRY_EXPEDITION,
        crisisNarrative = "Highway banditry attempts along the interstate rail corridor repelled by security squads; mining communities demand federal protection.",
        dangerLevel = 70,
        delegateCount = 100,
        primaryLandmark = "Ahmadu Bello Way & Kaduna Rail Terminal",
        streetVibeSummary = "Military barracks tradition, bustling textile markets, suya night spots & political strategy summits.",
        roadDanfoFareNaira = 19_000L,
        airFlightFareNaira = 115_000L,
        districtLandmarkCode = "KADUNA_RAIL"
    )

    val allStates: List<NigeriaState> = listOf(
        LAGOS_STATE,
        FCT_ABUJA,
        RIVERS_STATE,
        KANO_STATE,
        ANAMBRA_STATE,
        BENUE_PLATEAU,
        BORNO_STATE,
        OYO_STATE,
        KADUNA_STATE
    )

    fun getStateById(id: String): NigeriaState {
        return allStates.find { it.id == id } ?: LAGOS_STATE
    }
}
