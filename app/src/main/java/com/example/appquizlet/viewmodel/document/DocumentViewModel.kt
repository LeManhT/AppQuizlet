package com.example.appquizlet.viewmodel.document

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.repository.document.DocumentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(private val documentRepository: DocumentRepository) :
    ViewModel() {

    private val _updateUserInfoResponse = MutableLiveData<Boolean>()
    val updateUserInfoResponse: LiveData<Boolean> get() = _updateUserInfoResponse
    fun updateUserInfo(userId: String, body: RequestBody) {
        viewModelScope.launch {
            val result = documentRepository.updateUserInfo(userId, body)
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
}