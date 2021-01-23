package com.example.nogrammers_android

import android.graphics.Color
import android.content.Context
import android.os.Bundle
import android.widget.ScrollView
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.announcements.AnnouncementsFragment
import com.example.nogrammers_android.events.AddEventFragment
import com.example.nogrammers_android.events.EventsFragment
import com.example.nogrammers_android.profile.CellClickListener
import com.example.nogrammers_android.profile.EditProfileFragment
import com.example.nogrammers_android.profile.ProfileFragment
import com.example.nogrammers_android.profile.TagSearchFragment
import com.example.nogrammers_android.resources.BlmFragment
import com.example.nogrammers_android.resources.FormsFragment
import com.example.nogrammers_android.resources.ResourcesFragment
import com.example.nogrammers_android.resources.SocialFragment
import com.example.nogrammers_android.shoutouts.ShoutoutsFragment
import com.example.nogrammers_android.user.User
import com.example.nogrammers_android.user.UserObject
import com.example.nogrammers_android.user.UserTags
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference
    var showShoutoutRanking = false
    var userData: MutableList<User> = mutableListOf()
    var showProfileEditIcon = false
    private lateinit var shoutoutsFrag: ShoutoutsFragment
    private lateinit var eventsFrag: EventsFragment
    private lateinit var announcementsFrag: AnnouncementsFragment
    private lateinit var resourcesFrag: ResourcesFragment
    private lateinit var profileFrag: ProfileFragment
    private lateinit var editProfileFrag: EditProfileFragment
    private lateinit var tagSearchFrag: TagSearchFragment
    private lateinit var blmFrag: BlmFragment
    private lateinit var formsFrag: FormsFragment
    private lateinit var socialFrag: SocialFragment
    private lateinit var backArrow: ImageView

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
                    userData.add(
                        User(
                            childUser.netID,
                            childUser.gradYr,
                            childUser.name,
                            childUser.bio,
                            childUser.tags,
                            childUser.admin
                        )
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(updateListener)

        /* Declare fragments */
        shoutoutsFrag = ShoutoutsFragment(userNetID, 1)
        eventsFrag = EventsFragment()
        announcementsFrag = AnnouncementsFragment()
        resourcesFrag = ResourcesFragment()
        blmFrag = BlmFragment()
        formsFrag = FormsFragment()
        socialFrag = SocialFragment()
        profileFrag = ProfileFragment(userNetID, database, true)
        editProfileFrag = EditProfileFragment(userNetID, database)
        tagSearchFrag = TagSearchFragment(object : CellClickListener {
            override fun onCellClickListener(data: String) {
                /* When a tag is selected, show matching users */
                showTaggedUsers(data)
            }
        })

        /* Initialize backArrow, listeners are set later */
        backArrow = findViewById(R.id.tagSearchBackArrow)

        setCurrentFragment(shoutoutsFrag, "Shoutouts") // Home fragment is shoutouts

        /* Fragments are controlled by the bottom nav bar */
        findViewById<BottomNavigationView>(R.id.bottomNavView).setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.shoutouts_icon -> setCurrentFragment(shoutoutsFrag, "Shoutouts")
                R.id.events_icon -> setCurrentFragment(eventsFrag, "Events")
                R.id.announcements_icon -> setCurrentFragment(announcementsFrag, "Announcements")
                R.id.profile_icon -> setCurrentFragment(profileFrag, "Profile")
                R.id.resources_icon -> setCurrentFragment(resourcesFrag, "Resources")
            }
            true
        }

        val searchBarTxt = findViewById<EditText>(R.id.searchBarEditText)
        /* Bring up tag res frag on focus */
        searchBarTxt.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) setCurrentFragment(
                tagSearchFrag,
                ""
            )
        }
        /* Update tag search results */
        val allTags = UserTags.values().map { it.toString() }.sortedBy { it }
        searchBarTxt.addTextChangedListener {
            tagSearchFrag.removeSuggestedLabel()

            val queryStr = searchBarTxt.text.toString()
            /* Show tags by starts with then contains */
            tagSearchFrag.updateTagResults(
                listOf(
                    allTags.filter { it.startsWith(queryStr, true) },
                    allTags.filter { it.contains(queryStr) }).flatten().toSet().toList()
            )
        }
    }

    /**
     * Needed to show the toolbar icons
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_app_bar, menu)
        menu?.getItem(0)?.isVisible = showShoutoutRanking
        menu?.getItem(1)?.isVisible = showProfileEditIcon
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
        showShoutoutRanking = fragment is ShoutoutsFragment
        showProfileEditIcon =
            fragment is ProfileFragment && fragment.showEditIcon //shortcircuit eval ftw
        val searchBarLayout = findViewById<ConstraintLayout>(R.id.searchBarLayout)
        val searchBarBackground = findViewById<ImageView>(R.id.searchBarBackground)

        if (fragment is ProfileFragment || fragment is TagSearchFragment) {
            searchBarLayout.visibility = View.VISIBLE
            searchBarBackground.layoutParams.width = dpToPx(200f)
        } else searchBarLayout.visibility = View.GONE

        /* Show backArrow only for certain pages */
        backArrow.visibility = View.GONE
        if (fragment is TagSearchFragment) {
            searchBarBackground.layoutParams.width = dpToPx(300f)
            backArrow.visibility = View.VISIBLE
            backArrow.setOnClickListener {
                /* Hide keyboard and clear search */
                loseSearchBarFocus()

                /* Replace fragment */
                setCurrentFragment(profileFrag, "Profile")
            }
        }
        if (fragment is BlmFragment || fragment is FormsFragment || fragment is SocialFragment) {
            backArrow.visibility = View.VISIBLE
            backArrow.setOnClickListener {
                /* Replace fragment */
                setCurrentFragment(resourcesFrag, "Resources")
            }
        }

        /* Replace fragment */
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_fragment, fragment)
            commit()
        }
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

    fun addEvent(view: View) {
        // Create new fragment
        setCurrentFragment(AddEventFragment(), "New Event")
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
                /* Two possible forms: Timothy Goh (tmg5) or tmg5 */
                var selectedNetId = data
                if (data.contains("(")) selectedNetId =
                    data.substring(data.indexOf("(") + 1, data.indexOf((")")))
                /* Navigate to it */
                setCurrentFragment(ProfileFragment(selectedNetId, database, false), "Profile")
                loseSearchBarFocus()
            }
        })

        /* Show matching users */
        tagSearchFrag.updateTagResults(userData.filter {
            it.tags.contains(
                UserTags.textToUserTag(
                    selectedTag
                )
            )
        }.map { if (it.name != "Add your name here!") "${it.name} (${it.netID})" else it.netID })
    }

    /**
     * Hides keyboard and removes focus from edit text
     */
    private fun loseSearchBarFocus() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        findViewById<EditText>(R.id.searchBarEditText).text.clear()
        findViewById<EditText>(R.id.searchBarEditText).clearFocus()
    }

    /**
     * Adapter to navigate to blm fragment
     */
    fun setBlmFragAdapter() {
        setCurrentFragment(blmFrag, "")
    }

    /**
     * Adapter to navigate to forms fragment
     */
    fun setFormsFragAdapter() {
        setCurrentFragment(formsFrag, "")
    }

    /**
     * Adapter to navigate to forms fragment
     */
    fun setSocialFragAdapter() {
        setCurrentFragment(socialFrag, "")
    }

    /**
     * Utility function to convert dp -> px
     */
    private fun dpToPx(dp: Float): Int =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics).toInt()
}
