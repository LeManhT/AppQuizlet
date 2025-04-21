package com.example.appquizlet.model.requests

import com.example.appquizlet.model.newfeature.Comment

data class CreatePostRequest(
    val authorId: String,
    val content: String,
    val author: String,
    val createdAt: Long,
    val comments: List<Comment> = emptyList(),
    val likes: Int = 0,
    val fileUrls: List<String> = emptyList(),
    val imageUrls: List<String> = emptyList()
)

data class UploadResponse(
    val url: String
)


