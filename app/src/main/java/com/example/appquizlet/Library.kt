package com.example.appquizlet

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appquizlet.adapter.ViewPagerLibAdapter
import com.example.appquizlet.databinding.FragmentLibraryBinding
import com.google.android.material.tabs.TabLayout
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
                    tab.text = resources.getString(R.string.lb_study_sets)
                }

                1 -> {
                    tab.text = resources.getString(R.string.folders)
                }

                2 -> {
                    tab.text = resources.getString(R.string.lb_classes)
                }
            }
        }.attach()

        binding.tabLib.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val currentPosition = tab?.position
                Log.d("tab", currentPosition.toString())
                when (currentPosition) {
                    0 -> {

                    }

                    1 -> {
//                        val add = Add()
//                        add.showCustomDialog(
//                            resources.getString(R.string.add_folder),
//                            "",
//                            resources.getString(R.string.folder_name),
//                            resources.getString(R.string.desc_optional)
//                        )
                    }

                    2 -> {}
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

        return binding.root
    }

}