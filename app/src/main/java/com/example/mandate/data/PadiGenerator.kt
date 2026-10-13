package com.example.mandate.data

import com.example.mandate.model.Padi
import com.example.mandate.model.PadiArchetype

object PadiGenerator {
    fun generateInitialPadis(): List<Padi> {
        return listOf(
            Padi(
                id = "padi_1",
                name = "Segun Adeleke",
                nickname = "Segun Ride-or-Die",
                archetype = PadiArchetype.RIDE_OR_DIE,
                loyalty = 85,
                netWorth = 120_000,
                occupation = "Motor-Park Syndicate Organizer",
                bio = "Childhood friend from the trenches. Never graduated, but commands 200 bus conductors with one whistle.",
                memoryOfPlayer = "Remembers when you paid his hospital bill after a broken bottle melee at Oshodi."
            ),
            Padi(
                id = "padi_2",
                name = "Tari Briggs",
                nickname = "Tari Billionaire",
                archetype = PadiArchetype.RICH_PADI,
                loyalty = 60,
                netWorth = 25_000_000,
                occupation = "Offshore Marine Contractor & Club Promotor",
                bio = "Father owns oil bunkering tugs in Bonny Creek. Lives between Ikoyi and Dubai penthouses. Sprays mint ₦1000 notes.",
                memoryOfPlayer = "Remembers when you vouched for his offshore pipeline supply contract in front of the board."
            ),
            Padi(
                id = "padi_3",
                name = "Comrade Chioma Nwosu",
                nickname = "Iron Lady",
                archetype = PadiArchetype.ACTIVIST,
                loyalty = 70,
                netWorth = 80_000,
                occupation = "Civil Rights Attorney & #EndWahala Organizer",
                bio = "Fierce investigative blogger and human rights lawyer. Will march into tear gas with her head held high.",
                memoryOfPlayer = "Watched you speak during the campus SUG assembly and believes you might actually save the country."
            ),
            Padi(
                id = "padi_4",
                name = "Kunle 'Gist' Oladipo",
                nickname = "InstaGist King",
                archetype = PadiArchetype.GOSSIP_BLOGGER,
                loyalty = 50,
                netWorth = 650_000,
                occupation = "Viral Paparazzi & Gossip Blog Editor",
                bio = "Runs the notorious 'Lakos Night Gossip' channel with 1.8M followers. Trades secrets like treasury bills.",
                memoryOfPlayer = "Has three spicy photos of you at a private Victoria Island yacht party stored in an encrypted drive."
            ),
            Padi(
                id = "padi_5",
                name = "Bashir Bello",
                nickname = "The Prince of Fagge",
                archetype = PadiArchetype.SNAKE_RIVAL,
                loyalty = 40,
                netWorth = 4_500_000,
                occupation = "Party Youth Wing Secretary & Grain Merchant",
                bio = "Silky tongue, impeccable babanriga kaftan, cold calculating eyes. Wants the exact same legislative seat as you.",
                memoryOfPlayer = "Still bitter that the party elders gave you the microphone instead of him at the 2011 State Congress."
            )
        )
    }
}
