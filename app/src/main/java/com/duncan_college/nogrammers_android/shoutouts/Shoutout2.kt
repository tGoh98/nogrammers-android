package com.duncan_college.nogrammers_android.shoutouts

import java.util.*

data class Shoutout2 (
        val author: String, val msg: String,
        val date: String = Calendar.getInstance().timeInMillis.toString(), val pfp: String = "",
        val rooCount: Int, val isLiked: Boolean = false
)