package com.example.appquizlet

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.appquizlet.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))    // p co dong nay moi chay dc ham ơ dưới

        binding.layoutChangeLanguage.setOnClickListener {
            val i = Intent(this, ChangeLanguage::class.java)
            startActivity(i)
        }

        binding.layoutChangeTheme.setOnClickListener {
            val i = Intent(this, ChangeTheme::class.java)
            startActivity(i)
        }

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