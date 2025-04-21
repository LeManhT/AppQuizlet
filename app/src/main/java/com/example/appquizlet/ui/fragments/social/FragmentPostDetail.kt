package com.example.appquizlet.ui.fragments.social

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.newfeature.CommentAdapter
import com.example.appquizlet.databinding.FragmentPostDetailBinding
import com.example.appquizlet.model.newfeature.Comment
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.CommentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentPostDetail : Fragment() {
    private lateinit var binding: FragmentPostDetailBinding
    private lateinit var commentAdapter: CommentAdapter
    private val commentViewModel by viewModels<CommentViewModel>()
    private var postId: String = ""
    private var replyToCommentId: String? = null
    private var replyToUsername: String? = null
    private val navArgs by navArgs<FragmentPostDetailArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postId = navArgs.post.id
        setupRecyclerView()
        setupCommentInput()
        observeViewModel()

        commentViewModel.loadRootComments(postId)
    }

    private fun setupRecyclerView() {
        commentAdapter = CommentAdapter { comment ->
            setReplyMode(comment.id, comment.username)
        }

        binding.commentsRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = commentAdapter
        }
    }

    private fun setupCommentInput() {
        binding.sendButton.setOnClickListener {
            val content = binding.commentInput.text.toString().trim()
            if (content.isNotEmpty()) {
                val userId = Helper.getDataUserId(requireContext())
                if (userId.isNotEmpty()) {
                    commentViewModel.addComment(
                        postId = postId,
                        authorId = userId,
                        content = content,
                        parentCommentId = replyToCommentId
                    )
                    binding.commentInput.text.clear()

                    resetReplyMode()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "You need to log in to comment",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        binding.cancelReply.setOnClickListener {
            resetReplyMode()
        }
    }

    private fun setReplyMode(commentId: String, username: String) {
        replyToCommentId = commentId
        replyToUsername = username

        binding.replyingToLayout.visibility = View.VISIBLE
        binding.replyingToText.text = "Replying to $username"
        binding.commentInput.hint = "Write a reply..."
        binding.commentInput.requestFocus()
    }

    private fun resetReplyMode() {
        replyToCommentId = null
        replyToUsername = null

        binding.replyingToLayout.visibility = View.GONE
        binding.commentInput.hint = "Write a comment..."
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            commentViewModel.rootComments.collectLatest { comments ->
                commentAdapter.updateComments(comments)
                binding.noCommentsText.visibility =
                    if (comments.isEmpty()) View.VISIBLE else View.GONE
            }
        }

        binding.commentsRecyclerView.addOnItemClickListener { position ->
            val comment = commentAdapter.getCommentAt(position)
            commentViewModel.toggleCommentExpansion(comment.id)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            commentViewModel.isLoading.collectLatest { isLoading ->
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            commentViewModel.error.collectLatest { error ->
                error?.let {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun RecyclerView.addOnItemClickListener(onClickListener: (position: Int) -> Unit) {
        this.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener(holder.adapterPosition)
                }
            }

            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }
        })
    }

    private fun showReplyDialog(comment: Comment) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("Reply to Comment")
        val input = EditText(requireContext())
        dialog.setView(input)

        dialog.setPositiveButton("Reply") { _, _ ->
            val replyContent = input.text.toString()
            if (replyContent.isNotEmpty()) {
                commentViewModel.addComment(postId, "currentUserId", replyContent, comment.id)
            }
        }

        dialog.setNegativeButton("Cancel", null)
        dialog.show()
    }
}