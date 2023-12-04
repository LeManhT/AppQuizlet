package com.example.appquizlet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.appquizlet.adapter.LearnFlashcardAdapter
import com.example.appquizlet.adapter.SwiperAdapter
import com.example.appquizlet.databinding.ActivityFlashcardLearnBinding
import com.example.appquizlet.model.FlashCardModel
import com.example.appquizlet.model.SwiperStack
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FlashcardLearn : AppCompatActivity() {
    private lateinit var binding: ActivityFlashcardLearnBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlashcardLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val jsonList = intent.getStringExtra("listCard")

        // Chuyển đổi chuỗi JSON thành danh sách FlashCardModel
        val listCards: List<FlashCardModel> =
            Gson().fromJson(jsonList, object : TypeToken<List<FlashCardModel>>() {}.type)

        val adapterLearn = SwiperAdapter(listCards)
        val swipeStack = findViewById<View>(R.id.swipeStack) as SwiperStack
        swipeStack.adapter = adapterLearn
//        binding.viewPager.adapter = adapterLearn
//        val indicators = binding.circleIndicator3Learn
//        indicators.setViewPager(binding.viewPager)
    }
}