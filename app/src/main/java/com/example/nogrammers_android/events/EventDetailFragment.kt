package com.example.nogrammers_android.events

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toolbar
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.example.nogrammers_android.databinding.FragmentEventDetailBinding
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.user.User
import com.example.nogrammers_android.user.UserObject
import com.example.nogrammers_android.user.UserTags
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

/**
 * Fragment for seeing an event's details
 */
class EventDetailFragment(val event: Event, val netid: String) : Fragment() {

    lateinit var binding : FragmentEventDetailBinding

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate<FragmentEventDetailBinding>(
                inflater,
                R.layout.fragment_event_detail, container, false
        )

        checkAdmin()
        if (activity is MainActivity) {
            (activity as MainActivity).supportActionBar?.setTitle(event.title)
        }

        binding.usersAttending.text = event.interestedUsers.size.toString() + " Interested, " +
                event.goingUsers.size.toString() + " Going"
        setAuthorFields()
        binding.eventDate.text = DateTimeUtil.getStringFromDateinMillis(event.start) + "\n" +
                DateTimeUtil.getStringFromTimeinMillis(event.start) +
                " to " + DateTimeUtil.getStringFromTimeinMillis(event.end)
        binding.eventLocation.text = event.location
        binding.remoteBox.isChecked = event.remote
        binding.eventDescription.text = event.desc
        binding.campusWideBox.isChecked = (event.audience.equals(context?.resources?.getString(R.string.Campus_wide)))
        binding.duncaroosBox.isChecked = (event.audience.equals(context?.resources?.getString(R.string.Duncaroos_only)))
        for(child in binding.tagGroup.children) {
            if (child is Button && event.tags.contains(child.text.toString())) {
                child.isClickable = false
                this.context?.let { getColor(it, R.color.white) }?.let { child.setTextColor(it) }
                this.context?.let { getColor(it, R.color.olive) }?.let { child.setBackgroundColor(it) }
            }
            else if (child is Button) {
                child.visibility = View.GONE
            }
        }
        binding.adminButton.setOnClickListener {
            childFragmentManager.let {
                AdminBottomSheetFragment.newInstance(Bundle(), event).apply {
                    show(it, tag)
                }
            }
        }

        /* Set custom image */
        val storageRef = Firebase.storage.reference.child("eventPics").child(event.key)
        val ONE_MEGABYTE: Long = 1024 * 1024 * 5
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            /* Found pic, set it */
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val eventImgView = binding.eventImage
            eventImgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, eventImgView.width, eventImgView.height, false))
        }.addOnFailureListener {
            /* Not found/error, use default */
            Log.e("TAG", "Could not find event pic, using default image " + event.key)
            binding.eventImage.setImageResource(R.drawable.testimg)
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.add_event_actions, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Close fragment when action bar button is pressed
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        this.activity?.findViewById<BottomNavigationItemView>(R.id.events_icon)?.performClick()
        return super.onOptionsItemSelected(item)
    }

    fun checkAdmin() {
        val database = Firebase.database.reference.child("users").child(netid)
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // TODO: add fail check
                val userObjTemp = dataSnapshot.getValue(UserObject::class.java) as UserObject
                val userObj = User(
                        userObjTemp.netID,
                        userObjTemp.gradYr,
                        userObjTemp.name,
                        userObjTemp.bio,
                        userObjTemp.tags,
                        userObjTemp.interestedEvents,
                        userObjTemp.goingEvents
                )

                if (userObj.tags.contains(UserTags.Admin)) {
                    binding.adminButton.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(updateListener)
    }

    fun setAuthorFields() {
        val database = Firebase.database.reference.child("users").child(event.author)
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // TODO: add fail check
                val userObjTemp = dataSnapshot.getValue(UserObject::class.java) as UserObject
                val userObj = User(
                        userObjTemp.netID,
                        userObjTemp.gradYr,
                        userObjTemp.name,
                        userObjTemp.bio,
                        userObjTemp.tags,
                        userObjTemp.interestedEvents,
                        userObjTemp.goingEvents
                )
                binding.author.text = "Posted by " + userObj.name
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(updateListener)

        val storageRef = Firebase.storage.reference.child("profilePics").child(event.author)
        val ONE_MEGABYTE: Long = 1024 * 1024 * 5
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            /* Found pic, set it */
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val eventImgView = binding.pfpImgProfileSrc
            eventImgView.setImageBitmap(Bitmap.createScaledBitmap(bmp, eventImgView.width, eventImgView.height, false))
        }.addOnFailureListener {
            /* Not found/error, use default */
            Log.e("TAG", "Could not find event pic, using default image " + event.key)
            binding.pfpImgProfileSrc.setImageResource(R.drawable.testimg)
        }
    }

}