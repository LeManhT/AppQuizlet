package com.example.appquizlet

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivitySignInBinding
import com.example.appquizlet.model.DetectContinueModel
import com.example.appquizlet.model.UserM
import com.example.appquizlet.model.UserViewModel
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import java.util.regex.Pattern


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
                            Helper.saveAccessToken(this@SignInActivity, it.accessToken)
                            saveIdUser(it.user.id, it.user.loginName, pass, true)
                            UserM.setDataAchievements(
                                DetectContinueModel(it.user.streak, it.user.achievement)
                            )
                            UserM.setUserData(it.user)
                        }
                    }
                    val intent = Intent(this@SignInActivity, MainActivity_Logged_In::class.java)
                    startActivity(intent)
                } else {
                    result.errorBody()?.string()?.let {
                        CustomToast(this@SignInActivity).makeText(
                            this@SignInActivity,
                            it,
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
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

    private fun showCustomDialog(title: String, content: String, edtPlaceholder: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)

        // Tạo layout cho dialog
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(60)
        if (!content.isEmpty()) {
            val textContent = TextView(this)
            textContent.setText(content)
            textContent.setPadding(10, 0, 10, 0)
            layout.addView(textContent)
        }
        val editText = EditText(this)
        editText.hint = edtPlaceholder
        layout.addView(editText)

        builder.setView(layout)

        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
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

    private fun saveIdUser(
        userId: String,
        userName: String,
        password: String,
        isLoggedIn: Boolean
    ) {
        sharedPreferences = this.getSharedPreferences("idUser", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("key_userid", userId)
        editor.putString("key_userPass", password)
        editor.putString("key_username", userName)
        editor.putBoolean("isLoggedIn", isLoggedIn)
        editor.apply()
    }


}
