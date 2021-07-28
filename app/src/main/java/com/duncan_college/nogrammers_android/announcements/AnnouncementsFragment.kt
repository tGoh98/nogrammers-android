package com.duncan_college.nogrammers_android.announcements

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.duncan_college.nogrammers_android.MainActivity
import com.duncan_college.nogrammers_android.R
import com.duncan_college.nogrammers_android.databinding.FragmentAnnouncementsBinding
import com.duncan_college.nogrammers_android.user.UserTags
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener


/**
 * Announcements tab
 */
class AnnouncementsFragment(private val dbRef: DatabaseReference) : Fragment() {
    /* Shared view model */
    private val model: AnnouncementsViewModel by activityViewModels()
    private var shortAnimationDuration: Int = 0
    lateinit var adapter: AnnouncementsAdapter
    lateinit var announceDbRef: DatabaseReference
    val announcementsList: MutableList<Announcement> = mutableListOf()

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        /* Inflate the layout for this fragment with data binding */
        val binding = DataBindingUtil.inflate<FragmentAnnouncementsBinding>(
                inflater,
                R.layout.fragment_announcements,
                container,
                false
        )

        binding.loadingSpinner.visibility = View.VISIBLE
        /* Get announcements from db */
        announceDbRef = dbRef.child("announcements")

        // Write dummy objects
//        val aObjs = listOf(
//            Announcement("Fire drill", "wee woo wee woo beep boop", "Snoopy", urgent = true)
//        )
//        for (aObj in aObjs) announceDbRef.child("0").setValue(aObj)

        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                announcementsList.clear()
                for (child in dataSnapshot.children) {
                    val childAnnounce = child.getValue(AnnouncementObject::class.java) as AnnouncementObject
                    announcementsList.add(
                        Announcement(
                            childAnnounce.title,
                            childAnnounce.content,
                            childAnnounce.author,
                            childAnnounce.urgent,
                            childAnnounce.date
                        )
                    )
                }
                /* Update recycler view contents */
                adapter.submitList(announcementsList.sortedBy { it.date }.reversed())
                binding.loadingSpinner.visibility = View.GONE
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed
            }
        }
        announceDbRef.addValueEventListener(updateListener)

        /* Populate adapter with data */
        adapter = context?.let { AnnouncementsAdapter(it, (activity as MainActivity).curUser.tags.contains(UserTags.Admin), announceDbRef) }!!
        binding.announcementsView.adapter = adapter

        /* Create view model */
        model.createMode.observe(viewLifecycleOwner, { newVal ->
            if (newVal) {
                /* Frag */
                showNewAnnouncementsFrag()
                /* Freeze recycler view */
                disableRV(binding.announcementsView)
                /* FAB */
                binding.createAnnouncementBtn.visibility = View.INVISIBLE
                /* Overlay */
                binding.transparentOverlay.apply {
                    // Set the content view to 0% opacity but visible, so that it is visible
                    // (but fully transparent) during the animation.
                    alpha = 0f
                    visibility = View.VISIBLE

                    // Animate the content view to 100% opacity, and clear any animation
                    // listener set on the view.
                    animate()
                            .alpha(1f)
                            .setDuration(300L)
                            .setListener(null)
                }
            } else {
                /* Frag */
                hideNewAnnouncementsFrag()
                /* Overlay */
                binding.transparentOverlay.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.transparentOverlay.visibility = View.GONE
                            }
                        })
                /* FAB */
                binding.createAnnouncementBtn.visibility = View.VISIBLE
                /* Unfreeze recycler view */
                enableRV(binding.announcementsView)
            }
        })

        /* Listener for create announcement button */
        val createBtn = binding.createAnnouncementBtn
        createBtn.setOnClickListener {
            model.createMode.value = true
        }

        /* Delete icon */

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        return binding.root
    }

    /**
     * Shows new announcements fragment
     */
    private fun showNewAnnouncementsFrag() {
        // Note: duration is set in the xml animation file
        val transaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        transaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down
        ).add(
                R.id.announcementsContainer,
                CreateAnnouncementFragment(announceDbRef, announcementsList.size),
                "createAnnouncement"
        ).commit()
    }

    /**
     * Hides new announcements fragment
     */
    private fun hideNewAnnouncementsFrag() {
        val sfm = activity?.supportFragmentManager ?: return
        val fragment = sfm.findFragmentByTag("createAnnouncement")
        if (fragment != null) sfm.beginTransaction().setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down
        ).replace(R.id.announcementsContainer, TransparentFragment()).commit()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableRV(rv: RecyclerView) {
        rv.setOnTouchListener { _, _ -> false }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disableRV(rv: RecyclerView) {
        rv.setOnTouchListener { _, _ -> true }
    }

    /**
     * For create announcement to update the announcements list
     */
    fun appendAnnouncement(announce: Announcement) {
        announcementsList.add(announce)

    }

}