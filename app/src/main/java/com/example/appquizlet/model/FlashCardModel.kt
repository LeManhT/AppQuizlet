package com.example.appquizlet.model

import com.google.gson.annotations.SerializedName

class FlashCardModel (
    val id: String,
    val term: String,
    val definition: String,
    val timeCreated: Long,
    val isPublic: Boolean,
    @SerializedName("idSetOwner")
    val setOwnerId: String
) {
}