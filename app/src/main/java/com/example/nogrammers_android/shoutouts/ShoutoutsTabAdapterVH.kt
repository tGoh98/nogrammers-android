package com.example.nogrammers_android.shoutouts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.nogrammers_android.databinding.ShoutoutItemBinding

/**
 * Adapter for shoutouts item in recycler view
 */
class ShoutoutsTabAdapterVH(private val data: List<Shoutout>) :
    RecyclerView.Adapter<ShoutoutsTabAdapterVH.ShoutoutViewHolder>() {

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
            fun from(parent: ViewGroup): ShoutoutViewHolder {
                val binding =
                    ShoutoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ShoutoutViewHolder(binding)
            }
        }
    }
}
class ShoutoutsTabAdapter(activity: AppCompatActivity, val itemsCount: Int) :
        FragmentStateAdapter(activity) {
    override fun getItemCount() = itemsCount

    override fun createFragment(position: Int): Fragment = ShoutoutsTabFragment.getInstance(position)
}