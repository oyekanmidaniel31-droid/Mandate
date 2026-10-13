package com.example.mandate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.*
import com.example.ui.theme.*

/**
 * Interactive nationwide map and travel terminal connecting all states and geopolitical zones of Nigeria.
 */
@Composable
fun NigeriaNationMapDialog(
    currentState: NigeriaState,
    playerCash: Long,
    onTravelToState: (NigeriaState, Boolean) -> Unit, // state, isAirTravel
    onDismiss: () -> Unit
) {
    var selectedZone by remember { mutableStateOf<GeopoliticalZone?>(null) }
    var selectedState by remember { mutableStateOf(currentState) }

    val statesToDisplay = remember(selectedZone) {
        if (selectedZone == null) NigeriaFederation.allStates
        else NigeriaFederation.allStates.filter { it.zone == selectedZone }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        modifier = Modifier.testTag("nigeria_nation_map_dialog"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("🇳🇬", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "FEDERATION OF NIGERIA",
                            color = NaijaGoldLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "36 States & FCT • National Power & Crises",
                            color = TextSecondaryLight,
                            fontSize = 10.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = NaijaGold)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Zone filter row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        Surface(
                            color = if (selectedZone == null) NaijaGold else EmeraldSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedZone = null }
                        ) {
                            Text(
                                text = "All Zones",
                                color = if (selectedZone == null) DeepCharcoal else TextPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    items(GeopoliticalZone.values()) { zone ->
                        val isSelected = selectedZone == zone
                        Surface(
                            color = if (isSelected) Color(zone.primaryColorHex) else EmeraldSurfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.clickable { selectedZone = zone }
                        ) {
                            Text(
                                text = zone.shortCode,
                                color = if (isSelected) DeepCharcoal else TextPrimaryLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                HorizontalDivider(color = CardBorderGold.copy(alpha = 0.5f))

                // Scrollable states list
                LazyColumn(
                    modifier = Modifier.heightIn(max = 240.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(statesToDisplay, key = { it.id }) { state ->
                        val isCurrent = state.id == currentState.id
                        val isSelected = state.id == selectedState.id

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) Color(0xFF1E3A2F) else EmeraldSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = CardDefaults.outlinedCardBorder().copy(
                                brush = SolidColor(if (isSelected) NaijaGold else Color.Transparent)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedState = state }
                                .testTag("state_item_${state.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(state.flagEmoji, fontSize = 20.sp)
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = state.name,
                                                color = NaijaGoldLight,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (isCurrent) {
                                                Surface(
                                                    color = NaijaGold,
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "CURRENT",
                                                        color = DeepCharcoal,
                                                        fontSize = 8.5.sp,
                                                        fontWeight = FontWeight.Black,
                                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = "${state.hubCity} • ${state.zone.title}",
                                            color = TextSecondaryLight,
                                            fontSize = 9.5.sp
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "${state.delegateCount} Delegates",
                                        color = SuccessGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Danger: ${state.dangerLevel}%",
                                        color = if (state.dangerLevel > 60) AlertRed else WarningOrange,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Selected State Deep Dive & Travel Options
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(NaijaGold)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "REGIONAL CRISIS IN ${selectedState.name.uppercase()}:",
                            color = AlertRed,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedState.primaryCrisis.iconEmoji} ${selectedState.primaryCrisis.title}: ${selectedState.crisisNarrative}",
                            color = TextPrimaryLight,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )

                        Text(
                            text = "Landmark: ${selectedState.primaryLandmark}",
                            color = NaijaGoldLight,
                            fontSize = 10.sp
                        )

                        if (selectedState.id != currentState.id) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onTravelToState(selectedState, false) },
                                    enabled = playerCash >= selectedState.roadDanfoFareNaira,
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("🚌 Interstate Bus", fontSize = 10.sp, color = TextPrimaryLight)
                                        Text("₦${selectedState.roadDanfoFareNaira / 1_000}k", fontSize = 9.sp, color = NaijaGoldLight)
                                    }
                                }

                                Button(
                                    onClick = { onTravelToState(selectedState, true) },
                                    enabled = playerCash >= selectedState.airFlightFareNaira,
                                    colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text("✈️ Interstate Flight", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        Text("₦${selectedState.airFlightFareNaira / 1_000}k", fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
