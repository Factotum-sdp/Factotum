package com.github.factotum_sdp.factotum.ui.signup

/**
 * Data validation state of the signup form.
 */
class SignUpFormState (
    val usernameError: Int? = null,
    val emailError: Int? = null,
    val passwordError: Int? = null,
    val roleError: Int? = null,
    val isDataValid: Boolean = false
)