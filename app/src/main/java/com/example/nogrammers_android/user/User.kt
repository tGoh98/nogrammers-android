package com.example.nogrammers_android.user

data class User(
        val netID: String,
        var gradYr: Int = -1,
        var name: String = "Add your name here!",
        var bio: String = "Add your bio here!",
        var tags: MutableList<UserTags> = mutableListOf(),
        var interestedEvents: MutableMap<String, Int> = mutableMapOf<String, Int>(),
        var goingEvents: MutableMap<String, Int> = mutableMapOf<String, Int>()
)

/*
Need to add:
pfp: ???
savedEvents: HashSet<eventID>
attendingEvents: HashSet<eventID>
likedShoutouts: HashSet<shoutoutID>
authoredPosts: HashSet<postID>
 */