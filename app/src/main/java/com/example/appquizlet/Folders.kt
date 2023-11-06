package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.databinding.FragmentFoldersBinding


class Folders : Fragment(R.layout.fragment_folders) {
    private val binding: FragmentFoldersBinding by lazy {
        FragmentFoldersBinding.inflate(layoutInflater)
    }
    private var listFolderItem = mutableListOf<FolderItemData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFolderItem.add(FolderItemData("English", R.drawable.profile, "quizlet0509"))
        listFolderItem.add(FolderItemData("China", R.drawable.profile, "quizlet0509"))
        listFolderItem.add(FolderItemData("Math", R.drawable.profile, "quizlet0509"))
        binding.rvFolders.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        var adapterFolder = RVFolderItemAdapter(listFolderItem, object : RVFolderItem {
            override fun handleClickFolderItem(position: Int) {
                Toast.makeText(
                    context,
                    "You clicked " + listFolderItem[position].title,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        binding.rvFolders.adapter = adapterFolder
        binding.rvFolders.setHasFixedSize(true)
    }
}
