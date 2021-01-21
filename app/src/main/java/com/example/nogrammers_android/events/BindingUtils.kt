package com.example.nogrammers_android.events

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nogrammers_android.R
import java.util.*

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
            " to " + DateTimeUtil.getStringFromTimeinMillis(item.end)
}

@BindingAdapter("backgroundPic")
fun ImageView.setBackground(item: Event) {
    if (item.pic == "") setImageResource(R.drawable.testimg)
    else TODO("need to implement custom images")
}
