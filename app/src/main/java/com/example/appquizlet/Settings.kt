package com.example.appquizlet

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class Settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.toolbar))    // p co dong nay moi chay dc ham ơ dưới
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