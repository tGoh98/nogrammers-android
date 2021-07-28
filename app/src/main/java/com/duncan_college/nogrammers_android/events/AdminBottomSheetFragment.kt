package com.duncan_college.nogrammers_android.events

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.duncan_college.nogrammers_android.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.duncan_college.nogrammers_android.databinding.FragmentAdminBottomSheetBinding
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class AdminBottomSheetFragment(val event: Event): BottomSheetDialogFragment() {
    lateinit var binding: FragmentAdminBottomSheetBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_admin_bottom_sheet, container, false
        )
        binding.deleteButton.setOnClickListener{onDelete(event)}
        binding.cancelButton.setOnClickListener {
            this.dismiss()
        }
        binding.editButton.setOnClickListener {
            /* Replace fragment */
            parentFragment?.parentFragmentManager?.beginTransaction()?.apply {
                val fragment = EditEventFragment(event)
                replace(R.id.fl_fragment, fragment)
                addToBackStack("edit event")
                setReorderingAllowed(true)
                commit()
            }
        }
        return binding.root
    }

    /**
     * Delete current event
     */
    fun onDelete(event: Event) {
        // delete event from each interested and going user's list
        for (netId in event.interestedUsers) {
            Firebase.database.reference.child("users").child(netId).
            child("interestedEvents").child(event.key).removeValue()
        }
        for (netId in event.goingUsers) {
            Firebase.database.reference.child("users").child(netId).
            child("goingEvents").child(event.key).removeValue()
        }
        // delete event and image from firebase
        Firebase.database.reference.child("events").child(event.key).removeValue()
        Firebase.storage.reference.child("eventPics").child(event.key).delete()
        // close fragment
        this.activity?.findViewById<BottomNavigationItemView>(R.id.events_icon)?.performClick()
    }

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle, event: Event): AdminBottomSheetFragment {
            val fragment = AdminBottomSheetFragment(event)
            fragment.arguments = bundle
            return fragment
        }
    }
}