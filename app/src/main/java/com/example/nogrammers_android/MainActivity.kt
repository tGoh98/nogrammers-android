package com.example.nogrammers_android

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.announcements.AnnouncementsFragment
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {

    lateinit var dbRefUsers: DatabaseReference
    lateinit var database: DatabaseReference
    lateinit var curUser: User
    var showShoutoutRanking = false
    var userData: MutableList<User> = mutableListOf()
    var showProfileEditIcon = false
    lateinit var netId: String
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
    private lateinit var selectedNetId: String

    /* Singleton click handler objects for tag search results */
    private val showMatchingTagUsersListener = object : CellClickListener {
        override fun onCellClickListener(data: String) {
            /* When a tag is selected, show matching users */
            showTaggedUsers(data)
        }
    }
    private val showSelectedTagUserListener = object : CellClickListener {
        override fun onCellClickListener(data: String) {
            /* Two possible forms: Timothy Goh (tmg5) or tmg5 */
            selectedNetId = data
            if (data.contains("(")) selectedNetId =
                    data.substring(data.indexOf("(") + 1, data.indexOf((")")))
            /* Navigate to it */
            val showEditIcon = curUser.tags.contains(UserTags.Admin)
            setCurrentFragment(ProfileFragment(selectedNetId, dbRefUsers, showEditIcon), "Profile")
            loseSearchBarFocus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        /* Show toolbar and icons */
        setSupportActionBar(findViewById(R.id.toolbar))
        invalidateOptionsMenu()

        /* Get data from firebase */
        val userNetID = intent.getStringExtra(NETID_MESSAGE) ?: return
        netId = userNetID
        database = Firebase.database.reference
        dbRefUsers = database.child("users")
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userData.clear()
                /* Create new user if not exist */
                if (!dataSnapshot.hasChild(userNetID)) {
                    dbRefUsers.child(userNetID).setValue(User(userNetID))
                    userData.add(User(userNetID))
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
                            )
                    )
                }
                /* Set user */
                curUser = userData.filter { it.netID == userNetID }[0]
                selectedNetId = curUser.netID // Default selectedNetId to current user's
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        dbRefUsers.addValueEventListener(updateListener)

        /* Declare fragments */
        shoutoutsFrag = ShoutoutsFragment(userNetID, 1)
        eventsFrag = EventsFragment()
        announcementsFrag = AnnouncementsFragment(database)
        resourcesFrag = ResourcesFragment()
        blmFrag = BlmFragment()
        formsFrag = FormsFragment()
        socialFrag = SocialFragment()
        profileFrag = ProfileFragment(userNetID, dbRefUsers, true)
        editProfileFrag = EditProfileFragment(userNetID, dbRefUsers)
        tagSearchFrag = TagSearchFragment(showMatchingTagUsersListener)

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
            if (hasFocus) {
                /* Update fragment */
                setCurrentFragment(tagSearchFrag, "")
                /* Reset click listener - case where user sees results and searches again */
                resetTagSearchListener()
            }
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

        val menuItem = menu?.findItem(R.id.app_bar_switch)
        val view = menuItem?.actionView
        val switchLayout = view?.findViewById<RelativeLayout>(R.id.switchRelativeLayout)
        switchLayout?.findViewById<SwitchCompat>(R.id.switchAB)?.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                shoutoutsFrag.forceUpdate(-1)
            } else {
                shoutoutsFrag.forceUpdate(1)
            }
        }

        return true
    }

    /**
     * Handles top menu icon clicks
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.editProfileIcon -> {
                if (selectedNetId == curUser.netID) setCurrentFragment(editProfileFrag, "Edit Profile")
                else setCurrentFragment(EditProfileFragment(selectedNetId, dbRefUsers), "Edit Profile")
            }
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

        /* Show backArrow only for certain pages */
        backArrow.visibility = View.GONE

        when (fragment) {
            is ProfileFragment -> {
                /* Show small search bar */
                searchBarLayout.visibility = View.VISIBLE
                searchBarBackground.layoutParams.width = dpToPx(200f)
                searchBarBackground.requestLayout()
            }
            is TagSearchFragment -> {
                /* Show big search bar */
                searchBarLayout.visibility = View.VISIBLE
                searchBarBackground.layoutParams.width = dpToPx(300f)

                /* And back arrow */
                backArrow.visibility = View.VISIBLE
                backArrow.setOnClickListener {
                    /* Hide keyboard and clear search */
                    loseSearchBarFocus()

                    /* Replace fragment */
                    setCurrentFragment(profileFrag, "Profile")
                }
            }
            else -> searchBarLayout.visibility = View.GONE // Nothing
        }

        /* Back arrow listener */
        if (fragment is BlmFragment || fragment is FormsFragment || fragment is SocialFragment) {
            backArrow.visibility = View.VISIBLE
            backArrow.setOnClickListener {
                /* Replace fragment */
                setCurrentFragment(resourcesFrag, "Resources") // Go back to res page
            }
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
        if (selectedNetId == curUser.netID) setCurrentFragment(profileFrag, "Profile")
        else setCurrentFragment(ProfileFragment(selectedNetId, dbRefUsers, curUser.tags.contains(UserTags.Admin)), "Profile")
    }

    /**
     * Given a chosen tag, shows the users containing that tag
     */
    private fun showTaggedUsers(selectedTag: String) {
        /* Update click listener to handle a selected user */
        tagSearchFrag.updateClickListener(showSelectedTagUserListener)

        /* Remove suggested tags */
        tagSearchFrag.removeSuggestedLabel()

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
     * Resets click listener to show tagged user results
     */
    private fun resetTagSearchListener() {
        if (tagSearchFrag.clickListener == showSelectedTagUserListener) tagSearchFrag.updateClickListener(showMatchingTagUsersListener)
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

    /**
     * Causes all EditText's to lose focus on touching outside the field
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
