package com.example.appquizlet.adapter.newfeature

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.R
import com.example.appquizlet.model.newfeature.GroupMember

class GroupMemberAdapter(
    private var members: List<GroupMember>,
    private val currentUserId: String,
    private val onRemoveMember: (String) -> Unit,
    private val onUpdateRole: (String, String) -> Unit
) : RecyclerView.Adapter<GroupMemberAdapter.MemberViewHolder>() {

    class MemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.textMemberName)
        val userRole: TextView = view.findViewById(R.id.textMemberRole)
        val btnOptions: ImageView = view.findViewById(R.id.btnMemberOptions)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_group_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]

        holder.userName.text = member.userName ?: "User-${member.userId}"
        holder.userRole.text = member.role

        // Only show options for admin users and not for current user
        if (isAdmin() && member.userId != currentUserId) {
            holder.btnOptions.visibility = View.VISIBLE
            holder.btnOptions.setOnClickListener {
                showMemberOptions(it, member)
            }
        } else {
            holder.btnOptions.visibility = View.GONE
        }
    }

    private fun isAdmin(): Boolean {
        return members.find { it.userId == currentUserId }?.role == "admin"
    }

    private fun showMemberOptions(view: View, member: GroupMember) {
        val popup = PopupMenu(view.context, view)
        popup.menuInflater.inflate(R.menu.member_options_menu, popup.menu)

        // If member is already admin, hide "Make Admin" option
        if (member.role == "admin") {
            popup.menu.findItem(R.id.action_make_admin).isVisible = false
        }

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_remove -> {
                    onRemoveMember(member.userId)
                    true
                }
                R.id.action_make_admin -> {
                    onUpdateRole(member.userId, "admin")
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    override fun getItemCount() = members.size

    fun updateMembers(newMembers: List<GroupMember>) {
        members = newMembers
        notifyDataSetChanged()
    }
}