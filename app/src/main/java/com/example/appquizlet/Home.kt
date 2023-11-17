package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.databinding.FragmentHomeBinding
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.model.FolderItemData
import com.example.appquizlet.model.StudySetItemData
import com.google.android.material.bottomsheet.BottomSheetBehavior


class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.imgNotification.setOnClickListener {
            showDialogBottomSheet()
        }
        binding.btnHomeAddCourse.setOnClickListener {
            showAddCourseBottomSheet()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetView = view.findViewById<View>(R.id.notification_bottomsheet)
        if (bottomSheetView != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            Toast.makeText(context, "gggg", Toast.LENGTH_SHORT).show()
// Ensure that bottomSheetView is not null before accessing it
            bottomSheetView.let {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }


        binding.txtFolderViewAll.setOnClickListener {
            val fragment = FoldersFragment()
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.fragmentHome, fragment)
            transaction.commit()
        }

        binding.txtStudySetViewAll.setOnClickListener {
            val i = Intent(context, FolderClickActivity::class.java)
            startActivity(i)
        }


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

        var adapterHomeFolder = RVFolderItemAdapter(listFolderItems, object : RVFolderItem {
            override fun handleClickFolderItem(position: Int) {
                Toast.makeText(
                    context,
                    "You clicked " + listFolderItems[position].title,
                    Toast.LENGTH_SHORT
                ).show()

            }
        })

        // Access the RecyclerView through the binding
        var rvHomeFolder = binding.rvHomeFolders
        rvHomeFolder.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        rvHomeFolder.adapter = adapterHomeFolder


//        Studyset adapter
        val listStudySet = mutableListOf<StudySetItemData>()
        listStudySet.add(StudySetItemData("Everyday word 1", 3, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 2", 15, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 3", 5, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))

        val adapterHomeStudySet = RvStudySetItemAdapter(listStudySet)
        var rvStudySet = binding.rvHomeStudySet
        rvStudySet.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvStudySet.adapter = adapterHomeStudySet
    }

    companion object {

    }

    private fun showDialogBottomSheet() {
        val notificationBottomSheet = NotificationFragment()

        //        parentFragmentManager được sử dụng để đảm bảo rằng Bottom Sheet Dialog được hiển thị trong phạm vi của Fragment.
        notificationBottomSheet.show(parentFragmentManager, notificationBottomSheet.tag)
        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }
        }

    }

    private fun showAddCourseBottomSheet () {
        val addCourseBottomSheet = FragmentAddCourse()
        addCourseBottomSheet.show(parentFragmentManager, addCourseBottomSheet.tag)
    }

}