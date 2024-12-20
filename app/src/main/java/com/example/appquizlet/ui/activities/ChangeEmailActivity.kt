package com.example.appquizlet.ui.activities

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.R
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityChangeEmailBinding
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class ChangeEmailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        val currentEmail = intent.getStringExtra("currentEmail")

        binding.btnChangeEmail.setOnClickListener {
            if (binding.edtEmailChange.text?.isEmpty() == true) {
                CustomToast(this@ChangeEmailActivity).makeText(
                    this@ChangeEmailActivity,
                    resources.getString(R.string.cannot_empty_field),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else {
                val newEmail = binding.edtEmailChange.text.toString()
                if (newEmail == currentEmail) {
                    CustomToast(this).makeText(
                        this,
                        resources.getString(R.string.your_current_mail_concide),
                        CustomToast.LONG,
                        CustomToast.WARNING
                    ).show()
                } else {
                    changeEmail(this,Helper.getDataUserId(this), newEmail)
                }
            }
        }

        binding.txtBack.setOnClickListener {
            finish()
        }


    }

    private fun changeEmail(context: Context, userId: String, newEmail: String) {
        lifecycleScope.launch {
            try {
                showLoading(resources.getString(R.string.changing_your_email))
                val body = JsonObject().apply {
                    addProperty("email", newEmail)
                }
                val accessToken = Helper.getAccessToken(context)
                if(accessToken.isNullOrEmpty()) {
                    Log.d("AccessTokenLog : ", "Access token is null")
                }
                val result = apiService.updateUserInfoNoImg(
                    authorization = "Bearer $accessToken",
                    userId,
                    body
                )
                if (result.isSuccessful) {
                    result.body().let {
                        if (it != null) {
                            UserM.setDataSettings(it)
                        }
                    }
                    CustomToast(this@ChangeEmailActivity).makeText(
                        this@ChangeEmailActivity,
                        resources.getString(R.string.change_email_success),
                        CustomToast.LONG,
                        CustomToast.SUCCESS
                    ).show()

                    val i = Intent(this@ChangeEmailActivity, Settings::class.java)
                    startActivity(i)
                } else {
                    result.errorBody()?.let {
                        // Show your CustomToast or handle the error as needed
                        CustomToast(this@ChangeEmailActivity).makeText(
                            this@ChangeEmailActivity,
                            it.toString(),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@ChangeEmailActivity).makeText(
                    this@ChangeEmailActivity,
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