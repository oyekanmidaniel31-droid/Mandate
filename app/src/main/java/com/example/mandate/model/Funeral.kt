package com.example.mandate.model

data class FuneralSummary(
    val deceasedName: String,
    val deceasedAge: Int,
    val finalOffice: String,
    val causeOfDeath: String,
    val newspaperHeadline: String,
    val newspaperSubhead: String,
    val newspaperEditorialSnippet: String,
    val legacyTitle: String,
    val legacyScore: Int,
    val attendeesList: List<String>,
    val boycottersList: List<String>,
    val willContestDispute: String,
    val survivingHeirName: String,
    val survivingEstateNara: Long
)
