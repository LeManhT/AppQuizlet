package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.model.UserResponse

class SuggestedFriendsAdapter(
    private var users: List<UserResponse>,
    private val onSendRequest: (UserResponse) -> Unit,
    private val onCancelRequest: (UserResponse) -> Unit
) : RecyclerView.Adapter<SuggestedFriendsAdapter.ViewHolder>() {
    private val sentRequests = mutableSetOf<String>()
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.iv_suggest_fr_avatar)
        val name: TextView = view.findViewById(R.id.tv_name)
        val btnAdd: Button = view.findViewById(R.id.btn_add_friend)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_suggested_friend, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.name.text = user.userName
        Glide.with(holder.avatar.context)
            .load(user.avatar)
            .placeholder(R.drawable.user)
            .into(holder.avatar)

//        holder.btnAdd.setOnClickListener {
//            onSendRequest(user)
//        }
        if (sentRequests.contains(user.id)) {
            holder.btnAdd.text = "Đã gửi lời mời"
            holder.btnAdd.setOnClickListener {
                onCancelRequest(user) // Gọi API thu hồi
                sentRequests.remove(user.id) // Xóa khỏi danh sách đã gửi
                notifyItemChanged(position) // Cập nhật UI
            }
        } else {
            holder.btnAdd.text = "Thêm bạn bè"
            holder.btnAdd.setOnClickListener {
                onSendRequest(user) // Gửi lời mời kết bạn
                sentRequests.add(user.id) // Đánh dấu đã gửi
                notifyItemChanged(position) // Cập nhật UI
            }
        }
    }

    override fun getItemCount(): Int = users.size

    fun updateData(newUsers: List<UserResponse>) {
        users = newUsers
        notifyDataSetChanged()
    }
}
