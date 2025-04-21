package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R

class ImageAdapter(private val onItemClick: (String) -> Unit) :
    RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var images: List<String> = mutableListOf()

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        Glide.with(holder.imageView.context)
            .load(images[position])
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClick(images[position])
        }
    }

    override fun getItemCount() = images.size

    fun updateData(newImages: List<String>) {
        this.images = newImages
        notifyDataSetChanged()
    }
}
