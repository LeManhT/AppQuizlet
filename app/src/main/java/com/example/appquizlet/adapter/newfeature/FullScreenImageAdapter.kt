package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.databinding.ItemFullscreenImageBinding

class FullScreenImageAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<FullScreenImageAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemFullscreenImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    inner class ImageViewHolder(private val binding: ItemFullscreenImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(imageUrl: String) {
            Glide.with(binding.photoView.context)
                .load(imageUrl)
                .into(binding.photoView)
        }
    }
}