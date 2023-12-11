package com.example.appquizlet

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appquizlet.MainActivity_Logged_In
import com.example.appquizlet.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

private lateinit var binding: ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val i = Intent(this, SplashActivity::class.java)
        startActivity(i)

        sharedPreferences = this.getSharedPreferences("ChangeLanguage", Context.MODE_PRIVATE)
        var mylang = sharedPreferences.getString("language", "en")
        updateLocale(Locale(mylang))

        binding.btnSlash.setOnClickListener {
            var intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
        binding.btnLoggedIn.setOnClickListener {
            var intent = Intent(this, MainActivity_Logged_In::class.java)
            startActivity(intent)
        }
        binding.btn404.setOnClickListener {
            var intent = Intent(this, NotFound::class.java)
            startActivity(intent)
        }

        if (isLoggedIn(this)) {
            loadDataAfterLogin()
        } else {
            startActivity(Intent(this, SignIn::class.java))
        }

    }

    private fun loadDataAfterLogin() {
        // Hiển thị hoặc ẩn ProgressBar nếu cần
//        showProgressBar()
        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                val result = fetchDataFromServer()
//
//                // Xử lý kết quả tải dữ liệu
//                handleData(result)
//                startActivity(Intent(this@MainActivity, MainActivity_Logged_In::class.java))
//                finish()  // Đóng Activity hiện tại nếu cần
//
//            } catch (e: Exception) {
//                // Xử lý lỗi nếu cần thiết
//                showErrorDialog(e.message ?: "Unknown error")
//            } finally {
//                // Ẩn ProgressBar nếu cần
//                hideProgressBar()
//            }
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
    }

    private fun isLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

}