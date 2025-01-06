package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.newfeature.FriendRequestAdapter
import com.example.appquizlet.databinding.FragmentFriendBinding
import com.example.appquizlet.viewmodel.social.FriendRequestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFriend : Fragment() {
    private lateinit var binding: FragmentFriendBinding
    private val viewModel: FriendRequestViewModel by viewModels()
    private lateinit var adapter: FriendRequestAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FriendRequestAdapter(
            requests = emptyList(),
            onAccept = { request ->
                viewModel.acceptRequest(request.id)
            },
            onReject = { request ->
                viewModel.rejectRequest(request.id)
            }
        )

        binding.rvFriendRequests.adapter = adapter
        binding.rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.friendRequests.collect { requests ->
                    adapter.updateData(requests)
                }
            }
        }
    }
}