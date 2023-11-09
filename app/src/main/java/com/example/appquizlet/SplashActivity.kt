package com.example.appquizlet

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
import android.view.View
import com.example.appquizlet.databinding.ActivitySplashBinding

private lateinit var binding: ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //        Khoi tao viewbinding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        chuyen mau chu
        //      Spannable text
        val termsTextView = binding.txtTermsServices
        val text =
            resources.getString(R.string.terms_of_service)// Tìm vị trí của các từ "Terms of Services" và "Privacy Policy" trong văn bản
        val spannableStringBuilder = SpannableStringBuilder(text)

        // Tùy chỉnh màu và font chữ cho "Terms of Services"
        val termsOfServiceClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Xử lý khi người dùng bấm vào "Terms of Services"

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

        val indicators = binding.circleIndicator3
        val listItemPhoto = mutableListOf<PhotoSplash>()
        listItemPhoto.add(
            PhotoSplash(
                R.drawable.splash2,
                resources.getString(R.string.splash_text1)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.drawable.splash4,
                resources.getString(R.string.splash_text2)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.drawable.splash5,
                resources.getString(R.string.splash_text3)
            )
        )
        listItemPhoto.add(
            PhotoSplash(
                R.drawable.splash6,
                resources.getString(R.string.splash_text4)
            )
        )
        val adapterPhotoSlash = PhotoSplashAdapter(listItemPhoto)
        binding.viewPagerSplash.adapter = adapterPhotoSlash
        indicators.setViewPager(binding.viewPagerSplash)


        binding.imgSplashSearch.setOnClickListener {
            val i = Intent(this,SplashSearch::class.java)
            startActivity(i)
        }

        binding.btnSignup.setOnClickListener {
            val i = Intent(this,SignUp::class.java)
            startActivity(i)
        }
        binding.btnSignin.setOnClickListener {
            val i = Intent(this,SignIn::class.java)
            startActivity(i)
        }
    }

}