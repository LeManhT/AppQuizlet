package com.example.appquizlet.api.retrofit

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("User/SignUp")
    suspend fun createUser(@Body body : JsonObject) : Response<JsonObject>

}