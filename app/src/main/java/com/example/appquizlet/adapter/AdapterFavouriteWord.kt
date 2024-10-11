package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.entity.NewWord

class AdapterFavouriteWord(
    private val onFavouriteWordClick: IFavouriteClick? = null
) : RecyclerView.Adapter<AdapterFavouriteWord.FavouriteWordViewHolder>() {

    private var favouriteWords: List<NewWord> = emptyList()

    interface IFavouriteClick {
        fun onWordClick(favouriteWord: NewWord)
        fun onRemoveClick(favouriteWord: NewWord)
    }

    inner class FavouriteWordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordTextView: TextView = itemView.findViewById(R.id.tvWord)
        private val meaningTextView: TextView = itemView.findViewById(R.id.tvMeaning)
        private val removeButton: ImageButton = itemView.findViewById(R.id.btnRemove)

        fun bind(favouriteWord: NewWord) {
            wordTextView.text = favouriteWord.word
            meaningTextView.text = favouriteWord.meaning

            itemView.setOnClickListener {
                onFavouriteWordClick?.onWordClick(favouriteWord)
            }

            removeButton.setOnClickListener {
                onFavouriteWordClick?.onRemoveClick(favouriteWord)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteWordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favourite_word, parent, false)
        return FavouriteWordViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteWordViewHolder, position: Int) {
        holder.bind(favouriteWords[position])
    }

    override fun getItemCount(): Int {
        return favouriteWords.size
    }

    fun setFavouriteWords(words: List<NewWord>) {
        this.favouriteWords = words
        notifyDataSetChanged()
    }
}