package com.example.mandate.data

import com.example.mandate.model.EventChoice
import com.example.mandate.model.GameEvent
import com.example.mandate.model.LifeStage

object EventCatalog {
    val stageEvents: Map<LifeStage, List<GameEvent>> = mapOf(
        LifeStage.CAMPUS to listOf(
            GameEvent(
                id = "campus_1",
                stage = LifeStage.CAMPUS,
                category = "Campus Admission Drama",
                headline = "The JAMB Cut-Off Switcheroo",
                narration = "You applied to study Medicine at the premier Federal University of Lakos with a score of 285. But when the merit list comes out on the departmental notice board, your name appears under 'Fisheries & Agricultural Science'.",
                pidginQuote = "Head of Admin whisper: 'My pikin, admission na by connection. If your papa no be general, na catfish you go rear.'",
                options = listOf(
                    EventChoice(
                        id = "c1_a",
                        label = "Pay ₦150,000 'Registrar Sorting Fee' to switch to Law",
                        description = "Borrow the money from your elder cousin and grease the registrar's palms.",
                        costNara = 150_000,
                        energyCost = 20,
                        ambitionDelta = 15,
                        conscienceDelta = -10,
                        cloutDelta = 10,
                        politicalCapitalDelta = 15,
                        outcomeNarrative = "Within 48 hours, your admission slip magically transformed into Faculty of Law. Welcome to legal loopholes."
                    ),
                    EventChoice(
                        id = "c1_b",
                        label = "Embrace Agriculture & become student union farmer",
                        description = "Study the course proudly, sell organic eggs to female hostels, and build grassroot popularity.",
                        costNara = 0,
                        energyCost = 15,
                        ambitionDelta = 5,
                        conscienceDelta = 15,
                        cloutDelta = 20,
                        stressDelta = -10,
                        outcomeNarrative = "Your student farm business boomed! Hostel students nickname you 'The Agric Messiah'. You now control the student vote."
                    ),
                    EventChoice(
                        id = "c1_c",
                        label = "Organize a student sit-in protest against quota injustice",
                        description = "Mobilize rejected candidates with placards in front of the Senate Building.",
                        costNara = 15_000,
                        energyCost = 25,
                        ambitionDelta = 20,
                        cloutDelta = 35,
                        stressDelta = 20,
                        angerIndexDelta = 5,
                        outcomeNarrative = "Security fired water cannons, but TV stations broadcast your fiery speech. You are suddenly a campus celebrity."
                    )
                )
            ),
            GameEvent(
                id = "campus_2",
                stage = LifeStage.CAMPUS,
                category = "Hostel Turf War",
                headline = "Midnight Knock: The Secret Brotherhood",
                narration = "At 2:00 AM, heavy footsteps echo outside your hostel room. Three masked senior students wearing black berets step in. They invite you to join the 'Pyramid Vanguard'—the most influential campus brotherhood with alumni in high court and state ministries.",
                pidginQuote = "Leader hiss: 'You get mouth for campus. Either you join the table or you become the suya on it.'",
                options = listOf(
                    EventChoice(
                        id = "c2_a",
                        label = "Take the blood oath and join the brotherhood",
                        description = "Gain violent protection and lifelong fraternity connections in high places.",
                        costNara = 50_000,
                        energyCost = 20,
                        ambitionDelta = 25,
                        conscienceDelta = -25,
                        cloutDelta = 25,
                        godfatherFavorDelta = 15,
                        stressDelta = 20,
                        outcomeNarrative = "You endured the brutal initiation. Now when you walk through campus, even the security guards look away in deference."
                    ),
                    EventChoice(
                        id = "c2_b",
                        label = "Decline politely and join the University Debate & Law Clinic",
                        description = "Keep your hands clean and build reputation purely through intellectual grit.",
                        costNara = 10_000,
                        energyCost = 15,
                        ambitionDelta = 10,
                        conscienceDelta = 20,
                        cloutDelta = 15,
                        healthDelta = 5,
                        outcomeNarrative = "They whispered threats in your ear, but you won the All-Nigeria Inter-Varsity Debate. The Vice-Chancellor shook your hand."
                    ),
                    EventChoice(
                        id = "c2_c",
                        label = "Run for Student Union (SUG) President as an independent candidate",
                        description = "Channel student frustration against brotherhood intimidation into a mass electoral campaign.",
                        costNara = 80_000,
                        energyCost = 30,
                        ambitionDelta = 30,
                        conscienceDelta = 10,
                        cloutDelta = 40,
                        politicalCapitalDelta = 25,
                        stressDelta = 25,
                        outcomeNarrative = "You swept all five faculties in a landslide! You are now SUG President, commanding 22,000 university students."
                    )
                )
            )
        ),
        LifeStage.HUSTLE_YEARS to listOf(
            GameEvent(
                id = "hustle_1",
                stage = LifeStage.HUSTLE_YEARS,
                category = "Youth Service (NYSC)",
                headline = "The Borno Remote Camp Posting",
                narration = "Your National Youth Service call-up letter drops: you have been posted to a remote desert LGA bordering Lake Chad, 1,400km away from your networks in Lakos.",
                pidginQuote = "NYSC official at camp gate: 'If you want relocation to ministry in Abuka, bring ₦250k or go face the desert.'",
                options = listOf(
                    EventChoice(
                        id = "h1_a",
                        label = "Pay ₦250,000 to redeploy to the Ministry of Finance in Abuka",
                        description = "Borrow from Tari and position yourself right beside federal contract directors.",
                        costNara = 250_000,
                        energyCost = 15,
                        ambitionDelta = 20,
                        conscienceDelta = -10,
                        politicalCapitalDelta = 20,
                        godfatherFavorDelta = 20,
                        outcomeNarrative = "You landed in Abuka! In the corridors of power, you photocopied confidential budget memos and shook hands with senators."
                    ),
                    EventChoice(
                        id = "h1_b",
                        label = "Accept the remote posting and teach rural community children",
                        description = "Travel to the frontier. Build grassroot connection with local emirs and nomadic communities.",
                        costNara = 0,
                        energyCost = 25,
                        ambitionDelta = 5,
                        conscienceDelta = 25,
                        cloutDelta = 15,
                        stressDelta = 10,
                        healthDelta = -5,
                        outcomeNarrative = "The villagers loved your selfless dedication. The local Emir awarded you an honorary youth chieftaincy title: 'Dan Haske'."
                    ),
                    EventChoice(
                        id = "h1_c",
                        label = "Ghost the camp and run a Crypto & POS syndicate in Lakos",
                        description = "Bribe the NYSC local inspector ₦30k monthly while trading volatile coins and agency banking.",
                        costNara = 100_000,
                        energyCost = 20,
                        ambitionDelta = 25,
                        riskAppetiteDelta = 20,
                        cloutDelta = 20,
                        conscienceDelta = -15,
                        outcomeNarrative = "Your POS chain pulled ₦850k pure profit monthly during the currency scarcity! You bought your first foreign-used Lexus."
                    )
                )
            ),
            GameEvent(
                id = "hustle_2",
                stage = LifeStage.HUSTLE_YEARS,
                category = "Party Youth Wing",
                headline = "The Godfather's Midnight Brown Envelope",
                narration = "Chief Balogun, the apex chieftain of the ruling party, invites you to his private Ikoyi suite. He points at a heavy brown carton containing ₦15 million in wrapped notes. He needs someone to organize 500 boys for the upcoming ward congress.",
                pidginQuote = "Chief puffs cigar: 'Look here, boy. Power is not served in a restaurant. You snatch it, you grab it, and you run with it.'",
                options = listOf(
                    EventChoice(
                        id = "h2_a",
                        label = "Take the carton and execute the field operation ruthlessly",
                        description = "Disburse funds to park boys and guarantee Chief's candidate sweeps the congress.",
                        costNara = -5_000_000, // Gain 5m cut
                        energyCost = 30,
                        ambitionDelta = 30,
                        conscienceDelta = -30,
                        politicalCapitalDelta = 25,
                        godfatherFavorDelta = 35,
                        cloutDelta = 20,
                        stressDelta = 20,
                        outcomeNarrative = "The congress was locked down tight. Chief patted your shoulder: 'From now on, you sit at my dinner table.'"
                    ),
                    EventChoice(
                        id = "h2_b",
                        label = "Decline the cash; offer to manage media strategy and youth debates instead",
                        description = "Keep blood off your hands while proving you are an indispensable communication strategist.",
                        costNara = 0,
                        energyCost = 20,
                        ambitionDelta = 15,
                        conscienceDelta = 15,
                        cloutDelta = 25,
                        politicalCapitalDelta = 15,
                        godfatherFavorDelta = 10,
                        outcomeNarrative = "Chief laughed at your timidity, but your slick viral videos dominated social media. You proved your modern worth."
                    )
                )
            )
        ),
        LifeStage.LOCAL_POWER to listOf(
            GameEvent(
                id = "local_1",
                stage = LifeStage.LOCAL_POWER,
                category = "Council Administration",
                headline = "The Local Government 40% Kickback Demand",
                narration = "You are now elected Chairman of Mainland Local Government Area. The state godfather's emissary enters your mahogany office with a signed bank remittance slip demanding 40% of the monthly federal allocation.",
                pidginQuote = "Emissary whispers: 'Oga Chairman, don't play hero. Without Baba, you wouldn't even be councillor of public toilets.'",
                options = listOf(
                    EventChoice(
                        id = "l1_a",
                        label = "Sign the remittance quietly and take your own 15% cut",
                        description = "Play by the rules of the system. Starve local clinic renovations, but keep your seat safe.",
                        costNara = -12_000_000,
                        energyCost = 15,
                        ambitionDelta = 20,
                        conscienceDelta = -35,
                        godfatherFavorDelta = 30,
                        cloutDelta = -15,
                        angerIndexDelta = 5,
                        outcomeNarrative = "Baba sent a thumbs-up emoji on WhatsApp. You bought a plot in Banana Island, but the local council health center has no drip water."
                    ),
                    EventChoice(
                        id = "l1_b",
                        label = "Refuse and divert the entire funds to renovate 12 Primary Health Centres",
                        description = "Defy the godfather openly. Back your move with overwhelming community goodwill.",
                        costNara = 0,
                        energyCost = 30,
                        ambitionDelta = 15,
                        conscienceDelta = 35,
                        cloutDelta = 45,
                        godfatherFavorDelta = -40,
                        stressDelta = 30,
                        outcomeNarrative = "Mothers and market traders showered you with white powder and songs! But Baba cancelled your invitation to the State Executive Council."
                    ),
                    EventChoice(
                        id = "l1_c",
                        label = "Audit the LGA ghost worker payroll to find the 40% without touching clinic funds",
                        description = "Expose 600 fake names on the payroll and use the reclaimed cash to satisfy both Baba and the people.",
                        costNara = 1_000_000,
                        energyCost = 25,
                        ambitionDelta = 25,
                        conscienceDelta = 10,
                        politicalCapitalDelta = 30,
                        godfatherFavorDelta = 15,
                        cloutDelta = 25,
                        outcomeNarrative = "A masterstroke! You trimmed the parasitic ghost workers and emerged as the smartest technocrat in the state."
                    )
                )
            ),
            GameEvent(
                id = "local_2",
                stage = LifeStage.LOCAL_POWER,
                category = "Market Turf & Union Strike",
                headline = "The Oshodi Market Women Mutiny",
                narration = "The Iya-Oloja (Market Women General) has ordered all 15,000 market women to shut their stalls and dump rotten tomatoes in front of your council headquarters over high stall levies.",
                pidginQuote = "Iya-Oloja shouts: 'If this young boy think say he fit tax our bitterleaf, we go strip naked for council gate!'",
                options = listOf(
                    EventChoice(
                        id = "l2_a",
                        label = "Prostrate before Iya-Oloja with ₦5M party gift and slash the stall levy by half",
                        description = "Eat humble pie, respect cultural matriarchal authority, and win their absolute electoral loyalty.",
                        costNara = 5_000_000,
                        energyCost = 20,
                        conscienceDelta = 10,
                        cloutDelta = 35,
                        politicalCapitalDelta = 25,
                        angerIndexDelta = -10,
                        outcomeNarrative = "Iya-Oloja laughed heartily, poured honey on your head, and declared: 'You are our beloved son!' The market vote is locked."
                    ),
                    EventChoice(
                        id = "l2_b",
                        label = "Deploy council task force and teargas the gate to assert state authority",
                        description = "Demonstrate zero tolerance for civil disruption. Crush the protest by force.",
                        costNara = 2_000_000,
                        energyCost = 25,
                        ambitionDelta = 20,
                        conscienceDelta = -25,
                        cloutDelta = -30,
                        angerIndexDelta = 15,
                        stressDelta = 25,
                        outcomeNarrative = "The stalls reopened under armed guard, but a curse was laid upon your name. Every woman in the ward now prays against you."
                    )
                )
            )
        ),
        LifeStage.STATE_POWER to listOf(
            GameEvent(
                id = "state_1",
                stage = LifeStage.STATE_POWER,
                category = "State Gubernatorial Crisis",
                headline = "The Interstate Highway Kidnapping Crisis",
                narration = "You are now Governor of Lakos State. A gang of heavily armed bandits has hijacked two luxury passenger buses on the state boundary expressway, holding 74 citizens for ₦500 million ransom.",
                pidginQuote = "Commissioner of Police: 'Your Excellency, Abuja is slow to deploy air support. The families are crying on national television.'",
                options = listOf(
                    EventChoice(
                        id = "s1_a",
                        label = "Quietly pay the ₦500 million ransom from your State Security Vote",
                        description = "Save all 74 lives immediately without bloodshed, but embolden future attacks.",
                        costNara = 500_000_000,
                        energyCost = 20,
                        conscienceDelta = 20,
                        cloutDelta = 25,
                        stressDelta = -10,
                        politicalCapitalDelta = 10,
                        outcomeNarrative = "All 74 hostages walked home to their weeping families! The public rejoiced, though security analysts criticized the precedent."
                    ),
                    EventChoice(
                        id = "s1_b",
                        label = "Form a crack State Vigilante Brigade with modern drone surveillance",
                        description = "Storm the forest hideouts directly with local hunters and tactical elite units.",
                        costNara = 250_000_000,
                        energyCost = 35,
                        ambitionDelta = 30,
                        politicalCapitalDelta = 35,
                        cloutDelta = 40,
                        angerIndexDelta = -15,
                        stressDelta = 20,
                        outcomeNarrative = "The operation was a resounding triumph! 68 hostages were rescued, the bandit warlord was captured alive. You are hailed as the 'Iron Governor'."
                    ),
                    EventChoice(
                        id = "s1_c",
                        label = "Blame the Federal Government and call a stormy press conference in Abuka",
                        description = "Shift all culpability to the opposition president while saving your state treasury.",
                        costNara = 10_000_000,
                        energyCost = 20,
                        ambitionDelta = 25,
                        conscienceDelta = -20,
                        politicalCapitalDelta = 15,
                        godfatherFavorDelta = -15,
                        angerIndexDelta = 10,
                        outcomeNarrative = "Your speech dominated prime-time news headlines, creating political turmoil in the capital, but the victims' families felt abandoned."
                    )
                )
            )
        ),
        LifeStage.NATIONAL_POWER to listOf(
            GameEvent(
                id = "nat_1",
                stage = LifeStage.NATIONAL_POWER,
                category = "Federal Ministerial Hearing",
                headline = "The Fuel Subsidy Removal Ultimatum",
                narration = "As Federal Minister of Finance and Energy, the IMF and World Bank warn that Naijaland will default on sovereign bonds unless you completely remove the ₦3 trillion annual petrol subsidy tomorrow.",
                pidginQuote = "Labour Union Boss leans forward: 'Touch that pump price and we go shut down every airport, refinery, and power grid!'",
                options = listOf(
                    EventChoice(
                        id = "n1_a",
                        label = "Abolish the subsidy immediately with zero palliatives to save the treasury",
                        description = "Take the bitter macroeconomic pill. Nara stabilizes, but transport fares triple overnight.",
                        costNara = 0,
                        energyCost = 30,
                        ambitionDelta = 25,
                        conscienceDelta = -25,
                        cloutDelta = -35,
                        politicalCapitalDelta = 20,
                        angerIndexDelta = 35,
                        stressDelta = 35,
                        outcomeNarrative = "Financial markets cheered, but within 6 hours, angry crowds blocked the third mainland bridge. Bonfires burn in 12 states."
                    ),
                    EventChoice(
                        id = "n1_b",
                        label = "Negotiate a phased reduction with mass electric transit buses & cash transfers",
                        description = "Roll out ₦20,000 monthly cash relief to 5 million poor households while cushioning fares.",
                        costNara = 800_000_000,
                        energyCost = 35,
                        ambitionDelta = 20,
                        conscienceDelta = 30,
                        politicalCapitalDelta = 30,
                        cloutDelta = 25,
                        angerIndexDelta = -10,
                        outcomeNarrative = "The unions called off the nationwide strike after 48 hours of grueling negotiations. You are hailed as a master statesman."
                    )
                )
            )
        ),
        LifeStage.THE_RACE to listOf(
            GameEvent(
                id = "race_1",
                stage = LifeStage.THE_RACE,
                category = "Presidential Primaries",
                headline = "The Midnight Dollar Rain: National Convention",
                narration = "The Eagle Square convention hall in Abuka is buzzing. It is 3:00 AM on presidential primary night. You are trailing your bitter rival, Senator Danjuma, by 240 delegate votes. His agents are distributing $10,000 envelopes in luxury buses outside.",
                pidginQuote = "Campaign Manager Tari whispers: 'Oga, our private jet just landed with the cash boxes from Switzerland. Do we match them or lose the ticket?'",
                options = listOf(
                    EventChoice(
                        id = "r1_a",
                        label = "Open the cash boxes and distribute $15,000 to each swinging delegate",
                        description = "Outbid Danjuma ruthlessly. Secure the presidential ticket at any financial cost.",
                        costNara = 3_500_000_000,
                        energyCost = 30,
                        ambitionDelta = 35,
                        conscienceDelta = -35,
                        politicalCapitalDelta = 40,
                        cloutDelta = 30,
                        stressDelta = 25,
                        outcomeNarrative = "Delegates chanted your praise till dawn! When the ballot boxes were tallied, you clinched the nomination with 1,840 votes."
                    ),
                    EventChoice(
                        id = "r1_b",
                        label = "Deliver an electrifying live nationwide speech on televised reform",
                        description = "Appeal directly to the conscience of the delegates and the millions watching on home televisions.",
                        costNara = 100_000_000,
                        energyCost = 35,
                        ambitionDelta = 20,
                        conscienceDelta = 35,
                        cloutDelta = 50,
                        politicalCapitalDelta = 25,
                        stressDelta = 20,
                        outcomeNarrative = "Your speech brought elderly delegates to tears. A youth revolt inside the hall forced an open roll call. You won by a razor-thin margin!"
                    )
                )
            )
        ),
        LifeStage.THE_VILLA to listOf(
            GameEvent(
                id = "villa_1",
                stage = LifeStage.THE_VILLA,
                category = "The Seat of Power",
                headline = "The Whispers of a Coup Plot in the Barracks",
                narration = "At 1:30 AM in the private residence of the Presidential Villa, the Director of State Security knocks on your bedroom door. Intelligence reports that a cadre of disgruntled Brigadiers is massing armored vehicles in Kaduna.",
                pidginQuote = "National Security Adviser: 'Mr. President, we have 3 hours before dawn. Either we strike preemptively or we prepare the presidential bunker.'",
                options = listOf(
                    EventChoice(
                        id = "v1_a",
                        label = "Order the immediate arrest of the coup plotters and purge the top command",
                        description = "Neutralize the threat decisively. Send loyal Guards Brigade units to Kaduna.",
                        costNara = 200_000_000,
                        energyCost = 35,
                        ambitionDelta = 30,
                        politicalCapitalDelta = 30,
                        stressDelta = 30,
                        healthDelta = -10,
                        outcomeNarrative = "The conspirators were rounded up before their engines could fire. You addressed the nation at 7:00 AM in full commander-in-chief uniform."
                    ),
                    EventChoice(
                        id = "v1_b",
                        label = "Invite the plotters' leaders for a tense breakfast meeting; offer diplomatic ambassador postings",
                        description = "Co-opt your enemies. Turn mutinous generals into well-fed diplomats in London and Washington.",
                        costNara = 500_000_000,
                        energyCost = 30,
                        ambitionDelta = 20,
                        conscienceDelta = 10,
                        godfatherFavorDelta = 25,
                        politicalCapitalDelta = 25,
                        outcomeNarrative = "Over eggs and champagne, the coup melted into high-paying foreign postings. Bloodshed avoided, power consolidated."
                    )
                )
            )
        )
    )

    fun getEventForStage(stage: LifeStage, eventHistory: List<String>): GameEvent {
        val available = stageEvents[stage]?.filter { it.id !in eventHistory }
        return if (!available.isNullOrEmpty()) {
            available.first()
        } else {
            // Generate dynamic repeating stage event
            generateDynamicEvent(stage)
        }
    }

    private fun generateDynamicEvent(stage: LifeStage): GameEvent {
        return when (stage) {
            LifeStage.CAMPUS -> GameEvent(
                id = "dynamic_campus_${System.currentTimeMillis()}",
                stage = LifeStage.CAMPUS,
                category = "Campus Life",
                headline = "The SUG Union Election Showdown",
                narration = "Opposing student factions are contesting control of the campus transport shuttle park. Your support could tip the scales.",
                pidginQuote = "Padi Segun: 'Senior man, if we back Comrade Jerry, we go control all the park tickets!'",
                options = listOf(
                    EventChoice(
                        id = "dyn_c1",
                        label = "Back Comrade Jerry with campaign banners",
                        description = "Fund the rallies and expand your campus network.",
                        costNara = 40_000,
                        cloutDelta = 20,
                        politicalCapitalDelta = 15,
                        outcomeNarrative = "Comrade Jerry won! You now receive VIP protocol at every campus event."
                    ),
                    EventChoice(
                        id = "dyn_c2",
                        label = "Remain neutral and focus on your GPA",
                        description = "Avoid unnecessary campus drama.",
                        costNara = 0,
                        healthDelta = 5,
                        stressDelta = -10,
                        outcomeNarrative = "You stayed clear of the tear gas and maintained your honors standing."
                    )
                )
            )
            LifeStage.HUSTLE_YEARS -> GameEvent(
                id = "dynamic_hustle_${System.currentTimeMillis()}",
                stage = LifeStage.HUSTLE_YEARS,
                category = "Street Hustle & Owambe",
                headline = "The High Society Owambe Spraying Contest",
                narration = "Your rich friend Tari invites you to the 50th birthday party of an oil baron on Victoria Island. Fuji music is blasting and people are spraying bundles of crisp Nara.",
                pidginQuote = "Musician chants your name on mic: 'Chief Senior Man! The youth leader of our time! Oya spray the boys!'",
                options = listOf(
                    EventChoice(
                        id = "dyn_h1",
                        label = "Step onto the dancefloor and spray ₦300,000 cash",
                        description = "Establish yourself as a heavy spender with unlimited street clout.",
                        costNara = 300_000,
                        cloutDelta = 35,
                        ambitionDelta = 15,
                        outcomeNarrative = "Videos of you spraying crisp notes went viral on TikTok! Your street status skyrocketed."
                    ),
                    EventChoice(
                        id = "dyn_h2",
                        label = "Sit quietly at the VIP table and network with ministry directors",
                        description = "Exchange business cards and discuss federal procurement deals.",
                        costNara = 50_000,
                        politicalCapitalDelta = 20,
                        godfatherFavorDelta = 15,
                        outcomeNarrative = "You secured an introduction to the Director of Maritime Infrastructure over vintage wine."
                    )
                )
            )
            LifeStage.LOCAL_POWER -> GameEvent(
                id = "dynamic_local_${System.currentTimeMillis()}",
                stage = LifeStage.LOCAL_POWER,
                category = "Ward Governance",
                headline = "The Chieftaincy Title Conferment",
                narration = "The traditional ruler of your hometown offers to confer upon you the royal title of 'Aare Asiwaju of the Realm'. But the palace secretary requires a contribution to the palace roof.",
                pidginQuote = "Palace Secretary: 'A chief without a title is like a soup without maggi.'",
                options = listOf(
                    EventChoice(
                        id = "dyn_l1",
                        label = "Pay ₦2.5 Million and accept the beaded crown and horse-tail whisk",
                        description = "Solidify your status as a respected grassroot chieftain.",
                        costNara = 2_500_000,
                        cloutDelta = 30,
                        godfatherFavorDelta = 20,
                        politicalCapitalDelta = 20,
                        outcomeNarrative = "Drums beat through the night! You are now addressed as Chief. Your political stature is unassailable."
                    ),
                    EventChoice(
                        id = "dyn_l2",
                        label = "Respectfully decline and build a community solar borehole instead",
                        description = "Direct the funds directly to safe drinking water for ordinary citizens.",
                        costNara = 1_800_000,
                        conscienceDelta = 25,
                        cloutDelta = 25,
                        angerIndexDelta = -5,
                        outcomeNarrative = "The villagers cheered as fresh clean water flowed! Mothers named their newborn babies after you."
                    )
                )
            )
            LifeStage.STATE_POWER -> GameEvent(
                id = "dynamic_state_${System.currentTimeMillis()}",
                stage = LifeStage.STATE_POWER,
                category = "State Assembly Politics",
                headline = "The State Budget Ghost Project Audit",
                narration = "Your Commissioner of Works presents a highway flyover contract with an inflated ₦8 billion markup intended for the ruling party's upcoming re-election war chest.",
                pidginQuote = "Commissioner smiles: 'Your Excellency, this is how all governors fund their second term.'",
                options = listOf(
                    EventChoice(
                        id = "dyn_s1",
                        label = "Approve the markup and funnel ₦3 billion to your campaign foundation",
                        description = "Secure your second term campaign war chest with unassailable liquidity.",
                        costNara = -3_000_000_000,
                        ambitionDelta = 25,
                        conscienceDelta = -30,
                        politicalCapitalDelta = 25,
                        outcomeNarrative = "The campaign chest is impenetrable! Opponents fear your sheer financial firepower."
                    ),
                    EventChoice(
                        id = "dyn_s2",
                        label = "Slash the contract to actual engineering cost and build 20 modern public schools with the savings",
                        description = "Refuse the kickback and deliver real human capital for the next generation.",
                        costNara = 0,
                        conscienceDelta = 35,
                        cloutDelta = 40,
                        politicalCapitalDelta = 20,
                        angerIndexDelta = -10,
                        outcomeNarrative = "Thousands of children sat in brand-new science labs! Civil society groups nominated you for Governor of the Year."
                    )
                )
            )
            LifeStage.NATIONAL_POWER -> GameEvent(
                id = "dynamic_nat_${System.currentTimeMillis()}",
                stage = LifeStage.NATIONAL_POWER,
                category = "National Statecraft",
                headline = "The Central Bank Monetary Crisis",
                narration = "The Nara is under speculative attack from foreign currency traders. The exchange rate threatens to hit ₦2,000 per dollar.",
                pidginQuote = "CBN Governor on hot line: 'We have 24 hours to either pump $1 billion reserves or hike interest rates to 30%!'",
                options = listOf(
                    EventChoice(
                        id = "dyn_n1",
                        label = "Defend the currency with $800M sovereign foreign reserves",
                        description = "Stabilize market prices temporarily for ordinary food buyers.",
                        costNara = 0,
                        politicalCapitalDelta = 25,
                        angerIndexDelta = -15,
                        outcomeNarrative = "The Nara rebounded to ₦1,400! Importers sighed with relief and market panic subsided."
                    ),
                    EventChoice(
                        id = "dyn_n2",
                        label = "Let the market float freely and provide tax holidays for local manufacturing",
                        description = "Tough market medicine to force domestic production over foreign imports.",
                        costNara = 0,
                        ambitionDelta = 20,
                        conscienceDelta = 10,
                        cloutDelta = -10,
                        angerIndexDelta = 10,
                        outcomeNarrative = "Short-term pain, but local factories began humming. Economic historians praise your courage."
                    )
                )
            )
            LifeStage.THE_RACE -> GameEvent(
                id = "dynamic_race_${System.currentTimeMillis()}",
                stage = LifeStage.THE_RACE,
                category = "The Presidential Debate",
                headline = "Live Presidential Debate on National Television",
                narration = "Over 65 million citizens are tuned in. The debate moderator asks you point-blank about your family secret and offshore holdings.",
                pidginQuote = "Moderator: 'Candidate, the nation wants to know: can Naijaland truly trust your mandate?'",
                options = listOf(
                    EventChoice(
                        id = "dyn_r1",
                        label = "Attack your opponent's record with devastating oratorical fire",
                        description = "Flip the spotlight onto your rival's corrupt past and electrify the television audience.",
                        costNara = 0,
                        cloutDelta = 35,
                        politicalCapitalDelta = 30,
                        outcomeNarrative = "Your counter-attack became an instant viral soundbite! Social media crowned you the winner of the debate."
                    ),
                    EventChoice(
                        id = "dyn_r2",
                        label = "Acknowledge your past struggles with humility and outline your visionary 10-year development blueprint",
                        description = "Win undecided middle-class and youth voters through statesmanship and transparency.",
                        costNara = 0,
                        conscienceDelta = 25,
                        cloutDelta = 30,
                        politicalCapitalDelta = 35,
                        outcomeNarrative = "Editorial boards across the nation endorsed your candidacy the next morning. Momentum is unstoppable."
                    )
                )
            )
            LifeStage.THE_VILLA -> GameEvent(
                id = "dynamic_villa_${System.currentTimeMillis()}",
                stage = LifeStage.THE_VILLA,
                category = "The Mandate",
                headline = "The State Banquet for Foreign Heads of State",
                narration = "You are hosting the African Union Summit at the Banquet Hall of the Presidential Villa. A billion-dollar transnational gas pipeline treaty is on the table.",
                pidginQuote = "Foreign Minister: 'Mr. President, signing this cements Naijaland as the undisputed giant of the continent.'",
                options = listOf(
                    EventChoice(
                        id = "dyn_v1",
                        label = "Sign the historic treaty and dedicate the proceeds to a National Sovereign Education Endowment",
                        description = "Guarantee free tertiary education for every Nigerian youth for the next 50 years.",
                        costNara = 0,
                        conscienceDelta = 40,
                        politicalCapitalDelta = 40,
                        cloutDelta = 45,
                        angerIndexDelta = -20,
                        outcomeNarrative = "Thunderous applause echoed across Africa! Your legacy is carved in solid gold."
                    ),
                    EventChoice(
                        id = "dyn_v2",
                        label = "Negotiate hard concessions to retain 60% controlling equity inside Naijaland",
                        description = "Defend national sovereignty against foreign economic dominance.",
                        costNara = 0,
                        ambitionDelta = 35,
                        politicalCapitalDelta = 35,
                        cloutDelta = 30,
                        outcomeNarrative = "You out-negotiated the global consortium. The foreign press christened you 'The Tiger of Abuka'."
                    )
                )
            )
            else -> GameEvent(
                id = "generic_${System.currentTimeMillis()}",
                stage = LifeStage.CAMPUS,
                category = "Life",
                headline = "A Day in Naijaland",
                narration = "Life continues to test your resolve, cunning, and endurance in this bustling republic.",
                options = listOf(
                    EventChoice(
                        id = "gen_1",
                        label = "Push forward with relentless ambition",
                        description = "Hustle never sleeps.",
                        ambitionDelta = 10,
                        outcomeNarrative = "You pressed ahead despite the odds."
                    )
                )
            )
        }
    }
}
