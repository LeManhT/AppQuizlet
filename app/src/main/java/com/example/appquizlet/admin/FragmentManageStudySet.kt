package com.example.appquizlet.admin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.paging.flatMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.databinding.FragmentManageStudySetBinding
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.viewmodel.admin.AdminViewModel
import com.example.appquizlet.adapter.admin.ManageStudySetAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class FragmentManageStudySet : Fragment() {
    private lateinit var binding: FragmentManageStudySetBinding
    private val adminViewModel: AdminViewModel by activityViewModels()
    private lateinit var studySetAdapter: ManageStudySetAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageStudySetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        studySetAdapter = ManageStudySetAdapter(object : ManageStudySetAdapter.IAdminStudySetClick {
            override fun handleDeleteClick(studySet: StudySetModel) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_delete_set))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
//                        documentViewModel.deleteStudySet(studySet.id,)
                    }
                    .show()
            }

            override fun handleEditClick(studySet: StudySetModel) {

            }
        })

//        adminViewModel.getListUserAdmin(0, 10)
        lifecycleScope.launch {
            Log.d("StudySet", Gson().toJson("Vao paging user"))
            adminViewModel.pagingUsers.collectLatest { pagingData: PagingData<UserResponse> ->
                val studySets = pagingData.flatMap { user ->
                    Log.d("StudySet", "User: ${Gson().toJson(user)}") // Log user details
                    user.documents.studySets
                }
                studySetAdapter.submitData(studySets)
            }
        }


        binding.recyclerViewStudySets.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewStudySets.adapter = studySetAdapter

        adminViewModel.allUser.observe(viewLifecycleOwner) { result ->
            run {
                result.onSuccess {
                    Log.d("StudySet", Gson().toJson(it))
                    val studySets = it.flatMap { user -> user.documents.studySets }
                    Log.d("StudySet", Gson().toJson(studySets))
                    if (studySets.isEmpty()) {
                        binding.recyclerViewStudySets.visibility = View.GONE
                        binding.layoutNoData.visibility = View.VISIBLE
                    } else {
                        binding.recyclerViewStudySets.visibility = View.VISIBLE
                        binding.layoutNoData.visibility = View.GONE
                    }
//                    studySetAdapter.updateData(studySets)
                    studySetAdapter.submitData(
                        lifecycle,
                        PagingData.from(it.flatMap { user -> user.documents.studySets })
                    )
                }.onFailure {
                    Timber.log(1, it.message.toString())
                }
            }
        }

    }

}