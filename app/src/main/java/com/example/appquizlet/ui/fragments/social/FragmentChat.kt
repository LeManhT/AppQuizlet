package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.newfeature.ChatAdapter
import com.example.appquizlet.databinding.FragmentChatBinding
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneOffset

class FragmentChat : Fragment() {
    private val viewModel: SocialViewModel by viewModels()
    private lateinit var adapter: ChatAdapter
    private lateinit var binding : FragmentChatBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    "0",
                    "",
                    message,
                    LocalDateTime.now().toEpochSecond(ZoneOffset.MIN),
                    true,
                    attachments = mutableListOf()
                )
                viewModel.sendMessage(newMessage)
                binding.messageInput.text.clear()
            }
        }
        viewModel.fetchMessages(Helper.getDataUserId(requireContext()))
    }
}