package com.example.nogrammers_android.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentProfileBinding
import com.google.android.material.tabs.TabLayoutMediator

// View pager tutorial: https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started

/**
 * Profile tab
 */
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        val binding = DataBindingUtil.inflate<FragmentProfileBinding>(
            inflater,
            R.layout.fragment_profile, container, false
        )

        /* Create and set tabs adapter */
        binding.profileTabsPager.adapter = ProfileTabsAdapter(activity as AppCompatActivity, 2)

        /* Tab layout mediator to connect tab labels to ViewPager */
        TabLayoutMediator(binding.profileTabLayout, binding.profileTabsPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "My Posts"
                else -> tab.text = "Mentions"
            }
        }.attach()

        return binding.root
    }
}