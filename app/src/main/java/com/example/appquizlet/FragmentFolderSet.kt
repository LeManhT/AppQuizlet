package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentFolderSetBinding
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.model.FolderModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FragmentFolderSet : Fragment() {
    private lateinit var binding: FragmentFolderSetBinding
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog
    private val listFolderSelected: MutableList<FolderModel> = mutableListOf()
    private val listSetId: MutableSet<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFolderSetBinding.inflate(inflater, container, false)
        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listFolderItems = mutableListOf<FolderModel>()


        val adapterFolder =
            RVFolderItemAdapter(requireContext(), listFolderItems, object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
                    folderItem.isSelected = folderItem.isSelected?.not() ?: true
                    val selectedFolder = listFolderItems.filter { it.isSelected == true }
                    if (selectedFolder.isNotEmpty()) {
                        listFolderSelected.addAll(selectedFolder)
                        listFolderSelected.map {
                            it.studySets.forEach { it1 ->
                                listSetId.add(it1.id)
                            }
                        }
                        Log.d("ids", Gson().toJson(listSetId))
                    }
                }
            })

        // Thêm một Observer cho userData
        val userData = UserM.getUserData()
        userData.observe(viewLifecycleOwner) { userResponse ->
            // Khi dữ liệu thay đổi, cập nhật danh sách listFolderItems
            // Lưu ý: Trong trường hợp thực tế, bạn có thể cần xử lý dữ liệu từ userResponse một cách thích hợp.
            // Ở đây, ta giả sử userResponse có một thuộc tính là danh sách các FolderItemData.
            listFolderItems.clear()
            listFolderItems.addAll(userResponse.documents.folders)
            if (listFolderItems.isEmpty()) {
                Helper.replaceWithNoDataFragment(
                    requireFragmentManager(),
                    R.id.fragmentFolderContainer
                )
            }

            // Thông báo cho adapter rằng dữ liệu đã thay đổi để cập nhật giao diện người dùng
            adapterFolder.notifyDataSetChanged()
        }
        // Access the RecyclerView through the binding
        val rvFolder = binding.rvFolderFragment
        rvFolder.layoutManager = LinearLayoutManager(context)
        rvFolder.adapter = adapterFolder


    }

    companion object {

    }

    fun insertFolderToFolder(
        folderId: String
    ) {
        lifecycleScope.launch {
            try {
                showLoading("Add sets to folder processing ...")
                val body: MutableSet<String> = listSetId
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
                        requireContext().let { it1 ->
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