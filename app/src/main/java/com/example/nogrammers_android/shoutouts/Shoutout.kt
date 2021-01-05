package com.example.nogrammers_android.shoutouts

import java.util.*

data class Shoutout(
    val author: String, val msg: String,
    val date: String = Calendar.getInstance().timeInMillis.toString(), val pfp: String = ""
)