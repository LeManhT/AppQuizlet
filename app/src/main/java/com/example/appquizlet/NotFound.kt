package com.example.appquizlet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.example.appquizlet.databinding.ActivityNotFoundBinding


class NotFound : AppCompatActivity() {
    private lateinit var binding: ActivityNotFoundBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotFoundBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnGoHome.setOnClickListener {
            val intent = Intent(this, MainActivity_Logged_In::class.java)
            startActivity(intent)
        }
    }
}