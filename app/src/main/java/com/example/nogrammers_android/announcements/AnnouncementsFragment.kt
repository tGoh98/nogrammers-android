package com.example.nogrammers_android.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentAnnouncementsBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Announcements tab
 */
class AnnouncementsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment with data binding */
        val binding = DataBindingUtil.inflate<FragmentAnnouncementsBinding>(
            inflater,
            R.layout.fragment_announcements,
            container,
            false
        )
        // TODO: dynamically populate the content for the recycler view
        val adapter = AnnouncementsAdapter((1..10).map {
            Announcement(
                "Fire drill $it",
                "Fire drill wee woo Fire drill wee woo Fire drill wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo wee woo beep beep beep beep beep beep beep beep",
                "snoopy$it"
            )
        })
        binding.announcementsView.adapter = adapter

        /* Listener for create announcement button */
        val createBtn = binding.createAnnouncementBtn
        createBtn.setOnClickListener { view ->
            Snackbar.make(view, "TODO: create a new announcement", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
        }

        return binding.root
    }
}