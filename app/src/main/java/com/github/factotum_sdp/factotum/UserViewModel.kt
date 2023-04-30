package com.github.factotum_sdp.factotum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.factotum_sdp.factotum.models.User

/**
 * ViewModel class for the App User
 * Observed in MainActivity
 */
class UserViewModel : ViewModel() {

    private val _loggedInUser: MutableLiveData<User> = MutableLiveData()
    val loggedInUser: LiveData<User> = _loggedInUser

    fun setLoggedInUser(loggedInUser: User) {
        _loggedInUser.postValue(loggedInUser)
    }
}
