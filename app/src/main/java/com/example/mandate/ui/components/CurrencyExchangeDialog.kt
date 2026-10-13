package com.example.mandate.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.mandate.model.CurrencyWallet
import com.example.ui.theme.*

/**
 * Currency, Banking, and Bureau De Change (BDC) multi-tier economic interface.
 * Allows moving funds between physical Cash (₦), Bank Accounts (₦), and Domiciliary USD ($).
 */
@Composable
fun CurrencyExchangeDialog(
    wallet: CurrencyWallet,
    characterCash: Long,
    onDepositToBank: (Long) -> Unit,
    onWithdrawToCash: (Long) -> Unit,
    onBuyDollars: (Long) -> Unit, // amount of USD to buy
    onSellDollars: (Long) -> Unit, // amount of USD to sell
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DeepCharcoal,
        modifier = Modifier.testTag("currency_exchange_dialog"),
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
                    Text("💱", fontSize = 24.sp)
                    Column {
                        Text(
                            text = "CURRENCY & BDC BUREAU",
                            color = NaijaGoldLight,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "Naira Cash • Digital Bank • USD Reserve",
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
                // Wallet Balance Trio Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(CardBorderGold)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Cash
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("💵 CASH", color = TextSecondaryLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("₦${characterCash / 1_000}k", color = NaijaGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Street Hustle", color = NaijaGreenLight, fontSize = 8.5.sp)
                        }

                        VerticalDivider(modifier = Modifier.height(30.dp), color = CardBorderGold.copy(alpha = 0.5f))

                        // Bank
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🏦 BANK", color = TextSecondaryLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("₦${wallet.bankNaira / 1_000}k", color = NaijaGoldLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Transfers", color = Color(0xFF00B0FF), fontSize = 8.5.sp)
                        }

                        VerticalDivider(modifier = Modifier.height(30.dp), color = CardBorderGold.copy(alpha = 0.5f))

                        // USD
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🇺🇸 DOM USD", color = TextSecondaryLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("$${wallet.domUsd}", color = SuccessGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Hedge Reserve", color = SuccessGreen, fontSize = 8.5.sp)
                        }
                    }
                }

                // Black Market Rate Pill
                Surface(
                    color = Color(0xFF16251E),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PARALLEL BDC RATE: $1.00 = ₦${wallet.bdcExchangeRate}",
                            color = NaijaGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Net: ₦${wallet.totalNetWorthNaira / 1_000}k",
                            color = TextSecondaryLight,
                            fontSize = 10.sp
                        )
                    }
                }

                // Daily Living Costs Breakdown
                Card(
                    colors = CardDefaults.cardColors(containerColor = DeepCharcoal),
                    shape = RoundedCornerShape(10.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = SolidColor(EmeraldSurfaceVariant)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "DAILY LIFE COST BURDEN (Deducted at Midnight):",
                            color = WarningOrange,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Compound Generator Fuel (Blackout):", color = TextSecondaryLight, fontSize = 10.sp)
                            Text("₦${wallet.dailyCompoundGenCost}", color = TextPrimaryLight, fontSize = 10.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Compound Family & Wives Food Ration:", color = TextSecondaryLight, fontSize = 10.sp)
                            Text("₦${wallet.dailyCompoundFoodCost}", color = TextPrimaryLight, fontSize = 10.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("• Extended Kinship Black Tax Reserve:", color = TextSecondaryLight, fontSize = 10.sp)
                            Text("₦${wallet.dailyBlackTaxAllowance}", color = TextPrimaryLight, fontSize = 10.sp)
                        }
                        HorizontalDivider(color = CardBorderGold.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 2.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("TOTAL DAILY BURN RATE:", color = AlertRed, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            Text("₦${wallet.dailyLivingCostTotal} / Day", color = AlertRed, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Quick Currency Actions
                Text("QUICK FINANCIAL OPERATIONS:", color = NaijaGoldLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Deposit Cash to Bank
                    Button(
                        onClick = { onDepositToBank(20_000L) },
                        enabled = characterCash >= 20_000L,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Deposit ₦20k", fontSize = 10.sp, color = TextPrimaryLight)
                            Text("To Bank", fontSize = 8.5.sp, color = TextSecondaryLight)
                        }
                    }

                    // Withdraw to Cash
                    Button(
                        onClick = { onWithdrawToCash(20_000L) },
                        enabled = wallet.bankNaira >= 20_000L,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Withdraw ₦20k", fontSize = 10.sp, color = TextPrimaryLight)
                            Text("From ATM", fontSize = 8.5.sp, color = TextSecondaryLight)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Buy USD
                    val usdCost = wallet.bdcExchangeRate * 50L
                    Button(
                        onClick = { onBuyDollars(50L) },
                        enabled = characterCash >= usdCost,
                        colors = ButtonDefaults.buttonColors(containerColor = NaijaGold, contentColor = DeepCharcoal),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Buy $50 USD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Costs ₦${usdCost / 1_000}k", fontSize = 8.5.sp)
                        }
                    }

                    // Sell USD
                    Button(
                        onClick = { onSellDollars(50L) },
                        enabled = wallet.domUsd >= 50L,
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen, contentColor = DeepCharcoal),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Sell $50 USD", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("Get ₦${usdCost / 1_000}k Cash", fontSize = 8.5.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {}
    )
}
