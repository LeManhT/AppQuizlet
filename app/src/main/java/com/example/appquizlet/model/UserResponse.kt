package com.example.appquizlet.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class UserResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("seqId")
    val seqId: Int,
    @SerializedName("loginName")
    val loginName: String,
    @SerializedName("loginPassword")
    val loginPassword: String,
    @SerializedName("isSuspend")
    var isSuspend: Boolean,
    @SerializedName("userName")
    val userName: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("dateOfBirth")
    val dateOfBirth: String,
    @SerializedName("timeCreated")
    val timeCreated: Long,
    @SerializedName("documents")
    val documents: DocumentModel,
    @SerializedName("streak")
    val streak: StreakData,
    @SerializedName("achievement")
    val achievement: AchievementData
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserResponse

        if (id != other.id) return false
        if (seqId != other.seqId) return false
        if (loginName != other.loginName) return false
        if (loginPassword != other.loginPassword) return false
        if (isSuspend != other.isSuspend) return false
        if (userName != other.userName) return false
        if (email != other.email) return false
        if (dateOfBirth != other.dateOfBirth) return false
        if (timeCreated != other.timeCreated) return false
        if (documents != other.documents) return false
        if (streak != other.streak) return false
        if (achievement != other.achievement) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + seqId
        result = 31 * result + loginName.hashCode()
        result = 31 * result + loginPassword.hashCode()
        result = 31 * result + isSuspend.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + dateOfBirth.hashCode()
        result = 31 * result + timeCreated.hashCode()
        result = 31 * result + documents.hashCode()
        result = 31 * result + streak.hashCode()
        result = 31 * result + achievement.hashCode()
        return result
    }
}
@Parcelize
data class LoginResponse(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("user")
    val user: UserResponse
) : Parcelable
