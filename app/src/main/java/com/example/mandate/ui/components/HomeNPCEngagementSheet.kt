package com.example.mandate.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

/**
 * Interactive 3D Engagement Modal for conversing and taking direct action
 * with Family Members and Padis inside the 3D home environment.
 */
@Composable
fun HomeNPCEngagementSheet(
    npc: HomeNPCEntity,
    playerBalance: Long,
    onDismiss: () -> Unit,
    onProstrationBlessing: (HomeNPCEntity) -> Unit,
    onRemitCash: (HomeNPCEntity, Long) -> Unit,
    onShareMealOrDrink: (HomeNPCEntity) -> Unit,
    onSeekCounselOrMobilize: (HomeNPCEntity) -> Unit,
    onResolveRivalry: (HomeNPCEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("home_npc_engagement_sheet"),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF20F1D15)),
        border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Drag handle / Title Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(npc.primaryColor, npc.secondaryColor)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = npc.thoughtEmoji,
                            fontSize = 24.sp
                        )
                    }

                    Column {
                        Text(
                            text = npc.name,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = npc.title,
                            fontSize = 11.sp,
                            color = NaijaGoldLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("dismiss_npc_dialog")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Speech Thought Bubble from the NPC in the home
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF162B1F),
                border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(Color(0x33E5B83E)))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "💬", fontSize = 16.sp)
                    Text(
                        text = "\"${npc.speechThought}\"",
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        color = TextPrimaryLight,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stat Bar: Affection or Loyalty
            val statVal = npc.familyMember?.affection ?: (npc.padi?.loyalty ?: 50)
            val statLabel = if (npc.isFamilyMember) "Kinship Affection & Warmth" else "Syndicate Loyalty"
            val statColor = if (statVal > 60) SuccessGreen else if (statVal > 30) NaijaGold else AlertRed

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = statLabel,
                    fontSize = 11.sp,
                    color = TextSecondaryLight
                )
                Text(
                    text = "$statVal%",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { (statVal / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statColor,
                trackColor = Color(0x33288B5B)
            )

            // Demands / Alert (if present)
            if (npc.hasUrgentAlert) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x44D32F2F)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = AlertRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Pending Black Tax remittance request from village!",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AlertRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "INTERACTIVE 3D HOME ACTIONS",
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                color = NaijaGold
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons Grid
            if (npc.isFamilyMember) {
                // Family Member Actions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Traditional Respect: Dobale & Ikunle (Prostration/Kneeling)
                        Button(
                            onClick = { onProstrationBlessing(npc) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_prostrate_blessing"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "👑 Dobale & Ikunle",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Seek Elder Prayer (+Blessing)",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight
                                )
                            }
                        }

                        // 2. Share Meal: Hot Jollof / Pounded Yam
                        Button(
                            onClick = { onShareMealOrDrink(npc) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_share_meal"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B431D))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🍲 Share Hot Meal",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Pounded Yam (-Stress)",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3. Remit Housekeeping Remittance (₦25,000)
                        val remitCost = 25_000L
                        OutlinedButton(
                            onClick = { onRemitCash(npc, remitCost) },
                            enabled = playerBalance >= remitCost,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_remit_cash"),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = SolidColor(if (playerBalance >= remitCost) NaijaGold else Color.Gray)
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "💸 Send ₦25,000",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaijaGoldLight
                                )
                                Text(
                                    text = "Housekeeping & Tax",
                                    fontSize = 9.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }

                        // 4. Settle Rivalry / Seek Political Counsel
                        val isElderOrFather = npc.familyMember?.role == KinshipRole.FATHER ||
                                npc.familyMember?.role == KinshipRole.VILLAGE_ELDER

                        Button(
                            onClick = {
                                if (isElderOrFather) onSeekCounselOrMobilize(npc)
                                else onResolveRivalry(npc)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_counsel_or_rivalry"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isElderOrFather) RoyalPurple else Color(0xFF332038)
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (isElderOrFather) "📜 Political Counsel" else "🤝 Mediate Rivalry",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isElderOrFather) "+Capital & Clan Votes" else "+Compound Peace",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight
                                )
                            }
                        }
                    }
                }
            } else {
                // Visiting Padi Actions
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Street Handshake & Knuckle Snap
                        Button(
                            onClick = { onProstrationBlessing(npc) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_padi_handshake"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🤜 Senior Man Snap!",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Lagos Handshake (+Loyalty)",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight
                                )
                            }
                        }

                        // 2. Share Chilled Malt / Trophy Beer
                        Button(
                            onClick = { onShareMealOrDrink(npc) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_padi_drinks"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A341A))
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "🍺 Chilled Malt Toast",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Parlor Gist (-Stress)",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3. Mobilize Grassroots Street Machinery
                        Button(
                            onClick = { onSeekCounselOrMobilize(npc) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_padi_mobilize"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalPurple)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "📢 Mobilize Street",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "+25 Clout & Capital",
                                    fontSize = 9.sp,
                                    color = NaijaGoldLight
                                )
                            }
                        }

                        // 4. Borrow or Invest Funds (Rich Padi)
                        OutlinedButton(
                            onClick = { onRemitCash(npc, 10_000L) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_padi_financial"),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = SolidColor(NaijaGold)
                            )
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "💼 Share Slush Fund",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaijaGoldLight
                                )
                                Text(
                                    text = "Political Backing",
                                    fontSize = 9.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
