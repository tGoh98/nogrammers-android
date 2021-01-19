package com.example.nogrammers_android

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.announcements.AnnouncementsFragment
import com.example.nogrammers_android.events.EventsFragment
import com.example.nogrammers_android.profile.EditProfileFragment
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
    var showEditIcon = false
    var showShoutoutRanking = false
    private lateinit var shoutoutsFrag: ShoutoutsFragment
    private lateinit var eventsFrag: EventsFragment
    private lateinit var announcementsFrag: AnnouncementsFragment
    private lateinit var profileFrag: ProfileFragment
    private lateinit var editProfileFrag: EditProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        /* Show toolbar and icons */
        setSupportActionBar(findViewById(R.id.toolbar))
        invalidateOptionsMenu()

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
        shoutoutsFrag = ShoutoutsFragment()
        eventsFrag = EventsFragment()
        announcementsFrag = AnnouncementsFragment()
        profileFrag = ProfileFragment(userNetID, database)
        editProfileFrag = EditProfileFragment(userNetID, database)

        setCurrentFragment(shoutoutsFrag, "Shoutouts") // Home fragment is shoutouts

        /* Fragments are controlled by the bottom nav bar */
        findViewById<BottomNavigationView>(R.id.bottomNavView).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.shoutouts_icon -> setCurrentFragment(shoutoutsFrag, "Shoutouts")
                R.id.events_icon -> setCurrentFragment(eventsFrag, "Events")
                R.id.announcements_icon -> setCurrentFragment(announcementsFrag, "Announcements")
                R.id.profile_icon -> setCurrentFragment(profileFrag, "Profile")
            }
            true
        }
    }

    /**
     * Needed to show the toolbar icons
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        menu?.getItem(0)?.isVisible = showShoutoutRanking
        menu?.getItem(1)?.isVisible = showEditIcon
        return true
    }

    /**
     * Handles top menu icon clicks
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editProfileIcon -> setCurrentFragment(editProfileFrag, "Edit Profile")
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    /**
     * Updates the current fragment
     */
    private fun setCurrentFragment(fragment: Fragment, tabTitle: String) {
        invalidateOptionsMenu()
        supportActionBar?.title = tabTitle
        showEditIcon = fragment is ProfileFragment // Only display edit icon for profile main page
        showShoutoutRanking = fragment is ShoutoutsFragment

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_fragment, fragment)
            commit()
        }
    }

    /**
     * Adapter to reset current fragment
     */
    fun setProfileFragAdapter() {
        setCurrentFragment(profileFrag, "Profile")
    }
}
