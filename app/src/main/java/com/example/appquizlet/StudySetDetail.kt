package com.example.appquizlet

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.adapter.FlashcardItemAdapter
import com.example.appquizlet.adapter.StudySetItemAdapter
import com.example.appquizlet.databinding.ActivityStudySetDetailBinding
import com.example.appquizlet.interfaceFolder.RvFlashCard
import com.example.appquizlet.model.FlashCardModel

class StudySetDetail : AppCompatActivity() {
    private lateinit var binding: ActivityStudySetDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudySetDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //        set toolbar back display
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại
// Tắt tiêu đề của Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.layoutSortText.setOnClickListener {
            showDialogBottomSheet()
        }

        val indicators = binding.circleIndicator3
        val listCards = mutableListOf<FlashCardModel>()
        listCards.add(FlashCardModel("1", "Term 1", "Def1", 555555, true, ""))
        listCards.add(FlashCardModel("2", "Term 2", "Def2", 555555, true, ""))
        listCards.add(FlashCardModel("3", "Term 3", "Def3", 555555, true, ""))
        listCards.add(FlashCardModel("4", "Term 4", "Def4", 555555, true, ""))
        val adapterStudySet = StudySetItemAdapter(listCards, object : RvFlashCard {
            override fun handleClickFLashCard(flashcardItem: FlashCardModel) {
                flashcardItem.isUnMark = !flashcardItem.isUnMark!!
            }
        })
        binding.viewPagerStudySet.adapter = adapterStudySet
        indicators.setViewPager(binding.viewPagerStudySet)


        val listFlashcardDetails = mutableListOf<FlashCardModel>()
        listFlashcardDetails.add(FlashCardModel("1", "Term 1", "Def1", 555555, true, ""))
        listFlashcardDetails.add(FlashCardModel("2", "Term 2", "Def2", 555555, true, ""))
        listFlashcardDetails.add(FlashCardModel("3", "Term 3", "Def3", 555555, true, ""))
        listFlashcardDetails.add(FlashCardModel("4", "Term 4", "Def4", 555555, true, ""))

        val adapterFlashcardDetail = FlashcardItemAdapter(listFlashcardDetails)
        binding.rvAllFlashCards.layoutManager = LinearLayoutManager(this)
        binding.rvAllFlashCards.adapter = adapterFlashcardDetail


        binding.btnStudyThisSet.setOnClickListener {
            showStudyThisSetBottomsheet()
        }


        binding.layoutFlashcardLearn.setOnClickListener {
            val intent = Intent(this, FlashcardLearn::class.java)
            startActivity(intent)
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
        val addCourseBottomSheet = StudyThisSetFragment()
        addCourseBottomSheet.show(supportFragmentManager, "")
    }

    private fun showDialogBottomSheet() {
        val addBottomSheet = FragmentSortTerm()
        val transaction = supportFragmentManager.beginTransaction()
        addBottomSheet.show(transaction, FragmentSortTerm.TAG)
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
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }
}