package com.example.appquizlet

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.example.appquizlet.api.retrofit.ApiService
import com.example.appquizlet.api.retrofit.RetrofitHelper
import com.example.appquizlet.custom.CustomToast
import com.example.appquizlet.databinding.ActivitySignUpBinding
import com.google.gson.JsonObject
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.regex.Pattern


class SignUp : AppCompatActivity(), View.OnClickListener, View.OnFocusChangeListener,
    View.OnKeyListener {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var calendar: Calendar
    private val PASSWORD_PATTERN: Pattern = Pattern.compile(
        "^" +
                "(?=.*[@#$%^&+=])" +  // at least 1 special character
                "(?=\\S+$)" +  // no white spaces
                ".{6,}" +  // at least 8 characters
                "$"
    )

    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.txtLayout1.onFocusChangeListener = this
        binding.txtLayout2.onFocusChangeListener = this
        binding.edtEmail.onFocusChangeListener = this
        binding.edtPass.onFocusChangeListener = this

        apiService = RetrofitHelper.getInstance().create(ApiService::class.java)

//        set toolbar back display
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại
// Tắt tiêu đề của Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        Date dialog
        val edtDOB = binding.edtDOB

        // Khởi tạo Calendar
        calendar = Calendar.getInstance()

        // Định dạng ngày
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = dateFormat.format(Calendar.getInstance().time) // Lấy ngày hiện tại
        binding.edtDOB.setText(currentDate)

        // Sự kiện khi EditText được nhấn
        edtDOB.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _: DatePicker?, year: Int, month: Int, day: Int ->
                    // Xử lý khi người dùng chọn ngày
                    calendar.set(year, month, day)
                    val formattedDate = dateFormat.format(calendar.time)
                    edtDOB.setText(formattedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.show()

        }
//      Spannable text
        val termsTextView = binding.txtTermsOfService
        val text =
            resources.getString(R.string.terms_of_service)// Tìm vị trí của các từ "Terms of Services" và "Privacy Policy" trong văn bản
        val spannableStringBuilder = SpannableStringBuilder(text)

        // Tùy chỉnh màu và font chữ cho "Terms of Services"
        val termsOfServiceClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào "Terms of Services"
                Toast.makeText(this@SignUp, "Bấm vào Terms of Services", Toast.LENGTH_SHORT).show()

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
                Toast.makeText(this@SignUp, "Bấm vào Privacy Policy", Toast.LENGTH_SHORT).show()

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


        binding.btnSignUpForm.setOnClickListener {
            val edtEmail = binding.edtEmail.text.toString()
            val edtPass = binding.edtPass.text.toString()
            if (validateEmail(edtEmail) && validatePassword(edtPass)) {
                createNewUser(edtEmail, edtPass)
            } else {
                CustomToast(this).makeText(
                    this,
                    resources.getString(R.string.failed_sign_up),
                    CustomToast.LONG,
                    CustomToast.ERROR
                ).show()
            }
        }
    }


    private fun validateEmail(email: String): Boolean {
        var errorMessage: String? = null
        if (email.trim().isEmpty()) {
            errorMessage = resources.getString(R.string.errBlankEmail)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            errorMessage = resources.getString(R.string.errEmailInvalid)
        }
        if (errorMessage != null) {
            binding.txtLayout1.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun validatePassword(pass: String): Boolean {
        var errorMessage: String? = null
        if (pass.trim().isEmpty()) {
            errorMessage = resources.getString(R.string.errBlankPass)
        } else if (!PASSWORD_PATTERN.matcher(pass.trim()).matches()) {
            errorMessage = resources.getString(R.string.errInsufficientLength)
        }
        if (errorMessage != null) {
            binding.txtLayout2.apply {
                isErrorEnabled = true
                error = errorMessage
            }
        }
        return errorMessage == null
    }

    private fun createNewUser(email: String, pass: String) {
        lifecycleScope.launch {
            showLoading()

            try {
                val body = JsonObject().apply {
                    addProperty(resources.getString(R.string.loginNameField), email)
                    addProperty(resources.getString(R.string.loginPasswordField), pass)
                }
                val result = apiService.createUser(body)
                Toast.makeText(this@SignUp, body.toString(), Toast.LENGTH_SHORT).show()
                if (result.isSuccessful) {
                    val msgSignInSuccess = resources.getString(R.string.sign_in_success)
                    val intent = Intent(this@SignUp, SignIn::class.java)
                    startActivity(intent)
                    CustomToast(this@SignUp).makeText(
                        this@SignUp,
                        msgSignInSuccess,
                        CustomToast.LONG,
                        CustomToast.SUCCESS
                    ).show()
                } else {
                    result.errorBody()?.string()?.let {
                        CustomToast(this@SignUp).makeText(
                            this@SignUp,
                            it,
                            CustomToast.LONG,
                            CustomToast.ERROR
                        ).show()
                    }

                }
            } catch (e: IOException) {
                Log.e("IOException", e.message.toString())
            } catch (e: HttpException) {
                Log.e("HttpException", e.message.toString())
            } catch (e: Exception) {
                Log.e("Exception", e.message.toString())
            } finally {
                hideLoading()
            }

        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSignUpForm.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSignUpForm.visibility = View.VISIBLE
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v?.id != null) {
            when (v.id) {
//                R.id.edtDOB -> {
//                    if(hasFocus){
//                        binding.txtLayout0.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
//                            // Xử lý dữ liệu khi người dùng đã chọn ngày mới
//                            val selectedDate = "$dayOfMonth/${monthOfYear + 1}/$year"
//                        }
//                    }
//                }

                R.id.edtEmail -> {
                    if (hasFocus) {
                        //isCounterEnabled được đặt thành true, thì trường nhập liệu hoặc widget đó sẽ theo dõi và hiển thị số ký tự mà người dùng đã nhập vào
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
                        validatePassword(binding.edtPass.text.toString())
                    }
                }
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        TODO("Not yet implemented")
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