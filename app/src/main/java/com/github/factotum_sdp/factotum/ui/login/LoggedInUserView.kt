package com.github.factotum_sdp.factotum.ui.login

import com.github.factotum_sdp.factotum.data.Role

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val email: String,
    val role: Role
    //... other data fields that may be accessible to the UI
)