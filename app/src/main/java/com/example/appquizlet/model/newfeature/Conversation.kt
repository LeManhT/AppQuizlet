package com.example.appquizlet.model.newfeature
import com.google.gson.annotations.SerializedName

data class Conversation(
    @SerializedName("conversation_id")
    val conversationId: String = "",

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("type")
    val type: String = "personal",

    @SerializedName("participants")
    val members: List<String> = emptyList(),

    @SerializedName("last_message")
    val lastMessage: String? = null,

    @SerializedName("last_message_time")
    val lastMessageTime: Long = System.currentTimeMillis() / 1000,

    @SerializedName("is_deleted")
    val isDeleted: Boolean = false,

    @SerializedName("created_at")
    val createdAt: Long = System.currentTimeMillis() / 1000,

    @SerializedName("updated_at")
    val updatedAt: Long = System.currentTimeMillis() / 1000
)
