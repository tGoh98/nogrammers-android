package com.example.nogrammers_android.shoutouts

import java.util.*

data class Shoutout(
    val author: String ="", val msg: String = "",
    val date: String = Calendar.getInstance().timeInMillis.toString(), val pfp: String = "",
    var likes: List<String> = listOf(), var isLiked: Boolean = false,
    var loves: List<String> = listOf(), var isLoved: Boolean = false,
    var hahas: List<String> = listOf(), var isHahad: Boolean = false,
    var surprises: List<String> = listOf(), var isSurprised: Boolean = false,
    var sads: List<String> = listOf(), var isSaded: Boolean = false,
    var angrys: List<String> = listOf(), var isAngried: Boolean = false,
    var netID: String = ""
)