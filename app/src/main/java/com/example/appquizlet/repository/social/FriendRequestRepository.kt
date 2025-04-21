package com.example.appquizlet.repository.social

import android.content.Context
import android.util.Log
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.model.newfeature.FriendRequest
import com.example.appquizlet.model.response.FriendRequestResponse
import com.example.appquizlet.model.response.FriendStatusResponse
import com.example.appquizlet.model.response.FriendsResponse
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class FriendRequestRepository @Inject constructor(private val apiService: ApiService) {
    fun getFriendsList(userId: String): Flow<Result<List<UserResponse>>> = flow {
        try {
            val response: Response<FriendsResponse> = apiService.getFriendsList(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.success) {
                    emit(Result.success(body.friends))
                    Log.d("FriendRequestRepository", "Response: ${Gson().toJson(body)}")
                } else {
                    emit(Result.failure(Exception("Không thể lấy danh sách bạn bè!")))
                }
            } else {
                emit(Result.failure(Exception("Lỗi lấy danh sách bạn bè!")))
                Log.d("getFriendsList", "Response: ${Gson().toJson(response.errorBody()
                    ?.string() ?: "Nothing" )}")
            }
        } catch (e: HttpException) {
            emit(Result.failure(Exception("Lỗi kết nối server!")))
        } catch (e: Exception) {
            emit(Result.failure(Exception("Có lỗi xảy ra!")))
            Log.e("getFriendsList", "Exception: ${e.message}")
        }
    }

    fun getSuggestedFriends(userId: String, context: Context): Flow<Result<List<UserResponse>>> =
        flow {
            try {
                val accessToken = Helper.getAccessToken(context)
                if (accessToken.isNullOrEmpty()) {
                    Log.e("AuthError", "Access Token is missing")
                }
                val authorizationHeader = "Bearer ${accessToken?.trim()}"
                val response = apiService.getSuggestedFriends(authorizationHeader, userId)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    emit(Result.success(body))
                } else {
                    emit(Result.failure(Exception("Lỗi lấy danh sách gợi ý!")))
                }
            } catch (e: HttpException) {
                emit(Result.failure(Exception("Lỗi kết nối server!")))
            } catch (e: Exception) {
                Log.e("FriendRequestRepository", "Exception: ${e.message}")
                emit(Result.failure(Exception("Có lỗi xảy ra!")))
            }
        }

    fun getReceivedFriendRequests(userId: String): Flow<Result<List<FriendRequest>>> = flow {
        try {
            val response = apiService.getReceivedFriendRequests(userId)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!.requests
                emit(Result.success(body))
            } else {
                Log.d("FriendRequestRepository", "Response: ${Gson().toJson(response.errorBody()
                    ?.string() ?: "Nothing" )}")
                emit(Result.failure(Exception("Lỗi lấy danh sách gợi ý!")))
            }
        } catch (e: HttpException) {
            emit(Result.failure(Exception("Lỗi kết nối server!")))
        } catch (e: Exception) {
            Log.e("FriendRequestRepository", "Exception: ${e.message}")
            emit(Result.failure(Exception("Có lỗi xảy ra!")))
        }
    }
}