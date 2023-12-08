package com.example.appquizlet

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import com.example.appquizlet.databinding.ActivityChangeThemeBinding

class ChangeTheme : AppCompatActivity() {
    private lateinit var binding: ActivityChangeThemeBinding
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = this.getSharedPreferences("changeTheme", Context.MODE_PRIVATE)
        val myTheme = sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val btnApplyTheme: Button = findViewById(R.id.btnApplyTheme)

        btnApplyTheme.setOnClickListener {
            val selectedId: Int = radioGroup.checkedRadioButtonId

            when (selectedId) {
                R.id.radioDark -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                R.id.radioNight -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                R.id.radioSystemDefault -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        with(sharedPreferences.edit()) {
            putInt("theme", mode)
            apply()
        }
        recreate() // Recreate the activity to apply the theme changes
    }
}