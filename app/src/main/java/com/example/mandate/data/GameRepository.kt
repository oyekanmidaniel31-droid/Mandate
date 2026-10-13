package com.example.mandate.data

import android.content.Context
import android.content.SharedPreferences
import com.example.mandate.model.*
import kotlin.random.Random

class GameRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("mandate_game_prefs", Context.MODE_PRIVATE)

    fun initializeFromOriginScores(scores: Map<String, Int>): GameState {
        val ambition = scores["ambition"] ?: 50
        val empathy = scores["empathy"] ?: 50
        val risk = scores["risk"] ?: 50
        val loyalty = scores["loyalty"] ?: 50
        val cunning = scores["cunning"] ?: 50

        // Determine background from traits
        val background = when {
            ambition > 70 && risk > 65 -> OriginBackground.STREET_HUSTLER
            cunning > 70 && ambition > 60 -> OriginBackground.DYNASTY_HEIR
            loyalty > 65 && empathy > 60 -> OriginBackground.VILLAGE_FARMER
            risk > 70 -> OriginBackground.OIL_REGION
            else -> OriginBackground.DIASPORA_RETURNEE
        }

        val familyType = when {
            risk > 60 -> FamilyStructure.POLYGAMOUS
            empathy > 60 -> FamilyStructure.MONOGAMOUS
            else -> FamilyStructure.SINGLE_PARENT
        }

        val secrets = FamilySecret.values()
        val familySecret = secrets[Random.nextInt(secrets.size)]

        val courses = AcademicCourse.values()
        val course = courses[Random.nextInt(courses.size)]

        val character = Character(
            name = if (background == OriginBackground.DYNASTY_HEIR) "Babatunde Alabi-Danjuma" else "Adeolu 'Senior Man' Balogun",
            alias = if (cunning > 65) "The Tactician" else "Jagaban of the Youth",
            gender = "Male",
            age = 18,
            year = 2005,
            stateOfOrigin = when (background) {
                OriginBackground.STREET_HUSTLER -> "Lakos State"
                OriginBackground.OIL_REGION -> "Rivers State"
                OriginBackground.VILLAGE_FARMER -> "Oyo State"
                OriginBackground.DYNASTY_HEIR -> "Abuka FCT"
                OriginBackground.DIASPORA_RETURNEE -> "Lakos State"
            },
            neighbourhood = when (background) {
                OriginBackground.STREET_HUSTLER -> "Ajegunle"
                OriginBackground.OIL_REGION -> "Bonny Creek"
                OriginBackground.VILLAGE_FARMER -> "Iseyin Valley"
                OriginBackground.DYNASTY_HEIR -> "Maitama Heights"
                OriginBackground.DIASPORA_RETURNEE -> "Ikoyi Crescent"
            },
            background = background,
            familyType = familyType,
            familySecret = familySecret,
            ambition = ambition,
            empathy = empathy,
            riskAppetite = risk,
            loyalty = loyalty,
            cunning = cunning,
            health = 95,
            stress = 20,
            energy = 100,
            naraBalance = background.initialCash,
            politicalCapital = 15,
            streetClout = background.initialClout,
            godfatherFavor = background.initialGodfatherFavor,
            conscience = (empathy + loyalty) / 2,
            currentStage = LifeStage.CAMPUS,
            academicCourse = course,
            politicalOffice = "Freshman Undergrad & Aspiring Leader"
        )

        val padis = PadiGenerator.generateInitialPadis()
        val familyMembers = FamilyGenerator.generateFamily(familyType)
        val blackTax = FamilyGenerator.generateInitialBlackTaxRequests()

        val initialChats = listOf(
            ChatThread(
                id = "chat_segun",
                contactName = "Segun Ride-or-Die",
                contactRole = "Childhood Padi",
                lastMessage = "Senior man! You don pay hostel accommodation? Room 304 dey wait for us!",
                timestamp = "Just now",
                unreadCount = 1,
                messages = listOf(
                    ChatMessage(
                        id = "m1",
                        senderName = "Segun Ride-or-Die",
                        text = "Senior man! You don pay hostel accommodation? Room 304 dey wait for us! Make we lock down campus this semester o!",
                        timestamp = "10:14 AM"
                    )
                )
            ),
            ChatThread(
                id = "chat_family",
                contactName = "Balogun Dynasty WhatsApp",
                contactRole = "Family Group",
                lastMessage = "Mama Nkechi: May the Lord elevate my child beyond all mockers!",
                timestamp = "2h ago",
                unreadCount = 2,
                messages = listOf(
                    ChatMessage(
                        id = "mf1",
                        senderName = "Mama Nkechi",
                        text = "May the Lord elevate my child beyond all mockers! Make sure you go to church on Sunday and eat well.",
                        timestamp = "08:30 AM"
                    )
                )
            )
        )

        val initialChirps = listOf(
            ChirpPost(
                id = "cp_1",
                authorName = "Naija Infotech & Trends",
                handle = "@NaijaTrendsNow",
                content = "Fresh academic session resumes across federal universities today! Who is running for SUG President this year? Drop your posters! 🇳🇬🔥",
                likesCount = "14.2K",
                retweetsCount = "3.8K",
                timestamp = "1h ago",
                isVerified = true
            ),
            ChirpPost(
                id = "cp_2",
                authorName = "Lakos Vibe Police",
                handle = "@LakosVibes",
                content = "Fuel queues spotted in Surulere. Black market already selling ₦900 per litre. Naijaland which way??",
                likesCount = "8.4K",
                retweetsCount = "4.2K",
                timestamp = "3h ago",
                isVerified = false
            )
        )

        val initialNews = listOf(
            NewsFlash(
                id = "nf_1",
                source = "The Daily Chronicle",
                headline = "FEDERAL GOVERNMENT REVEALS MULTI-BILLION CAMPUS INFRASTRUCTURE GRANTS",
                summary = "Tertiary education trust fund announces major upgrades for student welfare across six geopolitical zones.",
                category = "EDUCATION",
                timeAgo = "2h ago"
            )
        )

        val firstEvent = EventCatalog.getEventForStage(LifeStage.CAMPUS, emptyList())

        return GameState(
            character = character,
            macroEconomy = MacroEconomy(),
            padis = padis,
            familyMembers = familyMembers,
            blackTaxQueue = blackTax,
            chatThreads = initialChats,
            chirpFeed = initialChirps,
            newsFlashes = initialNews,
            currentEvent = firstEvent,
            isOriginEngineActive = false
        )
    }

    fun applyChoice(state: GameState, choice: EventChoice): GameState {
        var char = state.character
        var macro = state.macroEconomy
        val history = state.eventHistory.toMutableList()
        state.currentEvent?.let { history.add(it.id) }

        // Check fatal risk
        if (choice.isFatalRisk && Random.nextDouble() < choice.fatalProbability) {
            return triggerDeath(state, "Tragic mishap during high-risk confrontation")
        }

        // Apply stat changes
        val newNara = (char.naraBalance - choice.costNara).coerceAtLeast(0)
        val newAmbition = (char.ambition + choice.ambitionDelta).coerceIn(0, 100)
        val newConscience = (char.conscience + choice.conscienceDelta).coerceIn(0, 100)
        val newClout = (char.streetClout + choice.cloutDelta).coerceIn(0, 100)
        val newPolCap = (char.politicalCapital + choice.politicalCapitalDelta).coerceIn(0, 100)
        val newHealth = (char.health + choice.healthDelta).coerceIn(0, 100)
        val newStress = (char.stress + choice.stressDelta).coerceIn(0, 100)
        val newGodfather = (char.godfatherFavor + choice.godfatherFavorDelta).coerceIn(0, 100)
        val newEnergy = (char.energy - choice.energyCost).coerceAtLeast(0)

        // Check health death condition
        if (newHealth <= 10 && Random.nextDouble() < 0.35) {
            return triggerDeath(state, "Sudden cardiovascular collapse induced by chronic stress and neglect")
        }

        // Macro economy adjustments
        val newAnger = (macro.angerIndex + choice.angerIndexDelta).coerceIn(0, 100)
        val updatedMacro = macro.copy(
            angerIndex = newAnger,
            securityLevel = when {
                newAnger > 80 -> SecurityLevel.YOUTH_UPRISING
                newAnger > 65 -> SecurityLevel.NATIONWIDE_STRIKE
                newAnger > 50 -> SecurityLevel.TENSION
                else -> SecurityLevel.PEACEFUL
            }
        )

        char = char.copy(
            naraBalance = newNara,
            ambition = newAmbition,
            conscience = newConscience,
            streetClout = newClout,
            politicalCapital = newPolCap,
            health = newHealth,
            stress = newStress,
            godfatherFavor = newGodfather,
            energy = newEnergy
        )

        // Create transaction record if money moved
        val newTx = if (choice.costNara != 0L) {
            BankTransaction(
                id = "tx_${System.currentTimeMillis()}",
                title = choice.label,
                amountNara = kotlin.math.abs(choice.costNara),
                isCredit = choice.costNara < 0,
                date = "Year ${char.year}",
                reference = "TXN-${Random.nextInt(100000, 999999)}"
            )
        } else null

        val updatedTransactions = if (newTx != null) {
            listOf(newTx) + state.bankTransactions
        } else state.bankTransactions

        // Add news flash
        val newNews = NewsFlash(
            id = "news_${System.currentTimeMillis()}",
            source = "The Naija Wire",
            headline = "${char.name.uppercase()}: ${choice.label.take(50).uppercase()}",
            summary = choice.outcomeNarrative,
            category = "BREAKING",
            timeAgo = "Just now"
        )

        // Advance to next event in stage
        val nextEvent = EventCatalog.getEventForStage(char.currentStage, history)

        return state.copy(
            character = char,
            macroEconomy = updatedMacro,
            eventHistory = history,
            currentEvent = nextEvent,
            bankTransactions = updatedTransactions,
            newsFlashes = listOf(newNews) + state.newsFlashes,
            lastOutcomeMessage = choice.outcomeNarrative
        )
    }

    fun advanceLifeTurn(state: GameState): GameState {
        val char = state.character
        val newAge = char.age + 2
        val newYear = char.year + 2

        // Determine if stage transitions
        val nextStage = when {
            newAge in 18..23 -> LifeStage.CAMPUS
            newAge in 24..30 -> LifeStage.HUSTLE_YEARS
            newAge in 31..42 -> LifeStage.LOCAL_POWER
            newAge in 43..54 -> LifeStage.STATE_POWER
            newAge in 55..64 -> LifeStage.NATIONAL_POWER
            newAge in 65..69 -> LifeStage.THE_RACE
            newAge >= 70 -> LifeStage.THE_VILLA
            else -> char.currentStage
        }

        // Office title upgrade based on stage
        val updatedOffice = when (nextStage) {
            LifeStage.CAMPUS -> "SUG President & Youth Delegate"
            LifeStage.HUSTLE_YEARS -> "Strategic Consultant & Party Youth Leader"
            LifeStage.LOCAL_POWER -> "Executive Chairman, Mainland Local Government"
            LifeStage.STATE_POWER -> "Executive Governor of Lakos State"
            LifeStage.NATIONAL_POWER -> "Distinguished Senator & Ministerial Envoy"
            LifeStage.THE_RACE -> "Flagbearer & Presidential Candidate"
            LifeStage.THE_VILLA -> "President & Commander-in-Chief of Naijaland"
            else -> char.politicalOffice
        }

        // Age mortality check: probability increases over age 72
        if (newAge >= 75) {
            val mortalityChance = (newAge - 70) * 0.08
            if (Random.nextDouble() < mortalityChance) {
                return triggerDeath(state, "Passed away peacefully in his private residence, surrounded by family and state flags")
            }
        }

        val updatedChar = char.copy(
            age = newAge,
            year = newYear,
            currentStage = nextStage,
            politicalOffice = updatedOffice,
            energy = 100, // replenish energy
            health = (char.health - Random.nextInt(1, 4)).coerceAtLeast(15)
        )

        // Macro economy fluctuations
        val newAnger = (state.macroEconomy.angerIndex + Random.nextInt(-5, 8)).coerceIn(10, 95)
        val newNaraToDollar = state.macroEconomy.naraToDollar + Random.nextInt(20, 80)
        val newOilPrice = (state.macroEconomy.oilBarrelPrice + Random.nextDouble(-4.0, 4.0)).coerceIn(55.0, 115.0)

        // Rotate or activate dynamic crises based on national state
        val updatedCrises = mutableListOf(CrisisType.FUEL_SCARCITY)
        if (newAnger > 50) updatedCrises.add(CrisisType.STUDENT_STRIKE)
        if (newAnger > 65) updatedCrises.add(CrisisType.NARA_CRASH)
        if (newAnger > 75) updatedCrises.add(CrisisType.POLICE_BRUTALITY)
        if (Random.nextDouble() < 0.30) updatedCrises.add(CrisisType.GRID_COLLAPSE)

        val updatedMacro = state.macroEconomy.copy(
            angerIndex = newAnger,
            naraToDollar = newNaraToDollar,
            oilBarrelPrice = newOilPrice,
            activeCrises = updatedCrises,
            studentAnger = (newAnger + Random.nextInt(-8, 12)).coerceIn(10, 100),
            traderAnger = (newAnger + Random.nextInt(-10, 8)).coerceIn(10, 100),
            transportAnger = (newAnger + Random.nextInt(-5, 15)).coerceIn(10, 100),
            youthAnger = (newAnger + Random.nextInt(-3, 16)).coerceIn(10, 100),
            workerAnger = (newAnger + Random.nextInt(-12, 6)).coerceIn(10, 100),
            securityLevel = when {
                newAnger >= 85 -> SecurityLevel.YOUTH_UPRISING
                newAnger >= 70 -> SecurityLevel.NATIONWIDE_STRIKE
                newAnger >= 50 -> SecurityLevel.TENSION
                else -> SecurityLevel.PEACEFUL
            }
        )

        // If entering THE_RACE, launch presidential election tally!
        val election = if (nextStage == LifeStage.THE_RACE && state.electionTally == null) {
            generateElectionTally(updatedChar)
        } else state.electionTally

        val nextEvent = EventCatalog.getEventForStage(nextStage, state.eventHistory)

        return state.copy(
            character = updatedChar,
            macroEconomy = updatedMacro,
            currentEvent = nextEvent,
            electionTally = election,
            lastOutcomeMessage = "Two years have passed in Naijaland. You are now $newAge years old, serving as $updatedOffice."
        )
    }

    fun generateElectionTally(char: Character): ElectionTally {
        val states = listOf(
            Triple("Lakos State (Mega Commercial Hub)", 24, 0.65),
            Triple("Abuka FCT (Federal Capital)", 12, 0.55),
            Triple("Kano State (Northern Savanna Heartland)", 20, 0.48),
            Triple("Rivers State (Oil Creek Delta)", 18, 0.60),
            Triple("Enugu State (Coal East Belt)", 14, 0.70),
            Triple("Benue State (Middle Agricultural Belt)", 14, 0.52),
            Triple("Kaduna State (Command Garrison Hub)", 16, 0.50)
        )

        val stateResults = states.map { (name, ev, playerAdvantage) ->
            val totalVotes = Random.nextLong(1_200_000, 3_500_000)
            val bonus = (char.politicalCapital + char.streetClout) / 300.0
            val effectiveAdvantage = (playerAdvantage + bonus - 0.25).coerceIn(0.35, 0.75)
            val pVotes = (totalVotes * effectiveAdvantage).toLong()
            val oVotes = totalVotes - pVotes
            StateElectoralResult(
                stateName = name,
                electoralVotes = ev,
                playerVotes = pVotes,
                opponentVotes = oVotes,
                isDeclaredForPlayer = pVotes > oVotes
            )
        }

        val playerTotalEV = stateResults.filter { it.isDeclaredForPlayer }.sumOf { it.electoralVotes }
        val opponentTotalEV = stateResults.filter { !it.isDeclaredForPlayer }.sumOf { it.electoralVotes }
        val won = playerTotalEV >= 60 // 118 total EV, need > 59

        return ElectionTally(
            playerCandidateName = char.name,
            opponentCandidateName = "Alhaji Bello Danjuma",
            runningMateName = "Dr. Amina Shehu (Zonal Technocrat)",
            playerTotalElectoral = playerTotalEV,
            opponentTotalElectoral = opponentTotalEV,
            playerPopularVotes = stateResults.sumOf { it.playerVotes },
            opponentPopularVotes = stateResults.sumOf { it.opponentVotes },
            stateResults = stateResults,
            isConcluded = true,
            wonElection = won
        )
    }

    fun triggerDeath(state: GameState, cause: String): GameState {
        val char = state.character.copy(
            isAlive = false,
            causeOfDeath = cause,
            currentStage = LifeStage.FUNERAL
        )

        val conscience = char.conscience
        val power = char.politicalCapital
        val clout = char.streetClout

        val legacyTitle = when {
            conscience > 75 && power > 60 -> "THE PEOPLE'S REFORMER"
            conscience < 35 && power > 75 -> "THE UNTOUCHABLE GODFATHER"
            clout > 80 -> "THE LION OF THE STREETS"
            char.naraBalance > 500_000_000 -> "THE BILLIONAIRE FIXER"
            conscience > 65 -> "THE CONSCIENCE OF THE NATION"
            else -> "THE PRAGMATIC KINGMAKER"
        }

        val legacyScore = (char.politicalCapital * 3) + (char.streetClout * 2) +
                (char.conscience * 2) + (if (char.naraBalance > 100_000_000) 200 else 80)

        val attendees = listOf(
            "The Sitting President of Naijaland and 18 State Governors",
            "The National Association of Market Women with ceremonial white lace",
            "Ambassadors of Great Britain, United States, and European Union",
            "Padi Tari Billionaire, who arrived in a convoy of 8 bulletproof Maybachs",
            "Thousands of students carrying banners: 'Adieu, Our Champion!'"
        )

        val boycotted = listOf(
            "Bitter political rival Alhaji Bello Danjuma, who refused to send flowers",
            "The Federal Anti-Corruption Bureau Director, who monitored the bank accounts"
        )

        val funeral = FuneralSummary(
            deceasedName = char.name,
            deceasedAge = char.age,
            finalOffice = char.politicalOffice,
            causeOfDeath = cause,
            newspaperHeadline = "TITAN OF NAIJALAND PASSES ON AT ${char.age}: ${char.name.uppercase()}",
            newspaperSubhead = "Flags fly at half-mast across all 36 states as millions mourn the ${legacyTitle.lowercase()}",
            newspaperEditorialSnippet = "Whether praised as an uncompromising kingmaker or revered as the architect of modern infrastructure, no Nigerian can deny that ${char.name} played the Long Game with absolute masterstroke.",
            legacyTitle = legacyTitle,
            legacyScore = legacyScore,
            attendeesList = attendees,
            boycottersList = boycotted,
            willContestDispute = "At the reading of the will in Victoria Island, half-siblings and rival step-mothers clashed in the lobby over the Ikoyi waterfront mansion and offshore family trust.",
            survivingHeirName = "Chukwudi 'Junior' Balogun",
            survivingEstateNara = char.naraBalance
        )

        return state.copy(
            character = char,
            funeralSummary = funeral,
            currentEvent = null,
            lastOutcomeMessage = "Your journey has concluded. Naijaland gathers to render its final verdict."
        )
    }

    fun startDynastyNextGen(state: GameState): GameState {
        val deceased = state.character
        val heirName = state.funeralSummary?.survivingHeirName ?: "Junior ${deceased.name}"
        val inheritedNara = (deceased.naraBalance * 0.70).toLong() // 30% inheritance tax/disputes

        val nextGenChar = Character(
            name = heirName,
            alias = "The Dynasty Heir",
            gender = "Male",
            age = 18,
            year = deceased.year + 2,
            stateOfOrigin = deceased.stateOfOrigin,
            neighbourhood = "Maitama & Ikoyi",
            background = OriginBackground.DYNASTY_HEIR,
            familyType = FamilyStructure.MONOGAMOUS,
            familySecret = FamilySecret.SWISS_SAFE_KEY,
            ambition = 80,
            empathy = 55,
            riskAppetite = 65,
            loyalty = 70,
            cunning = 75,
            health = 98,
            stress = 15,
            energy = 100,
            naraBalance = inheritedNara,
            politicalCapital = 40, // inherited reputation
            streetClout = 50,
            godfatherFavor = 60,
            conscience = 60,
            currentStage = LifeStage.CAMPUS,
            academicCourse = AcademicCourse.LAW,
            politicalOffice = "Dynasty Heir & Young Leader",
            generationNumber = deceased.generationNumber + 1
        )

        val nextPadis = PadiGenerator.generateInitialPadis()
        val nextFamily = FamilyGenerator.generateFamily(FamilyStructure.MONOGAMOUS)
        val firstEvent = EventCatalog.getEventForStage(LifeStage.CAMPUS, emptyList())

        return GameState(
            character = nextGenChar,
            macroEconomy = MacroEconomy(),
            padis = nextPadis,
            familyMembers = nextFamily,
            blackTaxQueue = FamilyGenerator.generateInitialBlackTaxRequests(),
            currentEvent = firstEvent,
            isOriginEngineActive = false,
            funeralSummary = null,
            electionTally = null,
            lastOutcomeMessage = "Dynasty Mode Activated: You now play as Generation ${nextGenChar.generationNumber}, carrying your father's wealth, enemies, and legacy!"
        )
    }
}
