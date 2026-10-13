package com.example.mandate.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.mandate.model.FuneralSummary
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FuneralScreen(
    funeral: FuneralSummary,
    onStartDynasty: () -> Unit,
    onRestart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C120E))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(scrollState)
            .padding(16.dp)
            .testTag("funeral_screen")
    ) {
        // Newspaper Header: "THE NAIJA CHRONICLE"
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, NaijaGold, RoundedCornerShape(12.dp)),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F4EA)) // Vintage parchment color
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1791640702854),
                    contentDescription = "National Seal",
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "THE NAIJA CHRONICLE",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    letterSpacing = 2.sp,
                    color = Color(0xFF1B1B1B)
                )

                Text(
                    text = "NATIONAL OBITUARY SPECIAL EDITION • NAIJALAND'S RECORD OF MEMORY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF555555),
                    letterSpacing = 1.sp
                )

                Divider(color = Color(0xFF1B1B1B), thickness = 2.dp, modifier = Modifier.padding(vertical = 8.dp))

                // Big Headline
                Text(
                    text = funeral.newspaperHeadline,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF111111),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = funeral.newspaperSubhead,
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center
                )

                Divider(color = Color(0xFF888888), thickness = 1.dp, modifier = Modifier.padding(vertical = 10.dp))

                // Legacy Title Badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F3E26)
                ) {
                    Text(
                        text = "HISTORICAL VERDICT: ${funeral.legacyTitle}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGoldLight,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Editorial excerpt
                Text(
                    text = funeral.newspaperEditorialSnippet,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFF222222),
                    textAlign = TextAlign.Justify
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Cause of Death: ${funeral.causeOfDeath}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AlertRed
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Legacy Score Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "FINAL LEGACY SCORE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )
                Text(
                    text = "${funeral.legacyScore} PTS",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = NaijaGoldLight
                )
                Text(
                    text = "Remaining Family Estate: ₦${numberFormat.format(funeral.survivingEstateNara)}",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Who Showed Up vs Who Boycotted
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "STATE FUNERAL ATTENDEES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaijaGold
                )
                funeral.attendeesList.forEach { attendee ->
                    Text(
                        text = "• $attendee",
                        fontSize = 12.sp,
                        color = TextPrimaryLight,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "NOTABLE BOYCOTTS & ABSENCES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AlertRed
                )
                funeral.boycottersList.forEach { boycotter ->
                    Text(
                        text = "• $boycotter",
                        fontSize = 12.sp,
                        color = TextSecondaryLight,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Will Dispute Drama
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF261818))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "THE READING OF THE WILL & SIBLING BRAWL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = WarningOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = funeral.willContestDispute,
                    fontSize = 12.sp,
                    color = TextPrimaryLight,
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dynasty Mode Transition Button
        Button(
            onClick = onStartDynasty,
            modifier = Modifier.fillMaxWidth().testTag("start_dynasty_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NaijaGreenPrimary, contentColor = NaijaGoldLight)
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "CONTINUE IN DYNASTY MODE (${funeral.survivingHeirName})",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onRestart,
            modifier = Modifier.fillMaxWidth().testTag("restart_game_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondaryLight)
        ) {
            Icon(imageVector = Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Start Fresh Life in Naijaland", fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
