package com.example.appquizlet.ui.fragments.social

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.SelectedFileAdapter
import com.example.appquizlet.databinding.FragmentCreatePostBinding
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FragmentCreatePost : Fragment() {
    private lateinit var binding : FragmentCreatePostBinding
    private val fileUriList = mutableListOf<Uri>()
    private lateinit var adapter: SelectedFileAdapter
    private var cameraImageUri: Uri? = null
    private val socialViewModel: SocialViewModel by viewModels()
    private lateinit var progressDialog: AlertDialog


    private val imagePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri>? ->
            uris?.let {
                fileUriList.addAll(it)
                adapter.notifyDataSetChanged()
            }
        }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && cameraImageUri != null) {
                fileUriList.add(cameraImageUri!!)
                adapter.notifyDataSetChanged()
            }
        }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let {
                requireContext().contentResolver.takePersistableUriPermission(
                    it, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                fileUriList.add(it)
                adapter.notifyDataSetChanged()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePostBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = SelectedFileAdapter(fileUriList)
        binding.rvSelectedFiles.adapter = adapter
        binding.rvSelectedFiles.layoutManager = LinearLayoutManager(requireContext())
        binding.btnAttachImage.setOnClickListener {
            showImagePickerDialog()
        }
        binding.btnAttachFile.setOnClickListener {
            filePickerLauncher.launch(arrayOf("*/*"))
        }

        binding.btnAttachEmoji.setOnClickListener {
            val dialog = EmotionPickerBottomSheet { emoji, text ->
                binding.btnAttachEmoji.text = "$emoji $text"
            }
            dialog.show(parentFragmentManager, "EmotionPicker")
        }

        binding.btnPublish.setOnClickListener {
            val content = binding.etContent.text.toString().trim()

            if (content.isEmpty()) {
                binding.etContent.error = "Vui lòng nhập nội dung"
            }

            binding.btnPublish.isEnabled = false
            showLoading("Creating post ...")
            socialViewModel.createPost(
                authorId = Helper.getDataUserId(requireContext()),
                content = content,
                author = Helper.getDataUsername(requireContext()),
                imageUris = fileUriList.filter { isImage(it) },
                fileUris = fileUriList.filter { !isImage(it) },
                context = requireContext()
            )
        }
        lifecycleScope.launch {
            socialViewModel.createPostStatus.collectLatest { success ->
                hideLoading()
                if (success) {
                    binding.etContent.text.clear()
                    fileUriList.clear()
                    adapter.notifyDataSetChanged()
                    binding.btnPublish.isEnabled = true
                    Toast.makeText(requireContext(), "Đăng bài thành công!", Toast.LENGTH_LONG)
                        .show()
                    findNavController().popBackStack()
                } else {
                    binding.btnPublish.isEnabled = true
                    Toast.makeText(requireContext(), "Đăng bài thất bại!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Chụp ảnh", "Chọn từ thư viện")
        AlertDialog.Builder(requireContext())
            .setTitle("Chọn ảnh")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> imagePickerLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun openCamera() {
        val picturesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val photoFile = File(picturesDir, "${System.currentTimeMillis()}.jpg")

        cameraImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().applicationContext.packageName}.fileprovider",
            photoFile
        )

        cameraLauncher.launch(cameraImageUri)
    }

    private fun isImage(uri: Uri): Boolean {
        val contentResolver = requireContext().contentResolver
        val type = contentResolver.getType(uri)
        return type?.startsWith("image/") == true
    }

    private fun showLoading(msg: String) {
        progressDialog =
            ProgressDialog.show(requireContext(), resources.getString(R.string.loading_data), msg)
        progressDialog.show()
    }

    private fun hideLoading() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }


}