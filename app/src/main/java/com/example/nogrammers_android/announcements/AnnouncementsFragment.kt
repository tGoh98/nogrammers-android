package com.example.nogrammers_android.announcements

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentAnnouncementsBinding

/**
 * Announcements tab
 */
class AnnouncementsFragment : Fragment() {
    /* Shared view model */
    private val model: AnnouncementsViewModel by activityViewModels()

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

        /* Create view model */
        model.createMode.observe(viewLifecycleOwner, { newVal ->
            if (newVal) {
                /* Frag */
                showNewAnnouncementsFrag()
                /* Overlay */
                binding.transparentOverlay.visibility = View.VISIBLE
                /* FAB */
                binding.createAnnouncementBtn.visibility = View.INVISIBLE
                /* Freeze recycler view */
                disableRV(binding.announcementsView)
            } else {
                /* Frag */
                hideNewAnnouncementsFrag()
                /* Overlay */
                binding.transparentOverlay.visibility = View.INVISIBLE
                /* FAB */
                binding.createAnnouncementBtn.visibility = View.VISIBLE
                /* Unfreeze recycler view */
                enableRV(binding.announcementsView)
            }
        })

        /* Listener for create announcement button */
        val createBtn = binding.createAnnouncementBtn
        createBtn.setOnClickListener {
            model.createMode.value = true
        }

        return binding.root
    }

    /**
     * Shows new announcements fragment
     */
    private fun showNewAnnouncementsFrag() {
        activity?.supportFragmentManager
            ?.beginTransaction()
            ?.add(
                R.id.announcementsContainer,
                CreateAnnouncementFragment(),
                "createAnnouncement"
            )
            ?.commit()
    }

    /**
     * Hides new announcements fragment
     */
    private fun hideNewAnnouncementsFrag() {
        val sfm = activity?.supportFragmentManager ?: return
        val fragment = sfm.findFragmentByTag("createAnnouncement")
        if (fragment != null) sfm.beginTransaction().remove(fragment).commit()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableRV(rv: RecyclerView) {
        rv.setOnTouchListener { _, _ -> false }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disableRV(rv: RecyclerView) {
        rv.setOnTouchListener { _, _ -> true }
    }

}