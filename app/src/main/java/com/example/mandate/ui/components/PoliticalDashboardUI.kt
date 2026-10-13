package com.example.mandate.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.data.PoliticalNewsCatalog
import com.example.mandate.data.ResourceTooltipCatalog
import com.example.mandate.data.ResourceTooltipSpec
import com.example.mandate.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * High-Impact Main Dashboard UI for "Mandate: The Long Game".
 *
 * Showcases:
 * 1. Player's comprehensive status indicators (Power, Clout, Energy, Stress, Health, Conscience, Treasury, Drip, Security).
 * 2. Real-time National Macro-political tension meters (Anger Index, Fuel Price, Power Grid, Forex Rate).
 * 3. Curated Live Political News Events Feed with interactive press dispatches, filter tabs, and impact tags.
 * 4. Breaking News marquee alert ticker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PoliticalDashboardUI(
    state: GameState,
    onJumpToStoryFlashpoint: () -> Unit = {},
    onOpenPhone: () -> Unit = {},
    onInspectTension: () -> Unit = {},
    onOpenBDC: () -> Unit = {},
    onExecutePatronage: (costNaira: Long, gain: Int, note: String) -> Unit = { _, _, _ -> },
    onDisburseFamilyGrant: (amount: Long) -> Unit = {},
    onToggleSfx: () -> Unit = {},
    isSfxEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    val character = state.character
    val macro = state.macroEconomy
    val wallet = state.openWorldState.currencyWallet

    // Curated combined news feed (Player-generated + National political wire)
    val allNews = remember(state.newsFlashes, state.character.currentStage, state.character.year, macro.angerIndex) {
        PoliticalNewsCatalog.getCuratedNewsFeed(state)
    }

    var selectedNewsFilter by remember { mutableStateOf("ALL") }
    var selectedNewsDossier by remember { mutableStateOf<NewsFlash?>(null) }
    var showPatronageConfirmDialog by remember { mutableStateOf(false) }
    var showFamilyGrantConfirmDialog by remember { mutableStateOf(false) }
    var activeTooltipSpec by remember { mutableStateOf<ResourceTooltipSpec?>(null) }

    val filteredNews = remember(allNews, selectedNewsFilter) {
        if (selectedNewsFilter == "ALL") allNews
        else allNews.filter { it.category.equals(selectedNewsFilter, ignoreCase = true) }
    }

    // Breaking headline cycling
    var headlineIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            kotlinx.coroutines.delay(6000)
            headlineIndex = (headlineIndex + 1) % PoliticalNewsCatalog.breakingHeadlines.size
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepCharcoal)
            .testTag("political_dashboard_ui")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("dashboard_lazy_column"),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. EXECUTIVE IDENTITY & STAGE BANNER
            item {
                ExecutiveIdentityCard(
                    character = character,
                    onOpenPhone = onOpenPhone,
                    onToggleSfx = onToggleSfx,
                    isSfxEnabled = isSfxEnabled
                )
            }

            // 2. BREAKING POLITICAL WIRE TICKER
            item {
                BreakingWireTicker(
                    headline = PoliticalNewsCatalog.breakingHeadlines[headlineIndex],
                    onTickerClick = { selectedNewsFilter = "BREAKING" }
                )
            }

            // 3. PENDING STORY FLASHPOINT CALLOUT (IF ACTIVE)
            if (state.currentEvent != null) {
                item {
                    PendingFlashpointAlertCard(
                        event = state.currentEvent,
                        onResolveNow = onJumpToStoryFlashpoint
                    )
                }
            }

            // 4. CORE RESOURCE PILLARS: INFLUENCE, WEALTH & FAMILY LOYALTY
            item {
                CoreResourcePillarsSection(
                    resources = state.playerResources,
                    numberFormat = numberFormat,
                    onOpenBDC = onOpenBDC,
                    onPromptPatronage = { showPatronageConfirmDialog = true },
                    onPromptFamilyGrant = { showFamilyGrantConfirmDialog = true },
                    onInspectResource = { key -> activeTooltipSpec = ResourceTooltipCatalog.getSpec(key) }
                )
            }

            // 5. PLAYER STATUS INDICATORS: EXECUTIVE POWER & VITALS MATRIX
            item {
                StatusIndicatorsSection(
                    character = character,
                    wallet = wallet,
                    dripRating = state.openWorldState.dripRating,
                    numberFormat = numberFormat,
                    onInspectResource = { key -> activeTooltipSpec = ResourceTooltipCatalog.getSpec(key) }
                )
            }

            // 5. NATIONAL MACRO SOCIO-POLITICAL TENSION METERS
            item {
                MacroPoliticalTensionSection(
                    macro = macro,
                    onInspectTension = onInspectTension
                )
            }

            // 6. LATEST POLITICAL NEWS EVENTS FEED HEADER & FILTERS
            item {
                NewsFeedHeaderAndFilters(
                    selectedFilter = selectedNewsFilter,
                    totalCount = allNews.size,
                    onFilterSelected = { selectedNewsFilter = it }
                )
            }

            // 7. POLITICAL NEWS EVENT CARDS
            if (filteredNews.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No dispatches found for category '$selectedNewsFilter'. Check back after next turn cycle.",
                                color = TextSecondaryLight,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            } else {
                items(filteredNews, key = { it.id }) { newsItem ->
                    PoliticalNewsEventCard(
                        news = newsItem,
                        onClick = { selectedNewsDossier = newsItem }
                    )
                }
            }
        }
    }

    // --- DETAILED INTELLIGENCE DISPATCH MODAL ---
    selectedNewsDossier?.let { dossier ->
        AlertDialog(
            onDismissRequest = { selectedNewsDossier = null },
            containerColor = DeepCharcoal,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(if (dossier.isBreaking) "🚨" else "📰", fontSize = 20.sp)
                    Column {
                        Text(
                            text = dossier.source.uppercase(),
                            color = NaijaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "OFFICIAL PRESS DISPATCH",
                            color = TextSecondaryLight,
                            fontSize = 9.sp
                        )
                    }
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = if (dossier.isBreaking) AlertRed else Color(0xFF1B3D2F),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${dossier.category} • ${dossier.timeAgo}",
                            color = Color.White,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = dossier.headline,
                        color = TextPrimaryLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 20.sp
                    )

                    HorizontalDivider(color = CardBorderGold)

                    Text(
                        text = dossier.fullDispatch ?: dossier.summary,
                        color = TextPrimaryLight,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )

                    dossier.publicReaction?.let { reaction ->
                        Surface(
                            color = Color(0xFF10281C),
                            shape = RoundedCornerShape(8.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenLight)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "PUBLIC & DELEGATE REACTION:",
                                    color = NaijaGoldLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = reaction,
                                    color = TextPrimaryLight,
                                    fontSize = 11.5.sp,
                                    fontStyle = FontStyle.Italic,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    dossier.impactTag?.let { tag ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text("POLITICAL IMPACT:", color = NaijaGold, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                            Surface(
                                color = Color(0xFF26331A),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = tag,
                                    color = NaijaGoldLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedNewsDossier = null },
                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("close_news_dossier_btn")
                ) {
                    Text("CLOSE DOSSIER", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        )
    }

    // --- PATRONAGE CONFIRMATION MODAL ---
    if (showPatronageConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showPatronageConfirmDialog = false },
            containerColor = DeepCharcoal,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("👑", fontSize = 22.sp)
                    Text(
                        "DEPLOY WARD PATRONAGE",
                        color = NaijaGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Disburse ₦50,000 from treasury logistics to mobilize youth coordinators, motor park chairmen, and ward delegates across key constituencies.",
                        color = TextPrimaryLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Surface(
                        color = Color(0xFF10281C),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenLight)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "TACTICAL DIVIDEND:",
                                color = NaijaGoldLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text("• +15 Composite Political Influence 👑", color = TextPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• +10 Grassroots Street Clout 💥", color = TextPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• Cost: ₦50,000 (Liquid Cash / Bank)", color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPatronageConfirmDialog = false
                        onExecutePatronage(50_000L, 15, "Grassroots ward caucus mobilization")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_deploy_patronage_btn")
                ) {
                    Text("AUTHORIZE DISBURSEMENT", fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPatronageConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_deploy_patronage_btn")
                ) {
                    Text("CANCEL", color = TextSecondaryLight, fontSize = 11.sp)
                }
            }
        )
    }

    // --- FAMILY GRANT CONFIRMATION MODAL ---
    if (showFamilyGrantConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showFamilyGrantConfirmDialog = false },
            containerColor = DeepCharcoal,
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🛡️", fontSize = 22.sp)
                    Text(
                        "DISBURSE FAMILY WELFARE GRANT",
                        color = NaijaGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Send ₦50,000 emergency medical & food subsidy to the ancestral family compound. Elders will offer traditional blessings and prayers for your political ambitions.",
                        color = TextPrimaryLight,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                    Surface(
                        color = Color(0xFF10281C),
                        shape = RoundedCornerShape(8.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenLight)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "KINSHIP DIVIDEND:",
                                color = NaijaGoldLight,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text("• +8 Clan Solidarity & Loyalty 🛡️", color = TextPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• +6 Traditional Elders' Blessing Approval 👴", color = TextPrimaryLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• -15 Black Tax Tension Pressure 📉", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text("• Cost: ₦50,000 (Liquid Cash / Bank)", color = AlertRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showFamilyGrantConfirmDialog = false
                        onDisburseFamilyGrant(50_000L)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("confirm_disburse_family_grant_btn")
                ) {
                    Text("SEND STIPEND", fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showFamilyGrantConfirmDialog = false },
                    modifier = Modifier.testTag("cancel_disburse_family_grant_btn")
                ) {
                    Text("CANCEL", color = TextSecondaryLight, fontSize = 11.sp)
                }
            }
        )
    }

    // --- STRATEGIC RESOURCE IMPACT TOOLTIP MODAL ---
    activeTooltipSpec?.let { spec ->
        ResourceImpactTooltipDialog(
            spec = spec,
            onDismiss = { activeTooltipSpec = null }
        )
    }
}

// =========================================================================
// CORE RESOURCE PILLARS SECTION (INFLUENCE, WEALTH, FAMILY LOYALTY)
// =========================================================================

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CoreResourcePillarsSection(
    resources: PlayerResources,
    numberFormat: NumberFormat,
    onOpenBDC: () -> Unit,
    onPromptPatronage: () -> Unit,
    onPromptFamilyGrant: () -> Unit,
    onInspectResource: (String) -> Unit = {}
) {
    val haptic = LocalHapticFeedback.current
    var activePillarTab by remember { mutableStateOf("INFLUENCE") } // "INFLUENCE", "WEALTH", "FAMILY", "AUDIT"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("🏛️", fontSize = 16.sp)
                Text(
                    text = "THE THREE PILLARS OF POWER",
                    color = NaijaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }
            Surface(
                color = Color(0xFF1B3D2F),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.4f))),
                modifier = Modifier
                    .clickable { onInspectResource(activePillarTab) }
                    .testTag("pillar_impact_guide_btn")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("ℹ️", fontSize = 11.sp)
                    Text(
                        text = "Impacts",
                        color = NaijaGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Informative Long-Press Helper Banner
        Surface(
            color = Color(0xFF10261C),
            shape = RoundedCornerShape(8.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.25f))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("💡", fontSize = 11.sp)
                Text(
                    text = "Long-press any pillar tab, hero score, or sub-metric to see its full gameplay impact.",
                    color = TextSecondaryLight,
                    fontSize = 9.5.sp,
                    lineHeight = 13.sp
                )
            }
        }

        // Segmented Pillar Tab Selector
        Card(
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.4f))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val tabs = listOf(
                    Triple("INFLUENCE", "👑 Influence", "resource_pillar_tab_influence"),
                    Triple("WEALTH", "💰 Wealth", "resource_pillar_tab_wealth"),
                    Triple("FAMILY", "🛡️ Clan", "resource_pillar_tab_family"),
                    Triple("AUDIT", "📜 Ledger", "resource_pillar_tab_audit")
                )

                tabs.forEach { (key, label, tag) ->
                    val isSelected = (activePillarTab == key)
                    Surface(
                        color = if (isSelected) NaijaGold else Color.Transparent,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .combinedClickable(
                                onClick = { activePillarTab = key },
                                onLongClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onInspectResource(key)
                                }
                            )
                            .testTag(tag)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) DeepCharcoal else TextPrimaryLight,
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Active Pillar Card Content
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261B)),
            shape = RoundedCornerShape(14.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("active_pillar_card_${activePillarTab.lowercase()}")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (activePillarTab) {
                    "INFLUENCE" -> {
                        val inf = resources.influence
                        // 1. Top Hero Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onInspectResource("INFLUENCE") },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onInspectResource("INFLUENCE")
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(inf.tierEmoji, fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = inf.tierTitle.uppercase(),
                                        color = NaijaGoldLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Composite Influence Score (Tap for impact)",
                                        color = TextSecondaryLight,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF1B3D2F),
                                shape = RoundedCornerShape(8.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold))
                            ) {
                                Text(
                                    text = "${inf.compositeScore} / 100",
                                    color = NaijaGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Score Progress Bar
                        val progress = (inf.compositeScore / 100f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF142B20))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF00B0FF), NaijaGold)
                                        )
                                    )
                            )
                        }

                        // 4 Sub-Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubMetricMiniTile(
                                icon = "🏛️",
                                label = "Political Capital",
                                value = "${inf.politicalCapital}%",
                                subtitle = "Institutional sway",
                                color = Color(0xFF00B0FF),
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("POLITICAL_CAPITAL") },
                                onLongClick = { onInspectResource("POLITICAL_CAPITAL") }
                            )
                            SubMetricMiniTile(
                                icon = "💥",
                                label = "Street Clout",
                                value = "${inf.streetClout}%",
                                subtitle = "Grassroots mobilization",
                                color = NaijaGold,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("STREET_CLOUT") },
                                onLongClick = { onInspectResource("STREET_CLOUT") }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubMetricMiniTile(
                                icon = "💼",
                                label = "Godfather Favor",
                                value = "${inf.godfatherFavor}%",
                                subtitle = "Kingmaker boardroom backing",
                                color = Color(0xFFAB47BC),
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("GODFATHER_FAVOR") },
                                onLongClick = { onInspectResource("GODFATHER_FAVOR") }
                            )
                            SubMetricMiniTile(
                                icon = "📰",
                                label = "Media Spin",
                                value = "${inf.mediaSpinControl}%",
                                subtitle = "Press narrative control",
                                color = SuccessGreen,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("MEDIA_SPIN") },
                                onLongClick = { onInspectResource("MEDIA_SPIN") }
                            )
                        }

                        // Last Delta Chip
                        Surface(
                            color = Color(0xFF162E22),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RECENT SHIFT:",
                                    color = NaijaGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${if (inf.lastDelta >= 0) "+" else ""}${inf.lastDelta} pts (${inf.lastDeltaReason})",
                                    color = TextPrimaryLight,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Action Button
                        Button(
                            onClick = onPromptPatronage,
                            colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("deploy_patronage_action_btn")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("⚡", fontSize = 16.sp)
                                Text("DEPLOY WARD PATRONAGE (₦50k)", fontWeight = FontWeight.Black, fontSize = 11.5.sp)
                            }
                        }
                    }

                    "WEALTH" -> {
                        val w = resources.wealth
                        // Top Hero Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onInspectResource("WEALTH") },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onInspectResource("WEALTH")
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(w.wealthEmoji, fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = w.wealthTier.uppercase(),
                                        color = NaijaGoldLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Total Combined Net Worth (Tap for impact)",
                                        color = TextSecondaryLight,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Text(
                                text = "₦${numberFormat.format(w.netWorthNaira)}",
                                color = SuccessGreen,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        HorizontalDivider(color = CardBorderGold.copy(alpha = 0.3f))

                        // 4 Sub-Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubMetricMiniTile(
                                icon = "💵",
                                label = "Liquid Cash",
                                value = "₦${numberFormat.format(w.cashNaira)}",
                                subtitle = "Physical mint notes",
                                color = NaijaGoldLight,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("CASH") },
                                onLongClick = { onInspectResource("CASH") }
                            )
                            SubMetricMiniTile(
                                icon = "🏦",
                                label = "Commercial Bank",
                                value = "₦${numberFormat.format(w.bankNaira)}",
                                subtitle = "Interest & savings",
                                color = Color(0xFF00B0FF),
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("BANK") },
                                onLongClick = { onInspectResource("BANK") }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubMetricMiniTile(
                                icon = "🇺🇸",
                                label = "Dom USD FX",
                                value = "$${numberFormat.format(w.domUsd)}",
                                subtitle = "@ ₦${numberFormat.format(w.bdcRate)}/$",
                                color = SuccessGreen,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("DOM_USD") },
                                onLongClick = { onInspectResource("DOM_USD") }
                            )
                            SubMetricMiniTile(
                                icon = "🔥",
                                label = "Daily Living Burn",
                                value = "₦${numberFormat.format(w.dailyBurnNaira)}",
                                subtitle = "Transport, food & fuel",
                                color = AlertRed,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("DAILY_BURN") },
                                onLongClick = { onInspectResource("DAILY_BURN") }
                            )
                        }

                        // Last Delta Chip
                        Surface(
                            color = Color(0xFF162E22),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RECENT TRANSACTION:",
                                    color = NaijaGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${if (w.lastDeltaNaira >= 0) "+" else ""}₦${numberFormat.format(w.lastDeltaNaira)} (${w.lastDeltaReason})",
                                    color = TextPrimaryLight,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Action Button
                        Button(
                            onClick = onOpenBDC,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B0FF), contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("open_bdc_action_btn")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("💱", fontSize = 16.sp)
                                Text("OPEN BDC BUREAU DE CHANGE (FX)", fontWeight = FontWeight.Black, fontSize = 11.5.sp)
                            }
                        }
                    }

                    "FAMILY" -> {
                        val fam = resources.familyLoyalty
                        // Top Hero Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .combinedClickable(
                                    onClick = { onInspectResource("FAMILY_LOYALTY") },
                                    onLongClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onInspectResource("FAMILY_LOYALTY")
                                    }
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(fam.solidarityEmoji, fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = fam.solidarityTier.uppercase(),
                                        color = NaijaGoldLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Clan Solidarity Score (Tap for impact)",
                                        color = TextSecondaryLight,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF1B3D2F),
                                shape = RoundedCornerShape(8.dp),
                                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold))
                            ) {
                                Text(
                                    text = "${fam.clanLoyaltyScore} / 100",
                                    color = NaijaGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Progress Bar
                        val progress = (fam.clanLoyaltyScore / 100f).coerceIn(0f, 1f)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF142B20))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(NaijaGreenLight, NaijaGold)
                                        )
                                    )
                            )
                        }

                        // 4 Sub-Metrics Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubMetricMiniTile(
                                icon = "👴",
                                label = "Elders' Approval",
                                value = "${fam.eldersApprovalScore}%",
                                subtitle = "Ancestral blessings",
                                color = SuccessGreen,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("ELDERS_FAVOR") },
                                onLongClick = { onInspectResource("ELDERS_FAVOR") }
                            )
                            SubMetricMiniTile(
                                icon = "👨‍👩‍👧‍👦",
                                label = "Active Dependents",
                                value = "${fam.activeDependentsCount} clan",
                                subtitle = "Extended household",
                                color = Color(0xFF00B0FF),
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("FAMILY_LOYALTY") },
                                onLongClick = { onInspectResource("FAMILY_LOYALTY") }
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            SubMetricMiniTile(
                                icon = "⚠️",
                                label = "Black Tax Pressure",
                                value = "${fam.blackTaxBurdenIndex}%",
                                subtitle = "Demands vs cash ratio",
                                color = if (fam.blackTaxBurdenIndex > 50) AlertRed else NaijaGold,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("BLACK_TAX") },
                                onLongClick = { onInspectResource("BLACK_TAX") }
                            )
                            SubMetricMiniTile(
                                icon = "📬",
                                label = "Pending Black Tax",
                                value = "₦${numberFormat.format(fam.pendingBlackTaxAmount)}",
                                subtitle = "Unsettled family pleas",
                                color = AlertRed,
                                modifier = Modifier.weight(1f),
                                onClick = { onInspectResource("BLACK_TAX") },
                                onLongClick = { onInspectResource("BLACK_TAX") }
                            )
                        }

                        // Last Delta Chip
                        Surface(
                            color = Color(0xFF162E22),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RECENT CLAN SHIFT:",
                                    color = NaijaGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${if (fam.lastDelta >= 0) "+" else ""}${fam.lastDelta} pts (${fam.lastDeltaReason})",
                                    color = TextPrimaryLight,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Action Button
                        Button(
                            onClick = onPromptFamilyGrant,
                            colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("disburse_family_grant_action_btn")
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎁", fontSize = 16.sp)
                                Text("DISBURSE WELFARE GRANT (₦50k)", fontWeight = FontWeight.Black, fontSize = 11.5.sp)
                            }
                        }
                    }

                    "AUDIT" -> {
                        // Resource Audit Ledger
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📜 RESOURCE TRANSACTION LEDGER",
                                    color = NaijaGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${resources.resourceAuditLog.size} Records",
                                    color = TextSecondaryLight,
                                    fontSize = 9.sp
                                )
                            }

                            if (resources.resourceAuditLog.isEmpty()) {
                                Text(
                                    text = "No recorded transactions yet in this dynasty cycle.",
                                    color = TextSecondaryLight,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            } else {
                                resources.resourceAuditLog.take(8).forEach { entry ->
                                    Surface(
                                        color = Color(0xFF142B20),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                text = when {
                                                    entry.contains("Influence", ignoreCase = true) -> "👑"
                                                    entry.contains("Wealth", ignoreCase = true) || entry.contains("₦") -> "💵"
                                                    else -> "🛡️"
                                                },
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = entry,
                                                color = TextPrimaryLight,
                                                fontSize = 11.sp,
                                                lineHeight = 15.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SubMetricMiniTile(
    icon: String,
    label: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val clickableModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick?.invoke()
            }
        )
    } else Modifier

    Surface(
        color = Color(0xFF142B20),
        shape = RoundedCornerShape(10.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.3f))),
        modifier = modifier.then(clickableModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 13.sp)
                Text(
                    text = value,
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = label,
                color = TextPrimaryLight,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                color = TextSecondaryLight,
                fontSize = 8.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// =========================================================================
// SUB-COMPONENTS FOR DASHBOARD
// =========================================================================

@Composable
private fun ExecutiveIdentityCard(
    character: Character,
    onOpenPhone: () -> Unit,
    onToggleSfx: () -> Unit = {},
    isSfxEnabled: Boolean = true
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("dashboard_executive_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = NaijaGreenPrimary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "GEN ${character.generationNumber}",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = character.name.uppercase(),
                            color = NaijaGoldLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Text(
                        text = "${character.politicalOffice} • ${character.currentStage.displayName}",
                        color = TextPrimaryLight,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Age ${character.age} • Year ${character.year} in Naijaland",
                        color = TextSecondaryLight,
                        fontSize = 10.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Sound FX quick toggle button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10261C))
                            .border(1.2.dp, if (isSfxEnabled) NaijaGold else TextSecondaryLight, CircleShape)
                            .clickable { onToggleSfx() }
                            .testTag("dashboard_sfx_toggle_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isSfxEnabled) "🔊" else "🔇", fontSize = 16.sp)
                    }

                    // Phone quick button
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NaijaGreenPrimary)
                            .border(1.2.dp, NaijaGold, CircleShape)
                            .clickable { onOpenPhone() }
                            .testTag("dashboard_phone_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = "Chirp Phone",
                            tint = NaijaGoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BreakingWireTicker(
    headline: String,
    onTickerClick: () -> Unit
) {
    Surface(
        color = Color(0xFF1E0E0E),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(AlertRed.copy(alpha = 0.8f))),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onTickerClick() }
            .testTag("dashboard_breaking_ticker")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = AlertRed,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "WIRE",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }

            Text(
                text = headline,
                color = TextPrimaryLight,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text("⏩", fontSize = 10.sp)
        }
    }
}

@Composable
private fun PendingFlashpointAlertCard(
    event: GameEvent,
    onResolveNow: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF261D07)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onResolveNow() }
            .testTag("dashboard_pending_flashpoint_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("⚡", fontSize = 14.sp)
                    Text(
                        text = "DECISION REQUIRED: ${event.category.uppercase()}",
                        color = NaijaGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.headline,
                    color = TextPrimaryLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Surface(
                color = NaijaGold,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "RESOLVE ❯",
                    color = DeepCharcoal,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                )
            }
        }
    }
}

@Composable
private fun StatusIndicatorsSection(
    character: Character,
    wallet: CurrencyWallet,
    dripRating: Int,
    numberFormat: NumberFormat,
    onInspectResource: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "📊 EXECUTIVE STATUS INDICATORS",
                color = NaijaGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "Live Telemetry (Long-press to inspect)",
                color = TextSecondaryLight,
                fontSize = 9.sp
            )
        }

        // Row A: Power, Clout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusGaugeTile(
                icon = "🏛️",
                title = "Political Power",
                value = character.politicalCapital,
                maxValue = 100,
                color = Color(0xFF00B0FF),
                rankLabel = when {
                    character.politicalCapital >= 80 -> "Villa Titan"
                    character.politicalCapital >= 50 -> "State Heavyweight"
                    character.politicalCapital >= 25 -> "Ward Operator"
                    else -> "Grassroots Rookie"
                },
                modifier = Modifier.weight(1f),
                onClick = { onInspectResource("POLITICAL_CAPITAL") },
                onLongClick = { onInspectResource("POLITICAL_CAPITAL") }
            )

            StatusGaugeTile(
                icon = "💥",
                title = "Street Clout",
                value = character.streetClout,
                maxValue = 100,
                color = NaijaGold,
                rankLabel = when {
                    character.streetClout >= 80 -> "National Icon"
                    character.streetClout >= 50 -> "Motor Park General"
                    else -> "Local Agbero Fav"
                },
                modifier = Modifier.weight(1f),
                onClick = { onInspectResource("STREET_CLOUT") },
                onLongClick = { onInspectResource("STREET_CLOUT") }
            )
        }

        // Row B: Energy, Stress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusGaugeTile(
                icon = "⚡",
                title = "Stamina / Energy",
                value = character.energy,
                maxValue = 100,
                color = NaijaGreenLight,
                rankLabel = if (character.energy > 40) "Active" else "Fatigued",
                modifier = Modifier.weight(1f),
                onClick = { onInspectResource("ENERGY") },
                onLongClick = { onInspectResource("ENERGY") }
            )

            StatusGaugeTile(
                icon = "🧠",
                title = "Mental Stress",
                value = character.stress,
                maxValue = 100,
                color = if (character.stress > 65) AlertRed else Color(0xFFFFB300),
                rankLabel = if (character.stress > 65) "Burnout Imminent" else "Composed",
                modifier = Modifier.weight(1f),
                onClick = { onInspectResource("STRESS") },
                onLongClick = { onInspectResource("STRESS") }
            )
        }

        // Row C: Health, Drip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusGaugeTile(
                icon = "💖",
                title = "Health Vitality",
                value = character.health,
                maxValue = 100,
                color = Color(0xFF00E676),
                rankLabel = if (character.health > 70) "Robust" else "Medical Care Req.",
                modifier = Modifier.weight(1f),
                onClick = { onInspectResource("HEALTH") },
                onLongClick = { onInspectResource("HEALTH") }
            )

            StatusGaugeTile(
                icon = "👔",
                title = "Executive Drip",
                value = dripRating,
                maxValue = 100,
                color = Color(0xFFAB47BC),
                rankLabel = when {
                    dripRating >= 80 -> "Royal Silk Agbada"
                    dripRating >= 50 -> "Senatorial Kaftan"
                    else -> "Basic Kampala"
                },
                modifier = Modifier.weight(1f),
                onClick = { onInspectResource("DRIP") },
                onLongClick = { onInspectResource("DRIP") }
            )
        }

        // Row D: Conscience vs Cunning Alignment Bar
        AlignmentBalanceTile(
            conscience = character.conscience,
            cunning = character.cunning,
            onClick = { onInspectResource("CONSCIENCE") },
            onLongClick = { onInspectResource("CONSCIENCE") }
        )

        // Row E: Treasury Strip
        TreasuryStripCard(
            cashNaira = character.naraBalance,
            bankNaira = wallet.bankNaira,
            domUsd = wallet.domUsd,
            bodyguardLevel = character.bodyguardLevel,
            numberFormat = numberFormat,
            onClick = { onInspectResource("WEALTH") },
            onLongClick = { onInspectResource("WEALTH") }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StatusGaugeTile(
    icon: String,
    title: String,
    value: Int,
    maxValue: Int,
    color: Color,
    rankLabel: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val clickableModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick?.invoke()
            }
        )
    } else Modifier

    Card(
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.4f))),
        modifier = modifier.then(clickableModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(icon, fontSize = 14.sp)
                Text(
                    text = "$value/$maxValue",
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Text(
                text = title,
                color = TextPrimaryLight,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            // Progress bar
            val progress = (value.toFloat() / maxValue.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF142B20))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(color)
                )
            }

            Text(
                text = rankLabel,
                color = TextSecondaryLight,
                fontSize = 9.sp,
                maxLines = 1
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AlignmentBalanceTile(
    conscience: Int,
    cunning: Int,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val clickableModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick?.invoke()
            }
        )
    } else Modifier

    Card(
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.4f))),
        modifier = Modifier
            .fillMaxWidth()
            .then(clickableModifier)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚖️ Moral Conscience: $conscience%",
                    color = SuccessGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🦊 Machiavellian Cunning: $cunning%",
                    color = AlertRed,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val total = (conscience + cunning).coerceAtLeast(1)
            val conscienceRatio = (conscience.toFloat() / total.toFloat()).coerceIn(0.05f, 0.95f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(conscienceRatio)
                        .background(SuccessGreen)
                )
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1f - conscienceRatio)
                        .background(AlertRed)
                )
            }

            Text(
                text = if (conscience >= cunning) "Ethical Reformer • Rebuffs dirty backroom bribes (Tap for impact)" else "Calculated Realist • Sacrifices ideals for decisive leverage (Tap for impact)",
                color = TextSecondaryLight,
                fontSize = 9.sp
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TreasuryStripCard(
    cashNaira: Long,
    bankNaira: Long,
    domUsd: Long,
    bodyguardLevel: Int,
    numberFormat: NumberFormat,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null
) {
    val haptic = LocalHapticFeedback.current
    val clickableModifier = if (onClick != null || onLongClick != null) {
        Modifier.combinedClickable(
            onClick = { onClick?.invoke() },
            onLongClick = {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                onLongClick?.invoke()
            }
        )
    } else Modifier

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F261B)),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold.copy(alpha = 0.6f))),
        modifier = Modifier
            .fillMaxWidth()
            .then(clickableModifier)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("💵 PHYSICAL CASH", color = TextSecondaryLight, fontSize = 8.5.sp)
                Text(
                    text = "₦${numberFormat.format(cashNaira)}",
                    color = NaijaGoldLight,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            VerticalDivider(modifier = Modifier.height(24.dp), color = CardBorderGold)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🏦 COMMERCIAL BANK", color = TextSecondaryLight, fontSize = 8.5.sp)
                Text(
                    text = "₦${numberFormat.format(bankNaira)}",
                    color = Color(0xFF00B0FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            VerticalDivider(modifier = Modifier.height(24.dp), color = CardBorderGold)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🇺🇸 BDC DOM USD", color = TextSecondaryLight, fontSize = 8.5.sp)
                Text(
                    text = "$${numberFormat.format(domUsd)}",
                    color = SuccessGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            VerticalDivider(modifier = Modifier.height(24.dp), color = CardBorderGold)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🛡️ SECURITY", color = TextSecondaryLight, fontSize = 8.5.sp)
                Text(
                    text = "LVL $bodyguardLevel",
                    color = AlertRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun MacroPoliticalTensionSection(
    macro: MacroEconomy,
    onInspectTension: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🇳🇬 NATIONAL MACRO TENSION METERS",
                color = NaijaGold,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
            Surface(
                color = if (macro.angerIndex > 65) AlertRed else Color(0xFF1B3D2F),
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier.clickable { onInspectTension() }
            ) {
                Text(
                    text = macro.securityLevel.label.uppercase(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold.copy(alpha = 0.4f))),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Anger Index Bar
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "🔥 Public Anger Index",
                            color = TextPrimaryLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${macro.angerIndex}%",
                            color = if (macro.angerIndex > 65) AlertRed else NaijaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    val progress = (macro.angerIndex.toFloat() / 100f).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(Color(0xFF152A1E))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (macro.angerIndex > 65) AlertRed else NaijaGold)
                        )
                    }
                }

                // Macro Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("🛢️ FUEL PRICE", color = TextSecondaryLight, fontSize = 8.5.sp)
                        Text(
                            text = "₦${macro.fuelPumpPrice}/L",
                            color = TextPrimaryLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text("💵 FOREX BDC", color = TextSecondaryLight, fontSize = 8.5.sp)
                        Text(
                            text = "₦${macro.naraToDollar.toInt()}/$",
                            color = Color(0xFF00B0FF),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text("⚡ BRENT CRUDE", color = TextSecondaryLight, fontSize = 8.5.sp)
                        Text(
                            text = "$${macro.oilBarrelPrice.toInt()}/bbl",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text("🏦 RESERVES", color = TextSecondaryLight, fontSize = 8.5.sp)
                        Text(
                            text = "$${macro.treasuryReservesBillionUsd}B",
                            color = NaijaGoldLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsFeedHeaderAndFilters(
    selectedFilter: String,
    totalCount: Int,
    onFilterSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
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
                Text("📰", fontSize = 16.sp)
                Text(
                    text = "LATEST POLITICAL NEWS & WIRES",
                    color = NaijaGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
            }

            Surface(
                color = DeepCharcoal,
                shape = RoundedCornerShape(6.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
            ) {
                Text(
                    text = "$totalCount Dispatches",
                    color = NaijaGoldLight,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // News filter chips
        val filters = listOf("ALL", "BREAKING", "POLITICS", "CRISIS", "ECONOMY", "SCANDAL")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { category ->
                val isSelected = (selectedFilter == category)
                Surface(
                    color = if (isSelected) NaijaGold else EmeraldSurfaceVariant,
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(if (isSelected) NaijaGoldLight else CardBorderGold.copy(alpha = 0.3f))),
                    modifier = Modifier
                        .clickable { onFilterSelected(category) }
                        .testTag("news_filter_${category.lowercase()}")
                ) {
                    Text(
                        text = category,
                        color = if (isSelected) DeepCharcoal else TextPrimaryLight,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PoliticalNewsEventCard(
    news: NewsFlash,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (news.isBreaking) Color(0xFF1E1414) else EmeraldSurfaceVariant
        ),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = SolidColor(if (news.isBreaking) AlertRed else CardBorderGold.copy(alpha = 0.5f))
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .clickable { onClick() }
            .testTag("news_card_${news.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Top Row: Source, Category, Timestamp
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
                        color = if (news.isBreaking) AlertRed else Color(0xFF1B3D2F),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = news.category.uppercase(),
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = news.source.uppercase(),
                        color = NaijaGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Text(
                    text = news.timeAgo,
                    color = TextSecondaryLight,
                    fontSize = 9.sp
                )
            }

            // Headline
            Text(
                text = news.headline,
                color = TextPrimaryLight,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Summary
            Text(
                text = news.summary,
                color = TextPrimaryLight.copy(alpha = 0.9f),
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Bottom strip: Impact tag & Tap for Dispatch prompt
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (news.impactTag != null) {
                    Surface(
                        color = Color(0xFF26331A),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = news.impactTag,
                            color = NaijaGoldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                Text(
                    text = "READ DISPATCH ❯",
                    color = NaijaGold,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
