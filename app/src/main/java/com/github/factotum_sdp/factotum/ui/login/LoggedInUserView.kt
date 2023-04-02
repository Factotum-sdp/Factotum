package com.github.factotum_sdp.factotum.ui.login

/**
 * User details post authentication that is exposed to the UI
 */
data class LoggedInUserView(
    val displayName: String,
    val email : String
    //... other data fields that may be accessible to the UI
)