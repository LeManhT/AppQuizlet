package com.example.appquizlet.repository.document

import android.content.Context
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.util.Helper
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

class DocumentRepository @Inject constructor(val apiService: ApiService) {

    suspend fun updateUserInfo(
        context: Context,
        userId: String,
        body: RequestBody
    ): Result<UserResponse> {
        return try {
            val accessToken = Helper.getAccessToken(context)
            val response =
                apiService.updateUserInfo(authorization = "Bearer :$accessToken", userId, body)
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