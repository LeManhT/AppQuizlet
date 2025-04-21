package com.example.appquizlet.repository.social

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.model.newfeature.ChatBotMessage
import com.example.appquizlet.model.newfeature.ChatbotConversation
import com.example.appquizlet.model.newfeature.Conversation
import com.example.appquizlet.model.newfeature.GroupMember
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.newfeature.Post
import com.example.appquizlet.model.requests.CheckLikedPostsRequest
import com.example.appquizlet.model.requests.CreatePostRequest
import com.example.appquizlet.model.response.LikeResponse
import com.example.appquizlet.paging.PostPagingSource
import com.example.appquizlet.util.FileHelperUtils
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class SocialRepository @Inject constructor(private val apiService: ApiService) {

    fun getMessages(userId1: String, userId2: String): Flow<List<Message>> = flow {
        try {
            val response = apiService.getMessages(userId1, userId2)
            if (response.isSuccessful) {
                val messages = response.body()?.map { message ->
                    message.copy(isSentByUser = message.senderId == userId1)
                } ?: emptyList()
                emit(messages)
            } else {
                throw Exception("Error fetching messages: ${response.message()}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }

    fun getUserConversations(userId: String): Flow<List<Conversation>> = flow {
        try {
            val response = apiService.getUserConversations(userId)
            Log.d("SocialRepository", "Response: $response")
            if (response.isSuccessful) {
                val conversations = response.body() ?: emptyList()
                Log.d("SocialRepository", "Conversations: ${Gson().toJson(conversations)}")
                emit(conversations)
            } else {
                throw Exception("Error fetching conversations: ${response.message()}")
            }
        } catch (e: HttpException) {
            Log.d("SocialRepository", "HTTP error: ${e.message}")
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            Log.d("SocialRepository", "Unknown error: ${e.message}")
            throw Exception("Unknown error: ${e.message}")
        }
    }

    suspend fun sendMessage(message: Message): Boolean {
        return try {
            val response = apiService.sendMessage(message)
            if (response.isSuccessful) {
                true
            } else {
                throw Exception("Error sending message: ${response.message()}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }

    suspend fun deleteMessage(messageId: String) {
        return try {
            val response = apiService.deleteMessage(messageId)
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }

//    suspend fun getPosts(page: Int, pageSize: Int): List<Post> {
//        return apiService.getPosts(page, pageSize)
//    }

    fun getPostPagingData(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false),
            pagingSourceFactory = { PostPagingSource(apiService) }
        ).flow
    }

    suspend fun createPost(
        userId: String,
        content: String,
        author: String,
        imageUrls: List<String>,
        fileUrls: List<String>
    ): Post {
        val postRequest = CreatePostRequest(
            authorId = userId,
            author = author,
            content = content,
            createdAt = System.currentTimeMillis(),
            imageUrls = imageUrls,
            fileUrls = fileUrls
        )
        Log.d("postRequest", Gson().toJson(postRequest))
        val response = apiService.createPost(postRequest)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Response body is null")
        } else {
            Log.e("SocialRepository", "Error creating post: ${response.message()}")
            throw Exception("Error creating post: ${response.message()}")
        }
    }

    suspend fun uploadFile(fileUri: Uri, context: Context): String? {
        val file = FileHelperUtils.getFileFromUri(context, fileUri) ?: return null
        val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", file.name, requestBody)
        Log.d("SocialRepository", "Uploading file: ${file.name}")
        val response = apiService.uploadFile(filePart)
        if (response.isSuccessful) {
            Log.d("SocialRepository", "File uploaded successfully: ${response.body()?.url}")
        } else {
            Log.d("SocialRepository", "File uploaded failed: ${response.errorBody()?.string()}")
        }
        return if (response.isSuccessful) response.body()?.url else null
    }


    suspend fun updatePost(postId: String, updatedFields: Post) {
        try {
            Log.d("PostRepository", "Updating post with ID like: $postId")
            apiService.updatePost(postId, updatedFields)
        } catch (e: Exception) {
            Log.e("PostRepository", "Failed to update post", e)
        }
    }

    fun likePost(userId: String, postId: String): Flow<Result<Boolean>> = flow {
        try {
            // Log thông tin trước khi gọi API
            Log.d("SocialRepository", "Sending like request - UserId: $userId, PostId: $postId")

            // Tạo request body đúng định dạng
            val jsonObject = JSONObject().apply {
                put("userId", userId)
                put("postId", postId)
            }
            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

            // Đảm bảo endpoint API chính xác
            val response = apiService.likePost(userId, postId)

            if (response.isSuccessful) {
                Log.d("SocialRepository", "Like post API success: $postId")
                emit(Result.success(true))
            } else {
                // Log chi tiết lỗi
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("SocialRepository", "Like post API error: ${response.code()} - $errorBody")
                emit(Result.failure(Exception(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Like post exception: ${e.message}")
            emit(Result.failure(e))
        }
    }

    fun unlikePost(userId: String, postId: String): Flow<Result<Boolean>> = flow {
        try {
            // Log thông tin trước khi gọi API
            Log.d("SocialRepository", "Sending unlike request - UserId: $userId, PostId: $postId")

            // Tạo request body đúng định dạng
            val jsonObject = JSONObject().apply {
                put("userId", userId)
                put("postId", postId)
            }
            val requestBody = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

            // Đảm bảo endpoint API chính xác
            val response = apiService.unlikePost(userId, postId)

            if (response.isSuccessful) {
                Log.d("SocialRepository", "Unlike post API success: $postId")
                emit(Result.success(true))
            } else {
                // Log chi tiết lỗi
                val errorBody = response.errorBody()?.string() ?: "Unknown error"
                Log.e("SocialRepository", "Unlike post API error: ${response.code()} - $errorBody")
                emit(Result.failure(Exception(errorBody)))
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Unlike post exception: ${e.message}")
            emit(Result.failure(e))
        }
    }

    suspend fun checkLikedPosts(userId: String, postIds: List<String>): List<String> {
        if (postIds.isEmpty()) return emptyList()

        return try {
            Log.d("SocialRepository", "Checking liked posts for user: $userId, posts count: ${postIds.size}")

            val request = CheckLikedPostsRequest(userId, postIds)
            val response = apiService.checkLikedPosts(request)

            if (response.isSuccessful) {
                val likedPosts = response.body() ?: emptyList()
                Log.d("SocialRepository", "Liked posts found: ${likedPosts.size}")
                likedPosts
            } else {
                Log.e("SocialRepository", "Error checking liked posts: ${response.code()}, ${response.errorBody()?.string()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Exception checking liked posts: ${e.message}", e)
            emptyList()
        }
    }


    suspend fun updateUserInfo(
        context: Context,
        userId: String,
        body: RequestBody
    ): Result<UserResponse> {
        return try {
            val accessToken = Helper.getAccessToken(context)
            val response =
                apiService.updateUserInfo(authorization = "Bearer :$accessToken", userId, body)
            if (response.isSuccessful) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Unknown error"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error updating user info")
            Result.failure(e)
        }
    }

    suspend fun getChatHistory(userId: String): List<ChatbotConversation>? {
        val response = apiService.getChatHistory(userId)
        Log.d("SocialRepository", "Response: $response")
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
    }

    suspend fun getUserGroups(userId: String): List<Conversation> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserGroups(userId)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            } else {
                throw Exception("Failed to fetch user groups: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error fetching user groups: ${e.message}")
            throw e
        }
    }

    suspend fun getGroupById(groupId: String): Conversation = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGroupById(groupId)
            if (response.isSuccessful) {
                return@withContext response.body() ?: throw Exception("Group not found")
            } else {
                throw Exception("Failed to fetch group: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error fetching group: ${e.message}")
            throw e
        }
    }

    suspend fun getGroupMembers(groupId: String): List<GroupMember> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGroupMembers(groupId)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            } else {
                throw Exception("Failed to fetch group members: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error fetching group members: ${e.message}")
            throw e
        }
    }

    suspend fun getGroupMessages(
        groupId: String,
        limit: Int = 50,
        beforeTimestamp: Long? = null
    ): List<Message> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getGroupMessages(groupId, limit, beforeTimestamp)
            if (response.isSuccessful) {
                return@withContext response.body() ?: emptyList()
            } else {
                throw Exception("Failed to fetch group messages: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error fetching group messages: ${e.message}")
            throw e
        }
    }

    suspend fun createGroup(conversation: Conversation): Conversation = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createGroup(conversation)
            if (response.isSuccessful) {
                return@withContext response.body() ?: throw Exception("Failed to create group")
            } else {
                throw Exception("Failed to create group: ${response.code()}")
            }
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error creating group: ${e.message}")
            throw e
        }
    }

    suspend fun updateGroup(groupId: String, conversation: Conversation): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateGroup(groupId, conversation)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error updating group: ${e.message}")
            throw e
        }
    }

    suspend fun deleteGroup(groupId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteGroup(groupId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error deleting group: ${e.message}")
            throw e
        }
    }

    suspend fun addMemberToGroup(groupId: String, member: GroupMember) : Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("SocialRepository", "Adding member to group: $groupId, $member")
            val response = apiService.addMemberToGroup(groupId, member)
            Log.d("SocialRepository", "Response: ${response.code()}, ${response.message()}")
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error adding member to group: ${e.message}")
            throw e
        }
    }

    suspend fun removeMemberFromGroup(groupId: String, userId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.removeMemberFromGroup(groupId, userId)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error removing member from group: ${e.message}")
            throw e
        }
    }

    suspend fun updateMemberRole(groupId: String, userId: String, role: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val response = apiService.updateMemberRole(groupId, userId, role)
            return@withContext response.isSuccessful
        } catch (e: Exception) {
            Log.e("SocialRepository", "Error updating member role: ${e.message}")
            throw e
        }
    }

    // Modify the SocialRepository.kt function to be a suspend function returning List<UserResponse>
    suspend fun getUserFriends(userId: String): List<UserResponse> {
        try {
            val response = apiService.getUserFriends(userId)
            if (response.isSuccessful) {
                return response.body() ?: emptyList()
            } else {
                throw Exception("Error fetching friends: ${response.message()}")
            }
        } catch (e: HttpException) {
            throw Exception("HTTP error: ${e.message}")
        } catch (e: Exception) {
            throw Exception("Unknown error: ${e.message}")
        }
    }

}