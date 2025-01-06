package com.example.appquizlet.api.retrofit

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AuthInterceptor(
    private val context: Context,
    private val refreshTokenCallback: () -> String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val token = sharedPreferences.getString("accessToken", null)
        val expiry = sharedPreferences.getLong("expiry", 0L)

        val currentTime = System.currentTimeMillis()

        // Kiểm tra xem token có hợp lệ không
        return if (token != null && expiry > currentTime) {
            // Token còn hợp lệ, thêm vào header
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
            chain.proceed(request)
        } else {
            // Token hết hạn, gọi refresh token
            val newToken = refreshTokenCallback()

            return if (newToken != null) {
                // Cập nhật token mới và gửi yêu cầu lại
                sharedPreferences.edit().putString("accessToken", newToken).apply()
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $newToken")
                    .build()
                chain.proceed(newRequest)
            } else {
                // Nếu không có refresh token, yêu cầu đăng nhập lại
                clearToken()
                throw IOException("Token expired, user needs to log in again.")
            }
        }
    }

    private fun clearToken() {
        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        sharedPreferences.edit().clear().apply()
    }
}
