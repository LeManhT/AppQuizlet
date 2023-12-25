package com.example.appquizlet

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivityChangePasswordBinding
import com.example.appquizlet.model.UserM
import com.example.appquizlet.util.Helper
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import kotlinx.coroutines.launch

class Change_Password : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var apiService: ApiService
    private lateinit var progressDialog: ProgressDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar_change_password))


        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

        val curPass = intent.getStringExtra("currentPass")

        binding.txtSave.setOnClickListener {
            val currentPass =
                binding.edtCurrentPassword.text.toString()
            val newPass = binding.edtNewPassword.text.toString()
            val confirmPass =
                binding.edtConfirmYourPassword.text.toString()
            if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                CustomToast(this@Change_Password).makeText(
                    this@Change_Password,
                    resources.getString(R.string.cannot_empty_field),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            } else {
                Log.d("ggg", "$newPass $confirmPass $currentPass")
                if (currentPass != curPass) {
                    CustomToast(this@Change_Password).makeText(
                        this@Change_Password,
                        resources.getString(R.string.current_pass_incorrect),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                } else if (newPass != confirmPass) {
                    CustomToast(this@Change_Password).makeText(
                        this@Change_Password,
                        resources.getString(R.string.pass_not_equal_confirm),
                        CustomToast.LONG,
                        CustomToast.ERROR
                    ).show()
                } else {
                    changePassword(
                        Helper.getDataUserId(this),
                        currentPass,
                        newPass
                    )
                }
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

    private fun changePassword(userId: String, oldPass: String, newPass: String) {
        lifecycleScope.launch {
            try {
                showLoading(resources.getString(R.string.changing_your_pass))
                val body = JsonObject().apply {
                    addProperty("oldPassword", oldPass)
                    addProperty("newPassword", newPass)
                }
                val result = apiService.changePassword(userId, body)
                if (result.isSuccessful) {
                    result.body().let {
                        Log.d("ggg5",Gson().toJson(it))
                        if (it != null) {
                            UserM.setUserData(it)
                        }
                    }
                    CustomToast(this@Change_Password).makeText(
                        this@Change_Password,
                        resources.getString(R.string.change_pass_successful),
                        CustomToast.LONG,
                        CustomToast.SUCCESS
                    ).show()
                } else {
                    result.errorBody()?.let {
                        // Show your CustomToast or handle the error as needed
                        CustomToast(this@Change_Password).makeText(
                            this@Change_Password,
                            "eeeeee",
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    }
                }
            } catch (e: Exception) {
                CustomToast(this@Change_Password).makeText(
                    this@Change_Password,
                    e.message.toString(),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
                Log.e("ggg4",  e.message.toString())
            } finally {
                progressDialog.dismiss()
            }
        }
    }

    private fun showLoading(msg: String) {
        progressDialog = ProgressDialog.show(this, null, msg)
    }
}