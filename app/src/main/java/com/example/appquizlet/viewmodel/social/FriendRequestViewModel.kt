package com.example.appquizlet.viewmodel.social

import androidx.lifecycle.ViewModel
import com.example.appquizlet.model.newfeature.FriendRequest
import com.example.appquizlet.repository.social.FriendRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FriendRequestViewModel @Inject constructor(private val friendRequestRepository: FriendRequestRepository) : ViewModel() {
    private val _friendRequests = MutableStateFlow<List<FriendRequest>>(emptyList())
    val friendRequests: StateFlow<List<FriendRequest>> get() = _friendRequests

    init {
        loadFriendRequests()
    }

    private fun loadFriendRequests() {
        _friendRequests.value = listOf(
            FriendRequest("1", "Quang Nguyễn", 37, "25 tuần"),
            FriendRequest("2", "Hoang Anh", 30, "2 năm"),
            FriendRequest("3", "Phạm Ngọc Nguyễn", 5, "2 năm")
        )
    }

    fun acceptRequest(requestId: String) {
        // Logic xử lý chấp nhận yêu cầu
    }

    fun rejectRequest(requestId: String) {
        // Logic xử lý từ chối yêu cầu
    }
}