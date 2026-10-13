package com.example.mandate.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.*
import com.example.mandate.ui.components.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * Family & Clan screen featuring:
 * 1. 3D Isometric Living Space simulation (Sims-style Lagos compound with autonomous moving NPCs)
 * 2. Relationship Status Dossier with dual individual Happiness & Loyalty meters
 * 3. Traditional Clan Council Ledger & Black Tax demands
 */
@Composable
fun FamilyScreen(
    character: Character = Character(),
    familyMembers: List<FamilyMember>,
    padis: List<Padi> = emptyList(),
    blackTaxQueue: List<BlackTaxRequest>,
    onPayTax: (BlackTaxRequest) -> Unit,
    onIgnoreTax: (BlackTaxRequest) -> Unit,
    onSwitchHomeLayout: (FamilyStructure) -> Unit = {},
    onHomeProstrationBlessing: (HomeNPCEntity) -> Unit = {},
    onHomeRemitCash: (HomeNPCEntity, Long) -> Unit = { _, _ -> },
    onHomeShareMealOrDrink: (HomeNPCEntity) -> Unit = {},
    onHomeSeekCounselOrMobilize: (HomeNPCEntity) -> Unit = {},
    onHomeResolveRivalry: (HomeNPCEntity) -> Unit = {},
    onSendRelationshipGift: (String, Boolean, Long) -> Unit = { _, _, _ -> },
    onSpendRelationshipTime: (String, Boolean) -> Unit = { _, _ -> },
    onPledgeLoyaltyPact: (String, Boolean) -> Unit = { _, _ -> },
    onSettleRelationshipWahala: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    // Navigation sub-mode: "3D_SPACE", "LEDGER", "RELATIONSHIPS"
    var selectedViewMode by remember { mutableStateOf("3D_SPACE") }
    var cameraPerspective by remember { mutableStateOf(HomeCameraPerspective.COURTYARD_MAIN) }
    var selectedNpc by remember { mutableStateOf<HomeNPCEntity?>(null) }
    var showRelationshipOverlayModal by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("family_screen_root")
    ) {
        // TOP SEGMENTED SWITCHER: [ 🏡 3D Home Space | 📋 Clan Ledger | 👥 Relations Radar ]
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0C1B13),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenPrimary))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FamilyTabButton(
                    title = "🏡 3D Living Space",
                    subtitle = "Sims Compound",
                    selected = selectedViewMode == "3D_SPACE",
                    onClick = { selectedViewMode = "3D_SPACE" },
                    modifier = Modifier.weight(1.1f),
                    tag = "tab_home_3d"
                )
                FamilyTabButton(
                    title = "👥 Relations Dossier",
                    subtitle = "Meters & Tiers",
                    selected = selectedViewMode == "RELATIONSHIPS",
                    onClick = { selectedViewMode = "RELATIONSHIPS" },
                    modifier = Modifier.weight(1.1f),
                    tag = "tab_relationships"
                )
                FamilyTabButton(
                    title = "📋 Clan Ledger",
                    subtitle = if (blackTaxQueue.isNotEmpty()) "${blackTaxQueue.size} Tax Alerts" else "Demands",
                    selected = selectedViewMode == "LEDGER",
                    hasAlert = blackTaxQueue.isNotEmpty(),
                    onClick = { selectedViewMode = "LEDGER" },
                    modifier = Modifier.weight(1f),
                    tag = "tab_clan_ledger"
                )
            }
        }

        // CONTENT PANE ACCORDING TO SELECTED VIEW MODE
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedViewMode) {
                "3D_SPACE" -> {
                    // 3D Isometric Home Simulation Space
                    Box(modifier = Modifier.fillMaxSize()) {
                        HomeIsometric3DCanvas(
                            layoutType = character.familyType,
                            familyMembers = familyMembers,
                            padis = padis,
                            blackTaxQueue = blackTaxQueue,
                            selectedNpc = selectedNpc,
                            onSelectNpc = { npc ->
                                selectedNpc = npc
                            },
                            onTapFloor = { _, _ ->
                                selectedNpc = null
                            },
                            cameraPerspective = cameraPerspective,
                            onCameraChange = { cameraPerspective = it },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top Controls Overlay in 3D Canvas: Architecture & Radar button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.TopCenter)
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Layout Switcher (Compound vs Duplex)
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xDD0D1B13),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (character.familyType == FamilyStructure.POLYGAMOUS) "🏰 Polygamous Compound" else "🏢 Urban Duplex",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaijaGoldLight
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    TextButton(
                                        onClick = {
                                            val next = if (character.familyType == FamilyStructure.POLYGAMOUS)
                                                FamilyStructure.MONOGAMOUS else FamilyStructure.POLYGAMOUS
                                            onSwitchHomeLayout(next)
                                        },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text(text = "Switch", fontSize = 10.sp, color = NaijaGreenLight)
                                    }
                                }
                            }

                            // Relations Radar Quick Summon Button
                            FilledTonalButton(
                                onClick = { showRelationshipOverlayModal = true },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xE6143321),
                                    contentColor = NaijaGoldLight
                                ),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(32.dp)
                                    .testTag("open_relations_radar_from_3d")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Diversity1,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Relations Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Bottom NPC Engagement Modal Dial (when an NPC is clicked)
                        selectedNpc?.let { npc ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 6.dp)
                            ) {
                                HomeNPCEngagementSheet(
                                    npc = npc,
                                    playerBalance = character.naraBalance,
                                    onDismiss = { selectedNpc = null },
                                    onProstrationBlessing = {
                                        onHomeProstrationBlessing(it)
                                        selectedNpc = null
                                    },
                                    onRemitCash = { entity, amount ->
                                        onHomeRemitCash(entity, amount)
                                        selectedNpc = null
                                    },
                                    onShareMealOrDrink = {
                                        onHomeShareMealOrDrink(it)
                                        selectedNpc = null
                                    },
                                    onSeekCounselOrMobilize = {
                                        onHomeSeekCounselOrMobilize(it)
                                        selectedNpc = null
                                    },
                                    onResolveRivalry = {
                                        onHomeResolveRivalry(it)
                                        selectedNpc = null
                                    }
                                )
                            }
                        }
                    }
                }

                "RELATIONSHIPS" -> {
                    // Full Embedded Relationship Dossier with Dual Happiness & Loyalty Meters
                    RelationshipTrackerOverlay(
                        familyMembers = familyMembers,
                        padis = padis,
                        playerNara = character.naraBalance,
                        onClose = { selectedViewMode = "3D_SPACE" },
                        onSendGift = onSendRelationshipGift,
                        onSpendTime = onSpendRelationshipTime,
                        onPledgeLoyalty = onPledgeLoyaltyPact,
                        onSettleWahala = onSettleRelationshipWahala
                    )
                }

                "LEDGER" -> {
                    // Traditional Clan Council Ledger & Black Tax Queue
                    ClanLedgerView(
                        familyMembers = familyMembers,
                        blackTaxQueue = blackTaxQueue,
                        numberFormat = numberFormat,
                        onPayTax = onPayTax,
                        onIgnoreTax = onIgnoreTax,
                        onOpenRelationships = { selectedViewMode = "RELATIONSHIPS" },
                        onOpen3D = { selectedViewMode = "3D_SPACE" }
                    )
                }
            }

            // Quick Floating Relationship Overlay Modal if triggered from 3D space
            androidx.compose.animation.AnimatedVisibility(
                visible = showRelationshipOverlayModal,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
            ) {
                RelationshipTrackerOverlay(
                    familyMembers = familyMembers,
                    padis = padis,
                    playerNara = character.naraBalance,
                    onClose = { showRelationshipOverlayModal = false },
                    onSendGift = onSendRelationshipGift,
                    onSpendTime = onSpendRelationshipTime,
                    onPledgeLoyalty = onPledgeLoyaltyPact,
                    onSettleWahala = onSettleRelationshipWahala
                )
            }
        }
    }
}

@Composable
private fun FamilyTabButton(
    title: String,
    subtitle: String,
    selected: Boolean,
    hasAlert: Boolean = false,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (selected) NaijaGold else Color(0x66112519),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(if (selected) NaijaGoldLight else Color(0x332C4C38))
        ),
        modifier = modifier.testTag(tag)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selected) DeepCharcoal else Color.White
                )
                if (hasAlert) {
                    Spacer(modifier = Modifier.width(3.dp))
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(AlertRed)
                    )
                }
            }
            Text(
                text = subtitle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = if (selected) DeepCharcoal.copy(alpha = 0.8f) else NaijaGoldLight
            )
        }
    }
}

@Composable
private fun ClanLedgerView(
    familyMembers: List<FamilyMember>,
    blackTaxQueue: List<BlackTaxRequest>,
    numberFormat: NumberFormat,
    onPayTax: (BlackTaxRequest) -> Unit,
    onIgnoreTax: (BlackTaxRequest) -> Unit,
    onOpenRelationships: () -> Unit,
    onOpen3D: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("clan_ledger_list"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dramatic Clan Assembly Artwork Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_power_meeting_1791644463952),
                    contentDescription = "Family Council & Village Assembly",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0x990A1A12), Color(0xFA091811))
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = NaijaGold
                    ) {
                        Text(
                            text = "👑 PATRIARCHAL COUNCIL & KINSHIP WAR ROOM",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepCharcoal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "THE CLAN & EXTENDED FAMILY",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        // Black Tax Warning Queue
        if (blackTaxQueue.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PENDING BLACK TAX PLEAS (${blackTaxQueue.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = AlertRed
                    )
                    Text(
                        text = "Family guilt trip active",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
            }

            items(blackTaxQueue) { request ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF281010)),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(AlertRed))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = request.fromMemberName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = "₦${numberFormat.format(request.amountRequested)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = NaijaGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Reason: ${request.reason}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = WarningOrange
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1D0E0E)
                        ) {
                            Text(
                                text = "\"${request.pidginPlea}\"",
                                fontSize = 12.sp,
                                fontStyle = FontStyle.Italic,
                                color = TextPrimaryLight,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Penalty if ignored: ${request.consequencesIfIgnored}",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onIgnoreTax(request) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                            ) {
                                Text(text = "Ignore", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onPayTax(request) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = Color.White)
                            ) {
                                Text(text = "Send ₦ (Blessings)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Family Members List with DUAL METERS
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FAMILY CLAN MEMBERS",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )
                TextButton(onClick = onOpenRelationships) {
                    Text(text = "View All Meters →", fontSize = 11.sp, color = NaijaGoldLight)
                }
            }
        }

        items(familyMembers) { member ->
            val record = remember(member) { member.toTrackRecord() }
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    // Name & Role Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = record.avatarEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = member.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimaryLight
                                )
                                Text(
                                    text = member.role.title,
                                    fontSize = 11.sp,
                                    color = NaijaGold,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Relationship Tier Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(record.tier.badgeColorHex).copy(alpha = 0.2f),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(Color(record.tier.badgeColorHex))
                            )
                        ) {
                            Text(
                                text = record.tier.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(record.tier.badgeColorHex),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = member.demands,
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // DUAL METERS: Individual Happiness and Loyalty Meters
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Happiness Meter
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Happiness ${record.moodEmoji}", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(
                                    text = "${member.happiness}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (member.happiness >= 70) SuccessGreen else NaijaGoldLight
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            DualMeterProgressBar(
                                value = member.happiness,
                                activeBrush = Brush.horizontalGradient(
                                    colors = if (member.happiness >= 70) listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                                    else listOf(Color(0xFFE65100), NaijaGold)
                                )
                            )
                            Text(
                                text = record.moodLabel,
                                fontSize = 9.sp,
                                color = TextSecondaryLight,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        // Loyalty Meter
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Loyalty ${record.loyaltyEmoji}", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(
                                    text = "${member.loyalty}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (member.loyalty >= 70) Color(0xFF42A5F5) else AlertRed
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            DualMeterProgressBar(
                                value = member.loyalty,
                                activeBrush = Brush.horizontalGradient(
                                    colors = if (member.loyalty >= 70) listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                                    else listOf(Color(0xFF880E4F), Color(0xFFEC407A))
                                )
                            )
                            Text(
                                text = record.loyaltyLabel,
                                fontSize = 9.sp,
                                color = TextSecondaryLight,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
