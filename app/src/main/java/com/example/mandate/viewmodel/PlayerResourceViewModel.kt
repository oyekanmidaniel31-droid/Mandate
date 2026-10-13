package com.example.mandate.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mandate.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel dedicated to managing, tracking, and executing operations
 * on the player's core resource pillars: Influence, Wealth, and Family Loyalty.
 */
class PlayerResourceViewModel : ViewModel() {

    private val _resources = MutableStateFlow(PlayerResources())
    val resources: StateFlow<PlayerResources> = _resources.asStateFlow()

    private var audioEngine: com.example.mandate.audio.FujiAudioEngine? = null

    /**
     * Binds audio engine to play immersive procedural sound effects on resource changes.
     */
    fun attachAudioEngine(engine: com.example.mandate.audio.FujiAudioEngine?) {
        this.audioEngine = engine
    }

    /**
     * Synchronizes the resource state from the overall GameState.
     */
    fun syncFromGameState(state: GameState) {
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

        _resources.update { current ->
            current.copy(
                influence = current.influence.copy(
                    politicalCapital = char.politicalCapital,
                    streetClout = char.streetClout,
                    godfatherFavor = char.godfatherFavor,
                    mediaSpinControl = (char.cunning * 0.6 + char.streetClout * 0.4).toInt().coerceIn(0, 100)
                ),
                wealth = current.wealth.copy(
                    cashNaira = char.naraBalance,
                    bankNaira = wallet.bankNaira,
                    domUsd = wallet.domUsd,
                    bdcRate = wallet.bdcExchangeRate,
                    dailyBurnNaira = wallet.dailyLivingCostTotal
                ),
                familyLoyalty = current.familyLoyalty.copy(
                    clanLoyaltyScore = avgFamilyLoyalty,
                    eldersApprovalScore = eldersApproval,
                    blackTaxBurdenIndex = taxBurden,
                    activeDependentsCount = family.size,
                    pendingBlackTaxAmount = totalPendingTax
                )
            )
        }
    }

    /**
     * Modifies player influence metrics across specific categories.
     */
    fun modifyInfluence(category: String, amount: Int, reason: String) {
        _resources.update { curr ->
            val inf = curr.influence
            val updatedInf = when (category.uppercase()) {
                "POLITICAL" -> inf.copy(
                    politicalCapital = (inf.politicalCapital + amount).coerceIn(0, 100),
                    lastDelta = amount,
                    lastDeltaReason = reason
                )
                "CLOUT" -> inf.copy(
                    streetClout = (inf.streetClout + amount).coerceIn(0, 100),
                    lastDelta = amount,
                    lastDeltaReason = reason
                )
                "GODFATHER" -> inf.copy(
                    godfatherFavor = (inf.godfatherFavor + amount).coerceIn(0, 100),
                    lastDelta = amount,
                    lastDeltaReason = reason
                )
                "MEDIA" -> inf.copy(
                    mediaSpinControl = (inf.mediaSpinControl + amount).coerceIn(0, 100),
                    lastDelta = amount,
                    lastDeltaReason = reason
                )
                else -> inf.copy(
                    politicalCapital = (inf.politicalCapital + amount).coerceIn(0, 100),
                    streetClout = (inf.streetClout + amount).coerceIn(0, 100),
                    lastDelta = amount,
                    lastDeltaReason = reason
                )
            }
            curr.copy(
                influence = updatedInf,
                resourceAuditLog = listOf("${if (amount >= 0) "+" else ""}$amount Influence: $reason") + curr.resourceAuditLog.take(19),
                lastActionTimestamp = System.currentTimeMillis()
            )
        }
        if (amount > 0) {
            audioEngine?.playPositiveEventSound()
        } else if (amount < 0) {
            audioEngine?.playNegativeEventSound()
        }
    }

    /**
     * Modifies wealth balances (Cash, Bank, USD).
     */
    fun modifyWealth(deltaCash: Long, deltaBank: Long, deltaUsd: Long, reason: String) {
        val currVal = _resources.value
        val totalDeltaNaira = deltaCash + deltaBank + (deltaUsd * currVal.wealth.bdcRate)
        _resources.update { curr ->
            val w = curr.wealth
            val updatedW = w.copy(
                cashNaira = (w.cashNaira + deltaCash).coerceAtLeast(0L),
                bankNaira = (w.bankNaira + deltaBank).coerceAtLeast(0L),
                domUsd = (w.domUsd + deltaUsd).coerceAtLeast(0L),
                lastDeltaNaira = totalDeltaNaira,
                lastDeltaReason = reason
            )
            curr.copy(
                wealth = updatedW,
                resourceAuditLog = listOf("Wealth update: $reason") + curr.resourceAuditLog.take(19),
                lastActionTimestamp = System.currentTimeMillis()
            )
        }
        if (totalDeltaNaira > 0) {
            audioEngine?.playResourceGainSound()
        } else if (totalDeltaNaira < 0) {
            audioEngine?.playResourceLossSound()
        }
    }

    /**
     * Modifies family loyalty scores.
     */
    fun modifyFamilyLoyalty(deltaLoyalty: Int, deltaApproval: Int, reason: String) {
        _resources.update { curr ->
            val fam = curr.familyLoyalty
            val updatedFam = fam.copy(
                clanLoyaltyScore = (fam.clanLoyaltyScore + deltaLoyalty).coerceIn(0, 100),
                eldersApprovalScore = (fam.eldersApprovalScore + deltaApproval).coerceIn(0, 100),
                lastDelta = deltaLoyalty,
                lastDeltaReason = reason
            )
            curr.copy(
                familyLoyalty = updatedFam,
                resourceAuditLog = listOf("${if (deltaLoyalty >= 0) "+" else ""}$deltaLoyalty Clan Loyalty: $reason") + curr.resourceAuditLog.take(19),
                lastActionTimestamp = System.currentTimeMillis()
            )
        }
        if (deltaLoyalty > 0 || deltaApproval > 0) {
            audioEngine?.playBlessingChime()
        } else if (deltaLoyalty < 0 || deltaApproval < 0) {
            audioEngine?.playNegativeEventSound()
        }
    }

    /**
     * Executes tactical patronage maneuver: converts treasury wealth into political influence.
     */
    fun executeStrategicPatronage(costNaira: Long, influenceGain: Int, note: String): Boolean {
        val curr = _resources.value
        if (curr.wealth.cashNaira < costNaira && curr.wealth.bankNaira < costNaira) {
            audioEngine?.playNegativeEventSound()
            return false
        }

        val (newCash, newBank) = if (curr.wealth.cashNaira >= costNaira) {
            (curr.wealth.cashNaira - costNaira) to curr.wealth.bankNaira
        } else {
            val deficit = costNaira - curr.wealth.cashNaira
            0L to (curr.wealth.bankNaira - deficit).coerceAtLeast(0L)
        }

        _resources.update { state ->
            state.copy(
                wealth = state.wealth.copy(
                    cashNaira = newCash,
                    bankNaira = newBank,
                    lastDeltaNaira = -costNaira,
                    lastDeltaReason = "Strategic Patronage: $note"
                ),
                influence = state.influence.copy(
                    politicalCapital = (state.influence.politicalCapital + influenceGain).coerceIn(0, 100),
                    streetClout = (state.influence.streetClout + (influenceGain / 2)).coerceIn(0, 100),
                    lastDelta = influenceGain,
                    lastDeltaReason = "Patronage dividend: $note"
                ),
                resourceAuditLog = listOf("Executed Patronage: ₦${costNaira / 1_000}k spent for +$influenceGain Influence ($note)") + state.resourceAuditLog.take(19),
                lastActionTimestamp = System.currentTimeMillis()
            )
        }
        audioEngine?.playPositiveEventSound()
        return true
    }

    /**
     * Disburses family welfare grant to compound elders: converts wealth into family loyalty & approval.
     */
    fun disburseFamilyWelfareGrant(grantAmount: Long): Boolean {
        val curr = _resources.value
        if (curr.wealth.cashNaira < grantAmount && curr.wealth.bankNaira < grantAmount) {
            audioEngine?.playNegativeEventSound()
            return false
        }

        val (newCash, newBank) = if (curr.wealth.cashNaira >= grantAmount) {
            (curr.wealth.cashNaira - grantAmount) to curr.wealth.bankNaira
        } else {
            val deficit = grantAmount - curr.wealth.cashNaira
            0L to (curr.wealth.bankNaira - deficit).coerceAtLeast(0L)
        }

        val loyaltyBoost = if (grantAmount >= 100_000L) 15 else 8
        val approvalBoost = if (grantAmount >= 100_000L) 12 else 6

        _resources.update { state ->
            state.copy(
                wealth = state.wealth.copy(
                    cashNaira = newCash,
                    bankNaira = newBank,
                    lastDeltaNaira = -grantAmount,
                    lastDeltaReason = "Family Compound Welfare & Medical Grant"
                ),
                familyLoyalty = state.familyLoyalty.copy(
                    clanLoyaltyScore = (state.familyLoyalty.clanLoyaltyScore + loyaltyBoost).coerceIn(0, 100),
                    eldersApprovalScore = (state.familyLoyalty.eldersApprovalScore + approvalBoost).coerceIn(0, 100),
                    blackTaxBurdenIndex = (state.familyLoyalty.blackTaxBurdenIndex - 15).coerceAtLeast(0),
                    lastDelta = loyaltyBoost,
                    lastDeltaReason = "Emergency kinship stipend disbursed"
                ),
                resourceAuditLog = listOf("Family Welfare: Sent ₦${grantAmount / 1_000}k to compound elders (+$loyaltyBoost Loyalty, +$approvalBoost Approval)") + state.resourceAuditLog.take(19),
                lastActionTimestamp = System.currentTimeMillis()
            )
        }
        audioEngine?.playBlessingChime()
        return true
    }
}
