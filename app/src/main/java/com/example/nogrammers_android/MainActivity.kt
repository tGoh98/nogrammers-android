package com.example.nogrammers_android

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.announcements.AnnouncementsFragment
import com.example.nogrammers_android.events.AddEventFragment
import com.example.nogrammers_android.events.EventsFragment
import com.example.nogrammers_android.profile.ProfileFragment
import com.example.nogrammers_android.shoutouts.ShoutoutsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        /* Declare fragments */
        val shoutoutsFrag = ShoutoutsFragment()
        val eventsFrag = EventsFragment()
        val announcementsFrag = AnnouncementsFragment()
        val profileFrag = ProfileFragment()

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

    /**
     * Listeners for events tab
     */
    fun handleTag(view: View) {
        val button = findViewById<MaterialButton>(view.id)
        if (button.currentTextColor == Color.parseColor("#757575")) {
            button.setTextColor(Color.parseColor("#FFFFFFFF"))
            button.setBackgroundColor(Color.parseColor("#313d23"))
        }
        else {
            button.setTextColor(Color.parseColor("#757575"))
            button.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
        }
    }

    fun applyFilter(view: View) {
        findViewById<ScrollView>(R.id.filterLayout).visibility = View.GONE
    }

    fun addEvent(view: View) {
        // Create new fragment
        setCurrentFragment(AddEventFragment())
        supportActionBar?.title = "New Event"
    }

}
