package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.MainActivity_Logged_In
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.ChatAdapter
import com.example.appquizlet.api.retrofit.QuizletAIService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.databinding.FragmentChatBotBinding
import com.example.appquizlet.model.newfeature.Message
import com.example.appquizlet.model.requests.DialogflowRequest
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentChatBot : Fragment() {
    private lateinit var adapter: ChatAdapter
    private lateinit var binding: FragmentChatBotBinding
    private lateinit var quizletAIService: QuizletAIService
    private val messages = mutableListOf<Message>()
    private val viewModel: SocialViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChatBotBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messages.addAll(
            listOf(
                Message(
                    messageId = "msg2",
                    senderId = "chatbot",
                    receiverId = "user123",
                    content = "Chào bạn! Tôi có thể giúp gì?",
                    timestamp = System.currentTimeMillis() - 55000,
                    conversationId = "conv1",
                    isRead = true,
                    isDeleted = false,
                    isPinned = false,
                    isSentByUser = false
                )
            )
        )
        adapter = ChatAdapter(messages)
        quizletAIService =
            RetrofitHelper.getQuizletAIServiceInstance().create(QuizletAIService::class.java)
        binding.rvMessages.adapter = adapter
        binding.rvMessages.layoutManager = LinearLayoutManager(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chatBotMessages.collect { messageList ->
                messages.clear()
                messages.addAll(messageList)
                adapter.notifyDataSetChanged()
                binding.rvMessages.scrollToPosition(messages.size - 1)
            }
        }
        binding.sendMessageButton.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessageToChatBot(messageText)
                binding.messageInput.text?.clear()
            }
        }

        binding.btnCloseChatbot.setOnClickListener {
            // Đóng fragment hiện tại và quay lại fragment trước đó
            requireActivity().supportFragmentManager.popBackStack()

            // Hiển thị lại FAB trong MainActivity
            if (requireActivity() is MainActivity_Logged_In) {
                (requireActivity() as MainActivity_Logged_In).findViewById<View>(R.id.fab_chatbot).visibility = View.VISIBLE
            }
        }

        viewModel.fetchChatHistory(requireContext())
    }

    private fun sendMessageToChatBot(message: String) {
        val userId = Helper.getDataUserId(requireContext())
        val sessionId = "111"
        val request = DialogflowRequest(userId, sessionId, message)

        val userMessage = Message(
            senderId = userId,
            receiverId = "chatbot",
            content = message,
            isSentByUser = true
        )
        messages.add(userMessage)
        adapter.notifyItemInserted(messages.size - 1)
        binding.rvMessages.scrollToPosition(messages.size - 1)

        lifecycleScope.launch {
            try {
                val response = quizletAIService.sendMessageToBot(request)
                if (response.isSuccessful) {
                    val botReply = response.body()?.response ?: "Chatbot không phản hồi"

                    val botMessage = Message(
                        senderId = "chatbot",
                        receiverId = userId,
                        content = botReply,
                        isSentByUser = false
                    )
                    Log.d("ChatFragment", "Bot reply: ${Gson().toJson(botReply)}")
                    messages.add(botMessage)
                    adapter.notifyItemInserted(messages.size - 1)
                    binding.rvMessages.scrollToPosition(messages.size - 1)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Lỗi API: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Lỗi mạng: ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}