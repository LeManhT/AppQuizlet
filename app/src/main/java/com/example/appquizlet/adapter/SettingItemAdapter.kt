package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R

class SettingItemAdapter(private val list : List<Int>) : RecyclerView.Adapter<SettingItemAdapter.ItemViewHolder>() {
    class ItemViewHolder (itemView : View) : RecyclerView.ViewHolder (itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_setting_item, parent,false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.itemView.apply {
            var title = findViewById<TextView>(R.id.txtSettingTitle)
            var desc = findViewById<TextView>(R.id.txtSettingDesc)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}