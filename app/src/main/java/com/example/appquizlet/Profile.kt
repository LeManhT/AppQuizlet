package com.example.appquizlet

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appquizlet.databinding.ActivityMainBinding
import com.example.appquizlet.databinding.FragmentProfileBinding



private var binding: FragmentProfileBinding? = null


class Profile : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding?.linearLayoutSettings?.setOnClickListener{
            val i = Intent(context,Settings::class.java)
            startActivity(i)
        }
        binding?.linearLayoutCourse?.setOnClickListener {
            var i = Intent(context,Add_Course::class.java)
            startActivity(i)
        }
        return binding?.root
    }
    // Đặt _binding = null khi fragment bị phá hủy.

    // Sử dụng lớp binding để truy cập các view trong layout.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}