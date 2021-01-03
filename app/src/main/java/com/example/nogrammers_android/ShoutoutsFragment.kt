package com.example.nogrammers_android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.databinding.FragmentShoutoutsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class ShoutoutsFragment : Fragment() {
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentShoutoutsBinding>(
            inflater,
            R.layout.fragment_shoutouts, container, false
        )

        var authors = listOf(
            "adrienne",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim"
        ).map { Shoutouts(it, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.") }
        authors = authors.toMutableList()
        val adapter = ShoutoutsAdapter(authors)

        database = Firebase.database.reference.child("shoutouts")
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val so = dataSnapshot.getValue<ArrayList<HashMap<String, String>>>()
                if (so != null) {
                    authors.clear()
                    for (map in so) {
                        map["author"]?.let {
                            map["msg"]?.let { it1 ->
                                map["date"]?.let { it2 ->
                                    Shoutouts(
                                        it,
                                        it1,
                                        it2
                                    )
                                }
                            }
                        }?.let { authors.add(it) }
                    }
                    authors.sortByDescending { it.date }
                    adapter.notifyDataSetChanged()
                }
                Log.d("TAG", so.toString())
                Log.d("TAG2", authors.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(updateListener)

        binding.shoutoutsView.adapter = adapter

        // Button listeners
        // Cancel, clear text fields and hide keyboard
        binding.cancelButton.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            binding.editTextName.text.clear()
            binding.editTextMsg.text.clear()
        }
        // Post, add shoutout to firebase, then cancel
        binding.postButton.setOnClickListener {
            var num = 0
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val so = dataSnapshot.getValue<ArrayList<HashMap<String, String>>>()
                    if (so != null) {
                        num = so.size
                    }
                    database.child(num.toString()).setValue(
                        Shoutouts(
                            binding.editTextName.text.toString(),
                            binding.editTextMsg.text.toString(),
                            java.util.Calendar.getInstance().timeInMillis.toString()
                        )
                    )
                    binding.cancelButton.performClick()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.addListenerForSingleValueEvent(postListener)
        }

        return binding.root
    }
}