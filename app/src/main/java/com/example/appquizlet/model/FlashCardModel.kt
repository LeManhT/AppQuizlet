package com.example.appquizlet.model

import com.google.gson.annotations.SerializedName

class FlashCardModel(
    val id: String,
    val term: String,
    val definition: String,
    val timeCreated: Long,
    val isPublic: Boolean? = true,
    @SerializedName("idSetOwner")
    val setOwnerId: String? = "",
    val isSelected: Boolean? = false,
    var isUnMark : Boolean ?= false
) {
}