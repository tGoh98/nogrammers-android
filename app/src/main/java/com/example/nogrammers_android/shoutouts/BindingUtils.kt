package com.example.nogrammers_android.shoutouts

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nogrammers_android.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Binding adapters for ShoutoutsViewHolder
 */
@BindingAdapter("author")
fun TextView.setAuthor(item: Shoutout) {
    text = item.author
}

@BindingAdapter("msg")
fun TextView.setMsg(item: Shoutout) {
    text = item.msg
}

@BindingAdapter("date")
fun TextView.setDate(item: Shoutout) {
    val sdf = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())
    val date = java.util.Date(item.date.toLong())
    text = sdf.format(date)
//    text = java.util.Date(item.date.toLong()).toString()

}

@BindingAdapter("pfp")
fun ImageView.setPfp(item: Shoutout) {
    if (item.pfp == "") setImageResource(R.drawable.km1)
    else TODO("need to implement custom images")
}

@BindingAdapter("author2")
fun TextView.setAuthor(item: Shoutout2) {
    text = item.author
}

@BindingAdapter("msg2")
fun TextView.setMsg(item: Shoutout2) {
    text = item.msg
}

@BindingAdapter("date2")
fun TextView.setDate(item: Shoutout2) {
    val sdf = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())
    val date = java.util.Date(item.date.toLong())
    text = sdf.format(date)
//    text = java.util.Date(item.date.toLong()).toString()

}

@BindingAdapter("pfp2")
fun ImageView.setPfp(item: Shoutout2) {
    if (item.pfp == "") setImageResource(R.drawable.km1)
    else TODO("need to implement custom images")
}

@BindingAdapter("rooCount")
fun TextView.setRooCount(item: Shoutout2) {
    rooCount = item.rooCount
}