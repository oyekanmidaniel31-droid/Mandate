package com.example.mandate.data

import com.example.mandate.model.*

object DailyMissionData {

    fun generateInitialDailyMissions(): List<DailyMission> {
        return listOf(
            // 1. FAMILY OBLIGATION: School Fees & Hospital Bill Black Tax
            DailyMission(
                id = "FAM_001_SCHOOL_FEES",
                title = "Junior's School Fees & Mama's Drugs",
                category = DailyMissionCategory.FAMILY_OBLIGATION,
                briefDescription = "Transfer ₦25,000 to clear Uncle Tunde's son's WAEC fees and buy Mama's monthly hypertension prescription from the local chemist.",
                targetStateName = "Lagos State",
                targetLocationName = "Surulere Compound",
                iconEmoji = "💊",
                pidginFlavor = "\"Senior Man, Mama dey call since morning say her blood pressure drug don finish. Make you no disappoint your people!\"",
                costNaira = 25_000L,
                staminaCost = 10,
                rewardNaira = 0L,
                rewardClout = 5,
                rewardFamilyLoyalty = 15,
                rewardGodfatherFavor = 0,
                rewardConscienceDelta = 8,
                currentProgress = 0,
                maxProgress = 1,
                status = MissionStatus.AVAILABLE
            ),

            // 2. FAMILY OBLIGATION: Compound Generator Fuel
            DailyMission(
                id = "FAM_002_GEN_PETROL",
                title = "Generator Fuel Run for Family Compound",
                category = DailyMissionCategory.FAMILY_OBLIGATION,
                briefDescription = "National grid has collapsed. Buy 20 Litres of petrol for the compound's 'I Better Pass My Neighbour' generator before dark.",
                targetStateName = "Lagos State",
                targetLocationName = "Surulere Fuel Depot",
                iconEmoji = "⛽",
                pidginFlavor = "\"Nepah don take light since 3 days! The children cannot read for exam in darkness. Fuel station dey open now.\"",
                costNaira = 14_000L,
                staminaCost = 15,
                rewardNaira = 0L,
                rewardClout = 0,
                rewardFamilyLoyalty = 10,
                rewardGodfatherFavor = 0,
                rewardConscienceDelta = 5,
                currentProgress = 0,
                maxProgress = 1,
                status = MissionStatus.AVAILABLE
            ),

            // 3. POLITICAL HUSTLE: Agbero Motor Park Dues
            DailyMission(
                id = "POL_001_AGBERO_UNION",
                title = "Oshodi Motor Park Union Settlement",
                category = DailyMissionCategory.POLITICAL_HUSTLE,
                briefDescription = "Meet Alhaji Kazeem (Union Chairman) at Oshodi Bus Terminal; settle transport boys to guarantee safe convoy passage and youth votes.",
                targetStateName = "Lagos State",
                targetLocationName = "Oshodi Motor Park",
                iconEmoji = "🚌",
                pidginFlavor = "\"Chairman say if you no drop something for boys, your campaign bus go enter puncture tomorrow morning. Settle the boys!\"",
                costNaira = 30_000L,
                staminaCost = 20,
                rewardNaira = 15_000L, // Kickback share
                rewardClout = 20,
                rewardFamilyLoyalty = 0,
                rewardGodfatherFavor = 10,
                rewardConscienceDelta = -5,
                currentProgress = 0,
                maxProgress = 1,
                status = MissionStatus.AVAILABLE
            ),

            // 4. POLITICAL HUSTLE: Abuja Caucus Backroom Pact
            DailyMission(
                id = "POL_002_ABUJA_CAUCUS",
                title = "Three Arms Zone Closed-Door Caucus",
                category = DailyMissionCategory.POLITICAL_HUSTLE,
                briefDescription = "Fly to Abuja to attend a late-night strategy summit at Transcorp Hilton with northern delegates and godfather Alhaji Danbaba.",
                targetStateName = "FCT Abuja",
                targetLocationName = "Three Arms Zone",
                iconEmoji = "🏛️",
                pidginFlavor = "\"The ticket for governor go be decided tonight behind closed doors in Suite 404. If you no show up, dem go replace your name!\"",
                costNaira = 50_000L,
                staminaCost = 25,
                rewardNaira = 120_000L, // Slush fund allocation
                rewardClout = 35,
                rewardFamilyLoyalty = 0,
                rewardGodfatherFavor = 25,
                rewardConscienceDelta = -8,
                currentProgress = 0,
                maxProgress = 1,
                status = MissionStatus.AVAILABLE
            ),

            // 5. NATIONAL CRISIS DUTY: Student Union ASUU Dialogue
            DailyMission(
                id = "CRI_001_ASUU_TALKS",
                title = "Address Protesting SUG University Comrades",
                category = DailyMissionCategory.NATIONAL_CRISIS,
                briefDescription = "6-month campus strike is boiling over. Meet the Student Union Government president in Ibadan to negotiate reopening without police violence.",
                targetStateName = "Oyo State",
                targetLocationName = "Ibadan Premier University",
                iconEmoji = "🎓",
                pidginFlavor = "\"Students dey burn tyres for highway! If you can calm the comrades down peacefully, your national respect go skyrocket.\"",
                costNaira = 18_000L,
                staminaCost = 20,
                rewardNaira = 35_000L,
                rewardClout = 25,
                rewardFamilyLoyalty = 5,
                rewardGodfatherFavor = 15,
                rewardConscienceDelta = 12,
                currentProgress = 0,
                maxProgress = 1,
                status = MissionStatus.AVAILABLE
            ),

            // 6. NATIONAL CRISIS DUTY: Niger Delta Creek Pipeline Pact
            DailyMission(
                id = "CRI_002_NIGER_DELTA",
                title = "Port Harcourt Pipeline Security Mediation",
                category = DailyMissionCategory.NATIONAL_CRISIS,
                briefDescription = "Travel to Rivers State to meet creek youth commanders and broker a ceasefire to stop crude oil bunkering and environmental soot.",
                targetStateName = "Rivers State",
                targetLocationName = "Port Harcourt Trans-Amadi",
                iconEmoji = "🛢️",
                pidginFlavor = "\"Oil companies dey run commot because of bunkering. The youth commanders say dem only want dialogue with credible leaders.\"",
                costNaira = 40_000L,
                staminaCost = 30,
                rewardNaira = 180_000L,
                rewardClout = 40,
                rewardFamilyLoyalty = 0,
                rewardGodfatherFavor = 30,
                rewardConscienceDelta = 10,
                currentProgress = 0,
                maxProgress = 1,
                status = MissionStatus.AVAILABLE
            )
        )
    }

    /**
     * Generates a new fresh batch of missions upon rolling over to a new day.
     */
    fun generateNextDayMissions(dayNumber: Int): List<DailyMission> {
        val base = generateInitialDailyMissions().toMutableList()
        // Mix in specific day flavor
        if (dayNumber % 2 == 0) {
            base.add(
                DailyMission(
                    id = "FAM_003_COWIFE_PEACE",
                    title = "Compound Peace: Co-Wives Kitchen Dispute",
                    category = DailyMissionCategory.FAMILY_OBLIGATION,
                    briefDescription = "Intervene in the loud quarrel between Iya Bose and Aunty Funke over compound water pump roster. Buy peace with gifts.",
                    targetStateName = "Lagos State",
                    targetLocationName = "Surulere Living Space",
                    iconEmoji = "🍲",
                    pidginFlavor = "\"The two wives dey exchange hot words since 6am! As the head of this generation, you must show wisdom.\"",
                    costNaira = 12_000L,
                    staminaCost = 15,
                    rewardNaira = 0L,
                    rewardClout = 5,
                    rewardFamilyLoyalty = 20,
                    rewardGodfatherFavor = 0,
                    rewardConscienceDelta = 10,
                    currentProgress = 0,
                    maxProgress = 1,
                    status = MissionStatus.AVAILABLE
                )
            )
            base.add(
                DailyMission(
                    id = "POL_003_KANO_DELEGATES",
                    title = "Sabon Gari Primary Delegate Logistics",
                    category = DailyMissionCategory.POLITICAL_HUSTLE,
                    briefDescription = "Dispatch financial mobilization envelopes to 50 key party delegates in Kano Municipal to guarantee their votes for your ticket.",
                    targetStateName = "Kano State",
                    targetLocationName = "Sabon Gari Market Hub",
                    iconEmoji = "🗳️",
                    pidginFlavor = "\"North-West delegates hold 135 electoral votes. If you lock down Kano today, your party primary is secured!\"",
                    costNaira = 60_000L,
                    staminaCost = 20,
                    rewardNaira = 50_000L,
                    rewardClout = 30,
                    rewardFamilyLoyalty = 0,
                    rewardGodfatherFavor = 35,
                    rewardConscienceDelta = -5,
                    currentProgress = 0,
                    maxProgress = 1,
                    status = MissionStatus.AVAILABLE
                )
            )
        }
        return base
    }
}
