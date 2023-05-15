package com.github.factotum_sdp.factotum.models

import android.location.Location

data class Contact(
    val username: String = "",
    val role: String = "",
    val name: String = "",
    val surname: String = "",
    val profile_pic_id: Int = 0,
    val super_client: String? = null,
    val addressName: String? = null,
    override val latitude: Double? = null,
    override val longitude: Double? = null,
    val phone: String = "",
    val details: String? = null
) : Localisable<Contact> {
    override fun withLocation(location: Location): Contact {
        return this.copy(latitude = location.latitude, longitude = location.longitude)
    }
}