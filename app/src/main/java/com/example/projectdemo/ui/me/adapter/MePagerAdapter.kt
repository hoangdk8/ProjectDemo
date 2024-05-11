package com.example.projectdemo.ui.me.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.projectdemo.ui.me.view.DownloadedFragment
import com.example.projectdemo.ui.me.view.FavoritesFragment

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