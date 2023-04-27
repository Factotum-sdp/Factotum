package com.github.factotum_sdp.factotum.models

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val name: String,
    val email: String,
    val role: Role
)