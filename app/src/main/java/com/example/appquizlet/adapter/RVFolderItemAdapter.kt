package com.example.appquizlet.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.interfaceFolder.ItemTouchHelperAdapter
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.model.FolderModel
import java.util.Collections


class RVFolderItemAdapter(
    private var listFolderItem: List<FolderModel>,
    private val onFolderItemClick: RVFolderItem
) :
    RecyclerView.Adapter<RVFolderItemAdapter.FolderItemHolder>(), ItemTouchHelperAdapter {
    class FolderItemHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView)

//    {
//        val txtTitle: TextView = itemView.findViewById(R.id.txtFolderItemTitle)
//        val txtUsername: TextView = itemView.findViewById(R.id.txtFolderItemUsername)
//        val imgAvatar: ImageView = itemView.findViewById(R.id.imgFolderItemAvatar)
//    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(listFolderItem, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onItemDismiss(position: Int) {
        notifyItemRemoved(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderItemHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_folders_item, parent, false)
        return FolderItemHolder(view)

    }

    override fun onBindViewHolder(holder: FolderItemHolder, position: Int) {
        holder.itemView.apply {
            val txtTitle = findViewById<TextView>(R.id.txtFolderItemTitle)
            val txtUsername = findViewById<TextView>(R.id.txtFolderItemUsername)
//            val imgAvatar = findViewById<ImageView>(R.id.imgFolderItemAvatar)
            txtTitle.text = listFolderItem[position].name
            txtUsername.text = listFolderItem[position].id
//            imgAvatar.setImageResource(listFolderItem[position].avatar)
            holder.itemView.setOnClickListener {
                onFolderItemClick.handleClickFolderItem(position)
            }


        }
    }

    override fun getItemCount(): Int {
        return listFolderItem.size
    }
}