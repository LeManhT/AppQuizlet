package com.example.appquizlet.api.retrofit

import com.example.appquizlet.util.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private const val baseUrl = Constants.baseUrl
    fun getInstance(): Retrofit {
//        val client = OkHttpClient.Builder()
//            .addInterceptor(AuthInterceptor(context) {
//                val sharedPreferences = EncryptedSharedPreferences.create(
//                    context,
//                    "secure_prefs",
//                    MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
//                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
//                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
//                )
//                return@AuthInterceptor sharedPreferences.getString(
//                    "refreshToken",
//                    null
//                ) // Trả về refresh token nếu có
//            })
//            .build()
        return Retrofit.Builder().baseUrl(baseUrl)
//            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

