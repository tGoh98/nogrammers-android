package com.example.nogrammers_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShoutoutsAdapter(private val data: List<Shoutouts>) : RecyclerView.Adapter<ShoutoutsAdapter.ShoutoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoutoutViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.shoutout_item_new, parent, false)
        return ShoutoutViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ShoutoutViewHolder, position: Int) {
        val item = data[position]
        holder.msg.text = item.author
        holder.pfp.setImageResource(R.drawable.km1)
    }

    override fun getItemCount() = data.size

    class ShoutoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val msg: TextView = itemView.findViewById(R.id.msg)
        val pfp: ImageView = itemView.findViewById(R.id.pfp)
    }
}