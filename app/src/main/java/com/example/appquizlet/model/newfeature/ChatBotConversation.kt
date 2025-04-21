package com.example.appquizlet.model.newfeature

data class ChatbotConversation(
    val userId: String,
    val sessionId: String,
    val messages: List<ChatBotMessage>,
    val createdAt: String,
    val updatedAt: String
)

data class ChatBotMessage(
    val message: String,
    val response: String,
    val timestamp: String
)

