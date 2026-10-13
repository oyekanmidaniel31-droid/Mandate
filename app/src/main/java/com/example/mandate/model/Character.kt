package com.example.mandate.model

enum class OriginBackground(
    val title: String,
    val description: String,
    val initialCash: Long,
    val initialClout: Int,
    val initialGodfatherFavor: Int
) {
    STREET_HUSTLER(
        title = "Street Hustler's Child",
        description = "Raised in the trenches of Ajegunle. High street sense, zero silver spoon.",
        initialCash = 15_000,
        initialClout = 35,
        initialGodfatherFavor = 10
    ),
    VILLAGE_FARMER(
        title = "Village Farmer's Child",
        description = "Grew up among the cocoa groves. Deep ancestral roots and resilient discipline.",
        initialCash = 25_000,
        initialClout = 15,
        initialGodfatherFavor = 5
    ),
    OIL_REGION(
        title = "Oil Creek Family",
        description = "Born in the Niger delta where flare stacks burn. Militant contacts and raw resource grit.",
        initialCash = 80_000,
        initialClout = 25,
        initialGodfatherFavor = 20
    ),
    DYNASTY_HEIR(
        title = "Dynasty Heir",
        description = "Child of a former military administrator and oil baron. Wealthy but drowning in scrutiny.",
        initialCash = 5_000_000,
        initialClout = 50,
        initialGodfatherFavor = 60
    ),
    DIASPORA_RETURNEE(
        title = "Diaspora Returnee",
        description = "Schooled in Manchester/London, back with fresh ideas, tech skills, and an accent people mock.",
        initialCash = 1_200_000,
        initialClout = 20,
        initialGodfatherFavor = 15
    )
}

enum class FamilyStructure(val title: String, val dynamicDescription: String) {
    POLYGAMOUS(
        title = "Polygamous Home",
        dynamicDescription = "2 rival co-wives, 7 half-siblings competing for school fees and inheritance."
    ),
    MONOGAMOUS(
        title = "Monogamous Home",
        dynamicDescription = "Sole spotlight. Extreme parental pressure: 'You must carry the family name!'"
    ),
    SINGLE_PARENT(
        title = "Single-Mother Household",
        dynamicDescription = "Mother sacrificed market profits to send you to school. Deep filial debt."
    )
}

enum class FamilySecret(val title: String, val detail: String) {
    GODFATHER_BLOODLINE(
        title = "Godfather's Secret Blood",
        detail = "Your real biological father is Alhaji Danbaba, the reigning political godfather of the North-West."
    ),
    STOLEN_LAND_SURVEY(
        title = "Forged Land Deed",
        detail = "Your family's compound rests on government reservation land registered with a forged 1982 C-of-O."
    ),
    EXILED_COUP_UNCLE(
        title = "Uncle in Exile",
        detail = "Your uncle was a Major implicated in the 1990 failed radio broadcast coup, currently hiding in Ghana."
    ),
    FORGED_WAEC(
        title = "Miracle Center WAEC",
        detail = "Your secondary school credits were sorted at an infamous miracle examination center in Benin."
    ),
    SWISS_SAFE_KEY(
        title = "Grandfather's Brass Key",
        detail = "An old ledger and safety deposit box key in Zurich belonging to your late civil servant grandfather."
    )
}

enum class AcademicCourse(
    val title: String,
    val skillBonus: String,
    val politicalEdge: String
) {
    LAW("Law", "Tribunals & Loopholes", "Crushes election petitions and drafts bulletproof decrees"),
    MEDICINE("Medicine", "Public Trust & Health Crises", "Command high moral authority during epidemics & bio-crises"),
    ENGINEERING("Engineering", "Projects & Contracts", "Builds visible flyovers, bridges, and controls public works kickbacks"),
    MASS_COMM("Mass Communication", "Media Spin & Press Control", "Controls news headlines, spins scandals, and weaponizes talk radio"),
    ACCOUNTING("Accounting", "Audits & Ghost Budgets", "Finds hidden billions, audits political enemies, balances slush funds"),
    COMPUTER_SCIENCE("Computer Science", "Online Influence & Cyber Ops", "Commands troll armies, cyber leaks, and data-driven election polling"),
    STREET_HUSTLE("Street Hustle & Trade", "Street Clout & Agbero Loyalty", "Direct control of the motor parks, market unions, and street rallies")
}

enum class LifeStage(val displayName: String, val ageRange: String) {
    ORIGIN("Childhood & Dilemmas", "Ages 6-17"),
    CAMPUS("Campus & Student Union", "Ages 18-23"),
    HUSTLE_YEARS("NYSC & The Street Hustle", "Ages 24-30"),
    LOCAL_POWER("Ward to Local Govt Council", "Ages 31-42"),
    STATE_POWER("State Assembly & Governor", "Ages 43-54"),
    NATIONAL_POWER("Senate & Federal Ministry", "Ages 55-64"),
    THE_RACE("The Presidential Primaries & Race", "Age 65+"),
    THE_VILLA("The Seat of Power (The Villa)", "The Mandate"),
    FUNERAL("The Funeral & Legacy", "Eternity")
}

data class Character(
    val name: String = "Adeolu 'Jagaban' Balogun",
    val alias: String = "Senior Man",
    val gender: String = "Male",
    val age: Int = 18,
    val year: Int = 2005,
    val stateOfOrigin: String = "Lakos State",
    val neighbourhood: String = "Ajegunle",
    val religion: String = "Christian",
    val background: OriginBackground = OriginBackground.STREET_HUSTLER,
    val familyType: FamilyStructure = FamilyStructure.POLYGAMOUS,
    val familySecret: FamilySecret = FamilySecret.GODFATHER_BLOODLINE,
    
    // Core Psychological Profile (0-100)
    val ambition: Int = 75,
    val empathy: Int = 50,
    val riskAppetite: Int = 80,
    val loyalty: Int = 60,
    val cunning: Int = 70,

    // Life Vitals
    val health: Int = 90,
    val stress: Int = 30,
    val energy: Int = 100,
    val naraBalance: Long = 45_000,
    
    // Power & Clout Metrics
    val politicalCapital: Int = 20,
    val streetClout: Int = 40,
    val godfatherFavor: Int = 30,
    val conscience: Int = 55, // 0 = Cold Machiavellian, 100 = Saintly Reformer

    // Career & Role
    val currentStage: LifeStage = LifeStage.CAMPUS,
    val academicCourse: AcademicCourse? = AcademicCourse.MASS_COMM,
    val politicalOffice: String = "Student Union Director of Socials",
    val partyAffiliation: String = "Labour Vanguard (LV)",
    val bodyguardLevel: Int = 1,
    val healthAccess: String = "General Hospital",
    
    // Status
    val isAlive: Boolean = true,
    val causeOfDeath: String? = null,
    val generationNumber: Int = 1
)
