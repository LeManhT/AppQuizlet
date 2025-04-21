package com.example.appquizlet.model.response

import com.example.appquizlet.model.UserResponse

data class FriendsResponse(val success: Boolean, val friends: List<UserResponse>)
