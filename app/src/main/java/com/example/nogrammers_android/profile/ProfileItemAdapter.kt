package com.example.nogrammers_android.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.databinding.ProfileItemBinding

class ProfileItemAdapter(private val data: List<ProfileItem>) :
    RecyclerView.Adapter<ProfileItemAdapter.ProfileItemViewHolder>() {

    // TODO: instead of passing in static data, consider managing list contents with https://developer.android.com/codelabs/kotlin-android-training-diffutil-databinding/#3

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileItemViewHolder {
        return ProfileItemViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ProfileItemViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    class ProfileItemViewHolder private constructor(val binding: ProfileItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProfileItem) {
            binding.profileItem = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ProfileItemViewHolder {
                val binding = ProfileItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return ProfileItemViewHolder(binding)
            }
        }
    }
}