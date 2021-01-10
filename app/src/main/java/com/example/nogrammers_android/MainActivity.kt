package com.example.nogrammers_android

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.announcements.AnnouncementsFragment
import com.example.nogrammers_android.events.EventsFragment
import com.example.nogrammers_android.profile.ProfileFragment
import com.example.nogrammers_android.shoutouts.ShoutoutsFragment
import com.example.nogrammers_android.user.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        /* Create user obj in firebase if it doesn't exist already */
        val userNetID = intent.getStringExtra(NETID_MESSAGE) ?: return
        database = Firebase.database.reference.child("users")
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (!dataSnapshot.hasChild(userNetID)) {
                    database.child(userNetID).setValue(User(userNetID))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(updateListener)

        /* Declare fragments */
        val shoutoutsFrag = ShoutoutsFragment()
        val eventsFrag = EventsFragment()
        val announcementsFrag = AnnouncementsFragment()
        val profileFrag = ProfileFragment(userNetID, database)

        setCurrentFragment(shoutoutsFrag) // Home fragment is shoutouts
        supportActionBar?.title = "Shoutouts"

        /* Fragments are controlled by the bottom nav bar */
        findViewById<BottomNavigationView>(R.id.bottomNavView).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.shoutouts_icon -> {
                    setCurrentFragment(shoutoutsFrag)
                    supportActionBar?.title = "Shoutouts"
                }
                R.id.events_icon -> {
                    setCurrentFragment(eventsFrag)
                    supportActionBar?.title = "Events"
                }
                R.id.announcements_icon -> {
                    setCurrentFragment(announcementsFrag)
                    supportActionBar?.title = "Announcements"
                }
                R.id.profile_icon -> {
                    setCurrentFragment(profileFrag)
                    supportActionBar?.title = "Profile"
                }
            }
            true
        }
    }

    /**
     * Updates the current fragment
     */
    private fun setCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_fragment, fragment)
            commit()
        }
}
