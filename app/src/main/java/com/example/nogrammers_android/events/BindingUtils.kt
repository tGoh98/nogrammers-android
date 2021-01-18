package com.example.nogrammers_android.events

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nogrammers_android.R

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
    text = item.start + " to " + item.end
}

@BindingAdapter("backgroundPic")
fun ImageView.setBackground(item: Event) {
    if (item.pic == "") setImageResource(R.drawable.testimg)
    else TODO("need to implement custom images")
}
