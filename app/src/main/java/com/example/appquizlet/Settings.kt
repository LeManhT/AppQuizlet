package com.example.appquizlet

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import com.example.appquizlet.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    lateinit var dialog_change_username: AlertDialog
    lateinit var dialog_update_email: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(findViewById(R.id.toolbar))    // p co dong nay moi chay dc ham ơ dưới

        binding.layoutChangeLanguage.setOnClickListener {
            val i = Intent(this, ChangeLanguage::class.java)
            startActivity(i)
        }

        binding.layoutChangeTheme.setOnClickListener {
            val i = Intent(this, ChangeTheme::class.java)
            startActivity(i)
        }

        val ChangeUsername = findViewById<LinearLayout>(R.id.change_username)
        ChangeUsername.setOnClickListener {
            showDialogChangeUsername()
        }

        val ChangeEmail = findViewById<LinearLayout>(R.id.change_email)
        ChangeEmail.setOnClickListener {
            showDialogChangeEmail()
        }

        val ChangePassword = findViewById<LinearLayout>(R.id.change_password)
        ChangePassword.setOnClickListener {
            val Ch_pw = Intent(this, Change_Password::class.java)
            startActivity(Ch_pw)
        }

    }


    private fun showDialogChangeEmail() {
        val build2 = AlertDialog.Builder(this)
        val view2 = layoutInflater.inflate(R.layout.activity_update_email, null)
        build2.setView(view2)

        val btn_cancel_update_email =
            view2.findViewById<AppCompatButton>(R.id.btn_cancel_update_email)
        btn_cancel_update_email.setOnClickListener {
            dialog_update_email.dismiss()
        }

        dialog_update_email = build2.create()
        dialog_update_email.show()
    }

    private fun showDialogChangeUsername() {
        val build = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.activity_change_username, null)
        build.setView(view)

        val btn_cancel_change_username =
            view.findViewById<AppCompatButton>(R.id.btn_cancel_change_username)
        btn_cancel_change_username.setOnClickListener {
            dialog_change_username.dismiss()
        }

//        val btn_send_changeusername = view.findViewById<AppCompatButton>(R.id.btn_send_change_username)
//            btn_send_changeusername.setOnClickListener {
//                val btn_send_username = Intent(this,Ch_username::class.java)
//                startActivity(btn_send_username)
//            }


        dialog_change_username = build.create()
        dialog_change_username.show()

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

}