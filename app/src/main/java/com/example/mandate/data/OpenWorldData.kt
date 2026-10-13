package com.example.mandate.data

import com.example.mandate.model.*
import com.example.mandate.ui.components.NPCHeadgear

object OpenWorldData {

    fun getPOIsForDistrict(district: LagosDistrict): List<OpenWorldPOI> {
        return when (district) {
            LagosDistrict.OSHODI_OJUELEGBA -> listOf(
                OpenWorldPOI(
                    id = "poi_oshodi_park",
                    type = POIType.AGBERO_MOTOR_PARK,
                    name = "Oshodi Central Motor Park",
                    district = district,
                    gridX = 3.5f,
                    gridY = 3.5f,
                    loreDescription = "The undisputed transit nerve center of Lagos. Hundreds of yellow Danfo buses revving engines under the roar of flyovers.",
                    actionPrompt = "🚌 Engage Transport Union"
                ),
                OpenWorldPOI(
                    id = "poi_oshodi_bukka",
                    type = POIType.MAMA_PUT_BUKKA,
                    name = "Iya Basira 24/7 Amala Bukka",
                    district = district,
                    gridX = 11.5f,
                    gridY = 3.5f,
                    loreDescription = "Steaming pots of fluffy dark Amala, thick Gbegiri and Ewedu soup, with assorted beef and goat meat seasoned with scotch bonnets.",
                    actionPrompt = "🍲 Eat Hot Amala & Suya"
                ),
                OpenWorldPOI(
                    id = "poi_oshodi_owambe",
                    type = POIType.OWAMBE_CANOPY,
                    name = "Ojuelegba Street Owambe Canopy",
                    district = district,
                    gridX = 7.5f,
                    gridY = 13.5f,
                    loreDescription = "Huge green and gold party tents pitched right across the street. The live Fuji band is praising the local elders while champagne flows.",
                    actionPrompt = "🎉 Join Owambe & Spray Cash"
                ),
                OpenWorldPOI(
                    id = "poi_oshodi_police",
                    type = POIType.POLICE_CHECKPOINT,
                    name = "LASTMA & Police Checkpoint",
                    district = district,
                    gridX = 11.5f,
                    gridY = 11.5f,
                    loreDescription = "Traffic marshals and mobile police monitoring vehicle documents and lane violations.",
                    actionPrompt = "👮 Clear Checkpoint"
                ),
                OpenWorldPOI(
                    id = "poi_oshodi_fuel",
                    type = POIType.FUEL_STATION,
                    name = "TotalEnergies Fuel & Gen Depot",
                    district = district,
                    gridX = 3.5f,
                    gridY = 11.5f,
                    loreDescription = "Long lines of yellow jerrycans waiting for PMS petrol to power local generators and Danfo minibuses.",
                    actionPrompt = "⛽ Buy Fuel for Gen & Car"
                )
            )
            LagosDistrict.IKEJA_COMPUTER_VILLAGE -> listOf(
                OpenWorldPOI(
                    id = "poi_ikeja_tech",
                    type = POIType.COMPUTER_VILLAGE,
                    name = "Otigba Tech Plaza",
                    district = district,
                    gridX = 3.5f,
                    gridY = 3.5f,
                    loreDescription = "The Silicon Valley of West Africa. Thousands of tech hustlers swapping iPhones, repairing motherboards, and trading gadgets.",
                    actionPrompt = "💻 Buy & Flip Gadgets"
                ),
                OpenWorldPOI(
                    id = "poi_ikeja_bukka",
                    type = POIType.MAMA_PUT_BUKKA,
                    name = "Mama Tolu Smokey Jollof Joint",
                    district = district,
                    gridX = 11.5f,
                    gridY = 3.5f,
                    loreDescription = "Famous party firewood jollof rice served with fried plantain (dodo), crispy croaker fish, and cold malt.",
                    actionPrompt = "🍲 Eat Firewood Jollof"
                ),
                OpenWorldPOI(
                    id = "poi_ikeja_drip",
                    type = POIType.BARBING_SALON,
                    name = "Allen Ave Celebrity Fade & Drip",
                    district = district,
                    gridX = 3.5f,
                    gridY = 11.5f,
                    loreDescription = "Top-tier styling parlor. Sharp fades, designer sunglasses, and tailored senator suits to boost your street clout.",
                    actionPrompt = "💈 Upgrade Drip & Haircut"
                ),
                OpenWorldPOI(
                    id = "poi_ikeja_bank",
                    type = POIType.COMMERCIAL_BANK,
                    name = "Zenith Commercial & BDC Forex",
                    district = district,
                    gridX = 11.5f,
                    gridY = 11.5f,
                    loreDescription = "High-velocity bureau de change kiosk and bulletproof bank lobby handling dollar swaps and cash withdrawals.",
                    actionPrompt = "🏦 Exchange Forex & Deposit"
                )
            )
            LagosDistrict.VICTORIA_ISLAND_MARINA -> listOf(
                OpenWorldPOI(
                    id = "poi_vi_bank",
                    type = POIType.COMMERCIAL_BANK,
                    name = "Marina Financial Towers",
                    district = district,
                    gridX = 3.5f,
                    gridY = 3.5f,
                    loreDescription = "Corporate headquarters of Nigeria's tier-1 banks overlooking the Lagos lagoon. Multi-million dollar syndications signed here.",
                    actionPrompt = "🏦 Corporate Finance Hub"
                ),
                OpenWorldPOI(
                    id = "poi_vi_lounge",
                    type = POIType.MAMA_PUT_BUKKA,
                    name = "Yellow Chilli Gourmet Bukka",
                    district = district,
                    gridX = 11.5f,
                    gridY = 3.5f,
                    loreDescription = "Seafood okra, gourmet pepper soup, and vintage palm wine served to executives and diplomats.",
                    actionPrompt = "🍲 Dine at Executive Bukka"
                ),
                OpenWorldPOI(
                    id = "poi_vi_police",
                    type = POIType.POLICE_CHECKPOINT,
                    name = "Bar Beach VIP Tactical Post",
                    district = district,
                    gridX = 11.5f,
                    gridY = 11.5f,
                    loreDescription = "Elite tactical police escorts and private security checkpoints guarding corporate corridors.",
                    actionPrompt = "👮 VIP Security Clearance"
                ),
                OpenWorldPOI(
                    id = "poi_vi_drip",
                    type = POIType.BARBING_SALON,
                    name = "Billionaire Row Luxury Boutique",
                    district = district,
                    gridX = 3.5f,
                    gridY = 11.5f,
                    loreDescription = "Imported Swiss lace, embroidered Chieftaincy caps, and gold cufflinks for society heavyweights.",
                    actionPrompt = "💈 Luxury Wardrobe Upgrade"
                )
            )
            LagosDistrict.LEKKI_PHASE_ONE -> listOf(
                OpenWorldPOI(
                    id = "poi_lekki_owambe",
                    type = POIType.OWAMBE_CANOPY,
                    name = "Monarch Palace Mega Owambe",
                    district = district,
                    gridX = 7.5f,
                    gridY = 7.5f,
                    loreDescription = "Glamorous open-air high society wedding. Fuji legend performing live with a 20-piece percussion ensemble. Money spraying heaven!",
                    actionPrompt = "🎉 Spray Bundles of Naira"
                ),
                OpenWorldPOI(
                    id = "poi_lekki_bukka",
                    type = POIType.MAMA_PUT_BUKKA,
                    name = "Sailors Lounge Suya & Grill",
                    district = district,
                    gridX = 3.5f,
                    gridY = 11.5f,
                    loreDescription = "Overwater deck serving spicy beef suya seasoned with yaji pepper, grilled catfish, and chilled palm wine.",
                    actionPrompt = "🍲 Eat Waterfront Suya"
                ),
                OpenWorldPOI(
                    id = "poi_lekki_drip",
                    type = POIType.BARBING_SALON,
                    name = "Admiralty Way Drip Palace",
                    district = district,
                    gridX = 11.5f,
                    gridY = 3.5f,
                    loreDescription = "High-end bespoke agbada house styling Nigerian music superstars and billionaire heirs.",
                    actionPrompt = "💈 Buy Star Agbada & Drip"
                )
            )
            LagosDistrict.LAGOS_ISLAND_BALOGUN -> listOf(
                OpenWorldPOI(
                    id = "poi_balogun_market",
                    type = POIType.COMPUTER_VILLAGE,
                    name = "Balogun Fabric Wholesale Plaza",
                    district = district,
                    gridX = 3.5f,
                    gridY = 3.5f,
                    loreDescription = "The beating commercial heart of Lagos Island. Mountains of genuine Swiss voile lace, Aso-Oke, and gold jewelry.",
                    actionPrompt = "💻 Trade & Wholesale Bargain"
                ),
                OpenWorldPOI(
                    id = "poi_balogun_bukka",
                    type = POIType.MAMA_PUT_BUKKA,
                    name = "White Sand Canteen & Ofada Rice",
                    district = district,
                    gridX = 11.5f,
                    gridY = 3.5f,
                    loreDescription = "Fragrant authentic Ofada rice wrapped in green uma leaves, drenched in spicy Ayamase bleached-oil stew with boiled eggs.",
                    actionPrompt = "🍲 Eat Authentic Ofada Rice"
                ),
                OpenWorldPOI(
                    id = "poi_balogun_park",
                    type = POIType.AGBERO_MOTOR_PARK,
                    name = "CMS Jetty & Water Bus Terminal",
                    district = district,
                    gridX = 3.5f,
                    gridY = 11.5f,
                    loreDescription = "Historic water terminal connecting the Island to Ikorodu and Mile 2 across the bright lagoon waters.",
                    actionPrompt = "🚌 Board Transit Ferry/Bus"
                ),
                OpenWorldPOI(
                    id = "poi_balogun_home",
                    type = POIType.FAMILY_COMPOUND,
                    name = "Ancestral Brazilian Quarter Compound",
                    district = district,
                    gridX = 11.5f,
                    gridY = 11.5f,
                    loreDescription = "Historic family home built with Portuguese tiles and broad wooden verandas, home to your clan elders.",
                    actionPrompt = "🏡 Return to Clan Living Space"
                )
            )
            LagosDistrict.SURULERE_STREETS -> listOf(
                OpenWorldPOI(
                    id = "poi_surulere_home",
                    type = POIType.FAMILY_COMPOUND,
                    name = "Balogun Clan Family Compound",
                    district = district,
                    gridX = 7.5f,
                    gridY = 7.5f,
                    loreDescription = "Your primary family compound base. Features the courtyard, parlor, and veranda where your family members and visiting padis assemble.",
                    actionPrompt = "🏡 Enter 3D Home Living Space"
                ),
                OpenWorldPOI(
                    id = "poi_surulere_bukka",
                    type = POIType.MAMA_PUT_BUKKA,
                    name = "Adeniran Ogunsanya Pounded Yam Spot",
                    district = district,
                    gridX = 3.5f,
                    gridY = 3.5f,
                    loreDescription = "Silky pounded yam with rich Egusi soup studded with stockfish, goat meat, and fresh bitterleaf.",
                    actionPrompt = "🍲 Eat Hot Pounded Yam"
                ),
                OpenWorldPOI(
                    id = "poi_surulere_drip",
                    type = POIType.BARBING_SALON,
                    name = "Shitta Street Legendary Barbing Shop",
                    district = district,
                    gridX = 11.5f,
                    gridY = 11.5f,
                    loreDescription = "The neighborhood social hub where youth debate football, politics, and music while getting razor-sharp fades.",
                    actionPrompt = "💈 Fresh Fade & Street Gist"
                ),
                OpenWorldPOI(
                    id = "poi_surulere_fuel",
                    type = POIType.FUEL_STATION,
                    name = "Mobil Station & Gen Power Station",
                    district = district,
                    gridX = 3.5f,
                    gridY = 11.5f,
                    loreDescription = "Keep your generator humming so your home won't suffer from constant national grid blackouts.",
                    actionPrompt = "⛽ Refuel Compound Generator"
                )
            )
        }
    }

    fun getInitialNPCs(district: LagosDistrict): List<OpenWorldNPC> {
        return listOf(
            OpenWorldNPC(
                id = "npc_hawker_1",
                name = "Alaba",
                role = "Traffic Hawker",
                speechBubble = "Cold pure water! Lacasera! Gala hot!",
                gridX = 5.0f,
                gridY = 6.0f,
                targetGridX = 9.0f,
                targetGridY = 6.0f,
                outfitColorHex = 0xFFFF7043,
                headgear = NPCHeadgear.MODERN_HAIR,
                isHawker = true
            ),
            OpenWorldNPC(
                id = "npc_agbero_1",
                name = "MC Koko",
                role = "Road Union Conductor",
                speechBubble = "Yaba! Oshodi straight! Enter with your change!",
                gridX = 3.0f,
                gridY = 4.5f,
                targetGridX = 4.5f,
                targetGridY = 4.5f,
                outfitColorHex = 0xFFFFD54F,
                headgear = NPCHeadgear.BASEBALL_CAP,
                isAgbero = true
            ),
            OpenWorldNPC(
                id = "npc_banker_1",
                name = "Femi",
                role = "Tech Bro & Forex Trader",
                speechBubble = "Dollar just jumped 20 Naira today!",
                gridX = 10.0f,
                gridY = 4.0f,
                targetGridX = 10.0f,
                targetGridY = 7.0f,
                outfitColorHex = 0xFF42A5F5,
                headgear = NPCHeadgear.MODERN_HAIR
            ),
            OpenWorldNPC(
                id = "npc_alhaja_1",
                name = "Alhaja Kudirat",
                role = "Lace Merchant",
                speechBubble = "Senior man, come touch pure Swiss lace!",
                gridX = 12.0f,
                gridY = 8.5f,
                targetGridX = 12.0f,
                targetGridY = 11.0f,
                outfitColorHex = 0xFFBA68C8,
                headgear = NPCHeadgear.TOWERING_GELE
            ),
            OpenWorldNPC(
                id = "npc_police_1",
                name = "Inspector Sunday",
                role = "LASTMA Officer",
                speechBubble = "Park well! Tinted permit complete?",
                gridX = 10.5f,
                gridY = 10.5f,
                targetGridX = 11.5f,
                targetGridY = 10.5f,
                outfitColorHex = 0xFF43A047,
                headgear = NPCHeadgear.COUP_BERET,
                isPolice = true
            ),
            OpenWorldNPC(
                id = "npc_padi_homie",
                name = "Segun Sparks",
                role = "Syndicate Homie",
                speechBubble = "Senior man! Your drip is choking the streets!",
                gridX = 7.0f,
                gridY = 8.0f,
                targetGridX = 8.5f,
                targetGridY = 8.0f,
                outfitColorHex = 0xFFFFD700,
                headgear = NPCHeadgear.YORUBA_FILA,
                isPadiOrFamily = true
            )
        )
    }

    fun getInitialVehicles(): List<OpenWorldVehicle> {
        return listOf(
            OpenWorldVehicle(
                id = "veh_danfo_1",
                type = LagosVehicleType.YELLOW_DANFO,
                gridX = 1.0f,
                gridY = 7.0f,
                speed = 0.07f,
                directionX = 1.0f,
                directionY = 0.0f,
                driverName = "Baale Morufu",
                routeText = "CMS - OSHODI"
            ),
            OpenWorldVehicle(
                id = "veh_keke_1",
                type = LagosVehicleType.KEKE_MARWA,
                gridX = 13.0f,
                gridY = 8.0f,
                speed = 0.08f,
                directionX = -1.0f,
                directionY = 0.0f,
                driverName = "Chinedu Express",
                routeText = "MARKET SHUTTLE"
            ),
            OpenWorldVehicle(
                id = "veh_okada_1",
                type = LagosVehicleType.OKADA_BOXER,
                gridX = 7.0f,
                gridY = 1.0f,
                speed = 0.12f,
                directionX = 0.0f,
                directionY = 1.0f,
                driverName = "Musa Dispatch",
                routeText = "EXPRESS COURIER"
            ),
            OpenWorldVehicle(
                id = "veh_gwagon_1",
                type = LagosVehicleType.G_WAGON_LUXURY,
                gridX = 8.0f,
                gridY = 13.0f,
                speed = 0.09f,
                directionX = 0.0f,
                directionY = -1.0f,
                driverName = "Chief Convoy",
                routeText = "VIP MOTORCADE"
            )
        )
    }

    val bukkaMenu = listOf(
        BukkaDish(
            id = "dish_amala",
            name = "Hot Amala with Gbegiri, Ewedu & Goat Meat",
            costNara = 3_500L,
            staminaGain = 35,
            stressRelief = 20,
            description = "The king of Yoruba comfort food. Silky hot amala soaked in savory yellow gbegiri and vibrant ewedu.",
            emoji = "🍲"
        ),
        BukkaDish(
            id = "dish_jollof",
            name = "Firewood Party Jollof Rice with Dodo & Chicken",
            costNara = 4_000L,
            staminaGain = 40,
            stressRelief = 15,
            description = "Authentic smoky Lagos jollof cooked over wood fire, accompanied by sweet fried ripe plantain.",
            emoji = "🍗"
        ),
        BukkaDish(
            id = "dish_suya",
            name = "Mai Suya Peppered Beef with Onions & Cabbage",
            costNara = 2_500L,
            staminaGain = 25,
            stressRelief = 18,
            description = "Thinly sliced tender beef grilled over open coals, coated in pungent yaji ginger-chili spice.",
            emoji = "🥩"
        ),
        BukkaDish(
            id = "dish_peppersoup",
            name = "Catfish Point & Kill Pepper Soup",
            costNara = 5_000L,
            staminaGain = 30,
            stressRelief = 25,
            description = "Simmered fresh catfish with aromatic calabash nutmeg and uziza leaves. Instant revitalizer.",
            emoji = "🐟"
        ),
        BukkaDish(
            id = "dish_palmwine",
            name = "Chilled Sweet Palm Wine in Gourd",
            costNara = 1_200L,
            staminaGain = 15,
            stressRelief = 30,
            description = "Naturally fermented frothy sap freshly tapped from the oil palm. Deeply relaxing.",
            emoji = "🍶"
        )
    )

    val gadgetList = listOf(
        MarketGadget(
            id = "gadget_iphone",
            name = "Refurbished iPhone 15 Pro Max (Clean London Used)",
            costNara = 450_000L,
            cloutGain = 25,
            description = "The ultimate status symbol in Lagos. Flashing this in clubs immediately earns respect.",
            emoji = "📱"
        ),
        MarketGadget(
            id = "gadget_powerbank",
            name = "Heavy Duty 50,000mAh Solar Power Bank",
            costNara = 45_000L,
            cloutGain = 8,
            description = "Essential Lagos survival tool. Keeps your phone alive through 48-hour NEPA power cuts.",
            emoji = "🔋"
        ),
        MarketGadget(
            id = "gadget_laptop",
            name = "Custom Hacker Laptop with Dual OS",
            costNara = 680_000L,
            cloutGain = 35,
            description = "High-spec machine for data ops, social media campaign analytics, and political intelligence.",
            emoji = "💻"
        ),
        MarketGadget(
            id = "gadget_satellite_phone",
            name = "Encrypted Satellite Comms Phone",
            costNara = 950_000L,
            cloutGain = 40,
            description = "Bypasses all network shutdowns during state curfews. Direct hotline to apex Godfathers.",
            emoji = "🛰️"
        )
    )

    val boutiqueList = listOf(
        BoutiqueOutfit(
            id = "outfit_agbada",
            name = "Royal 3-Piece Embroidered Agbada",
            costNara = 120_000L,
            dripGain = 35,
            description = "Heavy damask fabric with gold bullion hand-embroidery. Sweeps majestically as you walk.",
            emoji = "👑"
        ),
        BoutiqueOutfit(
            id = "outfit_fila",
            name = "Handwoven Aso-Oke Fila Cap (Gobi Style)",
            costNara = 25_000L,
            dripGain = 15,
            description = "Tilted with effortless confidence to the left, signaling senior man status.",
            emoji = "🧢"
        ),
        BoutiqueOutfit(
            id = "outfit_shades",
            name = "Gold-Rimmed Tinted Cartier Aviators",
            costNara = 85_000L,
            dripGain = 25,
            description = "Conceals your eyes during intense boardroom negotiations and street standoffs.",
            emoji = "🕶️"
        ),
        BoutiqueOutfit(
            id = "outfit_chain",
            name = "18K Solid Gold Cuban Link Chain",
            costNara = 750_000L,
            dripGain = 50,
            description = "Heavy gold links that gleam in the Lagos sunshine. Unlocks instant VIP access.",
            emoji = "✨"
        )
    )

    val sampleEncounters = listOf(
        StreetEncounter(
            id = "enc_agbero_toll",
            title = "Agbero Boys Demand Street Toll",
            narrative = "A crew of rugged youths with yellow reflective vests flag you down near the roundabout: 'Chairman! You cannot pass our territory without dropping kola for the boys on ground!'",
            actorName = "MC Tiger & The Oshodi Boys",
            actorRole = "NURTW Area Boys",
            actorEmoji = "🦁",
            options = listOf(
                StreetEncounterOption(
                    label = "Drop ₦2,000 Kola & Exchange Banter",
                    costNara = 2_000L,
                    staminaDelta = 0,
                    cloutDelta = 10,
                    stressDelta = -5,
                    cashReward = 0L,
                    outcomeNarrative = "You dropped crisp ₦1,000 notes and hailed them: 'Up National!' The boys saluted you enthusiastically and cleared the road: 'Senior Man! Walk well!'"
                ),
                StreetEncounterOption(
                    label = "Flash Political Clout & Intimidate",
                    costNara = 0L,
                    staminaDelta = -5,
                    cloutDelta = 15,
                    stressDelta = 10,
                    cashReward = 0L,
                    outcomeNarrative = "You raised your voice and dropped the name of Alhaji Danbaba. The boys froze, apologized profusely, and ushered you through with bowed heads."
                ),
                StreetEncounterOption(
                    label = "Dodge into a Side Alley (Treck Fast)",
                    costNara = 0L,
                    staminaDelta = -15,
                    cloutDelta = -5,
                    stressDelta = 5,
                    cashReward = 0L,
                    outcomeNarrative = "You swiftly ducked past the yellow Danfo buses into a narrow passage. You saved your cash, but arrived catching your breath."
                )
            )
        ),
        StreetEncounter(
            id = "enc_police_stop",
            title = "Surprise Mobile Police Checkpoint",
            narrative = "An armored police Hilux with flashing blue lights blocks the road. An officer with a walkie-talkie gestures for you to stop: 'Good afternoon, Sir. Security routine search.'",
            actorName = "ASP Danjuma",
            actorRole = "Tactical Squad Commander",
            actorEmoji = "👮",
            options = listOf(
                StreetEncounterOption(
                    label = "Present Official Credentials & Smile",
                    costNara = 0L,
                    staminaDelta = 0,
                    cloutDelta = 8,
                    stressDelta = -5,
                    cashReward = 0L,
                    outcomeNarrative = "The officer inspected your party membership and student union executive card, nodded in respect, and wished you safe travels."
                ),
                StreetEncounterOption(
                    label = "Offer ₦5,000 'Pure Water & Minerals'",
                    costNara = 5_000L,
                    staminaDelta = 0,
                    cloutDelta = 12,
                    stressDelta = -10,
                    cashReward = 0L,
                    outcomeNarrative = "The officers smiled broadly: 'Senior Man! May your pocket never run dry!' They gave you an immediate VIP lane escort."
                )
            )
        ),
        StreetEncounter(
            id = "enc_hawker_special",
            title = "Chilled Pure Water & Fresh Meat Pie",
            narrative = "A young street hawker balances a giant aluminum tray on her head with icy mist curling up: 'Brother! Pure cold water straight from the freezer! Pure meat pie hot!'",
            actorName = "Chidinma",
            actorRole = "Street Hawker",
            actorEmoji = "🧊",
            options = listOf(
                StreetEncounterOption(
                    label = "Buy 2 Cold Waters & Meat Pie (₦1,000)",
                    costNara = 1_000L,
                    staminaDelta = 25,
                    cloutDelta = 5,
                    stressDelta = -15,
                    cashReward = 0L,
                    outcomeNarrative = "The icy pure water cut through the tropical heat instantly. Stamina recharged and feeling fresh!"
                ),
                StreetEncounterOption(
                    label = "Tip ₦5,000 for Her School Fees",
                    costNara = 5_000L,
                    staminaDelta = 25,
                    cloutDelta = 18,
                    stressDelta = -20,
                    cashReward = 0L,
                    outcomeNarrative = "She broke into joyful tears and called out passionate prayers of blessing upon you. Street bystanders murmured in admiration."
                )
            )
        )
    )

    val sampleMissions = listOf(
        StreetMission(
            id = "mission_danfo_conductor",
            title = "Danfo Conductor Rush Hour Shift",
            brief = "Take the conductor spot on a yellow Danfo bus running from Oshodi to CMS Marina. Slap the bus body, call out passengers, and collect exact change.",
            rewardNara = 25_000L,
            rewardClout = 15,
            destinationDistrict = LagosDistrict.LAGOS_ISLAND_BALOGUN,
            missionType = "TRANSIT_GIG",
            iconEmoji = "🚌"
        ),
        StreetMission(
            id = "mission_dispatch_rider",
            title = "High-Stakes Dispatch Courier Run",
            brief = "Jump on an Okada Boxer to weave through standstill traffic and deliver sealed election tribunal documents from Ikeja to Victoria Island before 5 PM.",
            rewardNara = 45_000L,
            rewardClout = 20,
            destinationDistrict = LagosDistrict.VICTORIA_ISLAND_MARINA,
            missionType = "SPEED_RUN",
            iconEmoji = "🏍️"
        ),
        StreetMission(
            id = "mission_owambe_escort",
            title = "Owambe VIP Convoy Escort",
            brief = "Escort an influential society Alhaja in a convoy of G-Wagons to a high-society wedding in Lekki Phase 1 and secure the VIP front table.",
            rewardNara = 120_000L,
            rewardClout = 30,
            destinationDistrict = LagosDistrict.LEKKI_PHASE_ONE,
            missionType = "VIP_SECURITY",
            iconEmoji = "🎉"
        ),
        StreetMission(
            id = "mission_gadget_flip",
            title = "Computer Village Tech Arbitrage",
            brief = "Bargain a batch of imported smartphones in Ikeja and wholesale distribute them to market merchants in Balogun for pure profit.",
            rewardNara = 80_000L,
            rewardClout = 22,
            destinationDistrict = LagosDistrict.LAGOS_ISLAND_BALOGUN,
            missionType = "COMMERCE_FLIP",
            iconEmoji = "💻"
        )
    )
}
