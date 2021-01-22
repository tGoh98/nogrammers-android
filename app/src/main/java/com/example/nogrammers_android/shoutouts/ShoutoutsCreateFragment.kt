package com.example.nogrammers_android.shoutouts

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.nogrammers_android.R
import com.example.nogrammers_android.announcements.AnnouncementsViewModel
import com.example.nogrammers_android.databinding.FragmentCreateAnnouncementBinding
import com.example.nogrammers_android.databinding.FragmentCreateShoutoutBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*

class ShoutoutsCreateFragment(val position: Int, val netID: String) : Fragment() {

    /* Shared view model */
    private val model: ShoutoutsViewModel by activityViewModels()
    private lateinit var db: DatabaseReference
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        /* Inflate the layout for this fragment */
        val binding = DataBindingUtil.inflate<FragmentCreateShoutoutBinding>(
                inflater,
                R.layout.fragment_create_shoutout,
                container,
                false
        )

        if (position.equals(0)) {
            db = Firebase.database.reference.child("shoutouts")
        } else {
            db = Firebase.database.reference.child("sds")
        }
        /* Add listener for cancel button */
        binding.cancelShoutoutBtn.setOnClickListener {
            Log.d("cancel", "CANECLE")
            closeTab(binding.newShoutoutTitle, binding.newShoutoutTxt)
        }

        /* Listener for post announcements */
        binding.postNewShoutoutBtn.setOnClickListener {
            val name = binding.newShoutoutTitle.text.toString()
            val msg = binding.newShoutoutTxt.text.toString()
            var num = 0
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val so = dataSnapshot.getValue<ArrayList<HashMap<String, Any>>>()
                    if (so != null) {
                        num = so.size
                    }
                    val newShoutout = ShoutoutsDBObject()
                    newShoutout.author = netID
                    newShoutout.msg = msg
                    newShoutout.date = Calendar.getInstance().timeInMillis.toString()
                    db.child(newShoutout.uuid).setValue(
                            newShoutout
                    )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            }
            db.addListenerForSingleValueEvent(postListener)

            closeTab(binding.newShoutoutTitle, binding.newShoutoutTxt)
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