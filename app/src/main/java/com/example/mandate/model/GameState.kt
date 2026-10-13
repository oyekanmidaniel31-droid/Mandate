package com.example.mandate.model

data class StateElectoralResult(
    val stateName: String,
    val electoralVotes: Int,
    val playerVotes: Long,
    val opponentVotes: Long,
    val isDeclaredForPlayer: Boolean
)

data class ElectionTally(
    val playerCandidateName: String,
    val opponentCandidateName: String,
    val runningMateName: String,
    val playerTotalElectoral: Int,
    val opponentTotalElectoral: Int,
    val playerPopularVotes: Long,
    val opponentPopularVotes: Long,
    val stateResults: List<StateElectoralResult>,
    val isConcluded: Boolean,
    val tribunalVerdictPending: Boolean = false,
    val wonElection: Boolean = false
)

data class GameState(
    val character: Character = Character(),
    val macroEconomy: MacroEconomy = MacroEconomy(),
    val padis: List<Padi> = emptyList(),
    val familyMembers: List<FamilyMember> = emptyList(),
    val blackTaxQueue: List<BlackTaxRequest> = emptyList(),
    val chatThreads: List<ChatThread> = emptyList(),
    val chirpFeed: List<ChirpPost> = emptyList(),
    val bankTransactions: List<BankTransaction> = emptyList(),
    val newsFlashes: List<NewsFlash> = emptyList(),
    val currentEvent: GameEvent? = null,
    val eventHistory: List<String> = emptyList(),
    val isOriginEngineActive: Boolean = true,
    val isOriginDossierReady: Boolean = false,
    val originQuizIndex: Int = 0,
    val originScores: Map<String, Int> = mapOf(
        "ambition" to 50,
        "empathy" to 50,
        "risk" to 50,
        "loyalty" to 50,
        "cunning" to 50
    ),
    val electionTally: ElectionTally? = null,
    val funeralSummary: FuneralSummary? = null,
    val lastOutcomeMessage: String? = null,
    val activeNavTab: String = "LIFE",
    val showPhoneSheet: Boolean = false,
    val phoneActiveApp: String = "CHIRP", // "CHIRP", "CHAT", "BANK", "NEWS"
    val totalOwambeSprayed: Long = 0L,
    val showRelationshipOverlay: Boolean = false,
    val relationshipFilter: String = "ALL", // "ALL", "FAMILY", "PADI", "AT_RISK"
    val openWorldState: OpenWorldState = OpenWorldState(),
    val playerResources: PlayerResources = PlayerResources(),
    val isSfxEnabled: Boolean = true
)
