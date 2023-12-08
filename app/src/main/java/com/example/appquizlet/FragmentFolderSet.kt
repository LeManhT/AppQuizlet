package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.databinding.FragmentFolderSetBinding
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.model.FolderModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper

class FragmentFolderSet : Fragment() {
    private lateinit var binding: FragmentFolderSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFolderSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listFolderItems = mutableListOf<FolderModel>()


        val adapterFolder =
            RVFolderItemAdapter(requireContext(), listFolderItems, object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
                    folderItem.isSelected = folderItem.isSelected?.not() ?: true
                }
            })
        // Thêm một Observer cho userData
        val userData = UserM.getUserData()
        userData.observe(viewLifecycleOwner, Observer { userResponse ->
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
        })
        // Access the RecyclerView through the binding
        val rvFolder = binding.rvFolderFragment
        rvFolder.layoutManager = LinearLayoutManager(context)
        rvFolder.adapter = adapterFolder


    }

    companion object {

    }
}