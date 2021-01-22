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

//                val uuid = binding.
                likesBtn.setOnClickListener {
                    if (binding.isLiked.text.toString() == "true") {
//                        var current : List<String> = database.child(binding.shoutoutUUIDInvisible.text.toString()).child("likes").
//                        Log.d("test list current", current[0] + " " + current[1])
                        database.child(binding.shoutoutUUIDInvisible.text.toString()).child("likes").child(netID).removeValue()
                    } else {
                        database.child(binding.shoutoutUUIDInvisible.text.toString()).child("likes").child(netID).setValue(netID)
                    }
                    database.child(binding.shoutoutUUIDInvisible.text.toString()).child("likes")
                    Toast.makeText(parent.context, netID + position + binding.isLiked.text.toString(), Toast.LENGTH_SHORT).show()
                }

                return ShoutoutViewHolder(binding)
            }
        }
    }
}
class ShoutoutsTabAdapter(activity: AppCompatActivity, val itemsCount: Int, val netID: String) :
        FragmentStateAdapter(activity) {
    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment = ShoutoutsTabFragment.getInstance(position, netID)
}