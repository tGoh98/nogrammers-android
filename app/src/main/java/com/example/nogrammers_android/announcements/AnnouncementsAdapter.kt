package com.example.nogrammers_android.announcements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.databinding.AnnouncementItemBinding

class AnnouncementsAdapter(private val data: List<Announcement>) :
    RecyclerView.Adapter<AnnouncementsAdapter.AnnouncementViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    class AnnouncementViewHolder private constructor(val binding: AnnouncementItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Announcement) {
            binding.announcement = item
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