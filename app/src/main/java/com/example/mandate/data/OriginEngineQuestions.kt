package com.example.mandate.data

data class OriginQuestion(
    val id: Int,
    val category: String,
    val situationTitle: String,
    val dilemmaText: String,
    val pidginFlavor: String,
    val options: List<OriginAnswer>
)

data class OriginAnswer(
    val title: String,
    val description: String,
    val traitEffects: Map<String, Int>, // ambition, empathy, risk, loyalty, cunning
    val narrativeFlavor: String
)

object OriginEngineData {
    val questions = listOf(
        OriginQuestion(
            id = 1,
            category = "ACADEMIC ADVANCEMENT",
            situationTitle = "The Stolen Exam Leak",
            dilemmaText = "It is 48 hours before your final WAEC secondary school examination. Your childhood friend Segun pulls you behind the chemistry laboratory to show you a leaked photocopied exam paper he purchased from a rogue invigilator for ₦5,000.",
            pidginFlavor = "Segun whisper: 'Senior man, expo don land! Wetin we dey wait for? You wan fail or you wan enter university?'",
            options = listOf(
                OriginAnswer(
                    title = "Memorize the Questions in Silence",
                    description = "Take mental notes, tell no one, and secure straight A's without leaving any paper trail.",
                    traitEffects = mapOf("ambition" to 25, "cunning" to 20, "empathy" to -10, "risk" to 15),
                    narrativeFlavor = "You kept silent and smiled as the exact questions appeared on the chalkboard."
                ),
                OriginAnswer(
                    title = "Burn Midnight Oil Honestly",
                    description = "Refuse the leaked copy flatly. Study through the night with a kerosene lantern to keep your dignity.",
                    traitEffects = mapOf("empathy" to 25, "loyalty" to 20, "ambition" to -15, "cunning" to -10),
                    narrativeFlavor = "Your eyes burned with kerosene smoke, but your conscience remained untainted."
                ),
                OriginAnswer(
                    title = "Monetize the Opportunity",
                    description = "Photocopy 20 copies and sell them to wealthy classmates in Ikoyi for ₦15,000 each.",
                    traitEffects = mapOf("ambition" to 35, "risk" to 30, "cunning" to 30, "empathy" to -25),
                    narrativeFlavor = "Your schoolbag swelled with crisp currency. You tasted the raw power of commercial leverage."
                ),
                OriginAnswer(
                    title = "Shield Your Friend from Arrest",
                    description = "Warn Segun that plainclothes detectives are searching hostels, and persuade him to burn the papers.",
                    traitEffects = mapOf("loyalty" to 30, "empathy" to 25, "ambition" to 5, "risk" to -10),
                    narrativeFlavor = "Segun burned the papers in the gutter. He never forgot who saved his future."
                )
            )
        ),
        OriginQuestion(
            id = 2,
            category = "STREET MORALITY",
            situationTitle = "The Danfo Conductor's Excess Change",
            dilemmaText = "Under torrential Lakos rainfall at Oshodi bus terminal, you step off a jam-packed yellow Danfo bus. In the chaotic scramble, the drenched conductor mistakenly hands you a crisp ₦5,000 note instead of ₦500 change.",
            pidginFlavor = "Rain dey beat everybody, conductor dey shout: 'Enter with your change o! No delay me!'",
            options = listOf(
                OriginAnswer(
                    title = "Disappear into the Rainstorm",
                    description = "Slip the ₦5,000 note into your pocket and melt into the market crowd: 'God has provided for my week.'",
                    traitEffects = mapOf("ambition" to 20, "cunning" to 25, "empathy" to -25, "risk" to 10),
                    narrativeFlavor = "Street survival logic prevailed. You bought fried plantain and paid for your notebook."
                ),
                OriginAnswer(
                    title = "Return the Money to the Conductor",
                    description = "Tap the conductor's wet shoulder and hand back the surplus note: 'Oga, your change was too much.'",
                    traitEffects = mapOf("empathy" to 35, "loyalty" to 20, "cunning" to -15, "ambition" to -10),
                    narrativeFlavor = "The conductor looked at you in astonishment: 'My pikin, God go bless your entire generation!'"
                ),
                OriginAnswer(
                    title = "Streetwise Compromise",
                    description = "Hand him back ₦2,500 and claim the rest as fair compensation for the ruined rain-soaked seat.",
                    traitEffects = mapOf("cunning" to 25, "ambition" to 15, "risk" to 15, "empathy" to 5),
                    narrativeFlavor = "You struck a pragmatist's deal on the muddy asphalt. Neither pure saint nor thief."
                )
            )
        ),
        OriginQuestion(
            id = 3,
            category = "FAMILY DUTY VS AMBITION",
            situationTitle = "The Hospital Bed Deposit",
            dilemmaText = "Your 14-year-old half-sister is rushed to the hospital with acute appendicitis requiring an immediate ₦80,000 surgery deposit. That exact amount is the lifetime savings you earmarked to purchase the Student Union Presidential nomination form tomorrow morning.",
            pidginFlavor = "Doctor warns: 'Without deposit before midnight, we cannot wheel her into the theater.' Mother weeps on hospital floor.",
            options = listOf(
                OriginAnswer(
                    title = "Hand Over Your Nomination Savings",
                    description = "Surrender the entire ₦80,000 to the cashier immediately. 'A human life comes before any political crown.'",
                    traitEffects = mapOf("empathy" to 35, "loyalty" to 30, "ambition" to -20, "cunning" to -10),
                    narrativeFlavor = "The surgery was a complete success. Your political ambitions were postponed, but your sister lived."
                ),
                OriginAnswer(
                    title = "Purchase the Nomination Form Anyway",
                    description = "Proceed to buy the form. Once elected, you will command the union welfare emergency fund to settle the bill.",
                    traitEffects = mapOf("ambition" to 40, "risk" to 35, "cunning" to 20, "empathy" to -35),
                    narrativeFlavor = "You filed your candidacy with trembling hands. Cold calculation took over your young heart."
                ),
                OriginAnswer(
                    title = "Pawn Assets & Negotiate Medical Credit",
                    description = "Pawn your wristwatch and laptop for ₦40,000, and use your oratorical charm to convince the Chief Doctor to start surgery on credit.",
                    traitEffects = mapOf("cunning" to 30, "ambition" to 20, "empathy" to 20, "risk" to 15),
                    narrativeFlavor = "A tactical miracle! The doctor agreed to the terms, and you remained on the ballot."
                )
            )
        ),
        OriginQuestion(
            id = 4,
            category = "GODFATHER DYNAMICS",
            situationTitle = "The Chief's Shattered Windshield",
            dilemmaText = "During a fierce Sunday street football match in Surulere, your rogue shot smashes the rear windscreen of Chief Alabi's Mercedes Benz. He is the ward's most feared political godfather and bankroller of candidates.",
            pidginFlavor = "Spectators gasp: 'Chineke! Chief Benz don break! Run before his thugs arrive!'",
            options = listOf(
                OriginAnswer(
                    title = "Flee and Vanish into Back Alleys",
                    description = "Sprint through the maze of shanties and hide under your grandmother's bed until nightfall.",
                    traitEffects = mapOf("risk" to -25, "cunning" to 20, "ambition" to -15, "loyalty" to -10),
                    narrativeFlavor = "You vanished like smoke. Chief never discovered whose boot launched the fateful strike."
                ),
                OriginAnswer(
                    title = "Prostrate and Offer Your Loyalty",
                    description = "Walk boldly to Chief's fortified iron gates, prostrate on the gravel, apologize, and offer your service.",
                    traitEffects = mapOf("cunning" to 35, "ambition" to 25, "loyalty" to 20, "empathy" to -5),
                    narrativeFlavor = "Chief laughed: 'This boy has liver. From today, you will carry my campaign dispatch bag.'"
                ),
                OriginAnswer(
                    title = "Stand Your Ground for the Community Youths",
                    description = "Point out respectfully that his driver parked on the only public football pitch allocated to local youths.",
                    traitEffects = mapOf("empathy" to 30, "risk" to 35, "loyalty" to 25, "ambition" to 10),
                    narrativeFlavor = "The neighbourhood held its breath. Chief respected your spine and ordered his driver to park elsewhere."
                )
            )
        ),
        OriginQuestion(
            id = 5,
            category = "INTEGRITY & PATRONAGE",
            situationTitle = "The Whistleblower's Ghost Payroll",
            dilemmaText = "As an intern in the State Civil Service Audit Commission, you uncover a fraudulent ledger funnelling ₦40 million monthly to ghost workers, controlled by the Permanent Secretary who promised you a permanent appointment upon graduation.",
            pidginFlavor = "Senior colleague whispers: 'Close your eyes. In Naijaland, you don't bite the hand that intends to feed your family.'",
            options = listOf(
                OriginAnswer(
                    title = "Leak the Dossier to Investigative Journalists",
                    description = "Smuggle photocopies to investigative reporters. Public funds meant for clinics must not be looted.",
                    traitEffects = mapOf("empathy" to 35, "loyalty" to 25, "ambition" to -20, "risk" to 30),
                    narrativeFlavor = "Front-page headlines rocked the state capital. You sacrificed your career to spark an inquiry."
                ),
                OriginAnswer(
                    title = "Trade the File for Instant Career Ascent",
                    description = "Bring the file privately to the Permanent Secretary's mahogany desk and request your overseas scholarship letter today.",
                    traitEffects = mapOf("ambition" to 40, "cunning" to 35, "empathy" to -30, "risk" to 15),
                    narrativeFlavor = "The Secretary smiled warmly, shredded the document, and handed you a signed fellowship to Cambridge."
                ),
                OriginAnswer(
                    title = "Redact Lower Clerks to Protect the Vulnerable",
                    description = "Alter the report so junior typists are spared while subtly flagging senior discrepancies for future leverage.",
                    traitEffects = mapOf("cunning" to 30, "empathy" to 15, "ambition" to 20, "loyalty" to 10),
                    narrativeFlavor = "You played 4D chess in the bureaucracy, protecting the weak while retaining political leverage."
                )
            )
        ),
        OriginQuestion(
            id = 6,
            category = "LAND & ANCESTRAL HERITAGE",
            situationTitle = "The Village Farmland Highway Dispute",
            dilemmaText = "A multinational pipeline consortium offers ₦100 million to buy ancestral community farmland. The clan elders support the payout for their chieftaincy titles, but 40 subsistence farming families will be permanently evicted without land.",
            pidginFlavor = "Elder bellows: 'Tradition gives us the right to sell! Who are these poor tenants to complain?'",
            options = listOf(
                OriginAnswer(
                    title = "Organize the Farming Families to Resist",
                    description = "Stand in front of the surveyors with the tenant farmers and file an injunction with legal aid.",
                    traitEffects = mapOf("empathy" to 35, "loyalty" to 30, "ambition" to -15, "risk" to 25),
                    narrativeFlavor = "The farming families blessed your name with yams and palm wine. You chose grassroots justice over gold."
                ),
                OriginAnswer(
                    title = "Broker the Corporate Transaction",
                    description = "Help the clan elders finalize the deal and secure a 10% consultancy broker fee for your personal war chest.",
                    traitEffects = mapOf("ambition" to 40, "cunning" to 30, "empathy" to -35, "risk" to 15),
                    narrativeFlavor = "You banked an enormous financial cut. The elders praised your business acumen, but farmers wept."
                ),
                OriginAnswer(
                    title = "Architect a Perpetual Community Trust",
                    description = "Structure a perpetual equity agreement where the pipeline pays yearly royalties into a community school trust.",
                    traitEffects = mapOf("cunning" to 30, "empathy" to 25, "ambition" to 25, "loyalty" to 20),
                    narrativeFlavor = "A visionary compromise! Both the consortium and the farmers signed the historic covenant."
                )
            )
        ),
        OriginQuestion(
            id = 7,
            category = "MASS MOVEMENTS & THE STREET",
            situationTitle = "The Campus Police Siege",
            dilemmaText = "Special police squad operatives arrest student union comrades on fabricated cultism allegations. Over 4,000 furious students are massing at the main university gate with flaming tyres and placards ready to storm the divisional police station.",
            pidginFlavor = "Comrades roar: 'Aluta Continua! Today na today! If dem shoot tear gas, we go return stone!'",
            options = listOf(
                OriginAnswer(
                    title = "Climb the Barricade & Lead the March",
                    description = "Take the megaphone, climb atop the gate, and lead the peaceful mass march directly into the police cordon.",
                    traitEffects = mapOf("empathy" to 30, "risk" to 40, "ambition" to 25, "loyalty" to 25),
                    narrativeFlavor = "Your fiery speech stirred thousands. Tear gas burned your lungs, but your name became a campus legend."
                ),
                OriginAnswer(
                    title = "Negotiate Through Back-Channel Bribes",
                    description = "Slip into the Area Commander's private quarters and arrange the release of only your close inner circle.",
                    traitEffects = mapOf("cunning" to 40, "loyalty" to 20, "empathy" to -20, "ambition" to 20),
                    narrativeFlavor = "Your close friends walked out the back door in slippers while the riot burned outside."
                ),
                OriginAnswer(
                    title = "Retreat to the Library Sanctuary",
                    description = "Refuse to take part in mob unrest. 'No dead student leader ever became Governor or President.'",
                    traitEffects = mapOf("ambition" to 20, "risk" to -35, "empathy" to -25, "loyalty" to -25),
                    narrativeFlavor = "You completed your term paper while ambulances wailed down the avenue. Cold survival."
                )
            )
        )
    )
}
