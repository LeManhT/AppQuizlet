package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.entity.Story

class StoryAdapter(
    private val onStoryClick: IStoryClick? = null
) : RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    private var stories: List<Story> = mutableListOf()

    interface IStoryClick {
        fun handleStoryClick(story: Story)
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitleStory: TextView = itemView.findViewById(R.id.txtTitleStory)
        private val tvContentStory: TextView = itemView.findViewById(R.id.txtContentStory)
        private val imgNotification: ImageView = itemView.findViewById(R.id.imgNotification)

        fun bind(story: Story) {
            tvTitleStory.text = story.title
            tvContentStory.text = story.content
            Glide.with(itemView.context)
                .load(story.imagePath)
                .into(imgNotification)
            itemView.setOnClickListener { onStoryClick?.handleStoryClick(story) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.story_item, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(stories[position])
    }

    override fun getItemCount(): Int = stories.size

    fun updateData(newStories: List<Story>) {
        this.stories = newStories
        notifyDataSetChanged()
    }
}