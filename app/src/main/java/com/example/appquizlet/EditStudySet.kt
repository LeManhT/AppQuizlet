package com.example.appquizlet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appquizlet.databinding.ActivityEditStudySetBinding

class EditStudySet : AppCompatActivity() {
    private lateinit var binding: ActivityEditStudySetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStudySetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.iconEditStudySetSetting.setOnClickListener {
            var i = Intent(this, SetOptionActivity::class.java)
            startActivity(i)
        }
    }
}