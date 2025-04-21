package com.example.appquizlet.adapter.newfeature

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.appquizlet.ui.fragments.social.FragmentFeed
import com.example.appquizlet.ui.fragments.social.FragmentFriend
import com.example.appquizlet.ui.fragments.social.FragmentSocialProfile

class SocialViewPagerLibAdapter(fragment: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when (position) {
            0 -> {
                FragmentFeed()
            }

            1 -> {
                FragmentFriend()
            }

            2 -> {
                FragmentSocialProfile()
            }

            else -> {
                FragmentFriend()
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId < itemCount // Trả về true nếu itemId hợp lệ
    }
}