package com.duncan_college.nogrammers_android.shoutouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.duncan_college.nogrammers_android.R
import com.duncan_college.nogrammers_android.databinding.FragmentShoutoutsBinding
import com.google.android.material.tabs.TabLayoutMediator

class ShoutoutsFragment(private val netID: String, val sortBy: Int) : Fragment() {
    lateinit var binding: FragmentShoutoutsBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        binding = DataBindingUtil.inflate<FragmentShoutoutsBinding>(
                inflater,
                R.layout.fragment_shoutouts, container, false
        )
        /* Create and set tabs adapter */
        binding.shoutoutsTabsPager.adapter = ShoutoutsTabAdapter(activity as AppCompatActivity, 2, netID, sortBy)

        /* Tab layout mediator to connect tab labels to ViewPager */
        TabLayoutMediator(binding.shoutoutsTabLayout, binding.shoutoutsTabsPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Shoutouts"
                else -> tab.text = "Stuff"
            }
        }.attach()

        return binding.root
    }

    fun forceUpdate(newSort: Int) {
        (binding.shoutoutsTabsPager.adapter as ShoutoutsTabAdapter).remakeFragment(newSort)
    }
}