package com.example.appquizlet.model.requests

import com.example.appquizlet.model.FlashCardModel

data class CreateSetRequest(
    val name: String,
    val description: String,
    val idFolderOwner: String? = "",
    val allNewCards: List<FlashCardModel>,
    var isPublish: Boolean? = false
)