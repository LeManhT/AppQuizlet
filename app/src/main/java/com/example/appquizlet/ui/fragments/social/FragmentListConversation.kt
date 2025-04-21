package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.newfeature.ListConversationAdapter
import com.example.appquizlet.adapter.newfeature.SearchListConversationAdapter
import com.example.appquizlet.databinding.FragmentListConversationBinding
import com.example.appquizlet.interfaceFolder.newfeature.IConversationClick
import com.example.appquizlet.model.newfeature.Conversation
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentListConversation : Fragment() {
    private lateinit var binding: FragmentListConversationBinding
    private lateinit var listConversationAdapter: ListConversationAdapter
    private lateinit var listSearchConversationAdapter: SearchListConversationAdapter
    private val socialViewModel by viewModels<SocialViewModel>()
    private var conversationLists: MutableList<Conversation> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListConversationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            socialViewModel.getUserConversations(Helper.getDataUserId(requireContext()))
        }

        listConversationAdapter = ListConversationAdapter(conversationLists) { conversation ->
            navigateBasedOnConversationType(conversation)
        }

        binding.rvListConversations.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvListConversations.adapter = listConversationAdapter

        listSearchConversationAdapter = context?.let {
            SearchListConversationAdapter(
                it,
                conversationLists,
                object : IConversationClick {
                    override fun handleConversationCLick(conversation: Conversation) {
                        navigateBasedOnConversationType(conversation)
                    }
                })
        }!!

        binding.rvListSearchConversations.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvListSearchConversations.adapter = listSearchConversationAdapter

        lifecycleScope.launch {
            socialViewModel.conversations.collectLatest { conversations ->
                binding.searchBar.visibility = View.VISIBLE
                binding.rvListConversations.visibility = View.GONE
                if (conversations.isEmpty()) {
                    binding.rvListConversations.visibility = View.GONE
                    binding.layoutNoData.visibility = View.VISIBLE
                } else {
                    binding.rvListConversations.visibility = View.VISIBLE
                    binding.layoutNoData.visibility = View.GONE
                }
                listConversationAdapter.updateData(conversations)
                conversationLists.clear()
                conversationLists.addAll(conversations)

                binding.searchView.editText.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                        // Trước khi văn bản thay đổi
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (s?.isEmpty() == true) {
                            conversationLists.clear()
                            conversationLists.addAll(conversations)
                            listSearchConversationAdapter.notifyDataSetChanged()
                        } else {
                            filterConversations(s.toString(), conversations.toMutableList())
                        }
                    }

                    override fun afterTextChanged(s: Editable?) {
                        // Sau khi văn bản thay đổi
                    }
                })
            }
        }
    }

    private fun navigateBasedOnConversationType(conversation: Conversation) {
        when (conversation.type) {
            ListConversationAdapter.TYPE_GROUP -> {
                Log.d("FragmentListConversation", "Conversation type: ${Gson().toJson(conversation)}")
                val action = FragmentListConversationDirections.actionFragmentListConversationToFragmentGroupChat(conversation)
                findNavController().navigate(action)
            }
            ListConversationAdapter.TYPE_PERSONAL -> {
                val action = FragmentListConversationDirections.actionFragmentListConversationToFragmentChat(conversation)
                findNavController().navigate(action)
            }
            else -> {
                // Mặc định sẽ điều hướng đến FragmentChat nếu type không xác định
                val action = FragmentListConversationDirections.actionFragmentListConversationToFragmentChat(conversation)
                findNavController().navigate(action)
            }
        }
    }

    private fun filterConversations(query: String?, originalList: MutableList<Conversation>) {
        val filteredList = mutableListOf<Conversation>()
        for (conversation in originalList) {
            if (conversation.name?.contains(query.orEmpty(), ignoreCase = true) == true
            ) {
                filteredList.add(conversation)
            }
        }

        if (filteredList.isEmpty()) {
            binding.rvListSearchConversations.visibility = View.GONE
            binding.layoutNoDataSearch.visibility = View.VISIBLE
        } else {
            binding.rvListSearchConversations.visibility = View.VISIBLE
            binding.layoutNoDataSearch.visibility = View.GONE
            conversationLists.clear()
            conversationLists.addAll(filteredList)
            listSearchConversationAdapter.notifyDataSetChanged()
        }
    }
}