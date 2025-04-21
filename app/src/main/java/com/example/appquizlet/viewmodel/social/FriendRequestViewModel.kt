package com.example.appquizlet.viewmodel.social

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.model.newfeature.FriendRequest
import com.example.appquizlet.model.response.FriendRequestResponse
import com.example.appquizlet.model.response.FriendStatusResponse
import com.example.appquizlet.repository.signalR.SignalRFriendRepository
import com.example.appquizlet.repository.social.FriendRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(
    private val friendRequestRepository: FriendRequestRepository,
    private val signalRFriendRepository: SignalRFriendRepository
) : ViewModel() {
    private val _friendRequests = MutableStateFlow<Result<List<FriendRequest>>?>(null)
    val friendRequests: StateFlow<Result<List<FriendRequest>>?> get() = _friendRequests

    private val _friendRequestResult = MutableStateFlow<Result<String>?>(null)
    val friendRequestResult: StateFlow<Result<String>?> = _friendRequestResult

    private val _friendsList = MutableStateFlow<Result<List<UserResponse>>?>(null)
    val friendsList: StateFlow<Result<List<UserResponse>>?> = _friendsList

    private val _suggestedFriends = MutableStateFlow<Result<List<UserResponse>>?>(null)
    val suggestedFriends: StateFlow<Result<List<UserResponse>>?> = _suggestedFriends

    fun getFriendsList(userId: String) {
        viewModelScope.launch {
            friendRequestRepository.getFriendsList(userId).collectLatest { result ->
                _friendsList.value = result
            }
        }
    }

    fun getSuggestedFriends(userId: String, context: Context) {
        viewModelScope.launch {
            friendRequestRepository.getSuggestedFriends(userId, context).collectLatest { result ->
                _suggestedFriends.value = result
            }
        }
    }

    fun getReceivedFriendRequests(userId: String) {
        viewModelScope.launch {
            friendRequestRepository.getReceivedFriendRequests(userId).collectLatest { result ->
                _friendRequests.value = result
            }
        }
    }

    fun startConnection() {
        signalRFriendRepository.startConnection()
    }

    fun stopConnection() {
        signalRFriendRepository.stopConnection()
    }

    fun sendFriendRequest(receiverId: String) {
        Log.d("FriendRequestViewModel", "Sending friend request to $receiverId")
        signalRFriendRepository.sendFriendRequest(receiverId)
    }

    fun acceptFriendRequest(requestId: String) {
        signalRFriendRepository.acceptFriendRequest(requestId)
    }

    fun cancelFriendRequest(receiverId: String) {
        signalRFriendRepository.cancelFriendRequest(receiverId)
    }

    fun rejectFriendRequest(receiverId: String) {
        signalRFriendRepository.rejectFriendRequest(receiverId)
    }

    override fun onCleared() {
        super.onCleared()
        stopConnection()
    }
}