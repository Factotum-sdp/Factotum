package com.github.factotum_sdp.factotum.models

import android.location.Location

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val uid: String,
    val name: String,
    val email: String,
    val role: Role,
    override val latitude: Double? = null,
    override val longitude: Double? = null
) : Localisable<User> {
    override fun withLocation(location: Location): User {
        return this.copy(latitude = location.latitude, longitude = location.longitude)
    }
}