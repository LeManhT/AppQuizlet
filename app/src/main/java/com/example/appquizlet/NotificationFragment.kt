package com.example.appquizlet

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.appquizlet.databinding.FragmentNotificationBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment



class NotificationFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        val notificationDb = context?.let { MyDBHelper(it) }
//        val selectQuery = "SELECT * FROM ${MyDBHelper.TABLE_NAME}"
//        val db = notificationDb?.readableDatabase
//        val cursor = db?.rawQuery(selectQuery, null)
//        if (cursor != null) {
//            if(cursor.moveToLast()) {
//                if (cursor != null) {
//                    Toast.makeText(context,cursor.getString(1), Toast.LENGTH_SHORT).show()
//                }
//            }
//        }

        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(R.layout.fragment_notification)

        dialog.setOnShowListener { dialogInterface ->
            val bottomSheet =
                (dialogInterface as BottomSheetDialog).findViewById<View>(R.id.notification_bottomsheet)
            bottomSheet?.layoutParams?.height = ViewGroup.LayoutParams.MATCH_PARENT
            val behavior = dialogInterface.behavior
            val closeIcon = dialog.findViewById<TextView>(R.id.txtNotificationCloseIcon)
            closeIcon.setOnClickListener {
                dismiss()
            }

            // Set minimum height for the bottom sheet using the screen height
            val screenHeight = Resources.getSystem().displayMetrics.heightPixels
            bottomSheet?.minimumHeight = screenHeight - 60
            behavior.peekHeight = screenHeight

            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        return dialog
    }


}