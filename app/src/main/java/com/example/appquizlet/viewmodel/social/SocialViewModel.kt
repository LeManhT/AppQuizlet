package com.example.appquizlet.viewmodel.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.AchievementData
import com.example.appquizlet.model.DocumentModel
import com.example.appquizlet.model.StreakData
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.model.newfeature.FriendResponse
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.newfeature.Post
import com.example.appquizlet.repository.social.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialViewModel @Inject constructor(private val socialRepository: SocialRepository) :
    ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> get() = _posts

    private val _createPostStatus = MutableStateFlow<Boolean>(false)
    val createPostStatus: StateFlow<Boolean> get() = _createPostStatus

    private val _friendList = MutableStateFlow<List<FriendResponse>>(emptyList())
    val friendList: StateFlow<List<FriendResponse>> = _friendList

    fun fetchMessages(userId: String) {
        viewModelScope.launch {
            socialRepository.getMessages(userId).collect { messageList ->
                _messages.value = messageList
            }
        }
    }

    fun sendMessage(message: Message) {
        viewModelScope.launch {
            socialRepository.sendMessage(message)
            fetchMessages(message.senderId)
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            socialRepository.deleteMessage(messageId)
        }
    }

//    fun loadPosts() {
//        viewModelScope.launch {
//            try {
//                _posts.value = socialRepository.getPosts()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    init {
        loadPosts()
    }

    fun loadPosts() {
        val fakePosts = listOf(
            Post(
                id = "1",
                user = UserResponse(
                    id = "user_1",
                    seqId = 1,
                    loginName = "alan_patterson",
                    loginPassword = "password123",
                    isSuspend = false,
                    userName = "Alan Patterson",
                    email = "alan.patterson@example.com",
                    dateOfBirth = "1985-05-10",
                    timeCreated = System.currentTimeMillis() - 100000000,
                    documents = DocumentModel(mutableListOf(), mutableListOf(), mutableListOf()),
                    streak = StreakData(3, 3),
                    achievement = AchievementData(5, "dsds", mutableListOf()),
                    avatar = "https://randomuser.me/api/portraits/men/1.jpg"
                ),
                content = "Was great meeting up with Anna Ferguson and Dave Bishop at the breakfast talk! #breakfast",
                timestamp = System.currentTimeMillis() - 7200000,
                image = "https://images.unsplash.com/photo-1604908812316-5d6ab1a4e867",
                likes = 45,
                comments = 16
            ),
            Post(
                id = "2",
                user = UserResponse(
                    id = "user_2",
                    seqId = 2,
                    loginName = "pierre_rushman",
                    loginPassword = "securepass",
                    isSuspend = false,
                    userName = "Pierre Rushman",
                    email = "pierre.rushman@example.com",
                    dateOfBirth = "1990-10-15",
                    timeCreated = System.currentTimeMillis() - 200000000,
                    documents = DocumentModel(mutableListOf(), mutableListOf(), mutableListOf()),
                    streak = StreakData(3, 3),
                    achievement = AchievementData(5, "dsds", mutableListOf()),
                    avatar = "https://randomuser.me/api/portraits/men/2.jpg"
                ),
                content = "Exploring the mountains today! 🏞️ Love the fresh air and nature vibes.",
                timestamp = System.currentTimeMillis() - 3600000,
                image = "https://images.unsplash.com/photo-1517816743773-6e0fd518b4a6",
                likes = 32,
                comments = 8
            )
        )
        _posts.value = fakePosts
    }

    fun createPost(userId: String, content: String, image: String?) {
        viewModelScope.launch {
            try {
                val newPost = socialRepository.createPost(userId, content, image)
                _posts.value = listOf(newPost) + _posts.value
                _createPostStatus.value = true
            } catch (e: Exception) {
                e.printStackTrace()
                _createPostStatus.value = false
            }
        }
    }

    fun loadFakeData() {
        val fakeData = listOf(
            FriendResponse(
                id = "1",
                userName = "Nguyễn Văn A",
                email = "vana@gmail.com",
                dateOfBirth = "1995-01-01",
                timeCreated = System.currentTimeMillis(),
                avatar = "https://via.placeholder.com/150"
            ),
            FriendResponse(
                id = "2",
                userName = "Trần Thị B",
                email = "thib@gmail.com",
                dateOfBirth = "1996-05-12",
                timeCreated = System.currentTimeMillis(),
                avatar = "https://via.placeholder.com/150"
            ),
            FriendResponse(
                id = "3",
                userName = "Lê Văn C",
                email = "levanc@gmail.com",
                dateOfBirth = "1997-10-21",
                timeCreated = System.currentTimeMillis(),
                avatar = "https://via.placeholder.com/150"
            ),

            FriendResponse(
                id = "1",
                userName = "Nguyễn Văn A",
                email = "vana@gmail.com",
                dateOfBirth = "1995-01-01",
                timeCreated = System.currentTimeMillis(),
                avatar = "https://via.placeholder.com/150"
            ),
            FriendResponse(
                id = "2",
                userName = "Trần Thị B",
                email = "thib@gmail.com",
                dateOfBirth = "1996-05-12",
                timeCreated = System.currentTimeMillis(),
                avatar = "https://via.placeholder.com/150"
            ),
            FriendResponse(
                id = "3",
                userName = "Lê Văn C",
                email = "levanc@gmail.com",
                dateOfBirth = "1997-10-21",
                timeCreated = System.currentTimeMillis(),
                avatar = "https://via.placeholder.com/150"
            )
        )

        viewModelScope.launch {
            _friendList.emit(fakeData)
        }
    }
}