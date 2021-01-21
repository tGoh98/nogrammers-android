package com.example.nogrammers_android.announcements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.databinding.AnnouncementItemBinding

class AnnouncementsAdapter :
    ListAdapter<Announcement, AnnouncementsAdapter.AnnouncementViewHolder>(AnnouncementItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnnouncementViewHolder private constructor(val binding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Announcement) {
            binding.announcement = item
            /* Check for urgent status */
            if (item.urgent) binding.urgentPill.visibility = View.VISIBLE
            /* Bind the other elements */
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): AnnouncementViewHolder {
                val binding = AnnouncementItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AnnouncementViewHolder(binding)
            }
        }
    }
}

/**
 * Compute list diffs
 */
class AnnouncementItemDiffCallback : DiffUtil.ItemCallback<Announcement>() {
    override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean = (oldItem.date == newItem.date && oldItem.title == newItem.title)
    override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean = oldItem == newItem
}