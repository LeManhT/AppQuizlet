package com.example.appquizlet.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.StoryAdapter
import com.example.appquizlet.databinding.FragmentManageStoryBinding
import com.example.appquizlet.entity.Story
import com.example.appquizlet.viewmodel.story.StoryViewModel
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentManageStory : Fragment() {
    private lateinit var binding: FragmentManageStoryBinding
    private val storyViewModel by viewModels<StoryViewModel>()
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storyAdapter = StoryAdapter(object : StoryAdapter.IStoryClick {
            override fun handleStoryClick(story: Story) {

            }
        })

        binding.rvManageStory.apply {
            adapter = storyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.addNewStory.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentManageStory2_to_fragmentCreateNewStory2)
        }

        storyViewModel.allStories.observe(viewLifecycleOwner) { stories ->
            storyAdapter.updateData(stories)
            Log.d("ManageStoryFragment", "Stories: ${Gson().toJson(stories)}")
        }

    }

}