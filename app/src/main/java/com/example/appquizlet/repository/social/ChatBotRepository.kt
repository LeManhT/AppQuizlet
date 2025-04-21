package com.example.appquizlet.repository.social

import com.example.appquizlet.api.retrofit.QuizletAIService
import com.example.appquizlet.model.requests.DialogflowRequest
import com.example.appquizlet.model.response.ChatBotResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ChatBotRepository @Inject constructor(
    private val quizletAIService: QuizletAIService
) {
    fun sendMessage(userId: String, userMessage: String): Flow<ChatBotResponse> = flow {
        val request = DialogflowRequest(userId, "123", userMessage)
        val response = quizletAIService.sendMessageToBot(request)
        if (response.isSuccessful) {
            response.body()?.let { emit(it) }
        } else {
            throw Exception("Lỗi API: ${response.message()}")
        }
    }
}