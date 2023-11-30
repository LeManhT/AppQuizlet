package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.databinding.FragmentFoldersBinding
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.model.FolderModel
import com.example.appquizlet.model.UserM

class FoldersFragment : Fragment() {

    // Declare the binding property with late initialization
    private lateinit var binding: FragmentFoldersBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Initialize the binding
        binding = FragmentFoldersBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ...

        val listFolderItems = mutableListOf<FolderModel>()


        val adapterFolder = RVFolderItemAdapter(listFolderItems, object : RVFolderItem {
            override fun handleClickFolderItem(position: Int) {
                val i = Intent(context, FolderClickActivity::class.java)
                i.putExtra("idFolder", listFolderItems[position].id)
                startActivity(i)
            }
        })
        // Thêm một Observer cho userData
        val userData = UserM.getUserData()
        Log.d("value", userData.value.toString())
        userData.observe(viewLifecycleOwner, Observer { userResponse ->
            // Khi dữ liệu thay đổi, cập nhật danh sách listFolderItems
            // Lưu ý: Trong trường hợp thực tế, bạn có thể cần xử lý dữ liệu từ userResponse một cách thích hợp.
            // Ở đây, ta giả sử userResponse có một thuộc tính là danh sách các FolderItemData.
            listFolderItems.clear()
            listFolderItems.addAll(userResponse.documents.folders)
            if (listFolderItems.isEmpty()) {
                replaceWithNoDataFragment()
                Toast.makeText(context,"ggggg",Toast.LENGTH_SHORT).show()
            }

            // Thông báo cho adapter rằng dữ liệu đã thay đổi để cập nhật giao diện người dùng
            adapterFolder.notifyDataSetChanged()
        })
        // Access the RecyclerView through the binding
        val rvFolder = binding.rvFolderFragment
        rvFolder.layoutManager = LinearLayoutManager(context)
        rvFolder.adapter = adapterFolder
    }

    private fun replaceWithNoDataFragment() {
        val noDataFragment =
            NoDataFragment()
        val transaction = requireFragmentManager().beginTransaction()
        transaction.replace(R.id.fragmentFolderContainer, noDataFragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}