package com.example.nogrammers_android.events

import java.util.*

data class Event(
        val author: String, val title: String, val desc: String,
        val start: Long, val end: Long, val tags: List<String>, val location: String,
        val audience: String, val pic: String = "", val remote: Boolean = false
)