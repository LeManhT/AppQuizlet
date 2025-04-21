package com.example.appquizlet.ui.fragments.social

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.appquizlet.databinding.FragmentUploadBinding

class FragmentUpload : Fragment() {
    private lateinit var binding: FragmentUploadBinding
    private var fileUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnChooseFile.setOnClickListener { openFileChooser() }
        binding.btnUpload.setOnClickListener { uploadFileToFirebase() }
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                fileUri = result.data?.data
                Toast.makeText(requireContext(), "File selected!", Toast.LENGTH_SHORT).show()
            }
        }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" // Chọn ảnh, có thể thay bằng "*/*" nếu muốn chọn mọi loại file
        filePickerLauncher.launch(intent)
    }

    private fun uploadFileToFirebase() {
        if (fileUri == null) {
            Toast.makeText(requireContext(), "Vui lòng chọn file!", Toast.LENGTH_SHORT).show()
            return
        }
    }
}