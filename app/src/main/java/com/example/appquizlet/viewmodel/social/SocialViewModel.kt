package com.example.appquizlet.viewmodel.social

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.example.appquizlet.model.UpdateUserResponse
import com.example.appquizlet.model.newfeature.ChatBotMessage
import com.example.appquizlet.model.newfeature.ChatbotConversation
import com.example.appquizlet.model.newfeature.Conversation
import com.example.appquizlet.model.newfeature.FriendResponse
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.newfeature.Post
import com.example.appquizlet.repository.signalR.SignalRRepository
import com.example.appquizlet.repository.social.ChatBotRepository
import com.example.appquizlet.repository.social.SocialRepository
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val signalRRepository: SignalRRepository,
    private val chatBotRepository: ChatBotRepository
) :
    ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _groupMessages = MutableStateFlow<List<Message>>(emptyList())
    val groupMessages: StateFlow<List<Message>> = _groupMessages

    private val messagesList = mutableListOf<Conversation>()
    private var currentGroupId: String = ""

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _chatBotMessages = MutableStateFlow<List<Message>>(emptyList())
    val chatBotMessages: StateFlow<List<Message>> = _chatBotMessages.asStateFlow()

    private val refreshTrigger = MutableStateFlow(Unit)

    @OptIn(ExperimentalCoroutinesApi::class)
    val posts: Flow<PagingData<Post>> = refreshTrigger
        .flatMapLatest { socialRepository.getPostPagingData() }
        .cachedIn(viewModelScope)


    private val _createPostStatus = MutableStateFlow<Boolean>(false)
    val createPostStatus: StateFlow<Boolean> get() = _createPostStatus

    private val _friendList = MutableStateFlow<List<FriendResponse>>(emptyList())
    val friendList: StateFlow<List<FriendResponse>> = _friendList

    private val _likeStatus = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val likeStatus: StateFlow<Map<String, Boolean>> = _likeStatus.asStateFlow()


    private val _updateUserInfoResponse = MutableLiveData<Boolean>()
    val updateUserInfoResponse: LiveData<Boolean> get() = _updateUserInfoResponse

    init {
        signalRRepository.startConnection()
        observeNewMessages()
    }

    override fun onCleared() {
        super.onCleared()
        signalRRepository.stopConnection()
    }

    fun fetchMessages(userId1: String, userId2: String) {
        viewModelScope.launch {
            socialRepository.getMessages(userId1, userId2).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            signalRRepository.sendMessage(message, message.senderId)
            _messages.emit(_messages.value + message)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            socialRepository.deleteMessage(messageId)
        }
    }

    fun createPost(
        authorId: String,
        content: String,
        author: String,
        imageUris: List<Uri>,
        fileUris: List<Uri>,
        context: Context
    ) {
        viewModelScope.launch {
            try {
                _createPostStatus.value = false
                val imageUrls = imageUris.mapNotNull { uri ->
                    socialRepository.uploadFile(uri, context)
                }
                val fileUrls = fileUris.mapNotNull { uri ->
                    socialRepository.uploadFile(uri, context)
                }
                socialRepository.createPost(
                    authorId,
                    content,
                    author,
                    imageUrls = imageUrls,
                    fileUrls = fileUrls
                )
                _createPostStatus.value = true
                refreshTrigger.value = Unit
            } catch (e: Exception) {
                e.printStackTrace()
                _createPostStatus.value = false
            }
        }
    }

    fun getUserConversations(userId: String) {
        viewModelScope.launch {
            Log.d("SocialViewModel", "Fetching user conversations for user ID: $userId")
            socialRepository.getUserConversations(userId).collectLatest { conversations ->
                _conversations.value = conversations
            }
        }
    }

    private fun observeNewMessages() {
        signalRRepository.onMessageReceived { newMessage ->
            viewModelScope.launch {
                val currentMessages = _messages.value.toMutableList()
                if (currentMessages.none { it.messageId == newMessage.messageId }) {
                    currentMessages.add(newMessage)
                    Log.d("SocialViewModel", "New message received: ${newMessage.messageId}")
                    _messages.value = currentMessages
                }
                Log.d("SocialViewModel", "No mess")
            }
        }
    }

    fun updatePost(postId: String, updatedFields: Post) {
        viewModelScope.launch {
            socialRepository.updatePost(postId, updatedFields)
        }
    }

    // Sửa lại hàm likePost trong SocialViewModel
    fun likePost(userId: String, postId: String) {
        viewModelScope.launch {
            try {
                // Bổ sung log để kiểm tra ID post
                Log.d("SocialViewModel", "Like post: $postId")

                // Kiểm tra IDs có đúng định dạng không
                if (postId.isEmpty() || userId.isEmpty()) {
                    Log.e("SocialViewModel", "Invalid postId or userId")
                    return@launch
                }

                // Cập nhật local state trước cho UI phản hồi nhanh
                updateLikeStatus(postId, true)

                socialRepository.likePost(userId, postId)
                    .catch { e ->
                        Log.e("SocialViewModel", "Error liking post: ${e.message}")
                        updateLikeStatus(postId, false)
                    }
                    .collect { result ->
                        result.onSuccess { response ->
                            Log.d("SocialViewModel", "Post liked successfully: $postId")
                            // Trạng thái đã được cập nhật, không cần làm gì thêm
                        }
                        result.onFailure { error ->
                            Log.e("SocialViewModel", "Failed to like post: ${error.message}")
                            updateLikeStatus(postId, false)
                        }
                    }
            } catch (e: Exception) {
                Log.e("SocialViewModel", "Exception when liking post: ${e.message}")
                updateLikeStatus(postId, false)
            }
        }
    }

    fun unlikePost(userId: String, postId: String) {
        viewModelScope.launch {
            try {
                // Bổ sung log để kiểm tra ID post
                Log.d("SocialViewModel", "Unlike post: $postId")

                // Kiểm tra IDs có đúng định dạng không
                if (postId.isEmpty() || userId.isEmpty()) {
                    Log.e("SocialViewModel", "Invalid postId or userId")
                    return@launch
                }

                // Cập nhật local state trước cho UI phản hồi nhanh
                updateLikeStatus(postId, false)

                socialRepository.unlikePost(userId, postId)
                    .catch { e ->
                        Log.e("SocialViewModel", "Error unliking post: ${e.message}")
                        updateLikeStatus(postId, true)
                    }
                    .collect { result ->
                        result.onSuccess { response ->
                            Log.d("SocialViewModel", "Post unliked successfully: $postId")
                            // Trạng thái đã được cập nhật, không cần làm gì thêm
                        }
                        result.onFailure { error ->
                            Log.e("SocialViewModel", "Failed to unlike post: ${error.message}")
                            updateLikeStatus(postId, true)
                        }
                    }
            } catch (e: Exception) {
                Log.e("SocialViewModel", "Exception when unliking post: ${e.message}")
                updateLikeStatus(postId, true)
            }
        }
    }

    // Make this function public so it can be called from the fragment
    fun updateLikeStatus(postId: String, isLiked: Boolean) {
        _likeStatus.update { currentMap ->
            val newMap = currentMap.toMutableMap()
            newMap[postId] = isLiked
            newMap
        }
    }

    // Improved version of checkPostLikeStatus
    fun checkPostLikeStatus(userId: String, posts: List<String>) {
        if (posts.isEmpty()) return

        viewModelScope.launch {
            try {
                val likedPosts = socialRepository.checkLikedPosts(userId, posts)
                val likeMap = posts.associateWith { postId ->
                    likedPosts.contains(postId)
                }
                _likeStatus.update { currentMap ->
                    val newMap = currentMap.toMutableMap()
                    newMap.putAll(likeMap)
                    newMap
                }
            } catch (e: Exception) {
                Log.e("SocialViewModel", "Error checking like status: ${e.message}")
            }
        }
    }

    fun getUserPosts(userId: String): Flow<PagingData<Post>> {
        return posts.map { pagingData ->
            pagingData.filter {
                Log.d("SocialViewModel", "Filter post: ${it.authorId} user ID : $userId")
                it.authorId == userId
            }
        }.cachedIn(viewModelScope)
    }

    fun updateAvatar(
        userId: String,
        avatarUri: Uri,
        context: Context,
        description: String = "Đã cập nhật ảnh đại diện mới!"
    ) {
        viewModelScope.launch {
            try {
                val avatarUrl = socialRepository.uploadFile(avatarUri, context)
                if (avatarUrl != null) {
                    val userInfo = UpdateUserResponse(
                        avatar = avatarUrl
                    )
                    val json = Gson().toJson(userInfo)
                    val requestBody =
                        RequestBody.create("application/json".toMediaTypeOrNull(), json)
                    socialRepository.updateUserInfo(context, userId, requestBody)
                    Log.d("SocialViewModel", "Cover URL: $avatarUrl")

                    createPost(
                        userId,
                        description, "Bạn", listOf(avatarUri), emptyList(), context
                    )
                } else {
                    Log.d("SocialViewModel", "avatarUrl is null")
                }
            } catch (e: Exception) {
                Log.d("SocialViewModel", e.message.toString())
                e.printStackTrace()
            }
        }
    }

    fun updateCover(
        userId: String,
        coverUri: Uri,
        context: Context,
        description: String? = "Đã cập nhật ảnh bìa mới!"
    ) {
        viewModelScope.launch {
            try {
                val coverUrl = socialRepository.uploadFile(coverUri, context)
                if (coverUrl != null) {
                    // Cập nhật ảnh bìa trong database
//                    socialRepository.updateUserCover(userId, coverUrl)
                    Log.d("SocialViewModel", "Cover URL: $coverUrl")
                    if (description != null) {
                        createPost(
                            userId,
                            description,
                            "Bạn",
                            listOf(coverUri),
                            emptyList(),
                            context
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUserInfo(context: Context, userId: String, body: RequestBody) {
        viewModelScope.launch {
            val result = socialRepository.updateUserInfo(context, userId, body)
            result.fold(
                onSuccess = { _updateUserInfoResponse.postValue(true) },
                onFailure = {
                    Timber.e(it, "Error updating user info")
                    _updateUserInfoResponse.postValue(
                        false
                    )
                }
            )
        }
    }

    fun fetchChatHistory(context: Context) {
        viewModelScope.launch {
            try {
                val chatBotConversations = socialRepository.getChatHistory(Helper.getDataUserId(context))
                val history = chatBotConversations?.let {
                    convertChatBotConversations(it, Helper.getDataUserId(context))
                } ?: emptyList()
                _chatBotMessages.value = history
            } catch (e: Exception) {
                Log.e("ChatBotVM", "Lỗi lấy lịch sử: ${e.message}")
            }
        }
    }

    private fun convertChatBotConversations(
        chatBotConversations: List<ChatbotConversation>,
        userId: String,
        botId: String = "chatbot"
    ): List<Message> {
        val result = mutableListOf<Message>()
        for (conversation in chatBotConversations) {
            for (item in conversation.messages) {
                val userMsg = Message(
                    senderId = userId,
                    receiverId = botId,
                    content = item.message,
                    timestamp = System.currentTimeMillis(),
                    isSentByUser = true
                )
                val botMsg = Message(
                    senderId = botId,
                    receiverId = userId,
                    content = item.response,
                    timestamp = System.currentTimeMillis(),
                    isSentByUser = false
                )
                result.add(userMsg)
                result.add(botMsg)
            }
        }
        return result
    }

    fun sendMessageToBot(context: Context, userMessage: String) {
        val userId = Helper.getDataUserId(context)

        val userMsg = Message(
            senderId = userId,
            receiverId = "chatbot",
            content = userMessage,
            isSentByUser = true
        )
        _chatBotMessages.update { it + userMsg }

        viewModelScope.launch {
            chatBotRepository.sendMessage(userId, userMessage)
                .catch { e ->
                    val errorMsg = Message(
                        senderId = "chatbot",
                        receiverId = userId,
                        content = "Lỗi: ${e.message}",
                        isSentByUser = false
                    )
                    _chatBotMessages.update { it + errorMsg }
                }
                .collect { response ->
                    val botMsg = Message(
                        senderId = "chatbot",
                        receiverId = userId,
                        content = response.response,
                        isSentByUser = false
                    )
                    _chatBotMessages.update { it + botMsg }
                }
        }
    }
}