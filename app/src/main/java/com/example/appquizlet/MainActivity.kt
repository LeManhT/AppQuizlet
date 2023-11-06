package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appquizlet.MainActivity_Logged_In
import com.example.appquizlet.databinding.ActivityMainBinding

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
      binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

       binding.btnSlash.setOnClickListener {
            var intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
        binding.btnLoggedIn.setOnClickListener {
            var intent = Intent(this, MainActivity_Logged_In::class.java)
            startActivity(intent)
        }
        binding.btn404.setOnClickListener {
            var intent = Intent(this,NotFound::class.java)
            startActivity(intent)
        }


    }


}