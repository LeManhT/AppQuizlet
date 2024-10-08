package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.entity.NewWord

class NewWordAdapter(
    private val onTranslateClicked: INewWordClick? = null
) : RecyclerView.Adapter<NewWordAdapter.NewWordViewHolder>() {
    private var newWords: List<NewWord> = mutableListOf()

    interface INewWordClick {
        fun handleTranslateClick(newWord: NewWord, position: Int, view: View)
        fun handleAddToFavourite(newWord: NewWord)
        fun showSnackbar(message: String)
        fun deleteFavourite(newWord: NewWord) // New method for deleting favorite
    }

    inner class NewWordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val wordTextView: TextView = itemView.findViewById(R.id.wordTextView)
        val translatedMeaningTextView: TextView =
            itemView.findViewById(R.id.translatedMeaningTextView)
        val translateButton: ImageButton = itemView.findViewById(R.id.translateButton)
        val btnAddToFavourite: ImageButton = itemView.findViewById(R.id.btnAddToFavourite)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewWordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_new_word, parent, false)
        return NewWordViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewWordViewHolder, position: Int) {
        val newWord = newWords[position]
        holder.wordTextView.text = newWord.word
        holder.translatedMeaningTextView.text = newWord.meaning


        holder.translateButton.setOnClickListener { view ->
            onTranslateClicked?.handleTranslateClick(newWord, position, view)
        }

        // Set the icon based on the favorite status
        val favoriteIcon = if (newWord.isFavourite) {
            R.drawable.favourite // Replace with your active favorite icon
        } else {
            R.drawable.favourite_off // Replace with your inactive favorite icon
        }
        holder.btnAddToFavourite.setImageResource(favoriteIcon)

        // Handle favorite button click
        holder.btnAddToFavourite.setOnClickListener {
            // Toggle the favorite status
            newWord.isFavourite = !newWord.isFavourite

            if (newWord.isFavourite) {
                onTranslateClicked?.handleAddToFavourite(newWord)
            } else {
                onTranslateClicked?.deleteFavourite(newWord)
            }
            val message = if (newWord.isFavourite) {
                holder.itemView.context.getString(R.string.added_to_favorites)
            } else {
                holder.itemView.context.getString(R.string.removed_from_favorites)
            }
            onTranslateClicked?.showSnackbar(message)
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int {
        return newWords.size
    }

    fun updateData(newWordList: List<NewWord>) {
        this.newWords = newWordList
        notifyDataSetChanged()
    }

}