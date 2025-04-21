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
import com.example.appquizlet.adapter.newfeature.FriendRequestAdapter
import com.example.appquizlet.databinding.FragmentFriendRequestBinding
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.FriendRequestViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFriendRequest : Fragment() {
    private lateinit var binding: FragmentFriendRequestBinding
    private lateinit var friendRequestAdapter: FriendRequestAdapter
    private val viewModel: FriendRequestViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendRequestBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startConnection()

        friendRequestAdapter = FriendRequestAdapter(
            onAccept = { requestId ->
                viewModel.acceptFriendRequest(requestId.id)
            },
            onReject = { requestId ->
                viewModel.rejectFriendRequest(requestId.id)
            }
        )

        binding.rvListFriendRequests.adapter = friendRequestAdapter
        binding.rvListFriendRequests.layoutManager = LinearLayoutManager(requireContext())
        lifecycleScope.launch {
            viewModel.friendRequests.collectLatest { requests ->
                requests?.let {
                    if (it.isSuccess) {
                        it.getOrNull()?.let { it1 ->
                            if(it1.isEmpty()) {
                                binding.layoutNoData.visibility = View.VISIBLE
                                binding.rvListFriendRequests.visibility = View.GONE
                            } else {
                                binding.layoutNDA.visibility = View.GONE
                                binding.rvListFriendRequests.visibility = View.VISIBLE
                            }
                            friendRequestAdapter.updateData(it1)
                            Log.d("FriendRequestFragment", "Friend requests: ${Gson().toJson(it1)}")
                        }
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
        viewModel.getReceivedFriendRequests(Helper.getDataUserId(requireContext()))
    }
}