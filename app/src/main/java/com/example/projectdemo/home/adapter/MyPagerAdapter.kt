package com.example.projectdemo.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectdemo.explore.fragment.ExploreFragment
import com.example.projectdemo.home.fragment.HomeFragment
import com.example.projectdemo.me.MeFragment
import com.example.projectdemo.search.SearchFragment

class MyPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 4
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ExploreFragment()
            2 -> SearchFragment()
            3 -> MeFragment()
            else -> HomeFragment()
        }
    }
}
