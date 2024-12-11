package com.example.appquizlet.repository.social

import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.newfeature.Post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class SocialRepository @Inject constructor(private val apiService: ApiService) {

    fun getMessages(userId: String): Flow<List<Message>> = flow {
        try {
            val response = apiService.getMessages(userId)
            if (response.isSuccessful) {
                emit(response.body() ?: emptyList())
            } else {
                throw Exception("Error fetching messages: ${response.message()}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }

    suspend fun sendMessage(message: Message): Boolean {
        return try {
            val response = apiService.sendMessage(message)
            if (response.isSuccessful) {
                true
            } else {
                throw Exception("Error sending message: ${response.message()}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }

    suspend fun deleteMessage(messageId: String) {
        return try {
            val response = apiService.deleteMessage(messageId)
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }


    suspend fun getPosts(): List<Post> {
        return apiService.getPosts()
    }

}