package com.example.appquizlet

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.databinding.FragmentItemAchievementBottomSheetBinding
import com.example.appquizlet.model.TaskData
import com.example.appquizlet.util.Helper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ItemAchievementBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentItemAchievementBottomSheetBinding
    private var progressText: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private var currentAchieveStreak: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemAchievementBottomSheetBinding.inflate(inflater, container, false)

        sharedPreferences = context?.getSharedPreferences("currentStreak", Context.MODE_PRIVATE)!!

        currentAchieveStreak = sharedPreferences.getInt("countStreak", 0)

        val taskData = arguments?.getParcelable<TaskData>(ARG_TASK_DATA)
        if (taskData != null) {
            binding.txtNameAchievement.text = taskData.taskName
            binding.txtAchievementDesc.text = taskData.description
            val imageName = "ac${taskData.id}"
            val imageResourceId =
                context?.resources?.getIdentifier(
                    imageName,
                    "drawable",
                    requireContext().packageName
                )
            // Check if drawable is set
            if (taskData.status != 2) {
                val originalBitmap: Bitmap? =
                    imageResourceId?.let {
                        BitmapFactory.decodeResource(
                            context?.resources,
                            it
                        )
                    }
                val grayscaleBitmap = originalBitmap?.let { Helper.toGrayscale(it) }
                binding.imgAchievement.setImageBitmap(grayscaleBitmap)
            } else {
                if (imageResourceId != null) {
                    binding.imgAchievement.setImageResource(imageResourceId)
                }
            }
            if (taskData.type == "Streak") {
                progressText = "$currentAchieveStreak / ${taskData.condition}"
                val progress =
                    (currentAchieveStreak.toDouble() / taskData.condition.toDouble()) * 100
                binding.customProgressBar.setProgress(progress.toInt(), progressText)
            } else if (taskData.type == "Study") {
                if (taskData.status == 2) {
                    progressText = resources.getString(R.string.awarded)
                    binding.customProgressBar.setProgress(100, progressText)
                } else {
                    progressText = resources.getString(R.string.uncompleted)
                    binding.customProgressBar.setProgress(0, progressText)
                }
            }

        }
        return binding.root
    }

    companion object {
        const val TAG = "ItemAchievementBottomSheet"
        private const val ARG_TASK_DATA = "arg_task_data"

        fun newInstance(taskData: TaskData): ItemAchievementBottomSheet {
            val fragment = ItemAchievementBottomSheet()
            val args = Bundle()
            args.putParcelable(ARG_TASK_DATA, taskData)
            fragment.arguments = args
            return fragment
        }

    }

}