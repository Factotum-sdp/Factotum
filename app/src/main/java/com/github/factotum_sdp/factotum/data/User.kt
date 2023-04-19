package com.github.factotum_sdp.factotum.data

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val displayName: String,
    val email: String,
    val role : Role
)