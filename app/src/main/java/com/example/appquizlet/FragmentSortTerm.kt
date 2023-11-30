package com.example.appquizlet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.databinding.FragmentSortTermBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentSortTerm : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentSortTermBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSortTermBinding.inflate(inflater, container, false)
        binding.txtOriginalSort.setOnClickListener {
            binding.iconTick2.visibility = View.GONE
            binding.imageTick1.visibility = View.VISIBLE
        }

        binding.txtAlphabetically.setOnClickListener {
            binding.imageTick1.visibility = View.GONE
            binding.iconTick2.visibility = View.VISIBLE
        }
        return binding.root
    }

    companion object {
        const val TAG = "Fragment sort term"
    }
}