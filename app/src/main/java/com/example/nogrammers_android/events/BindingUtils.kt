package com.example.nogrammers_android.events

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nogrammers_android.R
import com.example.nogrammers_android.shoutouts.Shoutout

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

@BindingAdapter("saveText")
fun TextView.setSave(item: Event) {
    text = "Save"
}

@BindingAdapter("savePic")
fun ImageView.setSavePic(item: Event) {
    setImageResource(R.drawable.heart)
}
