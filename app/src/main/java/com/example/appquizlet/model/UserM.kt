package com.example.appquizlet.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserM {
    private val userData = MutableLiveData<UserResponse>()

    fun setUserData(data: UserResponse) {
        userData.value = data
    }

    fun getUserData(): LiveData<UserResponse> {
        return userData
    }
}