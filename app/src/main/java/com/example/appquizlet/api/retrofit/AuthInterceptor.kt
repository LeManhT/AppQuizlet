package com.example.appquizlet.api.retrofit

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val credentials: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", credentials)
            .build()
        return chain.proceed(request)
    }
}