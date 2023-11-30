package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.FlashCardModel

class FlashcardItemAdapter(
    private val listFlashcard: List<FlashCardModel>
) : RecyclerView.Adapter<FlashcardItemAdapter.FlashcardItemHolder>() {
    class FlashcardItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlashcardItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flashcard_detail, parent, false)
        return FlashcardItemHolder(view)
    }

    override fun onBindViewHolder(holder: FlashcardItemHolder, position: Int) {
        holder.itemView.apply {
            val txtTerm = findViewById<TextView>(R.id.txtFlashcardDetailTerm)
            val txtDefinition = findViewById<TextView>(R.id.txtFlashcardDetailDefinition)
            txtTerm.text = listFlashcard[position].term
            txtDefinition.text = listFlashcard[position].definition
        }
    }

    override fun getItemCount(): Int {
        return listFlashcard.size
    }
}