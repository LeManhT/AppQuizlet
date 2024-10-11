package com.example.appquizlet.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.databinding.FragmentManageUserBinding
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.viewmodel.admin.AdminViewModel
import com.example.quizletappandroidv1.adapter.admin.ManageUserAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FragmentManageUser : Fragment() {
    private lateinit var binding: FragmentManageUserBinding
    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var listUserAdapter: ManageUserAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageUserBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listUserAdapter = ManageUserAdapter(object : ManageUserAdapter.IUserAdminClick {
            override fun handleDeleteClick(user: UserResponse) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_suspend_user))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        showLoading(resources.getString(R.string.delete_user_loading))
                        adminViewModel.deleteUser(user.id)
                    }
                    .show()
            }

            override fun handleEditClick(user: UserResponse) {
                val action = FragmentManageUserDirections.actionManageUserToFragmentEditUser(user)
                findNavController().navigate(action)
            }

            override fun handleSuspendClick(user: UserResponse) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_suspend_user))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        adminViewModel.suspendUser(user.id, true).also {
                            user.isSuspend =
                                true
                            listUserAdapter.notifyDataSetChanged()
                        }
                    }
                    .show()
            }

            override fun handleUnsuspendClick(user: UserResponse) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_unsuspend_user))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        adminViewModel.suspendUser(user.id, false).also {
                            user.isSuspend = false
                            listUserAdapter.notifyDataSetChanged()
                        }
                    }
                    .show()
            }
        })

        lifecycleScope.launch {
            adminViewModel.pagingUsers.collectLatest { pagingData: PagingData<UserResponse> ->
                listUserAdapter.submitData(pagingData)
            }
        }

        adminViewModel.deleteResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                Log.d("Delete",it.toString())
                if (it) {
                    lifecycleScope.launch {
                        adminViewModel.pagingUsers.collectLatest { pagingData: PagingData<UserResponse> ->
                            listUserAdapter.submitData(pagingData)
                        }
                    }
                }
                progressDialog.dismiss()
            }.onFailure {
                Timber.log(1, it.message.toString())
            }
        }

        binding.recyclerViewUsers.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewUsers.adapter = listUserAdapter

    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(context, null, msg)
    }


}