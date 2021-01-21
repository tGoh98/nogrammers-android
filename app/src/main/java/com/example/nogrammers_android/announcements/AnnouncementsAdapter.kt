package com.example.nogrammers_android.announcements

import android.content.Context
import android.graphics.Paint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nogrammers_android.R
import com.example.nogrammers_android.databinding.AnnouncementItemBinding

class AnnouncementsAdapter(private val context: Context) :
    ListAdapter<Announcement, AnnouncementsAdapter.AnnouncementViewHolder>(AnnouncementItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        return AnnouncementViewHolder.from(parent, context)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AnnouncementViewHolder private constructor(val binding: AnnouncementItemBinding, val context: Context) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Announcement) {
            binding.announcement = item
            /* Check for urgent status */
            if (item.urgent) binding.urgentPill.visibility = View.VISIBLE
            /* Bind the other elements */
            binding.executePendingBindings()
            ellipsizeContent()
            /* Show more if needed  - must be done after text view has rendered */
            binding.announcementContent.post {
                val numLines = binding.announcementContent.lineCount
                if (numLines > 0 && binding.announcementContent.layout.getEllipsisCount(numLines - 1) > 0) {
                    // has been ellipsized
                    binding.announcementSeeMoreTxt.visibility = View.VISIBLE
                    binding.announcementSeeMoreTxt.paintFlags = Paint.UNDERLINE_TEXT_FLAG
                    binding.announcementSeeMoreTxt.setOnClickListener {
                        /* Show more */
                        undoEllipsizeContent()
                    }
                }
            }
        }

        /**
         * Converts text view to ellipsized (...) version
         */
        private fun ellipsizeContent() {
            binding.announcementContent.ellipsize = TextUtils.TruncateAt.END
            binding.announcementContent.maxLines = 4
            binding.announcementSeeMoreTxt.text = context.resources.getString(R.string.show_more)
            binding.announcementSeeMoreTxt.setOnClickListener { undoEllipsizeContent() }
        }

        /**
         * Undoes ellipsization
         */
        private fun undoEllipsizeContent() {
            binding.announcementContent.ellipsize = null
            binding.announcementContent.maxLines = Integer.MAX_VALUE
            binding.announcementSeeMoreTxt.text = context.resources.getString(R.string.show_less)
            binding.announcementSeeMoreTxt.setOnClickListener { ellipsizeContent() }
        }

        companion object {
            fun from(parent: ViewGroup, context: Context): AnnouncementViewHolder {
                val binding = AnnouncementItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                return AnnouncementViewHolder(binding, context)
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