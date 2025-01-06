package com.example.appquizlet.model.newfeature

data class FriendRequest(
    val id: String,
    val name: String,
    val mutualFriends: Int,
    val time: String
)