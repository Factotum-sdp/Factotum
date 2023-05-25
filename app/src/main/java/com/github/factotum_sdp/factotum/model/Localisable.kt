package com.github.factotum_sdp.factotum.model

import android.location.Location


private const val LOCATION_PROVIDER_NAME = "factotum"

/**
 * The Localisable interface allowing to manage the Localisable entity of the app.
 * For example, an application User or a Contact.
 *
 * Link it the latitude and longitude fields to the android.location.Location object.
 * Note that The T parameter should be the type of the implemented class
 */
interface Localisable<out T: Localisable<T>> {

    val latitude: Double?
    val longitude: Double?

    /**
     * Whether the Localisable entity has currently some coordinates
     * @return Boolean
     */
    fun hasCoordinates(): Boolean {
        return latitude != null && longitude != null
    }

    /**
     * Create a new instance from an android.location.Location object
     * @param location: Location
     * @return T: the type of the "Localisable" class
     */
    fun withLocation(location: Location): T

    /**
     * Get the current Location of the Localisable entity
     * Or null if currently no Location is available
     *
     * @return Location?
     */
    fun getLocation(): Location? {
        val location = Location(LOCATION_PROVIDER_NAME)
        location.latitude = latitude ?: return null
        location.longitude = longitude ?: return null
        return location
    }
}