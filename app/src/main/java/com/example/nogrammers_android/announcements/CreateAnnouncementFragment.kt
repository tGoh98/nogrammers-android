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
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentCreateAnnouncementBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Fragment for creating a new announcement
 */
class CreateAnnouncementFragment : Fragment() {

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
            val anTitle = binding.newAnnouncementTitle.text.toString()
            val anMsg = binding.newAnnouncementTxt.text.toString()
            val pinToTop = binding.pinAnnouncment.isChecked
            val postToFb = binding.postToFb.isChecked
            val sendToListserv = binding.sendToListserv.isChecked

            Snackbar.make(binding.root, "$anTitle\n$anMsg", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show()
//            Snackbar.make(binding.root, "pinToTop: $pinToTop postToFb: $postToFb sendToListserv: $sendToListserv", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .show()
            // TODO: update database

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