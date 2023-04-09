package com.github.factotum_sdp.factotum.ui.login

import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult

/**
 * Authentication result : success (user details) or error message.
 */
data class LoginResult(
    override val success: LoggedInUserView? = null,
    override val error: Int? = null
): BaseAuthResult<LoggedInUserView>(success, error)