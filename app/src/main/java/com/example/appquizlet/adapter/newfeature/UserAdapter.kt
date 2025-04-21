//package com.example.appquizlet.adapter.newfeature
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.ImageView
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.bumptech.glide.Glide
//import com.example.appquizlet.R
//
//class UserAdapter(
//    private val users: List<User>,
//    private val selectedUsers: MutableList<User>,
//    private val onUserSelected: (User) -> Unit
//) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {
//
//    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        private val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
//        private val tvName: TextView = itemView.findViewById(R.id.tvName)
//        private val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
//
//        fun bind(user: User) {
//            tvName.text = user.name
//            Glide.with(itemView.context).load(user.avatarUrl).into(imgAvatar)
//            checkbox.isChecked = selectedUsers.contains(user)
//
//            itemView.setOnClickListener {
//                if (selectedUsers.contains(user)) {
//                    selectedUsers.remove(user)
//                } else {
//                    selectedUsers.add(user)
//                }
//                checkbox.isChecked = selectedUsers.contains(user)
//                onUserSelected(user)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
//        return UserViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        holder.bind(users[position])
//    }
//
//    override fun getItemCount(): Int = users.size
//}
