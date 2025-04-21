package com.example.appquizlet.ui.fragments.social

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.appquizlet.R
import com.example.appquizlet.databinding.FragmentEditAvatarBinding
import com.example.appquizlet.util.Helper
import com.example.appquizlet.viewmodel.social.SocialViewModel
import com.yalantis.ucrop.UCrop
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class FragmentEditAvatar : Fragment() {
    private lateinit var binding: FragmentEditAvatarBinding
    private val socialViewModel by viewModels<SocialViewModel>()
    private var selectedImageUri: Uri? = null
    private val args: FragmentEditAvatarArgs by navArgs()
    private lateinit var progressDialog: AlertDialog


    private val cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val resultUri = UCrop.getOutput(result.data!!)
            if (resultUri != null) {
                selectedImageUri = resultUri
                binding.imageViewAvatar.setImageURI(null) // Xóa cache ảnh cũ
                binding.imageViewAvatar.setImageURI(resultUri) // Cập nhật ảnh mới
            }
        } else if (result.resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(result.data!!)
            Log.e("CropError", "Lỗi cắt ảnh: ${cropError?.message}")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditAvatarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageUriString = arguments?.getString("image_uri")
        selectedImageUri = imageUriString?.let { Uri.parse(it) }
        Log.d("FragmentEditAvatar", "Selected image URI: $selectedImageUri")
        selectedImageUri?.let {
            binding.imageViewAvatar.setImageURI(it)
        }
        val imageType = arguments?.getString("IMAGE_TYPE") ?: "AVATAR"
        if (imageType == "COVER") {
            val params = binding.imageViewAvatar.layoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.cover_height)
            binding.imageViewAvatar.layoutParams = params
        } else {
            val params = binding.imageViewAvatar.layoutParams
            params.height = resources.getDimensionPixelSize(R.dimen.avatar_height)
            params.width = params.height
            binding.imageViewAvatar.layoutParams = params
        }

        binding.btnPublish.setOnClickListener {
            postAvatar()
        }

        selectedImageUri?.let { cropImage(it, args.imageType) }

        binding.btnCancel.setOnClickListener {
            val fragmentManager = requireActivity().supportFragmentManager
            if (fragmentManager.backStackEntryCount > 0) {
                fragmentManager.popBackStack()
            } else {
                requireActivity().finish()
            }
        }

        lifecycleScope.launch {
            socialViewModel.createPostStatus.collectLatest { success ->
                hideLoading()
                if (success) {
                    binding.etContent.text.clear()
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
        binding.btnPublish.setOnClickListener {
            if(selectedImageUri != null) {
                if (imageType == "COVER") {
                    Log.d("FragmentEditAvatar", "imageType: $imageType")
                    postCover()
                } else {
                    Log.d("FragmentEditAvatar", "imageType: $imageType")
                    postAvatar()
                }
            }
        }
    }

    private fun cropImage(imageUri: Uri, imageType: String) {
        val destinationUri = Uri.fromFile(File(requireContext().cacheDir, "cropped_${System.currentTimeMillis()}.jpg"))

        val aspectRatio = if (imageType == "AVATAR") 1f to 1f else 16f to 9f

        val cropIntent = UCrop.of(imageUri, destinationUri)
            .withAspectRatio(aspectRatio.first, aspectRatio.second)
            .withMaxResultSize(1000, 1000)
            .getIntent(requireContext())

        cropImageLauncher.launch(cropIntent)
    }

    private fun postAvatar() {
        val description = binding.etContent.text.toString()

        selectedImageUri?.let { uri ->
            socialViewModel.updateAvatar(
                Helper.getDataUserId(requireContext()), uri, requireContext(),description
            )
        }
    }

    private fun postCover() {
        val description = binding.etContent.text.toString()

        selectedImageUri?.let { uri ->
            socialViewModel.updateCover(
                Helper.getDataUserId(requireContext()), uri, requireContext(),description
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == UCrop.REQUEST_CROP && resultCode == Activity.RESULT_OK) {
            val resultUri = data?.let { UCrop.getOutput(it) }
            if (resultUri != null) {
                selectedImageUri = resultUri // Cập nhật URI ảnh mới
                binding.imageViewAvatar.setImageURI(resultUri) // Hiển thị ảnh mới
            }
        } else if (requestCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.e("CropError", "Lỗi cắt ảnh: ${cropError?.message}")
        }
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
