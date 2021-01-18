package com.example.nogrammers_android.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.R

/**
 * Adapter for recycler view for tag items
 * Takes in onclick handler to respond to selected items
 */
class TagItemAdapter(var clickListener: CellClickListener) : ListAdapter<String, TagItemAdapter.TagItemViewHolder>(TagItemDiffCallback()) {

    class TagItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val textView: TextView = view.findViewById(R.id.tagItem)

        fun bind(tagTxt: String, clickListener: CellClickListener) {
            textView.text = tagTxt
            /* Don't forget to set the onclick listener */
            textView.setOnClickListener { clickListener.onCellClickListener(tagTxt) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.tag_item, parent, false)
        return TagItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: TagItemViewHolder, position: Int) {
        holder.bind(getItem(position), clickListener)
    }

}

/**
 * Used to compute the diff to update the list with new items
 */
class TagItemDiffCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean = areItemsTheSame(oldItem, newItem)
 }