package com.example.nogrammers_android.events

import android.R
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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

        companion object : AdapterView.OnItemSelectedListener {
            fun from(parent: ViewGroup): EventViewHolder {
                val binding = EventItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                )
                val markAsOptions = listOf("Mark as", "Interested",
                        "Going")
                binding.markAs.adapter = ArrayAdapter(parent.context, R.layout.simple_spinner_item, markAsOptions)
                binding.markAs.onItemSelectedListener = this
                return EventViewHolder(binding)
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (parent != null) {
                    Log.d("Tag",parent.getItemAtPosition(position).toString())
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("Tag","nothing selected")
            }
        }
    }
}