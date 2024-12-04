package com.example.appquizlet.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.R
import com.example.appquizlet.databinding.FragmentClassesBinding
import com.example.appquizlet.ui.activities.AddClassActivity


class FragmentClasses : Fragment(R.layout.fragment_classes) {
    private lateinit var binding: FragmentClassesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater, container, false)
        binding.btnCreateClass.setOnClickListener {
            val intent = Intent(context, AddClassActivity::class.java)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}