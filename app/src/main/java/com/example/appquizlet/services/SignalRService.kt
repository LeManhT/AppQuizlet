package com.example.appquizlet.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.appquizlet.model.newfeature.Message
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder

class SignalRService {
    private lateinit var hubConnection: HubConnection

    @SuppressLint("CheckResult")
    fun startConnection() {
//        val hubUrl = "ws://192.168.34.106:7042/chatHub"
        val hubUrl = "ws://192.168.150.1:7042/chatHub"

        hubConnection = HubConnectionBuilder.create(hubUrl).build()

        hubConnection.on("ReceiveMessage", { userId, message ->
            Log.d("SignaLR", "Received message: $message from User: $userId")
        }, String::class.java, Message::class.java)

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
}