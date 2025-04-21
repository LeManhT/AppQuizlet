package com.example.appquizlet.api.retrofit

import com.example.appquizlet.model.DetectContinueModel
import com.example.appquizlet.model.LoginResponse
import com.example.appquizlet.model.NoticeModel
import com.example.appquizlet.model.RankResultModel
import com.example.appquizlet.model.SearchSetModel
import com.example.appquizlet.model.ShareFolderModel
import com.example.appquizlet.model.ShareResponse
import com.example.appquizlet.model.UpdateUserResponse
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.model.admin.NotificationBody
import com.example.appquizlet.model.newfeature.ChatBotMessage
import com.example.appquizlet.model.newfeature.ChatbotConversation
import com.example.appquizlet.model.newfeature.Comment
import com.example.appquizlet.model.newfeature.Conversation
import com.example.appquizlet.model.newfeature.GroupMember
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.newfeature.Post
import com.example.appquizlet.model.requests.CheckLikedPostsRequest
import com.example.appquizlet.model.requests.CommentRequest
import com.example.appquizlet.model.requests.CreatePostRequest
import com.example.appquizlet.model.requests.CreateSetRequest
import com.example.appquizlet.model.requests.DialogflowRequest
import com.example.appquizlet.model.requests.UploadResponse
import com.example.appquizlet.model.response.DialogflowResponse
import com.example.appquizlet.model.response.FriendRequestResponse
import com.example.appquizlet.model.response.FriendsResponse
import com.example.appquizlet.model.response.LikeResponse
import com.example.quizletappandroidv1.models.admin.UserAdmin
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("User/SignUp")
    suspend fun createUser(@Body body: JsonObject): Response<JsonObject>

    @POST("User/Login")
    suspend fun loginUser(@Body body: JsonObject): Response<LoginResponse>

    @GET("User/Authorization")
    suspend fun loginWithToken(@Header("Authorization") token: String): Response<LoginResponse>

    @POST("Folder/Create")
    suspend fun createNewFolder(
        @Query("userId") userId: String,
        @Body body: JsonObject
    ): Response<UserResponse>

    @PUT("Folder/UpdateInfo")
    suspend fun updateFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Body body: JsonObject
    ): Response<UserResponse>


    @DELETE("Folder/Delete")
    suspend fun deleteFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String
    ): Response<UserResponse>

    @POST("StudySet/Create")
    suspend fun createNewStudySet(
        @Query("userId") userId: String,
        @Body body: CreateSetRequest
    ): Response<UserResponse>

    @DELETE("StudySet/Delete")
    suspend fun deleteStudySet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    ): Response<UserResponse>

    @PUT("StudySet/UpdateInfo")
    suspend fun updateStudySet(
        @Query("userId") userId: String,
        @Query("setId") setId: String,
        @Body body: CreateSetRequest
    ): Response<UserResponse>

    @POST("Folder/InsertSetExisting")
    suspend fun addSetToFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Body body: MutableSet<String>
    ): Response<UserResponse>

    @DELETE("Folder/RemoveSet")
    suspend fun removeSetFromFolder(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String,
        @Query("setId") setId: String
    ): Response<UserResponse>

    @GET("/StudySetPublic/GetOne")
    suspend fun getOneStudySet(
        @Query("setId") setId: String
    )

    @POST("User/DetectContinueStudy")
    suspend fun detectContinueStudy(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String,
        @Query("timeDetect") timeDetect: Long
    ): Response<DetectContinueModel>

    @GET("StudySet/ShareView")
    suspend fun getSetShareView(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    ): Response<ShareResponse>

    @GET("Folder/ShareView")
    suspend fun getFolderShareView(
        @Query("userId") userId: String,
        @Query("folderId") folderId: String
    ): Response<ShareFolderModel>

    @PUT("User/UpdateInfo")
    suspend fun updateUserInfo(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String, @Body body: RequestBody
    ): Response<UserResponse>

    @POST("StudySet/EnablePublic")
    suspend fun enablePublicSet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    )

    @POST("StudySet/DisablePublic")
    suspend fun disablePublicSet(
        @Query("userId") userId: String,
        @Query("setId") setId: String
    )

    @GET("StudySetPublic/Find")
    suspend fun findStudySet(
        @Query("keyword") keyword: String
    ): Response<List<SearchSetModel>>

    @GET("StudySetPublic/GetAll")
    suspend fun getAllSet(
    ): Response<List<SearchSetModel>>

    @POST("StudySet/AddToManyFolders")
    suspend fun addSetToManyFolder(
        @Query("userId") userId: String,
        @Query("setId") setId: String,
        @Body body: MutableSet<String>
    ): Response<UserResponse>

    @PUT("User/UpdateInfo")
    suspend fun updateUserInfoNoImg(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String,
        @Body body: JsonObject
    ): Response<UpdateUserResponse>

    @PUT("User/ChangePassword")
    suspend fun changePassword(
        @Header("Authorization") authorization: String,
        @Query("id") id: String,
        @Body body: JsonObject
    ): Response<UserResponse>

    @GET("User/GetRankResult")
    suspend fun getRankResult(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String
    ): Response<RankResultModel>


    @GET("User/GetAllCurrentNotices")
    suspend fun getAllCurrentNotices(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String
    ): Response<List<NoticeModel>>

    @POST("User/GetInfoByID")
    suspend fun getUserData(
        @Header("Authorization") authorization: String,
        @Query("ID") userId: String
    ): Response<UserResponse>

    @GET("Admin/GetUsers")
    suspend fun getListUserAdmin(
        @Query("from") from: Int,
        @Query("to") to: Int
    ): Response<List<UserResponse>>

    @POST("Admin/SetSuspendUser")
    suspend fun suspendUser(
        @Query("userId") userId: String,
        @Query("suspend") suspend: Boolean
    )

    @DELETE("Admin/DeleteUser")
    suspend fun deleteUser(
        @Query("userId") userId: String,
    )

    @POST("Admin/Login")
    suspend fun loginAdmin(
        @Query("loginName") username: String,
        @Query("password") password: String
    ): Response<UserAdmin>

    // API to push notification to a specific user with body
    @POST("Admin/PingNoticeUser")
    suspend fun pushNoticeForUser(
        @Query("userID") userID: String,
        @Body notificationBody: NotificationBody
    ): Response<Unit>

    // API to push notification to all users with body
    @POST("Admin/PingNoticeForAllUsers")
    suspend fun pushNoticeForAllUser(
        @Body notificationBody: NotificationBody
    ): Response<Unit>

    @POST("Message/sendMessage")
    suspend fun sendMessage(
        @Body message: Message
    ): Response<Unit>

    @DELETE("Message/DeleteMessage")
    suspend fun deleteMessage(
        @Query("messageId") messageId: String
    ): Response<Unit>

    @GET("Message/messages")
    suspend fun getMessages(
        @Query("userId1") userId: String, @Query("userId2") userId2: String
    ): Response<List<Message>>


    @GET("Post/getPosts")
    suspend fun getPosts(
        @Query("page") page: Int, @Query("pageSize") pageSize: Int
    ): Response<List<Post>>

    @POST("Post/CreatePost")
    suspend fun createPost(@Body request: CreatePostRequest): Response<Post>

    @GET("User/VerifyUser")
    suspend fun verifyUser(
        @Header("Authorization") authorization: String,
        @Query("userId") userId: String,
        @Query("plainPassword") plainPassword: String
    ): Response<Unit>

    @POST("Comment/AddComment/add")
    suspend fun addComment(@Body request: CommentRequest): Response<Comment>

    @GET("Comment/GetRootComments/{postId}/root-comments")
    suspend fun getRootComments(@Path("postId") postId: String): Response<List<Comment>>

    @GET("Comment/GetReplies/{commentId}/replies")
    suspend fun getReplies(@Path("commentId") commentId: String): Response<List<Comment>>

    @DELETE("Comment/DeleteComment/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: String): Response<Boolean>

    @Multipart
    @POST("Post/UploadFile/upload")
    suspend fun uploadFile(@Part file: MultipartBody.Part): Response<UploadResponse>

    @PUT("post/updatePost/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: String,
        @Body updatedFields: Post
    )

    @POST("Post/CheckLikedPosts")
    suspend fun checkLikedPosts(
        @Body request: CheckLikedPostsRequest
    ): Response<List<String>>

    @POST("Post/LikePost")
    suspend fun likePost(
        @Query("userId") userId: String,
        @Query("postId") postId: String
    ): Response<LikeResponse>

    @POST("Post/UnLikePost")
    suspend fun unlikePost(
        @Query("userId") userId: String,
        @Query("postId") postId: String
    ): Response<LikeResponse>

    @POST("Friend/SendFriendRequest")
    suspend fun sendFriendRequest(
        @Query("senderId") senderId: String,
        @Query("receiverId") receiverId: String
    ): Response<FriendRequestResponse>

    @GET("Friend/GetFriends")
    suspend fun getFriendsList(
        @Query("userId") userId: String
    ): Response<FriendsResponse>

    @GET("User/GetSuggestedFriends")
    suspend fun getSuggestedFriends(
        @Header("Authorization") token: String,
        @Query("userId") userId: String
    ): Response<List<UserResponse>>

    @GET("Friend/GetReceivedFriendRequests")
    suspend fun getReceivedFriendRequests(
        @Query("receiverId") userId: String
    ): Response<FriendRequestResponse>

    @GET("Message/GetMessagesByConversation")
    suspend fun getMessagesByConversation(
        @Query("conversationId") conversationId: String
    ): Response<List<Message>>

    @GET("Message/GetUserConversations")
    suspend fun getUserConversations(
        @Query("userId") userId: String
    ): Response<List<Conversation>>

    @POST("projects/{project-id}/agent/sessions/{session-id}:detectIntent")
    suspend fun sendMessage(
        @Path("project-id") projectId: String,
        @Path("session-id") sessionId: String,
        @Body request: DialogflowRequest
    ): DialogflowResponse

    @GET("chat/getHistory/{userId}")
    suspend fun getChatHistory(@Path("userId") userId: String): Response<List<ChatbotConversation>>

    // Group endpoints
    @GET("Group/GetUserGroups")
    suspend fun getUserGroups(@Query("userId") userId: String): Response<List<Conversation>>

    @GET("Group/GetGroup")
    suspend fun getGroupById(@Query("groupId") groupId: String): Response<Conversation>

    @GET("Group/getGroupMembers")
    suspend fun getGroupMembers(@Query("groupId") groupId: String): Response<List<GroupMember>>

    @GET("Group/getGroupMessages")
    suspend fun getGroupMessages(
        @Query("groupId") groupId: String,
        @Query("limit") limit: Int = 50,
        @Query("beforeTimestamp") beforeTimestamp: Long? = null
    ): Response<List<Message>>

    @POST("Group/CreateGroup")
    suspend fun createGroup(@Body group: Conversation): Response<Conversation>

    @PUT("Group/UpdateGroup")
    suspend fun updateGroup(
        @Query("groupId") groupId: String,
        @Body group: Conversation
    ): Response<Unit>

    @DELETE("Group/DeleteGroup")
    suspend fun deleteGroup(@Query("groupId") groupId: String): Response<Unit>

    @POST("Group/AddMemberToGroup/members")
    suspend fun addMemberToGroup(
        @Query("groupId") groupId: String,
        @Body member: GroupMember
    ): Response<Unit>

    @DELETE("Group/RemoveMemberFromGroup")
    suspend fun removeMemberFromGroup(
        @Query("groupId") groupId: String,
        @Query("userId") userId: String
    ): Response<Unit>

    @PUT("Group/UpdateMemberRole")
    suspend fun updateMemberRole(
        @Query("groupId") groupId: String,
        @Query("userId") userId: String,
        @Body role: String
    ): Response<Unit>

    @GET("Friend/GetUserFriends")
    suspend fun getUserFriends(@Query("userId") userId: String): Response<List<UserResponse>>

}
