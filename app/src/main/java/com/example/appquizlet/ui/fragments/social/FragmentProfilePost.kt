package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appquizlet.adapter.newfeature.FriendProfileAdapter
import com.example.appquizlet.databinding.FragmentProfilePostBinding
import com.example.appquizlet.viewmodel.social.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentProfilePost : Fragment() {
    private lateinit var binding: FragmentProfilePostBinding
    private val viewModel: SocialViewModel by viewModels()
    private lateinit var adapter: FriendProfileAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePostBinding.inflate(inflater, container, false)
        Log.d("FragmentProfilePost", "on create view ")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("FragmentProfilePost", "onViewCreated: ")

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.friendList.collect { friends ->
                adapter = FriendProfileAdapter(friends, {})
                binding.rvListProfileFriends.layoutManager = GridLayoutManager(requireContext(), 3)
                binding.rvListProfileFriends.adapter = adapter
            }
        }
        viewModel.loadFakeData()
    }
}