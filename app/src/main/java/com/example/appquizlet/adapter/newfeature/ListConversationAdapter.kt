package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.model.newfeature.Conversation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ListConversationAdapter(
    private var conversations: List<Conversation>,
    private val onConversationClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ListConversationAdapter.ConversationViewHolder>() {

    companion object {
        const val TYPE_PERSONAL = "personal"
        const val TYPE_GROUP = "group"
    }

    class ConversationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewProfile: ImageView = view.findViewById(R.id.imageViewProfile)
        val txtConversationName: TextView = view.findViewById(R.id.txtConversationName)
        val txtViewLastMessage: TextView = view.findViewById(R.id.txtViewLastMessage)
        val txtConversationTimestamp: TextView = view.findViewById(R.id.txtConversationTimestamp)
        val txtMemberCount: TextView? = view.findViewById(R.id.txtMemberCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]

        // Display conversation name
        holder.txtConversationName.text = conversation.name ?: "Unknown"

        // Display last message
        holder.txtViewLastMessage.text = conversation.lastMessage ?: "No messages yet"

        // Format and display timestamp
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val lastMessageDate = Date(conversation.lastMessageTime * 1000)
        holder.txtConversationTimestamp.text = sdf.format(lastMessageDate)

        when (conversation.type) {
            TYPE_PERSONAL -> {
                holder.txtMemberCount?.visibility = View.GONE

                Glide.with(holder.itemView.context)
                    .load("https://i.pinimg.com/736x/50/08/ef/5008efb9df96969624d2674645027a3a.jpg")
                    .placeholder(R.drawable.user)
                    .circleCrop()
                    .into(holder.imageViewProfile)
            }
            TYPE_GROUP -> {
                holder.txtMemberCount?.let {
                    it.visibility = View.VISIBLE
                    it.text = "${conversation.members.size} members"
                }

                // Load group avatar
                Glide.with(holder.itemView.context)
                    .load("https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.reddit.com%2Fr%2FATLA%2Fcomments%2Fiyz7wl%2Fteam_avatar_fanart%2F&psig=AOvVaw3xjHu21HLBNhc3XcNMg8Cz&ust=1744990890155000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCNibvfSz34wDFQAAAAAdAAAAABAE")
                    .placeholder(R.drawable.icons8_users_50)
                    .circleCrop()
                    .into(holder.imageViewProfile)
            }
            else -> {
                holder.txtMemberCount?.visibility = View.GONE

                Glide.with(holder.itemView.context)
                    .load("https://example.com/default-avatar.jpg")
                    .placeholder(R.drawable.user)
                    .circleCrop()
                    .into(holder.imageViewProfile)
            }
        }

        // Set click listener
        holder.itemView.setOnClickListener {
            onConversationClick(conversation)
        }
    }

    override fun getItemCount(): Int = conversations.size

    fun updateData(newConversations: List<Conversation>) {
        this.conversations = newConversations
        notifyDataSetChanged()
    }
}