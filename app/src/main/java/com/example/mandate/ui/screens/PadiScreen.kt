package com.example.mandate.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Diversity1
import androidx.compose.material.icons.filled.SportsBar
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.*
import com.example.mandate.ui.components.DualMeterProgressBar
import com.example.mandate.ui.components.RelationshipTrackerOverlay
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun PadiScreen(
    padis: List<Padi>,
    familyMembers: List<FamilyMember> = emptyList(),
    playerNara: Long = 0L,
    onPadiAction: (Padi, String) -> Unit,
    onSendRelationshipGift: (String, Boolean, Long) -> Unit = { _, _, _ -> },
    onSpendRelationshipTime: (String, Boolean) -> Unit = { _, _ -> },
    onPledgeLoyaltyPact: (String, Boolean) -> Unit = { _, _ -> },
    onSettleRelationshipWahala: (String, Boolean) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }
    var showRelationshipOverlay by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp)
                .testTag("padi_screen_list"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Dramatic Padi Syndicate & Nightlife Artwork Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_street_action_1791644914933),
                        contentDescription = "Padi Syndicate & Street Alliance",
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
                            .padding(14.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NaijaGreenPrimary
                        ) {
                            Text(
                                text = "🤝 ALLIANCES, ENVOYS & SECRETS",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = NaijaGoldLight,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "THE PADI SYNDICATE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            // Header and Relations Radar button
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "THE PADI SYNDICATE",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = NaijaGoldLight
                        )
                        Text(
                            text = "Friends, kingmakers, and rivals. Track happiness, loyalty, and street standing.",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    FilledTonalButton(
                        onClick = { showRelationshipOverlay = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = Color(0xFF143321),
                            contentColor = NaijaGoldLight
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("open_padi_relations_dossier")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Diversity1,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Relations Radar", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(padis) { padi ->
                val record = remember(padi) { padi.toTrackRecord() }
                val tierColor = Color(record.tier.badgeColorHex)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("padi_card_${padi.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header row: Nickname & Tier
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(NaijaGreenPrimary),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = record.avatarEmoji,
                                        fontSize = 20.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = padi.nickname,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight
                                    )
                                    Text(
                                        text = padi.name,
                                        fontSize = 12.sp,
                                        color = TextSecondaryLight
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NaijaGreenDark
                                ) {
                                    Text(
                                        text = padi.archetype.title.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaijaGold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = tierColor.copy(alpha = 0.2f),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(tierColor))
                                ) {
                                    Text(
                                        text = record.tier.tag,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        color = tierColor,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = padi.occupation,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = NaijaGoldLight
                        )

                        Text(
                            text = padi.bio,
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )

                        // Memory Quote
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF0C1F16)
                        ) {
                            Text(
                                text = "Memory: \"${padi.memoryOfPlayer}\"",
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = TextSecondaryLight,
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // DUAL METERS: Individual Happiness and Loyalty Meters
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Happiness Meter
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Happiness ${record.moodEmoji}", fontSize = 10.sp, color = TextSecondaryLight)
                                    Text(
                                        text = "${padi.happiness}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (padi.happiness >= 70) SuccessGreen else NaijaGoldLight
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                DualMeterProgressBar(
                                    value = padi.happiness,
                                    activeBrush = Brush.horizontalGradient(
                                        colors = if (padi.happiness >= 70) listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                                        else listOf(Color(0xFFE65100), NaijaGold)
                                    )
                                )
                                Text(
                                    text = record.moodLabel,
                                    fontSize = 9.sp,
                                    color = TextSecondaryLight,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            // Loyalty Meter
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Loyalty ${record.loyaltyEmoji}", fontSize = 10.sp, color = TextSecondaryLight)
                                    Text(
                                        text = "${padi.loyalty}%",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (padi.loyalty >= 70) Color(0xFF42A5F5) else AlertRed
                                    )
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                DualMeterProgressBar(
                                    value = padi.loyalty,
                                    activeBrush = Brush.horizontalGradient(
                                        colors = if (padi.loyalty >= 70) listOf(Color(0xFF1565C0), Color(0xFF42A5F5))
                                        else listOf(Color(0xFF880E4F), Color(0xFFEC407A))
                                    )
                                )
                                Text(
                                    text = record.loyaltyLabel,
                                    fontSize = 9.sp,
                                    color = TextSecondaryLight,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Net Worth
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(text = "Net Worth: ", fontSize = 11.sp, color = TextSecondaryLight)
                            Text(
                                text = "₦${numberFormat.format(padi.netWorth)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaijaGoldLight
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { onPadiAction(padi, "hang_out") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaijaGoldLight)
                            ) {
                                Icon(imageVector = Icons.Default.SportsBar, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "VIP Chill (₦35k)", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onPadiAction(padi, "campaign_backing") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight)
                            ) {
                                Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Mobilize", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(30.dp)) }
        }

        // Full overlay sheet if triggered
        AnimatedVisibility(
            visible = showRelationshipOverlay,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            RelationshipTrackerOverlay(
                familyMembers = familyMembers,
                padis = padis,
                playerNara = playerNara,
                onClose = { showRelationshipOverlay = false },
                onSendGift = onSendRelationshipGift,
                onSpendTime = onSpendRelationshipTime,
                onPledgeLoyalty = onPledgeLoyaltyPact,
                onSettleWahala = onSettleRelationshipWahala
            )
        }
    }
}
