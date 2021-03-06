package com.example.nogrammers_android.shoutouts

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.TouchDelegate
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.nogrammers_android.MainActivity
import com.example.nogrammers_android.R
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import com.google.firebase.storage.ktx.storage

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
    else {
        val storageRef = Firebase.storage.reference.child("profilePics").child(item.pfp)
        val ONE_MEGABYTE: Long = 1024 * 1024
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener {
            /* Found pfp, set it */
            val bmp = BitmapFactory.decodeByteArray(it, 0, it.size)
            setImageBitmap(bmp)
        }.addOnFailureListener {
            /* Not found/error, use default */
            Log.e("TAG", "Could not find profile pic, using default image")
        }
    }
}

@BindingAdapter("likes")
fun TextView.setLikes(item: Shoutout) {
    text = (item.likes.size - 1).toString()
}

@BindingAdapter("angrys")
fun TextView.setAngrys(item: Shoutout) {
    text = (item.angrys.size - 1).toString()
}

@BindingAdapter("hahas")
fun TextView.setHahas(item: Shoutout) {
    text = (item.hahas.size - 1).toString()
}

@BindingAdapter("loves")
fun TextView.setLoves(item: Shoutout) {
    text = (item.loves.size - 1).toString()
}

@BindingAdapter("sads")
fun TextView.setSads(item: Shoutout) {
    text = (item.sads.size - 1).toString()
}

@BindingAdapter("surprises")
fun TextView.setSurprises(item: Shoutout) {
    text = (item.surprises.size - 1).toString()
}

@BindingAdapter("horrors")
fun TextView.setHorrors(item: Shoutout) {
    text = (item.horrors.size - 1).toString()
}

@BindingAdapter("userNetID")
fun TextView.setNetID(item: Shoutout) {
    text = item.netID
}

@BindingAdapter("shoutoutUUID")
fun TextView.setUUID(item: Shoutout) {
    text = item.uuid
}

@BindingAdapter("isLiked")
fun TextView.setIsLiked(item: Shoutout) {
    if (item.likes.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("isLoved")
fun TextView.setIsLoved(item: Shoutout) {
    if (item.loves.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("isHahad")
fun TextView.setIsHahad(item: Shoutout) {
    if (item.hahas.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("isSurprised")
fun TextView.setIsSurprised(item: Shoutout) {
    if (item.surprises.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("isSaded")
fun TextView.setIsSaded(item: Shoutout) {
    if (item.sads.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("isAngryd")
fun TextView.setIsAngryd(item: Shoutout) {
    if (item.angrys.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("isHorrored")
fun TextView.setIsHorrored(item: Shoutout) {
    if (item.horrors.containsKey(item.netID)) {
        text = "true"
    } else {
        text = "false"
    }
}

@BindingAdapter("increaseTouch")
fun increaseTouch(view: ImageButton, useless: Int) {
    val parent = view.parent
    (parent as View).post {
        val rect = Rect()
        view.getHitRect(rect)
//        TODO: find way to use dp instead
        val intValue = 50
        rect.top -= intValue    // increase top hit area
        rect.left -= intValue   // increase left hit area
        rect.bottom += intValue // increase bottom hit area
        rect.right += intValue  // increase right hit area
        parent.setTouchDelegate(TouchDelegate(rect, view));
    }
}