package com.github.factotum_sdp.factotum.ui.signup

import com.github.factotum_sdp.factotum.ui.auth.BaseAuthState

/**
 * Data validation state of the signup form.
 */
data class SignUpFormState (
    val usernameError: Int? = null,
    override val emailError: Int? = null,
    override val passwordError: Int? = null,
    override val isDataValid: Boolean = false
) : BaseAuthState()