package com.example.nogrammers_android.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream


class EditProfileFragment(private val userNetID: String, private val dbUserRef: DatabaseReference) :
        Fragment() {

    lateinit var binding: FragmentEditProfileBinding
    lateinit var userObj: User
    var pfpUpdated = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment */
        binding =
                DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false)

        /* Populate with user information */
        val database = dbUserRef.child(userNetID)
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // TODO: add fail check
                val userObjTemp = dataSnapshot.getValue(UserObject::class.java) as UserObject
                userObj = User(
                        userObjTemp.netID,
                        userObjTemp.gradYr,
                        userObjTemp.name,
                        userObjTemp.bio,
                        userObjTemp.tags
                )

                updateUI(userObj)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addListenerForSingleValueEvent(updateListener)

        /* Add tag chip */
        binding.chipAddTag.setOnClickListener {
            val availTags =
                    UserTags.values().filter { it !in userObj.tags && it != UserTags.UnknownTag }
                            .map { it.toString() }
                            .sortedBy { it }.toTypedArray()
            val initialCheckedItem = 0
            var chosenIndex = initialCheckedItem

            context?.let { it1 ->
                MaterialAlertDialogBuilder(it1)
                        .setTitle("Which tag do you want to add?")
                        .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                            /* Do nothing for cancel */
                        }
                        .setPositiveButton("Add tag") { _, _ ->
                            /* Add selected tag */
                            val newTag = UserTags.textToUserTag(availTags[chosenIndex])
                            userObj.tags.add(newTag)
                            addChip(newTag)
                        }
                        .setSingleChoiceItems(availTags, initialCheckedItem) { _, which ->
                            /* Update selected item */
                            chosenIndex = which
                        }
                        .show()
            }
        }

        /* Save button */
        binding.editProfileSaveBtn.setOnClickListener {
            // TODO: Add validation/constraints (e.g. name cannot have special characters like parentheses)
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
                /* Deleted chips have visibility = GONE, don't include them */
                if (chip.visibility == View.GONE) continue

                val chipText = (chip as Chip).text.toString()
                /* Skip "+ Add tag" */
                if (chipText == "+ Add tag") continue
                chipArr.add(UserTags.textToUserTag(chipText))
            }
            updatedUserObj.tags = chipArr

            /* Write to firebase */
            dbUserRef.child(userNetID).setValue(updatedUserObj)

            /* Upload pfp image to firebase if it changed */
            if (pfpUpdated) {
                val storageRef = Firebase.storage.reference.child("profilePics").child(userNetID)
                // Get the data from an ImageView as bytes
                //        imageView.isDrawingCacheEnabled = true <-- these two lines were deprecated but everything still seems to work ok
                //        imageView.buildDrawingCache()          <-- these two lines were deprecated but everything still seems to work ok
                val bitmap = (binding.editProfilePfp.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imgData = baos.toByteArray()

                val uploadTask = storageRef.putBytes(imgData)
                uploadTask.addOnFailureListener {
                    // Handle unsuccessful uploads
                    Log.d("TAG", "profile upload failed")
                }.addOnSuccessListener { taskSnapshot ->
                    // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                    // ...
                    Log.d("TAG", "Profile upload succeeded. $taskSnapshot")
                    closeEditProfile()
                }
            } else closeEditProfile()
        }

        /* Cancel button */
        binding.editProfileCancelBtn.setOnClickListener {
            closeEditProfile()
        }

        /* Edit pfp */
        /* Create launcher for choosing an image */
        val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                /* Update view with chosen image */
                val uri = res.data!!.data
                binding.editProfilePfp.setImageURI(uri)
                pfpUpdated = true
            }
        }
        /* Create launcher for getting permission to photo gallery */
        val requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()
            ) { isGranted: Boolean ->
                if (isGranted) {
                    /* Permission is granted, let user choose image */
                    val imageChooserIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).setType("image/*")
                    resultLauncher.launch(imageChooserIntent)
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // features requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Toast.makeText(context, "Please enable photos permission to change your profile picture :(", Toast.LENGTH_SHORT).show()
                }
            }
        /* Set listeners */
        val editPfpListener = View.OnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        binding.editProfileCameraCard.setOnClickListener(editPfpListener)
        binding.editProfilePfpCard.setOnClickListener(editPfpListener)

        return binding.root
    }

    /**
     * Given a user obj, updates the corresponding UI elements
     */
    private fun updateUI(userObj: User) {
        binding.editProfileNameField.hint = userObj.name
        binding.editProfileBioField.hint = userObj.bio

        for (userTag in userObj.tags) {
            addChip(userTag)
        }

        /* Pfp */
        val storageRef = Firebase.storage.reference.child("profilePics").child(userNetID)
        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            /* Found pfp, set it */
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            val pfpView = binding.editProfilePfp
            pfpView.setImageBitmap(Bitmap.createScaledBitmap(bmp, pfpView.width, pfpView.height, false))
        }.addOnFailureListener {
            /* Not found/error, use default */
            Log.e("TAG", "Could not find profile pic, using default image")
        }
    }

    /**
     * Given a user tag, adds the corresponding chip to the chip group
     */
    private fun addChip(newTag: UserTags) {
        val chipGroup = binding.editProfileChips
        val chip = Chip(context)
        chip.text = newTag.toString()
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            chip.visibility = View.GONE
        }
        chipGroup.addView(chip, chipGroup.size - 1)
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