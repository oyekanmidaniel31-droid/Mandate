package com.example

import com.example.mandate.model.*
import org.junit.Assert.*
import org.junit.Test

class RelationshipModelTest {

    @Test
    fun testFamilyMemberDefaultHappinessAndLoyalty() {
        val mother = FamilyMember(
            id = "fam_mom",
            name = "Mama Nkechi",
            role = KinshipRole.MOTHER,
            affection = 90,
            demands = "Prays for you 3 times a day"
        )

        assertEquals(90, mother.happiness)
        assertEquals(95, mother.loyalty)

        val record = mother.toTrackRecord()
        assertTrue(record.isFamily)
        assertEquals(RelationshipTier.DEVOTED_ALLIANCE, record.tier)
        assertEquals("😊", record.moodEmoji)
        assertEquals("🛡️", record.loyaltyEmoji)
    }

    @Test
    fun testCoWifeRivalryDetection() {
        val coWife = FamilyMember(
            id = "fam_cowife",
            name = "Iya Segun",
            role = KinshipRole.CO_WIFE,
            affection = 30,
            demands = "Eyeing inheritance shares"
        )

        assertEquals(30, coWife.happiness)
        assertEquals(35, coWife.loyalty)

        val record = coWife.toTrackRecord()
        // With low loyalty and rival stepmother role, tier is BITTER_RIVAL
        assertEquals(RelationshipTier.BITTER_RIVAL, record.tier)
        assertEquals("😟", record.moodEmoji)
        assertEquals("⚠️", record.loyaltyEmoji)
    }

    @Test
    fun testPadiHappinessAndLoyaltyMeters() {
        val rideOrDie = Padi(
            id = "padi_1",
            name = "Segun Adeleke",
            nickname = "Segun Ride-or-Die",
            archetype = PadiArchetype.RIDE_OR_DIE,
            loyalty = 85,
            netWorth = 100_000,
            occupation = "Motor-Park Syndicate Organizer",
            bio = "Childhood friend from the trenches"
        )

        assertEquals(85, rideOrDie.happiness)
        assertEquals(85, rideOrDie.loyalty)

        val record = rideOrDie.toTrackRecord()
        assertFalse(record.isFamily)
        assertEquals(RelationshipTier.DEVOTED_ALLIANCE, record.tier)
        assertEquals("Unshakable Ride-or-Die", record.loyaltyLabel)
    }

    @Test
    fun testRelationshipTierCalculations() {
        // High scores -> DEVOTED_ALLIANCE
        assertEquals(
            RelationshipTier.DEVOTED_ALLIANCE,
            RelationshipTier.fromScores(happiness = 85, loyalty = 85)
        )

        // Moderate scores -> WARM_CONFIDANT
        assertEquals(
            RelationshipTier.WARM_CONFIDANT,
            RelationshipTier.fromScores(happiness = 75, loyalty = 70)
        )

        // Low happiness but solid loyalty -> TRANSACTIONAL
        assertEquals(
            RelationshipTier.TRANSACTIONAL,
            RelationshipTier.fromScores(happiness = 42, loyalty = 60)
        )

        // Low both -> STRAINED_SUSPICIOUS
        assertEquals(
            RelationshipTier.STRAINED_SUSPICIOUS,
            RelationshipTier.fromScores(happiness = 35, loyalty = 35)
        )

        // Estranged override
        assertEquals(
            RelationshipTier.ESTRANGED,
            RelationshipTier.fromScores(happiness = 70, loyalty = 70, isEstranged = true)
        )
    }
}
