package com.example.mandate.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.ElectionTally
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun TheRaceScreen(
    electionTally: ElectionTally,
    onProceed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("the_race_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Dramatic Broadcast Graphic Hero Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_villa_gate_1791640975131),
                    contentDescription = "Presidential Villa Seat of Power",
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
                        color = if (electionTally.wonElection) SuccessGreen else AlertRed
                    ) {
                        Text(
                            text = if (electionTally.wonElection) "🏆 VICTORY MANDATE DECLARED" else "⚖️ ELECTION TRIBUNAL PENDING",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "THE BATTLE FOR THE VILLA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }
        }

        item {
            Column(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)) {
                Text(
                    text = "THE PRESIDENTIAL RACE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = NaijaGoldLight
                )
                Text(
                    text = "Live National Electoral Commission Result Collation Center",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
        }

        // Electoral Scoreboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorderGold))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "ELECTORAL COLLEGE SCOREBOARD (60 TO WIN)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = electionTally.playerCandidateName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SuccessGreen
                            )
                            Text(
                                text = "${electionTally.playerTotalElectoral} EV",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = SuccessGreen
                            )
                            Text(
                                text = "${numberFormat.format(electionTally.playerPopularVotes)} votes",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }

                        Text(
                            text = "VS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = NaijaGold
                        )

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = electionTally.opponentCandidateName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertRed
                            )
                            Text(
                                text = "${electionTally.opponentTotalElectoral} EV",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = AlertRed
                            )
                            Text(
                                text = "${numberFormat.format(electionTally.opponentPopularVotes)} votes",
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Graphic Popular Vote Race Bar Canvas
                    val totalVotes = (electionTally.playerPopularVotes + electionTally.opponentPopularVotes).coerceAtLeast(1)
                    val playerVoteRatio = (electionTally.playerPopularVotes.toFloat() / totalVotes).coerceIn(0.05f, 0.95f)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0C1F15))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val splitX = w * playerVoteRatio

                            // Player vote portion
                            drawRect(
                                color = Color(0xFF288B5B),
                                topLeft = Offset(0f, 0f),
                                size = androidx.compose.ui.geometry.Size(splitX, h)
                            )
                            // Opponent vote portion
                            drawRect(
                                color = Color(0xFFD32F2F),
                                topLeft = Offset(splitX, 0f),
                                size = androidx.compose.ui.geometry.Size(w - splitX, h)
                            )
                            // White divider
                            drawLine(
                                color = Color.White,
                                start = Offset(splitX, 0f),
                                end = Offset(splitX, h),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Verdict Banner
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        color = if (electionTally.wonElection) NaijaGreenDark else Color(0xFF331111)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (electionTally.wonElection) Icons.Default.MilitaryTech else Icons.Default.HowToVote,
                                contentDescription = null,
                                tint = if (electionTally.wonElection) NaijaGold else AlertRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (electionTally.wonElection)
                                    "CONGRATULATIONS: You have won the presidential election! You hold the sacred Mandate of Naijaland!"
                                else
                                    "ELECTION TRIBUNAL IN PROGRESS: Your lawyers are filing petitions challenging results in 3 northern states.",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (electionTally.wonElection) NaijaGoldLight else Color.White
                            )
                        }
                    }
                }
            }
        }

        // State-by-State Collation
        item {
            Text(
                text = "ZONAL RESULT RETURNS & ELECTORAL MAP",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )
        }

        items(electionTally.stateResults) { stateRes ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stateRes.stateName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "${stateRes.electoralVotes} Electoral Votes",
                            fontSize = 11.sp,
                            color = NaijaGold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (stateRes.isDeclaredForPlayer) SuccessGreen else AlertRed
                    ) {
                        Text(
                            text = if (stateRes.isDeclaredForPlayer) "WON" else "LOST",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = onProceed,
                modifier = Modifier.fillMaxWidth().testTag("proceed_from_race"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight)
            ) {
                Text(
                    text = if (electionTally.wonElection) "ENTER THE PRESIDENTIAL VILLA" else "CONTINUE THE STRUGGLE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

