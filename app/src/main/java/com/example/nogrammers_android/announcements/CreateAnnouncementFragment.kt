package com.example.nogrammers_android.announcements

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentCreateAnnouncementBinding
import com.google.firebase.database.DatabaseReference

/**
 * Fragment for creating a new announcement
 */
class CreateAnnouncementFragment(private val announceDbRef: DatabaseReference, private val dataSize: Int) : Fragment() {

    /* Shared view model */
    private val model: AnnouncementsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /* Inflate the layout for this fragment */
        val binding = DataBindingUtil.inflate<FragmentCreateAnnouncementBinding>(
            inflater,
            R.layout.fragment_create_announcement,
            container,
            false
        )

        /* Add listener for cancel button */
        binding.cancelAnnounceBtn.setOnClickListener {
            closeTab(binding.newAnnouncementTitle, binding.newAnnouncementTxt)
        }

        /* Listener for post announcements */
        binding.postNewAnnouncementBtn.setOnClickListener {
            /* Get author from app data */
            val author: String
            val name = (activity as MainActivity).curUser.name
            val netID = (activity as MainActivity).curUser.netID
            author = if (name != "Add your name here!") name else netID

            /* Get other stuff */
            val anTitle = binding.newAnnouncementTitle.text.toString()
            val anMsg = binding.newAnnouncementTxt.text.toString()
            val urgent = binding.markAsUrgentChckbx.isChecked
            val postToFb = binding.postToFb.isChecked
            val sendToListserv = binding.sendToListserv.isChecked

            /* Update db */
            announceDbRef.child(dataSize.toString()).setValue(Announcement(anTitle, anMsg, author, urgent))

            /* Update list adapter */


            closeTab(binding.newAnnouncementTitle, binding.newAnnouncementTxt)
        }

        return binding.root
    }

    private fun closeTab(anTitle: EditText, anTxt: EditText) {
        /* Update create mode */
        model.createMode.value = false

        /* Hide keyboard */
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        anTitle.text.clear()
        anTxt.text.clear()
    }

}