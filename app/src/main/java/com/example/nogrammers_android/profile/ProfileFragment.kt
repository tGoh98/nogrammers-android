package com.example.nogrammers_android.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentProfileBinding
import com.example.nogrammers_android.user.User
import com.example.nogrammers_android.user.UserObject
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

// View pager tutorial: https://www.raywenderlich.com/8192680-viewpager2-in-android-getting-started

/**
 * Profile tab
 */
class ProfileFragment(
    private val netID: String,
    private val dbUserRef: DatabaseReference,
    val showEditIcon: Boolean
) : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_profile, container, false
        )

        /* Create and set tabs adapter */
        binding.profileTabsPager.adapter = ProfileTabsAdapter(activity as AppCompatActivity, 2)

        /* Tab layout mediator to connect tab labels to ViewPager */
        TabLayoutMediator(binding.profileTabLayout, binding.profileTabsPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Posts"
                else -> tab.text = "Mentions"
            }
        }.attach()

        // Create dummy users
//        val db = Firebase.database.reference.child("users")
//        val userObjs = listOf(
//                User("tmg5", 2022, "Timothy Goh", "I like boba", arrayListOf(UserTags.JuniorRep, UserTags.HAndDRep, UserTags.OWeekCoordinator)),
//                User("al84", 2023, "Adrienne Li", "I am the master coder", arrayListOf(UserTags.FreshmanRep, UserTags.PAA, UserTags.CCCRep)),
//                User("cmz2", 2022, "Christina Zhou", "Archi is my passion", arrayListOf(UserTags.SophomoreRep, UserTags.RHA, UserTags.DiversityFacilitator)),
//                User("cbk1", 2022, "Colin King", "Gym bro", arrayListOf(UserTags.BeerBikeCaptain, UserTags.Historian, UserTags.SocialsHead)),
//                User("rjp5", 2023, "Julie Park", "Wakes up before the sun everyday :D", arrayListOf(UserTags.STRIVELiason, UserTags.PHA, UserTags.CCCRep)),
//                User("cys4", 2023, "Cindy Sheng", "Join Rice Design y'all", arrayListOf(UserTags.SophomoreRep, UserTags.FreshmanRep, UserTags.JuniorRep)),
//                User("jdh16", 2021, "Johnny Ho", "Forza? ez", arrayListOf(UserTags.SeniorRep))
//        )

//        for (userObj in userObjs) db.child(userObj.netID).setValue(userObj)

        val database = dbUserRef.child(netID)
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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

                updateUI(userObj)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed
            }
        }
        database.addListenerForSingleValueEvent(updateListener)

        return binding.root
    }

    /**
     * Given a user obj, updates the corresponding UI elements
     */
    private fun updateUI(userObj: User) {
        binding.profileNameNetId.text = userObj.netID
        binding.profileNameTxt.text = userObj.name
        binding.bioTxt.text = userObj.bio

        val chipGroup = binding.profileChips
        for (tag in userObj.tags) {
            val chip = Chip(context)
            // Note: crashes with NPE error if firebase tag array doesn't start with index 0
            chip.text = tag.toString()
            chipGroup.addView(chip)
        }

        /* Pfp */
        val storageRef = Firebase.storage.reference.child("profilePics").child(netID)
        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            /* Found pfp, set it */
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val pfpView = binding.pfpImgProfileSrc
            pfpView.setImageBitmap(Bitmap.createScaledBitmap(bmp, pfpView.width, pfpView.height, false))
        }.addOnFailureListener {
            /* Not found/error, use default */
        }
    }
}
