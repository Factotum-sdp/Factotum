package com.github.factotum_sdp.factotum.models

import kotlinx.serialization.Serializable

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
@Serializable
data class User(
    val name: String = "",
    val email: String = "",
    val role: Role = Role.UNKNOWN
)