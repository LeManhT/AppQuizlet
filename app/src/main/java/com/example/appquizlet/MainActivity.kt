package com.example.appquizlet

import android.app.Activity
import android.app.KeyguardManager
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private val REQUEST_CODE_LOCK = 1
    private var username: String = ""
    private var password: String = ""
    private lateinit var dialogEnterPassword: androidx.appcompat.app.AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        sharedPreferences = this.getSharedPreferences("ChangeLanguage", Context.MODE_PRIVATE)
        val mylang = sharedPreferences.getString("language", "en")
        updateLocale(Locale(mylang))
        val userData = Helper.getUserDataSecurely(this)
        username = (userData["userName"] as String?).toString()
        password = (userData["password"] as String?).toString()

        sharedPreferencesTheme = this.getSharedPreferences("changeTheme", Context.MODE_PRIVATE)

        when (sharedPreferencesTheme.getInt("theme", -1)) {
            1 -> setThemeMode(AppCompatDelegate.MODE_NIGHT_NO)
            2 -> setThemeMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> setThemeMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }

        try {
            val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
            if (!keyStore.containsAlias("BiometricKeyAlias")) {
                createKey()
            }
        } catch (e: Exception) {
            Log.e("KeyStoreError", "Error accessing or creating key: ${e.message}")
        }

        if (username.isNotEmpty()) {
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
            val accessToken = Helper.getAccessToken(this)
            val biometricManager = BiometricManager.from(this)
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
                authenticateWithBiometric()
            } else {
                if (isDeviceLockSet()) {
                    requestPinOrPattern()
                } else {
                    loginUser(username, password)
//                    showPasswordDialog { isPasswordCorrect ->
//                        run {
//                            if (isPasswordCorrect) {
//                                loginUser(username, password)
//                            } else {
//                                val intent = Intent(this, SignInActivity::class.java)
//                                startActivity(intent)
//                                finish()
//                            }
//                        }
//                    }
                }
                Log.e("BiometricAuth", "Thiết bị không hỗ trợ sinh trắc học.")
//                if (accessToken.isNullOrEmpty()) {
//                    // Nếu không có token, yêu cầu người dùng đăng nhập
//                    val intent = Intent(this@MainActivity, SplashActivity::class.java)
//                    startActivity(intent)
//                } else {
//                    loginUserWithToken(accessToken)
//                }
            }
        } else {
            val i = Intent(this@MainActivity, SplashActivity::class.java)
            startActivity(i)
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
                            it.user.streak?.let { it1 -> it.user.achievement?.let { it2 ->
                                DetectContinueModel(it1,
                                    it2
                                )
                            } }
                                ?.let { it2 ->
                                    UserM.setDataAchievements(
                                        it2
                                    )
                                }
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

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_LOCK) {
            if (resultCode == Activity.RESULT_OK) {
                loginUser(username, password)
            } else {
                showPasswordDialog { isPasswordCorrect ->
                    run {
                        if (isPasswordCorrect) {
                            loginUser(username, password)
                        } else {
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
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
                            handleBiometricCancel()
                        }

                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
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


    private fun ByteArray.toBase64(): String {
        return Base64.encodeToString(this, Base64.DEFAULT)
    }

    private fun String.fromBase64(): ByteArray {
        return Base64.decode(this, Base64.DEFAULT)
    }

    private fun handleBiometricCancel() {
        if (isDeviceLockSet()) {
            requestPinOrPattern()
        } else {
            val intent = Intent(this, SplashActivity::class.java)
            startActivity(intent)
        }
        CustomToast(this).makeText(
            this,
            "Bạn đã hủy xác thực sinh trắc học.",
            CustomToast.SHORT,
            CustomToast.WARNING
        ).show()
    }

    // Sử dụng JWT để xác thực người dùng
    private fun loginUserWithToken(token: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            showLoading(resources.getString(R.string.logging_in))
            try {
                val result = apiService.loginWithToken("Bearer $token")
                if (result.isSuccessful) {
                    result.body().let { it ->
                        if (it != null) {
                            Helper.saveAccessToken(this@MainActivity, it.accessToken)
                            UserM.setUserData(it.user)
                            it.user.streak?.let { it1 -> it.user.achievement?.let { it2 ->
                                DetectContinueModel(it1,
                                    it2
                                )
                            } }
                                ?.let { it2 ->
                                    UserM.setDataAchievements(
                                        it2
                                    )
                                }
                        }
                    }
                    val intent =
                        Intent(this@MainActivity, MainActivity_Logged_In::class.java)
                    startActivity(intent)
                } else {
                    val intent = Intent(this@MainActivity, SplashActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                val intent = Intent(this@MainActivity, SplashActivity::class.java)
                startActivity(intent)
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun isDeviceLockSet(): Boolean {
        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isKeyguardSecure
    }

    private fun requestPinOrPattern() {
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (keyguardManager.isKeyguardSecure) {
            val intent = keyguardManager.createConfirmDeviceCredentialIntent(
                "Authentication",
                "Please enter pin code or pattern"
            )
            startActivityForResult(intent, REQUEST_CODE_LOCK)
        }
    }

    private fun showPasswordDialog(callback: (Boolean) -> Unit) {
        val view = LayoutInflater.from(this).inflate(R.layout.type_pass_dialog, null)

        val edtPassword = view.findViewById<EditText>(R.id.edtPassword)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)

        val dialogBuilder = MaterialAlertDialogBuilder(this)
            .setView(view)
//            .setCancelable(false)

        btnSubmit.setOnClickListener {
            val txtCheckPass = edtPassword.text.toString()
            lifecycleScope.launch {
                showLoading(resources.getString(R.string.checking_pass))
                try {
                    val accessToken = Helper.getAccessToken(this@MainActivity)
                    if (accessToken.isNullOrEmpty()) {
                        Log.e("AuthError", "Access Token is missing")
                        return@launch
                    }
                    val authorizationHeader = "Bearer ${accessToken.trim()}"
                    val result = apiService.verifyUser(
                        authorizationHeader,
                        Helper.getDataUserId(this@MainActivity),
                        txtCheckPass
                    )
                    if (result.isSuccessful) {
                        callback(true)
                        dialogEnterPassword.dismiss()
                    } else {
                        callback(false)
                    }
                } catch (e: Exception) {
                    callback(false)
                } finally {
                    progressDialog.dismiss()
                }
            }
        }

        dialogEnterPassword = dialogBuilder.create()
        dialogEnterPassword.show()
    }
}