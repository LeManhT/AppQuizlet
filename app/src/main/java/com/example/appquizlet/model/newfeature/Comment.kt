package com.example.appquizlet.model.newfeature

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    val id: String = "",
    val postId: String = "",
    val userId: String = "",
    val username: String = "",
    val userAvatar: String? = null, // nullable vì có thể bị null
    val content: String = "",
    val createdAt: Long = 0L,
    val timestamp: Long = 0L,
    val parentCommentId: String? = null,
    var replies: List<Comment> = emptyList(),
    var isExpanded: Boolean = false
) : Parcelable
