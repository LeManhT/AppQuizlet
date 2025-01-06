package com.example.appquizlet.ui.fragments.social

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appquizlet.R
import com.example.appquizlet.adapter.newfeature.SocialViewPagerLibAdapter
import com.example.appquizlet.databinding.FragmentSocialMainBinding
import com.example.appquizlet.services.SignalRService
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentSocialMain : Fragment() {
    private lateinit var adapterLibPager: SocialViewPagerLibAdapter
    private lateinit var binding: FragmentSocialMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSocialMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        adapterLibPager =
            SocialViewPagerLibAdapter(parentFragmentManager, lifecycle)
        binding.viewPager.adapter = adapterLibPager
        binding.viewPager.isUserInputEnabled = false
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, pos ->
            when (pos) {
                0 -> {
//                    tab.text = resources.getString(R.string.lb_study_sets)
                    tab.icon = ResourcesCompat.getDrawable(resources, R.drawable.transfer, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                }

                1 -> {
//                    tab.text = resources.getString(R.string.folders)
                    tab.icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.icons8_users_24, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                }

                2 -> {
//                    tab.text = resources.getString(R.string.folders)
                    tab.icon =
                        ResourcesCompat.getDrawable(resources, R.drawable.user_edit, null)
                    val badge = tab.orCreateBadge
                    badge.backgroundColor =
                        ResourcesCompat.getColor(resources, R.color.semi_blue, null)
                }
            }
        }.attach()

        val fab: FloatingActionButton = binding.fab
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show()
        }

        binding.imgOpenMessage.setOnClickListener {
            findNavController().navigate(R.id.fragmentListConversation)
        }

        binding.etSearch.setOnClickListener {
            findNavController().navigate(R.id.fragmentCreatePost)
        }
    }

}