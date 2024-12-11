package com.example.appquizlet.model.newfeature

import com.example.appquizlet.model.UserResponse

data class Post(
    val id: String,
    val user: UserResponse,
    val content: String,
    val timestamp: Long,
    val image: String?,
    val likes: Int,
    val comments: Int
)
