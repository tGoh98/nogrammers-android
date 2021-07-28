package com.duncan_college.nogrammers_android.shoutouts

import java.util.*

data class Shoutout(
    var author: String ="", var msg: String = "",
    var date: String = Calendar.getInstance().timeInMillis.toString(), var pfp: String = "",
    var likes: HashMap<String, String> = HashMap(), var isLiked: Boolean = false,
    var loves: HashMap<String, String> = HashMap(), var isLoved: Boolean = false,
    var hahas: HashMap<String, String> = HashMap(), var isHahad: Boolean = false,
    var surprises: HashMap<String, String> = HashMap(), var isSurprised: Boolean = false,
    var sads: HashMap<String, String> = HashMap(), var isSaded: Boolean = false,
    var angrys: HashMap<String, String> = HashMap(), var isAngried: Boolean = false,
    var horrors: HashMap<String, String> = HashMap(), var isHorrored: Boolean = false,
    var netID: String = "", var id: String = ""
)