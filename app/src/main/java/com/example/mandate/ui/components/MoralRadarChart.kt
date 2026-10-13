package com.example.mandate.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-impact graphical 5-axis Radar / Pentagon Canvas Chart
 * Visualizes the player's core psychological attributes:
 * Ambition, Cunning, Risk Appetite, Empathy, Loyalty
 */
@Composable
fun MoralRadarChart(
    ambition: Int,
    empathy: Int,
    cunning: Int,
    risk: Int,
    loyalty: Int,
    modifier: Modifier = Modifier
) {
    // Smooth animated values for each axis
    val animAmbition by animateFloatAsState(targetValue = ambition / 100f, animationSpec = tween(600), label = "anim_ambition")
    val animCunning by animateFloatAsState(targetValue = cunning / 100f, animationSpec = tween(600), label = "anim_cunning")
    val animRisk by animateFloatAsState(targetValue = risk / 100f, animationSpec = tween(600), label = "anim_risk")
    val animEmpathy by animateFloatAsState(targetValue = empathy / 100f, animationSpec = tween(600), label = "anim_empathy")
    val animLoyalty by animateFloatAsState(targetValue = loyalty / 100f, animationSpec = tween(600), label = "anim_loyalty")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("moral_radar_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.horizontalGradient(listOf(NaijaGold, NaijaGreenLight)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PSYCHOLOGICAL RADAR PROFILE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = NaijaGold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "LIVE TACTICAL TELEMETRY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGreenLight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Canvas Radar Chart
            Box(
                modifier = Modifier
                    .size(230.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val centerX = size.width / 2f
                    val centerY = size.height / 2f
                    val maxRadius = size.minDimension / 2f - 16.dp.toPx()

                    // 5 Angles (in radians) starting at top (-90 degrees)
                    val angles = listOf(
                        -Math.PI / 2,                  // 0: Ambition (Top)
                        -Math.PI / 2 + 2 * Math.PI / 5, // 1: Cunning (Top-Right)
                        -Math.PI / 2 + 4 * Math.PI / 5, // 2: Risk (Bottom-Right)
                        -Math.PI / 2 + 6 * Math.PI / 5, // 3: Empathy (Bottom-Left)
                        -Math.PI / 2 + 8 * Math.PI / 5  // 4: Loyalty (Top-Left)
                    )

                    // Draw concentric polygon rings for 25%, 50%, 75%, 100%
                    val ringSteps = listOf(0.25f, 0.5f, 0.75f, 1.0f)
                    ringSteps.forEach { stepRatio ->
                        val ringPath = Path()
                        val r = maxRadius * stepRatio
                        angles.forEachIndexed { index, angle ->
                            val x = (centerX + r * cos(angle)).toFloat()
                            val y = (centerY + r * sin(angle)).toFloat()
                            if (index == 0) ringPath.moveTo(x, y) else ringPath.lineTo(x, y)
                        }
                        ringPath.close()

                        drawPath(
                            path = ringPath,
                            color = if (stepRatio == 1.0f) Color(0x55E5C158) else Color(0x22FFFFFF),
                            style = Stroke(width = if (stepRatio == 1.0f) 2.dp.toPx() else 1.dp.toPx())
                        )
                    }

                    // Draw axis spoke lines
                    angles.forEach { angle ->
                        val endX = (centerX + maxRadius * cos(angle)).toFloat()
                        val endY = (centerY + maxRadius * sin(angle)).toFloat()
                        drawLine(
                            color = Color(0x33E5C158),
                            start = Offset(centerX, centerY),
                            end = Offset(endX, endY),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Build user data polygon
                    val dataPoints = listOf(
                        animAmbition.coerceIn(0.1f, 1f),
                        animCunning.coerceIn(0.1f, 1f),
                        animRisk.coerceIn(0.1f, 1f),
                        animEmpathy.coerceIn(0.1f, 1f),
                        animLoyalty.coerceIn(0.1f, 1f)
                    )

                    val dataPath = Path()
                    val pointOffsets = mutableListOf<Offset>()

                    dataPoints.forEachIndexed { index, value ->
                        val r = maxRadius * value
                        val angle = angles[index]
                        val x = (centerX + r * cos(angle)).toFloat()
                        val y = (centerY + r * sin(angle)).toFloat()
                        val offset = Offset(x, y)
                        pointOffsets.add(offset)
                        if (index == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
                    }
                    dataPath.close()

                    // Fill data polygon with luminous translucent gradient
                    drawPath(
                        path = dataPath,
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0x99288B5B), Color(0x66E5C158), Color(0x220F5A37)),
                            center = Offset(centerX, centerY),
                            radius = maxRadius
                        ),
                        style = Fill
                    )

                    // Draw glowing stroke
                    drawPath(
                        path = dataPath,
                        brush = Brush.sweepGradient(
                            listOf(NaijaGold, NaijaGreenLight, InfoBlue, AlertRed, SuccessGreen, NaijaGold)
                        ),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Draw glowing vertex points
                    val pointColors = listOf(NaijaGold, InfoBlue, AlertRed, SuccessGreen, NaijaGoldLight)
                    pointOffsets.forEachIndexed { index, offset ->
                        drawCircle(
                            color = pointColors[index],
                            radius = 5.dp.toPx(),
                            center = offset
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.dp.toPx(),
                            center = offset
                        )
                    }
                }
            }

            // Legend Chips with live numeric values
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                RadarLegendItem(emoji = "🦅", label = "Ambition", value = ambition, color = NaijaGold)
                RadarLegendItem(emoji = "🦊", label = "Cunning", value = cunning, color = InfoBlue)
                RadarLegendItem(emoji = "🎲", label = "Risk", value = risk, color = AlertRed)
                RadarLegendItem(emoji = "🤝", label = "Empathy", value = empathy, color = SuccessGreen)
                RadarLegendItem(emoji = "🛡️", label = "Loyalty", value = loyalty, color = NaijaGoldLight)
            }
        }
    }
}

@Composable
private fun RadarLegendItem(
    emoji: String,
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(text = emoji, fontSize = 14.sp)
        Text(
            text = "$value%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondaryLight
        )
    }
}
