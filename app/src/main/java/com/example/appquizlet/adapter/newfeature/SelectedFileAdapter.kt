package com.example.appquizlet.adapter.newfeature

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R

class SelectedFileAdapter(private val fileList: List<Uri>) :
    RecyclerView.Adapter<SelectedFileAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val fileName: TextView = view.findViewById(R.id.fileName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_selected_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val uri = fileList[position]
        holder.fileName.text = uri.lastPathSegment
    }

    override fun getItemCount(): Int = fileList.size
}
