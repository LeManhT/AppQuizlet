package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.newfeature.FriendSelectionAdapter
import com.example.appquizlet.databinding.FragmentCreateGroupChatBinding
import com.example.appquizlet.viewmodel.social.GroupChatViewModel
import com.example.appquizlet.viewmodel.social.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FragmentCreateGroup : Fragment() {
    private lateinit var binding: FragmentCreateGroupChatBinding
    private val groupViewModel: GroupChatViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var adapter: FriendSelectionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateGroupChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupRecyclerView()
        setupObservers()
        loadFriends()
    }

    private fun setupUI() {
        binding.btnCreateGroup.setOnClickListener {
            createGroup()
        }

        binding.iconBack.setOnClickListener {
            findNavController().navigateUp()
        }

        // Mặc định disable nút Create Group
        binding.btnCreateGroup.isEnabled = false
    }

    private fun setupRecyclerView() {
        adapter = FriendSelectionAdapter(emptyList()) {
            // Selection callback - kiểm tra số lượng người được chọn
            updateCreateButtonState()
        }
        binding.rvFriends.adapter = adapter
        binding.rvFriends.layoutManager = LinearLayoutManager(requireContext())
    }

    // Hàm mới: Cập nhật trạng thái nút Create Group dựa trên số lượng người được chọn
    private fun updateCreateButtonState() {
        val selectedFriends = adapter.getSelectedFriends()
        // Chỉ cho phép tạo nhóm khi có ít nhất 1 người bạn được chọn
        binding.btnCreateGroup.isEnabled = selectedFriends.isNotEmpty()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            userViewModel.friends.collectLatest { friends ->
                adapter.updateFriends(friends)
                updateCreateButtonState() // Cập nhật trạng thái nút sau khi cập nhật danh sách bạn bè
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            groupViewModel.groupLoadingState.collectLatest { state ->
                when (state) {
                    is GroupChatViewModel.GroupLoadingState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.btnCreateGroup.isEnabled = false
                    }
                    is GroupChatViewModel.GroupLoadingState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        groupViewModel.activeGroup.value?.let { conversation ->
                            val action = FragmentCreateGroupDirections
                                .actionFragmentCreateGroup2ToFragmentGroupChat(conversation)
                            findNavController().navigate(action)
                        }
                    }
                    is GroupChatViewModel.GroupLoadingState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        updateCreateButtonState() // Khôi phục trạng thái nút
                        Toast.makeText(requireContext(), "Error: ${state.message}", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        updateCreateButtonState() // Khôi phục trạng thái nút
                    }
                }
            }
        }
    }

    private fun loadFriends() {
        userViewModel.loadFriends(requireContext())
    }

    private fun createGroup() {
        val groupName = binding.etGroupName.text.toString().trim()
        val groupDescription = binding.etGroupDescription.text.toString().trim()
        val selectedFriends = adapter.getSelectedFriends()

        if (groupName.isEmpty()) {
            binding.etGroupName.error = "Group name is required"
            return
        }

        if (selectedFriends.isEmpty()) {
            Toast.makeText(requireContext(), "Please select at least one friend", Toast.LENGTH_SHORT).show()
            return
        }

        // Xác định loại nhóm dựa trên số người
        val groupType = if (selectedFriends.size == 1) "personal" else "group"

        // Tạo nhóm với type đã xác định
        groupViewModel.createGroup(requireContext(), groupName, groupDescription, groupType)

        // Thêm thành viên sau khi tạo nhóm
        viewLifecycleOwner.lifecycleScope.launch {
            groupViewModel.activeGroup.collectLatest { group ->
                group?.let { conversation ->
                    selectedFriends.forEach { friend ->
                        groupViewModel.addMemberToGroup(
                            conversation.conversationId,
                            friend.id
                        )
                    }
                }
            }
        }
    }
}