package com.duncan_college.nogrammers_android.user

/**
 * To deserialize user objects from the Firebase db
 */
class UserObject {
    val netID: String = ""
    var gradYr: Int = -1
    var name: String = ""
    var bio: String = ""
    var tags: MutableList<UserTags> = mutableListOf()
    var interestedEvents: MutableMap<String, Int> = mutableMapOf<String, Int>()
    var goingEvents: MutableMap<String, Int> = mutableMapOf<String, Int>()
}