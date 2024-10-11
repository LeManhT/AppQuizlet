package com.example.appquizlet.viewmodel.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appquizlet.entity.Story
import com.example.appquizlet.repository.story.StoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val repository: StoryRepository) : ViewModel() {

    val allStories: LiveData<List<Story>> = repository.allStories

    fun insert(story: Story) = viewModelScope.launch {
        repository.insert(story)
    }

    fun update(story: Story) = viewModelScope.launch {
        repository.update(story)
    }

    fun delete(story: Story) = viewModelScope.launch {
        repository.delete(story)
    }

    fun getStoryById(id: Int): LiveData<Story?> {
        val result = MutableLiveData<Story?>()
        viewModelScope.launch {
            result.postValue(repository.getStoryById(id))
        }
        return result
    }
}
