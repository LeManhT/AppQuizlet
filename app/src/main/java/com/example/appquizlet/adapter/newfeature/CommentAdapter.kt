package com.example.appquizlet.adapter.newfeature

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ItemCommentBinding
import com.example.appquizlet.model.newfeature.Comment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentAdapter(
    private val onReplyClick: (Comment) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val allComments = mutableListOf<CommentWithDepth>()

    data class CommentWithDepth(
        val comment: Comment,
        val depth: Int,
        val isReply: Boolean
    )

    class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val commentWithDepth = allComments[position]
        val comment = commentWithDepth.comment
        val depth = commentWithDepth.depth
        val binding = holder.binding

        // Tạo margin trái dựa trên độ sâu của comment
        val layoutParams = binding.root.layoutParams as ViewGroup.MarginLayoutParams
        val marginInDp = (depth * 20).dpToPx(binding.root.context)
        layoutParams.marginStart = marginInDp
        binding.root.layoutParams = layoutParams

        // Thiết lập background khác cho replies
        if (commentWithDepth.isReply) {
            binding.root.setBackgroundResource(R.drawable.bg_reply_comment) // Tạo drawable này trong res/drawable
        } else {
            binding.root.setBackgroundResource(R.drawable.bg_comment) // Tạo drawable này trong res/drawable
        }

        // Thiết lập dữ liệu comment
        binding.userName.text = comment.username
        binding.commentText.text = comment.content
        binding.commentTime.text = formatTime(comment.timestamp)

        Glide.with(binding.root.context)
            .load(comment.userAvatar)
            .placeholder(R.drawable.user)
            .into(binding.userAvatar)

        binding.replyButton.setOnClickListener {
            onReplyClick(comment)
        }

        // Hiển thị/ẩn nút "View replies"
        if (comment.replies.isNotEmpty()) {
            binding.loadMoreReplies.visibility = View.VISIBLE
            binding.loadMoreReplies.text = if (comment.isExpanded) {
                "Hide replies (${comment.replies.size})"
            } else {
                "View replies (${comment.replies.size})"
            }

            binding.loadMoreReplies.setOnClickListener {
                val newIsExpanded = !comment.isExpanded

                // Cập nhật trạng thái expanded
                comment.isExpanded = newIsExpanded

                // Tạo lại danh sách
                updateCommentsWithExpanded(getAllRootComments())
            }
        } else {
            binding.loadMoreReplies.visibility = View.GONE
        }

        // Không cần container replies vì chúng ta đang sử dụng cấu trúc phẳng
        binding.repliesContainer.visibility = View.GONE
    }

    override fun getItemCount(): Int = allComments.size

    // Thêm phương thức này để truy cập comment tại vị trí cụ thể
    fun getCommentAt(position: Int): Comment {
        return allComments[position].comment
    }

    fun updateComments(newComments: List<Comment>) {
        Log.d("CommentAdapter", "Updating with ${newComments.size} root comments")
        newComments.forEach { comment ->
            Log.d("CommentAdapter", "Comment ${comment.id}: ${comment.content}, has ${comment.replies.size} replies, expanded: ${comment.isExpanded}")
        }

        updateCommentsWithExpanded(newComments)
    }

    private fun getAllRootComments(): List<Comment> {
        // Lấy tất cả root comments từ danh sách phẳng
        return allComments
            .filter { it.depth == 0 }
            .map { it.comment }
    }

    private fun updateCommentsWithExpanded(newComments: List<Comment>) {
        allComments.clear()
        processComments(newComments, 0, false)
        notifyDataSetChanged()
    }

    private fun processComments(comments: List<Comment>, depth: Int, isReply: Boolean) {
        comments.forEach { comment ->
            allComments.add(CommentWithDepth(comment, depth, isReply))

            // Xử lý replies nếu comment được mở rộng
            if (comment.isExpanded && comment.replies.isNotEmpty()) {
                processComments(comment.replies, depth + 1, true)
            }
        }
    }

    private fun formatTime(timestamp: Long): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun Int.dpToPx(context: android.content.Context): Int {
        val density = context.resources.displayMetrics.density
        return (this * density).toInt()
    }
}