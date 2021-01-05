package com.example.nogrammers_android.announcements

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R

/**
 * Fragment for creating a new announcement
 */
class CreateAnnouncementFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* Inflate the layout for this fragment */
        val view = inflater.inflate(R.layout.fragment_create_announcement, container, false)

        /* Add listener for cancel button */
        val cancelBtn = view.findViewById<Button>(R.id.cancelAnnounceBtn)
        cancelBtn.setOnClickListener { hideNewAnnouncementFrag() }

        return view
    }

    /**
     * Hides this fragment. Perhaps find some way to move this logic to AnnouncementsFragment.kt?
     */
    private fun hideNewAnnouncementFrag() {
        val sfm = activity?.supportFragmentManager ?: return
        val fragment = sfm.findFragmentByTag("createAnnouncement")
        if (fragment != null) sfm.beginTransaction().remove(fragment).commit()
    }
}