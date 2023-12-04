package com.example.appquizlet

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.FlashcardItemAdapter
import com.example.appquizlet.adapter.StudySetItemAdapter
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityStudySetDetailBinding
import com.example.appquizlet.interfaceFolder.RvFlashCard
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.util.Locale

class StudySetDetail : AppCompatActivity(), OnInitListener,
    FlashcardItemAdapter.OnFlashcardItemClickListener {
    private lateinit var binding: ActivityStudySetDetailBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiService: ApiService
    private lateinit var setId: String
    private lateinit var textToSpeech: TextToSpeech
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudySetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo TextToSpeech
        textToSpeech = TextToSpeech(this, this)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)
        //        set toolbar back display
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại
// Tắt tiêu đề của Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        var intent = intent
        setId = intent.getStringExtra("setId").toString()

        binding.layoutSortText.setOnClickListener {
            showDialogBottomSheet()
        }


        val listCards = mutableListOf<FlashCardModel>()
        val listFlashcardDetails = mutableListOf<FlashCardModel>()
        val adapterStudySet = StudySetItemAdapter(listCards, object : RvFlashCard {
            override fun handleClickFLashCard(flashcardItem: FlashCardModel) {
                flashcardItem.isUnMark = !flashcardItem.isUnMark!!
            }
        })
        val adapterFlashcardDetail = FlashcardItemAdapter(listFlashcardDetails)
        var userData = UserM.getUserData()
        userData.observe(this, Observer { userResponse ->
            val studySet = userResponse.documents.studySets.find { listStudySets ->
                listStudySets.id == setId
            }
            if (studySet != null) {
                listCards.clear()
                listCards.addAll(studySet.cards)
                listFlashcardDetails.addAll(studySet.cards)
                Log.d("kk1", Gson().toJson(listCards))
            }
            adapterStudySet.notifyDataSetChanged()
            adapterFlashcardDetail.notifyDataSetChanged()

            val indicators = binding.circleIndicator3
            indicators.setViewPager(binding.viewPagerStudySet)
            // Thông báo cho adapter rằng dữ liệu đã thay đổi để cập nhật giao diện người dùng
        })
        binding.viewPagerStudySet.adapter = adapterStudySet
        // Thiết lập lắng nghe sự kiện click cho adapter
        adapterFlashcardDetail.setOnFlashcardItemClickListener(this)

        binding.rvAllFlashCards.layoutManager = LinearLayoutManager(this)
        binding.rvAllFlashCards.adapter = adapterFlashcardDetail


        binding.btnStudyThisSet.setOnClickListener {
            showStudyThisSetBottomsheet()
        }


        // Chuyển đổi danh sách FlashCardModel thành chuỗi JSON
        val jsonList = Gson().toJson(listCards)

        // Đưa chuỗi JSON vào Intent
        binding.layoutFlashcardLearn.setOnClickListener {
            val i = Intent(applicationContext, FlashcardLearn::class.java)
            i.putExtra("listCard", jsonList)
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_study_set, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }

            R.id.option_add_to_folder -> {
                val i = Intent(this, FlashcardAddSetToFolder::class.java)
                startActivity(i)
            }

            R.id.option_share -> {
                shareDialog("Share content")
            }

            R.id.option_edit -> {
                val i = Intent(this, EditStudySet::class.java)
                startActivity(i)
            }

            R.id.option_delete -> {
                showDeleteDialog(resources.getString(R.string.delete_text))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showStudyThisSetBottomsheet() {
//        val addCourseBottomSheet = StudyThisSetFragment()
//        addCourseBottomSheet.show(supportFragmentManager, "")
    }

    private fun showDialogBottomSheet() {
        val addBottomSheet = FragmentSortTerm()

//        if (!addBottomSheet.isAdded) {
//            val transaction = supportFragmentManager.beginTransaction()
//            transaction.add(addBottomSheet, FragmentSortTerm.TAG)
//            transaction.commitAllowingStateLoss()
//        }
    }

    private fun shareDialog(content: String) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here")
        sharingIntent.putExtra(Intent.EXTRA_TEXT, content)

        val packageNames =
            arrayOf("com.facebook.katana", "com.facebook.orca", "com.google.android.gm")
        val chooserIntent = Intent.createChooser(sharingIntent, "Share via")
        chooserIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, packageNames)

        startActivity(sharingIntent)
    }

    private fun showDeleteDialog(desc: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(desc)
        builder.setPositiveButton(resources.getString(R.string.delete)) { dialog, _ ->
            deleteStudySet(Helper.getDataUserId(this), setId)
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    fun deleteStudySet(userId: String, setId: String) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.deleteFolderLoading))
            try {
                val result = apiService.deleteStudySet(userId, setId)
                if (result.isSuccessful) {

                    result.body().let {
                        if (it != null) {
                            CustomToast(this@StudySetDetail).makeText(
                                this@StudySetDetail,
                                resources.getString(R.string.deleteFolderSuccessful),
                                CustomToast.LONG,
                                CustomToast.SUCCESS
                            ).show()
//                            UserM.setUserDataStudySets(it.documents.studySets)
                            UserM.setUserData(it)
                        }
                    }
                } else {
                    CustomToast(this@StudySetDetail).makeText(
                        this@StudySetDetail,
                        resources.getString(R.string.deleteSetErr),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                }
            } catch (e: Exception) {
                CustomToast(this@StudySetDetail).makeText(
                    this@StudySetDetail,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } finally {
                progressDialog.dismiss()
            }
        }
    }


    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this, null, msg)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                Toast.makeText(this, "Language not supported.", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Initialization failed.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun speakOut(text: String) {
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onDestroy() {
        if (textToSpeech.isSpeaking) {
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        super.onDestroy()
    }

    override fun onFlashcardItemClick(term: String) {
        speakOut(term)
        Log.d("StudySetDetail", "Clicked on term: $term")
    }


}