package com.example.projectdemo.me.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectdemo.explore.fragment.ExploreFragment
import com.example.projectdemo.home.fragment.HomeFragment
import com.example.projectdemo.me.DownloadedFragment
import com.example.projectdemo.me.FavoritesFragment
import com.example.projectdemo.me.MeFragment
import com.example.projectdemo.search.SearchFragment

class MePagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> FavoritesFragment()
            1 -> DownloadedFragment()
            else -> FavoritesFragment()
        }
    }
}