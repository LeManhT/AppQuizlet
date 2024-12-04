package com.example.appquizlet.ui.activities

import android.os.Bundle
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ActivityAddCourseBinding
import com.example.appquizlet.ui.fragments.FragmentAddCourse

class AddCourseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCourseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.toolbar))
        binding.toolbarLayout.title = title
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        binding.btnAddCourse.setOnClickListener {
            showAddCourseBottomSheet()
        }
    }

    private fun showAddCourseBottomSheet() {
        val addCourseBottomSheet = FragmentAddCourse()
        addCourseBottomSheet.show(supportFragmentManager, addCourseBottomSheet.tag)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Xử lý khi nút "Quay lại" được bấm
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}