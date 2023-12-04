package com.example.appquizlet.model

import com.google.gson.annotations.SerializedName

class StudySetModel (
    val id: String,
    val name: String,
    val timeCreated: Long,
    @SerializedName("idFolderOwner")
    val folderOwnerId: String
) {
}