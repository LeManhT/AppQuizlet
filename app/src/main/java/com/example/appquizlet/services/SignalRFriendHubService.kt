package com.example.appquizlet.services

import android.util.Log
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder

class SignalRFriendHubService(private val userId: String) {
    private var hubConnection: HubConnection? = null

    fun startConnection() {
        hubConnection =
            HubConnectionBuilder.create("ws://192.168.150.1:7042/friendHub?userId=$userId")
                .build()

        hubConnection?.start()?.subscribe({
            Log.d("SignalR", "Kết nối thành công!")
        }, {
            Log.e("SignalR", "Lỗi kết nối: ${it.message}")
        })

        // Lắng nghe sự kiện khi nhận được lời mời kết bạn
        hubConnection?.on("ReceiveFriendRequest", { senderId ->
            Log.d("SignalR", "Bạn có lời mời kết bạn từ $senderId")
        }, String::class.java)

        // Lắng nghe sự kiện khi chấp nhận lời mời kết bạn
        hubConnection?.on("FriendRequestAccepted", { requestId ->
            Log.d("SignalR", "Lời mời kết bạn được chấp nhận: $requestId")
        }, String::class.java)
    }

    fun sendFriendRequest(receiverId: String) {
        hubConnection?.invoke("SendFriendRequest", userId, receiverId)
    }

    fun acceptFriendRequest(requestId: String) {
        hubConnection?.invoke("AcceptFriendRequest", requestId)
    }

    fun cancelFriendRequest(receiverId: String) {
        hubConnection?.invoke("CancelFriendRequest", userId, receiverId)
    }

    fun rejectFriendRequest(requestId: String) {
        hubConnection?.invoke("RejectFriendRequest", requestId)
    }

    fun stopConnection() {
        hubConnection?.stop()
    }
}