package com.github.factotum_sdp.factotum.placeholder

data class Contact(
    val username: String = "",
    val role: String = "",
    val name: String = "",
    val surname: String = "",
    val profile_pic_id: Int = 0,
    val super_client: String? = null,
    val addressName: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val phone: String = "",
    val details: String? = null
)