package com.example.appquizlet

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.databinding.FragmentFoldersBinding
import com.example.appquizlet.databinding.LayoutFoldersItemBinding

class RVFolderItemAdapter(
    var listFolderItem: List<FolderItemData>,
    val onFolderItemClick: RVFolderItem
) :
    RecyclerView.Adapter<RVFolderItemAdapter.FolderItemHolder>() {
    class FolderItemHolder(var binding: LayoutFoldersItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderItemHolder {
        val view =
            LayoutFoldersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FolderItemHolder(view)
    }

    override fun onBindViewHolder(holder: FolderItemHolder, position: Int) {
        holder.itemView.apply {
            var txtTitle = findViewById<TextView>(R.id.txtFolderItemTitle)
            var txtUsername = findViewById<TextView>(R.id.txtFolderItemUsername)
            var imgAvatar = findViewById<ImageView>(R.id.imgFolderItemAvatar)
            txtTitle.setText(listFolderItem[position].title)
            txtUsername.setText(listFolderItem[position].username)
            imgAvatar.setImageResource(listFolderItem[position].avatar)

            holder.itemView.setOnClickListener {
                onFolderItemClick.handleClickFolderItem(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return listFolderItem.size
    }
}