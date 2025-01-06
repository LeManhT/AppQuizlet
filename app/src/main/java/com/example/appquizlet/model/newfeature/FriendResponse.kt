package com.example.appquizlet.model.newfeature

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class FriendResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("dateOfBirth")
    val dateOfBirth: String,
    @SerializedName("timeCreated")
    val timeCreated: Long,
    @SerializedName("avatar")
    val avatar: String
) : Parcelable