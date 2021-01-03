package com.example.nogrammers_android

import android.icu.util.DateInterval
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

/**
 * Binding adapters for ShoutoutsViewHolder
 */
@BindingAdapter("author")
fun TextView.setAuthor(item: Shoutouts) {
    text = item.author
}

@BindingAdapter("msg")
fun TextView.setMsg(item: Shoutouts) {
    text = item.msg
}

@BindingAdapter("date")
fun TextView.setDate(item: Shoutouts) {
    text = java.util.Date(item.date.toLong()).toString()

}

@BindingAdapter("pfp")
fun ImageView.setPfp(item: Shoutouts) {
    if (item.pfp == "") setImageResource(R.drawable.km1)
    else TODO("need to implement custom images")
}