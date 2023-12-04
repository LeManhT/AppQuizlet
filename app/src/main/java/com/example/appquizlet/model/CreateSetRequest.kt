package com.example.appquizlet.model

data class CreateSetRequest(
    val name: String,
    val description: String,
    val allNewCards: List<CreateSetModel>
)