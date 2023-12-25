package com.example.appquizlet.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object UserM {
    private val userData = MutableLiveData<UserResponse>()

    //    private var userDataStudySets = MutableLiveData<List<StudySetModel>>()
    private var allStudySets = MutableLiveData<StudySetModel>()

    private var dataAchievement = MutableLiveData<DetectContinueModel>()
    private var dataSetSearch = MutableLiveData<List<SearchSetModel>>()
    private var dataSettings = MutableLiveData<UpdateUserResponse>()

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

    fun getDataAchievements(): LiveData<DetectContinueModel> {
        return dataAchievement
    }

    fun setDataAchievements(data: DetectContinueModel) {
        dataAchievement.value = data
    }

    fun getDataSetSearch(): LiveData<List<SearchSetModel>> {
        return dataSetSearch
    }

    fun setDataSetSearch(data: List<SearchSetModel>) {
        dataSetSearch.value = data
    }

    fun getDataSettings() : LiveData<UpdateUserResponse> {
        return dataSettings
    }

    fun setDataSettings(data : UpdateUserResponse) {
        dataSettings.value = data
    }


}