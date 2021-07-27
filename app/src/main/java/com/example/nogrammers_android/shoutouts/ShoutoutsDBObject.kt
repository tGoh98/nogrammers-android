package com.example.nogrammers_android.shoutouts

import java.util.*

/**
 * This class is used to create a new database entry for a shoutout.
 */
data class ShoutoutsDBObject(
        var author: String ="", var msg: String = "",
        var date: String = Calendar.getInstance().timeInMillis.toString(), var pfp: String = "",
        var likes: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var loves: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var hahas: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var surprises: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var sads: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var angrys: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var horrors: HashMap<String, String> = hashMapOf("ignore" to "ignore"),
        var netID: String = "",
        var id: String = ""
)