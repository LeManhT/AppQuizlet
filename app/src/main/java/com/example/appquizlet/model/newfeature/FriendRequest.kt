package com.example.appquizlet.model.newfeature

data class FriendRequest(
    val id : String,
    val senderId: String,
    val receiverId: String,
    val receiverName: String,
    val senderName: String,
    val createdAt: Long,
    val mutualFriends: Int,
    val status: String
)