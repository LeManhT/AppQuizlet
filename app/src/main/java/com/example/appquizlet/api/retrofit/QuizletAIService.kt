package com.example.appquizlet.api.retrofit

import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.requests.DialogflowRequest
import com.example.appquizlet.model.response.ChatBotResponse
import com.example.appquizlet.model.response.DialogflowResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface QuizletAIService {
    @POST("chatbot/sendMessage")
    suspend fun sendMessageToBot(@Body request: DialogflowRequest): Response<ChatBotResponse>
}