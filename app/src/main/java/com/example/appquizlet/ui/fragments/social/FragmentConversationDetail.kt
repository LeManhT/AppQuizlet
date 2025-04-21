package com.example.appquizlet.ui.fragments.social

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.newfeature.FriendSelectionAdapter
import com.example.appquizlet.adapter.newfeature.GroupMemberAdapter
import com.example.appquizlet.databinding.FragmentConversationDetailBinding
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.GroupChatViewModel
import com.example.appquizlet.viewmodel.social.UserViewModel
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentConversationDetail : Fragment() {
    private val viewModel: GroupChatViewModel by viewModels()
    private val navArgs by navArgs<FragmentConversationDetailArgs>()
    private var _binding: FragmentConversationDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var memberAdapter: GroupMemberAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConversationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUi()
        setupRecyclerView()
        setupListeners()
        setupObservers()

        // Initialize with conversation data
        val conversation = navArgs.conversation
        viewModel.initWithConversation(conversation)
        viewModel.loadGroupMembers(conversation.conversationId)
    }

    private fun setupUi() {
        val conversation = navArgs.conversation
        binding.profileName.text = conversation.name
        binding.memberCount.text = "${conversation.members.size} participants"
    }

    private fun setupRecyclerView() {
        memberAdapter = GroupMemberAdapter(
            members = emptyList(),
            currentUserId = Helper.getDataUserId(requireContext()),
            onRemoveMember = { memberId ->
                showRemoveMemberConfirmation(memberId)
            },
            onUpdateRole = { memberId, newRole ->
                viewModel.updateMemberRole(navArgs.conversation.conversationId, memberId, newRole)
            }
        )

        binding.recyclerViewMembers.apply {
            adapter = memberAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddMember.setOnClickListener {
            showAddMemberDialog()
        }

        binding.btnLeaveGroup.setOnClickListener {
            showLeaveGroupConfirmation()
        }
    }

    private fun setupObservers() {
        // Observe group members
        lifecycleScope.launch {
            viewModel.groupMembers.collectLatest { members ->
                Log.d("GroupProfile", "Members updated: ${members.size}")
                memberAdapter.updateMembers(members)
                binding.memberCount.text = "${members.size} participants"
            }
        }

        // Observe member operations
        lifecycleScope.launch {
            viewModel.memberOperationState.collectLatest { state ->
                when (state) {
                    is GroupChatViewModel.MemberOperationState.Loading -> {
                        // Could show loading indicator here
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is GroupChatViewModel.MemberOperationState.Success -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        // If user left the group, navigate back
                        if (state.message.contains("left")) {
                            findNavController().popBackStack(R.id.fragmentListConversation, false)
                        }
                    }
                    is GroupChatViewModel.MemberOperationState.Error -> {
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> { /* Idle state, do nothing */ }
                }
            }
        }
    }

//    private fun showAddMemberDialog() {
//        val dialogView = layoutInflater.inflate(R.layout.dialog_add_member, null)
//        val userIdInput = dialogView.findViewById<TextInputEditText>(R.id.input_user_id)
//
//        MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Add Member")
//            .setView(dialogView)
//            .setPositiveButton("Add") { _, _ ->
//                val userId = userIdInput.text.toString().trim()
//                if (userId.isNotEmpty()) {
//                    viewModel.addMemberToGroup(navArgs.conversation.conversationId, userId)
//                } else {
//                    Toast.makeText(requireContext(), "Please enter a valid user ID", Toast.LENGTH_SHORT).show()
//                }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }

    private fun showRemoveMemberConfirmation(memberId: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Remove Member")
            .setMessage("Are you sure you want to remove this member from the group?")
            .setPositiveButton("Remove") { _, _ ->
                viewModel.removeMemberFromGroup(navArgs.conversation.conversationId, memberId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLeaveGroupConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Leave Group")
            .setMessage("Are you sure you want to leave this group?")
            .setPositiveButton("Leave") { _, _ ->
                viewModel.leaveGroup(requireContext(), navArgs.conversation.conversationId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showAddMemberDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_friends, null)
        val searchInput = dialogView.findViewById<TextInputEditText>(R.id.search_input)
        val recyclerViewFriends = dialogView.findViewById<RecyclerView>(R.id.recycler_view_friends)
        val tvNoFriends = dialogView.findViewById<TextView>(R.id.tv_no_friends)

        // Tạo instance của UserViewModel
        val userViewModel: UserViewModel by viewModels()

        // Tạo và thiết lập adapter cho danh sách bạn bè
        val friendSelectionAdapter = FriendSelectionAdapter(
            friends = emptyList(),
            onFriendSelected = { /* Được gọi khi bạn bè được chọn */ }
        )

        recyclerViewFriends.apply {
            adapter = friendSelectionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                userViewModel.searchFriends(s.toString())
            }
        })

        // Tạo dialog
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Thêm thành viên")
            .setView(dialogView)
            .setPositiveButton("Thêm đã chọn", null) // Sẽ thiết lập lại sau
            .setNegativeButton("Hủy", null)
            .create()

        // Hiển thị dialog và thiết lập nút positive
        dialog.show()

        // Thay thế listener cho nút positive
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val selectedFriends = friendSelectionAdapter.getSelectedFriends()
            if (selectedFriends.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một người bạn", Toast.LENGTH_SHORT).show()
            } else {
                // Lấy danh sách ID người dùng từ bạn bè đã chọn
                val selectedIds = selectedFriends.mapNotNull { it.id }

                // Thêm từng thành viên vào nhóm
                selectedIds.forEach { userId ->
                    viewModel.addMemberToGroup(navArgs.conversation.conversationId, userId)
                }

                dialog.dismiss()
                Toast.makeText(requireContext(), "Đang thêm ${selectedIds.size} thành viên vào nhóm...", Toast.LENGTH_SHORT).show()
            }
        }

        // Tải danh sách bạn bè
        userViewModel.loadFriends(requireContext())

        // Theo dõi danh sách bạn bè
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.friends.collectLatest { friends ->
                if (friends.isEmpty()) {
                    tvNoFriends.visibility = View.VISIBLE
                    recyclerViewFriends.visibility = View.GONE
                } else {
                    tvNoFriends.visibility = View.GONE
                    recyclerViewFriends.visibility = View.VISIBLE
                    friendSelectionAdapter.updateFriends(friends)
                }
            }
        }

        // Theo dõi trạng thái tải
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.loadingState.collectLatest { state ->
                when (state) {
                    is UserViewModel.LoadingState.Loading -> {
                        tvNoFriends.text = "Đang tải danh sách bạn bè..."
                        tvNoFriends.visibility = View.VISIBLE
                        recyclerViewFriends.visibility = View.GONE
                    }
                    is UserViewModel.LoadingState.Error -> {
                        tvNoFriends.text = "Lỗi: ${state.message}"
                        tvNoFriends.visibility = View.VISIBLE
                        recyclerViewFriends.visibility = View.GONE
                    }
                    else -> {} // Không làm gì trong các trạng thái khác
                }
            }
        }

        // Theo dõi trạng thái thao tác thành viên
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.memberOperationState.collectLatest { state ->
                when (state) {
                    is GroupChatViewModel.MemberOperationState.Success -> {
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is GroupChatViewModel.MemberOperationState.Error -> {
                        Toast.makeText(requireContext(), "Lỗi: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {} // Không làm gì trong các trạng thái khác
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}