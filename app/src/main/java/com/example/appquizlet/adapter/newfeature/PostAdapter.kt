package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.model.newfeature.Post

class PostAdapter(private val posts: List<Post>) :
    RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.iv_avatar)
        val username: TextView = view.findViewById(R.id.tv_username)
        val content: TextView = view.findViewById(R.id.tv_content)
        val postImage: ImageView = view.findViewById(R.id.iv_post_image)
        val likes: TextView = view.findViewById(R.id.tv_likes)
        val comments: TextView = view.findViewById(R.id.tv_comments)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_post, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
//        Glide.with(holder.itemView.context).load(post.user.avatar).into(holder.avatar)
        holder.username.text = post.user.userName
        holder.content.text = post.content
        holder.likes.text = "${post.likes} Likes"
        holder.comments.text = "${post.comments} Comments"
        if (!post.image.isNullOrBlank()) {
            holder.postImage.visibility = View.VISIBLE
//            Glide.with(holder.itemView.context).load(post.image).into(holder.postImage)
        } else {
            holder.postImage.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = posts.size
}
