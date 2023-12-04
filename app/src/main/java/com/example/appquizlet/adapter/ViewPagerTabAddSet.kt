package com.example.appquizlet.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appquizlet.FragmentCreatedSet
import com.example.appquizlet.FragmentFolderSet
import com.example.appquizlet.FragmentStudiedSet

class ViewPagerTabAddSet(fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                FragmentCreatedSet()
            }

            1 -> {
                FragmentStudiedSet()
            }

            else -> {
                FragmentFolderSet()
            }
        }
    }
}