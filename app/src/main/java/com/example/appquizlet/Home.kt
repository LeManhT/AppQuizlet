package com.example.appquizlet

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
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
import com.example.appquizlet.util.Helper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var streakTextView: TextView
    private val PREF_NAME = "MyPrefs"
    private val KEY_LAST_CHECKED_DATE = "last_checked_date"
    private val KEY_STREAK_COUNT = "streak_count"
    private var checkedDates = mutableSetOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val calendar = Calendar.getInstance()
        // Thiết lập ngày tối thiểu và ngày tối đa cho DatePicker
        val minDate = calendar.timeInMillis // Đặt là ngày hiện tại
        val maxDate = calendar.timeInMillis // Đặt là ngày hiện tại
        binding.datePicker.minDate = minDate
        binding.datePicker.maxDate = maxDate
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

        streakTextView = binding.textView2

        // Hiển thị ngày đã được chọn và tính streak
        displayCheckedDates()


        return binding.root
    }

    private fun getFormattedDate(year: Int, month: Int, dayOfMonth: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
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
            var i = Intent(requireActivity(), Library::class.java)
            startActivity(i)
        }

        binding.txtStudySetViewAll.setOnClickListener {
            val i = Intent(requireActivity(), StudySets::class.java)
            startActivity(i)
        }


        val listFolderItems = mutableListOf<FolderModel>()
        var adapterHomeFolder =
            RVFolderItemAdapter(requireContext(), listFolderItems, object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
                    val i = Intent(context, FolderClickActivity::class.java)
                    i.putExtra("idFolder", listFolderItems[position].id)
                    startActivity(i)
                }
            })

        //        Studyset adapter
        val listStudySet = mutableListOf<StudySetModel>()

        val adapterHomeStudySet =
            RvStudySetItemAdapter(requireContext(), listStudySet, object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    val intent = Intent(requireContext(), StudySetDetail::class.java)
                    intent.putExtra("setId", listStudySet[position].id)
                    startActivity(intent)
                }
            }, false)
//PagerSnapHelper will provide the smooth swipe effect in the horizontal RecyclerView,
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvHomeFolders)
        snapHelper.attachToRecyclerView(binding.rvHomeStudySet)


        val userData = UserM.getUserData()
        userData.observe(viewLifecycleOwner, Observer {
            listFolderItems.clear()
            listFolderItems.addAll(it.documents.folders)
            listStudySet.addAll(Helper.getAllStudySets(it))
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
        const val Tag = "Home"
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

    private fun displayCheckedDates() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val currentDate = getFormattedDate(Calendar.getInstance())
        val lastCheckedDate = sharedPreferences.getString(KEY_LAST_CHECKED_DATE, "") ?: ""
        val streakCount = sharedPreferences.getInt(KEY_STREAK_COUNT, 0)

        // Kiểm tra mỗi khi người dùng vào app và nếu sang ngày mới
        if (currentDate != lastCheckedDate) {
            // Tăng streak và cập nhật danh sách ngày đã kiểm tra
            val editor = sharedPreferences.edit()
            editor.putString(KEY_LAST_CHECKED_DATE, currentDate)
            editor.putInt(KEY_STREAK_COUNT, streakCount + 1)
            editor.apply()

            // Update checkedDates with the current date
            checkedDates.add(currentDate)
        }

        // Hiển thị streak
        updateStreakText(streakCount)
    }


    private fun markCheckedDate(date: String) {
        val dayOfMonth = date.substring(8).toInt()
        val month = date.substring(5, 7).toInt() - 1
        val year = date.substring(0, 4).toInt()

        // Thực hiện logic tô màu cho ngày đã kiểm tra (ví dụ: thay đổi background color)
    }

    private fun getFormattedDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun updateStreakText(streakCount: Int) {
        streakTextView.text = "$streakCount-day streak"
    }

}