package com.example.mandate.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mandate.model.*
import com.example.ui.theme.*
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneScreen(
    activeApp: String,
    onAppChange: (String) -> Unit,
    chirpFeed: List<ChirpPost>,
    chatThreads: List<ChatThread>,
    bankTransactions: List<BankTransaction>,
    newsFlashes: List<NewsFlash>,
    characterNaraBalance: Long,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("phone_screen_modal"),
        color = Color(0xFF070E0B)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Phone Top Bar (Notch & Status)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "09:41 • NaijaTel 5G",
                    fontSize = 12.sp,
                    color = TextSecondaryLight
                )
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("close_phone_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Phone",
                        tint = NaijaGold
                    )
                }
            }

            // App Switcher Bar (Chirp, WhatsApp, Bank, News)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf(
                    Triple("CHIRP", Icons.Default.DynamicFeed, "Chirp"),
                    Triple("CHAT", Icons.Default.Chat, "Chats"),
                    Triple("BANK", Icons.Default.AccountBalance, "KudaBank"),
                    Triple("NEWS", Icons.Default.Newspaper, "NewsWire")
                ).forEach { (appKey, icon, label) ->
                    val isSelected = activeApp == appKey
                    Button(
                        onClick = { onAppChange(appKey) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) NaijaGreenPrimary else EmeraldSurfaceVariant,
                            contentColor = if (isSelected) NaijaGoldLight else TextSecondaryLight
                        ),
                        shape = RoundedCornerShape(12.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                        modifier = Modifier.testTag("phone_tab_$appKey")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Divider(color = Color(0xFF1E3A2B), modifier = Modifier.padding(vertical = 8.dp))

            // App Content
            Box(modifier = Modifier.weight(1f)) {
                when (activeApp) {
                    "CHIRP" -> ChirpAppContent(chirpFeed)
                    "CHAT" -> ChatAppContent(chatThreads)
                    "BANK" -> BankAppContent(characterNaraBalance, bankTransactions)
                    "NEWS" -> NewsAppContent(newsFlashes)
                }
            }
        }
    }
}

@Composable
private fun ChirpAppContent(feed: List<ChirpPost>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Trending in Naijaland",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        items(feed) { post ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(NaijaGreenPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = post.authorName.take(1),
                                    fontWeight = FontWeight.Bold,
                                    color = NaijaGoldLight
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = post.authorName,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimaryLight
                                    )
                                    if (post.isVerified) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = InfoBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "${post.handle} • ${post.timestamp}",
                                    fontSize = 11.sp,
                                    color = TextSecondaryLight
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = post.content,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        color = TextPrimaryLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "🔁 ${post.retweetsCount}", fontSize = 11.sp, color = TextSecondaryLight)
                        Text(text = "❤️ ${post.likesCount}", fontSize = 11.sp, color = TextSecondaryLight)
                        Text(text = post.tag, fontSize = 11.sp, color = NaijaGold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatAppContent(threads: List<ChatThread>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(threads) { thread ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1B4D36)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = thread.contactName.take(1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGoldLight
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = thread.contactName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimaryLight
                            )
                            Text(
                                text = thread.timestamp,
                                fontSize = 11.sp,
                                color = TextSecondaryLight
                            )
                        }
                        Text(
                            text = thread.contactRole,
                            fontSize = 11.sp,
                            color = NaijaGold,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = thread.lastMessage,
                            fontSize = 12.sp,
                            color = TextSecondaryLight,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BankAppContent(balance: Long, transactions: List<BankTransaction>) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    val formattedBalance = numberFormat.format(balance)

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NaijaGreenDark),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NaijaGold))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "KUDA-NAIJA MICROFINANCE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGoldLight
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SuccessGreen
                        ) {
                            Text(
                                text = "ACTIVE TIER 3",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Available Nara Balance",
                        fontSize = 12.sp,
                        color = TextSecondaryLight
                    )
                    Text(
                        text = "₦$formattedBalance",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = NaijaGoldLight
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Acct No: 0248910492 • Tier 3 BVN Verified",
                        fontSize = 11.sp,
                        color = TextSecondaryLight
                    )
                }
            }
        }

        item {
            Text(
                text = "Recent Transactions",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = NaijaGold
            )
        }

        items(transactions) { tx ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = tx.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimaryLight
                        )
                        Text(
                            text = "${tx.date} • ${tx.reference}",
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    Text(
                        text = if (tx.isCredit) "+₦${numberFormat.format(tx.amountNara)}" else "-₦${numberFormat.format(tx.amountNara)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (tx.isCredit) SuccessGreen else AlertRed
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsAppContent(newsList: List<NewsFlash>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(newsList) { item ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.source.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaijaGold
                        )
                        Text(
                            text = item.timeAgo,
                            fontSize = 11.sp,
                            color = TextSecondaryLight
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = item.headline,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimaryLight,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.summary,
                        fontSize = 12.sp,
                        color = TextSecondaryLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
