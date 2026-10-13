package com.example.mandate.data

import com.example.mandate.model.BlackTaxRequest
import com.example.mandate.model.FamilyMember
import com.example.mandate.model.FamilyStructure
import com.example.mandate.model.KinshipRole

object FamilyGenerator {
    fun generateFamily(structure: FamilyStructure): List<FamilyMember> {
        val baseMembers = mutableListOf(
            FamilyMember(
                id = "fam_mom",
                name = "Mama Nkechi (Biological Mother)",
                role = KinshipRole.MOTHER,
                affection = 90,
                demands = "Prays for you 3 times a day: 'My child will sit in the Villa!'"
            )
        )

        when (structure) {
            FamilyStructure.POLYGAMOUS -> {
                baseMembers.addAll(
                    listOf(
                        FamilyMember(
                            id = "fam_dad",
                            name = "Pa Alabi Balogun (Late Patriarch)",
                            role = KinshipRole.FATHER,
                            affection = 60,
                            demands = "Demanded that only his first son control the ancestral land in the village.",
                            isAlive = false
                        ),
                        FamilyMember(
                            id = "fam_cowife",
                            name = "Iya Segun (First Wife / Rival Stepmother)",
                            role = KinshipRole.CO_WIFE,
                            affection = 30,
                            demands = "Always watching your progress with side-eye: 'Why should her son shine while mine is struggling?'"
                        ),
                        FamilyMember(
                            id = "fam_halfsib1",
                            name = "Fatai Balogun (Half-Brother)",
                            role = KinshipRole.HALF_SIBLING,
                            affection = 45,
                            demands = "Demands you pay for his overseas UK Masters degree visa deposit."
                        ),
                        FamilyMember(
                            id = "fam_halfsib2",
                            name = "Kudirat Balogun (Half-Sister)",
                            role = KinshipRole.HALF_SIBLING,
                            affection = 70,
                            demands = "Loyal ally who manages family gossip and alerts you to plots."
                        )
                    )
                )
            }
            FamilyStructure.MONOGAMOUS -> {
                baseMembers.addAll(
                    listOf(
                        FamilyMember(
                            id = "fam_dad",
                            name = "Chief S. O. Balogun (Father)",
                            role = KinshipRole.FATHER,
                            affection = 75,
                            demands = "Strict retired principal: 'Anything less than Distinction is unacceptable!'"
                        ),
                        FamilyMember(
                            id = "fam_sibling",
                            name = "Tope Balogun (Younger Sister)",
                            role = KinshipRole.FULL_SIBLING,
                            affection = 85,
                            demands = "Needs capital to launch her high-end fashion boutique on Admiralty Way."
                        )
                    )
                )
            }
            FamilyStructure.SINGLE_PARENT -> {
                baseMembers.add(
                    FamilyMember(
                        id = "fam_uncle",
                        name = "Elder Obinna (Maternal Uncle)",
                        role = KinshipRole.VILLAGE_ELDER,
                        affection = 55,
                        demands = "Constant calls from the village: 'Your mother suffered alone, now it is your turn to build our village town hall!'"
                    )
                )
            }
        }
        return baseMembers
    }

    fun generateInitialBlackTaxRequests(): List<BlackTaxRequest> {
        return listOf(
            BlackTaxRequest(
                id = "tax_1",
                fromMemberName = "Elder Obinna (Maternal Uncle)",
                reason = "Village Compound Generator Breakdown",
                amountRequested = 45_000,
                pidginPlea = "My child, light don disappear for village since two weeks! Your cousins dey sleep inside heat. Send small ₦45k for Tiger gen.",
                consequencesIfIgnored = "Uncle tells entire village council you have grown wings and forgotten your ancestors."
            ),
            BlackTaxRequest(
                id = "tax_2",
                fromMemberName = "Fatai Balogun (Half-Brother)",
                reason = "Police Bail at Maroko Station",
                amountRequested = 120_000,
                pidginPlea = "Bros abeg! SARS stop my okada, dem say I dey do crypto. DPO talk say if ₦120k no land by 8pm, dem go remand me for Kirikiri!",
                consequencesIfIgnored = "Rival step-mother curses your career on the family WhatsApp group with voice notes."
            )
        )
    }
}
