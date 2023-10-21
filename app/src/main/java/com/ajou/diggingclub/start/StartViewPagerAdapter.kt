package com.ajou.diggingclub.start

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ajou.diggingclub.start.fragments.SwipeFragment1
import com.ajou.diggingclub.start.fragments.SwipeFragment2
import com.ajou.diggingclub.start.fragments.SwipeFragment3
import com.ajou.diggingclub.start.fragments.SwipeFragment4

class StartViewPagerAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    private val fragments= listOf<Fragment>(
        SwipeFragment1(),
        SwipeFragment2(),
        SwipeFragment3(),
        SwipeFragment4()

    )

    override fun getItemCount() = fragments.size

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}