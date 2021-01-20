package com.example.nogrammers_android.shoutouts

import android.graphics.Typeface
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
    val sdf = SimpleDateFormat("MM/dd/yy 'at' hh:mm a", Locale.getDefault())
    val date = java.util.Date(item.date.toLong())
    text = sdf.format(date)
//    text = java.util.Date(item.date.toLong()).toString()

}

@BindingAdapter("pfp")
fun ImageView.setPfp(item: Shoutout) {
    if (item.pfp == "") setImageResource(R.drawable.km1)
    else TODO("need to implement custom images")
}

@BindingAdapter("likes")
fun TextView.setLikes(item: Shoutout) {
    text = item.likes.size.toString()
}

@BindingAdapter("angrys")
fun TextView.setAngrys(item: Shoutout) {
    text = item.angrys.size.toString()
}

@BindingAdapter("hahas")
fun TextView.setHahas(item: Shoutout) {
    text = item.hahas.size.toString()
}

@BindingAdapter("loves")
fun TextView.setLoves(item: Shoutout) {
    text = item.loves.size.toString()
}

@BindingAdapter("sads")
fun TextView.setSads(item: Shoutout) {
    text = item.sads.size.toString()
}

@BindingAdapter("surprises")
fun TextView.setSurprises(item: Shoutout) {
    text = item.surprises.size.toString()
}

@BindingAdapter("userNetID")
fun TextView.setNetID(item: Shoutout) {
    text = item.netID
}

//@BindingAdapter("isBoldLikes")
//fun TextView.setBolded(item: Shoutout) {
//    if (item.angrys.contains("jdh16")) {
//        text.setTypeface(null, Typeface.BOLD)
//    } else {
//        textView.setTypeface(null, Typeface.NORMAL)
//    }
//}

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
    val sdf = SimpleDateFormat("MM/dd/yy 'at' hh:mm a", Locale.getDefault())
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
//    rooCount = item.rooCount
}