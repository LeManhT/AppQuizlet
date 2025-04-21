package com.example.appquizlet.model.newfeature

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GroupMember(
    @SerializedName("userId")
    val userId: String,
    @SerializedName("userName")
    val userName: String? = "",
    @SerializedName("avatarUrl")
    val avatarUrl: String? = "",
    @SerializedName("role")
    val role: String? = "user", // ADMIN, MEMBER
    @SerializedName("joinedAt")
    val joinedAt: Long
) : Parcelable