package com.example.nogrammers_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.databinding.FragmentShoutoutsBinding


class ShoutoutsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FragmentShoutoutsBinding>(inflater,
            R.layout.fragment_shoutouts,container,false)

        var authors = listOf("adrienne", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim", "julie", "colin", "cindy", "tim").map { Shoutouts(it, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.") }
        authors = authors.toMutableList()
        val adapter = ShoutoutsAdapter(authors)
        binding.shoutoutsView.adapter = adapter

        return binding.root
    }
}