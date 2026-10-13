package com.example.mandate.data

/**
 * Data specification for informative tooltips explaining how core resources
 * (Influence, Wealth, Family Loyalty) and their telemetry sub-metrics impact
 * gameplay, elections, crises, and dynasty survival.
 */
data class ResourceTooltipSpec(
    val key: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val tag: String,
    val gameImpact: String,
    val mechanics: List<Pair<String, String>>,
    val strategicAdvice: List<String>,
    val warning: String? = null
)

object ResourceTooltipCatalog {

    fun getSpec(key: String): ResourceTooltipSpec = when (key.uppercase()) {
        "INFLUENCE" -> ResourceTooltipSpec(
            key = "INFLUENCE",
            title = "INFLUENCE PILLAR",
            subtitle = "Institutional Power, Street Clout & Godfather Mandate",
            emoji = "👑",
            tag = "Electoral & Power Driver",
            gameImpact = "Influence is the ultimate political capital in Naijaland. It dictates whether state assembly delegates obey your commands, whether party chairmen hand you gubernatorial tickets, and whether street youth syndicates rally in your favor during national election crises.",
            mechanics = listOf(
                "🗳️ Primary Delegate Endorsements" to "Directly scales the percentage of party delegates who vote for your candidacy during state and national convention balloting.",
                "🏛️ Executive Appointments" to "Unlocks senior cabinet appointments (Special Adviser, Commissioner, Minister) in the Presidential Villa.",
                "⚡ Crisis De-escalation" to "High street clout allows you to broker peace with transport unions and street mobs before riots paralyze the megacity.",
                "📰 Narrative & Spin Control" to "Prevents adversarial press exposés and anti-graft leaks from tarnishing your public standing."
            ),
            strategicAdvice = listOf(
                "Deploy Ward Patronage regularly to keep grassroots coordinators energized.",
                "Cultivate godfather favor in private boardroom meetings to secure delegate block endorsements.",
                "Engage in Lagos Open World encounters to build grassroots fame across Oshodi and Mainland motor parks."
            ),
            warning = "If Influence drops below 25%, party kingmakers will revoke your ticket, and rival godfathers will target your seat for impeachment."
        )

        "WEALTH" -> ResourceTooltipSpec(
            key = "WEALTH",
            title = "WEALTH PILLAR",
            subtitle = "Treasury Reserves, Banking Liquidity & BDC Forex",
            emoji = "💰",
            tag = "Logistical Survival Engine",
            gameImpact = "Wealth fuels every movement of your political machine. In Naijaland politics, speeches without logistics are empty noise. You require liquid cash for delegate allowances, campaign sound trucks, police sirens escorts, and family compound subsidies.",
            mechanics = listOf(
                "🔥 Daily Living & Logistics Burn" to "Every turn cycle deducts transport, driver salaries, generator diesel, and entourage upkeep. Zero liquidity causes immediate exhaustion and health collapse.",
                "💱 BDC Forex Hedging" to "Holding US Dollars in your Dom Account insulates your net worth when the parallel market exchange rate spikes.",
                "🛡️ Tactical Security Convoy" to "Allows commissioning Level 3 Bulletproof Escort Convoys to survive armed street ambushes.",
                "🍾 Owambe Cash Spraying" to "Spraying mint banknotes at high-society owambes boosts street popularity and slashes mental stress."
            ),
            strategicAdvice = listOf(
                "Buy US Dollars at the BDC counter during currency dips and liquidate during devaluation spikes.",
                "Maintain at least ₦250,000 liquid reserves to survive unexpected hospital emergencies or security extortion.",
                "Balance patronage disbursements against your daily living burn rate."
            ),
            warning = "Insolvency triggers public humiliation, loss of bodyguard escorts, and immediate vulnerability to political rivals."
        )

        "FAMILY", "FAMILY_LOYALTY", "CLAN" -> ResourceTooltipSpec(
            key = "FAMILY",
            title = "FAMILY LOYALTY PILLAR",
            subtitle = "Ancestral Compound Solidarity & Patriarchal Blessings",
            emoji = "🛡️",
            tag = "Ancestral Shield & Dynasty Base",
            gameImpact = "Family Loyalty anchors your moral legitimacy and grassroots home base. In Naijaland, an aspirant disowned by their ancestral compound cannot win village wards. Elders' prayers grant resilience during moral scandals, and loyal siblings manage your secret campaign funds.",
            mechanics = listOf(
                "👴 Elders' Patriarchal Blessing" to "Traditional blessings fortify moral conscience and protect against backroom extortion and spiritual anxiety.",
                "⚠️ Black Tax Burden Management" to "Managing extended family financial pleas prevents explosive family feuds and embarrassing press leaks.",
                "👑 Dynasty Succession" to "High clan solidarity guarantees your political network, real estate, and goodwill transfer to your next-generation heir upon retirement.",
                "💅 Rival Step-Mother Neutralization" to "Keeping household rivals content prevents domestic poisonings and sabotage of your private documents."
            ),
            strategicAdvice = listOf(
                "Disburse emergency Kinship Welfare Grants (₦50k) to village elders to secure blessings and lower Black Tax tension.",
                "Settle urgent school and hospital Black Tax pleas before relatives take disputes to the village square.",
                "Visit the Clan ancestral compound to perform traditional prostrations and share communal meals."
            ),
            warning = "If Clan Loyalty falls below 30%, disgruntled relatives will leak damaging family secrets to opposition campaign managers."
        )

        "POLITICAL_CAPITAL" -> ResourceTooltipSpec(
            key = "POLITICAL_CAPITAL",
            title = "POLITICAL CAPITAL",
            subtitle = "Institutional Power & Party Machinery Sway",
            emoji = "🏛️",
            tag = "Legislative Authority",
            gameImpact = "Measures your institutional command over government agencies, legislative committees, and party caucuses. High political capital allows passing controversial reform bills and securing party tickets.",
            mechanics = listOf(
                "Committee Leadership" to "Unlocks legislative chairmanships and public hearing summons authority.",
                "Party Ticket Security" to "Prevents opposition factions from substituting your election nomination."
            ),
            strategicAdvice = listOf(
                "Pass key legislative motions and broker cross-caucus alignments in the State Assembly."
            )
        )

        "STREET_CLOUT" -> ResourceTooltipSpec(
            key = "STREET_CLOUT",
            title = "STREET CLOUT",
            subtitle = "Motor-Park Syndicates & Grassroots Youth Base",
            emoji = "💥",
            tag = "Grassroots Muscle",
            gameImpact = "Represents your grassroots popularity among market hawkers, transport union chairmen, and campus youth. High street clout provides organic campaign crowd turnouts and crowd protection during protests.",
            mechanics = listOf(
                "Rally Turnouts" to "Packs campaign rallies with energetic supporters without expensive crowd rental fees.",
                "Street Immunity" to "Pass safely through hostile districts without paying transit levies."
            ),
            strategicAdvice = listOf(
                "Patronize local street bukkas, resolve motor park disputes, and spray cash during street youth rallies."
            )
        )

        "GODFATHER_FAVOR" -> ResourceTooltipSpec(
            key = "GODFATHER_FAVOR",
            title = "GODFATHER FAVOR",
            subtitle = "Kingmaker Salon Standing & Boardroom Backing",
            emoji = "💼",
            tag = "Kingmaker Leverage",
            gameImpact = "Measures the trust placed in you by billionaire oligarchs and retired generals who finance campaigns and dictate government contracts behind closed doors.",
            mechanics = listOf(
                "Campaign Funding" to "Unlocks multi-million Naira private campaign donations and diplomatic passports.",
                "Ticket Clearance" to "Guarantees party screening committee approvals without delays."
            ),
            strategicAdvice = listOf(
                "Attend boardroom conclaves and pledge loyalty during closed-door kingmaker dinners."
            )
        )

        "MEDIA_SPIN" -> ResourceTooltipSpec(
            key = "MEDIA_SPIN",
            title = "MEDIA SPIN CONTROL",
            subtitle = "Press Conference Dominance & Public Narrative Control",
            emoji = "📰",
            tag = "Public Relations Mastery",
            gameImpact = "Reflects your ability to dominate news cycles, sway editorial boards, and neutralize viral social media outrage on the Chirp network.",
            mechanics = listOf(
                "Headline Suppression" to "Dilutes investigative scandals before they reach the national assembly floor.",
                "Popularity Surges" to "Converts minor philanthropic donations into national front-page praise."
            ),
            strategicAdvice = listOf(
                "Fund sympathetic media editors and maintain regular Chirp social media engagement."
            )
        )

        "CASH" -> ResourceTooltipSpec(
            key = "CASH",
            title = "PHYSICAL CASH (NAIRA)",
            subtitle = "Mint Banknotes for Immediate Street Deployment",
            emoji = "💵",
            tag = "Liquid Street Currency",
            gameImpact = "Physical cash is mandatory for fast street transactions where mobile networks and POS terminals fail—street food bukkas, Danfo transit, road checkpoints, and Owambe praise singing.",
            mechanics = listOf(
                "Instant Settlements" to "Resolve police extortion and street agbero levies instantly.",
                "Owambe Spraying" to "Used directly for spraying cash bundles on stage musicians."
            ),
            strategicAdvice = listOf(
                "Withdraw cash from commercial bank accounts before traveling through congested market districts."
            )
        )

        "BANK" -> ResourceTooltipSpec(
            key = "BANK",
            title = "COMMERCIAL BANK SAVINGS",
            subtitle = "Institutional Capital & Secure Electronic Deposits",
            emoji = "🏦",
            tag = "Institutional Treasury",
            gameImpact = "Holds your primary wealth securely away from street pickpockets and robbers. Used for large transactions such as real estate, private jets, and patronage grants.",
            mechanics = listOf(
                "Compound Welfare" to "Disburse electronic bank grants directly to village elders.",
                "Asset Acquisition" to "Required for buying luxury boutiques, fuel tankers, and generator setups."
            ),
            strategicAdvice = listOf(
                "Deposit excess street cash into your bank account to protect against theft."
            )
        )

        "DOM_USD" -> ResourceTooltipSpec(
            key = "DOM_USD",
            title = "DOMICILIARY USD ACCOUNT",
            subtitle = "Foreign Currency Reserve & Parallel Forex Hedge",
            emoji = "🇺🇸",
            tag = "Macro Hedge Vault",
            gameImpact = "Your dollar savings provide total immunity against national inflation and currency devaluations. When the Naira weakens, your net worth automatically climbs.",
            mechanics = listOf(
                "BDC Parallel Arbitrage" to "Sell dollars when foreign exchange rates spike for massive Naira profits.",
                "Executive Flights" to "Used for chartering private jets to London clinics for emergency medical recovery."
            ),
            strategicAdvice = listOf(
                "Accumulate foreign exchange at the Bureau De Change whenever the official rate dips."
            )
        )

        "DAILY_BURN" -> ResourceTooltipSpec(
            key = "DAILY_BURN",
            title = "DAILY LIVING & LOGISTICS BURN",
            subtitle = "Mandatory Operational Upkeep per Turn Cycle",
            emoji = "🔥",
            tag = "Overhead Runway",
            gameImpact = "Every day in politics carries an unavoidable financial drain: fueling luxury SUVs, feeding security escorts, maintaining diesel generators, and paying household staff.",
            mechanics = listOf(
                "Turn Deductions" to "Deducted automatically from liquid balances upon advancing the turn.",
                "Runway Calculations" to "Shows how many days your treasury can survive before starvation occurs."
            ),
            strategicAdvice = listOf(
                "Trim excessive staff if campaign inflows slow down; invest in fuel reserves."
            )
        )

        "ELDERS_APPROVAL" -> ResourceTooltipSpec(
            key = "ELDERS_APPROVAL",
            title = "ELDERS' APPROVAL RATING",
            subtitle = "Traditional Council Backing & Patriarchal Standing",
            emoji = "👴",
            tag = "Moral Legitimacy",
            gameImpact = "Village elders control community ward delegates and land inheritance titles. Their blessings protect you from negative spiritual consequences during intense power standoffs.",
            mechanics = listOf(
                "Ward Endorsements" to "Guarantees full bloc votes from ancestral local government areas.",
                "Moral Resilience" to "Reduces stress accumulation from high-stakes decisions."
            ),
            strategicAdvice = listOf(
                "Respect customary protocols and send regular care packages to the village compound."
            )
        )

        "BLACK_TAX" -> ResourceTooltipSpec(
            key = "BLACK_TAX",
            title = "BLACK TAX BURDEN PRESSURE",
            subtitle = "Extended Dependent Demands & Kinship Expectations",
            emoji = "⚠️",
            tag = "Socio-Financial Tension",
            gameImpact = "Measures the ratio between your wealth and the urgent financial requests of your extended family. When relatives feel neglected, resentment breeds and leads to household drama.",
            mechanics = listOf(
                "Mental Strain" to "Unsettled Black Tax pleas increase character stress per day.",
                "Family Feuds" to "Ignored requests can spark co-wife disputes and public accusations."
            ),
            strategicAdvice = listOf(
                "Settle critical medical and tuition requests while ignoring frivolous demands."
            )
        )

        else -> ResourceTooltipSpec(
            key = key,
            title = "$key INDICATOR",
            subtitle = "Strategic Telemetry & Performance Indicator",
            emoji = "📊",
            tag = "Telemetry Metric",
            gameImpact = "This vital metric directly impacts your character's resilience, political authority, and survival in the Nigerian political arena.",
            mechanics = listOf(
                "Core Performance" to "Affects event options, dialogue choices, and stamina recovery."
            ),
            strategicAdvice = listOf(
                "Balance all three pillars—Influence, Wealth, and Family Loyalty—to cement a lasting dynasty."
            )
        )
    }
}
