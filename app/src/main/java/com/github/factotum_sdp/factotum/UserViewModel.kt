package com.github.factotum_sdp.factotum

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.github.factotum_sdp.factotum.models.User
import com.github.factotum_sdp.factotum.ui.roadbook.LocationTrackingHandler
import java.io.Serializable

/**
 * ViewModel class for the App User
 * Observed in MainActivity
 */
class UserViewModel : ViewModel(), Serializable {

    private val _loggedInUser: MutableLiveData<User> = MutableLiveData()
    val locationTrackingHandler = LocationTrackingHandler()

    /**
     * LiveData<User> to observe the current user logged in
     */
    val loggedInUser: LiveData<User> = _loggedInUser

    /**
     * LiveData<Location?> to observe the current user location
     */
    val userLocation = locationTrackingHandler.currentLocation.asLiveData()

    /**
     * LiveData<Boolean> to observe the location service running state
     */
    val userHasTrackingEnabled = locationTrackingHandler.isTrackingEnabled.asLiveData()

    /**
     * Set the logged in app user
     *
     * @param loggedInUser: User
     */
    fun setLoggedInUser(loggedInUser: User) {
        _loggedInUser.postValue(loggedInUser)
    }
}
