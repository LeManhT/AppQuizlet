package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.StudySetModel
import com.google.android.material.chip.Chip

class RvStudySetItemAdapter(
    private val listStudySet: List<StudySetModel>,
    private val onStudySetItem: RVStudySetItem
) : RecyclerView.Adapter<RvStudySetItemAdapter.StudySetItemHolder>() {

    class StudySetItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_create_set_item, parent, false)
        return StudySetItemHolder(view)
    }

    override fun onBindViewHolder(holder: StudySetItemHolder, position: Int) {
        val currentItem = listStudySet[position]

        holder.itemView.apply {
            val txtStudySetTitle = findViewById<TextView>(R.id.txtStudySetTitle)
            val studySetChip = findViewById<Chip>(R.id.studySetChip)
            val imgStudySetAvatar = findViewById<ImageView>(R.id.imgStudySetAvatar)
            val txtStudySetUsername = findViewById<TextView>(R.id.txtStudySetUsername)
            val cardViewStudySet = findViewById<CardView>(R.id.studySetCardView)

            val countTermText =
                if (currentItem.countTerm!! > 1) "${currentItem.countTerm} terms" else
                    "${currentItem.countTerm} term"

            txtStudySetTitle.text = currentItem.name
            studySetChip.text = countTermText
//            imgStudySetAvatar.setImageResource(currentItem.avatar)
//            txtStudySetUsername.text = currentItem.username
//
//            if (currentItem.isSelected == true) {
//                cardViewStudySet.setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        holder.itemView.context,
//                        R.color.my_yellow
//                    )
//                )
//                cardViewStudySet.alpha = 0.8F
//            } else {
//                cardViewStudySet.setCardBackgroundColor(
//                    ContextCompat.getColor(
//                        holder.itemView.context,
//                        R.color.white
//                    )
//                )
//            }

            // Set item click listener
            holder.itemView.setOnClickListener {
                // Toggle the isSelected state
//                currentItem.isSelected = !currentItem.isSelected!!
//
//                // Notify the listener about the click event
                onStudySetItem.handleClickStudySetItem(currentItem,position)

                // Notify the adapter that the item at this position has changed
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listStudySet.size
    }
}
