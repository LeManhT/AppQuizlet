package com.example.appquizlet

import android.app.DatePickerDialog
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
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.appquizlet.databinding.ActivitySignUpBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

private lateinit var binding: ActivitySignUpBinding
private lateinit var calendar: Calendar

class SignUp : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        Khoi tao viewbinding
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        set toolbar back display
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // Hiển thị biểu tượng quay lại
// Tắt tiêu đề của Action Bar
        supportActionBar?.setDisplayShowTitleEnabled(false)

//        Date dialog
        var edtDOB = binding.edtDOB

        // Khởi tạo Calendar
        calendar = Calendar.getInstance()

        // Định dạng ngày
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        // Sự kiện khi EditText được nhấn
        edtDOB.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { _: DatePicker?, year: Int, month: Int, day: Int ->
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