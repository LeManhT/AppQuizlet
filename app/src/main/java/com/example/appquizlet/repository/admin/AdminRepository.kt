package com.example.appquizlet.repository.admin

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.UserResponse
import com.example.quizletappandroidv1.models.admin.UserAdmin
import javax.inject.Inject

class AdminRepository @Inject constructor(private val apiService: ApiService) {

    fun getPagingUsers(): PagingSource<Int, UserResponse> {
        return object : PagingSource<Int, UserResponse>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserResponse> {
                val position = params.key ?: 0
                val pageSize = params.loadSize

                return try {
                    val response = apiService.getListUserAdmin(position, position + pageSize)
                    if (response.isSuccessful) {
                        val users = response.body()!!
                        LoadResult.Page(
                            data = users,
                            prevKey = if (position == 0) null else position - pageSize,
                            nextKey = if (users.isEmpty()) null else position + pageSize
                        )
                    } else {
                        LoadResult.Error(Exception("Failed to load users"))
                    }
                } catch (e: Exception) {
                    LoadResult.Error(e)
                }
            }

            override fun getRefreshKey(state: PagingState<Int, UserResponse>): Int? {
                return state.anchorPosition
            }
        }
    }

    suspend fun suspendUser(userId: String, suspend: Boolean) {
        apiService.suspendUser(userId, suspend)
    }

    suspend fun deleteUser(userId: String): Result<Boolean> {
        return try {
            apiService.deleteUser(userId)
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginAdmin(username: String, password: String): Result<UserAdmin> {
        return try {
            val response = apiService.loginAdmin(username, password)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to login admin"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}