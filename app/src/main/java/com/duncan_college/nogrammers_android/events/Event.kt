package com.duncan_college.nogrammers_android.events

data class Event(
        val author: String, var title: String, var desc: String,
        var start: Long, var end: Long, var tags: List<String>, var location: String,
        var audience: String, val pic: String = "", var remote: Boolean = false,
        val interestedUsers: MutableList<String> = mutableListOf(),
        val goingUsers: MutableList<String> = mutableListOf(), var key: String = "") {

    /**
     * Adds user's net id to event's list of interested users, returns true if added, false
     * if already in the list
     */
    fun addUserToInterested(netId: String) : Boolean {
        if (!interestedUsers.contains(netId)) {
            interestedUsers.add(netId)
            return true
        }
        return false
    }

    /**
     * Adds user's net id to event's list of going users, returns true if added, false
     * if already in the list
     */
    fun addUserToGoing(netId: String) : Boolean {
        if (!goingUsers.contains(netId)) {
            goingUsers.add(netId)
            return true
        }
        return false
    }

    /**
     * Remove user's net id from event's list of interested users, returns true if remove, false
     * if not in the list
     */
    fun removeUserFromInterested(netId: String) : Boolean {
        return interestedUsers.remove(netId)
    }

    /**
     * Remove user's net id from event's list of going users, returns true if remove, false
     * if not in the list
     */
    fun removeUserFromGoing(netId: String) : Boolean {
        return goingUsers.remove(netId)
    }

    /**
     * remove all tags from the event
     */
    fun clearTags() {
        tags = mutableListOf()
    }

}