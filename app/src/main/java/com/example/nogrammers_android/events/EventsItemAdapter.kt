package com.example.nogrammers_android.events

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.databinding.EventItemBinding

class EventsItemAdapter(private val data: List<Event>) :
        RecyclerView.Adapter<EventsItemAdapter.EventViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        return EventViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    class EventViewHolder private constructor(val binding: EventItemBinding) :
            RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Event) {
            binding.event = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): EventViewHolder {
                val binding = EventItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
                return EventViewHolder(binding)
            }
        }
    }
}