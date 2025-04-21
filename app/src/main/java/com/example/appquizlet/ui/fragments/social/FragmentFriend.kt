package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.SuggestedFriendsAdapter
import com.example.appquizlet.databinding.FragmentFriendBinding
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.FriendRequestViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFriend : Fragment() {
    private lateinit var binding: FragmentFriendBinding
    private val viewModel: FriendRequestViewModel by viewModels()
    private lateinit var adapter: SuggestedFriendsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startConnection()

//        adapter = FriendRequestAdapter(
//            requests = emptyList(),
//            onAccept = { request ->
////                viewModel.acceptRequest(request.id)
//            },
//            onReject = { request ->
////                viewModel.rejectRequest(request.id)
//            }
//        )
//
//        binding.rvFriendRequests.adapter = adapter
//        binding.rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
//
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.friendRequests.collect { requests ->
//                    adapter.updateData(requests)
//                }
//            }
//        }

        binding.tabFriends.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentSocialMain_to_fragmentFriendRequest)
        }

        adapter = SuggestedFriendsAdapter(
            emptyList(),
            onSendRequest = { user ->
                viewModel.sendFriendRequest(user.id)
            },
            onCancelRequest = { user ->
                viewModel.cancelFriendRequest(user.id)
            })

        binding.rvFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFriendRequests.adapter = adapter

        lifecycleScope.launch {
            viewModel.suggestedFriends.collectLatest { result ->
                result?.let {
                    if (it.isSuccess) {
                        adapter.updateData(it.getOrNull() ?: emptyList())
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Lỗi: ${it.exceptionOrNull()?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        viewModel.getSuggestedFriends(Helper.getDataUserId(requireContext()), requireContext())
    }
}