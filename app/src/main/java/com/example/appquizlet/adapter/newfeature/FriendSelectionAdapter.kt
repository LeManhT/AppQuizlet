package com.example.appquizlet.adapter.newfeature
import com.example.appquizlet.model.UserResponse
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ItemFriendSelectionBinding

class FriendSelectionAdapter(
    private var friends: List<UserResponse>,
    private val onFriendSelected: (UserResponse) -> Unit
) : RecyclerView.Adapter<FriendSelectionAdapter.FriendViewHolder>() {

    private val selectedFriends = mutableSetOf<UserResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemFriendSelectionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        holder.bind(friend)
    }

    override fun getItemCount(): Int = friends.size

    fun updateFriends(newFriends: List<UserResponse>) {
        friends = newFriends
        notifyDataSetChanged()
    }

    fun getSelectedFriends(): List<UserResponse> {
        return selectedFriends.toList()
    }

    inner class FriendViewHolder(private val binding: ItemFriendSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(friend: UserResponse) {
            binding.textUsername.text = friend.userName ?: "Unknown User"

            // Load user avatar if available
            friend.avatar?.let { avatarUrl ->
                Glide.with(binding.root.context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.user)
                    .circleCrop()
                    .into(binding.imgUserAvatar)
            }

            binding.checkboxSelect.isChecked = selectedFriends.contains(friend)

            binding.root.setOnClickListener {
                binding.checkboxSelect.isChecked = !binding.checkboxSelect.isChecked
                if (binding.checkboxSelect.isChecked) {
                    selectedFriends.add(friend)
                } else {
                    selectedFriends.remove(friend)
                }
                onFriendSelected(friend)
            }

            binding.checkboxSelect.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedFriends.add(friend)
                } else {
                    selectedFriends.remove(friend)
                }
            }
        }
    }
}