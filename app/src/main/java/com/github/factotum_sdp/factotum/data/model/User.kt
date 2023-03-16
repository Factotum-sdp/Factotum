package com.github.factotum_sdp.factotum.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class User(
    val userId: String,
    val name: String,
    val email: String,
    val userRole: Role
)