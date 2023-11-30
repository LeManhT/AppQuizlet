package com.example.appquizlet

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.appquizlet.databinding.FragmentStudyThisSetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class StudyThisSetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentStudyThisSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentStudyThisSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object {

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.fragment_study_this_set)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog).findViewById<View>(R.id.fragmentStudyThisSetBottomsheet)
            val closeIcon = dialog.findViewById<TextView>(R.id.txtStudyThisSetCloseIcon)
            closeIcon.setOnClickListener {
                dismiss()
            }
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            var screenHeight = Resources.getSystem().displayMetrics.heightPixels - 60
            bottomSheet?.minimumHeight = screenHeight
            val behavior = (dialogInterface as BottomSheetDialog).behavior
            behavior.peekHeight = screenHeight
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        return dialog
    }

}