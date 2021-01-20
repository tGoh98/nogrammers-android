package com.example.nogrammers_android.shoutouts

// To deserialize data from the database
class ShoutoutsObject {
    // TODO should these be values instead?
    // These are just default values, will be overwritten when constructor is called
    var author: String = "author"
    var date: String = "0"
    var msg: String = "test"
    var likes: List<String> = listOf()
    var isLiked: Boolean = false
    var loves: List<String> = listOf()
    var isLoved: Boolean = false
    var hahas: List<String> = listOf()
    var isHahad: Boolean = false
    var surprises: List<String> = listOf()
    var isSurprised: Boolean = false
    var sads: List<String> = listOf()
    var isSaded: Boolean = false
    var angrys: List<String> = listOf()
    var isAngried: Boolean = false
}