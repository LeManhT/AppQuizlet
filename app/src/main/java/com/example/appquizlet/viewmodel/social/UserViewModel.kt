package com.example.appquizlet.viewmodel.social

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.repository.social.SocialRepository
import com.example.appquizlet.util.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    // Lưu trữ toàn bộ danh sách bạn bè (không thay đổi khi tìm kiếm)
    private val _allFriends = MutableStateFlow<List<UserResponse>>(emptyList())

    // Danh sách bạn bè hiển thị (có thể thay đổi khi tìm kiếm)
    private val _friends = MutableStateFlow<List<UserResponse>>(emptyList())
    val friends: StateFlow<List<UserResponse>> = _friends.asStateFlow()

    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    private var currentSearchQuery = ""

    fun loadFriends(context: Context) {
        val userId = Helper.getDataUserId(context)
        Log.d("UserViewModel", "Loading friends for user with ID: $userId")
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                val friendsList = socialRepository.getUserFriends(userId)
                _allFriends.value = friendsList

                // Nếu có query tìm kiếm, áp dụng nó
                if (currentSearchQuery.isNotEmpty()) {
                    searchFriends(currentSearchQuery)
                } else {
                    _friends.value = friendsList
                }

                _loadingState.value = LoadingState.Success
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error loading friends: ${e.message}")
                _loadingState.value = LoadingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // Phương thức để lọc bạn bè dựa trên truy vấn tìm kiếm
    fun searchFriends(query: String) {
        currentSearchQuery = query
        viewModelScope.launch {
            if (query.isEmpty()) {
                // Nếu truy vấn trống, hiển thị tất cả bạn bè
                _friends.value = _allFriends.value
            } else {
                // Lọc bạn bè theo truy vấn
                _friends.value = _allFriends.value.filter { friend ->
                    (friend.userName?.contains(query, ignoreCase = true) == true) ||
                            (friend.email?.contains(query, ignoreCase = true) == true) ||
                            (friend.id?.contains(query, ignoreCase = true) == true)
                }
            }
        }
    }

    sealed class LoadingState {
        object Idle : LoadingState()
        object Loading : LoadingState()
        object Success : LoadingState()
        data class Error(val message: String) : LoadingState()
    }
}