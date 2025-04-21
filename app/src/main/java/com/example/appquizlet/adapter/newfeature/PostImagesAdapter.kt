package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ItemPostImageBinding

class PostImagesAdapter(
    private val imageUrls: List<String>,
    private val onImageClick: (String, Int) -> Unit
) : RecyclerView.Adapter<PostImagesAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemPostImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position], position)
    }

    override fun getItemCount(): Int = imageUrls.size

    inner class ImageViewHolder(private val binding: ItemPostImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String, position: Int) {
            Glide.with(binding.imageView.context)
                .load(imageUrl)
                .placeholder(R.drawable.user)
                .error(R.drawable.emoji)
                .into(binding.imageView)

            binding.imageView.setOnClickListener {
                onImageClick(imageUrl, position)
            }
        }
    }
}