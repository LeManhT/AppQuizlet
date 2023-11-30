package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.SolutionItemModel
import com.google.android.material.chip.Chip

class SolutionItemAdapter(private val listSolutionItem: List<SolutionItemModel>) :
    RecyclerView.Adapter<SolutionItemAdapter.SolutionItemHolder>() {
    class SolutionItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SolutionItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_solution_item, parent, false)
        return SolutionItemHolder(view)
    }

    override fun onBindViewHolder(holder: SolutionItemHolder, position: Int) {
        holder.itemView.apply {
            val imgSolutionAvatar = findViewById<ImageView>(R.id.imgSolutionAvatar)
            val txtSolutionTitle = findViewById<TextView>(R.id.txtSolutionTitle)
            val chipSolution = findViewById<Chip>(R.id.chip)

            imgSolutionAvatar.setImageResource(listSolutionItem[position].avatar)
            txtSolutionTitle.text = listSolutionItem[position].title
            chipSolution.setText(listSolutionItem[position].countView.toString())
        }
    }

    override fun getItemCount(): Int {
        return listSolutionItem.size
    }
}