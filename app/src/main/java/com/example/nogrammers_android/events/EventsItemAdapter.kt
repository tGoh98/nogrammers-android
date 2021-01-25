package com.example.nogrammers_android.events

import android.R
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.databinding.EventItemBinding
import com.example.nogrammers_android.user.User
import com.example.nogrammers_android.user.UserObject
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.ArrayList

class EventsItemAdapter(private val data: List<Event>, val netid: String, val clickListener: EventsItemListener) :
        RecyclerView.Adapter<EventsItemAdapter.EventViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.from(parent, netid)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item, clickListener)
    }

    override fun getItemCount() = data.size

    class EventViewHolder private constructor(val binding: EventItemBinding, val netid: String) :
            RecyclerView.ViewHolder(binding.root), AdapterView.OnItemSelectedListener {

        fun bind(item: Event, clickListener: EventsItemListener) {
            binding.event = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
            if (item.interestedUsers.contains(netid)) {
                binding.markAs.setSelection(1)
            }
            else if (item.goingUsers.contains(netid)) {
                binding.markAs.setSelection(2)
            }
            binding.markAs.onItemSelectedListener = this
            /* Set custom image */
            val storageRef = Firebase.storage.reference.child("eventPics").child(item.key)
            val ONE_MEGABYTE: Long = 1024 * 1024 * 5
            storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
                /* Found pic, set it */
                val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
                val eventImgView = binding.eventImage
                eventImgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, eventImgView.width, eventImgView.height, false))
            }.addOnFailureListener {
                /* Not found/error, use default */
                Log.e("TAG", "Could not find event pic, using default image " + item.key)
            }
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val event = binding.event
            if (parent != null && event != null) {
                    if (position != 1) {
                        // remove user from events interested list
                        event.removeUserFromInterested(netid)
                        // remove event from user's interested list
                        Firebase.database.reference.child("users").child(netid).
                            child("interestedEvents").child(event.key).removeValue()
                    }
                    if (position != 2) {
                        // remove user from events going list
                        event.removeUserFromGoing(netid)
                        // remove event from user's going list
                        Firebase.database.reference.child("users").child(netid).
                            child("goingEvents").child(event.key).removeValue()
                    }
                    if (position == 1) {
                        // add user to events interested list and add event to user's interested events
                        event.addUserToInterested(netid)
                        // add event to user's interested list
                        updateUser(event.key, "interestedEvents")
                    }
                    else if (position == 2) {
                        // add user to events going list
                        event.addUserToGoing(netid)
                        // add event to user's going list
                        updateUser(event.key, "goingEvents")
                    }
                    postEventToFirebase(event)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            Log.d("Tag","nothing selected")
        }

        private fun postEventToFirebase (event: Event) {
            val database = Firebase.database.reference.child("events").child(event.key)
            database.setValue(event)
        }

        private fun updateUser (key: String, field: String) {
            val database = Firebase.database.reference.child("users").child(netid).child(field)
            database.child(key).setValue(0)
        }

        companion object {
            lateinit var binding: EventItemBinding

            fun from(parent: ViewGroup, netid: String): EventViewHolder {
                binding = EventItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
                val markAsOptions = listOf("Mark as", "Interested",
                        "Going")
                binding.markAs.adapter = ArrayAdapter(parent.context, R.layout.simple_spinner_item, markAsOptions)
                return EventViewHolder(binding, netid)
            }
        }
    }
}

class EventsItemListener(val clickListener: (event: Event) -> Unit) {
    fun onClick(event : Event) = clickListener(event)
}