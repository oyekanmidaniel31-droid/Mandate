package com.example.mandate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.MacroEconomy
import com.example.mandate.model.NigeriaFederation
import com.example.mandate.model.NigeriaState
import com.example.mandate.model.SecurityLevel
import com.example.mandate.ui.components.StatProgressBar
import com.example.mandate.ui.components.TacticalHotspotRadar
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun CrisisUprisingScreen(
    macroEconomy: MacroEconomy,
    onCrisisAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("crisis_uprising_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Graphic Protest Theater Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_protest_street_1791644444827),
                    contentDescription = "Street Action Theater",
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
                        .padding(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AlertRed
                    ) {
                        Text(
                            text = "LIVE CRISIS THEATER • ACTIVE DISPATCH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "THE STREETS OF NAIJALAND",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
                Text(
                    text = "CRISIS ENGINE & YOUTH ANGER",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NaijaGoldLight
                )
                Text(
                    text = "Live simulation of the economy, inflation, strikes, and nationwide street unrest.",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // Anger Index Gauge Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("anger_gauge_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (macroEconomy.angerIndex > 70) Color(0xFF331111) else EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(
                        if (macroEconomy.angerIndex > 70) AlertRed else CardBorderGold
                    )
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NATIONAL ANGER INDEX",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (macroEconomy.angerIndex > 70) AlertRed else NaijaGoldLight
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(macroEconomy.securityLevel.severityColorHex)
                        ) {
                            Text(
                                text = macroEconomy.securityLevel.label.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    StatProgressBar(
                        label = "Street Volatility Meter",
                        value = macroEconomy.angerIndex,
                        color = if (macroEconomy.angerIndex > 70) AlertRed else if (macroEconomy.angerIndex > 50) WarningOrange else SuccessGreen
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (macroEconomy.angerIndex > 75)
                            "CRITICAL: Youths, workers, and civil societies are massing at major highway tollgates and city squares! Threat of regime collapse!"
                        else if (macroEconomy.angerIndex > 50)
                            "ELEVATED: Grumbling in the markets over transport fares and petrol costs. Student unions threaten mass action."
                        else
                            "CALM: The populace is enduring the hardships with resilience. Relative social peace prevailing.",
                        fontSize = 12.sp,
                        color = TextPrimaryLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Tactical National Conflict Map
        item {
            TacticalHotspotRadar(
                nationalAnger = macroEconomy.angerIndex,
                securityLevel = macroEconomy.securityLevel
            )
        }

        // Federation Regional Crises by State
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FEDERATION REGIONAL CRISES (BY STATE)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )
                Text(
                    text = "36 States & FCT",
                    fontSize = 10.sp,
                    color = TextSecondaryLight
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth().testTag("state_crises_row")
            ) {
                this.items(NigeriaFederation.allStates) { state ->
                    Card(
                        modifier = Modifier.width(260.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (state.dangerLevel > 60) AlertRed else CardBorderGold
                            )
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
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
                                    Text(state.flagEmoji, fontSize = 16.sp)
                                    Text(
                                        text = state.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaijaGoldLight
                                    )
                                }
                                Surface(
                                    color = if (state.dangerLevel > 60) AlertRed.copy(alpha = 0.2f) else DeepCharcoal,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "${state.dangerLevel}% DANGER",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (state.dangerLevel > 60) AlertRed else WarningOrange,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "${state.primaryCrisis.iconEmoji} ${state.primaryCrisis.title}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )

                            Text(
                                text = state.crisisNarrative,
                                fontSize = 10.sp,
                                color = TextSecondaryLight,
                                lineHeight = 13.sp,
                                maxLines = 3
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Zone: ${state.zone.shortCode}",
                                    fontSize = 9.sp,
                                    color = NaijaGreenLight
                                )
                                Text(
                                    text = "${state.delegateCount} Delegates",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Macro Indicators
        item {
            Text(
                text = "MACROECONOMIC GAUGES",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "NARA TO USD", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(
                            text = "$1 = ₦${macroEconomy.naraToDollar.toInt()}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGoldLight
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "OIL BENCHMARK", fontSize = 10.sp, color = TextSecondaryLight)
                        Text(
                            text = "$${String.format(Locale.US, "%.1f", macroEconomy.oilBarrelPrice)}/bbl",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }

        // Active Crisis List
        item {
            Text(
                text = "ACTIVE CRISES DRIVING ANGER",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )
        }

        items(macroEconomy.activeCrises) { crisis ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF222C24)),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF2E4E39)))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = crisis.iconEmoji, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = crisis.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight,
                                modifier = Modifier.weight(1f)
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFF3B1818)
                            ) {
                                Text(
                                    text = "+${crisis.angerImpact}% ANGER",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AlertRed,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = crisis.shortDesc,
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Affected Demographic: ${crisis.primarySector}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = NaijaGoldLight
                        )
                    }
                }
            }
        }

        // Sector Tension Radar Section
        item {
            Text(
                text = "DEMOGRAPHIC SECTOR TENSIONS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )
        }

        items(macroEconomy.sectors) { sector ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurface)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = sector.iconEmoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = sector.sectorName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                        }
                        Text(
                            text = "${sector.angerLevel}% Anger",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (sector.angerLevel >= 70) AlertRed else if (sector.angerLevel >= 50) WarningOrange else SuccessGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = sector.grievanceSummary,
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (sector.angerLevel.coerceIn(0, 100)) / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (sector.angerLevel >= 70) AlertRed else if (sector.angerLevel >= 50) WarningOrange else SuccessGreen,
                        trackColor = Color(0xFF1B3626)
                    )
                }
            }
        }

        // Crisis Interventions
        item {
            Text(
                text = "DIRECT STATE INTERVENTIONS",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { onCrisisAction("negotiate") },
                    modifier = Modifier.fillMaxWidth().testTag("crisis_action_negotiate"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight)
                ) {
                    Icon(imageVector = Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Host Comrade & Student Dialogue (-Anger)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onCrisisAction("food_relief") },
                    modifier = Modifier.fillMaxWidth().testTag("crisis_action_food"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1B4E38), contentColor = Color.White)
                ) {
                    Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Distribute Grassroots Rice & Palliatives (₦20M)", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { onCrisisAction("crackdown") },
                    modifier = Modifier.fillMaxWidth().testTag("crisis_action_crackdown"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                ) {
                    Icon(imageVector = Icons.Default.LocalPolice, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Impose 24-Hour Curfew & Security Crackdown", fontSize = 13.sp)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}
