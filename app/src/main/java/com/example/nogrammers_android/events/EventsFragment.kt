package com.example.nogrammers_android.events

import android.graphics.Color
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.alpha
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEventsBinding
import com.google.android.material.tabs.TabLayoutMediator
import java.util.*

/**
 * Events tab
 */
class EventsFragment : Fragment() {
    lateinit var binding: FragmentEventsBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEventsBinding>(
                inflater,
                R.layout.fragment_events, container, false
        )

        /* Create and set tabs adapter */
        binding.eventTabsPager.adapter = EventsTabAdapter(activity as AppCompatActivity, 2)

        /* Tab layout mediator to connect tab labels to ViewPager */
        TabLayoutMediator(binding.eventTabLayout, binding.eventTabsPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Feed"
                else -> tab.text = "My Events"
            }
        }.attach()

        return binding.root
    }

}