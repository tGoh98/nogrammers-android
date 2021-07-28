package com.duncan_college.nogrammers_android.shoutouts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShoutoutsViewModel : ViewModel() {
    val createMode = MutableLiveData<Boolean>()

    init {
        createMode.value = false
    }
}