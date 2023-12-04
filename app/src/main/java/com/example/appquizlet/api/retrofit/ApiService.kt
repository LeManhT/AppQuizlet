package com.example.appquizlet.api.retrofit

import com.example.appquizlet.model.CreateSetRequest
import com.example.appquizlet.model.UserResponse
import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Query

interface ApiService {

    @POST("User/SignUp")
    suspend fun createUser(@Body body: JsonObject): Response<JsonObject>

    @POST("User/Login")
    suspend fun loginUser(@Body body: JsonObject): Response<UserResponse>

    @POST("Folder/Create")
    suspend fun createNewFolder(
        @Query("userId") userId: String,
        @Body body: JsonObject
    ): Response<UserResponse>


    //    @PUT("Folder/Update")
//    fun updateFolder(
//        @Query("userId") userId: String,
//        @Body body: JsonObject
//    ): Call<UserResponse>
//
    @PUT("Folder/UpdateInfo")
    suspend fun updateFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Body body: JsonObject
    ): Response<UserResponse>


    @DELETE("Folder/Delete")
    suspend fun deleteFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String
    ): Response<UserResponse>

    @POST("StudySet/Create")
    suspend fun createNewStudySet(
        @Query("userId") userId: String,
        @Body body: CreateSetRequest
    ): Response<UserResponse>

    @DELETE("StudySet/Delete")
    suspend fun deleteStudySet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    ): Response<UserResponse>
}