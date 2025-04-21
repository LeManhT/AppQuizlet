package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.newfeature.FriendRequest

class FriendRequestAdapter(
    private val onAccept: (FriendRequest) -> Unit,
    private val onReject: (FriendRequest) -> Unit
) : RecyclerView.Adapter<FriendRequestAdapter.FriendRequestViewHolder>() {

    private var requests: List<FriendRequest> = mutableListOf()
    inner class FriendRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.iv_avatar)
        private val name: TextView = itemView.findViewById(R.id.tv_name)
        private val mutualFriends: TextView = itemView.findViewById(R.id.tv_mutual_friends)
        private val acceptButton: Button = itemView.findViewById(R.id.btn_accept)
        private val rejectButton: Button = itemView.findViewById(R.id.btn_reject)

        fun bind(request: FriendRequest) {
            name.text = request.senderName
            mutualFriends.text = "${request.mutualFriends} bạn chung • ${request.createdAt}"

            acceptButton.setOnClickListener { onAccept(request) }
            rejectButton.setOnClickListener { onReject(request) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend_request, parent, false)
        return FriendRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendRequestViewHolder, position: Int) {
        holder.bind(requests[position])
    }

    override fun getItemCount(): Int = requests.size

    fun updateData(newRequests: List<FriendRequest>) {
        requests = newRequests
        notifyDataSetChanged()
    }
}
