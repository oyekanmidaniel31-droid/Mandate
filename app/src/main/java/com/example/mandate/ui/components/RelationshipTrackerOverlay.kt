package com.example.mandate.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * Comprehensive Relationship Status & Meters UI Overlay for tracking
 * Family Members and Syndicate Padis with individual Happiness & Loyalty meters.
 */
@Composable
fun RelationshipTrackerOverlay(
    familyMembers: List<FamilyMember>,
    padis: List<Padi>,
    playerNara: Long,
    onClose: () -> Unit,
    onSendGift: (personId: String, isFamily: Boolean, amount: Long) -> Unit,
    onSpendTime: (personId: String, isFamily: Boolean) -> Unit,
    onPledgeLoyalty: (personId: String, isFamily: Boolean) -> Unit,
    onSettleWahala: (personId: String, isFamily: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "FAMILY", "PADI", "AT_RISK", "ALLIES"
    var expandedPersonId by remember { mutableStateOf<String?>(null) }

    // Aggregate records
    val allRecords = remember(familyMembers, padis) {
        val famRecords = familyMembers.map { it.toTrackRecord() }
        val padiRecords = padis.map { it.toTrackRecord() }
        famRecords + padiRecords
    }

    val filteredRecords = remember(allRecords, selectedFilter) {
        when (selectedFilter) {
            "FAMILY" -> allRecords.filter { it.isFamily }
            "PADI" -> allRecords.filter { !it.isFamily }
            "AT_RISK" -> allRecords.filter { it.happiness < 50 || it.loyalty < 50 || it.tier == RelationshipTier.STRAINED_SUSPICIOUS || it.tier == RelationshipTier.BITTER_RIVAL }
            "ALLIES" -> allRecords.filter { it.tier == RelationshipTier.DEVOTED_ALLIANCE || it.tier == RelationshipTier.WARM_CONFIDANT }
            else -> allRecords
        }
    }

    val avgFamilyHappiness = remember(familyMembers) {
        if (familyMembers.isEmpty()) 0 else familyMembers.map { it.happiness }.average().toInt()
    }
    val avgPadiLoyalty = remember(padis) {
        if (padis.isEmpty()) 0 else padis.map { it.loyalty }.average().toInt()
    }
    val atRiskCount = remember(allRecords) {
        allRecords.count { it.happiness < 50 || it.loyalty < 50 }
    }

    // Modal Card Container
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xCC050E0A))
            .clickable(onClick = onClose)
            .padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)
            .testTag("relationship_tracker_overlay_backdrop"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .clickable(enabled = false) {} // Prevent backdrop click through
                .testTag("relationship_tracker_overlay_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xF50D1C14)),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = NaijaGold,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Diversity1,
                                    contentDescription = "Relationships",
                                    tint = DeepCharcoal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "RELATIONSHIP DOSSIER",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = NaijaGoldLight
                            )
                            Text(
                                text = "Clan Harmony & Syndicate Loyalty Ledger",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("close_relationship_overlay_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Summary Dashboard Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Clan Happiness Metric
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF132B1F),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenPrimary))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏡 Clan", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NaijaGold)
                            }
                            Text(
                                text = "$avgFamilyHappiness%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (avgFamilyHappiness >= 70) SuccessGreen else NaijaGoldLight
                            )
                            Text(
                                text = "Avg Happiness",
                                fontSize = 9.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    // Padi Loyalty Metric
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF132B1F),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenPrimary))
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🤛 Syndicate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NaijaGold)
                            }
                            Text(
                                text = "$avgPadiLoyalty%",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (avgPadiLoyalty >= 70) SuccessGreen else NaijaGoldLight
                            )
                            Text(
                                text = "Avg Loyalty",
                                fontSize = 9.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    // Wahala Alert Metric
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = if (atRiskCount > 0) Color(0xFF3B1214) else Color(0xFF132B1F),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (atRiskCount > 0) AlertRed else NaijaGreenPrimary)
                        )
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "⚠️ Friction", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (atRiskCount > 0) AlertRed else NaijaGold)
                            }
                            Text(
                                text = "$atRiskCount",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = if (atRiskCount > 0) AlertRed else SuccessGreen
                            )
                            Text(
                                text = if (atRiskCount > 0) "Needs Attention" else "All In Harmony",
                                fontSize = 9.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Filter Chips Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    RelationshipFilterChip(
                        label = "All (${allRecords.size})",
                        selected = selectedFilter == "ALL",
                        onClick = { selectedFilter = "ALL" },
                        tag = "filter_all"
                    )
                    RelationshipFilterChip(
                        label = "Clan (${familyMembers.size})",
                        selected = selectedFilter == "FAMILY",
                        onClick = { selectedFilter = "FAMILY" },
                        tag = "filter_family"
                    )
                    RelationshipFilterChip(
                        label = "Padis (${padis.size})",
                        selected = selectedFilter == "PADI",
                        onClick = { selectedFilter = "PADI" },
                        tag = "filter_padi"
                    )
                    RelationshipFilterChip(
                        label = "Friction / At Risk ($atRiskCount)",
                        selected = selectedFilter == "AT_RISK",
                        onClick = { selectedFilter = "AT_RISK" },
                        isWarning = atRiskCount > 0,
                        tag = "filter_at_risk"
                    )
                    RelationshipFilterChip(
                        label = "Allies",
                        selected = selectedFilter == "ALLIES",
                        onClick = { selectedFilter = "ALLIES" },
                        tag = "filter_allies"
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Person Cards
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("relationship_records_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRecords, key = { it.id }) { record ->
                        RelationshipRecordCard(
                            record = record,
                            isExpanded = expandedPersonId == record.id,
                            onToggleExpand = {
                                expandedPersonId = if (expandedPersonId == record.id) null else record.id
                            },
                            playerNara = playerNara,
                            onSendGift = { onSendGift(record.id, record.isFamily, 20_000L) },
                            onSpendTime = { onSpendTime(record.id, record.isFamily) },
                            onPledgeLoyalty = { onPledgeLoyalty(record.id, record.isFamily) },
                            onSettleWahala = { onSettleWahala(record.id, record.isFamily) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RelationshipFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    isWarning: Boolean = false,
    tag: String
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) {
            if (isWarning) AlertRed else NaijaGold
        } else {
            Color(0xFF14241B)
        },
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(if (selected) Color.White else Color(0xFF2C4C38))
        ),
        modifier = Modifier.testTag(tag)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            color = if (selected) {
                if (isWarning) Color.White else DeepCharcoal
            } else {
                if (isWarning) Color(0xFFFF8A80) else NaijaGoldLight
            },
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun RelationshipRecordCard(
    record: RelationshipTrackRecord,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    playerNara: Long,
    onSendGift: () -> Unit,
    onSpendTime: () -> Unit,
    onPledgeLoyalty: () -> Unit,
    onSettleWahala: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = Color(record.tier.badgeColorHex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onToggleExpand)
            .testTag("relationship_card_${record.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF112319)),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(if (isExpanded) NaijaGold else Color(0x662C4C38))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Avatar, Name, Tier Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF1F432D), tierColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = record.avatarEmoji, fontSize = 20.sp)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = record.name,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = record.roleOrSubtitle,
                                fontSize = 11.sp,
                                color = NaijaGoldLight,
                                fontWeight = FontWeight.Medium
                            )
                            if (record.isFamily) {
                                Text(text = " • 🏡 Clan", fontSize = 10.sp, color = Color(0xFFA5D6A7))
                            } else {
                                Text(text = " • 🤝 Syndicate", fontSize = 10.sp, color = Color(0xFF90CAF9))
                            }
                        }
                    }
                }

                // Tier Tag Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = tierColor.copy(alpha = 0.25f),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(tierColor))
                ) {
                    Text(
                        text = record.tier.tag,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = tierColor,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // DUAL METERS: Happiness and Loyalty Meters
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // HAPPINESS METER
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Happiness", fontSize = 10.sp, color = TextSecondaryLight)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = record.moodEmoji, fontSize = 11.sp)
                        }
                        Text(
                            text = "${record.happiness}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (record.happiness >= 70) SuccessGreen else if (record.happiness >= 45) NaijaGoldLight else AlertRed
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    DualMeterProgressBar(
                        value = record.happiness,
                        activeBrush = Brush.horizontalGradient(
                            colors = if (record.happiness >= 70) listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                            else if (record.happiness >= 45) listOf(Color(0xFFE65100), NaijaGold)
                            else listOf(Color(0xFFB71C1C), AlertRed)
                        )
                    )
                    Text(
                        text = record.moodLabel,
                        fontSize = 9.sp,
                        color = TextSecondaryLight,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // LOYALTY METER
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Loyalty", fontSize = 10.sp, color = TextSecondaryLight)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(text = record.loyaltyEmoji, fontSize = 11.sp)
                        }
                        Text(
                            text = "${record.loyalty}%",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = if (record.loyalty >= 70) Color(0xFF42A5F5) else if (record.loyalty >= 45) NaijaGoldLight else AlertRed
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    DualMeterProgressBar(
                        value = record.loyalty,
                        activeBrush = Brush.horizontalGradient(
                            colors = if (record.loyalty >= 70) listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                            else if (record.loyalty >= 45) listOf(Color(0xFF00695C), Color(0xFF26A69A))
                            else listOf(Color(0xFF880E4F), Color(0xFFEC407A))
                        )
                    )
                    Text(
                        text = record.loyaltyLabel,
                        fontSize = 9.sp,
                        color = TextSecondaryLight,
                        maxLines = 1,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            // Summary description
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = record.summaryGist,
                fontSize = 11.sp,
                color = Color(0xFFB0BEC5),
                lineHeight = 15.sp
            )

            // EXPANDED DETAILS & QUICK ACTIONS
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    HorizontalDivider(color = Color(0x332C4C38), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Strategic Benefit
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF0E2519)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "🎯 STRATEGIC MANDATE BENEFIT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = NaijaGold
                            )
                            Text(
                                text = record.strategicBenefit,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Demands / Notes
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF16231D)
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "💬 CURRENT DEMANDS & SENTIMENT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaijaGoldLight
                            )
                            Text(
                                text = record.activeDemands,
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = Color(0xFFECEFF1)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // ACTION BUTTONS ROW
                    Text(
                        text = "DIRECT ACTIONS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = NaijaGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Gift / Remit
                        OutlinedButton(
                            onClick = onSendGift,
                            enabled = playerNara >= 20_000L,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gift_button_${record.id}"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = NaijaGoldLight)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "💸 Remit ₦20k", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "+15 Hap, +10 Loy", fontSize = 8.sp, color = TextSecondaryLight)
                            }
                        }

                        // Quality Time
                        OutlinedButton(
                            onClick = onSpendTime,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("gist_button_${record.id}"),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF81C784))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = "🍲 Share Gist", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(text = "+18 Happiness", fontSize = 8.sp, color = TextSecondaryLight)
                            }
                        }

                        // Loyalty Pact / Settle Wahala
                        if (record.happiness < 50 || record.loyalty < 50) {
                            Button(
                                onClick = onSettleWahala,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("wahala_button_${record.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = AlertRed)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🕊️ Make Peace", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    Text(text = "End Rivalry", fontSize = 8.sp, color = Color.White.copy(alpha = 0.8f))
                                }
                            }
                        } else {
                            Button(
                                onClick = onPledgeLoyalty,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("loyalty_pact_button_${record.id}"),
                                colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🛡️ Seal Pact", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaijaGoldLight)
                                    Text(text = "+15 Loyalty", fontSize = 8.sp, color = NaijaGoldLight.copy(alpha = 0.8f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Dual Meter Progress Bar for smooth, colorful visualization of Happiness and Loyalty.
 */
@Composable
fun DualMeterProgressBar(
    value: Int,
    activeBrush: Brush,
    modifier: Modifier = Modifier
) {
    val progress = (value.coerceIn(0, 100)) / 100f
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing),
        label = "progress_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF09140E))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(4.dp))
                .background(activeBrush)
        )
    }
}
