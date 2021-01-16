package com.example.nogrammers_android.user

/**
 * To deserialize user objects from the Firebase db
 */
class UserObject {
    val netID: String = ""
    var gradYr: Int = -1
    var name: String = ""
    var bio: String = ""
    var tags: List<UserTags> = ArrayList()
    val admin: Boolean = false
}