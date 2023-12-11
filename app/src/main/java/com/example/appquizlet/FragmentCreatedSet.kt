package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentCreatedSetBinding
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FragmentCreatedSet : Fragment(),RvStudySetItemAdapter.onClickSetItem {
    private lateinit var binding: FragmentCreatedSetBinding
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog
    private val listSetSelected: MutableList<StudySetModel> = mutableListOf()
    private lateinit var adapterStudySet : RvStudySetItemAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCreatedSetBinding.inflate(inflater, container, false)
        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listStudySet = mutableListOf<StudySetModel>()

        adapterStudySet =
            RvStudySetItemAdapter(requireContext(), listStudySet, object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    setItem.isSelected = setItem.isSelected?.not() ?: true
                    val selectedItems = listStudySet.filter { it.isSelected == true }
                    if (selectedItems.isNotEmpty()) {
                        listSetSelected.addAll(selectedItems)
                    } else {
                        Log.d("Selected Items", "No items selected")
                    }
                }
            }, true, true)

        val userDataStudySet = UserM.getUserData()
        userDataStudySet.observe(viewLifecycleOwner) {
            listStudySet.clear()
            listStudySet.addAll(Helper.getAllStudySets(it))
            if (listStudySet.isEmpty()) {
                Helper.replaceWithNoDataFragment(
                    requireFragmentManager(),
                    R.id.studySetItemContainer
                )
            }

            // Thông báo cho adapter rằng dữ liệu đã thay đổi để cập nhật giao diện người dùng
            adapterStudySet.notifyDataSetChanged()
        }


        val rvStudySet = binding.rvStudySet
        rvStudySet.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rvStudySet.adapter = adapterStudySet
    }

    companion object {
        const val Tag = "fragmentCreateSet"
    }

    override fun handleClickDelete(setId : String) {

    }

    fun insertSetToFolder(
        folderId: String
    ) {
        lifecycleScope.launch {
            try {
                showLoading("Add set to folder processing ...")
                val body: ArrayList<String> = ArrayList(listSetSelected.map { it.id })
                val result = apiService.addSetToFolder(
                    Helper.getDataUserId(requireContext()),
                    folderId,
                    body
                )
                if (result.isSuccessful) {
                    result.body()?.let {
                        requireContext().let { it1 ->
                            CustomToast(it1).makeText(
                                requireContext(),
                                resources.getString(R.string.update_study_set_success),
                                CustomToast.LONG,
                                CustomToast.SUCCESS
                            ).show()
                            UserM.setUserData(it)
                            val intent =
                                Intent(requireContext(), MainActivity_Logged_In::class.java)
                            intent.putExtra(
                                "selectedFragment",
                                "Home"
                            ) // "YourFragmentTag" là tag của Fragment cần hiển thị
                            startActivity(intent)

                        }
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        requireContext()?.let { it1 ->
                            CustomToast(it1).makeText(
                                requireContext(),
                                it,
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                        Log.d("err", it)
                    }
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

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(requireContext(), null, msg)
    }

}