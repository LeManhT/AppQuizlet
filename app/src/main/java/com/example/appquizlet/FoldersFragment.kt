package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.databinding.FragmentFoldersBinding
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.model.FolderItemData

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

        val listFolderItems = mutableListOf<FolderItemData>()
        listFolderItems.add(FolderItemData("English", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Chinese", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Japanese", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Forex", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Korea", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Android", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Javascript", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Kotlin", R.drawable.profile, "lemamjh222"))
        listFolderItems.add(FolderItemData("Military", R.drawable.profile, "lemamjh222"))

        val adapterFolder = RVFolderItemAdapter(listFolderItems, object : RVFolderItem {
            override fun handleClickFolderItem(position: Int) {
//                var i = Intent(context, FolderClickActivity::class.java)
//                startActivity(i)
                Toast.makeText(
                    context,
                    "You clicked " + listFolderItems[position].title,
                    Toast.LENGTH_SHORT
                ).show()

            }
        })

        // Access the RecyclerView through the binding
        val rvFolder = binding.rvFolderFragment
        rvFolder.layoutManager = LinearLayoutManager(
            context
        )
        rvFolder.adapter = adapterFolder
    }

}