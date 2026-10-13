package com.example.mandate.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.CrisisType
import com.example.mandate.model.MacroEconomy
import com.example.mandate.model.SecurityLevel
import com.example.ui.theme.*

@Composable
fun PersistentAngerIndexBar(
    macroEconomy: MacroEconomy,
    onInspectTension: () -> Unit,
    modifier: Modifier = Modifier
) {
    val anger = macroEconomy.angerIndex
    val stability = macroEconomy.nationalStabilityPercentage

    // Dynamic color coding based on anger severity
    val (statusColor, statusLabel, pulseAlert) = when {
        anger >= 85 -> Triple(Color(0xFFE53935), "CRITICAL UPRISING", true)
        anger >= 70 -> Triple(AlertRed, "NATIONWIDE STRIKES", true)
        anger >= 50 -> Triple(WarningOrange, "TENSION SIMMERING", false)
        anger >= 35 -> Triple(Color(0xFFFFB300), "GRUMBLING IN MARKETS", false)
        else -> Triple(SuccessGreen, "SOCIAL PEACE & CALM", false)
    }

    // Infinite pulsing animation for high tension
    val infiniteTransition = rememberInfiniteTransition(label = "tension_pulse")
    val pulseScale by if (pulseAlert) {
        infiniteTransition.animateFloat(
            initialValue = 0.95f,
            targetValue = 1.05f,
            animationSpec = infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse_scale"
        )
    } else {
        remember { mutableFloatStateOf(1f) }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persistent_anger_index_indicator")
            .clickable { onInspectTension() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (anger >= 70) Color(0xFF261212) else Color(0xFF0F2218)
        ),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = androidx.compose.ui.graphics.SolidColor(statusColor)
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Top Row: Indicator Title, Pulsing Dot, and Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Pulsing Status Dot
                    Box(
                        modifier = Modifier
                            .scale(pulseScale)
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ANGER INDEX: $anger%",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = statusColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = statusColor.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = statusLabel,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Stability: $stability%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (stability > 50) NaijaGoldLight else AlertRed
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Details",
                        tint = NaijaGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tension Progress Bar
            val animatedAngerProgress by animateFloatAsState(
                targetValue = (anger.coerceIn(0, 100)) / 100f,
                animationSpec = spring(),
                label = "anger_progress"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF1B3024))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedAngerProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(3.dp))
                        .background(statusColor)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Live Crisis Chips Ticker
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Active Crises:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondaryLight
                )
                Spacer(modifier = Modifier.width(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(macroEconomy.activeCrises) { crisis ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF162E20),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF254B35))
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(text = crisis.iconEmoji, fontSize = 10.sp)
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${crisis.title.take(22)}... (+${crisis.angerImpact}%)",
                                    fontSize = 10.sp,
                                    color = NaijaGoldLight,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalTensionDetailDialog(
    macroEconomy: MacroEconomy,
    onDismiss: () -> Unit,
    onDirectIntervention: (String) -> Unit
) {
    val anger = macroEconomy.angerIndex
    val stability = macroEconomy.nationalStabilityPercentage
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("national_tension_detail_dialog")
    ) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFF0C1912),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorderGold))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(20.dp)
            ) {
                // Header with national emblem
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "NATIONAL STABILITY MONITOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Republic Social Tension Ledger",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimaryLight
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondaryLight)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Graphic Street Action Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_protest_street_1791644444827),
                        contentDescription = "Street Flashpoint",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color(0x990A1A12), Color(0xF5091811))
                                )
                            )
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = AlertRed
                    ) {
                        Text(
                            text = "STREET MOBILIZATION HEATMAP",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Core Gauge Summary Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (anger >= 70) Color(0xFF331515) else EmeraldSurfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "CURRENT ANGER INDEX", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(
                                    text = "$anger%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (anger >= 70) AlertRed else if (anger >= 50) WarningOrange else SuccessGreen
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "NATIONWIDE STABILITY", fontSize = 10.sp, color = TextSecondaryLight)
                                Text(
                                    text = "$stability%",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (stability >= 50) NaijaGoldLight else AlertRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // State narrative
                        Text(
                            text = when {
                                anger >= 85 -> "EXTREME CRISIS: Youth uprisings and labour unions have paralyzed commercial hubs. Transport is frozen, barricades burn on highways, and military generals are whispering."
                                anger >= 70 -> "HIGH VOLATILITY: Nationwide strike threats active. ASUU students and transport syndicates are marching on government secretariats."
                                anger >= 50 -> "ELEVATED TENSION: Severe grumbling in market stalls. Food inflation and fuel pump prices are pushing households to the brink."
                                else -> "RELATIVE TRANQUILITY: The populace is absorbing shocks. Social peace allows governance and investments to flourish."
                            },
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            color = TextPrimaryLight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Demographic Sector Breakdown
                Text(
                    text = "SECTOR TENSION BREAKDOWN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                macroEconomy.sectors.forEach { sector ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurface)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
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
                                    text = "${sector.angerLevel}%",
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

                Spacer(modifier = Modifier.height(12.dp))

                // Threat & Instability Risk Gauges
                Text(
                    text = "REGIME & SYSTEMIC RISK THRESHOLDS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Coup / Mutiny Risk", fontSize = 10.sp, color = TextSecondaryLight)
                            Text(
                                text = "${macroEconomy.coupRiskPercentage}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (macroEconomy.coupRiskPercentage > 40) AlertRed else NaijaGoldLight
                            )
                        }
                    }

                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(text = "Street Riot Risk", fontSize = 10.sp, color = TextSecondaryLight)
                            Text(
                                text = "${macroEconomy.riotRiskPercentage}%",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (macroEconomy.riotRiskPercentage > 50) AlertRed else WarningOrange
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rapid Stabilization Interventions
                Text(
                    text = "EMERGENCY STABILIZATION DIRECTIVES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onDirectIntervention("negotiate")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight)
                    ) {
                        Icon(imageVector = Icons.Default.Handshake, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Host Comrade & Union Dialogue (-25% Anger)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            onDirectIntervention("food_relief")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F4D36), contentColor = Color.White)
                    ) {
                        Icon(imageVector = Icons.Default.VolunteerActivism, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Emergency Market Palliatives (-15% Anger, ₦20M)", fontSize = 12.sp)
                    }

                    OutlinedButton(
                        onClick = {
                            onDirectIntervention("crackdown")
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed)
                    ) {
                        Icon(imageVector = Icons.Default.LocalPolice, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Security Forces Crackdown (Enforce Order)", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
