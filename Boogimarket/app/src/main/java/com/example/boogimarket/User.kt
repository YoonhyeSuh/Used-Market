package com.example.boogimarket

data class User (
    val name: String,
    val email: String,
    val userId: String,
    val password: String
)

data class ChatData(
    val id: String?,
    val writer: User,
    val contact: User,
    val messages: List<MessageData>
)

data class MessageData(
    val content: String,
    val createdAt: Long,
    val from: Int
)