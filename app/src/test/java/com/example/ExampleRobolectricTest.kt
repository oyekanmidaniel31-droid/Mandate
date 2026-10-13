package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.mandate.data.GameRepository
import com.example.mandate.data.OriginEngineData
import com.example.mandate.model.CrisisType
import com.example.mandate.model.LifeStage
import com.example.mandate.model.MacroEconomy
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Mandate", appName)
    }

    @Test
    fun `origin engine questions count and structure`() {
        val questions = OriginEngineData.questions
        assertEquals(7, questions.size)
        assertTrue(questions.all { it.options.isNotEmpty() })
        // Verify narrative questions test ambition and empathy explicitly
        assertTrue(questions.any { it.options.any { opt -> opt.traitEffects.containsKey("ambition") } })
        assertTrue(questions.any { it.options.any { opt -> opt.traitEffects.containsKey("empathy") } })
    }

    @Test
    fun `game repository initializes character properly from scores`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = GameRepository(context)

        val scores = mapOf(
            "ambition" to 85,
            "empathy" to 40,
            "risk" to 90,
            "loyalty" to 60,
            "cunning" to 75
        )

        val state = repository.initializeFromOriginScores(scores)
        assertNotNull(state.character)
        assertTrue(state.character.naraBalance > 0)
        assertEquals(LifeStage.CAMPUS, state.character.currentStage)
        assertEquals(5, state.padis.size)
        assertFalse(state.isOriginEngineActive)
    }

    @Test
    fun `turn progression advances age and year`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repository = GameRepository(context)

        val initialState = repository.initializeFromOriginScores(
            mapOf("ambition" to 50, "empathy" to 50, "risk" to 50, "loyalty" to 50, "cunning" to 50)
        )

        val startingAge = initialState.character.age
        val startingYear = initialState.character.year

        val nextState = repository.advanceLifeTurn(initialState)
        assertEquals(startingAge + 2, nextState.character.age)
        assertEquals(startingYear + 2, nextState.character.year)
        assertTrue(nextState.macroEconomy.activeCrises.isNotEmpty())
    }

    @Test
    fun `persistent anger index and national stability calculations`() {
        val macro = MacroEconomy(angerIndex = 65)
        assertEquals(35, macro.nationalStabilityPercentage)
        assertEquals(5, macro.sectors.size)
        assertTrue(macro.sectors.any { it.sectorName.contains("Students") })
        assertTrue(macro.sectors.any { it.sectorName.contains("Danfo") })
        assertTrue(macro.activeCrises.contains(CrisisType.FUEL_SCARCITY))
        assertTrue(macro.activeCrises.all { it.angerImpact > 0 })
    }
}
