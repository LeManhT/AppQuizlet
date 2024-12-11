package com.example.appquizlet.model.newfeature

data class Message(
    private val messageId: String,
    val senderId: String,
    val content: String,
    val timeStamp: Long,
    val isSentByUser: Boolean,
    val attachments: List<Attachment>
)


data class Attachment(
    val type: String = "",
    val url: String = "",
)