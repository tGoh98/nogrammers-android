package com.example.nogrammers_android

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.databinding.ShoutoutItemNewBinding

/**
 * Adapter for shoutouts item in recycler view
 */
class ShoutoutsAdapter(private val data: List<Shoutouts>) : RecyclerView.Adapter<ShoutoutsAdapter.ShoutoutViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    /**
     * Inflates the item into the layout
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoutoutViewHolder {
        return ShoutoutViewHolder.from(parent)
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
    class ShoutoutViewHolder private constructor(val binding: ShoutoutItemNewBinding) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the content to shoutouts item using the helper functions in BindingUtils
         */
        fun bind(item: Shoutouts) {
            binding.shoutout = item
            binding.executePendingBindings()
        }

        /**
         * Inflates the view from the layout resource for the ViewHolder
         */
        companion object {
            fun from(parent: ViewGroup): ShoutoutViewHolder {
                val binding = ShoutoutItemNewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ShoutoutViewHolder(binding)
            }
        }
    }
}