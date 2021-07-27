package com.example.nogrammers_android.shoutouts

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nogrammers_android.databinding.ShoutoutItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * Adapter for shoutouts item in recycler view
 */
class ShoutoutsTabAdapterVH(private val data: List<Shoutout>, val netID: String, val position: Int, val isAdmin: Boolean, val context: Context) :
    RecyclerView.Adapter<ShoutoutsTabAdapterVH.ShoutoutViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    /**
     * Inflates the item into the layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoutoutViewHolder {
        return ShoutoutViewHolder.from(parent, netID, position, isAdmin, context)
    }

    /**
     * Binding
     */
    override fun onBindViewHolder(holder: ShoutoutViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    /**
     * ez
     */
    override fun getItemCount() = data.size

    /**
     * Extends view holder for shoutouts.
     * Binding contains inlined properties for msg, author, and pfp
     */
    class ShoutoutViewHolder private constructor(val binding: ShoutoutItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the content to shoutouts item using the helper functions in BindingUtils
         */
        fun bind(item: Shoutout) {
            binding.shoutout = item
            /* This automatically executes all the "@BindingAdapter" in BindingUtils */
            binding.executePendingBindings()
        }

        /**
         * Inflates the view from the layout resource for the ViewHolder
         */
        companion object {
            fun from(parent: ViewGroup, netID: String, position: Int, isAdmin: Boolean, context: Context): ShoutoutViewHolder {
                val binding =
                    ShoutoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

//                TODO : This code is almost the same as previous database code in other files, could refactor
                var database: DatabaseReference

                Log.d("Created FROM", "test")
                if (position.equals(0)) {
                    database = Firebase.database.reference.child("shoutouts")
                } else {
                    database = Firebase.database.reference.child("sds")
                }

                val likesBtn = binding.likesImg
                val lovesBtn = binding.lovesImg
                val hahasBtn = binding.hahasImg
                val surprisesBtn = binding.surprisedImg
                val sadsBtn = binding.sadsImg
                val angrysBtn = binding.angryImg
                val horrorsBtn = binding.horrorsImg

                likesBtn.setOnClickListener { toggleReaction(binding, database, 1, binding.isLiked.text.toString() == "true", netID) }

                lovesBtn.setOnClickListener { toggleReaction(binding, database, 2, binding.isLoved.text.toString() == "true", netID) }

                hahasBtn.setOnClickListener { toggleReaction(binding, database, 3, binding.isHahad.text.toString() == "true", netID) }

                surprisesBtn.setOnClickListener { toggleReaction(binding, database, 4, binding.isSurprised.text.toString() == "true", netID) }

                sadsBtn.setOnClickListener { toggleReaction(binding, database, 5, binding.isSaded.text.toString() == "true", netID) }

                angrysBtn.setOnClickListener { toggleReaction(binding, database, 6, binding.isAngryd.text.toString() == "true", netID) }

                horrorsBtn.setOnClickListener { toggleReaction(binding, database, 7, binding.isHorrored.text.toString() == "true", netID) }

                // Hook up delete icon
                 if (isAdmin) {
                    binding.deleteShoutoutsIcon.visibility = View.VISIBLE
                    binding.deleteShoutoutsIcon.setOnClickListener {
                        MaterialAlertDialogBuilder(context)
                            .setTitle("Are you sure you want to delete item \"${binding.msg.text}?\"")
                            .setMessage("THIS ACTION CANNOT BE UNDONE")
                            .setNeutralButton("Cancel") { _, _ ->
                                /* Respond to neutral button press - do nothing */
                            }
                            .setPositiveButton("Delete") { _, _ ->
                                /* Respond to positive button press - delete from firebase and refresh view */
                                val key = binding.firebaseKeyShoutouts.text.toString()
                                database.child(key).removeValue()
                                Toast.makeText(context, "It's deleted :O", Toast.LENGTH_SHORT).show()
                            }
                            .show()
                    }
                 }

                return ShoutoutViewHolder(binding)
            }

            fun toggleReaction(binding: ShoutoutItemBinding, database: DatabaseReference, type: Int, isReacted: Boolean, netID: String) {
                // Should never be empty
                var pathString: String = ""
                when (type) {
                    1 -> pathString = "likes"
                    2 -> pathString = "loves"
                    3 -> pathString = "hahas"
                    4 -> pathString = "surprises"
                    5 -> pathString = "sads"
                    6 -> pathString = "angrys"
                    7 -> pathString = "horrors"
                }
                if (isReacted) {
                    database.child(binding.shoutoutIDInvisible.text.toString()).child(pathString).child(netID).removeValue()
                } else {
                    database.child(binding.shoutoutIDInvisible.text.toString()).child(pathString).child(netID).setValue(netID)
                }
            }
        }
    }
}
class ShoutoutsTabAdapter(activity: AppCompatActivity, val itemsCount: Int, val netID: String, val sortBy: Int) :
        FragmentStateAdapter(activity) {
    private var fragmentMutableList = mutableListOf<Fragment>()
    private var counter = 0

    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment {
        fragmentMutableList.add(ShoutoutsTabFragment.getInstance(0, netID, sortBy))
        fragmentMutableList.add(ShoutoutsTabFragment.getInstance(1, netID, sortBy))
        counter++
        return fragmentMutableList.get(position)
    }

    fun remakeFragment(newSort: Int) {
        (fragmentMutableList.get(0) as ShoutoutsTabFragment).toggleSorting(newSort)
        if (fragmentMutableList.size > 1 && counter > 1) {
            (fragmentMutableList.get(1) as ShoutoutsTabFragment).toggleSorting(newSort)
        }
    }
}
