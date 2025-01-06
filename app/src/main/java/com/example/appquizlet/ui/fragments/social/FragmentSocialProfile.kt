package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.SocialProfileViewPagerLibAdapter
import com.example.appquizlet.adapter.newfeature.SocialViewPagerLibAdapter
import com.example.appquizlet.databinding.FragmentSocialProfileBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSocialProfile : Fragment() {
    private lateinit var binding: FragmentSocialProfileBinding
    private lateinit var adapterLibPager: SocialProfileViewPagerLibAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSocialProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterLibPager =
            SocialProfileViewPagerLibAdapter(childFragmentManager, lifecycle)
        binding.viewPager2.adapter = adapterLibPager
        binding.viewPager2.isUserInputEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, pos ->
            when (pos) {
                0 -> {
                    tab.text = resources.getString(R.string.post)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                    Log.d("FragmentSocialProfile", "tab1: ")
                }

                1 -> {
                    tab.text = resources.getString(R.string.images_video)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                    Log.d("FragmentSocialProfile", "tab2: ")
                }

                2 -> {
                    tab.text = resources.getString(R.string.study_set)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                    Log.d("FragmentSocialProfile", "tab3: ")
                }
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                Log.d("FragmentSocialProfile", "Tab selected: ${tab.text}")
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                Log.d("FragmentSocialProfile", "Tab unselected: ${tab.text}")
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                Log.d("FragmentSocialProfile", "Tab reselected: ${tab.text}")
            }
        })
    }

}