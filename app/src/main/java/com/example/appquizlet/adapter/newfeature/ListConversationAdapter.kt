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

    class ConversationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageViewProfile: ImageView = view.findViewById(R.id.imageViewProfile)
        val txtConversationName: TextView = view.findViewById(R.id.txtConversationName)
        val txtViewLastMessage: TextView = view.findViewById(R.id.txtViewLastMessage)
        val txtConversationTimestamp: TextView = view.findViewById(R.id.txtConversationTimestamp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_conversation, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        val conversation = conversations[position]

        // Set name
        holder.txtConversationName.text = conversation.name ?: "Unknown"

        // Set last message
        holder.txtViewLastMessage.text = conversation.lastMessage ?: "No messages yet"

        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val lastMessageDate = Date(conversation.lastMessageTime * 1000)
        holder.txtConversationTimestamp.text = sdf.format(lastMessageDate)

        // Load avatar (placeholder in case of null or error)
        Glide.with(holder.itemView.context)
            .load("https://example.com/default-avatar.jpg") // Change to actual avatar URL if available
            .placeholder(R.drawable.user) // Add your own placeholder
            .circleCrop()
            .into(holder.imageViewProfile)

        holder.itemView.setOnClickListener {
            onConversationClick(conversation)
        }
    }

    override fun getItemCount(): Int = conversations.size

    // Update data in the adapter
    fun updateData(newConversations: List<Conversation>) {
        this.conversations = newConversations
        notifyDataSetChanged()
    }
}