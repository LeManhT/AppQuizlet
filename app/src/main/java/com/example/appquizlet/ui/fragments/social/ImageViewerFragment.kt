package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.example.appquizlet.adapter.newfeature.FullScreenImageAdapter
import com.example.appquizlet.databinding.FragmentImageViewerBinding
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ImageViewerFragment : Fragment() {
    private lateinit var binding: FragmentImageViewerBinding
    private val args: ImageViewerFragmentArgs by navArgs()
    private lateinit var imageAdapter: FullScreenImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageViewerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupImageViewPager()
        setupUI()
    }

    private fun setupImageViewPager() {
        val imageUrls = args.imageUrls.toList()
        imageAdapter = FullScreenImageAdapter(imageUrls)

        binding.viewPagerFullScreen.apply {
            adapter = imageAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL

            // Set the current position to show the clicked image
            currentItem = args.initialPosition
        }

        // Setup page indicator
        TabLayoutMediator(binding.tabLayoutIndicator, binding.viewPagerFullScreen) { _, _ ->
            // No text needed for the tabs
        }.attach()

        // Update image counter text
        updateImageCounter(args.initialPosition + 1, imageUrls.size)

        // Add page change listener
        binding.viewPagerFullScreen.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position + 1, imageUrls.size)
            }
        })
    }

    private fun setupUI() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun updateImageCounter(currentPosition: Int, totalImages: Int) {
        binding.tvImageCounter.text = "$currentPosition/$totalImages"
    }
}