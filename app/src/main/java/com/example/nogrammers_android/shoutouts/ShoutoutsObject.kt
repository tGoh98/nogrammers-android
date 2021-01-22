package com.example.nogrammers_android.shoutouts

// To deserialize data from the database
class ShoutoutsObject {
    // TODO should these be values instead?
    // These are just default values, will be overwritten when constructor is called
    var author: String = "author"
    var date: String = "0"
    var msg: String = "test"
    var likes: HashMap<String, String> = HashMap()
    var isLiked: Boolean = false
    var loves: HashMap<String, String> = HashMap()
    var isLoved: Boolean = false
    var hahas: HashMap<String, String> = HashMap()
    var isHahad: Boolean = false
    var surprises: HashMap<String, String> = HashMap()
    var isSurprised: Boolean = false
    var sads: HashMap<String, String> = HashMap()
    var isSaded: Boolean = false
    var angrys: HashMap<String, String> = HashMap()
    var isAngried: Boolean = false
    var uuid: String = ""
}