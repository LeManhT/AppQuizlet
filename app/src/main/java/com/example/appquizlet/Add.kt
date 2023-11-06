package com.example.appquizlet

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.appquizlet.databinding.FragmentAddBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

private lateinit var binding: FragmentAddBinding
class Add : BottomSheetDialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddBinding.inflate(inflater, container, false)
//        setStyle(STYLE_NORMAL,R.style.BottomSheetStyleTheme)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.layoutSet.setOnClickListener {
            dismiss()
            Toast.makeText(context,"Create study sets", Toast.LENGTH_SHORT).show()
        }
        binding.layoutFolder.setOnClickListener{
            dismiss()
            Toast.makeText(context,"Create folder", Toast.LENGTH_SHORT).show()
        }
        binding.layoutClass.setOnClickListener{
            dismiss()
            Toast.makeText(context,"Create class", Toast.LENGTH_SHORT).show()
        }
    }
    companion object {
        const val TAG = "ModalBottomSheet"
    }
}