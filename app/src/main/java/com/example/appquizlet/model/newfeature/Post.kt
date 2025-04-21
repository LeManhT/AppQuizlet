package com.example.appquizlet.model.newfeature

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post(
    val id: String = "",
    @SerializedName("authorId")
    val authorId: String = "",
    val content: String = "",
    val timestamp: Long = 0L,
    var isLiked: Boolean = false,
    var likes: Int = 0,
    val comments: List<Comment> = emptyList(),
    val imageUrls: List<String> = emptyList(),
    val fileUrls: List<String> = emptyList(),
    val likedByUsers: MutableList<String> = mutableListOf()
) : Parcelable
