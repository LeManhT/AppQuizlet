package com.example.appquizlet.repository.document

import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.UserResponse
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

class DocumentRepository @Inject constructor(val apiService: ApiService) {

    suspend fun updateUserInfo(userId: String, body: RequestBody): Result<UserResponse> {
        return try {
            val response = apiService.updateUserInfo(userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating user info")
            Result.failure(e)
        }
    }

}