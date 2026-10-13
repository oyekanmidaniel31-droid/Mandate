package com.example.mandate.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

data class OwambeRefreshment(
    val id: String,
    val title: String,
    val costNara: Long,
    val emoji: String,
    val description: String,
    val energyGain: Int,
    val stressLoss: Int,
    val cloutGain: Int
)

data class OwambeVIPGuest(
    val id: String,
    val name: String,
    val roleTitle: String,
    val avatarEmoji: String,
    val quote: String,
    val actions: List<OwambeVIPAction>
)

data class OwambeVIPAction(
    val actionId: String,
    val label: String,
    val costNara: Long,
    val effectSummary: String,
    val cloutReward: Int,
    val influenceReward: Int
)

@Composable
fun OwambeSimsPanel(
    naraBalance: Long,
    streetClout: Int,
    socialInfluence: Int,
    partyHype: Int,
    totalSprayedTonight: Long,
    currentOutfit: String,
    currentDanceStyle: String,
    onSpraySingleNote: () -> Unit,
    onSprayBundle: () -> Unit,
    onSprayUSD: () -> Unit,
    onSelectDanceStyle: (String) -> Unit,
    onSelectOutfit: (String) -> Unit,
    onOrderRefreshment: (OwambeRefreshment) -> Unit,
    onInteractVIP: (OwambeVIPGuest, OwambeVIPAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    var selectedTab by remember { mutableStateOf("SPRAY") } // "SPRAY", "GUESTS", "BAR", "FITS"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("owambe_sims_panel"),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Meters Dashboard: Street Clout, Social Influence, and Total Sprayed
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
            border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "STREET CLOUT LEVEL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGoldLight
                        )
                        val rankTitle = when {
                            streetClout >= 80 -> "👑 AGBA AWO / OTUNBA"
                            streetClout >= 60 -> "🦅 AREA FATHER"
                            streetClout >= 40 -> "✨ LAGOS BIG BOY"
                            streetClout >= 20 -> "🔥 STREET HUSTLER"
                            else -> "🐣 AJEBUTTER"
                        }
                        Text(
                            text = rankTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF142B1D),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = "SPRAYED TONIGHT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondaryLight
                            )
                            Text(
                                text = "₦${numberFormat.format(totalSprayedTonight)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = NaijaGold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bars
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Street Clout Meter
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Clout Meter", fontSize = 10.sp, color = TextSecondaryLight)
                            Text(text = "$streetClout%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = NaijaGold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { streetClout / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = NaijaGold,
                            trackColor = Color(0xFF132B1E)
                        )
                    }

                    // Social Influence Meter
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Social Influence", fontSize = 10.sp, color = TextSecondaryLight)
                            Text(text = "$socialInfluence%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { socialInfluence / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(7.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = SuccessGreen,
                            trackColor = Color(0xFF132B1E)
                        )
                    }
                }
            }
        }

        // Sub-Tab Switcher (Sims Category Hub)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SubTabChip(
                label = "💸 Spray Cash",
                isSelected = selectedTab == "SPRAY",
                onClick = { selectedTab = "SPRAY" }
            )
            SubTabChip(
                label = "👥 VIP Guests",
                isSelected = selectedTab == "GUESTS",
                onClick = { selectedTab = "GUESTS" }
            )
            SubTabChip(
                label = "🍾 VIP Bar & Chops",
                isSelected = selectedTab == "BAR",
                onClick = { selectedTab = "BAR" }
            )
            SubTabChip(
                label = "🕺 Fits & Dance",
                isSelected = selectedTab == "FITS",
                onClick = { selectedTab = "FITS" }
            )
        }

        // Tab Content
        when (selectedTab) {
            "SPRAY" -> {
                OwambeSprayControls(
                    naraBalance = naraBalance,
                    onSpraySingleNote = onSpraySingleNote,
                    onSprayBundle = onSprayBundle,
                    onSprayUSD = onSprayUSD,
                    numberFormat = numberFormat
                )
            }
            "GUESTS" -> {
                OwambeVIPGuestsList(
                    naraBalance = naraBalance,
                    onInteract = onInteractVIP,
                    numberFormat = numberFormat
                )
            }
            "BAR" -> {
                OwambeRefreshmentList(
                    naraBalance = naraBalance,
                    onOrder = onOrderRefreshment,
                    numberFormat = numberFormat
                )
            }
            "FITS" -> {
                OwambeStyleAndDanceSelector(
                    currentOutfit = currentOutfit,
                    currentDanceStyle = currentDanceStyle,
                    onSelectOutfit = onSelectOutfit,
                    onSelectDanceStyle = onSelectDanceStyle
                )
            }
        }
    }
}

@Composable
private fun SubTabChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) NaijaGold else Color(0xFF132D20),
        label = "chipBg"
    )
    val textColor = if (isSelected) DeepCharcoal else TextSecondaryLight

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = bgColor,
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("owambe_tab_${label.take(5)}")
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun OwambeSprayControls(
    naraBalance: Long,
    onSpraySingleNote: () -> Unit,
    onSprayBundle: () -> Unit,
    onSprayUSD: () -> Unit,
    numberFormat: NumberFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CASH SPRAY MACHINE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = NaijaGold
                )
                Text(
                    text = "Bank: ₦${numberFormat.format(naraBalance)}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimaryLight
                )
            }

            // Primary Huge Spray Button: ₦1,000 Note
            Button(
                onClick = onSpraySingleNote,
                enabled = naraBalance >= 1000,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .testTag("spray_1000_note_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaijaGold,
                    contentColor = DeepCharcoal
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null,
                        tint = DeepCharcoal,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "SPRAY ₦1,000 NOTE 💸",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = DeepCharcoal
                        )
                        Text(
                            text = "Peel crisp ₦1,000 banknote on Fuji praise singer!",
                            fontSize = 10.sp,
                            color = DeepCharcoal.copy(alpha = 0.85f)
                        )
                    }
                }
            }

            // Dual Secondary Spray Actions: Bundle & Hard Currency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Bundle Turbo Spray (₦10,000)
                OutlinedButton(
                    onClick = onSprayBundle,
                    enabled = naraBalance >= 10_000,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("spray_bundle_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF163825),
                        contentColor = NaijaGoldLight
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = SolidColor(NaijaGold)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TURBO BUNDLE 💥",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGold
                        )
                        Text(
                            text = "₦10k (10 Notes)",
                            fontSize = 10.sp,
                            color = TextSecondaryLight
                        )
                    }
                }

                // $100 Hard Currency FX (₦150k)
                OutlinedButton(
                    onClick = onSprayUSD,
                    enabled = naraBalance >= 150_000,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("spray_usd_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color(0xFF142B1A),
                        contentColor = SuccessGreen
                    ),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        brush = SolidColor(SuccessGreen)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SPRAY $100 BILLS 💵",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                        Text(
                            text = "Dorime Anthem (₦150k)",
                            fontSize = 10.sp,
                            color = TextSecondaryLight
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwambeVIPGuestsList(
    naraBalance: Long,
    onInteract: (OwambeVIPGuest, OwambeVIPAction) -> Unit,
    numberFormat: NumberFormat
) {
    val guests = remember {
        listOf(
            OwambeVIPGuest(
                id = "godfather",
                name = "Chief Alabi 'Agba'",
                roleTitle = "Lakos Political Godfather",
                avatarEmoji = "👑",
                quote = "A o ba e gbe agbo soke! We control the delegates.",
                actions = listOf(
                    OwambeVIPAction(
                        actionId = "prostrate_homage",
                        label = "Prostrate & Pay Respect",
                        costNara = 0,
                        effectSummary = "Chief smiles proudly and taps your shoulder.",
                        cloutReward = 10,
                        influenceReward = 15
                    ),
                    OwambeVIPAction(
                        actionId = "godfather_tribute",
                        label = "Present ₦50k Gin & Slush Tribute",
                        costNara = 50_000,
                        effectSummary = "Seals party endorsement for your next election campaign!",
                        cloutReward = 20,
                        influenceReward = 30
                    )
                )
            ),
            OwambeVIPGuest(
                id = "alhaja_ronke",
                name = "Alhaja Ronke",
                roleTitle = "Balogun Fabric Tycoon & Socialite",
                avatarEmoji = "🧕",
                quote = "Look at this pure Swiss gold lace! Money talks!",
                actions = listOf(
                    OwambeVIPAction(
                        actionId = "praise_gele",
                        label = "Compliment Luxury Gele & Lace",
                        costNara = 0,
                        effectSummary = "Alhaja laughs warmly and introduces you to market union leaders.",
                        cloutReward = 8,
                        influenceReward = 12
                    ),
                    OwambeVIPAction(
                        actionId = "buy_fabric_bundle",
                        label = "Sponsor Aso-Ebi Bundle (₦30k)",
                        costNara = 30_000,
                        effectSummary = "30 high-society women wear your campaign colors!",
                        cloutReward = 25,
                        influenceReward = 20
                    )
                )
            ),
            OwambeVIPGuest(
                id = "dj_kaycee",
                name = "DJ Kaycee & The Hype Man",
                roleTitle = "Celebrity DJ & Megaphone Voice",
                avatarEmoji = "🎧",
                quote = "Dorime! Who is spraying that heavy money?!",
                actions = listOf(
                    OwambeVIPAction(
                        actionId = "tip_megaphone",
                        label = "Tip ₦10k for Megaphone Shoutout",
                        costNara = 10_000,
                        effectSummary = "'Make way for the Chairman!' echoes across the entire hall!",
                        cloutReward = 22,
                        influenceReward = 10
                    )
                )
            ),
            OwambeVIPGuest(
                id = "area_boys",
                name = "MC Jagaban (Agbero Boss)",
                roleTitle = "Motor Park Boys at Event Gate",
                avatarEmoji = "🦁",
                quote = "Chairman! Your boys dey hail! Give us small weekend flex!",
                actions = listOf(
                    OwambeVIPAction(
                        actionId = "settle_boys",
                        label = "Settle the Boys with ₦15,000",
                        costNara = 15_000,
                        effectSummary = "Area boys salute: 'Leader! Nothing do you!' 100% street security guaranteed.",
                        cloutReward = 30,
                        influenceReward = 5
                    )
                )
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        guests.forEach { guest ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = guest.avatarEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = guest.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaijaGoldLight
                            )
                            Text(
                                text = guest.roleTitle,
                                fontSize = 10.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "\"${guest.quote}\"",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        guest.actions.forEach { action ->
                            val canAfford = naraBalance >= action.costNara
                            Button(
                                onClick = { onInteract(guest, action) },
                                enabled = canAfford,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (action.costNara > 0) Color(0xFF1E462E) else Color(0xFF163723),
                                    contentColor = NaijaGoldLight
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = action.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    if (action.costNara > 0) {
                                        Text(
                                            text = "-₦${numberFormat.format(action.costNara)}",
                                            fontSize = 9.sp,
                                            color = AlertRed
                                        )
                                    } else {
                                        Text(
                                            text = "FREE",
                                            fontSize = 9.sp,
                                            color = SuccessGreen
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

@Composable
private fun OwambeRefreshmentList(
    naraBalance: Long,
    onOrder: (OwambeRefreshment) -> Unit,
    numberFormat: NumberFormat
) {
    val items = remember {
        listOf(
            OwambeRefreshment(
                id = "star_asun",
                title = "Star/Trophy Beer + Peppered Asun Meat",
                costNara = 15_000,
                emoji = "🍺",
                description = "Chilled local brew and hot spicy goat meat from the live grill.",
                energyGain = 20,
                stressLoss = 15,
                cloutGain = 5
            ),
            OwambeRefreshment(
                id = "jollof_dodo",
                title = "Smoky Party Jollof Rice & Fried Plantain",
                costNara = 25_000,
                emoji = "🍛",
                description = "Firewood-cooked classic party rice served with juicy chicken lap.",
                energyGain = 35,
                stressLoss = 20,
                cloutGain = 10
            ),
            OwambeRefreshment(
                id = "moet_hennessy",
                title = "Moët & Hennessy Ice Bucket Parade",
                costNara = 120_000,
                emoji = "🍾",
                description = "VIP bottle service escorted with handheld sparklers and fanfare music!",
                energyGain = 15,
                stressLoss = 30,
                cloutGain = 35
            )
        )
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.forEach { item ->
            val canAfford = naraBalance >= item.costNara
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = item.emoji, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = item.title,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGoldLight
                        )
                        Text(
                            text = item.description,
                            fontSize = 10.sp,
                            color = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF142B1A)) {
                                Text(
                                    text = "+${item.energyGain} Energy",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF142B1A)) {
                                Text(
                                    text = "-${item.stressLoss} Stress",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SuccessGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onOrder(item) },
                        enabled = canAfford,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NaijaGold,
                            contentColor = DeepCharcoal
                        )
                    ) {
                        Text(
                            text = "₦${numberFormat.format(item.costNara)}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OwambeStyleAndDanceSelector(
    currentOutfit: String,
    currentDanceStyle: String,
    onSelectOutfit: (String) -> Unit,
    onSelectDanceStyle: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "CHOOSE YOUR DANCE MOVE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("GBESE", "Gbese! 🦵"),
                    Pair("ZANKU", "Zanku Legwork 👟"),
                    Pair("FOCUS", "Focus Dance 🎯"),
                    Pair("ODOGWU", "Odogwu Swag 👑")
                ).forEach { (id, label) ->
                    val isSelected = currentDanceStyle == id
                    OutlinedButton(
                        onClick = { onSelectDanceStyle(id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) NaijaGold else Color(0xFF132D1F),
                            contentColor = if (isSelected) DeepCharcoal else NaijaGoldLight
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(if (isSelected) NaijaGold else CardBorderGold)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "LAGOS EVENT WARDROBE (FITS)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    Pair("EMERALD_AGBADA", "Gold Agbada"),
                    Pair("ROYAL_LACE", "Royal Lace"),
                    Pair("SENATOR_SUIT", "Senator Fit"),
                    Pair("STREET_ICE", "Cuban Ice")
                ).forEach { (id, label) ->
                    val isSelected = currentOutfit == id
                    OutlinedButton(
                        onClick = { onSelectOutfit(id) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) NaijaGold else Color(0xFF132D1F),
                            contentColor = if (isSelected) DeepCharcoal else NaijaGoldLight
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(
                            brush = SolidColor(if (isSelected) NaijaGold else CardBorderGold)
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
