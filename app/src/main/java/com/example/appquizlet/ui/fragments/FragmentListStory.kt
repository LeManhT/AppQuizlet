package com.example.appquizlet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.StoryAdapter
import com.example.appquizlet.databinding.FragmentListStoryBinding
import com.example.appquizlet.entity.Story
import com.example.appquizlet.util.Helper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentListStory : Fragment() {
    private lateinit var binding: FragmentListStoryBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListStoryBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val stories = Helper.getStories(requireContext())
        storyAdapter = StoryAdapter(object : StoryAdapter.IStoryClick {
            override fun handleStoryClick(story: Story) {
                val action =
                    FragmentListStoryDirections.actionFragmentListStoryToFragmentStory(
                        story
                    )
                findNavController().navigate(action)
            }
        })
        storyAdapter.updateData(stories)

        binding.rvListSolutionImages.adapter = storyAdapter
        binding.rvListSolutionImages.layoutManager = LinearLayoutManager(context)

        binding.txtFavouriteWord.setOnClickListener {
            findNavController().navigate(R.id.action_fragmentListStory_to_fragmentFavouriteNewWord)
        }
    }

}