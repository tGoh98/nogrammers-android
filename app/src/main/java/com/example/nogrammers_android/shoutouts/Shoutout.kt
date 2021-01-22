package com.example.nogrammers_android.shoutouts

import java.util.*

data class Shoutout(
    var author: String ="", var msg: String = "",
    var date: String = Calendar.getInstance().timeInMillis.toString(), var pfp: String = "",
    var likes: HashMap<String, String> = HashMap(), var isLiked: Boolean = false,
    var loves: List<String> = listOf(), var isLoved: Boolean = false,
    var hahas: List<String> = listOf(), var isHahad: Boolean = false,
    var surprises: List<String> = listOf(), var isSurprised: Boolean = false,
    var sads: List<String> = listOf(), var isSaded: Boolean = false,
    var angrys: List<String> = listOf(), var isAngried: Boolean = false,
    var netID: String = "", var uuid: String = ""
)