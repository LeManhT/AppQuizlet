package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.FlashCardModel

class LearnFlashcardAdapter(private val listFlashcards: List<FlashCardModel>, ) :
    RecyclerView.Adapter<LearnFlashcardAdapter.LearnFlashcardHolder>() {
    class LearnFlashcardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LearnFlashcardHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_flashcard_study, parent, false)
        return LearnFlashcardHolder(view)
    }

    override fun onBindViewHolder(holder: LearnFlashcardHolder, position: Int) {
        holder.itemView.apply {
            val txtFlashcardTerm = findViewById<TextView>(R.id.txtFlashcardTerm)
            val txtFlashcardDefinition = findViewById<TextView>(R.id.txtFlashcardDefinition)
            txtFlashcardTerm.text = listFlashcards[position].term
        }
    }

    override fun getItemCount(): Int {
        return listFlashcards.size
    }
}