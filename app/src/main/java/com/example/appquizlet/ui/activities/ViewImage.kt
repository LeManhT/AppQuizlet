package com.example.appquizlet.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.appquizlet.R
import com.example.appquizlet.databinding.ActivityViewImage2Binding
import com.github.chrisbanes.photoview.PhotoView

class ViewImage : AppCompatActivity() {
    private lateinit var binding: ActivityViewImage2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewImage2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        val photoView: PhotoView = binding.photoView
        val imageUrl = intent.getStringExtra("IMAGE_URL")
        Glide.with(this).load(imageUrl).into(photoView)

        binding.imgClose.setOnClickListener {
            finish()
        }

        photoView.setOnClickListener {
            finish()
        }

    }
}