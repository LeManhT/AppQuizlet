package com.example.appquizlet.model.requests

data class CreatePostRequest(
    val userId: String,
    val content: String,
    val image: String?
)
