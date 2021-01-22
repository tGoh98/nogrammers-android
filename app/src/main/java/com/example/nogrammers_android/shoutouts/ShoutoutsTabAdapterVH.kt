package com.example.nogrammers_android.shoutouts

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nogrammers_android.databinding.ShoutoutItemBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

/**
 * Adapter for shoutouts item in recycler view
 */
class ShoutoutsTabAdapterVH(private val data: List<Shoutout>, val netID: String, val position: Int) :
    RecyclerView.Adapter<ShoutoutsTabAdapterVH.ShoutoutViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    /**
     * Inflates the item into the layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoutoutViewHolder {
        return ShoutoutViewHolder.from(parent, netID, position)
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
            fun from(parent: ViewGroup, netID: String, position: Int): ShoutoutViewHolder {
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

                likesBtn.setOnClickListener { toggleReaction(binding, database, 1, binding.isLiked.text.toString() == "true", netID) }

                lovesBtn.setOnClickListener { toggleReaction(binding, database, 2, binding.isLoved.text.toString() == "true", netID) }

                hahasBtn.setOnClickListener { toggleReaction(binding, database, 3, binding.isHahad.text.toString() == "true", netID) }

                surprisesBtn.setOnClickListener { toggleReaction(binding, database, 4, binding.isSurprised.text.toString() == "true", netID) }

                sadsBtn.setOnClickListener { toggleReaction(binding, database, 5, binding.isSaded.text.toString() == "true", netID) }

                angrysBtn.setOnClickListener { toggleReaction(binding, database, 6, binding.isAngryd.text.toString() == "true", netID) }

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
                }
                if (isReacted) {
                    database.child(binding.shoutoutUUIDInvisible.text.toString()).child(pathString).child(netID).removeValue()
                } else {
                    database.child(binding.shoutoutUUIDInvisible.text.toString()).child(pathString).child(netID).setValue(netID)
                }
            }
        }
    }
}
class ShoutoutsTabAdapter(activity: AppCompatActivity, val itemsCount: Int, val netID: String, val sortBy: Int) :
        FragmentStateAdapter(activity) {
    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment = ShoutoutsTabFragment.getInstance(position, netID, sortBy)
}
