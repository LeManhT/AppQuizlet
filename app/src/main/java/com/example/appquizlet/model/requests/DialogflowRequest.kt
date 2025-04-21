package com.example.appquizlet.model.requests

import com.google.gson.annotations.SerializedName

// Dialogflow API request/response models
data class DialogflowRequest(
    @SerializedName("userId") val userId: String,
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("message") val message: String
)
//data class QueryInput(val text: TextInput)
//data class TextInput(val text: String, val languageCode: String = "en")
