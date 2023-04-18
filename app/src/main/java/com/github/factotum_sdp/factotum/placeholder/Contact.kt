package com.github.factotum_sdp.factotum.placeholder

data class Contact(
    val id: String,
    val role: String,
    val name: String,
    val profile_pic_id: Int,
    val address: String,
    val phone: String,
    val details: String? = null)
{
    constructor() : this("", "", "", 0, "", "", null)
}