package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityMainBinding
import com.example.appquizlet.model.DetectContinueModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.Locale

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiService: ApiService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        sharedPreferences = this.getSharedPreferences("ChangeLanguage", Context.MODE_PRIVATE)
        val mylang = sharedPreferences.getString("language", "en")
        updateLocale(Locale(mylang))

        val sharedPreferences = this.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("key_username", "")
        val password = sharedPreferences.getString("key_userPass", "")

        if (username?.isNotEmpty() == true && password?.isNotEmpty() == true) {
            loginUser(username, password)
        } else {
            val i = Intent(this, SplashActivity::class.java)
            startActivity(i)
        }
    }

    private fun updateLocale(locale: Locale) {
        val config = resources.configuration
        Locale.setDefault(locale)
        config.locale = locale

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
            createConfigurationContext(config)
        }
        resources.updateConfiguration(config, resources.displayMetrics)
        val nativeDisplayLanguage = locale.getDisplayLanguage(locale)
        val sharedPreferences = this.getSharedPreferences("languageChoose", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("languageDisplay", nativeDisplayLanguage).apply()
    }

    private fun loginUser(email: String, pass: String) {
        lifecycleScope.launch {
            showLoading(resources.getString(R.string.logging_in))
            try {
                val body = JsonObject().apply {
                    addProperty(resources.getString(R.string.loginNameField), email)
                    addProperty(resources.getString(R.string.loginPasswordField), pass)
                }
                val result = apiService.loginUser(body)
                if (result.isSuccessful) {
//                    val msgSuccess = resources.getString(R.string.login_success)
                    result.body().let { it ->
                        if (it != null) {
//                            CustomToast(this@MainActivity).makeText(
//                                this@MainActivity,
//                                msgSuccess,
//                                CustomToast.LONG,
//                                CustomToast.SUCCESS
//                            ).show()
                            UserM.setUserData(it)
                            UserM.setDataAchievements(
                                DetectContinueModel(it.streak, it.achievement)
                            )
                            Helper.updateAppTheme(it.setting.darkMode)

                        }
                    }
                    val intent =
                        Intent(this@MainActivity, MainActivity_Logged_In::class.java)
                    startActivity(intent)

                } else {
                    result.errorBody()?.string()?.let {
                        CustomToast(this@MainActivity).makeText(
                            this@MainActivity,
                            it,
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    }
                    val intent = Intent(this@MainActivity, SplashActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                CustomToast(this@MainActivity).makeText(
                    this@MainActivity,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
                val intent = Intent(this@MainActivity, SplashActivity::class.java)
                startActivity(intent)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this@MainActivity, null, msg)
    }


}