package com.example.nogrammers_android.user

data class User(
        val netID: String,
        var gradYr: Int = -1,
        var name: String = "",
        var bio: String = "",
        var tags: List<UserTags> = ArrayList(),
        val admin: Boolean = false
)

/*
Need to add:
savedEvents: HashSet<eventID>
attendingEvents: HashSet<eventID>
likedShoutouts: HashSet<shoutoutID>
authoredPosts: HashSet<postID>
 */