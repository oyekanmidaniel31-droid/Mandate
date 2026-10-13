package com.example.mandate.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.data.OpenWorldData
import com.example.mandate.model.*
import com.example.mandate.ui.components.*
import com.example.ui.theme.*

/**
 * Primary full-screen Open World simulation interface ("Lagos Life").
 * Features real-time explorable 3D isometric districts, virtual thumb controls,
 * travel mode selection, Mama Put Bukka dining, tech shops, drip salon,
 * street encounters, and city-wide fast travel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LagosOpenWorldScreen(
    character: Character,
    worldState: OpenWorldState,
    onMovePlayer: (Float, Float) -> Unit,
    onSwitchDistrict: (LagosDistrict) -> Unit,
    onChangeTravelMode: (TravelMode) -> Unit,
    onInteractPOI: (OpenWorldPOI) -> Unit,
    onEatBukkaDish: (BukkaDish) -> Unit,
    onBuyMarketGadget: (MarketGadget) -> Unit,
    onBuyBoutiqueOutfit: (BoutiqueOutfit) -> Unit,
    onBuyFuel: (Int, Boolean) -> Unit, // litres, isForGenerator
    onResolveEncounterOption: (StreetEncounterOption) -> Unit,
    onStartMission: (StreetMission) -> Unit,
    onAdvanceTimeOfDay: () -> Unit,
    onHonkHorn: () -> Unit,
    onOpenOwambeScreen: () -> Unit,
    onOpenClanHomeScreen: () -> Unit,
    onToggleMissionOverlay: () -> Unit = {},
    onSetMissionFilter: (String) -> Unit = {},
    onExecuteDailyMission: (DailyMission) -> Unit = {},
    onClaimDailyMissionReward: (DailyMission) -> Unit = {},
    onTravelToState: (NigeriaState, Boolean) -> Unit = { _, _ -> },
    onAdvanceDayCycle: () -> Unit = {},
    onDepositToBank: (Long) -> Unit = {},
    onWithdrawToCash: (Long) -> Unit = {},
    onBuyDollars: (Long) -> Unit = {},
    onSellDollars: (Long) -> Unit = {},
    onToggleNationMap: (Boolean) -> Unit = {},
    onToggleCurrencyExchange: (Boolean) -> Unit = {},
    onToggleAmbientAudio: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val pois = remember(worldState.currentDistrict) {
        OpenWorldData.getPOIsForDistrict(worldState.currentDistrict)
    }
    val npcs = remember(worldState.currentDistrict) {
        OpenWorldData.getInitialNPCs(worldState.currentDistrict)
    }
    val vehicles = remember(worldState.currentDistrict) {
        OpenWorldData.getInitialVehicles()
    }

    var showCityMapDialog by remember { mutableStateOf(false) }
    var showMissionsSheet by remember { mutableStateOf(false) }
    var activePOIForSheet by remember { mutableStateOf<OpenWorldPOI?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .testTag("lagos_open_world_screen")
    ) {
        // 1. TOP STATUS, DISTRICT & NATION HUD
        OpenWorldTopHUD(
            character = character,
            worldState = worldState,
            onOpenCityMap = { showCityMapDialog = true },
            onOpenNationMap = { onToggleNationMap(true) },
            onOpenBDC = { onToggleCurrencyExchange(true) },
            onToggleMissions = onToggleMissionOverlay,
            onToggleAmbientAudio = onToggleAmbientAudio,
            onAdvanceTime = onAdvanceTimeOfDay,
            onAdvanceDayCycle = onAdvanceDayCycle
        )

        // 2. 3D ISOMETRIC VIEWPORT
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            LagosOpenWorld3DCanvas(
                worldState = worldState,
                pois = pois,
                npcs = npcs,
                vehicles = vehicles,
                onMoveToGrid = { gx, gy -> onMovePlayer(gx, gy) },
                onPOISelected = { poi ->
                    activePOIForSheet = poi
                    onInteractPOI(poi)
                },
                modifier = Modifier.fillMaxSize()
            )

            // Quick Floating Button for Active Street Encounter if present
            if (worldState.activeEncounter != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AlertRed.copy(alpha = 0.95f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 10.dp)
                        .clickable { /* Handled below */ }
                        .testTag("active_street_encounter_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(worldState.activeEncounter.actorEmoji, fontSize = 18.sp)
                        Text(
                            text = "STREET ENCOUNTER: ${worldState.activeEncounter.title}",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick State & District Tag in Top Left
            Surface(
                color = DeepCharcoal.copy(alpha = 0.88f),
                shape = RoundedCornerShape(8.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .clickable { onToggleNationMap(true) }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(worldState.currentState.flagEmoji, fontSize = 12.sp)
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color(worldState.currentDistrict.primaryColorHex), CircleShape)
                    )
                    Text(
                        text = "${worldState.currentState.name}: ${worldState.currentDistrict.displayName}",
                        color = NaijaGoldLight,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Quick Mission Log FAB in Top Right
            Surface(
                color = NaijaGold,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clickable { onToggleMissionOverlay() }
                    .testTag("floating_mission_log_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("📋", fontSize = 12.sp)
                    Text(
                        text = "LOG (${worldState.dailyMissions.count { !it.isCompletedOrClaimed }})",
                        color = DeepCharcoal,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // 4. DAILY MISSION OVERLAY (FLOATING GLASSMORPHISM OVERLAY OVER THE 3D VIEWPORT)
            if (worldState.isMissionOverlayOpen) {
                DailyMissionOverlay(
                    missions = worldState.dailyMissions,
                    activeFilter = worldState.missionFilter,
                    currentDayNumber = worldState.currentDayNumber,
                    dayOfWeek = worldState.dayOfWeek,
                    timeOfDay = worldState.timeOfDay,
                    currencyWallet = worldState.currencyWallet,
                    characterCash = character.naraBalance,
                    characterStamina = worldState.stamina,
                    onFilterChanged = onSetMissionFilter,
                    onExecuteMission = onExecuteDailyMission,
                    onClaimReward = onClaimDailyMissionReward,
                    onQuickTravelToMission = { mission ->
                        val targetState = NigeriaFederation.allStates.find { it.name == mission.targetStateName }
                            ?: NigeriaFederation.LAGOS_STATE
                        onTravelToState(targetState, false)
                    },
                    onAdvanceToNextDay = onAdvanceDayCycle,
                    onCloseOverlay = onToggleMissionOverlay,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // 3. BOTTOM CONTROLS & ERGONOMIC NAVIGATION PANEL
        OpenWorldBottomControls(
            worldState = worldState,
            onMoveDirection = { dx, dy ->
                val newX = (worldState.playerX + dx).coerceIn(1f, 15f)
                val newY = (worldState.playerY + dy).coerceIn(1f, 15f)
                onMovePlayer(newX, newY)
            },
            onChangeTravelMode = onChangeTravelMode,
            onHonkHorn = onHonkHorn,
            onOpenMissions = onToggleMissionOverlay,
            onOpenCityMap = { showCityMapDialog = true },
            onOpenNationMap = { onToggleNationMap(true) },
            onOpenBDC = { onToggleCurrencyExchange(true) }
        )
    }

    // --- BOTTOM SHEETS & DIALOGS ---

    // 1. POI INTERACTION SHEET (Mama Put, Computer Village, Drip Salon, Agbero Park, etc.)
    if (activePOIForSheet != null) {
        val poi = activePOIForSheet!!
        ModalBottomSheet(
            onDismissRequest = { activePOIForSheet = null },
            containerColor = DeepCharcoal,
            dragHandle = { BottomSheetDefaults.DragHandle(color = NaijaGold) }
        ) {
            POIInteractionContent(
                poi = poi,
                character = character,
                worldState = worldState,
                onEatBukkaDish = { dish ->
                    onEatBukkaDish(dish)
                    activePOIForSheet = null
                },
                onBuyMarketGadget = { gadget ->
                    onBuyMarketGadget(gadget)
                    activePOIForSheet = null
                },
                onBuyBoutiqueOutfit = { outfit ->
                    onBuyBoutiqueOutfit(outfit)
                    activePOIForSheet = null
                },
                onBuyFuel = { litres, forGen ->
                    onBuyFuel(litres, forGen)
                    activePOIForSheet = null
                },
                onEnterOwambe = {
                    activePOIForSheet = null
                    onOpenOwambeScreen()
                },
                onEnterHomeClan = {
                    activePOIForSheet = null
                    onOpenClanHomeScreen()
                },
                onDismiss = { activePOIForSheet = null }
            )
        }
    }

    // 2. STREET ENCOUNTER DIALOG
    if (worldState.activeEncounter != null) {
        val encounter = worldState.activeEncounter
        AlertDialog(
            onDismissRequest = { /* Must resolve choice */ },
            containerColor = DeepCharcoal,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(encounter.actorEmoji, fontSize = 24.sp)
                    Text(
                        text = encounter.title,
                        color = NaijaGoldLight,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = encounter.narrative,
                        color = TextPrimaryLight,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(color = CardBorderGold)

                    Text(
                        text = "CHOOSE YOUR RESPONSE:",
                        color = NaijaGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    encounter.options.forEach { opt ->
                        Button(
                            onClick = { onResolveEncounterOption(opt) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldSurfaceVariant,
                                contentColor = TextPrimaryLight
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(NaijaGreenLight)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(opt.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                if (opt.costNara > 0L) {
                                    Text("Cost: ₦${opt.costNara}", color = AlertRed, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // 3. LAGOS CITY MAP & FAST TRAVEL MODAL
    if (showCityMapDialog) {
        LagosCityMapDialog(
            currentDistrict = worldState.currentDistrict,
            onSelectDistrict = { dist ->
                onSwitchDistrict(dist)
                showCityMapDialog = false
            },
            onDismiss = { showCityMapDialog = false }
        )
    }

    // 4. STREET MISSIONS / LAGOS LIFE GIGS SHEET
    if (showMissionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMissionsSheet = false },
            containerColor = DeepCharcoal,
            dragHandle = { BottomSheetDefaults.DragHandle(color = NaijaGold) }
        ) {
            StreetMissionsContent(
                activeMission = worldState.activeMission,
                onStartMission = {
                    onStartMission(it)
                    showMissionsSheet = false
                },
                onDismiss = { showMissionsSheet = false }
            )
        }
    }

    // 5. NATIONWIDE FEDERATION MAP DIALOG
    if (worldState.isNationMapOpen) {
        NigeriaNationMapDialog(
            currentState = worldState.currentState,
            playerCash = character.naraBalance,
            onTravelToState = onTravelToState,
            onDismiss = { onToggleNationMap(false) }
        )
    }

    // 6. CURRENCY EXCHANGE & BDC BUREAU DIALOG
    if (worldState.isCurrencyExchangeOpen) {
        CurrencyExchangeDialog(
            wallet = worldState.currencyWallet,
            characterCash = character.naraBalance,
            onDepositToBank = onDepositToBank,
            onWithdrawToCash = onWithdrawToCash,
            onBuyDollars = onBuyDollars,
            onSellDollars = onSellDollars,
            onDismiss = { onToggleCurrencyExchange(false) }
        )
    }
}

/**
 * Top HUD displaying State & District info, Multi-Tier Currency, Hold-Up Traffic Meter, Clock, and Player Vitals.
 */
@Composable
private fun OpenWorldTopHUD(
    character: Character,
    worldState: OpenWorldState,
    onOpenCityMap: () -> Unit,
    onOpenNationMap: () -> Unit,
    onOpenBDC: () -> Unit,
    onToggleMissions: () -> Unit,
    onToggleAmbientAudio: () -> Unit,
    onAdvanceTime: () -> Unit,
    onAdvanceDayCycle: () -> Unit
) {
    val pendingMissions = worldState.dailyMissions.count { !it.isCompletedOrClaimed }

    Card(
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("open_world_top_hud")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Row 1: State & District with Action Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // District Info
                Row(
                    modifier = Modifier
                        .clickable { onOpenNationMap() }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(worldState.currentState.flagEmoji, fontSize = 16.sp)
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = worldState.currentState.name,
                                color = NaijaGoldLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text("•", color = TextSecondaryLight, fontSize = 10.sp)
                            Text(
                                text = worldState.currentDistrict.displayName,
                                color = TextPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = worldState.currentDistrict.subtitle,
                            color = TextSecondaryLight,
                            fontSize = 8.5.sp
                        )
                    }
                }

                // Top Quick Action Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Daily Missions Log Badge
                    Surface(
                        color = if (pendingMissions > 0) NaijaGold else DeepCharcoal,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { onToggleMissions() }
                            .testTag("top_missions_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text("📋", fontSize = 10.sp)
                            Text(
                                text = "$pendingMissions",
                                color = if (pendingMissions > 0) DeepCharcoal else NaijaGoldLight,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // Nation Map Button
                    Surface(
                        color = DeepCharcoal,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { onOpenNationMap() }
                            .testTag("top_nation_map_btn")
                    ) {
                        Text(
                            text = "🇳🇬 STATES",
                            color = NaijaGreenLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // BDC Forex Button
                    Surface(
                        color = DeepCharcoal,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .clickable { onOpenBDC() }
                            .testTag("top_bdc_btn")
                    ) {
                        Text(
                            text = "💱 BDC",
                            color = NaijaGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    // Ambient Soundscape Button
                    Surface(
                        color = if (worldState.isAmbientAudioEnabled) Color(0xFF1B3D2F) else DeepCharcoal,
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = SolidColor(if (worldState.isAmbientAudioEnabled) NaijaGreenLight else Color.Gray)
                        ),
                        modifier = Modifier
                            .clickable { onToggleAmbientAudio() }
                            .testTag("top_ambient_audio_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Text(worldState.currentAmbientSoundEmoji, fontSize = 9.sp)
                            Text(
                                text = if (worldState.isAmbientAudioEnabled) "AUDIO" else "MUTE",
                                color = if (worldState.isAmbientAudioEnabled) NaijaGreenLight else TextSecondaryLight,
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Row 2: Day of Week, Time Clock, Weather Pill & Sleep/Advance Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day & Time Pill
                Surface(
                    color = DeepCharcoal,
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold.copy(alpha = 0.4f))),
                    modifier = Modifier.clickable { onAdvanceTime() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = if (worldState.timeOfDay.isNightLighting) "🌙" else "☀️",
                            fontSize = 10.sp
                        )
                        Text(
                            text = "Day ${worldState.currentDayNumber} (${worldState.dayOfWeek.shortName}) • ${worldState.timeOfDay.clockTime} • ${worldState.weather.label}",
                            color = TextPrimaryLight,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Advance / Sleep Button
                Surface(
                    color = if (worldState.timeOfDay.isNightLighting) NaijaGold else Color(0xFF1E3A2F),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.clickable {
                        if (worldState.timeOfDay.isNightLighting) onAdvanceDayCycle()
                        else onAdvanceTime()
                    }
                ) {
                    Text(
                        text = if (worldState.timeOfDay.isNightLighting) "SLEEP & NEXT DAY" else "ADVANCE TIME ⏩",
                        color = if (worldState.timeOfDay.isNightLighting) DeepCharcoal else NaijaGoldLight,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Ambient Soundscape Ticker
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleAmbientAudio() }
                    .padding(horizontal = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "🔊 AMBIENCE:",
                    color = NaijaGold,
                    fontSize = 8.5.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${worldState.currentAmbientSoundEmoji} ${worldState.currentAmbientSoundTitle}",
                    color = if (worldState.isAmbientAudioEnabled) TextPrimaryLight else TextSecondaryLight,
                    fontSize = 8.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Row 3: Multi-tier Currency & Street Vitals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Currency Trio
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onOpenBDC() }
                ) {
                    Text(
                        text = "💵 ₦${character.naraBalance / 1_000}k",
                        color = NaijaGoldLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "🏦 ₦${worldState.currencyWallet.bankNaira / 1_000}k",
                        color = Color(0xFF00B0FF),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "🇺🇸 $${worldState.currencyWallet.domUsd}",
                        color = SuccessGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Traffic and Stamina Bars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HOLD-UP: ${worldState.trafficHoldUp}%",
                        color = if (worldState.trafficHoldUp > 70) AlertRed else NaijaGold,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "STAMINA: ${worldState.stamina}%",
                        color = NaijaGreenLight,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Bottom controller panel featuring:
 * - Travel Mode switch bar (Foot, Danfo, Keke, Okada, Car)
 * - Virtual Directional D-Pad for fluid one-handed navigation
 * - Action triggers (Horn, Missions, Fast Travel, Nation Map, BDC)
 */
@Composable
private fun OpenWorldBottomControls(
    worldState: OpenWorldState,
    onMoveDirection: (Float, Float) -> Unit,
    onChangeTravelMode: (TravelMode) -> Unit,
    onHonkHorn: () -> Unit,
    onOpenMissions: () -> Unit,
    onOpenCityMap: () -> Unit,
    onOpenNationMap: () -> Unit,
    onOpenBDC: () -> Unit
) {
    val pendingMissions = worldState.dailyMissions.count { !it.isCompletedOrClaimed }

    Surface(
        color = DeepCharcoal,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(EmeraldSurfaceVariant)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Travel Mode Selector Row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(TravelMode.values()) { mode ->
                    val isSelected = (worldState.travelMode == mode)
                    Surface(
                        color = if (isSelected) NaijaGold else EmeraldSurfaceVariant,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .clickable { onChangeTravelMode(mode) }
                            .testTag("travel_mode_${mode.name.lowercase()}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(mode.emoji, fontSize = 12.sp)
                            Text(
                                text = mode.title,
                                color = if (isSelected) DeepCharcoal else TextPrimaryLight,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Controls Row: Virtual D-Pad on Left, Quick Action Buttons on Right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Virtual Directional D-Pad
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // UP Button
                    IconButton(
                        onClick = { onMoveDirection(0f, -1.2f) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(EmeraldSurfaceVariant, RoundedCornerShape(6.dp))
                            .testTag("dpad_up")
                    ) {
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = NaijaGold)
                    }

                    // LEFT - CENTER - RIGHT
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        IconButton(
                            onClick = { onMoveDirection(-1.2f, 0f) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(EmeraldSurfaceVariant, RoundedCornerShape(6.dp))
                                .testTag("dpad_left")
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = NaijaGold)
                        }

                        // Center action (Horn or Sprint)
                        IconButton(
                            onClick = { onHonkHorn() },
                            modifier = Modifier
                                .size(34.dp)
                                .background(NaijaGold, RoundedCornerShape(6.dp))
                                .testTag("dpad_horn")
                        ) {
                            Text("📢", fontSize = 14.sp)
                        }

                        IconButton(
                            onClick = { onMoveDirection(1.2f, 0f) },
                            modifier = Modifier
                                .size(34.dp)
                                .background(EmeraldSurfaceVariant, RoundedCornerShape(6.dp))
                                .testTag("dpad_right")
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = NaijaGold)
                        }
                    }

                    // DOWN Button
                    IconButton(
                        onClick = { onMoveDirection(0f, 1.2f) },
                        modifier = Modifier
                            .size(34.dp)
                            .background(EmeraldSurfaceVariant, RoundedCornerShape(6.dp))
                            .testTag("dpad_down")
                    ) {
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = NaijaGold)
                    }
                }

                // Quick Action Tiles
                Column(
                    modifier = Modifier.weight(1f).padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onOpenMissions,
                            colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).testTag("btn_daily_missions")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("📋", fontSize = 11.sp)
                                Text("Missions ($pendingMissions)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = onOpenNationMap,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).testTag("btn_nation_map")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("🇳🇬", fontSize = 11.sp)
                                Text("States", fontSize = 10.sp, color = TextPrimaryLight)
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = onOpenBDC,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).testTag("btn_bdc_forex")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("💱", fontSize = 11.sp)
                                Text("BDC Forex", fontSize = 10.sp, color = TextPrimaryLight)
                            }
                        }

                        Button(
                            onClick = onOpenCityMap,
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            modifier = Modifier.weight(1f).testTag("btn_open_city_map")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text("🗺️", fontSize = 11.sp)
                                Text("Districts", fontSize = 10.sp, color = TextPrimaryLight)
                            }
                        }
                    }

                    // Event Log Strip
                    if (worldState.lastWorldEventLog != null) {
                        Surface(
                            color = EmeraldSurfaceVariant.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = worldState.lastWorldEventLog,
                                color = NaijaGoldLight,
                                fontSize = 9.sp,
                                maxLines = 2,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Contextual content for interacting with any POI:
 * Mama Put food menu, Tech shop, Drip boutique, Fuel depot, or direct gateways to Owambe & Home.
 */
@Composable
private fun POIInteractionContent(
    poi: OpenWorldPOI,
    character: Character,
    worldState: OpenWorldState,
    onEatBukkaDish: (BukkaDish) -> Unit,
    onBuyMarketGadget: (MarketGadget) -> Unit,
    onBuyBoutiqueOutfit: (BoutiqueOutfit) -> Unit,
    onBuyFuel: (Int, Boolean) -> Unit,
    onEnterOwambe: () -> Unit,
    onEnterHomeClan: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(poi.type.iconEmoji, fontSize = 32.sp)
            Column {
                Text(poi.name, color = NaijaGoldLight, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(poi.district.displayName, color = TextSecondaryLight, fontSize = 11.sp)
            }
        }

        Text(poi.loreDescription, color = TextPrimaryLight, fontSize = 12.5.sp, lineHeight = 17.sp)

        HorizontalDivider(color = CardBorderGold)

        when (poi.type) {
            POIType.MAMA_PUT_BUKKA -> {
                Text("HOT BUKKA SPECIALS (Restores Stamina & Eases Stress):", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(OpenWorldData.bukkaMenu) { dish ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(dish.emoji, fontSize = 24.sp)
                                    Column {
                                        Text(dish.name, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(dish.description, color = TextSecondaryLight, fontSize = 9.5.sp, maxLines = 1)
                                        Text("Stamina +${dish.staminaGain}% • Stress -${dish.stressRelief}%", color = NaijaGreenLight, fontSize = 9.sp)
                                    }
                                }
                                Button(
                                    onClick = { onEatBukkaDish(dish) },
                                    enabled = (character.naraBalance >= dish.costNara),
                                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("₦${dish.costNara / 1_000}k", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            POIType.COMPUTER_VILLAGE -> {
                Text("TECH MARKET GADGETS (Boosts Cyber Influence & Clout):", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(OpenWorldData.gadgetList) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.emoji, fontSize = 24.sp)
                                    Column {
                                        Text(item.name, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(item.description, color = TextSecondaryLight, fontSize = 9.5.sp, maxLines = 1)
                                        Text("Clout +${item.cloutGain}%", color = NaijaGoldLight, fontSize = 9.sp)
                                    }
                                }
                                Button(
                                    onClick = { onBuyMarketGadget(item) },
                                    enabled = (character.naraBalance >= item.costNara),
                                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("₦${item.costNara / 1_000}k", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            POIType.BARBING_SALON -> {
                Text("DRIP BOUTIQUE & BESPOKE WARDROBE (Boosts Drip & Clout):", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 280.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(OpenWorldData.boutiqueList) { item ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.emoji, fontSize = 24.sp)
                                    Column {
                                        Text(item.name, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text(item.description, color = TextSecondaryLight, fontSize = 9.5.sp, maxLines = 1)
                                        Text("Drip +${item.dripGain}%", color = NaijaGoldLight, fontSize = 9.sp)
                                    }
                                }
                                Button(
                                    onClick = { onBuyBoutiqueOutfit(item) },
                                    enabled = (character.naraBalance >= item.costNara),
                                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("₦${item.costNara / 1_000}k", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            POIType.FUEL_STATION -> {
                Text("PMS PETROL FUEL PUMPS (Beat NEPA Power Cuts):", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onBuyFuel(25, true) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("⚡ 25L for Gen", fontWeight = FontWeight.Bold)
                            Text("₦21,250", color = NaijaGoldLight, fontSize = 11.sp)
                        }
                    }
                    Button(
                        onClick = { onBuyFuel(40, false) },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🚗 40L for Car", fontWeight = FontWeight.Bold)
                            Text("₦34,000", color = NaijaGoldLight, fontSize = 11.sp)
                        }
                    }
                }
            }
            POIType.OWAMBE_CANOPY -> {
                Text("ENTER THE FULL HIGH-FIDELITY 3D OWAMBE PARTY:", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onEnterOwambe,
                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🎉 ENTER OWAMBE ARENA & SPRAY CASH", fontWeight = FontWeight.Bold)
                }
            }
            POIType.FAMILY_COMPOUND -> {
                Text("RETURN HOME TO YOUR 3D CLAN LIVING SPACE:", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = onEnterHomeClan,
                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenLight, contentColor = DeepCharcoal),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🏡 ENTER 3D CLAN COMPOUND", fontWeight = FontWeight.Bold)
                }
            }
            else -> {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Done", color = TextPrimaryLight)
                }
            }
        }
    }
}

/**
 * Interactive Lagos City Map modal for fast traveling across all 6 districts.
 */
@Composable
private fun LagosCityMapDialog(
    currentDistrict: LagosDistrict,
    onSelectDistrict: (LagosDistrict) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Map, contentDescription = null, tint = NaijaGold)
                Text("LAGOS STATE TRANSIT MAP", color = NaijaGoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // High-fidelity Lagos Open World Hero Banner
                Image(
                    painter = painterResource(id = R.drawable.img_lagos_open_world),
                    contentDescription = "Lagos Cityscape",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Text(
                    text = "Select a district to fast-travel via Danfo or Okada:",
                    color = TextSecondaryLight,
                    fontSize = 11.sp
                )

                LazyColumn(
                    modifier = Modifier.heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(LagosDistrict.values()) { dist ->
                        val isCurrent = (dist == currentDistrict)
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isCurrent) EmeraldSurfaceVariant else CardBackgroundDark
                            ),
                            border = if (isCurrent) CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)) else null,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectDistrict(dist) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color(dist.primaryColorHex), CircleShape)
                                    )
                                    Column {
                                        Text(dist.displayName, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Text("Hold-Up Risk: ${dist.defaultTrafficRisk}%", color = TextSecondaryLight, fontSize = 9.sp)
                                    }
                                }

                                if (isCurrent) {
                                    Text("CURRENT", color = NaijaGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("₦${dist.baseTransitFare}", color = NaijaGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NaijaGold)
            }
        }
    )
}

/**
 * Street Gigs & Missions Hub Content.
 */
@Composable
private fun StreetMissionsContent(
    activeMission: StreetMission?,
    onStartMission: (StreetMission) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("💼", fontSize = 24.sp)
            Text("LAGOS LIFE STREET GIGS & MISSIONS", color = NaijaGoldLight, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Take on street hustles to earn quick cash, boost your clout, and build political grassroots power.",
            color = TextSecondaryLight,
            fontSize = 11.5.sp
        )

        HorizontalDivider(color = CardBorderGold)

        LazyColumn(
            modifier = Modifier.heightIn(max = 320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(OpenWorldData.sampleMissions) { mission ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(mission.iconEmoji, fontSize = 18.sp)
                                Text(mission.title, color = TextPrimaryLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("₦${mission.rewardNara / 1_000}k", color = NaijaGoldLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(mission.brief, color = TextSecondaryLight, fontSize = 10.sp, lineHeight = 14.sp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Target: ${mission.destinationDistrict.displayName}", color = NaijaGold, fontSize = 9.sp)
                            Button(
                                onClick = { onStartMission(mission) },
                                colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Text("ACCEPT GIG", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
