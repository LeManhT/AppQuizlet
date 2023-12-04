package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.custom.ScrollListener
import com.example.appquizlet.databinding.FragmentHomeBinding
import com.example.appquizlet.interfaceFolder.ItemTouchHelperAdapter
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.FolderModel
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


        val listFolderItems = mutableListOf<FolderModel>()
        listFolderItems.add(FolderModel("English", "lemamjh222", 34444, ""))
        listFolderItems.add(FolderModel("Chinese", "lemamjh222", 34444, ""))
        listFolderItems.add(FolderModel("Japanese", "lemamjh222", 34444, ""))
        listFolderItems.add(FolderModel("Forex", "lemamjh222", 34444, ""))
        listFolderItems.add(FolderModel("Korea", "lemamjh222", 34444, ""))
        listFolderItems.add(FolderModel("Android", "lemamjh222", 34444, ""))
        listFolderItems.add(FolderModel("Javascript", "lemamjh222", 34444, ""))

        var adapterHomeFolder = RVFolderItemAdapter(listFolderItems, object : RVFolderItem {
            override fun handleClickFolderItem(position: Int) {
                Toast.makeText(
                    context,
                    "You clicked " + listFolderItems[position].name,
                    Toast.LENGTH_SHORT
                ).show()

            }
        })

        // Access the RecyclerView through the binding
        var rvHomeFolder = binding.rvHomeFolders
        rvHomeFolder.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Gọi phương thức onItemMove từ Adapter khi item được di chuyển
                return (recyclerView.adapter as ItemTouchHelperAdapter).onItemMove(
                    viewHolder.adapterPosition,
                    target.adapterPosition
                )
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                Toast.makeText(context, "ffff", Toast.LENGTH_SHORT).show()
            }


            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.7f // Giảm độ mờ của item khi đang được kéo
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // Đặt lại thuộc tính khi kéo kết thúc
                viewHolder.itemView.animate().translationY(0f).alpha(1f).setDuration(300).start()
            }

        })

        itemTouchHelper.attachToRecyclerView(rvHomeFolder)

//        rvHomeFolder.isScrollbarFadingEnabled = false
        rvHomeFolder.adapter = adapterHomeFolder


//        Studyset adapter
        val listStudySet = mutableListOf<StudySetItemData>()
        listStudySet.add(StudySetItemData("Everyday word 1", 3, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 2", 15, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 3", 5, R.drawable.profile, "lemamnhed"))
        listStudySet.add(StudySetItemData("Everyday word 4", 26, R.drawable.profile, "lemamnhed"))

        val adapterHomeStudySet = RvStudySetItemAdapter(listStudySet, object : RVStudySetItem {
            override fun handleClickStudySetItem(setItem: StudySetItemData) {
//                var intent = Intent(context, StudySetDetail::class.java)
//                startActivity(intent)
            }
        })
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
    }

    private fun showAddCourseBottomSheet() {
        val addCourseBottomSheet = FragmentAddCourse()
        addCourseBottomSheet.show(parentFragmentManager, addCourseBottomSheet.tag)
    }

}