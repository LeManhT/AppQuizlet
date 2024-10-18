package com.example.appquizlet.model.admin

data class NotificationBody(
    val id: Int,
    val wasPushed: Boolean,
    val title: String,
    val detail: String
)