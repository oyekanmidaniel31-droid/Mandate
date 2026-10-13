package com.example.mandate.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.Character
import com.example.mandate.model.MacroEconomy
import com.example.ui.theme.*

/**
 * High-impact Graphical Power Standoff Arena
 * Displays the dynamic political confrontation between player's coalition
 * and the opposing state/street resistance.
 */
@Composable
fun PowerStandoffArena(
    character: Character,
    macroEconomy: MacroEconomy,
    modifier: Modifier = Modifier
) {
    // Player strength score (0 - 100)
    val playerPower = ((character.streetClout + character.politicalCapital + character.godfatherFavor) / 3f).coerceIn(10f, 95f)
    // Opposition resistance score (0 - 100)
    val resistancePower = ((macroEconomy.angerIndex + character.stress + (100 - character.health)) / 3f).coerceIn(10f, 95f)

    val total = (playerPower + resistancePower).coerceAtLeast(1f)
    val playerRatio = (playerPower / total).coerceIn(0.15f, 0.85f)

    // Animated electrical pulse effect
    val infiniteTransition = rememberInfiniteTransition(label = "standoff_pulse")
    val sparkOffset by infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "spark_anim"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("power_standoff_arena"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(
            brush = Brush.horizontalGradient(listOf(SuccessGreen, NaijaGold, AlertRed))
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = NaijaGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "TACTICAL POWER TUG-OF-WAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = NaijaGold,
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (playerRatio >= 0.5f) NaijaGreenDark else Color(0xFF331414)
                ) {
                    Text(
                        text = if (playerRatio >= 0.5f) "MANDATE SECURE" else "CRITICAL PRESSURE",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (playerRatio >= 0.5f) SuccessGreen else AlertRed,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Two-sided labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "YOUR ALLIANCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = SuccessGreen
                    )
                    Text(
                        text = "${(playerRatio * 100).toInt()}% DOMINANCE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryLight
                    )
                }

                Surface(
                    shape = CircleShape,
                    color = Color(0xFF1B3D2A),
                    modifier = Modifier.size(24.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "VS", fontSize = 9.sp, fontWeight = FontWeight.Black, color = NaijaGold)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "STREET & OPPOSITION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = AlertRed
                    )
                    Text(
                        text = "${((1f - playerRatio) * 100).toInt()}% RESISTANCE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Graphic Dynamic Standoff Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(18.dp)
                    .clip(RoundedCornerShape(9.dp))
                    .background(Color(0xFF0A1F14))
                    .border(1.dp, Color(0xFF1D4730), RoundedCornerShape(9.dp))
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val splitX = w * playerRatio

                    // Left Side: Player green/gold gradient
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFF0F5A37), Color(0xFF288B5B), Color(0xFFE5C158)),
                            startX = 0f,
                            endX = splitX
                        ),
                        topLeft = Offset(0f, 0f),
                        size = androidx.compose.ui.geometry.Size(splitX, h)
                    )

                    // Right Side: Opposition red/dark amber gradient
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(Color(0xFFE53935), Color(0xFF7F1D1D), Color(0xFF330B0B)),
                            startX = splitX,
                            endX = w
                        ),
                        topLeft = Offset(splitX, 0f),
                        size = androidx.compose.ui.geometry.Size(w - splitX, h)
                    )

                    // Electric clash line at interface
                    val clashX = splitX + sparkOffset
                    drawLine(
                        color = Color.White,
                        start = Offset(clashX, 0f),
                        end = Offset(clashX, h),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawCircle(
                        color = NaijaGold,
                        radius = 5.dp.toPx(),
                        center = Offset(clashX, h / 2f)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 2.dp.toPx(),
                        center = Offset(clashX, h / 2f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sub-metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Clout: ${character.streetClout}% • Favor: ${character.godfatherFavor}%",
                    fontSize = 10.sp,
                    color = NaijaGoldLight
                )
                Text(
                    text = "Anger: ${macroEconomy.angerIndex}% • Stress: ${character.stress}%",
                    fontSize = 10.sp,
                    color = if (macroEconomy.angerIndex > 65) AlertRed else TextSecondaryLight
                )
            }
        }
    }
}
