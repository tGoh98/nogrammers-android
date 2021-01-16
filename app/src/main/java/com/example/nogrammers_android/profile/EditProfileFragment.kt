package com.example.nogrammers_android.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.FragmentEditProfileBinding
import com.example.nogrammers_android.user.User
import com.example.nogrammers_android.user.UserObject
import com.example.nogrammers_android.user.UserTags
import com.google.android.material.chip.Chip
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


class EditProfileFragment(private val userNetID: String, private val dbUserRef: DatabaseReference) : Fragment() {

    lateinit var binding: FragmentEditProfileBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        /* Inflate the layout for this fragment */
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)

        /* Populate with user information */
        val database = dbUserRef.child(userNetID)
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // TODO: add fail check
                val userObjTemp = dataSnapshot.getValue(UserObject::class.java) as UserObject
                val userObj = User(userObjTemp.netID, userObjTemp.gradYr, userObjTemp.name, userObjTemp.bio, userObjTemp.tags, userObjTemp.admin)

                updateUI(userObj)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(updateListener)

        /* Save button */
        binding.editProfileSaveBtn.setOnClickListener {
            /* Update user obj */
            val updatedUserObj = User(userNetID)
            val newName = binding.editProfileNameField.text.toString()
            val newBio = binding.editProfileBioField.text.toString()
            if (newName == "") updatedUserObj.name = binding.editProfileNameField.hint.toString()
            else updatedUserObj.name = newName
            if (newBio == "") updatedUserObj.bio = binding.editProfileBioField.hint.toString()
            else updatedUserObj.bio = newBio

            val chipArr: MutableList<UserTags> = ArrayList()
            /* Convert chip texts to enum */
            for (chip in binding.editProfileChips) {
                /* Deleted chips have visibility = GONE */
                if (chip.visibility == View.GONE) continue

                when ((chip as Chip).text.toString().filter { !it.isWhitespace() }) {
                    "+Addtag" -> continue
                    "H&DRep" -> chipArr.add(UserTags.HAndDRep)
                    else -> chipArr.add(UserTags.valueOf(chip.text.toString().filter { !it.isWhitespace() }))
                }
            }
            updatedUserObj.tags = chipArr

            /* Write to firebase */
            dbUserRef.child(userNetID).setValue(updatedUserObj)

            closeEditProfile()
        }

        /* Cancel button */
        binding.editProfileCancelBtn.setOnClickListener {
            closeEditProfile()
        }

        return binding.root
    }

    /**
     * Given a user obj, updates the corresponding UI elements
     */
    private fun updateUI(userObj: User) {
        binding.editProfileNameField.hint = userObj.name
        binding.editProfileBioField.hint = userObj.bio

        val chipGroup = binding.editProfileChips
        for (tag in userObj.tags) {
            val chip = Chip(context)
            chip.text = tag.toString()
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                Toast.makeText(context, "close clicked!", Toast.LENGTH_SHORT).show()
                chip.visibility = View.GONE
            }
            chipGroup.addView(chip, chipGroup.size - 1)
        }
    }

    /**
     * Clears fields and navigates frag back to profile
     */
    private fun closeEditProfile() {
        binding.editProfileNameField.text.clear()
        binding.editProfileBioField.text.clear()
        (activity as MainActivity).setProfileFragAdapter()
    }
}