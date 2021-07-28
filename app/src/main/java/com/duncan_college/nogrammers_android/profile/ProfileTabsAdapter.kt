package com.duncan_college.nogrammers_android.profile

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ProfileTabsAdapter(activity: AppCompatActivity, val itemsCount: Int) :
    FragmentStateAdapter(activity) {
    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment = ProfileTabsFragment.getInstance(position)
}