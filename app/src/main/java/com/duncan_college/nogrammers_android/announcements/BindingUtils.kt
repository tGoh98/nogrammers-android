package com.duncan_college.nogrammers_android.announcements

import android.annotation.SuppressLint
import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import java.text.SimpleDateFormat
import java.util.*

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

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("SimpleDateFormat", "SetTextI18n")
@BindingAdapter("date")
fun TextView.setDate(item: Announcement) {
    val formatter = SimpleDateFormat("E, MMM d, h:m aa")
    formatter.timeZone = TimeZone.getTimeZone("CST")
    val formatted = formatter.format(Date(item.date.toLong()))
    text = "$formatted CST"
}

@BindingAdapter("hiddenKey")
fun TextView.setHiddenKey(item: Announcement) {
    text = item.author.filter { !it.isWhitespace() }.plus(item.date)
}