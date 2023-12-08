package com.example.appquizlet.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserM {
    private val userData = MutableLiveData<UserResponse>()

    //    private var userDataStudySets = MutableLiveData<List<StudySetModel>>()
    private var allStudySets = MutableLiveData<StudySetModel>()

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

    fun getAllStudySets(): LiveData<StudySetModel> {
        return allStudySets
    }

    fun setAllStudySet(data: StudySetModel) {
        allStudySets.value = data
    }
}