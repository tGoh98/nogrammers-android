package com.example.nogrammers_android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.databinding.FragmentAnnouncementsBinding

/**
 * Announcements tab
 */
class AnnouncementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = DataBindingUtil.inflate<FragmentAnnouncementsBinding>(
            inflater,
            R.layout.fragment_announcements,
            container,
            false
        )
        return binding.root
    }
}