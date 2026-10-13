package com.example

import com.example.mandate.model.*
import com.example.mandate.viewmodel.PlayerResourceViewModel
import org.junit.Assert.*
import org.junit.Test

class PlayerResourcesTest {

    @Test
    fun testInfluenceMetricsCompositeScoreAndTiers() {
        val lowInf = InfluenceMetrics(
            politicalCapital = 15,
            streetClout = 20,
            godfatherFavor = 10,
            mediaSpinControl = 10
        )
        assertTrue(lowInf.compositeScore < 20)
        assertEquals("Street Hustler", lowInf.tierTitle)
        assertEquals("🛵", lowInf.tierEmoji)

        val titanInf = InfluenceMetrics(
            politicalCapital = 90,
            streetClout = 85,
            godfatherFavor = 80,
            mediaSpinControl = 80
        )
        assertTrue(titanInf.compositeScore >= 80)
        assertEquals("Villa Mandate Kingmaker", titanInf.tierTitle)
        assertEquals("👑", titanInf.tierEmoji)
    }

    @Test
    fun testWealthMetricsNetWorthAndTiers() {
        val wealth = WealthMetrics(
            cashNaira = 100_000L,
            bankNaira = 1_900_000L,
            domUsd = 2_000L,
            bdcRate = 1_500L
        )
        // Net worth: 100k + 1.9M + (2,000 * 1,500 = 3M) = 5,000,000 Naira
        assertEquals(5_000_000L, wealth.netWorthNaira)
        assertEquals("Comfortable Politico", wealth.wealthTier)
        assertEquals("💼", wealth.wealthEmoji)
    }

    @Test
    fun testFamilyLoyaltyMetricsAndTiers() {
        val dynastyFam = FamilyLoyaltyMetrics(
            clanLoyaltyScore = 88,
            eldersApprovalScore = 92,
            blackTaxBurdenIndex = 25,
            activeDependentsCount = 6,
            pendingBlackTaxAmount = 40_000L
        )
        assertEquals("Unbreakable Dynasty", dynastyFam.solidarityTier)
        assertEquals("🛡️", dynastyFam.solidarityEmoji)

        val strainedFam = FamilyLoyaltyMetrics(
            clanLoyaltyScore = 25,
            eldersApprovalScore = 30,
            blackTaxBurdenIndex = 85,
            activeDependentsCount = 12,
            pendingBlackTaxAmount = 300_000L
        )
        assertEquals("Strained Clan Disputes", strainedFam.solidarityTier)
        assertEquals("⚠️", strainedFam.solidarityEmoji)
    }

    @Test
    fun testPlayerResourceViewModelPatronageAndFamilyGrant() {
        val vm = PlayerResourceViewModel()

        // Set baseline wealth
        vm.modifyWealth(deltaCash = 200_000L, deltaBank = 500_000L, deltaUsd = 0L, reason = "Initial campaign donations")

        val initialCash = vm.resources.value.wealth.cashNaira
        val initialInf = vm.resources.value.influence.politicalCapital

        // Execute patronage maneuver
        val success = vm.executeStrategicPatronage(
            costNaira = 50_000L,
            influenceGain = 15,
            note = "Ward delegates rally"
        )
        assertTrue(success)
        assertEquals(initialCash - 50_000L, vm.resources.value.wealth.cashNaira)
        assertEquals(initialInf + 15, vm.resources.value.influence.politicalCapital)
        assertTrue(vm.resources.value.resourceAuditLog.isNotEmpty())
        assertTrue(vm.resources.value.resourceAuditLog.first().contains("Executed Patronage"))

        // Disburse family grant
        val grantSuccess = vm.disburseFamilyWelfareGrant(50_000L)
        assertTrue(grantSuccess)
        assertTrue(vm.resources.value.familyLoyalty.clanLoyaltyScore > 65)
        assertTrue(vm.resources.value.resourceAuditLog.first().contains("Family Welfare"))

        // Audio Engine integration test
        val audio = com.example.mandate.audio.FujiAudioEngine()
        vm.attachAudioEngine(audio)
        vm.modifyInfluence("POLITICAL", 10, "Media praise")
        vm.modifyWealth(100_000L, 0L, 0L, "Dividend")
        vm.modifyFamilyLoyalty(5, 5, "Blessing")
        audio.release()
    }

    @Test
    fun testResourceTooltipCatalogCoverage() {
        val infSpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("INFLUENCE")
        assertEquals("👑", infSpec.emoji)
        assertTrue(infSpec.gameImpact.isNotBlank())
        assertTrue(infSpec.mechanics.isNotEmpty())
        assertTrue(infSpec.strategicAdvice.isNotEmpty())

        val wealthSpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("WEALTH")
        assertEquals("💰", wealthSpec.emoji)
        assertTrue(wealthSpec.title.contains("WEALTH", ignoreCase = true))

        val familySpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("FAMILY_LOYALTY")
        assertEquals("🛡️", familySpec.emoji)
        assertTrue(familySpec.title.contains("FAMILY", ignoreCase = true))

        // Sub-metric lookups
        val polCapSpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("POLITICAL_CAPITAL")
        assertTrue(polCapSpec.title.contains("POLITICAL", ignoreCase = true))

        val cashSpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("CASH")
        assertTrue(cashSpec.title.contains("CASH", ignoreCase = true))

        val blackTaxSpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("BLACK_TAX")
        assertTrue(blackTaxSpec.title.contains("BLACK TAX", ignoreCase = true))

        // Unknown key fallback
        val fallbackSpec = com.example.mandate.data.ResourceTooltipCatalog.getSpec("UNKNOWN_METRIC_KEY")
        assertNotNull(fallbackSpec)
        assertTrue(fallbackSpec.title.contains("UNKNOWN_METRIC_KEY"))
    }

    @Test
    fun testFujiAudioEngineSoundMethodsAndControls() {
        val audioEngine = com.example.mandate.audio.FujiAudioEngine()
        assertTrue(audioEngine.isSfxEnabled)
        assertEquals(0.85f, audioEngine.sfxVolume, 0.01f)

        // Test non-crashing invocations
        audioEngine.playPositiveEventSound()
        audioEngine.playNegativeEventSound()
        audioEngine.playResourceGainSound()
        audioEngine.playResourceLossSound()
        audioEngine.playPositiveEvent()
        audioEngine.playNegativeEvent()
        audioEngine.playPositiveChime()
        audioEngine.playNegativeBuzz()

        // Test mute toggle
        audioEngine.isSfxEnabled = false
        assertFalse(audioEngine.isSfxEnabled)
        audioEngine.playPositiveEventSound()

        audioEngine.release()
    }
}
