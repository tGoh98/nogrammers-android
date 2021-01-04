package com.example.nogrammers_android

import java.util.*

data class Shoutouts(
    val author: String, val msg: String,
    val date: String = Calendar.getInstance().timeInMillis.toString(), val pfp: String = ""
)