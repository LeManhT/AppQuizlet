package com.example.appquizlet.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("seqId") val seqId: Int,
    @SerializedName("loginName") val loginName: String,
    @SerializedName("loginPassword") val loginPassword: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("timeCreated") val timeCreated: Long,
    @SerializedName("documents") val documents: DocumentModel,
    @SerializedName("setting") val setting: SettingModel
) {

}