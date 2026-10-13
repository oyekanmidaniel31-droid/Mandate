package com.example.mandate.data

import com.example.mandate.model.GameState
import com.example.mandate.model.LifeStage
import com.example.mandate.model.NewsFlash

object PoliticalNewsCatalog {

    val breakingHeadlines = listOf(
        "🚨 BREAKING: GOVERNORS FORUM EMERGENCY SUMMIT CONVENES IN ABUJA AMID FISCAL REVENUE ROW",
        "⚡ URGENT: CRUDE OIL PIPELINE RESTORATION REACHES 85% AS NAVAL SECURITY OPERATION INTENSIFIES",
        "🗳️ ELECTION WATCH: ELECTORAL COMMISSION COMMENCES DISTRIBUTION OF SENSITIVE VOTING LOGISTICS",
        "🔥 SCANDAL ALERT: NATIONAL ASSEMBLY COMMITTEE GRアシLLS TOP OIL EXECUTIVES OVER SUBSIDY BILLIONS",
        "⚠️ ECONOMIC FLASH: CENTRAL BANK MANDATES NEW BDC ELECTRONIC SURVEILLANCE PLATFORM"
    )

    fun getCuratedNewsFeed(state: GameState): List<NewsFlash> {
        val playerNews = state.newsFlashes
        val stage = state.character.currentStage
        val anger = state.macroEconomy.angerIndex
        val playerName = state.character.name.uppercase()
        val year = state.character.year

        val contextualWireNews = mutableListOf<NewsFlash>()

        // 1. Stage-specific headline
        when (stage) {
            LifeStage.CAMPUS -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_campus_1",
                        source = "CAMPUS CHRONICLE",
                        headline = "STUDENT UNION CONGRESS DEADLOCKED AS $playerName PROPOSES RADICAL BUDGET REFORM",
                        summary = "Student parliament sessions extended late into the night amidst fierce debates over union transport levies and dining hall subsidies.",
                        category = "POLITICS",
                        timeAgo = "18m ago",
                        impactTag = "Campus Clout +12%",
                        isBreaking = false,
                        fullDispatch = "Academic unions and student delegates have turned their attention toward $playerName, whose fiery rhetoric during the budget plenary caught the attention of visiting state youth coordinators. Speculation mounts that state party recruiters are monitoring campus leadership closely.",
                        publicReaction = "\"This campus president has more political fire than three senators combined! Watch his future.\" — SUG Speaker"
                    )
                )
            }
            LifeStage.HUSTLE_YEARS -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_hustle_1",
                        source = "THE NATION WIRE",
                        headline = "POLITICAL CONSULTANCY FIRMS RACE FOR INFLUENCE AS LOCAL GOVERNMENT PRIMARIES NEAR",
                        summary = "Grassroots party structures in Mainland and Island districts see massive cash deployments as kingmakers select ward delegates.",
                        category = "POLITICS",
                        timeAgo = "35m ago",
                        impactTag = "Network Influence +15%",
                        isBreaking = false,
                        fullDispatch = "Party youth leaders and strategic consultants, including $playerName, have emerged as indispensable powerbrokers ahead of the LGA congresses. Cash handshakes and closed-door endorsements dominate the luxury hotel suites along Victoria Island.",
                        publicReaction = "\"If you do not have the backing of the street boys and the party elder, your ticket is dead on arrival.\" — Veteran Chieftain"
                    )
                )
            }
            LifeStage.LOCAL_POWER -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_local_1",
                        source = "METRO DISPATCH",
                        headline = "CHAIRMAN $playerName UNVEILS AGGRESSIVE REVENUE AND DRAINAGE MODERNIZATION PLAN",
                        summary = "Local Government Council allocates major capital expenditures toward market sanitation and motor-park infrastructure.",
                        category = "POLITICS",
                        timeAgo = "1h ago",
                        impactTag = "Local Goodwill +20%",
                        isBreaking = false,
                        fullDispatch = "Traders unions at Balogun and Oshodi have expressed cautious optimism as local council teams deploy heavy earthmoving machinery to clear silted drainages. Opposition councilors, however, demand full disclosure of procurement contractor identities.",
                        publicReaction = "\"If the roads are motorable and the agberos don't harass us twice, Chairman has our votes for life.\" — Iyaloja of Balogun"
                    )
                )
            }
            LifeStage.STATE_POWER -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_state_1",
                        source = "VANGUARD DAILY",
                        headline = "STATE EXECUTIVE COUNCIL APPROVES ₦850 BILLION MEGA-CITY INFRASTRUCTURE BOND",
                        summary = "Administration led by $playerName pledges high-speed light rail expansion and deep seaport logistics corridor.",
                        category = "ECONOMY",
                        timeAgo = "45m ago",
                        impactTag = "State Capital +25%",
                        isBreaking = true,
                        fullDispatch = "International credit rating agencies have reaffirmed the state's sovereign credit rating, citing robust internally generated revenue collections. The bond issuance was heavily oversubscribed within 48 hours of opening at the Nigerian Exchange.",
                        publicReaction = "\"Lagos continues to dictate the commercial heartbeat of the entire sub-region.\" — Financial Times Correspondent"
                    )
                )
            }
            LifeStage.NATIONAL_POWER -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_national_1",
                        source = "CHANNELS TV POLITICS",
                        headline = "SENATE DEADLOCKED AS $playerName INTRODUCES CONTROVERSIAL FISCAL FEDERALISM BILL",
                        summary = "Southern and Northern senatorial caucuses clash over crude oil derivative percentage and mineral resource governance.",
                        category = "POLITICS",
                        timeAgo = "25m ago",
                        impactTag = "Political Weight +30%",
                        isBreaking = true,
                        fullDispatch = "A stormy executive session preceded today's plenary as senators banged tables over the fiscal decentralization formula. Behind closed doors in the Senate President's tea room, $playerName was seen rallying cross-party chieftains to break the deadlock.",
                        publicReaction = "\"The center cannot hold this much revenue while states beg for allocations every month. The bill must pass!\" — South-South Senator"
                    )
                )
            }
            LifeStage.THE_RACE -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_race_1",
                        source = "PRESIDENTIAL WATCH 24",
                        headline = "ELECTORAL CAMPAIGN TRAIL: $playerName DRAWS RECORD CROWD IN MEGA-STADIUM RALLY",
                        summary = "Over 120,000 supporters gather in high-stakes campaign climax with key geopolitical endorsements announced.",
                        category = "POLITICS",
                        timeAgo = "10m ago",
                        impactTag = "Electoral Momentum +35%",
                        isBreaking = true,
                        fullDispatch = "Helicopters and armored convoys brought 18 state governors and traditional rulers together on the campaign podium to formally endorse $playerName. Market women, transport unions, and youth federations paralyzed city traffic for six hours.",
                        publicReaction = "\"The tsunami has started. Anyone standing in the way will be swept clean from the polling booths.\" — Campaign Director-General"
                    )
                )
            }
            LifeStage.THE_VILLA -> {
                contextualWireNews.add(
                    NewsFlash(
                        id = "wire_villa_1",
                        source = "STATE HOUSE CORRESPONDENT",
                        headline = "PRESIDENT $playerName CHAIRS FEDERAL EXECUTIVE COUNCIL; UNVEILS NATIONAL INDUSTRIAL REBIRTH PACT",
                        summary = "Comprehensive executive orders signed to subsidize local manufacturing, power generation, and food self-sufficiency.",
                        category = "POLITICS",
                        timeAgo = "5m ago",
                        impactTag = "Presidential Legacy +40%",
                        isBreaking = true,
                        fullDispatch = "Ambassadors from the G20, ECOWAS leaders, and financial captains packed the Council Chambers as the President declared a state of emergency on national energy generation and agricultural logistics corridors.",
                        publicReaction = "\"This is the bold leadership the Federation has waited four decades to witness.\" — ECOWAS Special Envoy"
                    )
                )
            }
            else -> {}
        }

        // 2. Macro Economy & Anger Headlines
        if (anger >= 65) {
            contextualWireNews.add(
                NewsFlash(
                    id = "wire_crisis_unrest",
                    source = "PUNCH METRO",
                    headline = "🚨 NATIONWIDE TENSION RISES: LABOUR AND YOUTH COALITIONS DEMAND IMMEDIATE PRICE STABILIZATION",
                    summary = "Public frustration over commodity costs boils over; commercial transport unions issue strike ultimatum as security chiefs meet.",
                    category = "CRISIS",
                    timeAgo = "12m ago",
                    impactTag = "Anger Index Alert ⚠️",
                    isBreaking = true,
                    fullDispatch = "Police commands across major geopolitical hubs have placed tactical units on red alert. Civil society organizations have submitted petitions to the National Assembly warning of total economic shutdown if urgent palliative interventions are not implemented.",
                    publicReaction = "\"No light, no fuel, food prices in the sky. Government must act now before the streets react!\" — Youth Coalition Spokesperson"
                )
            )
        } else {
            contextualWireNews.add(
                NewsFlash(
                    id = "wire_econ_calm",
                    source = "BUSINESS DAY WIRE",
                    headline = "CENTRAL BANK REPORTS FOREIGN RESERVES SURGE PAST $38 BILLION ON CRUDE OIL INFLOWS",
                    summary = "Parallel market exchange rate stabilizes as commercial banks expand FX liquidity to manufacturing conglomerates.",
                    category = "ECONOMY",
                    timeAgo = "1h ago",
                    impactTag = "Macro Stability +8%",
                    isBreaking = false,
                    fullDispatch = "The Monetary Policy Committee noted significant improvements in trade balances, commending inter-agency collaboration between the Nigerian National Petroleum Company and naval maritime security task forces.",
                    publicReaction = "\"A stable naira allows long-term planning for factories and port importers alike.\" — Manufacturers Association President"
                )
            )
        }

        // 3. High-Stakes Scandals & Wire Dispatches
        contextualWireNews.add(
            NewsFlash(
                id = "wire_scandal_1",
                source = "PREMIUM INVESTIGATIONS",
                headline = "🔥 LEAKED MEMO REVEALS SECRET KINGMAKER PACT SIGNED IN PRIVATE DUBAI VILLA",
                summary = "Documents detail behind-the-scenes delegate distribution and ministerial portfolio promises ahead of national congress.",
                category = "SCANDAL",
                timeAgo = "2h ago",
                impactTag = "Political Tension +15",
                isBreaking = false,
                fullDispatch = "The leaked memo, allegedly bearing signatures of prominent party godfathers, outlines revenue allocations and key maritime agency appointments. Party spokespersons dismiss the document as forged opposition propaganda.",
                publicReaction = "\"In Naijaland politics, there are no permanent enemies, only permanent interests written on private letterheads.\" — Senior Political Analyst"
            )
        )

        contextualWireNews.add(
            NewsFlash(
                id = "wire_security_1",
                source = "DEFENCE INTELLIGENCE WIRE",
                headline = "JOINT MILITARY TASKFORCE INTERCEPTS ILLEGAL OIL BUNKERING CONVOY IN NIGER DELTA CREEKS",
                summary = "Over 400,000 barrels of stolen crude impounded; clandestine barge terminals neutralized in multinational operation.",
                category = "SECURITY",
                timeAgo = "3h ago",
                impactTag = "Security Level Elevated 🛡️",
                isBreaking = false,
                fullDispatch = "Naval gunboats and surveillance drones tracked the syndicated vessels along the Bonny River estuaries. High-ranking sponsors of the bunkering cartel are currently being profiled by federal security intelligence agencies.",
                publicReaction = "\"Every barrel recovered is revenue saved for national hospitals and federal universities.\" — Naval Spokesman"
            )
        )

        contextualWireNews.add(
            NewsFlash(
                id = "wire_power_grid",
                source = "DAILY TRUST",
                headline = "NATIONAL GRID WHEELING CAPACITY CROSSES 5,400 MEGAWATTS FOLLOWING TURBINE REPAIRS",
                summary = "Transmission Company of Nigeria energizes new 330kV substations across North-Central and South-West distribution belts.",
                category = "ECONOMY",
                timeAgo = "4h ago",
                impactTag = "Grid Reliability +10%",
                isBreaking = false,
                fullDispatch = "Manufacturing zones in Ikeja and Kano have reported fewer generator hours as steady power supply reaches industrial parks. Minister of Power warns distribution companies against illegal consumer overbilling.",
                publicReaction = "\"My generator has rested for two consecutive days. May this miracle continue!\" — Small Business Owner, Alaba"
            )
        )

        // Combine user's real gameplay news flashes first, followed by contextual wire dispatches
        return (playerNews + contextualWireNews).distinctBy { it.id }
    }
}
