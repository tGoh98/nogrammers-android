package com.duncan_college.nogrammers_android.announcements

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AnnouncementsViewModel : ViewModel() {
    val createMode = MutableLiveData<Boolean>()

    init {
        createMode.value = false
    }
}