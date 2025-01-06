package com.example.appquizlet.adapter.newfeature

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.interfaceFolder.newfeature.IConversationClick
import com.example.appquizlet.model.newfeature.Conversation

class SearchListConversationAdapter(
    private val context: Context,
    private var listConversations: List<Conversation>,
    private var onIContactItemClick: IConversationClick? = null
) :
    RecyclerView.Adapter<SearchListConversationAdapter.ItemSearchBookHolder>() {
    inner class ItemSearchBookHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtContactName: TextView = itemView.findViewById(R.id.txtConversationName)
        val image: ImageView = itemView.findViewById(R.id.imageViewProfile)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSearchBookHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_conversation, parent, false)
        return ItemSearchBookHolder(view)
    }

    override fun onBindViewHolder(holder: ItemSearchBookHolder, position: Int) {
        val conversation = listConversations[position]

        holder.txtContactName.text = conversation.name

//        if (conversation.image?.isNotEmpty() == true) {
//            Glide.with(context)
//                .load(conversation.image)
//                .apply(
//                    RequestOptions()
//                        .centerCrop()
//                        .fitCenter()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                )
//                .into(holder.image)
//        } else {
//            Glide.with(context)
//                .load(R.drawable.owl_default_avatar)
//                .apply(
//                    RequestOptions()
//                        .centerCrop()
//                        .fitCenter()
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                )
//                .into(holder.image)
//        }


        holder.itemView.setOnClickListener {
            onIContactItemClick?.handleConversationCLick(conversation)
        }

    }

    override fun getItemCount(): Int {
        return listConversations.size
    }

    fun updateData(newConversations: List<Conversation>) {
        this.listConversations = newConversations
        notifyDataSetChanged()
    }

}