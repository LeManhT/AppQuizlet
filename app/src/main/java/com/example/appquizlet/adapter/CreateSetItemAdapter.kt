package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.CreateSetModel


class CreateSetItemAdapter(private val listSet: List<CreateSetModel>) :
    RecyclerView.Adapter<CreateSetItemAdapter.CreateSetItemHolder>() {

    class CreateSetItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateSetItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_flashcard, parent, false)
        return CreateSetItemHolder(view)
    }

    override fun onBindViewHolder(holder: CreateSetItemHolder, position: Int) {
        holder.itemView.apply {
            val txtTerm = findViewById<EditText>(R.id.edtTerm)
            val txtDefinition = findViewById<EditText>(R.id.edtDefinition)
        }

    }

    override fun getItemCount(): Int {
        return listSet.size
    }
}