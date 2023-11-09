package com.example.appquizlet

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Patterns
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.setPadding
import com.example.appquizlet.databinding.ActivitySignInBinding
import java.util.regex.Pattern


private lateinit var binding: ActivitySignInBinding

class SignIn : AppCompatActivity(), View.OnFocusChangeListener, View.OnKeyListener,
    View.OnClickListener {
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{6,}" +  // at least 8 characters
                "$"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtLayout1.onFocusChangeListener = this
        binding.txtLayout2.onFocusChangeListener = this
        binding.edtEmail.onFocusChangeListener = this
        binding.edtPass.onFocusChangeListener = this

        //        set toolbar back display
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại
// Tắt tiêu đề của Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)
//Spannable forgot username or pass
        val username = binding.txtForgotUsernameOrPass
        val textForgot = resources.getString(R.string.forgot_u_p)
        val spannableStringBuilderForgotUser = SpannableStringBuilder(textForgot)
        val forgotUserNameClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@SignIn, "ffff", Toast.LENGTH_SHORT).show()
                showCustomDialog(
                    resources.getString(R.string.forgot_username),
                    "",
                    "Enter email address"
                )
            }
        }
        val forgotPassClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(this@SignIn, "ffff", Toast.LENGTH_SHORT).show()
                showCustomDialog(
                    resources.getString(R.string.reset_password),
                    resources.getString(R.string.forgot_pass_text),
                    "Username or email address"
                )
            }
        }

        val indexOfForgotUsername = textForgot.indexOf("username")
        val indexOfForgotPass = textForgot.indexOf("password")

        spannableStringBuilderForgotUser.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfForgotUsername,
            indexOfForgotUsername + "username".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringBuilderForgotUser.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfForgotPass,
            indexOfForgotPass + "password".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
// Thay đổi màu chữ cho "username" và "password"
        val colorForgot = Color.BLUE // Chọn màu mong muốn
        spannableStringBuilderForgotUser.setSpan(
            ForegroundColorSpan(colorForgot),
            indexOfForgotUsername,
            indexOfForgotUsername + "username".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStringBuilderForgotUser.setSpan(
            ForegroundColorSpan(colorForgot),
            indexOfForgotPass,
            indexOfForgotPass + "password".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        // Áp dụng ClickableSpan cho "Terms of Services" và "Privacy Policy"

        spannableStringBuilderForgotUser.setSpan(
            forgotUserNameClickableSpan,
            indexOfForgotUsername,
            indexOfForgotUsername + "username".length,
            0
        )
        spannableStringBuilderForgotUser.setSpan(
            forgotPassClickableSpan, indexOfForgotPass, indexOfForgotPass + "password".length, 0
        )

        username.text = spannableStringBuilderForgotUser
        username.movementMethod = LinkMovementMethod.getInstance()


//      Spannable text
        val termsTextView = binding.txtTermsOfService
        val text =
            resources.getString(R.string.terms_of_service)// Tìm vị trí của các từ "Terms of Services" và "Privacy Policy" trong văn bản
        val spannableStringBuilder = SpannableStringBuilder(text)

        // Tùy chỉnh màu và font chữ cho "Terms of Services"
        val termsOfServiceClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào "Terms of Services"
                Toast.makeText(this@SignIn, "Bấm vào Terms of Services", Toast.LENGTH_SHORT).show()

                // Chuyển đến trang web của "Terms of Services" (hoặc trang Activity tùy thuộc vào nhu cầu của bạn)
                val termsOfServiceUrl = "https://quizlet.com/tos"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(termsOfServiceUrl))
                startActivity(intent)
            }
        }
        // Tùy chỉnh màu và font chữ cho "Privacy Policy"
        val privacyPolicyClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào "Privacy Policy"
                Toast.makeText(this@SignIn, "Bấm vào Privacy Policy", Toast.LENGTH_SHORT).show()

                // Chuyển đến trang web của "Privacy Policy" (hoặc trang Activity tùy thuộc vào nhu cầu của bạn)
                val privacyPolicyUrl = "https://quizlet.com/privacy"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl))
                startActivity(intent)
            }
        }


        val indexOfTerms = text.indexOf("Terms of Services")
        val indexOfPrivacyPolicy = text.indexOf("Privacy Policy")

        // Thay đổi font chữ (đặt kiểu đậm) cho "Terms of Services"
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfTerms,
            indexOfTerms + "Terms of Services".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Thay đổi font chữ (đặt kiểu đậm) cho "Privacy Policy"
        spannableStringBuilder.setSpan(
            StyleSpan(Typeface.BOLD),
            indexOfPrivacyPolicy,
            indexOfPrivacyPolicy + "Privacy Policy".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

// Thay đổi màu chữ cho "Terms of Services" và "Privacy Policy"
        val color = Color.BLUE // Chọn màu mong muốn
        spannableStringBuilder.setSpan(
            ForegroundColorSpan(color),
            indexOfTerms,
            indexOfTerms + "Terms of Services".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannableStringBuilder.setSpan(
            ForegroundColorSpan(color),
            indexOfPrivacyPolicy,
            indexOfPrivacyPolicy + "Privacy Policy".length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )


        // Áp dụng ClickableSpan cho "Terms of Services" và "Privacy Policy"

        spannableStringBuilder.setSpan(
            termsOfServiceClickableSpan,
            indexOfPrivacyPolicy,
            indexOfPrivacyPolicy + "Privacy Policy".length,
            0
        )
        spannableStringBuilder.setSpan(
            privacyPolicyClickableSpan, indexOfTerms, indexOfTerms + "Terms of Services".length, 0
        )
        // Đặt SpannableStringBuilder vào TextView và đặt movementMethod để kích hoạt tính năng bấm vào liên kết
        termsTextView.text = spannableStringBuilder
        termsTextView.movementMethod = LinkMovementMethod.getInstance()
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
//            builder.setMessage(content)
            val textContent = TextView(this)
            textContent.setText(content)
            textContent.setPadding(10, 0, 10, 0)
            layout.addView(textContent)
        }
        // Tạo EditText
        val editText = EditText(this)
        editText.hint = edtPlaceholder
        layout.addView(editText)

        builder.setView(layout)

        builder.setPositiveButton("OK") { dialog, _ ->
            val inputText = editText.text.toString()
            // Xử lý dữ liệu từ EditText sau khi người dùng nhấn OK
            // Ví dụ: Hiển thị nó hoặc thực hiện các tác vụ khác
            // ở đây
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            // Xử lý khi người dùng nhấn Cancel
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
                        validateEmail()
                    }
                }

                R.id.edtPass -> {
                    if (hasFocus) {
                        if (binding.txtLayout2.isErrorEnabled) {
                            binding.txtLayout2.isErrorEnabled = false
                        }
                    } else {
                        validatePass()
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

    private fun validateEmail() : Boolean {
        var errorMess: String? = null
        val email = binding.edtEmail.text.toString().trim()
        if (email.isEmpty()) {
            errorMess = resources.getString(R.string.errBlankEmail)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

    private fun validatePass() : Boolean{
        var errorMess: String? = null
        var pass = binding.edtEmail.text.toString().trim()
        if (pass.isEmpty()) {
            errorMess = resources.getString(R.string.errBlankEmail)
        } else if (!PASSWORD_PATTERN.matcher(pass).matches()) {
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
}
