package com.example.quizletappandroidv1.adapter.admin

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.UserResponse

class ManageUserAdapter(
    private val onUserAdminClick: IUserAdminClick
) : PagingDataAdapter<UserResponse, ManageUserAdapter.UserViewHolder>(USER_COMPARATOR) {

    interface IUserAdminClick {
        fun handleEditClick(user: UserResponse)
        fun handleDeleteClick(user: UserResponse)
        fun handleSuspendClick(user: UserResponse)
        fun handleUnsuspendClick(user: UserResponse)
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.txtUserName)
        val userEmail: TextView = itemView.findViewById(R.id.txtUserEmail)
        val btnEdit: Button = itemView.findViewById(R.id.btnEdit)
        val btnDelete: Button = itemView.findViewById(R.id.btnDelete)
        val btnSuspend: Button = itemView.findViewById(R.id.btnSuspend)
        val btnUnsuspend: Button = itemView.findViewById(R.id.btnDeSuspend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user_admin, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position) ?: return

        holder.userName.text = user.userName
        holder.userEmail.text = user.email

        if (user.isSuspend) {
            holder.btnSuspend.visibility = View.GONE
            holder.btnUnsuspend.visibility = View.VISIBLE
        } else {
            holder.btnSuspend.visibility = View.VISIBLE
            holder.btnUnsuspend.visibility = View.GONE
        }

        holder.btnUnsuspend.setOnClickListener {
            onUserAdminClick.handleUnsuspendClick(user)
        }

        holder.btnEdit.setOnClickListener {
            onUserAdminClick.handleEditClick(user)
        }

        holder.btnDelete.setOnClickListener {
            onUserAdminClick.handleDeleteClick(user)
        }

        holder.btnSuspend.setOnClickListener {
            onUserAdminClick.handleSuspendClick(user)
        }
    }

    companion object {
        private val USER_COMPARATOR = object : DiffUtil.ItemCallback<UserResponse>() {
            override fun areItemsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserResponse, newItem: UserResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}