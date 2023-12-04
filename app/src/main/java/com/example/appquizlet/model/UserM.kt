package com.example.appquizlet.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserM {
    private val userData = MutableLiveData<UserResponse>()
//    private var userDataStudySets = MutableLiveData<List<StudySetModel>>()

    fun setUserData(data: UserResponse) {
        userData.value = data
    }

    fun getUserData(): LiveData<UserResponse> {
        return userData
    }

//    fun getUserDataStudySets(): LiveData<List<StudySetModel>> {
//        return userDataStudySets
//    }
//
//    fun setUserDataStudySets(data: List<StudySetModel>) {
//        userDataStudySets.value = data
//    }
}