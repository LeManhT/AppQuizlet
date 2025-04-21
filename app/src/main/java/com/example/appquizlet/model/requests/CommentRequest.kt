package com.example.appquizlet.model.requests

data class CommentRequest(
    val postId: String,
    val authorId: String,
    val content: String,
    val parentCommentId: String? = null
)