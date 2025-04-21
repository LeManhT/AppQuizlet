package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.PostAdapter
import com.example.appquizlet.databinding.FragmentFeedBinding
import com.example.appquizlet.model.newfeature.Post
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFeed : Fragment() {
    private val socialViewModel: SocialViewModel by viewModels()
    private lateinit var binding: FragmentFeedBinding
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        postAdapter = PostAdapter(object : PostAdapter.PostClickListener {
            override fun onCommentClicked(post: Post) {
                val action = FragmentSocialMainDirections.actionFragmentSocialMainToPostDetail(post)
                findNavController().navigate(action)
            }

            override fun onReactClicked(post: Post, isLiked: Boolean) {
                val userId = Helper.getDataUserId(requireContext())

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

                postAdapter.updatePost(updatedPost)

                if (isLiked) {
                    socialViewModel.unlikePost(userId, post.id)
                } else {
                    socialViewModel.likePost(userId, post.id)
                }

                socialViewModel.updateLikeStatus(post.id, !isLiked)
            }

            override fun onImageClicked(imageUrl: String, position: Int, allImages: List<String>) {
                val action = FragmentSocialMainDirections.actionFragmentSocialMainToImageViewerFragment(
                    allImages.toTypedArray(),
                    position
                )
                findNavController().navigate(action)
            }
        })

        binding.rvPosts.apply {
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