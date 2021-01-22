package com.example.nogrammers_android.shoutouts

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.R
//import com.example.nogrammers_android.announcements.AnnouncementsViewModel
//import com.example.nogrammers_android.announcements.TransparentFragment
import com.example.nogrammers_android.databinding.FragmentShoutoutsTabsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


class ShoutoutsTabFragment(val position: Int, val netID: String) : Fragment() {
    private lateinit var database: DatabaseReference
    private val model: ShoutoutsViewModel by activityViewModels()
    private var shortAnimationDuration: Int = 0

    private var clicked: Int = -1

    lateinit var binding: FragmentShoutoutsTabsBinding

    companion object {
        const val ARG_POSITION = "position"

        fun getInstance(position: Int, netID: String): Fragment {
            val shoutoutsTabFragment = ShoutoutsTabFragment(position, netID)
            val bundle = Bundle()
            bundle.putInt(ARG_POSITION, position)
            shoutoutsTabFragment.arguments = bundle
            return shoutoutsTabFragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = DataBindingUtil.inflate<FragmentShoutoutsTabsBinding>(
            inflater,
            R.layout.fragment_shoutouts_tabs, container, false
        )

        var authors = listOf(
            "adrienne",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim",
            "julie",
            "colin",
            "cindy",
            "tim"
        ).map { Shoutout(it, "Lorem ipsum dolor sit amet, consectetur adipiscing elit.") }
        authors = authors.toMutableList()
        val adapter = ShoutoutsTabAdapterVH(authors, netID, position)
        if (position.equals(0)) {
            database = Firebase.database.reference.child("shoutouts")
        } else {
            database = Firebase.database.reference.child("sds")
        }
        val createShoutouts = ShoutoutsCreateFragment(position, netID)
        val updateListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    Log.d("NETID ", netID)
                    authors.clear()
                    for (ds : DataSnapshot in dataSnapshot.children) {
                        val so: ShoutoutsObject = ds.getValue(ShoutoutsObject::class.java) as ShoutoutsObject
                        val newShoutout: Shoutout = Shoutout(so.author, so.msg, so.date)
                        newShoutout.likes = so.likes
                        newShoutout.angrys = so.angrys
                        newShoutout.loves = so.loves
                        newShoutout.hahas = so.hahas
                        newShoutout.sads = so.sads
                        newShoutout.surprises = so.surprises
                        newShoutout.netID = netID
                        newShoutout.uuid = so.uuid
                        authors.add(newShoutout)
                    }
                    authors.sortByDescending { it.date }
                    adapter.notifyDataSetChanged()
                }
                Log.d("TAG", dataSnapshot.toString())
                Log.d("TAG2", authors.toString())
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
            }
        }
        database.addValueEventListener(updateListener)

        val usersDB = Firebase.database.reference.child("users").child(netID)
        val nameListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                createShoutouts.setName(snapshot.child("name").value.toString())
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("ERROR ON CANCELLED", " FOR nameListener in ShoutoutsTabFragment")
            }

        }

        usersDB.addValueEventListener(nameListener)

        binding.shoutoutsView.adapter = adapter

        model.createMode.observe(viewLifecycleOwner, { newVal ->
            Log.d("NEWVAL", newVal.toString())
            if (!newVal) {
                hideNewShoutoutsFrag()
                /* Overlay */
                binding.transparentShoutoutsOverlay.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration.toLong())
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.transparentShoutoutsOverlay.visibility = View.GONE
                            }
                        })
                /* FAB */
                binding.createShoutoutBtn.visibility = View.VISIBLE
                /* Unfreeze recycler view */
                enableRV(binding.shoutoutsView)
            }
        })

        /* Listener for create announcement button */
        val createBtn = binding.createShoutoutBtn
        createBtn.setOnClickListener {
            /* Frag */
            showNewShoutoutsFrag(createShoutouts)
            /* Freeze recycler view */
            disableRV(binding.shoutoutsView)
            /* FAB */
            binding.createShoutoutBtn.visibility = View.INVISIBLE
            /* Overlay */
            binding.transparentShoutoutsOverlay.apply {
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
            model.createMode.value = true
        }

        shortAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        // Button listeners
        // Cancel, clear text fields and hide keyboard
        binding.cancelButton.setOnClickListener {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
            binding.editTextName.text.clear()
            binding.editTextMsg.text.clear()
        }
        // Post, add shoutout to firebase, then cancel
        binding.postButton.setOnClickListener {
            var num = 0
            val postListener = object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val so = dataSnapshot.getValue<ArrayList<HashMap<String, String>>>()
                    if (so != null) {
                        num = so.size
                    }
                    database.child(num.toString()).setValue(
                        Shoutout(
                            binding.editTextName.text.toString(),
                            binding.editTextMsg.text.toString(),
                            Calendar.getInstance().timeInMillis.toString()
                        )
                    )
                    binding.cancelButton.performClick()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Getting Post failed, log a message
                    Log.w("TAG", "loadPost:onCancelled", databaseError.toException())
                }
            }
            database.addListenerForSingleValueEvent(postListener)
        }

        return binding.root
    }

    /**
     * Shows new shoutouts fragment
     */
    private fun showNewShoutoutsFrag(frag: ShoutoutsCreateFragment) {
        // Note: duration is set in the xml animation file
        val transaction = activity?.supportFragmentManager?.beginTransaction() ?: return
        transaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down
        ).add(
                R.id.createShoutoutsContainer,
                frag,
                "createShoutout"
        ).commit()
    }

    /**
     * Hides new shoutouts fragment
     */
    private fun hideNewShoutoutsFrag() {
        val sfm = activity?.supportFragmentManager ?: return
        val fragment = sfm.findFragmentByTag("createShoutout")
        if (fragment != null) sfm.beginTransaction().setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down
        ).replace(R.id.createShoutoutsContainer, ShoutoutsCreateTransparentFragment()).commit()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun enableRV(rv: RecyclerView) {
        rv.setOnTouchListener { _, _ -> false }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disableRV(rv: RecyclerView) {
        rv.setOnTouchListener { _, _ -> true }
    }
}