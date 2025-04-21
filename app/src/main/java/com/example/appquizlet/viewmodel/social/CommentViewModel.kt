package com.example.appquizlet.viewmodel.social

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.newfeature.Comment
import com.example.appquizlet.repository.social.CommentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val repository: CommentRepository) : ViewModel() {

    private val _rootComments = MutableStateFlow<List<Comment>>(emptyList())
    val rootComments: StateFlow<List<Comment>> get() = _rootComments

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // Cache lưu trữ replies đã tải cho mỗi comment
    private val repliesCache = mutableMapOf<String, List<Comment>>()

    fun loadRootComments(postId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                // Tải root comments
                val rootComments = repository.getRootComments(postId)

                // Cập nhật comments với bất kỳ replies nào đã được cache
                val commentsWithReplies = rootComments.map { comment ->
                    val cachedReplies = repliesCache[comment.id]
                    if (comment.isExpanded && cachedReplies != null) {
                        comment.copy(replies = cachedReplies)
                    } else {
                        comment
                    }
                }

                _rootComments.value = commentsWithReplies

                Log.d("CommentViewModel", "Loaded ${rootComments.size} root comments")
                rootComments.forEach { comment ->
                    Log.d("CommentViewModel", "Comment ${comment.id}: ${comment.content}, has ${comment.replies.size} replies")
                }
            } catch (e: Exception) {
                _error.value = "Failed to load comments: ${e.message}"
                Log.e("CommentViewModel", "Error loading comments", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadReplies(commentId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                // Tải replies
                val replies = repository.getReplies(commentId)
                Log.d("CommentViewModel", "Loaded ${replies.size} replies for comment $commentId")

                // Lưu replies vào cache
                repliesCache[commentId] = replies

                // Cập nhật comments để bao gồm replies
                updateCommentWithReplies(commentId, replies)
            } catch (e: Exception) {
                _error.value = "Failed to load replies: ${e.message}"
                Log.e("CommentViewModel", "Error loading replies", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun updateCommentWithReplies(commentId: String, replies: List<Comment>) {
        val updatedComments = _rootComments.value.map { comment ->
            if (comment.id == commentId) {
                comment.copy(replies = replies, isExpanded = true)
            } else {
                // Kiểm tra nếu là comment lồng nhau
                val updatedReplies = updateRepliesRecursively(comment.replies, commentId, replies)
                if (updatedReplies != comment.replies) {
                    comment.copy(replies = updatedReplies)
                } else {
                    comment
                }
            }
        }
        _rootComments.value = updatedComments
    }

    private fun updateRepliesRecursively(
        currentReplies: List<Comment>,
        targetCommentId: String,
        newReplies: List<Comment>
    ): List<Comment> {
        return currentReplies.map { reply ->
            if (reply.id == targetCommentId) {
                reply.copy(replies = newReplies, isExpanded = true)
            } else if (reply.replies.isNotEmpty()) {
                val updatedNestedReplies = updateRepliesRecursively(reply.replies, targetCommentId, newReplies)
                if (updatedNestedReplies != reply.replies) {
                    reply.copy(replies = updatedNestedReplies)
                } else {
                    reply
                }
            } else {
                reply
            }
        }
    }

    fun addComment(postId: String, authorId: String, content: String, parentCommentId: String? = null) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null

                Log.d("CommentViewModel", "Adding comment: $content, parent: $parentCommentId")

                repository.addComment(postId, authorId, content, parentCommentId)

                // Refresh comments after adding
                if (parentCommentId == null) {
                    loadRootComments(postId)
                } else {
                    loadReplies(parentCommentId)
                }
            } catch (e: Exception) {
                _error.value = "Failed to add comment: ${e.message}"
                Log.e("CommentViewModel", "Error adding comment", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                if (repository.deleteComment(commentId)) {
                    loadRootComments(postId)
                } else {
                    _error.value = "Failed to delete comment"
                }
            } catch (e: Exception) {
                _error.value = "Failed to delete comment: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleCommentExpansion(commentId: String) {
        val updatedComments = _rootComments.value.map { comment ->
            updateCommentExpansionRecursively(comment, commentId)
        }
        _rootComments.value = updatedComments
    }

    private fun updateCommentExpansionRecursively(comment: Comment, targetCommentId: String): Comment {
        if (comment.id == targetCommentId) {
            val newExpandedState = !comment.isExpanded

            // Nếu đang mở rộng và chưa có replies, tải replies
            if (newExpandedState && comment.replies.isEmpty() && !repliesCache.containsKey(comment.id)) {
                loadReplies(comment.id)
            }

            return comment.copy(isExpanded = newExpandedState)
        } else if (comment.replies.isNotEmpty()) {
            val updatedReplies = comment.replies.map { reply ->
                updateCommentExpansionRecursively(reply, targetCommentId)
            }
            return comment.copy(replies = updatedReplies)
        }
        return comment
    }
}