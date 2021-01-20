package com.example.nogrammers_android.shoutouts

import java.util.*

data class Shoutout(
    val author: String ="", val msg: String = "",
    val date: String = Calendar.getInstance().timeInMillis.toString(), val pfp: String = "",
    var likes: Int = 0, var isLiked: Boolean = false,
    var loves: Int = 0, var isLoved: Boolean = false,
    var hahas: Int = 0, var isHahad: Boolean = false,
    var surprises: Int = 0, var isSurprised: Boolean = false,
    var sads: Int = 0, var isSaded: Boolean = false,
    var angrys: Int = 0, var isAngried: Boolean = false
)