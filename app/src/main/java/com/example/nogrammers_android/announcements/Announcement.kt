package com.example.nogrammers_android.announcements

import java.util.*

data class Announcement(
    val title: String,
    val content: String,
    val author: String,
    val urgent: Boolean = false,
    val date: String = Calendar.getInstance().timeInMillis.toString()
)