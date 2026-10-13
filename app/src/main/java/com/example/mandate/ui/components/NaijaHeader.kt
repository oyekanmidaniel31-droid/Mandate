package com.example.mandate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.Character
import com.example.mandate.model.MacroEconomy
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@Composable
fun NaijaHeader(
    character: Character,
    macroEconomy: MacroEconomy,
    unreadMessages: Int,
    onOpenPhone: () -> Unit,
    onInspectTension: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val formattedNara = numberFormat.format(character.naraBalance)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("naija_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CardBorderGold))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Name, Office & Phone trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = NaijaGoldLight
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = NaijaGreenPrimary
                        ) {
                            Text(
                                text = "Gen ${character.generationNumber}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${character.politicalOffice} • Age ${character.age} (${character.year})",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondaryLight
                    )
                }

                // Phone quick button
                Box(
                    modifier = Modifier
                        .testTag("open_phone_button")
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NaijaGreenPrimary)
                        .border(1.dp, NaijaGold, CircleShape)
                        .clickable { onOpenPhone() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = "NaijaPhone",
                        tint = NaijaGoldLight,
                        modifier = Modifier.size(22.dp)
                    )
                    if (unreadMessages > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(AlertRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "$unreadMessages",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Money & Office Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0C1B14))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = "Wallet",
                        tint = NaijaGold,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "₦$formattedNara",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaijaGoldLight
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Origin",
                        tint = NaijaGreenLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = character.stateOfOrigin,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondaryLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Vitals row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatProgressBar(
                    label = "Health",
                    value = character.health,
                    color = if (character.health > 40) SuccessGreen else AlertRed,
                    modifier = Modifier.weight(1f)
                )
                StatProgressBar(
                    label = "Clout",
                    value = character.streetClout,
                    color = NaijaGold,
                    modifier = Modifier.weight(1f)
                )
                StatProgressBar(
                    label = "Power",
                    value = character.politicalCapital,
                    color = InfoBlue,
                    modifier = Modifier.weight(1f)
                )
                StatProgressBar(
                    label = "Conscience",
                    value = character.conscience,
                    color = if (character.conscience > 50) SuccessGreen else WarningOrange,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Persistent Anger Index Indicator
            PersistentAngerIndexBar(
                macroEconomy = macroEconomy,
                onInspectTension = onInspectTension
            )
        }
    }
}
