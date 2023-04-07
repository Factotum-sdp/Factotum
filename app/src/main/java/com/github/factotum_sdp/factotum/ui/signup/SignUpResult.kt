package com.github.factotum_sdp.factotum.ui.signup


/**
 * Sign up result : success (new user name) or error message.
 */
data class SignUpResult(
    val success: String? = null,
    val error: Int? = null
)