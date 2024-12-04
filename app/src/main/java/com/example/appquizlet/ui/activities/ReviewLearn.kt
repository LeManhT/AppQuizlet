package com.example.appquizlet.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.example.appquizlet.adapter.ReviewLearnAdapter
import com.example.appquizlet.databinding.ActivityReviewLearnBinding
import com.example.appquizlet.model.FlashCardModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ReviewLearn : AppCompatActivity() {
    private lateinit var binding: ActivityReviewLearnBinding
    private lateinit var adapterReviewLearn: ReviewLearnAdapter
    private lateinit var listCards: MutableList<FlashCardModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val intent = intent
        val jsonList = intent.getStringExtra("listCardTest")
        listCards = Gson().fromJson(jsonList, object : TypeToken<List<FlashCardModel>>() {}.type)
        adapterReviewLearn = ReviewLearnAdapter(this, binding.rvReviewLearn)
        adapterReviewLearn.updateData(listCards)
        val myLinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvReviewLearn.layoutManager =
            myLinearLayoutManager
        binding.rvReviewLearn.adapter = adapterReviewLearn
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvReviewLearn)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()

    }
}