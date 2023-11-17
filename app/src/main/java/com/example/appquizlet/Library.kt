package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.adapter.ViewPagerLibAdapter
import com.example.appquizlet.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator


class Library : Fragment() {
    private lateinit var binding: FragmentLibraryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLibraryBinding.inflate(inflater, container, false)

        binding.txtLibPlus.setOnClickListener {
            val intent = Intent(context, CreateSet::class.java)
            startActivity(intent)
        }

//        Adapter
        val adapterLibPager =
            ViewPagerLibAdapter(requireActivity().supportFragmentManager, lifecycle)
        binding.pagerLib.adapter = adapterLibPager
        TabLayoutMediator(binding.tabLib, binding.pagerLib) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = "Study Sets"
                }

                1 -> {
                    tab.text = "Folders"
                }

                2 -> {
                    tab.text = "Classes"
                }
            }
        }.attach()

        return binding.root
    }

}