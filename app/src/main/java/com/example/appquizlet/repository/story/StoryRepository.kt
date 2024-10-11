package com.example.appquizlet.repository.story

import androidx.lifecycle.LiveData
import com.example.appquizlet.entity.Story
import com.example.appquizlet.roomDatabase.QuoteDatabase
import javax.inject.Inject


class StoryRepository @Inject constructor(
    private val quoteDb: QuoteDatabase
) {
    val allStories: LiveData<List<Story>> = quoteDb.storyDao().getAllStories()

    suspend fun insert(story: Story) {
        quoteDb.storyDao().insertStory(story)
    }

    suspend fun update(story: Story) {
        quoteDb.storyDao().updateStory(story)
    }

    suspend fun delete(story: Story) {
        quoteDb.storyDao().deleteStory(story)
    }

    suspend fun getStoryById(id: Int): Story? {
        return quoteDb.storyDao().getStoryById(id)
    }
}
