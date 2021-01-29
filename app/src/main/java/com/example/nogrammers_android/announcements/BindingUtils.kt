package com.example.nogrammers_android.announcements

import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Binding adapters for AnnouncementsViewHolder
 */
@BindingAdapter("title")
fun TextView.setTitle(item: Announcement) {
    text = item.title
}

@BindingAdapter("author")
fun TextView.setAuthor(item: Announcement) {
    text = item.author
}

@BindingAdapter("content")
fun TextView.setContent(item: Announcement) {
    text = item.content
}

@BindingAdapter("date")
fun TextView.setDate(item: Announcement) {
    text = java.util.Date(item.date.toLong()).toString()
}

@BindingAdapter("hiddenKey")
fun TextView.setHiddenKey(item: Announcement) {
    text = item.author.filter { !it.isWhitespace() }.plus(item.date)
}