package com.example.mandate.model

data class ChatMessage(
    val id: String,
    val senderName: String,
    val text: String,
    val timestamp: String,
    val isFromPlayer: Boolean = false,
    val isUrgent: Boolean = false
)

data class ChatThread(
    val id: String,
    val contactName: String,
    val contactRole: String,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int = 0,
    val messages: List<ChatMessage> = emptyList()
)

data class ChirpPost(
    val id: String,
    val authorName: String,
    val handle: String,
    val content: String,
    val likesCount: String,
    val retweetsCount: String,
    val timestamp: String,
    val isVerified: Boolean = false,
    val tag: String = "#Naijaland"
)

data class BankTransaction(
    val id: String,
    val title: String,
    val amountNara: Long,
    val isCredit: Boolean,
    val date: String,
    val reference: String
)

data class NewsFlash(
    val id: String,
    val source: String,
    val headline: String,
    val summary: String,
    val category: String,
    val timeAgo: String,
    val impactTag: String? = null,
    val isBreaking: Boolean = false,
    val fullDispatch: String? = null,
    val publicReaction: String? = null
)
