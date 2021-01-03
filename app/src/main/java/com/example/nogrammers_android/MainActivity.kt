package com.example.nogrammers_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        /* Declare fragments */
        val shoutoutsFrag = ShoutoutsFragment()
        val eventsFrag = EventsFragment()

        setCurrentFragment(shoutoutsFrag) // Home fragment is shoutouts

        /* Fragments are controlled by the bottom nav bar */
        findViewById<BottomNavigationView>(R.id.bottomNavView).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.shoutouts_icon -> setCurrentFragment(shoutoutsFrag)
                R.id.events_icon -> setCurrentFragment(eventsFrag)
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
