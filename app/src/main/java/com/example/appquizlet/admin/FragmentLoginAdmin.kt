package com.example.appquizlet.admin

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.view.View.OnFocusChangeListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.appquizlet.AdminActivity
import com.example.appquizlet.R
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.FragmentLoginAdminBinding
import com.example.appquizlet.viewmodel.admin.AdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.regex.Pattern

@AndroidEntryPoint
class FragmentLoginAdmin : AppCompatActivity(), OnFocusChangeListener {
    private lateinit var binding: FragmentLoginAdminBinding
    private val adminViewModel: AdminViewModel by viewModels()
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{6,}" +  // at least 8 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLoginAdminBinding.inflate(layoutInflater)

        setContentView(binding.root)
        binding.txtLayout1.onFocusChangeListener = this
        binding.txtLayout2.onFocusChangeListener = this
        binding.edtEmail.onFocusChangeListener = this
        binding.edtPass.onFocusChangeListener = this
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val pass = binding.edtPass.text.toString()
            if (validateEmail(email) && validatePass(pass)) {
                showLoading()
                adminViewModel.loginAdmin(email, pass)
            }
        }

        adminViewModel.loginAdminResult.observe(this) { result ->
            hideLoading()
            result.onSuccess {
                val intent = Intent(this, AdminActivity::class.java)
                startActivity(intent)
            }.onFailure {
                Timber.e(it, "Login admin failed")
                CustomToast(this).makeText(
                    this,
                    resources.getString(R.string.login_admin_failed),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
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

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.visibility = View.VISIBLE
    }

}