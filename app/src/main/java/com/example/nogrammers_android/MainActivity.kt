package com.example.nogrammers_android

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.announcements.AnnouncementsFragment
import com.example.nogrammers_android.events.EventsFragment
import com.example.nogrammers_android.profile.CellClickListener
import com.example.nogrammers_android.profile.EditProfileFragment
import com.example.nogrammers_android.profile.ProfileFragment
import com.example.nogrammers_android.profile.TagSearchFragment
import com.example.nogrammers_android.shoutouts.ShoutoutsFragment
import com.example.nogrammers_android.user.User
import com.example.nogrammers_android.user.UserObject
import com.example.nogrammers_android.user.UserTags
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference
    var userData: MutableList<User> = mutableListOf()
    var isProfilePage = false
    private lateinit var shoutoutsFrag: ShoutoutsFragment
    private lateinit var eventsFrag: EventsFragment
    private lateinit var announcementsFrag: AnnouncementsFragment
    private lateinit var profileFrag: ProfileFragment
    private lateinit var editProfileFrag: EditProfileFragment
    private lateinit var tagSearchFrag: TagSearchFragment

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
                userData.clear()
                if (!dataSnapshot.hasChild(userNetID)) {
                    database.child(userNetID).setValue(User(userNetID))
                }
                for (child in dataSnapshot.children) {
                    val childUser = child.getValue(UserObject::class.java) as UserObject
                    userData.add(User(childUser.netID, childUser.gradYr, childUser.name, childUser.bio, childUser.tags, childUser.admin))
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(updateListener)

        /* Declare fragments */
        shoutoutsFrag = ShoutoutsFragment()
        eventsFrag = EventsFragment()
        announcementsFrag = AnnouncementsFragment()
        profileFrag = ProfileFragment(userNetID, database)
        editProfileFrag = EditProfileFragment(userNetID, database)
        tagSearchFrag = TagSearchFragment(object : CellClickListener {
            override fun onCellClickListener(data: String) {
                /* When a tag is selected, show matching users */
                showTaggedUsers(data)
            }
        })

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

        val searchBarTxt = findViewById<EditText>(R.id.searchBarEditText)
        /* Bring up tag res frag on focus */
        searchBarTxt.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) setCurrentFragment(tagSearchFrag, "") }
        /* Update results */
        val allTags = UserTags.values().map { it.toString() }.sortedBy { it }
        searchBarTxt.addTextChangedListener {
            tagSearchFrag.removeSuggestedLabel()

            val queryStr = searchBarTxt.text.toString()
            /* Show tags by starts with then contains */
            tagSearchFrag.updateTagResults(listOf(allTags.filter { it.startsWith(queryStr, true) }, allTags.filter { it.contains(queryStr) }).flatten().toSet().toList())
        }

        /* Listener for tag search back button */
        findViewById<ImageView>(R.id.tagSearchBackArrow).setOnClickListener {
            /* Hide keyboard and clear search */
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
            searchBarTxt.text.clear()
            searchBarTxt.clearFocus()

            /* Replace fragment */
            setCurrentFragment(profileFrag, "Profile")
        }
    }

    /**
     * Needed to show the toolbar icons
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        menu?.getItem(0)?.isVisible = isProfilePage
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
        /* Only show search bar stuff for profile page */
        invalidateOptionsMenu()
        supportActionBar?.title = tabTitle
        isProfilePage = fragment is ProfileFragment
        val searchBarLayout = findViewById<ConstraintLayout>(R.id.searchBarLayout)
        val searchBarBackground = findViewById<ImageView>(R.id.searchBarBackground)
        val tagSearchBackArrow = findViewById<ImageView>(R.id.tagSearchBackArrow)
        if (isProfilePage || fragment is TagSearchFragment) {
            searchBarLayout.visibility = View.VISIBLE
            tagSearchBackArrow.visibility = View.GONE
            searchBarBackground.layoutParams.width = dpToPx(200f)
        } else searchBarLayout.visibility = View.GONE
        if (fragment is TagSearchFragment) {
            searchBarBackground.layoutParams.width = dpToPx(300f)
            tagSearchBackArrow.visibility = View.VISIBLE
        }

        /* Replace fragment */
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

    /**
     * Given a chosen tag, shows the users containing that tag
     */
    private fun showTaggedUsers(selectedTag: String) {
        /* Update click listener to handle a selected user */
        tagSearchFrag.updateClickListener(object : CellClickListener {
            override fun onCellClickListener(data: String) {
                Toast.makeText(applicationContext, "Selected user: $data", Toast.LENGTH_SHORT).show()
            }
        })

        /* Show matching users */
        tagSearchFrag.updateTagResults(userData.filter { it.tags.contains(UserTags.textToUserTag(selectedTag)) }.map { if (it.name != "Add your name here!") it.name else it.netID })
    }

    /**
     * Utility function to convert dp -> px
     */
    private fun dpToPx(dp: Float): Int = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}
