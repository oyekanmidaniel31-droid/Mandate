package com.example.mandate.ui.components

import androidx.compose.animation.*
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.*
import com.example.ui.theme.*

/**
 * Scrollable floating UI overlay displaying the Daily Mission Log in the Open World.
 * Tracks both Family Obligations, Political Hustles, and National Crisis duties.
 */
@Composable
fun DailyMissionOverlay(
    missions: List<DailyMission>,
    activeFilter: String,
    currentDayNumber: Int,
    dayOfWeek: DayOfWeekName,
    timeOfDay: LagosTimeOfDay,
    currencyWallet: CurrencyWallet,
    characterCash: Long,
    characterStamina: Int,
    onFilterChanged: (String) -> Unit,
    onExecuteMission: (DailyMission) -> Unit,
    onClaimReward: (DailyMission) -> Unit,
    onQuickTravelToMission: (DailyMission) -> Unit,
    onAdvanceToNextDay: () -> Unit,
    onCloseOverlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredMissions = remember(missions, activeFilter) {
        when (activeFilter) {
            "FAMILY" -> missions.filter { it.category == DailyMissionCategory.FAMILY_OBLIGATION }
            "POLITICAL" -> missions.filter { it.category == DailyMissionCategory.POLITICAL_HUSTLE }
            "CRISIS" -> missions.filter { it.category == DailyMissionCategory.NATIONAL_CRISIS }
            else -> missions
        }
    }

    val completedCount = missions.count { it.status == MissionStatus.COMPLETED || it.status == MissionStatus.CLAIMED }
    val totalCount = missions.size

    Surface(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCharcoal.copy(alpha = 0.92f))
            .testTag("daily_mission_overlay"),
        color = Color.Transparent
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(NaijaGold, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📋", fontSize = 20.sp)
                    }
                    Column {
                        Text(
                            text = "DAILY MISSION LOG",
                            color = NaijaGoldLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Day $currentDayNumber (${dayOfWeek.displayName}) • ${timeOfDay.label}",
                            color = TextSecondaryLight,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = onCloseOverlay,
                    modifier = Modifier
                        .size(32.dp)
                        .background(EmeraldSurfaceVariant, CircleShape)
                        .testTag("close_mission_overlay_btn")
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Mission Overlay",
                        tint = NaijaGoldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Summary & Next Day Cycle Card
            Card(
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY PROGRESS: $completedCount / $totalCount TASKS",
                                color = NaijaGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Daily Living Cost: ₦${currencyWallet.dailyLivingCostTotal / 1_000}k / day",
                                color = TextSecondaryLight,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = onAdvanceToNextDay,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (timeOfDay.isNightLighting) NaijaGold else EmeraldSurfaceVariant,
                                contentColor = if (timeOfDay.isNightLighting) DeepCharcoal else NaijaGoldLight
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(NaijaGold)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("advance_to_next_day_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text("🌙", fontSize = 12.sp)
                                Text(
                                    text = if (timeOfDay.isNightLighting) "SLEEP & NEXT DAY" else "ADVANCE CYCLE",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    LinearProgressIndicator(
                        progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
                        color = NaijaGold,
                        trackColor = DeepCharcoal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Tasks ($totalCount)",
                    "FAMILY" to "Family Obligations 🏡",
                    "POLITICAL" to "Political Hustle 🏛️",
                    "CRISIS" to "National Crisis 🚨"
                )

                items(filters) { (key, label) ->
                    val isSelected = activeFilter == key
                    Surface(
                        color = if (isSelected) NaijaGold else EmeraldSurfaceVariant,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .clickable { onFilterChanged(key) }
                            .testTag("filter_chip_$key")
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) DeepCharcoal else TextPrimaryLight,
                            fontSize = 11.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Scrollable List of Missions
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("mission_list_scroll"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMissions, key = { it.id }) { mission ->
                    MissionItemCard(
                        mission = mission,
                        playerCash = characterCash,
                        playerStamina = characterStamina,
                        onExecute = { onExecuteMission(mission) },
                        onClaim = { onClaimReward(mission) },
                        onTravel = { onQuickTravelToMission(mission) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

/**
 * Individual task card in the scrollable mission log.
 */
@Composable
private fun MissionItemCard(
    mission: DailyMission,
    playerCash: Long,
    playerStamina: Int,
    onExecute: () -> Unit,
    onClaim: () -> Unit,
    onTravel: () -> Unit
) {
    val categoryColor = Color(mission.category.colorHex)
    val canAfford = playerCash >= mission.costNaira && playerStamina >= mission.staminaCost

    Card(
        colors = CardDefaults.cardColors(
            containerColor = when (mission.status) {
                MissionStatus.CLAIMED -> EmeraldSurfaceVariant.copy(alpha = 0.5f)
                MissionStatus.COMPLETED -> Color(0xFF1B3B2B)
                else -> EmeraldSurfaceVariant
            }
        ),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(
                when (mission.status) {
                    MissionStatus.CLAIMED -> Color.Gray.copy(alpha = 0.4f)
                    MissionStatus.COMPLETED -> NaijaGreenLight
                    else -> categoryColor.copy(alpha = 0.8f)
                }
            )
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mission_card_${mission.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Category & State Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = categoryColor.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(categoryColor))
                    ) {
                        Text(
                            text = "${mission.category.iconEmoji} ${mission.category.title.uppercase()}",
                            color = categoryColor,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = DeepCharcoal,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "📍 ${mission.targetStateName} • ${mission.targetLocationName}",
                            color = TextSecondaryLight,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Status Tag
                Surface(
                    color = when (mission.status) {
                        MissionStatus.CLAIMED -> Color.DarkGray
                        MissionStatus.COMPLETED -> SuccessGreen
                        MissionStatus.IN_PROGRESS -> WarningOrange
                        MissionStatus.AVAILABLE -> DeepCharcoal
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = when (mission.status) {
                            MissionStatus.CLAIMED -> "CLAIMED ✓"
                            MissionStatus.COMPLETED -> "READY TO CLAIM!"
                            MissionStatus.IN_PROGRESS -> "IN PROGRESS"
                            MissionStatus.AVAILABLE -> "PENDING"
                        },
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Title & Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(mission.iconEmoji, fontSize = 22.sp)
                Text(
                    text = mission.title,
                    color = if (mission.status == MissionStatus.CLAIMED) TextSecondaryLight else NaijaGoldLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Brief Description
            Text(
                text = mission.briefDescription,
                color = TextPrimaryLight,
                fontSize = 11.5.sp,
                lineHeight = 16.sp
            )

            // Pidgin Flavor Quote
            Surface(
                color = DeepCharcoal.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mission.pidginFlavor,
                    color = NaijaGoldLight.copy(alpha = 0.9f),
                    fontSize = 10.5.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Requirements & Rewards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Costs
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (mission.costNaira > 0L) {
                        Text(
                            text = "Cost: ₦${mission.costNaira / 1_000}k",
                            color = if (playerCash >= mission.costNaira) WarningOrange else AlertRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Stamina: -${mission.staminaCost}%",
                        color = if (playerStamina >= mission.staminaCost) TextSecondaryLight else AlertRed,
                        fontSize = 10.sp
                    )
                }

                // Rewards
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (mission.rewardNaira > 0L) {
                        Text("₦${mission.rewardNaira / 1_000}k", color = NaijaGoldLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (mission.rewardFamilyLoyalty > 0) {
                        Text("+${mission.rewardFamilyLoyalty} Loyalty", color = SuccessGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (mission.rewardGodfatherFavor > 0) {
                        Text("+${mission.rewardGodfatherFavor} Favor", color = NaijaGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    if (mission.rewardClout > 0) {
                        Text("+${mission.rewardClout} Clout", color = AlertRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            HorizontalDivider(color = CardBorderGold.copy(alpha = 0.4f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (mission.status) {
                    MissionStatus.CLAIMED -> {
                        Text(
                            text = "Task completed and rewards credited.",
                            color = TextSecondaryLight,
                            fontSize = 10.sp
                        )
                    }
                    MissionStatus.COMPLETED -> {
                        Button(
                            onClick = onClaim,
                            colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("claim_btn_${mission.id}")
                        ) {
                            Text("CLAIM REWARD ⭐", fontSize = 11.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    MissionStatus.AVAILABLE, MissionStatus.IN_PROGRESS -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = onTravel,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaijaGoldLight)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(12.dp))
                                    Text("GPS / Travel", fontSize = 10.sp)
                                }
                            }

                            Button(
                                onClick = onExecute,
                                enabled = canAfford,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = categoryColor,
                                    contentColor = DeepCharcoal
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("execute_btn_${mission.id}")
                            ) {
                                Text(
                                    text = if (canAfford) "EXECUTE TASK" else "INSUFFICIENT FUNDS/STAMINA",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
