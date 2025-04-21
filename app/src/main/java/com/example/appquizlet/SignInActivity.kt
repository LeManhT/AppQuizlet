package com.example.appquizlet

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivitySignInBinding
import com.example.appquizlet.model.DetectContinueModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.model.UserViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.math.abs


class SignInActivity : AppCompatActivity(), View.OnFocusChangeListener, View.OnKeyListener,
    View.OnClickListener {
    private lateinit var binding: ActivitySignInBinding
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{6,}" +  // at least 8 characters
                "$"
    )

    private lateinit var apiService: ApiService
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtLayout1.onFocusChangeListener = this
        binding.txtLayout2.onFocusChangeListener = this
        binding.edtEmail.onFocusChangeListener = this
        binding.edtPass.onFocusChangeListener = this


        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnSignin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPass.text.toString()
            if (validateEmail(email) && validatePass(pass)) {
                loginUser(email, pass)
            } else {
                CustomToast(this).makeText(
                    this,
                    resources.getString(R.string.wrong_email_or_pass),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }

    private fun loginUser(email: String, pass: String) {
        lifecycleScope.launch {
            showLoading()
            try {
                val body = JsonObject().apply {
                    addProperty(resources.getString(R.string.loginNameField), email)
                    addProperty(resources.getString(R.string.loginPasswordField), pass)
                }
                val result = apiService.loginUser(body)
                if (result.isSuccessful) {
                    result.body().let { it ->
                        if (it != null) {
//                            Helper.saveAccessToken(this@SignInActivity, it.accessToken)
//                            saveUserDataSecurely(it.user.id, it.user.loginName, pass, true)
                            saveUserDataSecurely(it.user.id, it.user.loginName, pass, true)
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
                            UserM.setUserData(it.user)
                        }
                    }
                    val intent = Intent(this@SignInActivity, MainActivity_Logged_In::class.java)
                    startActivity(intent)
                } else {
                    result.errorBody()?.string()?.let {
                        Log.d("Errrrrrr",it)
                        val errorObject = JsonParser.parseString(it).asJsonObject
                        val resultType = errorObject.get("result_type").asInt
                        val message = errorObject.get("message").asString
                        val tryLoginRemain = errorObject.get("try_login_remain")?.asInt ?: -1
                        val timeSuspendTemp = errorObject.get("time_suspend_temp")?.asLong ?: 0
                        handleLoginFailure(resultType, message, tryLoginRemain, timeSuspendTemp)
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@SignInActivity).makeText(
                    this@SignInActivity,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } finally {
                hideLoading()
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignin.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSignin.visibility = View.VISIBLE
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

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id != null) {
            when (v.id) {
                R.id.edtEmail -> {
                    if (hasFocus) {
                        if (binding.txtLayout1.isErrorEnabled) {
                            binding.txtLayout1.isErrorEnabled = false
                        }
                    } else {
                        validateEmail(binding.edtEmail.text.toString())
                    }
                }

                R.id.edtPass -> {
                    if (hasFocus) {
                        if (binding.txtLayout2.isErrorEnabled) {
                            binding.txtLayout2.isErrorEnabled = false
                        }
                    } else {
                        validatePass(binding.edtPass.text.toString())
                    }
                }
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    private fun validateEmail(email: String): Boolean {
        var errorMess: String? = null
        if (email.trim().isEmpty()) {
            errorMess = resources.getString(R.string.errBlankEmail)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMess = resources.getString(R.string.errEmailInvalid)
        }
        if (errorMess != null) {
            binding.txtLayout1.apply {
                isErrorEnabled = true
                error = errorMess
            }
        }
        return errorMess == null
    }

    private fun validatePass(pass: String): Boolean {
        var errorMess: String? = null
        if (pass.trim().isEmpty()) {
            errorMess = resources.getString(R.string.errBlankEmail)
        } else if (!PASSWORD_PATTERN.matcher(pass.trim()).matches()) {
            errorMess = resources.getString(R.string.errInsufficientLength)
        }
        if (errorMess != null) {
            binding.txtLayout2.apply {
                isErrorEnabled = true
                error = errorMess
            }
        }
        return errorMess == null
    }

    private fun saveUserDataSecurely(
        userId: String,
        userName: String,
        password: String,
        isLoggedIn: Boolean
    ) {
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val encryptedSharedPreferences = EncryptedSharedPreferences.create(
            this,
            "secure_user_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = encryptedSharedPreferences.edit()
        editor.putString("key_userid", userId)
        editor.putString("key_userPass", password)
        editor.putString("key_username", userName)
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }

    private fun handleLoginFailure(resultType: Int, message: String, tryLoginRemain: Int, timeSuspendTemp: Long) {
        Log.d("tryLoginRemain",tryLoginRemain.toString())
        when (tryLoginRemain) {
            1 -> {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.lock_login_warning))
                    .setMessage(resources.getString(R.string.supporting_lock_acc_text))
                    .setCancelable(false)
                    .setNeutralButton(resources.getString(R.string.cancel)) { dialog, which ->

                    }
                    .setPositiveButton(resources.getString(R.string.i_know)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
            0 -> {
                    val timeLeftMillis = abs((timeSuspendTemp * 1000L) - System.currentTimeMillis())
                    Log.d("TimeLeftMillis", "$timeLeftMillis")
                    if (timeLeftMillis > 0) {
                        Log.d("TimeLeftMillis2", "$timeLeftMillis")
                        val dialogBuilder = MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.lock_login_warning))
                            .setCancelable(false)
                            .setPositiveButton(getString(R.string.login_another_account)) { dialog, _ ->
                                dialog.dismiss()
                            }
                            .setNeutralButton(getString(R.string.cancel)) { dialog, _ ->
                                dialog.dismiss()
                            }

                        val countdownTextView = TextView(this).apply {
                            textSize = 18f
                            setPadding(40, 40, 40, 40)
                            gravity = Gravity.CENTER
                        }
                        dialogBuilder.setView(countdownTextView)
                        val alertDialog = dialogBuilder.create()
                        alertDialog.show()

                        object : CountDownTimer(timeLeftMillis, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                val minutesLeft = (millisUntilFinished % 3600000) / 60000
                                val secondsLeft = (millisUntilFinished / 1000) % 60
                                countdownTextView.text = getString(
                                    R.string.your_account_is_locked_please_try_again_after,
                                    minutesLeft,
                                    secondsLeft
                                )
                            }

                            override fun onFinish() {
                                alertDialog.dismiss()
                                CustomToast(this@SignInActivity).makeText(
                                    this@SignInActivity,
                                    getString(R.string.your_account_is_unlock),
                                    CustomToast.LONG,
                                    CustomToast.SUCCESS
                                ).show()
                            }
                        }.start()
                        CustomToast(this@SignInActivity).makeText(
                            this@SignInActivity,
                            getString(
                                R.string.your_account_is_locked_please_try_again_after_minutes,
                                timeLeftMillis.toString()
                            ),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    } else {
                        CustomToast(this@SignInActivity).makeText(
                            this@SignInActivity,
                            getString(R.string.your_account_is_unlock),
                            CustomToast.LONG,
                            CustomToast.WARNING
                        ).show()
                    }
            }
            else -> {
                CustomToast(this@SignInActivity).makeText(
                    this@SignInActivity,
                    message,
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }
}
