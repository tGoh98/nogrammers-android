package com.duncan_college.nogrammers_android.shoutouts

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
import com.duncan_college.nogrammers_android.R
import com.duncan_college.nogrammers_android.databinding.FragmentCreateShoutoutBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class ShoutoutsCreateFragment(val position: Int, val netID: String) : Fragment() {

    /* Shared view model */
    private val model: ShoutoutsViewModel by activityViewModels()
    private lateinit var db: DatabaseReference
    private var name: String = netID
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
            closeTab(binding.newShoutoutTxt)
        }

        /* Listener for post announcements */
        binding.postNewShoutoutBtn.setOnClickListener {
            val msg = binding.newShoutoutTxt.text.toString()
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val newShoutout = ShoutoutsDBObject()

                    val curTime = Calendar.getInstance().timeInMillis.toString()
                    // Key is author.concat(date)
                    val key = name.filter { !it.isWhitespace() }.plus(curTime)

                    newShoutout.author = name
                    newShoutout.msg = msg
                    newShoutout.netID = netID
                    newShoutout.date = curTime
                    newShoutout.id = key
                    db.child(key).setValue(
                            newShoutout
                    )
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed
                }
            }
            db.addListenerForSingleValueEvent(postListener)

            closeTab(binding.newShoutoutTxt)
        }

        return binding.root
    }

    private fun closeTab(anTxt: EditText) {
        /* Update create mode */
        model.createMode.value = false

        /* Hide keyboard */
        val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        anTxt.text.clear()
    }

    fun setName(newName: String) {
        name = newName
    }

}