package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appquizlet.databinding.FragmentProfileBinding
import com.example.appquizlet.model.UserM


class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.linearLayoutSettings.setOnClickListener {
            val i = Intent(context, Settings::class.java)
            startActivity(i)
        }
        binding.linearLayoutCourse.setOnClickListener {
            val i = Intent(context, Add_Course::class.java)
            startActivity(i)
        }
        // Observe the userData and update UI
        UserM.getUserData().observe(viewLifecycleOwner) { userData ->
            binding.txtUsername.text = userData?.userName
            Toast.makeText(context, userData?.userName, Toast.LENGTH_SHORT).show()
        }
        return binding.root
    }
    // Đặt _binding = null khi fragment bị phá hủy.

    // Sử dụng lớp binding để truy cập các view trong layout.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}