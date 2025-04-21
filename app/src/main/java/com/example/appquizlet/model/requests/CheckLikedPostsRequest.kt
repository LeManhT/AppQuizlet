package com.example.appquizlet.model.requests

data class CheckLikedPostsRequest(
    val userId: String,
    val postIds: List<String>
)