package com.example.appquizlet.viewmodel.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.repository.social.SocialRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SocialViewModel(private val socialRepository: SocialRepository) : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

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

}