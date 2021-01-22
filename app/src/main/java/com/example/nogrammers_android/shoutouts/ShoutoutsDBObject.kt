package com.example.nogrammers_android.shoutouts

import java.util.*

/**
 * This class is used to create a new database entry for a shoutout.
 */
data class ShoutoutsDBObject(
        var author: String ="", var msg: String = "",
        var date: String = Calendar.getInstance().timeInMillis.toString(), var pfp: String = "",
        var likes: List<String> = listOf("-1"),
        var loves: List<String> = listOf("-1"),
        var hahas: List<String> = listOf("-1"),
        var surprises: List<String> = listOf("-1"),
        var sads: List<String> = listOf("-1"),
        var angrys: List<String> = listOf("-1"),
        var netID: String = "",
        var uuid: String = UUID.randomUUID().toString()
)