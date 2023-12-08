package com.example.appquizlet

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appquizlet.databinding.FragmentProfileBinding
import com.example.appquizlet.model.UserM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.IOException

fun Uri.toBitmap(contentResolver: ContentResolver): Bitmap? {
    return try {
        contentResolver.openInputStream(this)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
        null
    }
}

class Profile : Fragment() {
    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    val REQUEST_CODE = 10
    val GALLERY_REQUEST_CODE = 15

    //    registerForActivityResult(ActivityResultContracts.GetContent()):
//
//registerForActivityResult là một phương thức mà bạn sử dụng để đăng ký một ActivityResultLauncher.
// Nó nhận vào một loại hành động (action), và trong trường hợp này, là ActivityResultContracts.GetContent().
//GetContent() là một contract (hợp đồng) được cung cấp sẵn trong thư viện activity-result của Android,
// và nó được sử dụng để nhận dữ liệu từ một nguồn nào đó, trong trường hợp này là thư viện ảnh.

    // Khai báo launcher
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                handleImageUri(it)
            }
        }

    private fun handleImageUri(uri: Uri) {
        // Xử lý đường dẫn của ảnh ở đây
        val bitmap: Bitmap? = uri.toBitmap(requireActivity().contentResolver)
        if (bitmap != null) {
            // Hiển thị hoặc xử lý ảnh theo nhu cầu của bạn
            val imageView: ImageView = binding.imgAvatar
            imageView.setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.linearLayoutSettings.setOnClickListener {
            val i = Intent(context, Settings::class.java)
            startActivity(i)
        }
        binding.linearLayoutCourse.setOnClickListener {
            val i = Intent(context, Add_Course::class.java)
            startActivity(i)
        }

        val userData = UserM.getUserData()

        userData.observe(viewLifecycleOwner) { userData ->
            binding.txtUsername.text = userData.loginName
        }

        binding.imgAvatar.setOnClickListener {
            onClickRequestPermission()
        }


        return binding.root
    }
    // Đặt _binding = null khi fragment bị phá hủy.

    // Sử dụng lớp binding để truy cập các view trong layout.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onClickRequestPermission() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Quyền đã được cấp, thực hiện công việc liên quan đến thư viện ở đây
            openGallery()
        } else {
            // Quyền chưa được cấp, yêu cầu quyền
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), REQUEST_CODE)
        }

    }

    private fun openGallery() {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = "image/*"
//        startActivityForResult(intent, GALLERY_REQUEST_CODE)

        //cách 2  dùng launcher
        // Sử dụng launcher để mở thư viện (gallery)
        galleryLauncher.launch("image/*")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform tasks related to the gallery here
                openGallery()
            } else {
                // Permission not granted, handle it (e.g., show a message to the user)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            // Handle the selected image from the gallery
// requestCode là một mã số đặc biệt được gán khi bạn gọi một hoạt động (activity)
            // để mở thư viện ảnh bằng startActivityForResult hoặc launcher.launch (nếu sử dụng ActivityResultLauncher).
//GALLERY_REQUEST_CODE là một hằng số mà bạn tự đặt để định danh cho yêu cầu mở thư viện ảnh.
            val selectedImageUri = data?.data
            try {
                // Sử dụng Coroutine để thực hiện các hoạt động đọc từ bộ nhớ ở nền
                GlobalScope.launch(Dispatchers.IO) {
                    // Chuyển đổi đường dẫn thành đối tượng Bitmap
                    val bitmap: Bitmap? = selectedImageUri?.let { uri ->
                        requireActivity().contentResolver.openInputStream(uri)?.use { inputStream ->
                            BitmapFactory.decodeStream(inputStream)
                        }
                    }

                    // Chuyển đổi Bitmap thành byte array
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val byteArray = byteArrayOutputStream.toByteArray()

                    // Kiểm tra xem bitmap có null hay không trước khi sử dụng
                    if (bitmap != null) {
                        // Chuyển đổi từng byte thành chuỗi hex và in ra Logcat
                        val hexString = byteArray.joinToString(separator = " ") { byteValue ->
                            String.format("%02X", byteValue)
                        }
                        Log.d("byte", hexString)

                        // Thực hiện các công việc trên main thread (nếu cần)
                        launch(Dispatchers.Main) {
                            val imageView: ImageView = binding.imgAvatar
                            imageView.setImageBitmap(bitmap)
                            // Hoặc tiếp tục xử lý ảnh theo nhu cầu của bạn trên main thread
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}