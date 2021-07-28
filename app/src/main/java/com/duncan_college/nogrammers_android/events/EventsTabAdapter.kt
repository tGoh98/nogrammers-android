package com.duncan_college.nogrammers_android.events

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventsTabAdapter(activity: AppCompatActivity, val itemsCount: Int, val netId: String) :
        FragmentStateAdapter(activity) {
    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment = EventTabsFragment.getInstance(position, netId)
    
}