package com.example.appquizlet.repository.social

import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.newfeature.Comment
import com.example.appquizlet.model.requests.CommentRequest
import javax.inject.Inject

class CommentRepository @Inject constructor(
    private val commentApiService: ApiService
) {
    suspend fun getRootComments(postId: String): List<Comment> {
        val response = commentApiService.getRootComments(postId)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun getReplies(commentId: String): List<Comment> {
        val response = commentApiService.getReplies(commentId)
        return if (response.isSuccessful) {
            response.body() ?: emptyList()
        } else {
            emptyList()
        }
    }

    suspend fun addComment(postId: String, authorId: String, content: String, parentCommentId: String? = null): Comment? {
        val request = CommentRequest(postId, authorId, content, parentCommentId)
        val response = commentApiService.addComment(request)
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun deleteComment(commentId: String): Boolean {
        val response = commentApiService.deleteComment(commentId)
        return response.isSuccessful && response.body() == true
    }
}