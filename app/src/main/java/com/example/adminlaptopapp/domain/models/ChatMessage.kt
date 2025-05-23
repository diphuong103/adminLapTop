package com.example.adminlaptopapp.domain.models

data class ChatMessage(
    val id: String = "",
    val senderId: String = "",
    val text: String = "",
    val urlIMG: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
