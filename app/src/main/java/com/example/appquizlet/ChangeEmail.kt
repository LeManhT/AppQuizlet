package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityChangeEmailBinding
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.JsonObject
import kotlinx.coroutines.launch

class ChangeEmail : AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)


        binding.btnChangeEmail.setOnClickListener {
            if (binding.edtEmailChange.text?.isEmpty() == true) {
                CustomToast(this@ChangeEmail).makeText(
                    this@ChangeEmail,
                    resources.getString(R.string.cannot_empty_field),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else {
                val newEmail = binding.edtEmailChange.text.toString()
                changeEmail(Helper.getDataUserId(this), newEmail)
            }
        }


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

    private fun changeEmail(userId: String, newEmail: String) {
        lifecycleScope.launch {
            try {
                showLoading(resources.getString(R.string.changing_your_pass))
                val body = JsonObject().apply {
                    addProperty("email", newEmail)
                }
                val result = apiService.updateUserInfoNoImg(userId, body)
                if (result.isSuccessful) {
                    result.body().let {
                        if (it != null) {
                            UserM.setDataSettings(it)
                        }
                    }
                    CustomToast(this@ChangeEmail).makeText(
                        this@ChangeEmail,
                        resources.getString(R.string.change_email_success),
                        CustomToast.LONG,
                        CustomToast.SUCCESS
                    ).show()

                    val i = Intent(this@ChangeEmail, Settings::class.java)
                    startActivity(i)
                } else {
                    result.errorBody()?.let {
                        // Show your CustomToast or handle the error as needed
                        CustomToast(this@ChangeEmail).makeText(
                            this@ChangeEmail,
                            it.toString(),
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@ChangeEmail).makeText(
                    this@ChangeEmail,
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