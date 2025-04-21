package com.example.appquizlet.repository.signalR

import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.services.SignalRService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRRepository @Inject constructor(
    private val signalRService: SignalRService
) {
    fun startConnection() {
        signalRService.startConnection()
    }

    fun stopConnection() {
        signalRService.stopConnection()
    }

    fun sendMessage(message: Message, senderId: String) {
        signalRService.sendMessage(message, senderId)
    }

    fun onMessageReceived(listener: (Message) -> Unit) {
        signalRService.setOnMessageReceivedListener(listener)
    }

    fun sendGroupMessage(userId: String, groupId: String, message: Message) {
        signalRService.sendGroupMessage(userId, groupId, message)
    }

    fun joinGroup(userId: String, groupId: String) {
        signalRService.joinGroup(userId, groupId)
    }

    fun leaveGroup(userId: String, groupId: String) {
        signalRService.leaveGroup(userId, groupId)
    }

    fun getGroupMessages(groupId: String, limit: Int = 50, beforeTimestamp: Long? = null) {
        signalRService.getGroupMessages(groupId, limit, beforeTimestamp)
    }

    fun onGroupMessageReceived(listener: (String, String, Message) -> Unit) {
        signalRService.setOnGroupMessageReceivedListener(listener)
    }

    fun onUserJoinedGroup(listener: (String, String) -> Unit) {
        signalRService.setOnUserJoinedGroupListener(listener)
    }

    fun onUserLeftGroup(listener: (String, String) -> Unit) {
        signalRService.setOnUserLeftGroupListener(listener)
    }

}
