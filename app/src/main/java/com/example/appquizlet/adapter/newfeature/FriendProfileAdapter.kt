package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.model.newfeature.FriendResponse

class FriendProfileAdapter(
    private val userList: List<FriendResponse>,
    private val onItemClick: (FriendResponse) -> Unit
) : RecyclerView.Adapter<FriendProfileAdapter.UserViewHolder>() {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatarImageView: ImageView = itemView.findViewById(R.id.avatarFriendProfile)
        private val userNameTextView: TextView = itemView.findViewById(R.id.txtFriendProfileName)

        fun bind(user: FriendResponse) {
            userNameTextView.text = user.userName

//            val inputStream = itemView.context.resources.openRawResource(R.raw.ac204)
//            Glide.with(itemView.context)
//                .load(inputStream)
//                .into(avatarImageView)

            itemView.setOnClickListener {
                onItemClick(user)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(userList[position])
    }

    override fun getItemCount(): Int = userList.size
}