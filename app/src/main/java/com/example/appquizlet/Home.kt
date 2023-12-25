package com.example.appquizlet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.adapter.AdapterCustomDatePicker
import com.example.appquizlet.adapter.DayOfWeekAdapter
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.databinding.FragmentHomeBinding
import com.example.appquizlet.interfaceFolder.ItemTouchHelperAdapter
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.model.FolderModel
import com.example.appquizlet.model.NotificationModel
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class Home : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var streakTextView: TextView
    private lateinit var apiService: ApiService
    private lateinit var adapterHomeStudySet: RvStudySetItemAdapter
    private lateinit var adapterHomeFolder: RVFolderItemAdapter
    private lateinit var sharedPreferences: SharedPreferences

    //        Studyset adapter
    private val listStudySet = mutableListOf<StudySetModel>()
    private val listFolderItems = mutableListOf<FolderModel>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        sharedPreferences = context?.getSharedPreferences("currentStreak", Context.MODE_PRIVATE)!!


        binding.searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                var i = Intent(context, SplashSearch::class.java)
                binding.searchView.clearFocus()
                startActivity(i)
            }
        }

        binding.imgNotification.setOnClickListener {
            showDialogBottomSheet()
        }
        binding.btnHomeAddCourse.setOnClickListener {
            showAddCourseBottomSheet()
        }
        binding.txtViewAllCourse.setOnClickListener {
            // Lưu thông báo vào cơ sở dữ liệu
//            val notificationDb = context?.let { it1 -> MyDBHelper(it1) }
            val timestamp = System.currentTimeMillis()
            val notificationItem = NotificationModel(
                0,
                "Daily Reminder",
                "Nhắc nhở bạn về điều gì đó quan trọng. Vào app học thôi nào",
                timestamp
            )
//            notificationDb?.addNotification(notificationItem)
            Toast.makeText(context, "add", Toast.LENGTH_SHORT).show()
        }
        // Hiển thị ngày đã được chọn và tính streak
        displayCheckedDates()


        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheetView = view.findViewById<View>(R.id.notification_bottomsheet)
        if (bottomSheetView != null) {
            bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetView)
            bottomSheetView.let {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }

        streakTextView = binding.textView2

        binding.txtFolderViewAll.setOnClickListener {
            val libraryFragment = Library.newInstance()
            (requireActivity() as MainActivity_Logged_In).replaceFragment(libraryFragment)
        }

        binding.txtStudySetViewAll.setOnClickListener {
            val libraryFragment = Library.newInstance()
            (requireActivity() as MainActivity_Logged_In).replaceFragment(libraryFragment)
        }
        val rvHomeFolder = binding.rvHomeFolders
        rvHomeFolder.layoutManager = LinearLayoutManager(
            context, LinearLayoutManager.HORIZONTAL, false
        )

        val rvStudySet = binding.rvHomeStudySet
        rvStudySet.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        adapterHomeFolder =
            RVFolderItemAdapter(requireContext(), listFolderItems, object : RVFolderItem {
                override fun handleClickFolderItem(folderItem: FolderModel, position: Int) {
                    val i = Intent(context, FolderClickActivity::class.java)
                    i.putExtra("idFolder", listFolderItems[position].id)
                    startActivity(i)
                }
            })

        adapterHomeStudySet =
            RvStudySetItemAdapter(requireContext(), listStudySet, object : RVStudySetItem {
                override fun handleClickStudySetItem(setItem: StudySetModel, position: Int) {
                    val intent = Intent(requireContext(), StudySetDetail::class.java)
                    intent.putExtra("setId", listStudySet[position].id)
                    startActivity(intent)
                }
            }, false)


//PagerSnapHelper will provide the smooth swipe effect in the horizontal RecyclerView,
        val snapHelper = PagerSnapHelper()
        val snapHelperFolder = PagerSnapHelper()
        snapHelperFolder.attachToRecyclerView(binding.rvHomeFolders)
        snapHelper.attachToRecyclerView(binding.rvHomeStudySet)


        val userData = UserM.getUserData()
        userData.observe(viewLifecycleOwner, Observer {
            listFolderItems.clear()
            listStudySet.clear()
            listFolderItems.addAll(it.documents.folders)
            listStudySet.addAll(Helper.getAllStudySets(it))
            if (listFolderItems.isEmpty()) {
                binding.rvHomeFolders.visibility = View.GONE
                binding.noDataHomeFolder.visibility = View.VISIBLE
            } else {
                binding.rvHomeFolders.visibility = View.VISIBLE
                binding.noDataHomeFolder.visibility = View.GONE
            }

            if (listStudySet.isEmpty()) {
                binding.rvHomeStudySet.visibility = View.GONE
                binding.noDataHomeSet.visibility = View.VISIBLE
            } else {
                binding.rvHomeStudySet.visibility = View.VISIBLE
                binding.noDataHomeSet.visibility = View.GONE
            }
            adapterHomeFolder.notifyDataSetChanged()
            adapterHomeStudySet.notifyDataSetChanged()
        })

        // Access the RecyclerView through the binding
        rvHomeFolder.adapter = adapterHomeFolder
        rvStudySet.adapter = adapterHomeStudySet

        binding.txtViewAll.setOnClickListener {
            val i = Intent(context, Achievement::class.java)
            startActivity(i)
        }

        binding.rvCustomDatePicker.setOnClickListener {
            val i = Intent(context, Achievement::class.java)
            startActivity(i)
        }


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
                    viewHolder.adapterPosition, target.adapterPosition
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
                recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)
                // Đặt lại thuộc tính khi kéo kết thúc
                viewHolder.itemView.animate().translationY(0f).alpha(1f).setDuration(300).start()
            }

        })

        itemTouchHelper.attachToRecyclerView(rvHomeFolder)

//        rvHomeFolder.isScrollbarFadingEnabled = false


//        Custom date/
        val recyclerViewDayOfWeek: RecyclerView = binding.rvDayOfWeek
        val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
        val dayOfWeekAdapter = DayOfWeekAdapter(daysOfWeek)
        recyclerViewDayOfWeek.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDayOfWeek.adapter = dayOfWeekAdapter

        // Dùng RecyclerView cho Ngày trong Tháng
        val recyclerViewDay: RecyclerView = binding.rvCustomDatePicker

        // Lấy ngày hiện tại
        val currentDate = LocalDate.now()

        // Lấy ngày Chủ Nhật của tuần trước
        val sundayOfLastWeek = currentDate.minusWeeks(1).with(DayOfWeek.SUNDAY)

        // Tạo danh sách 7 ngày từ Chủ Nhật đến Thứ Bảy
        val daysInWeek = (0 until 7).map { index ->
            sundayOfLastWeek.plusDays(index.toLong())
        }

        // Chuyển đổi danh sách ngày thành danh sách chuỗi
        val formattedDays = daysInWeek.map { day ->
            day.format(DateTimeFormatter.ofPattern("d")) // Định dạng là số ngày (1, 2, 3, ...)
        }
        // Lấy ngày hiện tại
        val today = LocalDate.now()
        val achievedDays = mutableListOf<String>()

        UserM.getDataAchievements().observe(viewLifecycleOwner, Observer {
            updateStreakText(it.streak.currentStreak)
//            if (it.streak.currentStreak > sharedPreferences.getInt(
//                    "countStreak", 0
//                )
//            ) {
                saveCountStreak(it.streak.currentStreak)
//            }
            // Tính ngày bắt đầu streak hiện tại
            val startStreakDate = today.minusDays(it.streak.currentStreak.toLong())
            // In ngày bắt đầu và ngày kết thúc của streak hiện tại
            println("Ngày bắt đầu streak hiện tại: $startStreakDate")
            println("Ngày kết thúc streak hiện tại: $today")

            // Nếu bạn muốn lấy danh sách các ngày đã đạt được streak, bạn có thể sử dụng vòng lặp
            for (i in 0..it.streak.currentStreak) {
                achievedDays.add(
                    startStreakDate.plusDays(i.toLong()).format(DateTimeFormatter.ofPattern("d"))
                )
            }
            val formattedAchieveDays = achievedDays.map { day ->
                day.format(DateTimeFormatter.ofPattern("d")) // Định dạng là số ngày (1, 2, 3, ...)
            }


            val dayAdapter = AdapterCustomDatePicker(formattedDays, formattedAchieveDays)
            recyclerViewDay.layoutManager = LinearLayoutManager(
                context, LinearLayoutManager.HORIZONTAL, false
            ) // Hiển thị 7 cột
            recyclerViewDay.adapter = dayAdapter
        })

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayCheckedDates() {
        val unixTime = Instant.now().epochSecond
        detectContinueStudy(Helper.getDataUserId(requireContext()), unixTime)
    }

    private fun getFormattedDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun updateStreakText(streakCount: Int) {
        streakTextView.text = "$streakCount-days streak"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun detectContinueStudy(userId: String, timeDetect: Long) {
        lifecycleScope.launch {
            try {
                val result = apiService.detectContinueStudy(userId, timeDetect)
                if (result.isSuccessful) {
                    result.body().let {
                        if (it != null) {
                            Log.d("data", it.streak.currentStreak.toString())
                        }
                        if (it != null) {
                            UserM.setDataAchievements(it)
                        }
                    }
                } else {
                    Log.d("error", result.errorBody().toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveCountStreak(streak: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("countStreak", streak)
        editor.apply()
    }

}