package com.example.appquizlet.model.newfeature

data class Message(
    val groupId: String? = null,
    val messageId: String? = null,
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val conversationId: String = "",
    val isRead: Boolean = false,
    val isDeleted: Boolean = false,
    val attachments: List<Attachment> = emptyList(),
    val isPinned: Boolean = false,
    val isSentByUser: Boolean? = false
)

data class Attachment(
    val type: String = "",
    val url: String = "",
    val fileName: String = "",
    val fileSize: Long = 0L
)

fun generateObjectId(): String {
    val timestamp = System.currentTimeMillis() / 1000
    val random = (1..12).map { (0..255).random().toByte() }
    val hexTimestamp = timestamp.toString(16).padStart(8, '0')
    val hexRandom = random.joinToString("") { "%02x".format(it) }
    return hexTimestamp + hexRandom
}