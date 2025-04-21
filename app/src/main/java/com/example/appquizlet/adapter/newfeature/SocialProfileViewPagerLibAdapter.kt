package com.example.appquizlet.adapter.newfeature

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appquizlet.ui.fragments.social.FragmentChat
import com.example.appquizlet.ui.fragments.social.FragmentFriend
import com.example.appquizlet.ui.fragments.social.FragmentMyImage
import com.example.appquizlet.ui.fragments.social.FragmentMyStudySet
import com.example.appquizlet.ui.fragments.social.FragmentProfilePost
import com.example.appquizlet.ui.fragments.social.FragmentSocialProfile

class SocialProfileViewPagerLibAdapter(fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                FragmentProfilePost()
            }

            1 -> {
                FragmentMyImage()
            }

            2 -> {
                FragmentMyStudySet()
            }

            else -> {
                FragmentProfilePost()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return false
    }
}
