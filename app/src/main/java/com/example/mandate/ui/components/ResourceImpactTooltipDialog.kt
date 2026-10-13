package com.example.mandate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.data.ResourceTooltipSpec
import com.example.ui.theme.*

/**
 * Informative Tooltip Dialog that displays detailed gameplay explanations
 * when a user long-presses (or taps info on) Influence, Wealth, or Family Loyalty indicators.
 */
@Composable
fun ResourceImpactTooltipDialog(
    spec: ResourceTooltipSpec,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .testTag("resource_impact_tooltip_dialog"),
        title = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(spec.emoji, fontSize = 24.sp)
                    Column {
                        Text(
                            text = spec.title,
                            color = NaijaGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = spec.subtitle,
                            color = TextSecondaryLight,
                            fontSize = 10.sp
                        )
                    }
                }

                Surface(
                    color = Color(0xFF1B3D2F),
                    shape = RoundedCornerShape(4.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold.copy(alpha = 0.6f)))
                ) {
                    Text(
                        text = spec.tag.uppercase(),
                        color = NaijaGoldLight,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Core Game Impact Summary
                Surface(
                    color = Color(0xFF10281C),
                    shape = RoundedCornerShape(8.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGreenLight.copy(alpha = 0.4f))),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "GAMEPLAY ROLE & MANDATE IMPACT:",
                            color = NaijaGoldLight,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = spec.gameImpact,
                            color = TextPrimaryLight,
                            fontSize = 11.5.sp,
                            lineHeight = 16.5.sp
                        )
                    }
                }

                // Detailed Game Mechanics Breakdown
                if (spec.mechanics.isNotEmpty()) {
                    Text(
                        text = "HOW THIS MECHANIC SHAPES THE RUN:",
                        color = NaijaGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black
                    )

                    spec.mechanics.forEach { (label, detail) ->
                        Surface(
                            color = Color(0xFF142B20),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = label,
                                    color = NaijaGoldLight,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = detail,
                                    color = TextPrimaryLight.copy(alpha = 0.9f),
                                    fontSize = 10.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }

                // Strategic Tactics
                if (spec.strategicAdvice.isNotEmpty()) {
                    Text(
                        text = "TACTICAL ADVICE (HOW TO MASTER IT):",
                        color = NaijaGold,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black
                    )

                    spec.strategicAdvice.forEach { tip ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("•", color = NaijaGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = tip,
                                color = TextPrimaryLight,
                                fontSize = 10.5.sp,
                                lineHeight = 14.5.sp
                            )
                        }
                    }
                }

                // Strategic Warning (if applicable)
                spec.warning?.let { warn ->
                    Surface(
                        color = Color(0xFF261212),
                        shape = RoundedCornerShape(6.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(AlertRed.copy(alpha = 0.6f))),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("⚠️", fontSize = 14.sp)
                            Text(
                                text = warn,
                                color = Color(0xFFFF8A80),
                                fontSize = 10.sp,
                                lineHeight = 13.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("close_resource_tooltip_btn")
            ) {
                Text(
                    text = "GOT IT • RETURN TO DASHBOARD",
                    fontWeight = FontWeight.Black,
                    fontSize = 10.5.sp
                )
            }
        }
    )
}
