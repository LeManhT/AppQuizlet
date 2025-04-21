package com.example.appquizlet.services

import android.annotation.SuppressLint
import android.util.Log
import com.example.appquizlet.model.newfeature.Post
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder

class   SignalRPostHubService {
    private lateinit var hubConnection: HubConnection
    private var postListener: ((Post) -> Unit)? = null

    @SuppressLint("CheckResult")
    fun startConnection() {
        val hubUrl = "ws://192.168.150.1:7042/postHub"

        hubConnection = HubConnectionBuilder.create(hubUrl).build()

        hubConnection.on("ReceivePost", { post ->
            Log.d("SignalR", "Received post: ${post.content}")
            postListener?.invoke(post)
        }, Post::class.java)

        hubConnection.start()
            .doOnComplete { Log.d("SignalR", "SignalR connected successfully") }
            .doOnError { error -> Log.d("SignalR", "Error in connection: ${error.message}") }
            .subscribe(
                { Log.d("SignalR", "Connection started") },
                { error -> Log.e("SignalR", "Connection failed: ${error.message}") }
            )
    }

    fun sendPost(post: Post) {
        Log.d("SignalR", "Post send request: ${hubConnection.connectionState.name}")
        if (hubConnection.connectionState.name == "CONNECTED") {
            hubConnection.send("CreatePost", post)
            Log.d("SignalR", "Post sent: ${post.content}")
        } else {
            Log.d("SignalR", "SignalR connection is not active")
        }
    }

    fun stopConnection() {
        hubConnection.stop()
        Log.d("SignalR", "SignalR connection stopped")
    }

    fun setOnPostReceivedListener(listener: (Post) -> Unit) {
        postListener = listener
    }
}