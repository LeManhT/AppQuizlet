package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityMainBinding
import com.example.appquizlet.model.DetectContinueModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.ui.activities.SplashActivity
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.KeyStore
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var sharedPreferencesTheme: SharedPreferences
    private lateinit var progressDialog: ProgressDialog
    private lateinit var apiService: ApiService
    private var username: String = ""
    private var password: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        sharedPreferences = this.getSharedPreferences("ChangeLanguage", Context.MODE_PRIVATE)
        val mylang = sharedPreferences.getString("language", "en")
        updateLocale(Locale(mylang))

        val sharedPreferences = this.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        username = sharedPreferences.getString("key_username", "").toString()
        password = sharedPreferences.getString("key_userPass", "").toString()

        sharedPreferencesTheme = this.getSharedPreferences("changeTheme", Context.MODE_PRIVATE)

        when (sharedPreferencesTheme.getInt("theme", -1)) {
            1 -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        // Generate the key if it doesn't exist
        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (!keyStore.containsAlias("BiometricKeyAlias")) {
                createKey()
            }
        } catch (e: Exception) {
            Log.e("KeyStoreError", "Error accessing or creating key: ${e.message}")
        }

        if (username?.isNotEmpty() == true) {
//            val encryptedPassword = getEncryptedPassword()
//            if (encryptedPassword != null) {
//                authenticateWithBiometricForLogin(encryptedPassword)
//            } else if (password?.isNotEmpty() == true) {
//                loginUser(username!!, password!!)
//                saveEncryptedPassword(password!!)
//            } else {
//                val i = Intent(this@MainActivity, SplashActivity::class.java)
//                startActivity(i)
//            }
            val biometricManager = BiometricManager.from(this)
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                authenticateWithBiometric()
            } else {
                loginUser(username, password)
                Log.e("BiometricAuth", "Thiết bị không hỗ trợ sinh trắc học.")
            }
        } else {
            val i = Intent(this@MainActivity, SplashActivity::class.java)
            startActivity(i)
        }
    }

    private fun setThemeModeAsync(themeMode: Int) {
        lifecycleScope.launch(Dispatchers.Main) {
            withContext(Dispatchers.IO) {
                when (themeMode) {
                    1 -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
                    2 -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
                    else -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
            recreate()
        }
    }

    private fun updateLocale(locale: Locale) {
        val config = resources.configuration
        Locale.setDefault(locale)
        config.locale = locale

        resources.updateConfiguration(config, resources.displayMetrics)
        val nativeDisplayLanguage = locale.getDisplayLanguage(locale)
        val sharedPreferences = this.getSharedPreferences("languageChoose", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("languageDisplay", nativeDisplayLanguage).apply()
    }

    private fun loginUser(email: String, pass: String) {
        Log.d("LoginAuto : ", "$email pass : $pass")
        lifecycleScope.launch(Dispatchers.Main) {
            showLoading(resources.getString(R.string.logging_in))
            try {
                val body = JsonObject().apply {
                    addProperty(resources.getString(R.string.loginNameField), email)
                    addProperty(resources.getString(R.string.loginPasswordField), pass)
                }
                val result = apiService.loginUser(body)
                if (result.isSuccessful) {
                    result.body().let { it ->
                        if (it != null) {
                            Helper.saveAccessToken(this@MainActivity, it.accessToken)
                            UserM.setUserData(it.user)
                            UserM.setDataAchievements(
                                DetectContinueModel(it.user.streak, it.user.achievement)
                            )
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
                Log.d("hhehhehe111", e.message.toString())
                val intent = Intent(this@MainActivity, SplashActivity::class.java)
                startActivity(intent)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog =
            ProgressDialog.show(this, resources.getString(R.string.logging_in), msg)
        progressDialog.show()
    }

    private fun setThemeMode(mode: Int) {
        AppCompatDelegate.setDefaultNightMode(mode)
        with(sharedPreferencesTheme.edit()) {
            putInt("theme", mode)
            apply()
        }
    }

    private fun createKey() {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )

        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                "BiometricKeyAlias",
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .setUserAuthenticationRequired(true) // Bắt buộc xác thực sinh trắc học
                .build()
        )
        keyGenerator.generateKey()
    }

    //Khởi tạo đối tượng Cipher từ khóa trong Keystore.
    private fun getCipher(): Cipher {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        val secretKey = keyStore.getKey("BiometricKeyAlias", null) as SecretKey
        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey) // Hoặc DECRYPT_MODE cho giải mã
        return cipher
    }

    private fun authenticateWithBiometricForLogin(encryptedPassword: ByteArray) {
        val cipher = getCipher()
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)

        val biometricPrompt = BiometricPrompt(
            this,
            { command -> runOnUiThread(command) },
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
//                    result.cryptoObject?.cipher?.let { decryptCipher ->
//                        val decryptedPassword = decryptData(encryptedPassword, decryptCipher)
//                        username?.let { loginUser(it, decryptedPassword) }
//                    }
                    loginUser(username, password)
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Log.e("BiometricAuth", "Authentication error: $errString")
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Log.e("BiometricAuth", "Authentication failed")
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Đăng nhập bằng sinh trắc học")
            .setSubtitle("Xác thực vân tay để đăng nhập")
            .setNegativeButtonText("Hủy")
            .build()

        biometricPrompt.authenticate(promptInfo, cryptoObject)
    }

    private fun encryptData(data: String, cipher: Cipher): ByteArray {
        return cipher.doFinal(data.toByteArray(Charsets.UTF_8))
    }

    private fun decryptData(encryptedData: ByteArray, cipher: Cipher): String {
        return String(cipher.doFinal(encryptedData), Charsets.UTF_8)
    }


    private fun authenticateWithBiometric() {
        val cipher = getCipher()
        val cryptoObject = BiometricPrompt.CryptoObject(cipher)

        val biometricPrompt = BiometricPrompt(
            this,
            { command -> runOnUiThread(command) },
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    username.let { password.let { it1 -> loginUser(it, it1) } }
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> {
                            // Người dùng đóng dialog
                            handleBiometricCancel()
                        }

                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                            // Người dùng bấm nút "Hủy"
                            handleBiometricCancel()
                        }

                        else -> {
                            CustomToast(this@MainActivity).makeText(
                                this@MainActivity,
                                "Error: $errString",
                                CustomToast.LONG,
                                CustomToast.ERROR
                            ).show()
                        }
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    CustomToast(this@MainActivity).makeText(
                        this@MainActivity,
                        "Authentication failed, please try again.",
                        CustomToast.SHORT,
                        CustomToast.ERROR
                    ).show()
                    val intent = Intent(this@MainActivity, SplashActivity::class.java)
                    startActivity(intent)
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Xác thực sinh trắc học")
            .setSubtitle("Sử dụng vân tay để đăng nhập")
            .setNegativeButtonText("Hủy")
            .build()

        biometricPrompt.authenticate(promptInfo, cryptoObject)
    }

    private fun saveEncryptedPassword(password: String) {
        val cipher = getCipher()
        val encryptedPassword = encryptData(password, cipher)
        sharedPreferences.edit().putString("encrypted_password", encryptedPassword.toBase64())
            .apply()
    }

    private fun getEncryptedPassword(): ByteArray? {
        val encryptedPasswordBase64 = sharedPreferences.getString("encrypted_password", null)
        return encryptedPasswordBase64?.fromBase64()
    }


    fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.DEFAULT)
    }

    fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.DEFAULT)
    }

    private fun handleBiometricCancel() {
        Log.d("BiometricAuth", "Biometric authentication canceled by user.")
        CustomToast(this).makeText(
            this,
            "Bạn đã hủy xác thực sinh trắc học.",
            CustomToast.SHORT,
            CustomToast.WARNING
        ).show()

        val intent = Intent(this, SplashActivity::class.java)
        startActivity(intent)
    }
}