package com.example.appquizlet.admin

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.PagingData
import androidx.paging.flatMap
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.admin.ManageStudySetAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentManageStudySetBinding
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserResponse
import com.example.appquizlet.viewmodel.admin.AdminViewModel
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
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageStudySetBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        studySetAdapter = ManageStudySetAdapter(object : ManageStudySetAdapter.IAdminStudySetClick {
            override fun handleDeleteClick(studySet: StudySetModel) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.warning))
                    .setMessage(resources.getString(R.string.confirm_delete_set))
                    .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        lifecycleScope.launch {
                            showLoading(resources.getString(R.string.deleteFolderLoading))
                            try {
                                val result =
                                    apiService.deleteStudySet(studySet.idOwner, studySet.id)
                                if (result.isSuccessful) {
                                    result.body().let {
                                        if (it != null) {
                                            CustomToast(requireContext()).makeText(
                                                requireContext(),
                                                resources.getString(R.string.deleteSetSuccessful),
                                                CustomToast.LONG,
                                                CustomToast.SUCCESS
                                            ).show()
                                            val position =
                                                studySetAdapter.snapshot().indexOf(studySet)
                                            if (position != -1) {
                                                studySetAdapter.notifyItemRemoved(position)
                                            }
                                        }
                                    }
                                } else {
                                    CustomToast(requireContext()).makeText(
                                        requireContext(),
                                        resources.getString(R.string.deleteSetErr),
                                        CustomToast.LONG,
                                        CustomToast.ERROR
                                    ).show()

                                }
                            } catch (e: Exception) {
                                CustomToast(requireContext()).makeText(
                                    requireContext(),
                                    e.message.toString(),
                                    CustomToast.LONG,
                                    CustomToast.ERROR
                                ).show()
                            } finally {
                                progressDialog.dismiss()
                            }
                        }
                    }
                    .show()
            }

            override fun handleEditClick(studySet: StudySetModel) {
                val action = FragmentManageStudySetDirections.actionManageStudySetToFragmentEditSet(
                    studySet
                )
                findNavController().navigate(action)
            }
        })


//        adminViewModel.getListUserAdmin(0, 10)
        lifecycleScope.launch {
            adminViewModel.pagingUsers.collectLatest { pagingData: PagingData<UserResponse> ->
                val studySets = pagingData.flatMap { user ->
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

    override fun onResume() {
        super.onResume()
        setFragmentResultListener("editStudySetRequestKey") { requestKey, bundle ->
            val isUpdated = bundle.getBoolean("isUpdated", false)
            if (isUpdated) {
                studySetAdapter.refresh()
            }
        }

    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }


}