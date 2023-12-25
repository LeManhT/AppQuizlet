package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityChangeThemeBinding
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class ChangeTheme : AppCompatActivity() {
    private lateinit var binding: ActivityChangeThemeBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var apiService: ApiService
    private var darkMode: Boolean = false
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeThemeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)


        val radioGroup: RadioGroup = findViewById(R.id.radioGroup)
        val btnApplyTheme: Button = findViewById(R.id.btnApplyTheme)

        btnApplyTheme.setOnClickListener {

            when (radioGroup.checkedRadioButtonId) {
                R.id.radioDark -> {
                    darkMode = true
                    changeTheme(Helper.getDataUserId(this), darkMode)
                }

                R.id.radioNight -> {
                    darkMode = false
                    changeTheme(Helper.getDataUserId(this), darkMode)
                }
            }
        }

        sharedPreferences = this.getSharedPreferences("changeTheme", Context.MODE_PRIVATE)
        when (sharedPreferences.getInt("theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)) {
            1 -> binding.radioNight.isChecked = true
            2 -> binding.radioDark.isChecked = true
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

    private fun changeTheme(userId: String, darkMode: Boolean) {
        lifecycleScope.launch {
            try {
                showLoading(resources.getString(R.string.changing_your_pass))
                val body = JsonObject().apply {
                    add("setting", JsonObject().apply {
                        addProperty("darkMode", darkMode)
                        addProperty("notification", true)
                    })
                }
                val result = apiService.updateUserInfoNoImg(userId, body)
                if (result.isSuccessful) {
                    result.body().let {
                        if (it != null) {
                            if (it.setting?.darkMode == true) {
                                setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                            } else {
                                setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                            }
                            UserM.setDataSettings(it)
                        }
                    }
                    CustomToast(this@ChangeTheme).makeText(
                        this@ChangeTheme,
                        resources.getString(R.string.change_email_success),
                        CustomToast.LONG,
                        CustomToast.SUCCESS
                    ).show()
                } else {
                    result.errorBody()?.let {
                        // Show your CustomToast or handle the error as needed
                        CustomToast(this@ChangeTheme).makeText(
                            this@ChangeTheme,
                            it.toString(),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@ChangeTheme).makeText(
                    this@ChangeTheme,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } finally {
                progressDialog.dismiss()
            }
        }
    }


    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this, null, msg)
    }
}