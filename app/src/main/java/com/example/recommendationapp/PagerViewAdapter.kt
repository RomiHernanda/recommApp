package com.example.recommendationapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.recommendationapp.fragments.HomeFragment
import com.example.recommendationapp.fragments.RecommendationFragment

class PagerViewAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2;
    }

    override fun createFragment(position: Int): Fragment {
        when(position) {
            0 -> HomeFragment()
            1 -> RecommendationFragment()
        }
        return HomeFragment()
    }
}