package com.duncan_college.nogrammers_android.events

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.duncan_college.nogrammers_android.R

/**
 * Binding adapters for Events
 */
@BindingAdapter("title")
fun TextView.setTitle(item: Event) {
    text = item.title
}

@BindingAdapter("desc")
fun TextView.setDesc(item: Event) {
    text = item.desc
}

@BindingAdapter("date")
fun TextView.setDate(item: Event) {
    text = DateTimeUtil.getStringFromDateinMillis(item.start) + "\n" +
            DateTimeUtil.getStringFromTimeinMillis(item.start) +
            " CST to " + DateTimeUtil.getStringFromTimeinMillis(item.end) + " CST"
}

@BindingAdapter("backgroundPic")
fun ImageView.setBackground(item: Event) {
    setImageResource(R.drawable.blank)
}
