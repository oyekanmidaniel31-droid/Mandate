package com.example.mandate.model

import com.example.mandate.ui.components.NPCHeadgear

/**
 * Iconic districts of Lagos representing diverse facets of the megalopolis:
 * transit hubs, tech markets, financial towers, high-society Owambe strips, and street compounds.
 */
enum class LagosDistrict(
    val displayName: String,
    val subtitle: String,
    val landmarkCode: String,
    val defaultTrafficRisk: Int, // 0 - 100% "Hold-Up" probability
    val dangerLevel: Int,        // 0 - 100% Street tension
    val baseTransitFare: Long,   // Base Danfo transit fee in Naira
    val primaryColorHex: Long,
    val soundAmbienceDescription: String
) {
    OSHODI_OJUELEGBA(
        displayName = "Oshodi & Ojuelegba",
        subtitle = "Transit Epicenter & Danfo Motor Parks",
        landmarkCode = "DANFO_JUNCTION",
        defaultTrafficRisk = 85,
        dangerLevel = 60,
        baseTransitFare = 300L,
        primaryColorHex = 0xFFFFD700, // Danfo Yellow
        soundAmbienceDescription = "Loud Danfo conductor chants, fuji street speakers & revving engines"
    ),
    IKEJA_COMPUTER_VILLAGE(
        displayName = "Ikeja Computer Village",
        subtitle = "Otigba Street Tech & Hustle Alley",
        landmarkCode = "TECH_PLAZA",
        defaultTrafficRisk = 65,
        dangerLevel = 40,
        baseTransitFare = 500L,
        primaryColorHex = 0xFF00B0FF, // Tech Cyan
        soundAmbienceDescription = "Phone repair bargaining, generator hums & hustle chatter"
    ),
    VICTORIA_ISLAND_MARINA(
        displayName = "Victoria Island & CMS Marina",
        subtitle = "Banking High-Rises & Oceanfront Boulevard",
        landmarkCode = "CORPORATE_TOWER",
        defaultTrafficRisk = 75,
        dangerLevel = 25,
        baseTransitFare = 800L,
        primaryColorHex = 0xFF00E676, // Financial Green
        soundAmbienceDescription = "Air-conditioned glass doors, VIP motorcade sirens & Atlantic breeze"
    ),
    LEKKI_PHASE_ONE(
        displayName = "Lekki Phase 1",
        subtitle = "Mansions, Beach Lounges & Owambe Strip",
        landmarkCode = "OWAMBE_PALACE",
        defaultTrafficRisk = 50,
        dangerLevel = 20,
        baseTransitFare = 1_200L,
        primaryColorHex = 0xFFFFAB00, // Gold Luxury
        soundAmbienceDescription = "Live Fuji bands, champagne cork pops & exhaust crackles of G-Wagons"
    ),
    LAGOS_ISLAND_BALOGUN(
        displayName = "Balogun Market & CMS",
        subtitle = "Lace Empires & Brazilian Quarter Alleys",
        landmarkCode = "BALOGUN_MARKET",
        defaultTrafficRisk = 80,
        dangerLevel = 50,
        baseTransitFare = 400L,
        primaryColorHex = 0xFFFF5722, // Market Terracotta
        soundAmbienceDescription = "Market women calling customers, wheelbarrow carts & fabric shears"
    ),
    SURULERE_STREETS(
        displayName = "Surulere & Stadium Hub",
        subtitle = "Residential Heartland & Bukka Hotspots",
        landmarkCode = "COMPOUND_ROOTS",
        defaultTrafficRisk = 55,
        dangerLevel = 35,
        baseTransitFare = 400L,
        primaryColorHex = 0xFF9C27B0, // Heritage Purple
        soundAmbienceDescription = "Neighbourhood street football, barbing salon afrobeat & sizzling suya"
    )
}

/**
 * Modes of open-world street transit in Lagos.
 */
enum class TravelMode(
    val title: String,
    val speedMultiplier: Float,
    val emoji: String,
    val trafficImmunity: Float, // How well it beats the Go-Slow
    val description: String
) {
    ON_FOOT(
        title = "On Foot (Treck)",
        speedMultiplier = 1.0f,
        emoji = "🚶",
        trafficImmunity = 0.0f,
        description = "Walk sidewalks, weave through crowds, explore every nook & cranny."
    ),
    DANFO_BUS(
        title = "Yellow Danfo Bus",
        speedMultiplier = 1.9f,
        emoji = "🚌",
        trafficImmunity = 0.25f,
        description = "Jump into the iconic yellow LT35 minibus with conductor hanging out shouting routes."
    ),
    KEKE_NAPEP(
        title = "Keke Marwa",
        speedMultiplier = 2.3f,
        emoji = "🛺",
        trafficImmunity = 0.60f,
        description = "Three-wheeled yellow tricycle that weaves through tight Lagos gridlocks."
    ),
    OKADA_BIKE(
        title = "Okada Express",
        speedMultiplier = 3.2f,
        emoji = "🏍️",
        trafficImmunity = 0.90f,
        description = "Motorbike lane-splitting past standstill trailers. Fast, gritty, adrenaline-packed."
    ),
    PRIVATE_CAR(
        title = "Private G-Wagon",
        speedMultiplier = 2.6f,
        emoji = "🚗",
        trafficImmunity = 0.15f,
        description = "Luxury tinted SUV with air-conditioning and heavy road presence. Boss moves."
    )
}

/**
 * Types of authentic Lagos vehicles roaming the open world streets.
 */
enum class LagosVehicleType(
    val displayName: String,
    val baseColorHex: Long,
    val stripeColorHex: Long,
    val isTwoWheeler: Boolean,
    val isThreeWheeler: Boolean,
    val speed: Float
) {
    YELLOW_DANFO("VW LT35 Danfo Minibus", 0xFFFFD700, 0xFF1A1A1A, false, false, 0.08f),
    KEKE_MARWA("Bajaj RE Tricycle", 0xFFFFC107, 0xFF1B5E20, false, true, 0.09f),
    OKADA_BOXER("Bajaj Boxer 100cc", 0xFFE53935, 0xFF000000, true, false, 0.13f),
    G_WAGON_LUXURY("Mercedes-AMG G63 VIP", 0xFF1A1A1A, 0xFFE0E0E0, false, false, 0.11f),
    TOYOTA_CAMRY("Toyota Camry 'Muscle'", 0xFF78909C, 0xFF37474F, false, false, 0.10f),
    MACK_TANKER("Mack Fuel Tanker", 0xFFD32F2F, 0xFFFFFFFF, false, false, 0.04f)
}

/**
 * Points of Interest (POIs) that players can discover and physically walk up to.
 */
enum class POIType(
    val categoryName: String,
    val iconEmoji: String,
    val defaultPrompt: String
) {
    OWAMBE_CANOPY("Owambe Event Grounds", "🎉", "Enter Canopy & Spray Cash"),
    MAMA_PUT_BUKKA("Mama Put Bukka Canteen", "🍲", "Eat Fresh Hot Jollof / Amala"),
    COMPUTER_VILLAGE("Computer Village Market", "💻", "Buy / Flip Tech Gadgets"),
    AGBERO_MOTOR_PARK("Agbero Motor Park", "🚌", "Engage Road Transport Union"),
    BARBING_SALON("Drip Salon & Boutique", "💈", "Get Fresh Fade & Designer Agbada"),
    COMMERCIAL_BANK("Commercial Bank & Forex", "🏦", "Bureau De Change & Cash Desk"),
    FUEL_STATION("Fuel Station & Gen Depot", "⛽", "Buy Petrol for Gen & Vehicle"),
    POLICE_CHECKPOINT("LASTMA & Police Checkpoint", "👮", "Clear Checkpoint & Settle"),
    FAMILY_COMPOUND("Home Living Space", "🏡", "Return to Compound Base")
}

data class OpenWorldPOI(
    val id: String,
    val type: POIType,
    val name: String,
    val district: LagosDistrict,
    val gridX: Float,
    val gridY: Float,
    val loreDescription: String,
    val actionPrompt: String
)

/**
 * Real-time dynamic time-of-day progression affecting atmosphere, traffic, and street vibes.
 */
enum class LagosTimeOfDay(
    val label: String,
    val clockTime: String,
    val lightIntensity: Float, // 0.0 to 1.0
    val ambientWarmth: Float,
    val isNightLighting: Boolean
) {
    MORNING_RUSH("Morning Rush Hour", "07:30 AM", 0.90f, 0.5f, false),
    AFTERNOON_HEAT("Afternoon Sun", "01:30 PM", 1.00f, 0.7f, false),
    EVENING_HOLDUP("Evening Go-Slow", "06:45 PM", 0.65f, 0.9f, true),
    OWAMBE_NIGHT("Owambe Nightlife", "09:45 PM", 0.38f, 0.4f, true),
    MIDNIGHT_BREEZE("Midnight Streets", "01:15 AM", 0.22f, 0.2f, true)
}

/**
 * Lagos weather condition with distinct canvas visual effects.
 */
enum class LagosWeather(
    val label: String,
    val isRaining: Boolean,
    val rainIntensity: Float,
    val description: String
) {
    SUNNY_HARMATTAN("Harmattan Golden Haze", false, 0.0f, "Dry Sahelian dust breeze over yellow asphalt."),
    LAGOS_DOWNPOUR("Heavy Tropical Downpour", true, 0.85f, "Gutters roaring with rain; wet pavement reflections."),
    COASTAL_HUMIDITY("Coastal Atlantic Breeze", false, 0.0f, "Warm ocean mist drifting off the Gulf of Guinea.")
}

/**
 * Interactive street NPCs roaming the open world sidewalks.
 */
data class OpenWorldNPC(
    val id: String,
    val name: String,
    val role: String,
    var speechBubble: String,
    var gridX: Float,
    var gridY: Float,
    var targetGridX: Float,
    var targetGridY: Float,
    val outfitColorHex: Long,
    val headgear: NPCHeadgear,
    val isHawker: Boolean = false,
    val isAgbero: Boolean = false,
    val isPolice: Boolean = false,
    val isPadiOrFamily: Boolean = false
)

/**
 * Dynamic vehicles traversing the roads.
 */
data class OpenWorldVehicle(
    val id: String,
    val type: LagosVehicleType,
    var gridX: Float,
    var gridY: Float,
    var speed: Float,
    var directionX: Float,
    var directionY: Float,
    val driverName: String,
    val routeText: String
)

/**
 * Bukka food items that restore stamina, alleviate stress, and recharge the player.
 */
data class BukkaDish(
    val id: String,
    val name: String,
    val costNara: Long,
    val staminaGain: Int,
    val stressRelief: Int,
    val description: String,
    val emoji: String
)

/**
 * Tech market goods at Computer Village.
 */
data class MarketGadget(
    val id: String,
    val name: String,
    val costNara: Long,
    val cloutGain: Int,
    val description: String,
    val emoji: String
)

/**
 * Boutique clothing and accessories at the Drip Salon.
 */
data class BoutiqueOutfit(
    val id: String,
    val name: String,
    val costNara: Long,
    val dripGain: Int,
    val description: String,
    val emoji: String
)

/**
 * Street encounters triggered randomly while roaming.
 */
data class StreetEncounter(
    val id: String,
    val title: String,
    val narrative: String,
    val actorName: String,
    val actorRole: String,
    val actorEmoji: String,
    val options: List<StreetEncounterOption>
)

data class StreetEncounterOption(
    val label: String,
    val costNara: Long = 0L,
    val staminaDelta: Int = 0,
    val cloutDelta: Int = 0,
    val stressDelta: Int = 0,
    val cashReward: Long = 0L,
    val outcomeNarrative: String
)

/**
 * Open world street hustles and gigs inspired by "Lagos Life".
 */
data class StreetMission(
    val id: String,
    val title: String,
    val brief: String,
    val rewardNara: Long,
    val rewardClout: Int,
    val destinationDistrict: LagosDistrict,
    val missionType: String,
    val iconEmoji: String,
    var progressPercent: Int = 0,
    var isCompleted: Boolean = false
)

/**
 * The master state holding all open-world simulation parameters.
 */
data class OpenWorldState(
    val playerX: Float = 7.0f,
    val playerY: Float = 7.0f,
    val targetX: Float = 7.0f,
    val targetY: Float = 7.0f,
    val playerFacing: Float = 0f, // degrees
    val currentDistrict: LagosDistrict = LagosDistrict.OSHODI_OJUELEGBA,
    val travelMode: TravelMode = TravelMode.ON_FOOT,
    val currentVehicle: LagosVehicleType? = null,
    val trafficHoldUp: Int = 68, // 0 - 100% Go-Slow index
    val timeOfDay: LagosTimeOfDay = LagosTimeOfDay.AFTERNOON_HEAT,
    val weather: LagosWeather = LagosWeather.SUNNY_HARMATTAN,
    val stamina: Int = 85,       // 0 - 100
    val dripRating: Int = 45,    // 0 - 100
    val generatorFuelLitres: Int = 15,
    val carFuelLitres: Int = 30,
    val activeEncounter: StreetEncounter? = null,
    val activeMission: StreetMission? = null,
    val activePOIDialog: OpenWorldPOI? = null,
    val nearbyPOI: OpenWorldPOI? = null,
    val isMiniMapExpanded: Boolean = false,
    val fastTravelDialogOpen: Boolean = false,
    val ownedVehicles: List<LagosVehicleType> = listOf(
        LagosVehicleType.YELLOW_DANFO,
        LagosVehicleType.OKADA_BOXER
    ),
    val lastWorldEventLog: String? = "Welcome to Nigeria Open World! Tap on the ground or use the D-Pad to explore streets, board Danfo buses, and discover hotspots.",
    val dailyMissions: List<DailyMission> = emptyList(),
    val isMissionOverlayOpen: Boolean = false,
    val missionFilter: String = "ALL", // "ALL", "FAMILY", "POLITICAL", "CRISIS"
    val currentState: NigeriaState = NigeriaFederation.LAGOS_STATE,
    val currentDayNumber: Int = 1,
    val dayOfWeek: DayOfWeekName = DayOfWeekName.MONDAY,
    val currencyWallet: CurrencyWallet = CurrencyWallet(),
    val isNationMapOpen: Boolean = false,
    val isCurrencyExchangeOpen: Boolean = false,
    val isAmbientAudioEnabled: Boolean = true,
    val currentAmbientSoundTitle: String = "Bustling Open Market & Hawkers",
    val currentAmbientSoundEmoji: String = "🏪"
)
