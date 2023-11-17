package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.databinding.FragmentClassesBinding


class Classes : Fragment(R.layout.fragment_classes) {
    private lateinit var binding: FragmentClassesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClassesBinding.inflate(inflater, container, false)
        binding.bntCreateClass.setOnClickListener {
           
        }
        return binding.root
    }

}