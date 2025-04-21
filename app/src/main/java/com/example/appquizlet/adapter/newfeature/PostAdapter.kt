package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingData
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ItemPostBinding
import com.example.appquizlet.model.newfeature.Post
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostAdapter(private val listener: PostClickListener) :
    PagingDataAdapter<Post, PostAdapter.PostViewHolder>(POST_COMPARATOR) {

    private val likeStatusMap = HashMap<String, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)
        post?.let {
            // Check if we have a stored like status for this post
            val isLiked = likeStatusMap[it.id] ?: it.isLiked
            holder.bind(it, isLiked)
        }
    }

    fun updateLikes(likeMap: Map<String, Boolean>) {
        likeStatusMap.clear()
        likeStatusMap.putAll(likeMap)
        notifyDataSetChanged()
    }

    fun updatePost(updatedPost: Post) {
        val position = snapshot().items.indexOfFirst { it.id == updatedPost.id }

        if (position != -1) {
            val currentList = snapshot().items.toMutableList()

            currentList[position] = updatedPost

            likeStatusMap[updatedPost.id] = updatedPost.isLiked

            CoroutineScope(Dispatchers.Main).launch {
                submitData(PagingData.from(currentList))
            }
        }
    }

    inner class PostViewHolder(private val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post, isLiked: Boolean) {
            with(binding) {
                tvAuthor.text = post.authorId
                tvContent.text = post.content

                // Format timestamp
                val date = Date(post.timestamp)
                val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                tvTimestamp.text = format.format(date)

                // Set profile image
                ivProfileImage.setImageResource(R.drawable.user)

                setupImageDisplay(post)

                setLikeButtonState(isLiked)

                // Set the correct like icon based on like status
                btnLike.setImageResource(
                    if (isLiked) R.drawable.heart
                    else R.drawable.heart_unfill
                )
                tvLikeCount.text = post.likes.toString()
                tvCommentCount.text = post.comments.size.toString()
                btnLike.setOnClickListener {
                    val currentLikeStatus = likeStatusMap[post.id] ?: post.isLiked
                    listener.onReactClicked(post, currentLikeStatus)
                }

                btnComment.setOnClickListener {
                    listener.onCommentClicked(post)
                }
            }
        }

        private fun setupImageDisplay(post: Post) {
            val imageUrls = post.imageUrls
            with(binding) {
                if (imageUrls.isNullOrEmpty()) {
                    imageContainer.visibility = View.GONE
                } else if (imageUrls.size == 1) {
                    // Display single image
                    imageContainer.visibility = View.VISIBLE
                    viewPagerImages.visibility = View.GONE
                    tabLayoutIndicator.visibility = View.GONE
                    singleImageView.visibility = View.VISIBLE

                    Glide.with(singleImageView.context)
                        .load(imageUrls[0])
                        .centerCrop()
                        .placeholder(R.drawable.user)
                        .into(singleImageView)

                    singleImageView.setOnClickListener {
                        listener.onImageClicked(imageUrls[0], 0, imageUrls)
                    }
                } else {
                    // Display multiple images with ViewPager
                    imageContainer.visibility = View.VISIBLE
                    singleImageView.visibility = View.GONE
                    viewPagerImages.visibility = View.VISIBLE
                    tabLayoutIndicator.visibility = View.VISIBLE

                    val imageAdapter = PostImagesAdapter(imageUrls) { url, position ->
                        listener.onImageClicked(url, position, imageUrls)
                    }
                    viewPagerImages.adapter = imageAdapter

                    // Connect TabLayout with ViewPager for indicator dots
                    TabLayoutMediator(tabLayoutIndicator, viewPagerImages) { _, _ ->
                        // No text needed for the tabs
                    }.attach()
                }
            }
        }

        private fun setLikeButtonState(isLiked: Boolean) {
            binding.btnLike.setImageResource(
                if (isLiked) R.drawable.heart
                else R.drawable.heart_unfill
            )
        }
    }

    interface PostClickListener {
        fun onCommentClicked(post: Post)
        fun onReactClicked(post: Post, isLiked: Boolean)
        fun onImageClicked(imageUrl: String, position: Int, allImages: List<String>)
    }

    companion object {
        private val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }
}