package com.example.appquizlet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.appquizlet.databinding.ActivityExcellentBinding

class Excellent : AppCompatActivity() {
    private lateinit var binding: ActivityExcellentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExcellentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoHome.setOnClickListener {
            val i = Intent(this, MainActivity_Logged_In::class.java)
            startActivity(i)
        }
    }
}