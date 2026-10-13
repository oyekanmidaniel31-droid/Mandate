package com.example.mandate.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.mandate.audio.EnvironmentalSoundEngine
import com.example.mandate.audio.FujiAudioEngine
import com.example.mandate.data.DailyMissionData
import com.example.mandate.data.FamilyGenerator
import com.example.mandate.data.GameRepository
import com.example.mandate.data.OpenWorldData
import com.example.mandate.data.OriginAnswer
import com.example.mandate.data.OriginEngineData
import com.example.mandate.model.*
import com.example.mandate.ui.components.HomeNPCEntity
import com.example.mandate.ui.components.OwambeRefreshment
import com.example.mandate.ui.components.OwambeVIPAction
import com.example.mandate.ui.components.OwambeVIPGuest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.hypot

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)
    private val audioEngine = FujiAudioEngine()
    private val environmentalAudio = EnvironmentalSoundEngine()
    val playerResourceViewModel = PlayerResourceViewModel()

    fun computePlayerResources(state: GameState): PlayerResources {
        val char = state.character
        val wallet = state.openWorldState.currencyWallet
        val family = state.familyMembers
        val blackTax = state.blackTaxQueue

        val avgFamilyLoyalty = if (family.isNotEmpty()) {
            family.map { it.loyalty }.average().toInt()
        } else 65

        val eldersApproval = if (family.isNotEmpty()) {
            val elders = family.filter {
                it.role == KinshipRole.VILLAGE_ELDER ||
                it.role == KinshipRole.FATHER ||
                it.role == KinshipRole.MOTHER
            }
            if (elders.isNotEmpty()) elders.map { it.loyalty }.average().toInt() else avgFamilyLoyalty
        } else 60

        val totalPendingTax = blackTax.sumOf { it.amountRequested }
        val taxBurden = ((totalPendingTax.toDouble() / (char.naraBalance.coerceAtLeast(10_000L))) * 100).toInt().coerceIn(0, 100)

        val prev = state.playerResources
        return prev.copy(
            influence = prev.influence.copy(
                politicalCapital = char.politicalCapital,
                streetClout = char.streetClout,
                godfatherFavor = char.godfatherFavor,
                mediaSpinControl = (char.cunning * 0.6 + char.streetClout * 0.4).toInt().coerceIn(0, 100)
            ),
            wealth = prev.wealth.copy(
                cashNaira = char.naraBalance,
                bankNaira = wallet.bankNaira,
                domUsd = wallet.domUsd,
                bdcRate = wallet.bdcExchangeRate,
                dailyBurnNaira = wallet.dailyLivingCostTotal
            ),
            familyLoyalty = prev.familyLoyalty.copy(
                clanLoyaltyScore = avgFamilyLoyalty,
                eldersApprovalScore = eldersApproval,
                blackTaxBurdenIndex = taxBurden,
                activeDependentsCount = family.size,
                pendingBlackTaxAmount = totalPendingTax
            )
        )
    }

    private val _uiState = MutableStateFlow(
        GameState(
            isOriginEngineActive = true,
            originQuizIndex = 0,
            openWorldState = OpenWorldState(
                dailyMissions = DailyMissionData.generateInitialDailyMissions()
            )
        )
    )
    val uiState: StateFlow<GameState> = _uiState.asStateFlow()

    init {
        val initialWorld = _uiState.value.openWorldState
        val initialProfile = environmentalAudio.evaluateSoundscape(
            timeOfDay = initialWorld.timeOfDay,
            district = initialWorld.currentDistrict,
            state = initialWorld.currentState,
            weather = initialWorld.weather
        )
        environmentalAudio.updateEnvironment(
            timeOfDay = initialWorld.timeOfDay,
            district = initialWorld.currentDistrict,
            state = initialWorld.currentState,
            weather = initialWorld.weather
        )
        _uiState.update {
            val withAudio = it.copy(
                openWorldState = it.openWorldState.copy(
                    currentAmbientSoundTitle = initialProfile.title,
                    currentAmbientSoundEmoji = initialProfile.iconEmoji
                )
            )
            withAudio.copy(playerResources = computePlayerResources(withAudio))
        }
        playerResourceViewModel.attachAudioEngine(audioEngine)
        playerResourceViewModel.syncFromGameState(_uiState.value)
    }

    fun onOriginAnswerSelected(answer: OriginAnswer) {
        val currentScores = _uiState.value.originScores.toMutableMap()
        answer.traitEffects.forEach { (trait, delta) ->
            val prev = currentScores[trait] ?: 50
            currentScores[trait] = (prev + delta).coerceIn(0, 100)
        }

        val nextIndex = _uiState.value.originQuizIndex + 1
        if (nextIndex < OriginEngineData.questions.size) {
            _uiState.update {
                it.copy(
                    originScores = currentScores,
                    originQuizIndex = nextIndex
                )
            }
        } else {
            // Completed origin questions, generate character and present Dossier!
            val initialState = repository.initializeFromOriginScores(currentScores)
            val readyState = initialState.copy(
                isOriginEngineActive = true,
                isOriginDossierReady = true,
                originScores = currentScores
            )
            _uiState.value = readyState.copy(playerResources = computePlayerResources(readyState))
            playerResourceViewModel.syncFromGameState(_uiState.value)
        }
    }

    fun onAcceptOriginDossier() {
        _uiState.update {
            val updated = it.copy(
                isOriginEngineActive = false,
                isOriginDossierReady = false
            )
            updated.copy(playerResources = computePlayerResources(updated))
        }
        playerResourceViewModel.syncFromGameState(_uiState.value)
    }

    fun onResetOriginEngine() {
        _uiState.value = GameState(
            isOriginEngineActive = true,
            isOriginDossierReady = false,
            originQuizIndex = 0,
            originScores = mapOf(
                "ambition" to 50,
                "empathy" to 50,
                "risk" to 50,
                "loyalty" to 50,
                "cunning" to 50
            )
        )
    }

    fun onChoiceSelected(choice: EventChoice) {
        val current = _uiState.value
        val updated = repository.applyChoice(current, choice)
        _uiState.value = updated

        // Immersive audio feedback based on choice outcomes & risk
        val netDelta = (choice.cloutDelta + choice.politicalCapitalDelta + choice.godfatherFavorDelta + choice.healthDelta) - (choice.stressDelta / 2)
        val isNegativeOutcome = choice.isFatalRisk || choice.healthDelta < 0 || choice.stressDelta > 15 || netDelta < -5
        val isPositiveOutcome = !choice.isFatalRisk && (netDelta > 5 || choice.cloutDelta > 0 || choice.politicalCapitalDelta > 0)

        if (isNegativeOutcome) {
            audioEngine.playNegativeEventSound()
        } else if (isPositiveOutcome) {
            audioEngine.playPositiveEventSound()
        } else if (choice.costNara > 0) {
            audioEngine.playResourceLossSound()
        } else {
            audioEngine.playTalkingDrum(1.0f)
        }
    }

    fun onAdvanceTurn() {
        val current = _uiState.value
        val updated = repository.advanceLifeTurn(current)
        _uiState.value = updated
        audioEngine.playTalkingDrum(1.1f)
    }

    fun onPayBlackTax(request: BlackTaxRequest) {
        val current = _uiState.value
        if (current.character.naraBalance >= request.amountRequested) {
            audioEngine.playBlessingChime()
            val updatedChar = current.character.copy(
                naraBalance = current.character.naraBalance - request.amountRequested,
                streetClout = (current.character.streetClout + 10).coerceAtLeast(0),
                conscience = (current.character.conscience + 5).coerceIn(0, 100)
            )
            val updatedQueue = current.blackTaxQueue.filter { it.id != request.id }
            val newTx = BankTransaction(
                id = "tax_tx_${System.currentTimeMillis()}",
                title = "Black Tax: ${request.fromMemberName}",
                amountNara = request.amountRequested,
                isCredit = false,
                date = "Year ${updatedChar.year}",
                reference = "NIP-FAM-${request.id}"
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    blackTaxQueue = updatedQueue,
                    bankTransactions = listOf(newTx) + it.bankTransactions,
                    lastOutcomeMessage = "You paid ₦${request.amountRequested} to ${request.fromMemberName}. Prayers and family blessings received!"
                )
            }
        } else {
            audioEngine.playNegativeEventSound()
            _uiState.update {
                it.copy(lastOutcomeMessage = "Insufficient Nara funds in your account to fulfill this Black Tax plea.")
            }
        }
    }

    fun onIgnoreBlackTax(request: BlackTaxRequest) {
        audioEngine.playNegativeEventSound()
        val current = _uiState.value
        val updatedQueue = current.blackTaxQueue.filter { it.id != request.id }
        val updatedChar = current.character.copy(
            streetClout = (current.character.streetClout - 5).coerceAtLeast(0),
            stress = (current.character.stress + 10).coerceIn(0, 100)
        )
        _uiState.update {
            it.copy(
                character = updatedChar,
                blackTaxQueue = updatedQueue,
                lastOutcomeMessage = "Ignored Black Tax request. ${request.consequencesIfIgnored}"
            )
        }
    }

    fun onPadiInteract(padi: Padi, action: String) {
        val current = _uiState.value
        when (action) {
            "hang_out" -> {
                val updatedPadis = current.padis.map {
                    if (it.id == padi.id) it.copy(loyalty = (it.loyalty + 10).coerceIn(0, 100))
                    else it
                }
                val updatedChar = current.character.copy(
                    stress = (current.character.stress - 15).coerceAtLeast(0),
                    naraBalance = (current.character.naraBalance - 35_000).coerceAtLeast(0)
                )
                _uiState.update {
                    it.copy(
                        padis = updatedPadis,
                        character = updatedChar,
                        lastOutcomeMessage = "Chilled at a VIP rooftop in Victoria Island with ${padi.name}. Shared cold drinks and fresh political intelligence."
                    )
                }
            }
            "campaign_backing" -> {
                if (padi.loyalty >= 50) {
                    val polGain = if (padi.archetype == PadiArchetype.RICH_PADI) 30 else 15
                    val cloutGain = if (padi.archetype == PadiArchetype.RIDE_OR_DIE) 25 else 15
                    val updatedChar = current.character.copy(
                        politicalCapital = (current.character.politicalCapital + polGain).coerceIn(0, 100),
                        streetClout = (current.character.streetClout + cloutGain).coerceIn(0, 100)
                    )
                    _uiState.update {
                        it.copy(
                            character = updatedChar,
                            lastOutcomeMessage = "${padi.name} mobilized their entire network for your political campaign! Clout & Capital increased."
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(lastOutcomeMessage = "${padi.name} smiled coldly and gave excuses. Their loyalty (${padi.loyalty}%) is too low.")
                    }
                }
            }
        }
    }

    fun onCrisisIntervention(action: String) {
        val current = _uiState.value
        val macro = current.macroEconomy
        val char = current.character

        when (action) {
            "negotiate" -> {
                audioEngine.playPositiveEventSound()
                val updatedAnger = (macro.angerIndex - 25).coerceAtLeast(10)
                val updatedChar = char.copy(
                    conscience = (char.conscience + 15).coerceIn(0, 100),
                    streetClout = (char.streetClout + 20).coerceIn(0, 100),
                    naraBalance = (char.naraBalance - 50_000_000).coerceAtLeast(0)
                )
                _uiState.update {
                    it.copy(
                        character = updatedChar,
                        macroEconomy = macro.copy(
                            angerIndex = updatedAnger,
                            securityLevel = SecurityLevel.PEACEFUL
                        ),
                        lastOutcomeMessage = "You hosted dialogue with youth comrades and student union leaders. The protests suspended for 60 days."
                    )
                }
            }
            "crackdown" -> {
                audioEngine.playNegativeEventSound()
                val updatedAnger = (macro.angerIndex + 15).coerceIn(0, 100)
                val updatedChar = char.copy(
                    conscience = (char.conscience - 25).coerceIn(0, 100),
                    politicalCapital = (char.politicalCapital + 10).coerceIn(0, 100),
                    stress = (char.stress + 20).coerceIn(0, 100)
                )
                _uiState.update {
                    it.copy(
                        character = updatedChar,
                        macroEconomy = macro.copy(
                            angerIndex = updatedAnger,
                            securityLevel = SecurityLevel.CURFEW
                        ),
                        lastOutcomeMessage = "Imposed 24-hour military curfew. Roads cleared with tear gas, but subterranean rage burns hotter."
                    )
                }
            }
            "food_relief" -> {
                val cost = 20_000_000L
                if (char.naraBalance >= cost) {
                    audioEngine.playPositiveEventSound()
                    val updatedChar = char.copy(
                        naraBalance = char.naraBalance - cost,
                        conscience = (char.conscience + 20).coerceIn(0, 100),
                        streetClout = (char.streetClout + 30).coerceIn(0, 100)
                    )
                    _uiState.update {
                        it.copy(
                            character = updatedChar,
                            macroEconomy = macro.copy(angerIndex = (macro.angerIndex - 15).coerceAtLeast(10)),
                            lastOutcomeMessage = "Distributed 5,000 bags of rice and subsidized garri in grassroots wards. People cheered your name in the streets!"
                        )
                    }
                } else {
                    audioEngine.playNegativeEventSound()
                    _uiState.update { it.copy(lastOutcomeMessage = "Not enough funds to finance food relief.") }
                }
            }
        }
    }

    fun onQuickAction(actionType: String) {
        val current = _uiState.value
        val char = current.character
        when (actionType) {
            "street_rally" -> {
                val cost = 50_000L
                if (char.naraBalance >= cost) {
                    audioEngine.playPositiveEventSound()
                    val updatedChar = char.copy(
                        naraBalance = char.naraBalance - cost,
                        streetClout = (char.streetClout + 25).coerceIn(0, 100),
                        energy = (char.energy - 15).coerceAtLeast(0),
                        stress = (char.stress - 5).coerceAtLeast(0)
                    )
                    val newTx = BankTransaction(
                        id = "tx_${System.currentTimeMillis()}",
                        title = "Street Youth Rally Logistics",
                        amountNara = cost,
                        isCredit = false,
                        date = "Year ${char.year}",
                        reference = "OPS-RALLY"
                    )
                    _uiState.update {
                        it.copy(
                            character = updatedChar,
                            bankTransactions = listOf(newTx) + it.bankTransactions,
                            lastOutcomeMessage = "💥 Massive Street Rally in Oshodi! Over 5,000 youths marched with your banners! Street Clout +25%!"
                        )
                    }
                } else {
                    audioEngine.playNegativeEventSound()
                    _uiState.update { it.copy(lastOutcomeMessage = "Need ₦50,000 for rally trucks and sound systems.") }
                }
            }
            "godfather_meeting" -> {
                val cost = 120_000L
                if (char.naraBalance >= cost) {
                    audioEngine.playPositiveEventSound()
                    val updatedChar = char.copy(
                        naraBalance = char.naraBalance - cost,
                        politicalCapital = (char.politicalCapital + 25).coerceIn(0, 100),
                        godfatherFavor = (char.godfatherFavor + 30).coerceIn(0, 100),
                        conscience = (char.conscience - 10).coerceIn(0, 100),
                        energy = (char.energy - 15).coerceAtLeast(0)
                    )
                    val newTx = BankTransaction(
                        id = "tx_${System.currentTimeMillis()}",
                        title = "Godfather Private Dinner Tribute",
                        amountNara = cost,
                        isCredit = false,
                        date = "Year ${char.year}",
                        reference = "VIP-CHIEF"
                    )
                    _uiState.update {
                        it.copy(
                            character = updatedChar,
                            bankTransactions = listOf(newTx) + it.bankTransactions,
                            lastOutcomeMessage = "🏛️ Midnight Boardroom Meeting with Apex Godfathers! Sealed back-channel endorsements. Political Capital +25%, Favor +30%!"
                        )
                    }
                } else {
                    audioEngine.playNegativeEventSound()
                    _uiState.update { it.copy(lastOutcomeMessage = "Need ₦120,000 for VIP dinner tribute and gifts.") }
                }
            }
            "open_world" -> {
                // Switch directly into the free-roaming 3D Lagos Open World experience!
                _uiState.update { it.copy(activeNavTab = "WORLD") }
            }
            "owambe_spraying" -> {
                // Switch directly into the high-fidelity 3D Owambe Party experience!
                _uiState.update { it.copy(activeNavTab = "OWAMBE") }
            }
            "security_convoy" -> {
                val cost = 300_000L
                if (char.naraBalance >= cost) {
                    val updatedChar = char.copy(
                        naraBalance = char.naraBalance - cost,
                        bodyguardLevel = (char.bodyguardLevel + 1).coerceAtMost(5),
                        stress = (char.stress - 10).coerceAtLeast(0)
                    )
                    _uiState.update {
                        it.copy(
                            character = updatedChar,
                            lastOutcomeMessage = "🛡️ Hired Elite Tactical Police Escorts with Hilux Sirens! Security level upgraded to Level ${updatedChar.bodyguardLevel}! Highway banditry and mob assault risks slashed."
                        )
                    }
                } else {
                    _uiState.update { it.copy(lastOutcomeMessage = "Need ₦300,000 for bulletproof escort contract.") }
                }
            }
            "medical_rejuvenation" -> {
                val cost = 450_000L
                if (char.naraBalance >= cost) {
                    val updatedChar = char.copy(
                        naraBalance = char.naraBalance - cost,
                        health = (char.health + 30).coerceIn(0, 100),
                        stress = (char.stress - 25).coerceAtLeast(0)
                    )
                    _uiState.update {
                        it.copy(
                            character = updatedChar,
                            lastOutcomeMessage = "🏥 Executive Medical Flight to Private Specialist Clinic! Restored cardiac and blood pressure vitals! Health +30%, Stress -25%!"
                        )
                    }
                } else {
                    _uiState.update { it.copy(lastOutcomeMessage = "Need ₦450,000 for VIP hospital flight deposit.") }
                }
            }
        }
    }

    fun onSprayOwambeCash(amount: Long, isCombo: Boolean) {
        val current = _uiState.value
        val char = current.character
        if (char.naraBalance >= amount) {
            val multiplier = if (isCombo) 2 else 1
            val cloutGain = (when {
                amount >= 150_000 -> 35
                amount >= 10_000 -> 15
                else -> 2
            }) * multiplier

            val influenceGain = (when {
                amount >= 150_000 -> 25
                amount >= 10_000 -> 10
                else -> 1
            }) * multiplier

            val stressRelief = when {
                amount >= 150_000 -> 20
                amount >= 10_000 -> 8
                else -> 2
            }

            val updatedChar = char.copy(
                naraBalance = char.naraBalance - amount,
                streetClout = (char.streetClout + cloutGain).coerceIn(0, 100),
                politicalCapital = (char.politicalCapital + influenceGain).coerceIn(0, 100),
                stress = (char.stress - stressRelief).coerceAtLeast(0)
            )

            val updatedTotal = current.totalOwambeSprayed + amount
            val newTransactions = if (amount >= 10_000) {
                listOf(
                    BankTransaction(
                        id = "tx_spray_${System.currentTimeMillis()}",
                        title = if (amount >= 150_000) "Owambe VIP FX Cash Flex" else "Owambe ₦1,000 Note Bundles",
                        amountNara = amount,
                        isCredit = false,
                        date = "Year ${char.year}",
                        reference = "NIP-OWAMBE"
                    )
                ) + current.bankTransactions
            } else {
                current.bankTransactions
            }

            _uiState.update {
                it.copy(
                    character = updatedChar,
                    totalOwambeSprayed = updatedTotal,
                    bankTransactions = newTransactions,
                    lastOutcomeMessage = "💸 Sprayed ₦$amount at Owambe! Street Clout: +$cloutGain%, Influence: +$influenceGain%!"
                )
            }
        }
    }

    fun onOrderOwambeRefreshment(item: OwambeRefreshment) {
        val current = _uiState.value
        val char = current.character
        if (char.naraBalance >= item.costNara) {
            val updatedChar = char.copy(
                naraBalance = char.naraBalance - item.costNara,
                energy = (char.energy + item.energyGain).coerceIn(0, 100),
                stress = (char.stress - item.stressLoss).coerceAtLeast(0),
                streetClout = (char.streetClout + item.cloutGain).coerceIn(0, 100)
            )
            val tx = BankTransaction(
                id = "tx_bar_${System.currentTimeMillis()}",
                title = "Owambe Bar: ${item.title}",
                amountNara = item.costNara,
                isCredit = false,
                date = "Year ${char.year}",
                reference = "BAR-VIP"
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    bankTransactions = listOf(tx) + it.bankTransactions,
                    lastOutcomeMessage = "🍾 Enjoyed ${item.title}! Energy +${item.energyGain}, Stress -${item.stressLoss}!"
                )
            }
        }
    }

    fun onInteractOwambeVIP(guest: OwambeVIPGuest, action: OwambeVIPAction) {
        val current = _uiState.value
        val char = current.character
        if (char.naraBalance >= action.costNara) {
            val favorBonus = if (guest.id == "godfather") 20 else 0
            val updatedChar = char.copy(
                naraBalance = char.naraBalance - action.costNara,
                streetClout = (char.streetClout + action.cloutReward).coerceIn(0, 100),
                politicalCapital = (char.politicalCapital + action.influenceReward).coerceIn(0, 100),
                godfatherFavor = (char.godfatherFavor + favorBonus).coerceIn(0, 100)
            )
            val tx = if (action.costNara > 0) {
                listOf(
                    BankTransaction(
                        id = "tx_vip_${System.currentTimeMillis()}",
                        title = "Owambe VIP: ${guest.name}",
                        amountNara = action.costNara,
                        isCredit = false,
                        date = "Year ${char.year}",
                        reference = "VIP-HOMAGE"
                    )
                ) + current.bankTransactions
            } else current.bankTransactions

            _uiState.update {
                it.copy(
                    character = updatedChar,
                    bankTransactions = tx,
                    lastOutcomeMessage = "👑 ${guest.name}: ${action.effectSummary} (+${action.cloutReward} Clout, +${action.influenceReward} Influence)"
                )
            }
        }
    }

    fun onHomeProstrationBlessing(npc: HomeNPCEntity) {
        val current = _uiState.value
        val char = current.character

        if (npc.isFamilyMember && npc.familyMember != null) {
            val updatedMembers = current.familyMembers.map {
                if (it.id == npc.familyMember.id) {
                    val log = "Received humble prostration & morning blessings in the compound"
                    it.copy(
                        affection = (it.affection + 15).coerceIn(0, 100),
                        happiness = (it.happiness + 18).coerceIn(0, 100),
                        loyalty = (it.loyalty + 10).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
            val updatedChar = char.copy(
                conscience = (char.conscience + 10).coerceIn(0, 100),
                energy = (char.energy + 15).coerceIn(0, 100),
                stress = (char.stress - 15).coerceAtLeast(0)
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    familyMembers = updatedMembers,
                    lastOutcomeMessage = "👑 Dobale & Ikunle! You prostrated with cultural respect before ${npc.name}. They placed ancestral hands on your head with prayers: 'Your political seat shall never be taken by rivals!'"
                )
            }
        } else if (npc.padi != null) {
            val updatedPadis = current.padis.map {
                if (it.id == npc.padi.id) {
                    val log = "Lagos street snap & brotherhood greeting in the living room"
                    it.copy(
                        loyalty = (it.loyalty + 12).coerceIn(0, 100),
                        happiness = (it.happiness + 10).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
            val updatedChar = char.copy(
                streetClout = (char.streetClout + 8).coerceIn(0, 100)
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    padis = updatedPadis,
                    lastOutcomeMessage = "🤜 Senior Man Snap! Hit a crisp Lagos handshake and finger snap with ${npc.name} in the parlor. Syndicate bonds renewed!"
                )
            }
        }
    }

    fun onHomeRemitCash(npc: HomeNPCEntity, amount: Long) {
        val current = _uiState.value
        val char = current.character

        if (char.naraBalance >= amount) {
            val updatedMembers = if (npc.isFamilyMember && npc.familyMember != null) {
                current.familyMembers.map {
                    if (it.id == npc.familyMember.id) {
                        val log = "Handed ₦${amount} cash remittance in person"
                        it.copy(
                            affection = (it.affection + 25).coerceIn(0, 100),
                            happiness = (it.happiness + 25).coerceIn(0, 100),
                            loyalty = (it.loyalty + 15).coerceIn(0, 100),
                            recentInteractions = listOf(log) + it.recentInteractions
                        )
                    } else it
                }
            } else current.familyMembers

            val updatedQueue = if (npc.isFamilyMember) {
                current.blackTaxQueue.filter { !it.fromMemberName.contains(npc.name, ignoreCase = true) }
            } else current.blackTaxQueue

            val updatedChar = char.copy(
                naraBalance = char.naraBalance - amount,
                stress = (char.stress - 10).coerceAtLeast(0),
                streetClout = (char.streetClout + 10).coerceIn(0, 100)
            )

            val tx = listOf(
                BankTransaction(
                    id = "tx_home_${System.currentTimeMillis()}",
                    title = "Home Remittance: ${npc.name}",
                    amountNara = amount,
                    isCredit = false,
                    date = "Year ${char.year}",
                    reference = "HOME-REMIT"
                )
            ) + current.bankTransactions

            _uiState.update {
                it.copy(
                    character = updatedChar,
                    familyMembers = updatedMembers,
                    blackTaxQueue = updatedQueue,
                    bankTransactions = tx,
                    lastOutcomeMessage = "💸 Remitted ₦${amount} directly to ${npc.name} in the living space. The entire household burst into joyful praise!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Not enough Naira balance to remit ₦${amount}.") }
        }
    }

    fun onHomeShareMealOrDrink(npc: HomeNPCEntity) {
        val current = _uiState.value
        val char = current.character
        val cost = 5_000L

        if (char.naraBalance >= cost) {
            val updatedMembers = if (npc.isFamilyMember && npc.familyMember != null) {
                current.familyMembers.map {
                    if (it.id == npc.familyMember.id) {
                        val log = "Shared hot pounded yam & palm wine feast in the parlor"
                        it.copy(
                            affection = (it.affection + 10).coerceIn(0, 100),
                            happiness = (it.happiness + 15).coerceIn(0, 100),
                            recentInteractions = listOf(log) + it.recentInteractions
                        )
                    } else it
                }
            } else current.familyMembers

            val updatedPadis = if (npc.padi != null) {
                current.padis.map {
                    if (it.id == npc.padi.id) {
                        val log = "Chilled with cold malt & roasted suya in the parlor"
                        it.copy(
                            loyalty = (it.loyalty + 12).coerceIn(0, 100),
                            happiness = (it.happiness + 15).coerceIn(0, 100),
                            recentInteractions = listOf(log) + it.recentInteractions
                        )
                    } else it
                }
            } else current.padis

            val updatedChar = char.copy(
                naraBalance = char.naraBalance - cost,
                stress = (char.stress - 20).coerceAtLeast(0),
                health = (char.health + 8).coerceIn(0, 100)
            )

            _uiState.update {
                it.copy(
                    character = updatedChar,
                    familyMembers = updatedMembers,
                    padis = updatedPadis,
                    lastOutcomeMessage = "🍲 Shared a hot plate of pounded yam and chilled drinks with ${npc.name}. Deep home gist and relaxation enjoyed!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Not enough cash for home refreshments.") }
        }
    }

    fun onHomeSeekCounselOrMobilize(npc: HomeNPCEntity) {
        val current = _uiState.value
        val char = current.character

        if (npc.isFamilyMember) {
            val updatedChar = char.copy(
                politicalCapital = (char.politicalCapital + 15).coerceIn(0, 100),
                godfatherFavor = (char.godfatherFavor + 8).coerceIn(0, 100)
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    lastOutcomeMessage = "📜 Elder Endorsement! ${npc.name} shared historical political secrets and aligned clan ward delegates with your mandate."
                )
            }
        } else if (npc.padi != null) {
            val updatedChar = char.copy(
                streetClout = (char.streetClout + 20).coerceIn(0, 100),
                politicalCapital = (char.politicalCapital + 10).coerceIn(0, 100)
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    lastOutcomeMessage = "📢 Street Mobilization! ${npc.name} sent out town criers and mobilized grassroots youth cells in support of your campaign!"
                )
            }
        }
    }

    fun onHomeResolveRivalry(npc: HomeNPCEntity) {
        val current = _uiState.value
        val char = current.character

        val updatedMembers = if (npc.familyMember != null) {
            current.familyMembers.map {
                if (it.id == npc.familyMember.id) {
                    val log = "Resolved domestic inheritance friction & declared diplomatic peace"
                    it.copy(
                        affection = (it.affection + 18).coerceIn(0, 100),
                        happiness = (it.happiness + 22).coerceIn(0, 100),
                        loyalty = (it.loyalty + 15).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.familyMembers

        val updatedPadis = if (npc.padi != null) {
            current.padis.map {
                if (it.id == npc.padi.id) {
                    val log = "Private truce; shook hands and agreed on mutual ward territories"
                    it.copy(
                        happiness = (it.happiness + 20).coerceIn(0, 100),
                        loyalty = (it.loyalty + 15).coerceIn(0, 100),
                        isEstranged = false,
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.padis

        val updatedChar = char.copy(
            stress = (char.stress - 15).coerceAtLeast(0),
            cunning = (char.cunning + 5).coerceIn(0, 100)
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                familyMembers = updatedMembers,
                padis = updatedPadis,
                lastOutcomeMessage = "🤝 Diplomatic Truce! You sat down privately with ${npc.name} to neutralize household friction. Domestic harmony restored."
            )
        }
    }

    fun onSendRelationshipGift(personId: String, isFamily: Boolean, amount: Long) {
        val current = _uiState.value
        val char = current.character

        if (char.naraBalance >= amount) {
            val updatedMembers = if (isFamily) {
                current.familyMembers.map {
                    if (it.id == personId) {
                        val log = "Received ₦$amount cash gift & direct household transfer"
                        it.copy(
                            happiness = (it.happiness + 16).coerceIn(0, 100),
                            loyalty = (it.loyalty + 12).coerceIn(0, 100),
                            affection = (it.affection + 16).coerceIn(0, 100),
                            recentInteractions = listOf(log) + it.recentInteractions
                        )
                    } else it
                }
            } else current.familyMembers

            val updatedPadis = if (!isFamily) {
                current.padis.map {
                    if (it.id == personId) {
                        val log = "Received ₦$amount syndicate gift & VIP lounge tab sponsorship"
                        it.copy(
                            happiness = (it.happiness + 16).coerceIn(0, 100),
                            loyalty = (it.loyalty + 12).coerceIn(0, 100),
                            recentInteractions = listOf(log) + it.recentInteractions
                        )
                    } else it
                }
            } else current.padis

            val targetName = if (isFamily) current.familyMembers.find { it.id == personId }?.name ?: "Family"
            else current.padis.find { it.id == personId }?.nickname ?: "Padi"

            val updatedChar = char.copy(
                naraBalance = char.naraBalance - amount,
                stress = (char.stress - 5).coerceAtLeast(0),
                streetClout = (char.streetClout + 8).coerceIn(0, 100)
            )

            val tx = listOf(
                BankTransaction(
                    id = "tx_rel_${System.currentTimeMillis()}",
                    title = "Relationship Remittance: $targetName",
                    amountNara = amount,
                    isCredit = false,
                    date = "Year ${char.year}",
                    reference = "REL-GIFT"
                )
            ) + current.bankTransactions

            _uiState.update {
                it.copy(
                    character = updatedChar,
                    familyMembers = updatedMembers,
                    padis = updatedPadis,
                    bankTransactions = tx,
                    lastOutcomeMessage = "🎁 Sent ₦$amount relationship remittance to $targetName. Happiness and loyalty surged!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Insufficient funds to send ₦$amount relationship gift.") }
        }
    }

    fun onSpendRelationshipTime(personId: String, isFamily: Boolean) {
        val current = _uiState.value
        val char = current.character

        val updatedMembers = if (isFamily) {
            current.familyMembers.map {
                if (it.id == personId) {
                    val log = "Shared evening tea & domestic gist in the compound"
                    it.copy(
                        happiness = (it.happiness + 18).coerceIn(0, 100),
                        affection = (it.affection + 15).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.familyMembers

        val updatedPadis = if (!isFamily) {
            current.padis.map {
                if (it.id == personId) {
                    val log = "Deep gist over roasted suya and drinks"
                    it.copy(
                        happiness = (it.happiness + 18).coerceIn(0, 100),
                        loyalty = (it.loyalty + 8).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.padis

        val targetName = if (isFamily) current.familyMembers.find { it.id == personId }?.name ?: "Family"
        else current.padis.find { it.id == personId }?.nickname ?: "Padi"

        val updatedChar = char.copy(
            stress = (char.stress - 15).coerceAtLeast(0),
            energy = (char.energy - 5).coerceAtLeast(10)
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                familyMembers = updatedMembers,
                padis = updatedPadis,
                lastOutcomeMessage = "🍲 Quality Time! Spent meaningful hours sharing deep life gist with $targetName. Household bonds flourished."
            )
        }
    }

    fun onPledgeLoyaltyPact(personId: String, isFamily: Boolean) {
        val current = _uiState.value
        val char = current.character

        val updatedMembers = if (isFamily) {
            current.familyMembers.map {
                if (it.id == personId) {
                    val log = "Sealed clan solidarity pact with ancestral prayers"
                    it.copy(
                        loyalty = (it.loyalty + 16).coerceIn(0, 100),
                        happiness = (it.happiness + 8).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.familyMembers

        val updatedPadis = if (!isFamily) {
            current.padis.map {
                if (it.id == personId) {
                    val log = "Exchanged blood brotherhood & political oath"
                    it.copy(
                        loyalty = (it.loyalty + 18).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.padis

        val targetName = if (isFamily) current.familyMembers.find { it.id == personId }?.name ?: "Family"
        else current.padis.find { it.id == personId }?.nickname ?: "Padi"

        val updatedChar = char.copy(
            politicalCapital = (char.politicalCapital + 10).coerceIn(0, 100),
            streetClout = (char.streetClout + 12).coerceIn(0, 100)
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                familyMembers = updatedMembers,
                padis = updatedPadis,
                lastOutcomeMessage = "🛡️ Loyalty Pact Sealed! Swore mutual defense and campaign backing with $targetName."
            )
        }
    }

    fun onSettleRelationshipWahala(personId: String, isFamily: Boolean) {
        val current = _uiState.value
        val char = current.character

        val updatedMembers = if (isFamily) {
            current.familyMembers.map {
                if (it.id == personId) {
                    val log = "Resolved domestic inheritance & favoritism dispute"
                    it.copy(
                        happiness = (it.happiness + 25).coerceIn(0, 100),
                        loyalty = (it.loyalty + 20).coerceIn(0, 100),
                        affection = (it.affection + 22).coerceIn(0, 100),
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.familyMembers

        val updatedPadis = if (!isFamily) {
            current.padis.map {
                if (it.id == personId) {
                    val log = "Peace summit at Victoria Island lounge; apologized for past snubs"
                    it.copy(
                        happiness = (it.happiness + 25).coerceIn(0, 100),
                        loyalty = (it.loyalty + 20).coerceIn(0, 100),
                        isEstranged = false,
                        recentInteractions = listOf(log) + it.recentInteractions
                    )
                } else it
            }
        } else current.padis

        val targetName = if (isFamily) current.familyMembers.find { it.id == personId }?.name ?: "Family"
        else current.padis.find { it.id == personId }?.nickname ?: "Padi"

        val updatedChar = char.copy(
            stress = (char.stress - 20).coerceAtLeast(0),
            cunning = (char.cunning + 6).coerceIn(0, 100)
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                familyMembers = updatedMembers,
                padis = updatedPadis,
                lastOutcomeMessage = "🕊️ Wahala Settled! Sat down with $targetName to clear the air. Grudges dropped, alliance restored!"
            )
        }
    }

    fun onToggleRelationshipOverlay(show: Boolean) {
        _uiState.update { it.copy(showRelationshipOverlay = show) }
    }

    fun onSwitchHomeLayout(structure: FamilyStructure) {
        val current = _uiState.value
        val updatedChar = current.character.copy(familyType = structure)
        _uiState.update {
            it.copy(
                character = updatedChar,
                lastOutcomeMessage = "Switched home living space architecture to: ${structure.title}."
            )
        }
    }

    fun onTabSelected(tab: String) {
        _uiState.update { it.copy(activeNavTab = tab) }
    }

    fun onOpenPhone(app: String) {
        _uiState.update { it.copy(showPhoneSheet = true, phoneActiveApp = app) }
    }

    fun onClosePhone() {
        _uiState.update { it.copy(showPhoneSheet = false) }
    }

    fun onStartDynasty() {
        val current = _uiState.value
        val nextGen = repository.startDynastyNextGen(current)
        _uiState.value = nextGen
    }

    fun onRestartGame() {
        _uiState.value = GameState(
            isOriginEngineActive = true,
            originQuizIndex = 0
        )
    }

    // --- OPEN WORLD SIMULATION METHODS ("Lagos Life") ---

    fun onMovePlayer(gx: Float, gy: Float) {
        val current = _uiState.value
        val openWorld = current.openWorldState
        val clampedX = gx.coerceIn(1f, 15f)
        val clampedY = gy.coerceIn(1f, 15f)

        // Check proximity to district POIs
        val districtPOIs = OpenWorldData.getPOIsForDistrict(openWorld.currentDistrict)
        val nearest = districtPOIs.minByOrNull { poi ->
            hypot(poi.gridX - clampedX, poi.gridY - clampedY)
        }
        val isNearby = nearest != null && hypot(nearest.gridX - clampedX, nearest.gridY - clampedY) < 2.0f
        val nearbyPOI = if (isNearby) nearest else null

        // Minor stamina burn when moving on foot
        val staminaBurn = if (openWorld.travelMode == TravelMode.ON_FOOT) 1 else 0
        val updatedStamina = (openWorld.stamina - staminaBurn).coerceAtLeast(10)

        // Occasional random street encounter trigger (8% chance when no active encounter)
        val shouldTriggerEncounter = (openWorld.activeEncounter == null && (Math.random() < 0.08))
        val newEncounter = if (shouldTriggerEncounter) {
            OpenWorldData.sampleEncounters.random()
        } else {
            openWorld.activeEncounter
        }

        val updatedWorld = openWorld.copy(
            playerX = clampedX,
            playerY = clampedY,
            stamina = updatedStamina,
            nearbyPOI = nearbyPOI,
            activeEncounter = newEncounter
        )

        _uiState.update { it.copy(openWorldState = updatedWorld) }
    }

    fun onSwitchDistrict(district: LagosDistrict) {
        val current = _uiState.value
        val char = current.character
        val fee = district.baseTransitFare

        if (char.naraBalance >= fee) {
            audioEngine.playDanfoHorn()
            val profile = environmentalAudio.evaluateSoundscape(
                timeOfDay = current.openWorldState.timeOfDay,
                district = district,
                state = current.openWorldState.currentState,
                weather = current.openWorldState.weather
            )
            environmentalAudio.updateEnvironment(
                timeOfDay = current.openWorldState.timeOfDay,
                district = district,
                state = current.openWorldState.currentState,
                weather = current.openWorldState.weather
            )
            val updatedChar = char.copy(naraBalance = char.naraBalance - fee)
            val updatedWorld = current.openWorldState.copy(
                currentDistrict = district,
                playerX = 7.0f,
                playerY = 7.0f,
                trafficHoldUp = district.defaultTrafficRisk,
                nearbyPOI = null,
                activeEncounter = null,
                currentAmbientSoundTitle = profile.title,
                currentAmbientSoundEmoji = profile.iconEmoji,
                lastWorldEventLog = "Boarded transit to ${district.displayName}! Paid ₦$fee Danfo fare. Ambience: ${profile.title}."
            )
            val tx = BankTransaction(
                id = "tx_transit_${System.currentTimeMillis()}",
                title = "Transit: ${district.displayName}",
                amountNara = fee,
                isCredit = false,
                date = "Year ${char.year}",
                reference = "NIP-TRANSIT"
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    openWorldState = updatedWorld,
                    bankTransactions = listOf(tx) + it.bankTransactions,
                    lastOutcomeMessage = "Arrived in ${district.displayName}!"
                )
            }
        } else {
            _uiState.update {
                it.copy(lastOutcomeMessage = "Insufficient cash for ₦$fee Danfo fare to ${district.displayName}.")
            }
        }
    }

    fun onChangeTravelMode(mode: TravelMode) {
        val current = _uiState.value
        when (mode) {
            TravelMode.DANFO_BUS -> audioEngine.playDanfoHorn()
            TravelMode.KEKE_NAPEP -> audioEngine.playKekeEngine()
            TravelMode.PRIVATE_CAR -> audioEngine.playVIPMotorcadeSiren()
            TravelMode.OKADA_BIKE -> audioEngine.playDanfoHorn()
            else -> {}
        }
        val updatedWorld = current.openWorldState.copy(
            travelMode = mode,
            lastWorldEventLog = "Switched transit mode to ${mode.title}. ${mode.description}"
        )
        _uiState.update { it.copy(openWorldState = updatedWorld) }
    }

    fun onInteractWorldPOI(poi: OpenWorldPOI) {
        val current = _uiState.value
        val updatedWorld = current.openWorldState.copy(activePOIDialog = poi)
        _uiState.update { it.copy(openWorldState = updatedWorld) }
    }

    fun onEatBukkaDish(dish: BukkaDish) {
        val current = _uiState.value
        val char = current.character
        if (char.naraBalance >= dish.costNara) {
            audioEngine.playBottleClink()
            val updatedChar = char.copy(
                naraBalance = char.naraBalance - dish.costNara,
                energy = (char.energy + dish.staminaGain).coerceIn(0, 100),
                stress = (char.stress - dish.stressRelief).coerceAtLeast(0)
            )
            val updatedWorld = current.openWorldState.copy(
                stamina = (current.openWorldState.stamina + dish.staminaGain).coerceIn(0, 100),
                activePOIDialog = null,
                lastWorldEventLog = "Enjoyed hot ${dish.name}! Stamina +${dish.staminaGain}%, Stress -${dish.stressRelief}%."
            )
            val tx = BankTransaction(
                id = "tx_bukka_${System.currentTimeMillis()}",
                title = "Mama Put: ${dish.name}",
                amountNara = dish.costNara,
                isCredit = false,
                date = "Year ${char.year}",
                reference = "NIP-BUKKA"
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    openWorldState = updatedWorld,
                    bankTransactions = listOf(tx) + it.bankTransactions,
                    lastOutcomeMessage = "Finished hot ${dish.name} feast at Mama Put Bukka!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Insufficient funds for ${dish.name} (₦${dish.costNara}).") }
        }
    }

    fun onBuyMarketGadget(gadget: MarketGadget) {
        val current = _uiState.value
        val char = current.character
        if (char.naraBalance >= gadget.costNara) {
            audioEngine.playCelebrationFanfare()
            val updatedChar = char.copy(
                naraBalance = char.naraBalance - gadget.costNara,
                streetClout = (char.streetClout + gadget.cloutGain).coerceIn(0, 100)
            )
            val updatedWorld = current.openWorldState.copy(
                activePOIDialog = null,
                lastWorldEventLog = "Purchased ${gadget.name} at Computer Village! Street Clout +${gadget.cloutGain}%."
            )
            val tx = BankTransaction(
                id = "tx_gadget_${System.currentTimeMillis()}",
                title = "Tech Market: ${gadget.name}",
                amountNara = gadget.costNara,
                isCredit = false,
                date = "Year ${char.year}",
                reference = "NIP-TECH"
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    openWorldState = updatedWorld,
                    bankTransactions = listOf(tx) + it.bankTransactions,
                    lastOutcomeMessage = "Acquired ${gadget.name}!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Need ₦${gadget.costNara} to buy ${gadget.name}.") }
        }
    }

    fun onBuyBoutiqueOutfit(outfit: BoutiqueOutfit) {
        val current = _uiState.value
        val char = current.character
        if (char.naraBalance >= outfit.costNara) {
            audioEngine.playLagosHandshakeSlap()
            val updatedChar = char.copy(
                naraBalance = char.naraBalance - outfit.costNara,
                streetClout = (char.streetClout + (outfit.dripGain / 2)).coerceIn(0, 100)
            )
            val updatedWorld = current.openWorldState.copy(
                dripRating = (current.openWorldState.dripRating + outfit.dripGain).coerceIn(0, 100),
                activePOIDialog = null,
                lastWorldEventLog = "Stepped out in ${outfit.name}! Drip +${outfit.dripGain}%, Clout +${outfit.dripGain / 2}%."
            )
            val tx = BankTransaction(
                id = "tx_drip_${System.currentTimeMillis()}",
                title = "Drip Salon: ${outfit.name}",
                amountNara = outfit.costNara,
                isCredit = false,
                date = "Year ${char.year}",
                reference = "NIP-DRIP"
            )
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    openWorldState = updatedWorld,
                    bankTransactions = listOf(tx) + it.bankTransactions,
                    lastOutcomeMessage = "Your drip rating escalated with ${outfit.name}!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Need ₦${outfit.costNara} for ${outfit.name}.") }
        }
    }

    fun onBuyFuel(litres: Int, forGenerator: Boolean) {
        val current = _uiState.value
        val char = current.character
        val cost = litres * 850L

        if (char.naraBalance >= cost) {
            val updatedChar = char.copy(
                naraBalance = char.naraBalance - cost,
                stress = (char.stress - 10).coerceAtLeast(0)
            )
            val updatedWorld = if (forGenerator) {
                current.openWorldState.copy(
                    generatorFuelLitres = current.openWorldState.generatorFuelLitres + litres,
                    activePOIDialog = null,
                    lastWorldEventLog = "Purchased $litres L of petrol for home generator. Compound power restored!"
                )
            } else {
                current.openWorldState.copy(
                    carFuelLitres = current.openWorldState.carFuelLitres + litres,
                    activePOIDialog = null,
                    lastWorldEventLog = "Filled $litres L of PMS in vehicle tank at ₦850/L."
                )
            }
            _uiState.update {
                it.copy(
                    character = updatedChar,
                    openWorldState = updatedWorld,
                    lastOutcomeMessage = "Fueled up $litres Litres of petrol!"
                )
            }
        } else {
            _uiState.update { it.copy(lastOutcomeMessage = "Need ₦$cost for $litres litres of fuel.") }
        }
    }

    fun onResolveStreetEncounter(option: StreetEncounterOption) {
        val current = _uiState.value
        val char = current.character
        val openWorld = current.openWorldState

        val updatedNara = (char.naraBalance - option.costNara + option.cashReward).coerceAtLeast(0)
        val updatedChar = char.copy(
            naraBalance = updatedNara,
            streetClout = (char.streetClout + option.cloutDelta).coerceIn(0, 100),
            stress = (char.stress + option.stressDelta).coerceIn(0, 100)
        )
        val updatedWorld = openWorld.copy(
            stamina = (openWorld.stamina + option.staminaDelta).coerceIn(0, 100),
            activeEncounter = null,
            lastWorldEventLog = option.outcomeNarrative
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = updatedWorld,
                lastOutcomeMessage = option.outcomeNarrative
            )
        }
    }

    fun onStartStreetMission(mission: StreetMission) {
        val current = _uiState.value
        audioEngine.playDanfoHorn()
        val updatedWorld = current.openWorldState.copy(
            activeMission = mission,
            lastWorldEventLog = "Accepted mission: ${mission.title}! Destination: ${mission.destinationDistrict.displayName}."
        )
        _uiState.update {
            it.copy(
                openWorldState = updatedWorld,
                lastOutcomeMessage = "Started Gig: ${mission.title}!"
            )
        }
    }

    fun onAdvanceWorldTimeOfDay() {
        val current = _uiState.value
        val openWorld = current.openWorldState
        val nextTime = when (openWorld.timeOfDay) {
            LagosTimeOfDay.MORNING_RUSH -> LagosTimeOfDay.AFTERNOON_HEAT
            LagosTimeOfDay.AFTERNOON_HEAT -> LagosTimeOfDay.EVENING_HOLDUP
            LagosTimeOfDay.EVENING_HOLDUP -> LagosTimeOfDay.OWAMBE_NIGHT
            LagosTimeOfDay.OWAMBE_NIGHT -> LagosTimeOfDay.MIDNIGHT_BREEZE
            LagosTimeOfDay.MIDNIGHT_BREEZE -> LagosTimeOfDay.MORNING_RUSH
        }
        val nextTraffic = when (nextTime) {
            LagosTimeOfDay.EVENING_HOLDUP -> 88
            LagosTimeOfDay.MORNING_RUSH -> 78
            LagosTimeOfDay.AFTERNOON_HEAT -> 62
            LagosTimeOfDay.OWAMBE_NIGHT -> 45
            LagosTimeOfDay.MIDNIGHT_BREEZE -> 20
        }
        val nextWeather = if (Math.random() < 0.35) {
            LagosWeather.LAGOS_DOWNPOUR
        } else {
            LagosWeather.SUNNY_HARMATTAN
        }

        val profile = environmentalAudio.evaluateSoundscape(
            timeOfDay = nextTime,
            district = openWorld.currentDistrict,
            state = openWorld.currentState,
            weather = nextWeather
        )
        environmentalAudio.updateEnvironment(
            timeOfDay = nextTime,
            district = openWorld.currentDistrict,
            state = openWorld.currentState,
            weather = nextWeather
        )

        val updatedWorld = openWorld.copy(
            timeOfDay = nextTime,
            trafficHoldUp = nextTraffic,
            weather = nextWeather,
            currentAmbientSoundTitle = profile.title,
            currentAmbientSoundEmoji = profile.iconEmoji,
            lastWorldEventLog = "Time advanced to ${nextTime.label} (${nextTime.clockTime}). Atmosphere: ${profile.title}."
        )
        _uiState.update { it.copy(openWorldState = updatedWorld) }
    }

    fun onHonkOpenWorldHorn() {
        val current = _uiState.value
        when (current.openWorldState.travelMode) {
            TravelMode.DANFO_BUS -> audioEngine.playDanfoHorn()
            TravelMode.KEKE_NAPEP -> audioEngine.playKekeEngine()
            TravelMode.PRIVATE_CAR -> audioEngine.playVIPMotorcadeSiren()
            TravelMode.OKADA_BIKE -> audioEngine.playDanfoHorn()
            else -> audioEngine.playLagosHandshakeSlap()
        }
    }

    // --- DAILY MISSION LOG & NIGERIA LIFE SIMULATION METHODS ---

    fun onToggleDailyMissionOverlay() {
        val current = _uiState.value
        val nextOpen = !current.openWorldState.isMissionOverlayOpen
        if (nextOpen) audioEngine.playCashChime()
        _uiState.update {
            it.copy(
                openWorldState = it.openWorldState.copy(
                    isMissionOverlayOpen = nextOpen
                )
            )
        }
    }

    fun onSetMissionFilter(filter: String) {
        _uiState.update {
            it.copy(
                openWorldState = it.openWorldState.copy(
                    missionFilter = filter
                )
            )
        }
    }

    fun onExecuteDailyMission(mission: DailyMission) {
        val current = _uiState.value
        val char = current.character
        val world = current.openWorldState

        if (char.naraBalance < mission.costNaira || world.stamina < mission.staminaCost) {
            audioEngine.playTensionChord()
            _uiState.update {
                it.copy(
                    openWorldState = world.copy(
                        lastWorldEventLog = "Cannot execute mission: Insufficient funds or stamina!"
                    )
                )
            }
            return
        }

        audioEngine.playFujiCheer()

        val updatedMissions = world.dailyMissions.map {
            if (it.id == mission.id) it.copy(status = MissionStatus.COMPLETED, currentProgress = it.maxProgress)
            else it
        }

        val updatedChar = char.copy(
            naraBalance = char.naraBalance - mission.costNaira + mission.rewardNaira,
            streetClout = (char.streetClout + mission.rewardClout).coerceAtLeast(0),
            godfatherFavor = (char.godfatherFavor + mission.rewardGodfatherFavor).coerceIn(0, 100),
            conscience = (char.conscience + mission.rewardConscienceDelta).coerceIn(0, 100)
        )

        val updatedFamily = if (mission.rewardFamilyLoyalty > 0) {
            current.familyMembers.map {
                it.copy(
                    loyalty = (it.loyalty + mission.rewardFamilyLoyalty).coerceIn(0, 100),
                    happiness = (it.happiness + mission.rewardFamilyLoyalty).coerceIn(0, 100)
                )
            }
        } else current.familyMembers

        val updatedWorld = world.copy(
            stamina = (world.stamina - mission.staminaCost).coerceAtLeast(0),
            dailyMissions = updatedMissions,
            lastWorldEventLog = "Completed task: ${mission.title}! Earned rewards. Tap CLAIM to finalize."
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                familyMembers = updatedFamily,
                openWorldState = updatedWorld,
                lastOutcomeMessage = "Task accomplished: ${mission.title}!"
            )
        }
    }

    fun onClaimDailyMissionReward(mission: DailyMission) {
        val current = _uiState.value
        val world = current.openWorldState

        audioEngine.playCashChime()

        val updatedMissions = world.dailyMissions.map {
            if (it.id == mission.id) it.copy(status = MissionStatus.CLAIMED)
            else it
        }

        val updatedWorld = world.copy(
            dailyMissions = updatedMissions,
            lastWorldEventLog = "Claimed reward for: ${mission.title}!"
        )

        _uiState.update {
            it.copy(
                openWorldState = updatedWorld,
                lastOutcomeMessage = "Claimed: ${mission.title} rewards credited!"
            )
        }
    }

    fun onTravelToState(state: NigeriaState, isAirTravel: Boolean) {
        val current = _uiState.value
        val fare = if (isAirTravel) state.airFlightFareNaira else state.roadDanfoFareNaira
        val char = current.character

        if (char.naraBalance < fare) {
            audioEngine.playTensionChord()
            _uiState.update {
                it.copy(
                    openWorldState = current.openWorldState.copy(
                        lastWorldEventLog = "Cannot afford travel to ${state.name} (Requires ₦${fare / 1_000}k)."
                    )
                )
            }
            return
        }

        if (isAirTravel) audioEngine.playVIPMotorcadeSiren()
        else audioEngine.playDanfoHorn()

        val profile = environmentalAudio.evaluateSoundscape(
            timeOfDay = current.openWorldState.timeOfDay,
            district = current.openWorldState.currentDistrict,
            state = state,
            weather = current.openWorldState.weather
        )
        environmentalAudio.updateEnvironment(
            timeOfDay = current.openWorldState.timeOfDay,
            district = current.openWorldState.currentDistrict,
            state = state,
            weather = current.openWorldState.weather
        )

        val updatedChar = char.copy(naraBalance = char.naraBalance - fare)
        val updatedWorld = current.openWorldState.copy(
            currentState = state,
            isNationMapOpen = false,
            currentAmbientSoundTitle = profile.title,
            currentAmbientSoundEmoji = profile.iconEmoji,
            lastWorldEventLog = "Arrived in ${state.name} (${state.hubCity}) via ${if (isAirTravel) "Flight" else "Interstate Bus"}! Ambience: ${profile.title}. Regional Crisis: ${state.primaryCrisis.title}."
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = updatedWorld,
                lastOutcomeMessage = "Welcome to ${state.name}! Navigating regional tensions in ${state.hubCity}."
            )
        }
    }

    fun onAdvanceDayCycle() {
        val current = _uiState.value
        val world = current.openWorldState
        val wallet = world.currencyWallet
        audioEngine.playCashChime()

        val newDayNum = world.currentDayNumber + 1
        val days = DayOfWeekName.values()
        val nextDayOfWeek = days[(newDayNum - 1) % days.size]

        val dailyBurn = wallet.dailyLivingCostTotal
        val (newCash, newBank) = if (current.character.naraBalance >= dailyBurn) {
            (current.character.naraBalance - dailyBurn) to wallet.bankNaira
        } else {
            val deficit = dailyBurn - current.character.naraBalance
            0L to (wallet.bankNaira - deficit).coerceAtLeast(0L)
        }

        val updatedWallet = wallet.copy(
            cashNaira = newCash,
            bankNaira = newBank
        )

        val newMissions = DailyMissionData.generateNextDayMissions(newDayNum)

        val updatedChar = current.character.copy(
            naraBalance = newCash,
            stress = (current.character.stress - 15).coerceAtLeast(0)
        )

        val profile = environmentalAudio.evaluateSoundscape(
            timeOfDay = LagosTimeOfDay.MORNING_RUSH,
            district = world.currentDistrict,
            state = world.currentState,
            weather = world.weather
        )
        environmentalAudio.updateEnvironment(
            timeOfDay = LagosTimeOfDay.MORNING_RUSH,
            district = world.currentDistrict,
            state = world.currentState,
            weather = world.weather
        )

        val updatedWorld = world.copy(
            currentDayNumber = newDayNum,
            dayOfWeek = nextDayOfWeek,
            timeOfDay = LagosTimeOfDay.MORNING_RUSH,
            stamina = 100,
            trafficHoldUp = 75,
            currencyWallet = updatedWallet,
            dailyMissions = newMissions,
            currentAmbientSoundTitle = profile.title,
            currentAmbientSoundEmoji = profile.iconEmoji,
            lastWorldEventLog = "Day $newDayNum (${nextDayOfWeek.displayName}) has begun! Compound gen & food expenses deducted (₦${dailyBurn / 1_000}k). Ambience: ${profile.title}."
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = updatedWorld,
                lastOutcomeMessage = "Awoke to Day $newDayNum! Compound living expenses settled. Morning hustle resumes!"
            )
        }
    }

    fun onDepositToBank(amount: Long) {
        val current = _uiState.value
        val char = current.character
        val wallet = current.openWorldState.currencyWallet
        if (char.naraBalance < amount) return

        audioEngine.playCashChime()
        val updatedChar = char.copy(naraBalance = char.naraBalance - amount)
        val updatedWallet = wallet.copy(
            cashNaira = updatedChar.naraBalance,
            bankNaira = wallet.bankNaira + amount
        )
        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = it.openWorldState.copy(
                    currencyWallet = updatedWallet,
                    lastWorldEventLog = "Deposited ₦${amount / 1_000}k to Commercial Bank account."
                )
            )
        }
    }

    fun onWithdrawToCash(amount: Long) {
        val current = _uiState.value
        val char = current.character
        val wallet = current.openWorldState.currencyWallet
        if (wallet.bankNaira < amount) return

        audioEngine.playCashChime()
        val updatedChar = char.copy(naraBalance = char.naraBalance + amount)
        val updatedWallet = wallet.copy(
            cashNaira = updatedChar.naraBalance,
            bankNaira = wallet.bankNaira - amount
        )
        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = it.openWorldState.copy(
                    currencyWallet = updatedWallet,
                    lastWorldEventLog = "Withdrew ₦${amount / 1_000}k cash from ATM."
                )
            )
        }
    }

    fun onBuyDollars(usdAmount: Long) {
        val current = _uiState.value
        val char = current.character
        val wallet = current.openWorldState.currencyWallet
        val costNaira = wallet.bdcExchangeRate * usdAmount
        if (char.naraBalance < costNaira) return

        audioEngine.playCashChime()
        val updatedChar = char.copy(naraBalance = char.naraBalance - costNaira)
        val updatedWallet = wallet.copy(
            cashNaira = updatedChar.naraBalance,
            domUsd = wallet.domUsd + usdAmount
        )
        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = it.openWorldState.copy(
                    currencyWallet = updatedWallet,
                    lastWorldEventLog = "Purchased $$usdAmount USD at BDC for ₦${costNaira / 1_000}k."
                )
            )
        }
    }

    fun onSellDollars(usdAmount: Long) {
        val current = _uiState.value
        val char = current.character
        val wallet = current.openWorldState.currencyWallet
        if (wallet.domUsd < usdAmount) return

        val returnNaira = wallet.bdcExchangeRate * usdAmount
        audioEngine.playCashChime()
        val updatedChar = char.copy(naraBalance = char.naraBalance + returnNaira)
        val updatedWallet = wallet.copy(
            cashNaira = updatedChar.naraBalance,
            domUsd = wallet.domUsd - usdAmount
        )
        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = it.openWorldState.copy(
                    currencyWallet = updatedWallet,
                    lastWorldEventLog = "Sold $$usdAmount USD at BDC for ₦${returnNaira / 1_000}k cash."
                )
            )
        }
    }

    fun onToggleNationMapDialog(isOpen: Boolean) {
        if (isOpen) audioEngine.playCashChime()
        _uiState.update {
            it.copy(
                openWorldState = it.openWorldState.copy(isNationMapOpen = isOpen)
            )
        }
    }

    fun onToggleCurrencyExchangeDialog(isOpen: Boolean) {
        if (isOpen) audioEngine.playCashChime()
        _uiState.update {
            it.copy(
                openWorldState = it.openWorldState.copy(isCurrencyExchangeOpen = isOpen)
            )
        }
    }

    fun onToggleAmbientAudio() {
        val current = _uiState.value
        val newEnabled = !current.openWorldState.isAmbientAudioEnabled
        environmentalAudio.setMuted(!newEnabled)
        val profile = environmentalAudio.currentProfile
        _uiState.update {
            it.copy(
                openWorldState = it.openWorldState.copy(
                    isAmbientAudioEnabled = newEnabled,
                    currentAmbientSoundTitle = if (newEnabled) profile.title else "Ambient Audio Muted",
                    currentAmbientSoundEmoji = if (newEnabled) profile.iconEmoji else "🔇",
                    lastWorldEventLog = if (newEnabled) "Ambient Soundscape: ${profile.title} (${profile.subtitle})." else "Environmental sound muted."
                )
            )
        }
    }

    fun onSetAmbientAudioVolume(volume: Float) {
        environmentalAudio.masterVolume = volume.coerceIn(0.0f, 1.0f)
    }

    fun onToggleSfx() {
        val next = !audioEngine.isSfxEnabled
        audioEngine.isSfxEnabled = next
        _uiState.update { it.copy(isSfxEnabled = next) }
        if (next) audioEngine.playPositiveEventSound()
    }

    fun isSfxEnabled(): Boolean = audioEngine.isSfxEnabled

    /**
     * Executes tactical political patronage: spends naira to mobilize wards and boost Influence.
     */
    fun onExecutePatronage(costNaira: Long, influenceGain: Int, note: String) {
        val curr = _uiState.value
        val wallet = curr.openWorldState.currencyWallet
        val char = curr.character
        val totalLiquid = char.naraBalance + wallet.bankNaira
        if (totalLiquid < costNaira) return

        audioEngine.playPositiveEventSound()

        val (newCash, newBank) = if (char.naraBalance >= costNaira) {
            (char.naraBalance - costNaira) to wallet.bankNaira
        } else {
            val def = costNaira - char.naraBalance
            0L to (wallet.bankNaira - def).coerceAtLeast(0L)
        }

        val updatedChar = char.copy(
            naraBalance = newCash,
            politicalCapital = (char.politicalCapital + influenceGain).coerceIn(0, 100),
            streetClout = (char.streetClout + (influenceGain / 2)).coerceIn(0, 100)
        )
        val updatedWallet = wallet.copy(bankNaira = newBank)
        val updatedWorld = curr.openWorldState.copy(currencyWallet = updatedWallet)

        val logEntry = "Patronage: ₦${costNaira / 1_000}k deployed for +$influenceGain Influence ($note)"
        val updatedRes = curr.playerResources.copy(
            influence = curr.playerResources.influence.copy(
                politicalCapital = updatedChar.politicalCapital,
                streetClout = updatedChar.streetClout,
                lastDelta = influenceGain,
                lastDeltaReason = "Patronage dividend: $note"
            ),
            wealth = curr.playerResources.wealth.copy(
                cashNaira = newCash,
                bankNaira = newBank,
                lastDeltaNaira = -costNaira,
                lastDeltaReason = "Patronage expenditure: $note"
            ),
            resourceAuditLog = listOf(logEntry) + curr.playerResources.resourceAuditLog.take(19),
            lastActionTimestamp = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                openWorldState = updatedWorld,
                playerResources = updatedRes
            )
        }
        playerResourceViewModel.syncFromGameState(_uiState.value)
    }

    /**
     * Disburses emergency kinship welfare grant: boosts Family Loyalty and Elders' blessings.
     */
    fun onDisburseFamilyGrant(grantAmount: Long) {
        val curr = _uiState.value
        val wallet = curr.openWorldState.currencyWallet
        val char = curr.character
        val totalLiquid = char.naraBalance + wallet.bankNaira
        if (totalLiquid < grantAmount) return

        audioEngine.playBlessingChime()

        val (newCash, newBank) = if (char.naraBalance >= grantAmount) {
            (char.naraBalance - grantAmount) to wallet.bankNaira
        } else {
            val def = grantAmount - char.naraBalance
            0L to (wallet.bankNaira - def).coerceAtLeast(0L)
        }

        val loyaltyBoost = if (grantAmount >= 100_000L) 15 else 8
        val approvalBoost = if (grantAmount >= 100_000L) 12 else 6

        val updatedChar = char.copy(
            naraBalance = newCash,
            stress = (char.stress - 10).coerceAtLeast(0)
        )
        val updatedWallet = wallet.copy(bankNaira = newBank)
        val updatedWorld = curr.openWorldState.copy(currencyWallet = updatedWallet)

        val updatedFamily = curr.familyMembers.map {
            it.copy(
                loyalty = (it.loyalty + loyaltyBoost).coerceIn(0, 100),
                affection = (it.affection + approvalBoost).coerceIn(0, 100)
            )
        }

        val logEntry = "Kinship Welfare: Disbursed ₦${grantAmount / 1_000}k to compound elders (+$loyaltyBoost Loyalty, +$approvalBoost Approval)"
        val updatedRes = curr.playerResources.copy(
            wealth = curr.playerResources.wealth.copy(
                cashNaira = newCash,
                bankNaira = newBank,
                lastDeltaNaira = -grantAmount,
                lastDeltaReason = "Family compound emergency welfare grant"
            ),
            familyLoyalty = curr.playerResources.familyLoyalty.copy(
                clanLoyaltyScore = (curr.playerResources.familyLoyalty.clanLoyaltyScore + loyaltyBoost).coerceIn(0, 100),
                eldersApprovalScore = (curr.playerResources.familyLoyalty.eldersApprovalScore + approvalBoost).coerceIn(0, 100),
                blackTaxBurdenIndex = (curr.playerResources.familyLoyalty.blackTaxBurdenIndex - 15).coerceAtLeast(0),
                lastDelta = loyaltyBoost,
                lastDeltaReason = "Emergency kinship stipend disbursed"
            ),
            resourceAuditLog = listOf(logEntry) + curr.playerResources.resourceAuditLog.take(19),
            lastActionTimestamp = System.currentTimeMillis()
        )

        _uiState.update {
            it.copy(
                character = updatedChar,
                familyMembers = updatedFamily,
                openWorldState = updatedWorld,
                playerResources = updatedRes
            )
        }
        playerResourceViewModel.syncFromGameState(_uiState.value)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
        environmentalAudio.release()
    }
}

