package com.example.appquizlet.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.appquizlet.databinding.FragmentCreateNewStoryBinding
import com.example.appquizlet.entity.NewWord
import com.example.appquizlet.entity.Story
import com.example.appquizlet.viewmodel.story.StoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentCreateNewStory : Fragment() {
    private val newWordsList = mutableListOf<Pair<EditText, EditText>>()
    private lateinit var binding: FragmentCreateNewStoryBinding
    private val storyViewModel by activityViewModels<StoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.addNewWordButton.setOnClickListener {
            addNewWordField()
        }

        binding.saveStoryButton.setOnClickListener {
            saveStory()
        }
    }

    // Function to dynamically add new word and meaning input fields
    private fun addNewWordField() {
        val wordInput = EditText(requireContext()).apply {
            hint = "Word"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val meaningInput = EditText(requireContext()).apply {
            hint = "Meaning"
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        binding.newWordsContainer.addView(wordInput)
        binding.newWordsContainer.addView(meaningInput)

        // Add to the list for later use when saving
        newWordsList.add(Pair(wordInput, meaningInput))
    }

    // Function to collect all inputs and save the story
    private fun saveStory() {
        val title = binding.storyTitleInput.text.toString()
        val content = binding.storyContentInput.text.toString()

        val newWords = newWordsList.map { (wordInput, meaningInput) ->
            NewWord(
                0,
                word = wordInput.text.toString(),
                meaning = meaningInput.text.toString()
            )
        }

        val story = Story(0, title, content, newWords, "")
        storyViewModel.insert(story)
        Log.d("AddStoryFragment", "Story: $story")
    }

}