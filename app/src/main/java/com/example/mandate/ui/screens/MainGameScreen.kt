package com.example.mandate.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.EventChoice
import com.example.mandate.model.GameEvent
import com.example.mandate.model.GameState
import com.example.mandate.model.LifeStage
import com.example.mandate.ui.components.PoliticalDashboardUI
import com.example.mandate.ui.components.PowerStandoffArena
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

data class QuickActionSpec(
    val title: String,
    val cost: Long,
    val emoji: String,
    val desc: String,
    val reward: String
)

@Composable
fun MainGameScreen(
    state: GameState,
    onChoiceSelected: (EventChoice) -> Unit,
    onAdvanceTurn: () -> Unit,
    onOpenPhone: () -> Unit,
    onQuickAction: (String) -> Unit,
    onExecutePatronage: (Long, Int, String) -> Unit = { _, _, _ -> },
    onDisburseFamilyGrant: (Long) -> Unit = {},
    onOpenBDC: () -> Unit = {},
    onToggleSfx: () -> Unit = {},
    isSfxEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val event = state.currentEvent
    var pendingQuickAction by remember { mutableStateOf<String?>(null) }
    var activeMainTab by remember { mutableStateOf("DASHBOARD") }

    // Interactive Tactical Quick-Action Modal
    pendingQuickAction?.let { actionKey ->
        val spec = when (actionKey) {
            "street_rally" -> QuickActionSpec("Street Youth Rally", 50_000L, "🎤", "Mobilize over 5,000 energetic grassroots youths with sound trucks across major motor-parks.", "+25% Street Clout 💥")
            "godfather_meeting" -> QuickActionSpec("Godfather Boardroom", 120_000L, "💼", "Enter the guarded private salon of kingmakers for secret power alignments and campaign pledges.", "+25% Political Power 🏛️")
            "open_world" -> QuickActionSpec("Roam Lagos Open World", 0L, "🚌", "Free roam across interconnected Lagos districts (Oshodi, Computer Village, VI, Lekki). Board Danfos, eat at Mama Put, engage street hustles.", "Free Exploration 🗺️")
            "owambe_spraying" -> QuickActionSpec("Owambe Cash Spray", 150_000L, "🍾", "Spray mint currency bundles on VIP dignitaries & praise singers to boost dynasty clout and blow off steam.", "-20% Stress, +30% Clout 💸")
            "security_convoy" -> QuickActionSpec("Police Tactical Escort", 300_000L, "🚔", "Hire armed sirens escort & bulletproof Hilux vehicles to protect against mob brawls & ambush.", "Bodyguard Level +1 🛡️")
            else -> QuickActionSpec("VIP Medical Flight", 450_000L, "✈️", "Charter an executive private jet to London/Dubai private clinic for total medical rejuvenation.", "+30% Health Recovery 🏥")
        }

        AlertDialog(
            onDismissRequest = { pendingQuickAction = null },
            containerColor = EmeraldSurfaceVariant,
            titleContentColor = NaijaGold,
            textContentColor = TextPrimaryLight,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = spec.emoji, fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TACTICAL ACTION ORDER",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = NaijaGold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = spec.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimaryLight
                    )
                    Text(
                        text = spec.desc,
                        fontSize = 12.sp,
                        color = TextSecondaryLight,
                        lineHeight = 16.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF10281C),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NaijaGreenLight))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "DEPLOYMENT COST", fontSize = 9.sp, color = TextSecondaryLight)
                                Text(
                                    text = "₦${numberFormat.format(spec.cost)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (state.character.naraBalance >= spec.cost) NaijaGoldLight else AlertRed
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF1A3D28)
                            ) {
                                Text(
                                    text = spec.reward,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaijaGoldLight,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }

                    if (state.character.naraBalance < spec.cost) {
                        Text(
                            text = "⚠️ INSUFFICIENT NARA BALANCE: You need ₦${numberFormat.format(spec.cost - state.character.naraBalance)} more.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onQuickAction(actionKey)
                        pendingQuickAction = null
                    },
                    enabled = if (actionKey == "owambe_spraying") true else (state.character.naraBalance >= spec.cost && state.character.energy >= 15),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (actionKey == "owambe_spraying") NaijaGold else NaijaGreenPrimary,
                        contentColor = if (actionKey == "owambe_spraying") DeepCharcoal else NaijaGoldLight
                    ),
                    modifier = Modifier.testTag("confirm_action_btn")
                ) {
                    Icon(
                        imageVector = if (actionKey == "owambe_spraying") Icons.Default.Celebration else Icons.Default.Bolt,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (actionKey == "owambe_spraying") "ENTER 3D OWAMBE PARTY 🍾" else "EXECUTE MANEUVER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingQuickAction = null }) {
                    Text(text = "STAND DOWN", color = TextSecondaryLight, fontSize = 12.sp)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("main_game_screen")
    ) {
        // Executive Top Segmented Control: Dashboard vs Story Flashpoints
        Surface(
            color = EmeraldSurfaceVariant,
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorderGold.copy(alpha = 0.5f))),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = if (activeMainTab == "DASHBOARD") NaijaGold else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeMainTab = "DASHBOARD" }
                        .testTag("tab_switch_dashboard")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📊 COMMAND DASHBOARD",
                            color = if (activeMainTab == "DASHBOARD") DeepCharcoal else TextPrimaryLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Surface(
                    color = if (activeMainTab == "STORY") NaijaGold else Color.Transparent,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeMainTab = "STORY" }
                        .testTag("tab_switch_story")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (event != null) "⚡ STORY FLASHPOINT (!)" else "📜 STORY FLASHPOINTS",
                            color = if (activeMainTab == "STORY") DeepCharcoal else (if (event != null) AlertRed else TextPrimaryLight),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        if (activeMainTab == "DASHBOARD") {
            PoliticalDashboardUI(
                state = state,
                onJumpToStoryFlashpoint = { activeMainTab = "STORY" },
                onOpenPhone = onOpenPhone,
                onInspectTension = { /* Inspect tension dialog */ },
                onOpenBDC = onOpenBDC,
                onExecutePatronage = onExecutePatronage,
                onDisburseFamilyGrant = onDisburseFamilyGrant,
                onToggleSfx = onToggleSfx,
                isSfxEnabled = isSfxEnabled,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
        // High-Impact Graphical Hero Scene Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
            ) {
                // Dynamic contextual artwork based on national tension & stage
                val (bannerRes, sceneTag, tagColor) = when {
                    state.macroEconomy.angerIndex >= 70 -> Triple(
                        R.drawable.img_protest_street_1791644444827,
                        "🚨 STREET UNREST FLASHPOINT",
                        AlertRed
                    )
                    state.character.currentStage == LifeStage.THE_VILLA -> Triple(
                        R.drawable.img_villa_gate_1791640975131,
                        "🏛️ PRESIDENTIAL VILLA (SEAT OF POWER)",
                        NaijaGold
                    )
                    state.character.currentStage == LifeStage.STATE_POWER ||
                    state.character.currentStage == LifeStage.LOCAL_POWER -> Triple(
                        R.drawable.img_power_meeting_1791644463952,
                        "💼 COUNCIL OF GODFATHERS & CHIEFTAINS",
                        Color(0xFF2E7D32)
                    )
                    else -> Triple(
                        R.drawable.img_hero_naija_1791640719865,
                        "🏙️ LAKOS MEGACITY STREETS",
                        NaijaGreenPrimary
                    )
                }

                Image(
                    painter = painterResource(id = bannerRes),
                    contentDescription = sceneTag,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dramatic Dark Vignette
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0x990A1A12),
                                    Color(0xFA091811)
                                )
                            )
                        )
                )

                // Top Floating Status Badges
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xDD000000)
                    ) {
                        Text(
                            text = "YEAR ${state.character.year}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Bottom Scene Banner Text
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = tagColor
                    ) {
                        Text(
                            text = sceneTag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${state.character.currentStage.displayName.uppercase()} (${state.character.currentStage.ageRange})",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryLight
                    )
                }
            }
        }

        // DYNAMIC POWER STANDOFF ARENA (TUG-OF-WAR GRAPHIC)
        item {
            PowerStandoffArena(
                character = state.character,
                macroEconomy = state.macroEconomy,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }

        // Action Feedback / Previous Consequence Alert
        state.lastOutcomeMessage?.let { outcome ->
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF143022),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NaijaGold))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = NaijaGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = outcome,
                            fontSize = 12.sp,
                            color = TextPrimaryLight,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // TACTICAL QUICK-ACTION OPERATIONS WHEEL
        item {
            Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FlashOn,
                            contentDescription = null,
                            tint = NaijaGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "EXECUTIVE ACTION COMMANDS",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "Energy: ${state.character.energy}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGoldLight
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Horizontal Carousel of Graphic Action Cards
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        QuickActionCard(
                            title = "Roam Lagos World",
                            costLabel = "Free",
                            effectBadge = "3D Open World 🗺️",
                            iconEmoji = "🚌",
                            cardColor = Color(0xFF1B3D2F),
                            onClick = { pendingQuickAction = "open_world" }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Street Youth Rally",
                            costLabel = "₦50k",
                            effectBadge = "+25% Clout 💥",
                            iconEmoji = "🎤",
                            cardColor = Color(0xFF1A3826),
                            onClick = { pendingQuickAction = "street_rally" }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Godfather Boardroom",
                            costLabel = "₦120k",
                            effectBadge = "+25% Power 🏛️",
                            iconEmoji = "💼",
                            cardColor = Color(0xFF26331A),
                            onClick = { pendingQuickAction = "godfather_meeting" }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Owambe Cash Spray",
                            costLabel = "₦150k",
                            effectBadge = "-20% Stress 🍾",
                            iconEmoji = "💸",
                            cardColor = Color(0xFF332A18),
                            onClick = { pendingQuickAction = "owambe_spraying" }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "Police Tactical Escort",
                            costLabel = "₦300k",
                            effectBadge = "Guard Lv+1 🛡️",
                            iconEmoji = "🚔",
                            cardColor = Color(0xFF192938),
                            onClick = { pendingQuickAction = "security_convoy" }
                        )
                    }
                    item {
                        QuickActionCard(
                            title = "VIP Medical Flight",
                            costLabel = "₦450k",
                            effectBadge = "+30% Health 🏥",
                            iconEmoji = "✈️",
                            cardColor = Color(0xFF331922),
                            onClick = { pendingQuickAction = "medical_rejuvenation" }
                        )
                    }
                }
            }
        }

        // Active Story Dilemma / Event Card
        if (event != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .testTag("current_event_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorderGold))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = NaijaGreenDark
                            ) {
                                Text(
                                    text = event.category.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaijaGold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Text(
                                text = "STORY FLASHPOINT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = event.headline,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryLight,
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = event.narration,
                            fontSize = 13.sp,
                            lineHeight = 19.sp,
                            color = TextPrimaryLight
                        )

                        event.pidginQuote?.let { quote ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F261B),
                                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF1E4530)))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Campaign,
                                        contentDescription = null,
                                        tint = NaijaGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = quote,
                                        fontSize = 12.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = NaijaGoldLight,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action Choice Cards with Visual Badges
            items(event.options) { choice ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .testTag("event_choice_${choice.id}")
                        .clickable { onChoiceSelected(choice) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF284836)))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF133624),
                                    modifier = Modifier.size(22.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = NaijaGold,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = choice.label,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                            }
                            if (choice.costNara > 0) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF331515)
                                ) {
                                    Text(
                                        text = "-₦${numberFormat.format(choice.costNara)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AlertRed,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            } else if (choice.costNara < 0) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF132B1A)
                                ) {
                                    Text(
                                        text = "+₦${numberFormat.format(-choice.costNara)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = choice.description,
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Visual Effect Tags
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (choice.ambitionDelta != 0) {
                                EffectPill(
                                    label = "Ambition",
                                    delta = choice.ambitionDelta,
                                    color = NaijaGold
                                )
                            }
                            if (choice.cloutDelta != 0) {
                                EffectPill(
                                    label = "Clout",
                                    delta = choice.cloutDelta,
                                    color = NaijaGoldLight
                                )
                            }
                            if (choice.politicalCapitalDelta != 0) {
                                EffectPill(
                                    label = "Power",
                                    delta = choice.politicalCapitalDelta,
                                    color = InfoBlue
                                )
                            }
                            if (choice.conscienceDelta != 0) {
                                EffectPill(
                                    label = "Conscience",
                                    delta = choice.conscienceDelta,
                                    color = if (choice.conscienceDelta > 0) SuccessGreen else AlertRed
                                )
                            }
                        }
                    }
                }
            }
        }

        // Big Action Launchpad: Advance 2 Years in Naijaland
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF132D1F)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NaijaGold))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timelapse,
                                contentDescription = null,
                                tint = NaijaGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "LIFECYCLE ADVANCEMENT",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaijaGold
                            )
                        }
                        Text(
                            text = "Next Era: Age ${state.character.age + 2} (${state.character.year + 2})",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimaryLight
                        )
                    }

                    Button(
                        onClick = onAdvanceTurn,
                        modifier = Modifier.testTag("advance_turn_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.FastForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Advance 2 Years", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(60.dp)) }
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    costLabel: String,
    effectBadge: String,
    iconEmoji: String,
    cardColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(145.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2F4E3A)))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = iconEmoji, fontSize = 20.sp)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0x66000000)
                ) {
                    Text(
                        text = costLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGoldLight,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimaryLight,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = effectBadge,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGoldLight
            )
        }
    }
}

@Composable
private fun EffectPill(
    label: String,
    delta: Int,
    color: Color
) {
    val sign = if (delta > 0) "+$delta" else "$delta"
    Surface(
        shape = RoundedCornerShape(4.dp),
        color = Color(0xFF132A1C)
    ) {
        Text(
            text = "$label $sign",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
