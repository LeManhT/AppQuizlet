package com.example.appquizlet.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.appquizlet.model.newfeature.Message
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import java.util.concurrent.atomic.AtomicBoolean

class SignalRService {
    private lateinit var hubConnection: HubConnection
    private var messageListener: ((Message) -> Unit)? = null
    private var userJoinedGroupListener: ((String, String) -> Unit)? = null
    private var userLeftGroupListener: ((String, String) -> Unit)? = null
    private var groupMessageListener: ((String, String, Message) -> Unit)? = null

    @SuppressLint("CheckResult")
    fun startConnection() {
//        val hubUrl = "ws://192.168.34.106:7042/chatHub"
        val hubUrl = "ws://192.168.150.1:7042/chatHub"

        hubConnection = HubConnectionBuilder.create(hubUrl).build()

        // Đăng ký sự kiện "ReceiveMessage" chỉ một lần
            hubConnection.on("ReceiveMessage", { userId, message ->
                Log.d("SignalR", "Received message: $message from User: $userId")
                messageListener?.invoke(message)
            }, String::class.java, Message::class.java)

        // Register group message events
        hubConnection.on("ReceiveGroupMessage", { userId, groupId, message ->
            Log.d("SignalR", "Group message from $userId to $groupId: ${message.content}")
            groupMessageListener?.invoke(userId, groupId, message)
        }, String::class.java, String::class.java, Message::class.java)

        // Register group join/leave events
        hubConnection.on("UserJoinedGroup", { userId, groupId ->
            Log.d("SignalR", "User $userId joined group $groupId")
            userJoinedGroupListener?.invoke(userId, groupId)
        }, String::class.java, String::class.java)

        hubConnection.on("UserLeftGroup", { userId, groupId ->
            Log.d("SignalR", "User $userId left group $groupId")
            userLeftGroupListener?.invoke(userId, groupId)
        }, String::class.java, String::class.java)

        hubConnection.start()
            .doOnComplete { Log.d("SignaLR", "SignalR connected successfully") }
            .doOnError { error -> Log.d("SignaLR", "Error in connection: ${error.message}") }
            .subscribe(
                { Log.d("SignalR", "Connection started") },
                { error -> Log.e("SignalR", "Connection failed: ${error.message}") }
            )
    }

    fun sendMessage(message: Message, userId: String) {
        Log.d("SignaLR", "Message sent: ${hubConnection.connectionState.name}")
        if (hubConnection.connectionState.name == "CONNECTED") {
            hubConnection.send("SendMessage", userId, message)
            Log.d("SignaLR", "Message sent: ${message.content}")
        } else {
            Log.d("SignaLR", "SignalR connection is not active")
        }
    }

    fun stopConnection() {
        hubConnection.stop()
        Log.d("SignaLR", "SignalR connection stopped")
    }

    fun setOnMessageReceivedListener(listener: (Message) -> Unit) {
        messageListener = listener
    }

    fun sendSignal(type: String, sender: String, receiver: String, data: String) {
        Log.d("SignalR", "Sending signal: $type from $sender to $receiver")
        hubConnection.send("SendSignal", type, sender, receiver, data)
    }

    fun receiveSignal(callback: (String, String, String) -> Unit) {
        hubConnection.on("ReceiveSignal", { type, sender, data ->
            Log.d("SignalR", "Received signal: $type from $sender")
            callback(type, sender, data)
        }, String::class.java, String::class.java, String::class.java)
    }

    fun sendGroupMessage(userId: String, groupId: String, message: Message) {
        if (hubConnection.connectionState.name == "CONNECTED") {
            hubConnection.send("SendGroupMessage", userId, groupId, message)
            Log.d("SignalR", "Group message sent: ${message.content}")
        } else {
            Log.d("SignalR", "Connection not active")
        }
    }
//    fun leaveGroup(groupId: String) {
//        if (hubConnection.connectionState.name == "CONNECTED") {
//            hubConnection.send("LeaveGroup", groupId)
//            Log.d("SignalR", "Left group: $groupId")
//        }
//    }
//
//    fun joinGroup(groupId: String) {
//        if (hubConnection.connectionState.name == "CONNECTED") {
//            hubConnection.send("JoinGroup", groupId)
//            Log.d("SignalR", "Joined group: $groupId")
//        }
//    }

    fun onMessageReceived(listener: (String, Message) -> Unit) {
        hubConnection.on("ReceiveMessage", { userId, message ->
            Log.d("SignalR", "Message from $userId: ${message.content}")
            listener(userId, message)
        }, String::class.java, Message::class.java)
    }

    fun joinGroup(userId: String, groupId: String) {
        if (hubConnection.connectionState.name == "CONNECTED") {
            hubConnection.send("JoinGroup", userId, groupId)
            Log.d("SignalR", "User $userId joined group: $groupId")
        } else {
            Log.d("SignalR", "Connection not active")
        }
    }

    fun leaveGroup(userId: String, groupId: String) {
        if (hubConnection.connectionState.name == "CONNECTED") {
            hubConnection.send("LeaveGroup", userId, groupId)
            Log.d("SignalR", "User $userId left group: $groupId")
        } else {
            Log.d("SignalR", "Connection not active")
        }
    }

    fun getGroupMessages(groupId: String, limit: Int = 50, beforeTimestamp: Long? = null) {
        if (hubConnection.connectionState.name == "CONNECTED") {
            hubConnection.send("GetGroupMessages", groupId, limit, beforeTimestamp)
            Log.d("SignalR", "Requesting messages for group: $groupId")
        } else {
            Log.d("SignalR", "Connection not active")
        }
    }

    fun setOnGroupMessageReceivedListener(listener: (String, String, Message) -> Unit) {
        groupMessageListener = listener
    }

    fun setOnUserJoinedGroupListener(listener: (String, String) -> Unit) {
        userJoinedGroupListener = listener
    }

    fun setOnUserLeftGroupListener(listener: (String, String) -> Unit) {
        userLeftGroupListener = listener
    }
}