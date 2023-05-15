package com.github.factotum_sdp.factotum.models

import android.location.Location
import android.net.Uri

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

@Serializable
data class User(
    val name: String,
    val email: String,
    val role: Role,
    val profilePicture: Uri? = null,
    override val latitude: Double? = null,
    override val longitude: Double? = null
) : Localisable<User> {
    override fun withLocation(location: Location): User {
        return this.copy(latitude = location.latitude, longitude = location.longitude)
    }
}