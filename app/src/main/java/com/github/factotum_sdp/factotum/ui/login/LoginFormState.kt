package com.github.factotum_sdp.factotum.ui.login

import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState

/**
 * Data validation state of the login form.
 */
data class LoginFormState(
    override val emailError: Int? = null,
    override val passwordError: Int? = null,
    override val isDataValid: Boolean = false
) : BaseAuthState()