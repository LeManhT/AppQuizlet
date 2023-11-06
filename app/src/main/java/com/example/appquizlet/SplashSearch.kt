package com.example.appquizlet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView

class SplashSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_search)


//        val searchView = findViewById<SearchView>(R.id.searchView)

        // Tùy chỉnh các thuộc tính khác của SearchView tại đây nếu cần
//        searchView.setIconifiedByDefault(false)
//        searchView.setIconified(false)
//        searchView.queryHint = "Tìm kiếm..."
    }
}