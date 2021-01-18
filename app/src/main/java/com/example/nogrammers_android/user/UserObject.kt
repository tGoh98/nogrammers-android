package com.example.nogrammers_android.user

/**
 * To deserialize user objects from the Firebase db
 */
class UserObject {
    val netID: String = ""
    var gradYr: Int = -1
    var name: String = ""
    var bio: String = ""
    var tags: MutableList<UserTags> = mutableListOf()
    val admin: Boolean = false
}