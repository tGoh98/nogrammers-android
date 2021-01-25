package com.example.nogrammers_android.events

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nogrammers_android.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    setImageResource(R.drawable.testimg)
}
