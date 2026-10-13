package com.example.mandate.model

data class EventChoice(
    val id: String,
    val label: String,
    val description: String,
    val costNara: Long = 0,
    val energyCost: Int = 10,
    val ambitionDelta: Int = 0,
    val conscienceDelta: Int = 0,
    val cloutDelta: Int = 0,
    val politicalCapitalDelta: Int = 0,
    val healthDelta: Int = 0,
    val stressDelta: Int = 0,
    val riskAppetiteDelta: Int = 0,
    val angerIndexDelta: Int = 0,
    val godfatherFavorDelta: Int = 0,
    val outcomeNarrative: String,
    val isFatalRisk: Boolean = false,
    val fatalProbability: Double = 0.0
)

data class GameEvent(
    val id: String,
    val stage: LifeStage,
    val category: String,
    val headline: String,
    val narration: String,
    val pidginQuote: String? = null,
    val contextTag: String = "DECISION",
    val options: List<EventChoice>
)
