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
import androidx.navigation.fragment.findNavController
import androidx.paging.map
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.FriendProfileAdapter
import com.example.appquizlet.adapter.newfeature.PostAdapter
import com.example.appquizlet.databinding.FragmentFeedBinding
import com.example.appquizlet.databinding.FragmentProfilePostBinding
import com.example.appquizlet.model.newfeature.Post
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.FriendRequestViewModel
import com.example.appquizlet.viewmodel.social.SocialViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentProfilePost : Fragment() {
    private lateinit var binding: FragmentProfilePostBinding
    private val friendViewModel: FriendRequestViewModel by viewModels()
    private lateinit var adapter: FriendProfileAdapter
    private lateinit var postAdapter : PostAdapter
    private val socialViewModel: SocialViewModel by viewModels()

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

        adapter = FriendProfileAdapter {}
        binding.rvListProfileFriends.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvListProfileFriends.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            friendViewModel.friendsList.collect { result ->
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
        friendViewModel.getFriendsList(Helper.getDataUserId(requireContext()))

        binding.layoutAttachments.setOnClickListener {
            findNavController().navigate(R.id.fragmentCreatePost)
        }

        binding.etSearch.setOnClickListener {
            findNavController().navigate(R.id.fragmentCreatePost)
        }
        setupRecyclerView()
        observeViewModel()

    }
    private fun setupRecyclerView() {
        postAdapter = PostAdapter(object : PostAdapter.PostClickListener {
            override fun onCommentClicked(post: Post) {
                view?.post {
                    findNavController().navigate(R.id.action_fragmentSocialMain_to_postDetail)
                }
            }

            override fun onReactClicked(post: Post, isLiked: Boolean) {
                val userId = Helper.getDataUserId(requireContext())

                // Cập nhật UI ngay lập tức để phản hồi người dùng
                val updatedPost = if (isLiked) {
                    post.copy(
                        isLiked = false,
                        likes = post.likes - 1
                    ).also {
                        it.likedByUsers.remove(userId)
                    }
                } else {
                    post.copy(
                        isLiked = true,
                        likes = post.likes + 1
                    ).also {
                        it.likedByUsers.add(userId)
                    }
                }

                // Cập nhật UI trước khi gọi API
                postAdapter.updatePost(updatedPost)

                // Gọi API
                if (isLiked) {
                    socialViewModel.unlikePost(userId, post.id)
                } else {
                    socialViewModel.likePost(userId, post.id)
                }

                // Cập nhật trạng thái like trực tiếp trong ViewModel
                socialViewModel.updateLikeStatus(post.id, !isLiked)
            }

            override fun onImageClicked(imageUrl: String, position: Int, allImages: List<String>) {
                // Xử lý sự kiện click vào ảnh tại đây (nếu cần)
            }
        })

        binding.rvListCurrentUserPosts.apply {
            adapter = postAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            // Collect posts
            launch {
                socialViewModel.posts.collect { pagingData ->
                    postAdapter.submitData(pagingData)
                }
            }

            // Collect like status
            launch {
                socialViewModel.likeStatus.collectLatest { likeMap ->
                    postAdapter.updateLikes(likeMap)
                }
            }

            // Check initial post like status when data is loaded
            launch {
                postAdapter.loadStateFlow.collect { loadState ->
                    val currentPosts = postAdapter.snapshot().items
                    if (currentPosts.isNotEmpty()) {
                        socialViewModel.checkPostLikeStatus(
                            Helper.getDataUserId(requireContext()),
                            currentPosts.map { it.id }
                        )
                    }
                }
            }
        }
    }
}