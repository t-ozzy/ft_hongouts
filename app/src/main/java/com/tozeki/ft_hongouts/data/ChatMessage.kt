package com.tozeki.ft_hongouts.data

data class ChatMessage(
    val id: Long = 0,
    val contactId: Long,
    val isSent: Boolean, // true: 送信, false: 受信
    val message: String,
    val timestamp: Long
)
