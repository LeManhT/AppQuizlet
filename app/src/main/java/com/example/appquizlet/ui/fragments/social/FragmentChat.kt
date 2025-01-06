package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.newfeature.ChatAdapter
import com.example.appquizlet.databinding.FragmentChatBinding
import com.example.appquizlet.model.newfeature.Attachment
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.services.SignalRService
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentChat : Fragment() {
    private val viewModel: SocialViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var binding : FragmentChatBinding
    private lateinit var signalRService: SignalRService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signalRService = SignalRService()
        signalRService.startConnection()

        adapter = ChatAdapter(emptyList())
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.messages.collect { messages ->
                adapter.updateMessages(messages)
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }

        binding.sendMessageButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotBlank()) {
                val newMessage = Message(
                    senderId = Helper.getDataUserId(requireContext()),
                    receiverId = "6596c374a34b72b309b19985",
                    content = "Hello, this is a test message!",
                    timestamp = System.currentTimeMillis(),
                    conversationId = "60b9e72b7d1d4c7c3f69a4fc",
                    isRead = false,
                    isDeleted = false,
                    attachments = listOf(
                        Attachment(
                            type = "image",
                            url = "https://example.com/image.jpg",
                            fileName = "image.jpg",
                            fileSize = 2048L
                        ),
                        Attachment(
                            type = "pdf",
                            url = "https://example.com/document.pdf",
                            fileName = "document.pdf",
                            fileSize = 1048576L
                        )
                    ),
                    isPinned = false
                )
//                viewModel.sendMessage(newMessage)
                signalRService.sendMessage(newMessage, Helper.getDataUserId(requireContext()))
                binding.messageInput.text.clear()
            }
        }
//        viewModel.fetchMessages(Helper.getDataUserId(requireContext()))
    }

    override fun onDestroy() {
        super.onDestroy()
        signalRService.stopConnection()
    }

}