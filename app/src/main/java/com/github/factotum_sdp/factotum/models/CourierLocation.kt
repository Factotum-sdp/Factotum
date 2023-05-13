package com.github.factotum_sdp.factotum.models

import android.location.Location

data class CourierLocation(
    val uid: String,
    val name: String,
    override val latitude: Double?,
    override val longitude: Double?,
): Localisable<CourierLocation> {

    override fun withLocation(location: Location): CourierLocation {
        return CourierLocation(uid, name, location.latitude, location.longitude)
    }
}
