package com.example.appquizlet.ui.fragments.social

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.ImageAdapter
import com.example.appquizlet.databinding.FragmentMyImageBinding
import com.example.appquizlet.ui.activities.ViewImage

class FragmentMyImage : Fragment() {
    private lateinit var binding : FragmentMyImageBinding
    private lateinit var imageAdapter: ImageAdapter
    private val dummyImages = listOf(
        "https://afamilycdn.com/150157425591193600/2023/6/18/monkey-d-luffy-one-piece-16870573558421290762146-1687081297638-16870812977141082701226.jpg",
        "https://afamilycdn.com/150157425591193600/2023/6/18/monkey-d-luffy-one-piece-16870573558421290762146-1687081297638-16870812977141082701226.jpg",
        "https://afamilycdn.com/150157425591193600/2023/6/18/monkey-d-luffy-one-piece-16870573558421290762146-1687081297638-16870812977141082701226.jpg",
        "https://afamilycdn.com/150157425591193600/2023/6/18/monkey-d-luffy-one-piece-16870573558421290762146-1687081297638-16870812977141082701226.jpg"
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        imageAdapter = ImageAdapter {
            imageUrl -> showFullImage(imageUrl)
        }
        binding.rvListImages.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.rvListImages.adapter = imageAdapter

        imageAdapter.updateData(dummyImages)
    }

    private fun showFullImage(imageUrl: String) {
        val intent = Intent(requireContext(), ViewImage::class.java).apply {
            putExtra("IMAGE_URL", imageUrl)
        }
        startActivity(intent)
    }

}