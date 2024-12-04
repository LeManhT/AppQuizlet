package com.example.appquizlet.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.databinding.FragmentLearnFlashCardSettingBinding
import com.example.appquizlet.ui.activities.OnClickButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FragmentLearnFlashCardSetting : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentLearnFlashCardSettingBinding
    private var onCLickListener: OnClickButton? = null
    private var isFront: Boolean = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLearnFlashCardSettingBinding.inflate(inflater, container, false)

        binding.layoutShuffle.setOnClickListener {
            onCLickListener?.handleClickShuffle()
        }

        binding.layoutOrientation.setOnClickListener {
            onCLickListener?.handleClickModeDisplay()
        }

        binding.layoutReset.setOnClickListener {
            onCLickListener?.handleResetCard()
        }

        if(isFront) {
            binding.textOrientation.text = "Term"
        } else {
            binding.textOrientation.text = "Definition"
        }

        return binding.root
    }

    fun setOnButtonSettingClickListener(listener: OnClickButton) {
        this.onCLickListener = listener
    }

    fun setIsFront(isFrontData: Boolean) {
        isFront = isFrontData
    }
}