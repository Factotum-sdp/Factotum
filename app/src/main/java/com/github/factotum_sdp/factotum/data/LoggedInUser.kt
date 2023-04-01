package com.github.factotum_sdp.factotum.data

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    private val displayName: String,
    private val email: String
)