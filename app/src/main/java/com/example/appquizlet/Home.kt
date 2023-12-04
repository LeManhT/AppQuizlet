package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.database.MyDBHelper
import com.example.appquizlet.databinding.FragmentHomeBinding
import com.example.appquizlet.interfaceFolder.ItemTouchHelperAdapter
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.FolderModel
import com.example.appquizlet.model.NotificationModel
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.notification.addNotification
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
        binding.txtViewAllCourse.setOnClickListener {
            // Lưu thông báo vào cơ sở dữ liệu
            val notificationDb = context?.let { it1 -> MyDBHelper(it1) }
            val timestamp = System.currentTimeMillis()
            val notificationItem = NotificationModel(
                0,
                "Daily Reminder",
                "Nhắc nhở bạn về điều gì đó quan trọng. Vào app học thôi nào",
                timestamp
            )
            notificationDb?.addNotification(notificationItem)
            Toast.makeText(context, "add", Toast.LENGTH_SHORT).show()
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
        var adapterHomeFolder = RVFolderItemAdapter(listFolderItems, object : RVFolderItem {
            override fun handleClickFolderItem(position: Int) {
                val i = Intent(context, FolderClickActivity::class.java)
                startActivity(i)
            }
        })

        //        Studyset adapter
        val listStudySet = mutableListOf<StudySetModel>()

        val adapterHomeStudySet = RvStudySetItemAdapter(listStudySet, object : RVStudySetItem {
            override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
//                var intent = Intent(context, StudySetDetail::class.java)
//                startActivity(intent)
            }
        })


        val userData = UserM.getUserData()
        userData.observe(viewLifecycleOwner, Observer {
            listFolderItems.clear()
            listFolderItems.addAll(it.documents.folders)
            listStudySet.addAll(it.documents.studySets)
            adapterHomeFolder.notifyDataSetChanged()
            adapterHomeStudySet.notifyDataSetChanged()
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