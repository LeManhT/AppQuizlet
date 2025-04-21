package com.example.appquizlet.repository.signalR

import com.example.appquizlet.services.SignalRFriendHubService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignalRFriendRepository @Inject constructor(
    private val signalRFriendHubService: SignalRFriendHubService
) {
    fun startConnection() {
        signalRFriendHubService.startConnection()
    }

    fun stopConnection() {
        signalRFriendHubService.stopConnection()
    }

    fun sendFriendRequest(receiveId: String) {
        signalRFriendHubService.sendFriendRequest(receiveId)
    }

    fun acceptFriendRequest(receiveId: String) {
        signalRFriendHubService.acceptFriendRequest(receiveId)
    }

    fun cancelFriendRequest(receiveId: String) {
        signalRFriendHubService.cancelFriendRequest(receiveId)
    }

    fun rejectFriendRequest(receiveId: String) {
        signalRFriendHubService.rejectFriendRequest(receiveId)
    }

}
