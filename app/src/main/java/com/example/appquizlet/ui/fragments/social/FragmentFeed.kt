package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.PostAdapter
import com.example.appquizlet.databinding.FragmentFeedBinding
import com.example.appquizlet.viewmodel.social.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentFeed : Fragment() {
    private val viewModel: SocialViewModel by viewModels()
    private lateinit var binding: FragmentFeedBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {  
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collect { posts ->
                val adapter = PostAdapter(posts)
                binding.rvPosts.adapter = adapter
                binding.rvPosts.layoutManager = LinearLayoutManager(requireContext())
            }
        }
        viewModel.loadPosts()
    }
}