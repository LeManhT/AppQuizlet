package com.example.appquizlet.viewmodel.social

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.model.newfeature.Conversation
import com.example.appquizlet.model.newfeature.GroupMember
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.repository.signalR.SignalRRepository
import com.example.appquizlet.repository.social.SocialRepository
import com.example.appquizlet.util.Helper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val signalRRepository: SignalRRepository
) : ViewModel() {

    private val _activeGroup = MutableStateFlow<Conversation?>(null)
    val activeGroup: StateFlow<Conversation?> = _activeGroup.asStateFlow()

    private val _groupMessages = MutableStateFlow<List<Message>>(emptyList())
    val groupMessages: StateFlow<List<Message>> = _groupMessages.asStateFlow()

    private val _groupMembers = MutableStateFlow<List<GroupMember>>(emptyList())
    val groupMembers: StateFlow<List<GroupMember>> = _groupMembers.asStateFlow()

    private val _userGroups = MutableStateFlow<List<Conversation>>(emptyList())
    val userGroups: StateFlow<List<Conversation>> = _userGroups.asStateFlow()

    private val _messageStatus = MutableLiveData<MessageStatus>()
    val messageStatus: LiveData<MessageStatus> = _messageStatus

    private val _groupLoadingState = MutableStateFlow<GroupLoadingState>(GroupLoadingState.Idle)
    val groupLoadingState: StateFlow<GroupLoadingState> = _groupLoadingState.asStateFlow()

    // Adding the missing memberOperationState
    private val _memberOperationState = MutableStateFlow<MemberOperationState>(MemberOperationState.Idle)
    val memberOperationState: StateFlow<MemberOperationState> = _memberOperationState.asStateFlow()
    private var currentGroupId: String = ""

    init {
        setupSignalRListeners()
    }

    private fun setupSignalRListeners() {
        signalRRepository.onGroupMessageReceived { userId, groupId, message ->
            if (groupId == currentGroupId) {
                viewModelScope.launch {
                    val currentMessages = _groupMessages.value.toMutableList()
                    if (currentMessages.none { it.messageId == message.messageId }) {
                        currentMessages.add(message)
                        _groupMessages.value = currentMessages
                    }
                }
            }
        }

        signalRRepository.onUserJoinedGroup { userId, groupId ->
            if (groupId == currentGroupId) {
                loadGroupMembers(groupId)
            }
        }

        signalRRepository.onUserLeftGroup { userId, groupId ->
            if (groupId == currentGroupId) {
                loadGroupMembers(groupId)
            }
        }
    }

    fun loadUserGroups(userId: String) {
        viewModelScope.launch {
            _groupLoadingState.value = GroupLoadingState.Loading
            try {
                val groups = socialRepository.getUserGroups(userId)
                _userGroups.value = groups
                _groupLoadingState.value = GroupLoadingState.Success
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error loading user groups: ${e.message}")
                _groupLoadingState.value = GroupLoadingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun initWithConversation(conversation: Conversation) {
        _activeGroup.value = conversation
        currentGroupId = conversation.conversationId

        Log.d("GroupChatViewModel", "Set current group ID to: $currentGroupId")
    }
    fun loadGroupDetails(groupId: String) {
        // Chỉ tải thông tin chi tiết nếu chưa có _activeGroup hoặc không khớp với groupId
        if (_activeGroup.value == null || _activeGroup.value?.conversationId != groupId) {
            viewModelScope.launch {
                _groupLoadingState.value = GroupLoadingState.Loading
                try {
                    val group = socialRepository.getGroupById(groupId)
                    _activeGroup.value = group
                    _groupLoadingState.value = GroupLoadingState.Success
                } catch (e: Exception) {
                    Log.e("GroupChatViewModel", "Error loading group details: ${e.message}")
                    _groupLoadingState.value = GroupLoadingState.Error(e.message ?: "Unknown error")
                }
            }
        }
    }

    fun loadGroupMembers(groupId: String) {
        viewModelScope.launch {
            try {
                val members = socialRepository.getGroupMembers(groupId)
                _groupMembers.value = members
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error loading group members: ${e.message}")
            }
        }
    }

    fun loadGroupMessages(groupId: String, limit: Int = 50, beforeTimestamp: Long? = null) {
        viewModelScope.launch {
            try {
                val messages = socialRepository.getGroupMessages(groupId, limit, beforeTimestamp)
                _groupMessages.value = messages
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error loading group messages: ${e.message}")
            }
        }
    }

    // Cập nhật để sử dụng đối tượng Conversation đã nhận
    fun joinGroup(context: Context, conversation: Conversation) {
        val userId = Helper.getDataUserId(context)
        val groupId = conversation.conversationId

        // Thiết lập active group trước tiên
        _activeGroup.value = conversation
        currentGroupId = groupId

        viewModelScope.launch {
            try {
                signalRRepository.joinGroup(userId, groupId)
                loadGroupMembers(groupId)
                loadGroupMessages(groupId)
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error joining group: ${e.message}")
            }
        }
    }

    // Giữ lại phương thức này cho các trường hợp chỉ có groupId
//    fun joinGroup(context: Context, groupId: String) {
//        val userId = Helper.getDataUserId(context)
//        currentGroupId = groupId
//
//        viewModelScope.launch {
//            try {
//                signalRRepository.joinGroup(userId, groupId)
//                loadGroupDetails(groupId)
//                loadGroupMembers(groupId)
//                loadGroupMessages(groupId)
//            } catch (e: Exception) {
//                Log.e("GroupChatViewModel", "Error joining group: ${e.message}")
//            }
//        }
//    }

    fun leaveGroup(context: Context, groupId: String) {
        val userId = Helper.getDataUserId(context)

        viewModelScope.launch {
            _memberOperationState.value = MemberOperationState.Loading("Leaving group...")
            try {
                signalRRepository.leaveGroup(userId, groupId)
                if (currentGroupId == groupId) {
                    _memberOperationState.value = MemberOperationState.Success("You have left the group")
                    _activeGroup.value = null
                    _groupMessages.value = emptyList()
                    _groupMembers.value = emptyList()
                    currentGroupId = ""
                }
                else {
                    _memberOperationState.value = MemberOperationState.Error("Failed to leave group")
                }
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error leaving group: ${e.message}")
                _memberOperationState.value = MemberOperationState.Error("Error leaving group: ${e.message}")
            }
        }
    }

    fun sendMessage(context: Context, content: String, groupId: String) {
        val userId = Helper.getDataUserId(context)

        if (groupId.isEmpty()) {
             _messageStatus.value = MessageStatus.Error("No active group")
            return
        }

        val message = Message(
            senderId = userId,
            receiverId = groupId,
            content = content,
            timestamp = System.currentTimeMillis(),
            messageId = UUID.randomUUID().toString(),
            isSentByUser = true
        )

        viewModelScope.launch {
            try {
                _messageStatus.value = MessageStatus.Sending

                val currentMessages = _groupMessages.value.toMutableList()
                currentMessages.add(message)
                _groupMessages.value = currentMessages

                signalRRepository.sendGroupMessage(userId, groupId, message)

                _messageStatus.value = MessageStatus.Sent
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error sending message: ${e.message}")
                _messageStatus.value = MessageStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun createGroup(context: Context, name: String, description: String, type: String) {
        val userId = Helper.getDataUserId(context)

        viewModelScope.launch {
            try {
                _groupLoadingState.value = GroupLoadingState.Loading

                val group = Conversation(
                    name = name,
                    description = description,
                    createdBy = userId,
                    createdAt = System.currentTimeMillis(),
                    members = listOf(
                        GroupMember(
                            userId = userId,
                            role = "admin",
                            userName = "admin",
                            joinedAt = System.currentTimeMillis()
                        )
                    ),
                    isActive = true,
                    updatedAt = System.currentTimeMillis(),
                    type = type // Thêm trường type
                )

                val createdGroup = socialRepository.createGroup(group)
                _activeGroup.value = createdGroup
                currentGroupId = createdGroup.conversationId

                // Join the group via SignalR
                createdGroup.conversationId.let { signalRRepository.joinGroup(userId, it) }

                // Refresh the user's groups
                loadUserGroups(userId)

                _groupLoadingState.value = GroupLoadingState.Success
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error creating group: ${e.message}")
                _groupLoadingState.value = GroupLoadingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun addMemberToGroup(groupId: String, userId: String, role: String = "member") {
        viewModelScope.launch {
            _memberOperationState.value = MemberOperationState.Loading("Adding member...")
            try {
                val member = GroupMember(
                    userId = userId,
                    role = role,
                    joinedAt = System.currentTimeMillis()
                )

                val success = socialRepository.addMemberToGroup(groupId, member)
                Log.d("GroupChatViewModel", "Add member success: $success")
                if (success) {
                    _memberOperationState.value = MemberOperationState.Success("Member added successfully")
                    if (groupId == currentGroupId) {
                        loadGroupMembers(groupId)
                    }
                } else {
                    _memberOperationState.value = MemberOperationState.Error("Failed to add member")
                }
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error adding member: ${e.message}")
                _memberOperationState.value = MemberOperationState.Error("Error adding member: ${e.message}")
            }
        }
    }

    fun removeMemberFromGroup(groupId: String, userId: String) {
        viewModelScope.launch {
            _memberOperationState.value = MemberOperationState.Loading("Removing member...")
            try {
                val success = socialRepository.removeMemberFromGroup(groupId, userId)
                if (success) {
                    _memberOperationState.value = MemberOperationState.Success("Member removed successfully")
                    if (groupId == currentGroupId) {
                        loadGroupMembers(groupId)
                    }
                } else {
                    _memberOperationState.value = MemberOperationState.Error("Failed to remove member")
                }
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error removing member: ${e.message}")
                _memberOperationState.value = MemberOperationState.Error("Error removing member: ${e.message}")
            }
        }
    }

    fun updateMemberRole(groupId: String, userId: String, newRole: String) {
        viewModelScope.launch {
            _memberOperationState.value = MemberOperationState.Loading("Updating member role...")
            try {
                val success = socialRepository.updateMemberRole(groupId, userId, newRole)
                if (success) {
                    _memberOperationState.value = MemberOperationState.Success("Member role updated to $newRole")
                    if (groupId == currentGroupId) {
                        loadGroupMembers(groupId)
                    }
                } else {
                    _memberOperationState.value = MemberOperationState.Error("Failed to update member role")
                }
            } catch (e: Exception) {
                Log.e("GroupChatViewModel", "Error updating member role: ${e.message}")
                _memberOperationState.value = MemberOperationState.Error("Error updating member role: ${e.message}")
            }
        }
    }


    sealed class MessageStatus {
        object Sending : MessageStatus()
        object Sent : MessageStatus()
        data class Error(val message: String) : MessageStatus()
    }

    sealed class GroupLoadingState {
        object Idle : GroupLoadingState()
        object Loading : GroupLoadingState()
        object Success : GroupLoadingState()
        data class Error(val message: String) : GroupLoadingState()
    }

    // Adding the MemberOperationState sealed class
    sealed class MemberOperationState {
        object Idle : MemberOperationState()
        data class Loading(val message: String) : MemberOperationState()
        data class Success(val message: String) : MemberOperationState()
        data class Error(val message: String) : MemberOperationState()
    }
}