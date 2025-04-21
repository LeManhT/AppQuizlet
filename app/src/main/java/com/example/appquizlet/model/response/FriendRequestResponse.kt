package com.example.appquizlet.model.response

import com.example.appquizlet.model.newfeature.FriendRequest

data class FriendRequestResponse(
    val success: Boolean, val requests: List<FriendRequest>
)
