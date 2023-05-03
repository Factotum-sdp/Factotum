package com.github.factotum_sdp.factotum.models

data class Contact(
    val username: String = "",
    val role: String = "",
    val name: String = "",
    val surname: String = "",
    val profile_pic_id: Int = 0,
    val address: String = "",
    val phone: String = "",
    val details: String? = null
)