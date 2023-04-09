package com.github.factotum_sdp.factotum.ui.signup

import com.github.factotum_sdp.factotum.ui.auth.BaseAuthResult


/**
 * Sign up result : success (new user name) or error message.
 */
data class SignUpResult(
    override val success: String? = null,
    override val error: Int? = null
): BaseAuthResult<String>(success, error)