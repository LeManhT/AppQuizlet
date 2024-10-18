package com.example.appquizlet.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appquizlet.api.retrofit.ApiPushNotification
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.Notification
import com.example.appquizlet.api.retrofit.NotificationData
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.databinding.FragmentManageNotificationBinding
import com.example.appquizlet.model.admin.NotificationBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FragmentManageNotification : Fragment() {
    private lateinit var binding: FragmentManageNotificationBinding
    private lateinit var apiService: ApiService
    private val FCM_BASE_URL = "https://fcm.googleapis.com/"

    val fcmService: ApiPushNotification by lazy {
        Retrofit.Builder()
            .baseUrl(FCM_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiPushNotification::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageNotificationBinding.inflate(
            layoutInflater, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        binding.pushNotificationButton.setOnClickListener {
            val title = binding.notificationTitle.text.toString()
            val detail = binding.notificationDetail.text.toString()
            val currentTimeInMillis: Long = System.currentTimeMillis()
            val notificationBody = NotificationBody(currentTimeInMillis.toInt(), true, title, detail)
            if (title.isNotEmpty() && detail.isNotEmpty()) {
                pushNotificationForAllUsers(notificationBody)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Title and detail cannot be empty",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    // Function to push notification for a specific user
    private fun pushNotificationForUser(userID: String, notificationBody: NotificationBody) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.pushNoticeForUser(userID, notificationBody)
                if (response.isSuccessful) {
                    sendFCMNotification(userID, notificationBody.title, notificationBody.detail)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Function to push notification for all users
    private fun pushNotificationForAllUsers(notificationBody: NotificationBody) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.pushNoticeForAllUser(notificationBody)
                if (response.isSuccessful) {
                    sendFCMNotificationToAll(notificationBody.title, notificationBody.detail)
                    Log.d("ERRORRRR1", response.message())
                } else {
                    Log.d("ERRORRRR", response.message())
                }
            } catch (e: Exception) {
                Log.d("ERRORRRR2", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    // Function to send FCM to a specific user
    private fun sendFCMNotification(userID: String, title: String, body: String) {
        val notification = Notification(title, body)
        val notificationData = NotificationData(
            to = "/topics/$userID", // Send to a specific user using their FCM token or a specific topic
            notification = notification,
            data = mapOf("extraData" to "value")
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fcmService.sendNotificationToServer(notificationData)
                if (!response.isSuccessful) {
                    Log.d("ERRORRRR", response.message())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Function to send FCM to all users
    private fun sendFCMNotificationToAll(title: String, body: String) {
        val notification = Notification(title, body)
        val notificationData = NotificationData(
            to = "/topics/all", // Send to all users subscribed to the 'all' topic
            notification = notification,
            data = mapOf("extraData" to "value")
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = fcmService.sendNotificationToServer(notificationData)
                if (!response.isSuccessful) {
                    Log.d("ERRORRRR", response.message())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}