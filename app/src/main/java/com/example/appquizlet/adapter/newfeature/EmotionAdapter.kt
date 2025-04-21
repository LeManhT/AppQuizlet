package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R

class EmotionAdapter(
    private val emotions: List<Pair<String, String>>,
    private val onClick: (String, String) -> Unit
) : RecyclerView.Adapter<EmotionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val emojiText: TextView = view.findViewById(R.id.emojiText)
        val emotionText: TextView = view.findViewById(R.id.emotionText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_emotion, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (emoji, text) = emotions[position]
        holder.emojiText.text = emoji
        holder.emotionText.text = text
        holder.itemView.setOnClickListener { onClick(emoji, text) }
    }

    override fun getItemCount() = emotions.size
}
