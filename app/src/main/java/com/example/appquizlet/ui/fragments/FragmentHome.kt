package com.example.appquizlet.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.appquizlet.MainActivity_Logged_In
import com.example.appquizlet.R
import com.example.appquizlet.SignInActivity
import com.example.appquizlet.adapter.AdapterCustomDatePicker
import com.example.appquizlet.adapter.DayOfWeekAdapter
import com.example.appquizlet.adapter.RVFolderItemAdapter
import com.example.appquizlet.adapter.RvStudySetItemAdapter
import com.example.appquizlet.adapter.SolutionItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentHomeBinding
import com.example.appquizlet.interfaceFolder.ItemTouchHelperAdapter
import com.example.appquizlet.interfaceFolder.RVFolderItem
import com.example.appquizlet.interfaceFolder.RVStudySetItem
import com.example.appquizlet.interfaceFolder.RvClickSearchSet
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.model.FolderModel
import com.example.appquizlet.model.SearchSetModel
import com.example.appquizlet.model.StudySetModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.ui.activities.AchievementActivity
import com.example.appquizlet.ui.activities.FolderClickActivity
import com.example.appquizlet.ui.activities.RankLeaderBoard
import com.example.appquizlet.ui.activities.SplashSearch
import com.example.appquizlet.ui.activities.StudySetDetail
import com.example.appquizlet.ui.activities.TranslateActivity
import com.example.appquizlet.ui.activities.WelcomeToLearn
import com.example.appquizlet.util.Helper
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.Gson
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var streakTextView: TextView
    private lateinit var apiService: ApiService
    private lateinit var adapterHomeStudySet: RvStudySetItemAdapter
    private lateinit var adapterRecommendationListSet: SolutionItemAdapter
    private lateinit var adapterHomeFolder: RVFolderItemAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTheme: SharedPreferences
    private var apiCallsInProgress = false

    private val listStudySet = mutableListOf<StudySetModel>()
    private val listRcmSets = mutableListOf<SearchSetModel>()
    private val listFolderItems = mutableListOf<FolderModel>()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        sharedPreferences = context?.getSharedPreferences("currentStreak", Context.MODE_PRIVATE)!!
        sharedPreferencesTheme =
            context?.getSharedPreferences("changeTheme", Context.MODE_PRIVATE)!!

        val themeChangeFlag = sharedPreferencesTheme.getBoolean("themeChange", false)
        sharedPreferencesTheme.edit().putBoolean("themeChange", false).apply()

        getAllStudySet()

        if (loadSuggestedFlashcards()?.isNotEmpty() == true) {
            showReviewDialog()
        }

        if (!themeChangeFlag) {
            if (!apiCallsInProgress) {
                apiCallsInProgress = true
                getUserRanking(requireContext(), Helper.getDataUserId(requireContext()))
                getAllNotices(requireContext(), Helper.getDataUserId(requireContext()))
            }
        }

        val dataRanking = UserM.getDataRanking()
        dataRanking.observe(viewLifecycleOwner) {
            binding.podiumView.setPodiumData(
                it, it, it,
                item1 = "2" to "Avenger",
                item2 = "1" to "Super hero",
                item3 = "3" to "Hero"
            )

            if (it.currentScore > 7000) {
                binding.btnUpgradeFeature.visibility = View.GONE
                binding.txtVerified.visibility = View.VISIBLE
                binding.txtVerified.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext()).setTitle(resources.getString(R.string.premium_account))
                        .setMessage(resources.getString(R.string.premium_account_desc))
                        .setNegativeButton(resources.getString(R.string.close)) { dialog, which ->
                            run {
                                dialog.dismiss()
                            }
                        }.show()
                }
            } else {
                binding.btnUpgradeFeature.visibility = View.VISIBLE
                binding.txtVerified.visibility = View.GONE
                binding.btnUpgradeFeature.setOnClickListener {
                    val i = Intent(context, FragmentQuizletPlus::class.java)
                    startActivity(i)
                }
            }
        }

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

        binding.btnOpenRankLeaderBoard.setOnClickListener {
            val i = Intent(context, RankLeaderBoard::class.java)
            startActivity(i)
        }
        binding.txtViewDetailLeaderBoard.setOnClickListener {
            val i = Intent(context, RankLeaderBoard::class.java)
            startActivity(i)
        }

//        binding.txtViewAllQuote.setOnClickListener {
//            val i = Intent(context, QuoteInLanguage::class.java)
//            startActivity(i)
//        }
//
//        binding.txtGoQuote.setOnClickListener {
//            val i = Intent(context, QuoteInLanguage::class.java)
//            startActivity(i)
//        }

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

        streakTextView = binding.txtCountStreak

        binding.txtFolderViewAll.setOnClickListener {
            (requireActivity() as MainActivity_Logged_In).selectBottomNavItem(
                "Library",
                "viewAllFolder"
            )
        }

        binding.txtStudySetViewAll.setOnClickListener {
            (requireActivity() as MainActivity_Logged_In).selectBottomNavItem(
                "Library",
                "createSet"
            )
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

        adapterRecommendationListSet =
            SolutionItemAdapter(listRcmSets, object : RvClickSearchSet {
                override fun handleClickSetSearch(position: Int) {
                    val intent = Intent(requireContext(), StudySetDetail::class.java)
                    intent.putExtra("setId", listStudySet[position].id)
                    startActivity(intent)
                }
            })

//PagerSnapHelper will provide the smooth swipe effect in the horizontal RecyclerView,
        val snapHelper = PagerSnapHelper()
        val snapHelperFolder = PagerSnapHelper()
        snapHelperFolder.attachToRecyclerView(binding.rvHomeFolders)
        snapHelper.attachToRecyclerView(binding.rvHomeStudySet)
        snapHelper.attachToRecyclerView(binding.rvListRecommendationLists)


        val userData = UserM.getUserData()
        userData.observe(viewLifecycleOwner) {
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
        }


        UserM.getAllStudySets().observe(viewLifecycleOwner) {
            listRcmSets.clear()
            listRcmSets.addAll(Helper.getRecommendedStudySets(it, listStudySet))

            if (listRcmSets.isEmpty()) {
                binding.layoutRecommendationList.visibility = View.GONE
            } else {
                binding.layoutRecommendationList.visibility = View.VISIBLE
            }
            adapterRecommendationListSet.notifyDataSetChanged()
        }

        // Access the RecyclerView through the binding
        rvHomeFolder.adapter = adapterHomeFolder
        rvStudySet.adapter = adapterHomeStudySet
        binding.rvListRecommendationLists.adapter = adapterRecommendationListSet
        binding.rvListRecommendationLists.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)


        binding.txtViewAll.setOnClickListener {
            val i = Intent(context, AchievementActivity::class.java)
            startActivity(i)
        }

        binding.txtViewAllTranslate.setOnClickListener {
            val i = Intent(context, TranslateActivity::class.java)
            startActivity(i)
        }

        binding.rvCustomDatePicker.setOnClickListener {
            val i = Intent(context, AchievementActivity::class.java)
            startActivity(i)
        }

        binding.txtTranslatePararaph.setOnClickListener {
            val intent = Intent(context, TranslateActivity::class.java)
            startActivity(intent)
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
            Log.d("currentStreakkk", it.streak.currentStreak.toString())
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
            for (i in 0 until it.streak.currentStreak) {
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
        const val TAG = "Home"
    }

    private fun showDialogBottomSheet() {
        val notificationBottomSheet = FragmentNotification()
        //        parentFragmentManager được sử dụng để đảm bảo rằng Bottom Sheet Dialog được hiển thị trong phạm vi của Fragment.
        notificationBottomSheet.show(parentFragmentManager, notificationBottomSheet.tag)
    }

    private fun showAddCourseBottomSheet() {
        val addCourseBottomSheet = FragmentAddCourse()
        addCourseBottomSheet.show(parentFragmentManager, addCourseBottomSheet.tag)
    }


    private fun getFormattedDate(calendar: Calendar): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(calendar.time)
    }

    private fun updateStreakText(streakCount: Int) {
        streakTextView.text = "$streakCount-days streak"
    }


    private fun saveCountStreak(streak: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("countStreak", streak)
        editor.apply()
    }

    private fun getUserRanking(context: Context, userId: String) {
        lifecycleScope.launch {
            try {
                val accessToken = Helper.getAccessToken(context)
                if (accessToken.isNullOrEmpty()) {
                    Log.e("AuthError", "Access Token is missing")
                    return@launch
                }
                val authorizationHeader = "Bearer ${accessToken.trim()}"
                val result = apiService.getRankResult(authorizationHeader, userId)
                Log.d("authorizationHeader : ",authorizationHeader)
                if (result.isSuccessful) {
                    result.body()?.let {
                        Log.d("Success", "Ranking Data: ${Gson().toJson(it)}")
                        UserM.setDataRanking(it)
                    }
                } else {
                    val errorBody = result.errorBody()?.string()
                    Log.e("API Error", "Error: $errorBody")
                    CustomToast(requireContext()).makeText(
                        requireContext(),
                        "Error: $errorBody",
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("Exception", "Error: ${e.message}")
                CustomToast(requireContext()).makeText(
                    requireContext(),
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }


    private fun getAllNotices(context: Context, userId: String) {
        lifecycleScope.launch {
            try {
                val accessToken = Helper.getAccessToken(context)
                if (accessToken.isNullOrEmpty()) {
                    Log.e("AuthError", "Access Token is missing")
                }
                val result =
                    apiService.getAllCurrentNotices(authorization = "Bearer $accessToken", userId)
                if (result.isSuccessful) {
                    result.body()?.let {
                        UserM.setDataNotification(it)
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        context?.let { it1 ->
                            CustomToast(it1).makeText(
                                requireContext(),
                                resources.getString(R.string.sth_went_wrong),
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                    }
                    return@launch
                }
            } catch (e: Exception) {
                Log.d("Exception Notice12", e.message.toString())
                val i = Intent(context, SignInActivity::class.java)
                startActivity(i)
                return@launch
            } finally {
                apiCallsInProgress = false
            }
        }
    }

    private fun getAllStudySet() {
        try {
            lifecycleScope.launch {
                val result = apiService.getAllSet()
                if (result.isSuccessful) {
                    result.body()?.let {
                        UserM.setAllStudySet(it)
                    }
                } else {
                    result.errorBody()?.string()?.let {
                        Log.e("Error get rank 0", "Error response: ${Gson().toJson(it)}")
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    private fun loadSuggestedFlashcards(): List<FlashCardModel>? {
        val sharedPreferences = context?.getSharedPreferences("quizlet_prefs", Context.MODE_PRIVATE)
        val jsonFlashcards = sharedPreferences?.getString("suggested_flashcards", null)
        return if (jsonFlashcards != null) {
            Gson().fromJson(jsonFlashcards, Array<FlashCardModel>::class.java).toList()
        } else {
            null
        }
    }

    private fun showReviewDialog() {
        val jsonList = Gson().toJson(loadSuggestedFlashcards())
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Ôn luyện cần thiết")
            .setMessage("Bạn có một số câu trả lời sai, hãy vào ôn luyện.")
            .setPositiveButton("Bắt đầu ôn luyện") { _, _ ->
                val i = Intent(requireContext(), WelcomeToLearn::class.java)
                i.putExtra("listCardTest", jsonList)
                startActivity(i)
            }
            .setNegativeButton("Để sau", null)
            .setCancelable(false)
            .show()
    }
}