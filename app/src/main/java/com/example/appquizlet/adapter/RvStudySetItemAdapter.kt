package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.StudySetItemData
import com.google.android.material.chip.Chip

class RvStudySetItemAdapter(private val listStudySet: List<StudySetItemData>) :
    RecyclerView.Adapter<RvStudySetItemAdapter.StudySetItemHolder>() {
    class StudySetItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudySetItemHolder {
        val view =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.layout_create_set_item, parent, false)
        return RvStudySetItemAdapter.StudySetItemHolder(view)
    }

    override fun onBindViewHolder(holder: StudySetItemHolder, position: Int) {
        holder.itemView.apply {
            val txtStudySetTitle = findViewById<TextView>(R.id.txtStudySetTitle)
            val studySetChip = findViewById<Chip>(R.id.studySetChip)
            val imgStudySetAvatar = findViewById<ImageView>(R.id.imgStudySetAvatar)
            val txtStudySetUsername = findViewById<TextView>(R.id.txtStudySetUsername)
            val countTermText = run {
                if (listStudySet[position].countTerms > 1) "${listStudySet[position].countTerms} terms" else
                    "${listStudySet[position].countTerms} term"
            }
            txtStudySetTitle.setText(listStudySet[position].title)

            studySetChip.setText(countTermText)
            imgStudySetAvatar.setImageResource(listStudySet[position].avatar)
            txtStudySetUsername.setText(listStudySet[position].username)
        }
    }

    override fun getItemCount(): Int {
        return listStudySet.size
    }
}