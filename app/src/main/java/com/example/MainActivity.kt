package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mandate.model.LifeStage
import com.example.mandate.ui.components.NaijaHeader
import com.example.mandate.ui.components.NationalTensionDetailDialog
import com.example.mandate.ui.components.RelationshipTrackerOverlay
import com.example.mandate.ui.screens.*
import com.example.mandate.viewmodel.GameViewModel
import com.example.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MandateApp()
            }
        }
    }
}

@Composable
fun MandateApp(viewModel: GameViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showTensionDetailDialog by remember { mutableStateOf(false) }

    // Handle back button when on sub-tabs or phone
    BackHandler(enabled = state.showPhoneSheet || state.activeNavTab != "LIFE") {
        if (state.showPhoneSheet) {
            viewModel.onClosePhone()
        } else {
            viewModel.onTabSelected("LIFE")
        }
    }

    if (showTensionDetailDialog) {
        NationalTensionDetailDialog(
            macroEconomy = state.macroEconomy,
            onDismiss = { showTensionDetailDialog = false },
            onDirectIntervention = { action ->
                viewModel.onCrisisIntervention(action)
            }
        )
    }

    if (state.isOriginEngineActive) {
        OriginEngineScreen(
            currentQuestionIndex = state.originQuizIndex,
            originScores = state.originScores,
            isDossierReady = state.isOriginDossierReady,
            generatedCharacter = state.character,
            onAnswerSelected = { answer ->
                viewModel.onOriginAnswerSelected(answer)
            },
            onAcceptDossier = {
                viewModel.onAcceptOriginDossier()
            },
            onResetQuiz = {
                viewModel.onResetOriginEngine()
            }
        )
    } else if (state.funeralSummary != null) {
        FuneralScreen(
            funeral = state.funeralSummary!!,
            onStartDynasty = { viewModel.onStartDynasty() },
            onRestart = { viewModel.onRestartGame() }
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                NaijaHeader(
                    character = state.character,
                    macroEconomy = state.macroEconomy,
                    unreadMessages = state.chatThreads.sumOf { it.unreadCount },
                    onOpenPhone = { viewModel.onOpenPhone("CHIRP") },
                    onInspectTension = { showTensionDetailDialog = true },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = EmeraldSurfaceVariant,
                    contentColor = NaijaGold,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    NavigationBarItem(
                        selected = state.activeNavTab == "WORLD",
                        onClick = { viewModel.onTabSelected("WORLD") },
                        icon = { Icon(Icons.Default.Explore, contentDescription = "Lagos World") },
                        label = { Text("World", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCharcoal,
                            selectedTextColor = NaijaGoldLight,
                            indicatorColor = NaijaGold,
                            unselectedIconColor = TextSecondaryLight,
                            unselectedTextColor = TextSecondaryLight
                        ),
                        modifier = Modifier.testTag("nav_tab_world")
                    )

                    NavigationBarItem(
                        selected = state.activeNavTab == "LIFE",
                        onClick = { viewModel.onTabSelected("LIFE") },
                        icon = { Icon(Icons.Default.AccountBalance, contentDescription = "Life") },
                        label = { Text("Life", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCharcoal,
                            selectedTextColor = NaijaGoldLight,
                            indicatorColor = NaijaGold,
                            unselectedIconColor = TextSecondaryLight,
                            unselectedTextColor = TextSecondaryLight
                        ),
                        modifier = Modifier.testTag("nav_tab_life")
                    )

                    NavigationBarItem(
                        selected = state.activeNavTab == "OWAMBE",
                        onClick = { viewModel.onTabSelected("OWAMBE") },
                        icon = { Icon(Icons.Default.Celebration, contentDescription = "Owambe 3D") },
                        label = { Text("Owambe", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCharcoal,
                            selectedTextColor = NaijaGoldLight,
                            indicatorColor = NaijaGold,
                            unselectedIconColor = TextSecondaryLight,
                            unselectedTextColor = TextSecondaryLight
                        ),
                        modifier = Modifier.testTag("nav_tab_owambe")
                    )

                    NavigationBarItem(
                        selected = state.activeNavTab == "PADI",
                        onClick = { viewModel.onTabSelected("PADI") },
                        icon = { Icon(Icons.Default.Groups, contentDescription = "Padi") },
                        label = { Text("Padi", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCharcoal,
                            selectedTextColor = NaijaGoldLight,
                            indicatorColor = NaijaGold,
                            unselectedIconColor = TextSecondaryLight,
                            unselectedTextColor = TextSecondaryLight
                        ),
                        modifier = Modifier.testTag("nav_tab_padi")
                    )

                    NavigationBarItem(
                        selected = state.activeNavTab == "CLAN",
                        onClick = { viewModel.onTabSelected("CLAN") },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (state.blackTaxQueue.isNotEmpty()) {
                                        Badge(containerColor = AlertRed) {
                                            Text("${state.blackTaxQueue.size}")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.FamilyRestroom, contentDescription = "Clan")
                            }
                        },
                        label = { Text("Clan", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCharcoal,
                            selectedTextColor = NaijaGoldLight,
                            indicatorColor = NaijaGold,
                            unselectedIconColor = TextSecondaryLight,
                            unselectedTextColor = TextSecondaryLight
                        ),
                        modifier = Modifier.testTag("nav_tab_clan")
                    )

                    NavigationBarItem(
                        selected = state.activeNavTab == "CRISIS",
                        onClick = { viewModel.onTabSelected("CRISIS") },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (state.macroEconomy.angerIndex > 70) {
                                        Badge(containerColor = AlertRed) {
                                            Text("!")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Crisis")
                            }
                        },
                        label = { Text("Crisis", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepCharcoal,
                            selectedTextColor = NaijaGoldLight,
                            indicatorColor = NaijaGold,
                            unselectedIconColor = TextSecondaryLight,
                            unselectedTextColor = TextSecondaryLight
                        ),
                        modifier = Modifier.testTag("nav_tab_crisis")
                    )

                    if (state.electionTally != null || state.character.currentStage == LifeStage.THE_RACE) {
                        NavigationBarItem(
                            selected = state.activeNavTab == "RACE",
                            onClick = { viewModel.onTabSelected("RACE") },
                            icon = { Icon(Icons.Default.HowToVote, contentDescription = "The Race") },
                            label = { Text("The Race", fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DeepCharcoal,
                                selectedTextColor = NaijaGoldLight,
                                indicatorColor = NaijaGold,
                                unselectedIconColor = TextSecondaryLight,
                                unselectedTextColor = TextSecondaryLight
                            ),
                            modifier = Modifier.testTag("nav_tab_race")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (state.activeNavTab) {
                    "WORLD" -> LagosOpenWorldScreen(
                        character = state.character,
                        worldState = state.openWorldState,
                        onMovePlayer = { gx, gy -> viewModel.onMovePlayer(gx, gy) },
                        onSwitchDistrict = { dist -> viewModel.onSwitchDistrict(dist) },
                        onChangeTravelMode = { mode -> viewModel.onChangeTravelMode(mode) },
                        onInteractPOI = { poi -> viewModel.onInteractWorldPOI(poi) },
                        onEatBukkaDish = { dish -> viewModel.onEatBukkaDish(dish) },
                        onBuyMarketGadget = { gadget -> viewModel.onBuyMarketGadget(gadget) },
                        onBuyBoutiqueOutfit = { outfit -> viewModel.onBuyBoutiqueOutfit(outfit) },
                        onBuyFuel = { litres, forGen -> viewModel.onBuyFuel(litres, forGen) },
                        onResolveEncounterOption = { opt -> viewModel.onResolveStreetEncounter(opt) },
                        onStartMission = { mission -> viewModel.onStartStreetMission(mission) },
                        onAdvanceTimeOfDay = { viewModel.onAdvanceWorldTimeOfDay() },
                        onHonkHorn = { viewModel.onHonkOpenWorldHorn() },
                        onOpenOwambeScreen = { viewModel.onTabSelected("OWAMBE") },
                        onOpenClanHomeScreen = { viewModel.onTabSelected("CLAN") },
                        onToggleMissionOverlay = { viewModel.onToggleDailyMissionOverlay() },
                        onSetMissionFilter = { filter -> viewModel.onSetMissionFilter(filter) },
                        onExecuteDailyMission = { mission -> viewModel.onExecuteDailyMission(mission) },
                        onClaimDailyMissionReward = { mission -> viewModel.onClaimDailyMissionReward(mission) },
                        onTravelToState = { targetState, isAir -> viewModel.onTravelToState(targetState, isAir) },
                        onAdvanceDayCycle = { viewModel.onAdvanceDayCycle() },
                        onDepositToBank = { amount -> viewModel.onDepositToBank(amount) },
                        onWithdrawToCash = { amount -> viewModel.onWithdrawToCash(amount) },
                        onBuyDollars = { usd -> viewModel.onBuyDollars(usd) },
                        onSellDollars = { usd -> viewModel.onSellDollars(usd) },
                        onToggleNationMap = { isOpen -> viewModel.onToggleNationMapDialog(isOpen) },
                        onToggleCurrencyExchange = { isOpen -> viewModel.onToggleCurrencyExchangeDialog(isOpen) },
                        onToggleAmbientAudio = { viewModel.onToggleAmbientAudio() }
                    )
                    "LIFE" -> MainGameScreen(
                        state = state,
                        onChoiceSelected = { viewModel.onChoiceSelected(it) },
                        onAdvanceTurn = { viewModel.onAdvanceTurn() },
                        onOpenPhone = { viewModel.onOpenPhone("CHIRP") },
                        onQuickAction = { viewModel.onQuickAction(it) },
                        onExecutePatronage = { cost, gain, note -> viewModel.onExecutePatronage(cost, gain, note) },
                        onDisburseFamilyGrant = { amt -> viewModel.onDisburseFamilyGrant(amt) },
                        onOpenBDC = { viewModel.onToggleCurrencyExchangeDialog(true) },
                        onToggleSfx = { viewModel.onToggleSfx() },
                        isSfxEnabled = state.isSfxEnabled
                    )
                    "OWAMBE" -> OwambeScreen(
                        character = state.character,
                        totalSprayedTonight = state.totalOwambeSprayed,
                        onSprayCash = { amount, isCombo -> viewModel.onSprayOwambeCash(amount, isCombo) },
                        onOrderRefreshment = { viewModel.onOrderOwambeRefreshment(it) },
                        onInteractVIP = { guest, action -> viewModel.onInteractOwambeVIP(guest, action) }
                    )
                    "PADI" -> PadiScreen(
                        padis = state.padis,
                        familyMembers = state.familyMembers,
                        playerNara = state.character.naraBalance,
                        onPadiAction = { padi, action -> viewModel.onPadiInteract(padi, action) },
                        onSendRelationshipGift = { id, isFam, amt -> viewModel.onSendRelationshipGift(id, isFam, amt) },
                        onSpendRelationshipTime = { id, isFam -> viewModel.onSpendRelationshipTime(id, isFam) },
                        onPledgeLoyaltyPact = { id, isFam -> viewModel.onPledgeLoyaltyPact(id, isFam) },
                        onSettleRelationshipWahala = { id, isFam -> viewModel.onSettleRelationshipWahala(id, isFam) }
                    )
                    "CLAN" -> FamilyScreen(
                        character = state.character,
                        familyMembers = state.familyMembers,
                        padis = state.padis,
                        blackTaxQueue = state.blackTaxQueue,
                        onPayTax = { viewModel.onPayBlackTax(it) },
                        onIgnoreTax = { viewModel.onIgnoreBlackTax(it) },
                        onSwitchHomeLayout = { viewModel.onSwitchHomeLayout(it) },
                        onHomeProstrationBlessing = { viewModel.onHomeProstrationBlessing(it) },
                        onHomeRemitCash = { entity, amt -> viewModel.onHomeRemitCash(entity, amt) },
                        onHomeShareMealOrDrink = { viewModel.onHomeShareMealOrDrink(it) },
                        onHomeSeekCounselOrMobilize = { viewModel.onHomeSeekCounselOrMobilize(it) },
                        onHomeResolveRivalry = { viewModel.onHomeResolveRivalry(it) },
                        onSendRelationshipGift = { id, isFam, amt -> viewModel.onSendRelationshipGift(id, isFam, amt) },
                        onSpendRelationshipTime = { id, isFam -> viewModel.onSpendRelationshipTime(id, isFam) },
                        onPledgeLoyaltyPact = { id, isFam -> viewModel.onPledgeLoyaltyPact(id, isFam) },
                        onSettleRelationshipWahala = { id, isFam -> viewModel.onSettleRelationshipWahala(id, isFam) }
                    )
                    "CRISIS" -> CrisisUprisingScreen(
                        macroEconomy = state.macroEconomy,
                        onCrisisAction = { viewModel.onCrisisIntervention(it) }
                    )
                    "RACE" -> state.electionTally?.let { tally ->
                        TheRaceScreen(
                            electionTally = tally,
                            onProceed = { viewModel.onTabSelected("LIFE") }
                        )
                    } ?: MainGameScreen(
                        state = state,
                        onChoiceSelected = { viewModel.onChoiceSelected(it) },
                        onAdvanceTurn = { viewModel.onAdvanceTurn() },
                        onOpenPhone = { viewModel.onOpenPhone("CHIRP") },
                        onQuickAction = { viewModel.onQuickAction(it) }
                    )
                }

                // Phone Overlay Sheet
                AnimatedVisibility(
                    visible = state.showPhoneSheet,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    PhoneScreen(
                        activeApp = state.phoneActiveApp,
                        onAppChange = { viewModel.onOpenPhone(it) },
                        chirpFeed = state.chirpFeed,
                        chatThreads = state.chatThreads,
                        bankTransactions = state.bankTransactions,
                        newsFlashes = state.newsFlashes,
                        characterNaraBalance = state.character.naraBalance,
                        onClose = { viewModel.onClosePhone() }
                    )
                }

                // Global Relationship Dossier Overlay
                AnimatedVisibility(
                    visible = state.showRelationshipOverlay,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    RelationshipTrackerOverlay(
                        familyMembers = state.familyMembers,
                        padis = state.padis,
                        playerNara = state.character.naraBalance,
                        onClose = { viewModel.onToggleRelationshipOverlay(false) },
                        onSendGift = { id, isFam, amt -> viewModel.onSendRelationshipGift(id, isFam, amt) },
                        onSpendTime = { id, isFam -> viewModel.onSpendRelationshipTime(id, isFam) },
                        onPledgeLoyalty = { id, isFam -> viewModel.onPledgeLoyaltyPact(id, isFam) },
                        onSettleWahala = { id, isFam -> viewModel.onSettleRelationshipWahala(id, isFam) }
                    )
                }
            }
        }
    }
}
