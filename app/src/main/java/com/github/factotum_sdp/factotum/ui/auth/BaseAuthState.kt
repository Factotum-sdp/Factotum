package com.github.factotum_sdp.factotum.ui.auth

import androidx.core.util.PatternsCompat

abstract class BaseAuthState {
    abstract val emailError: Int?
    abstract val passwordError: Int?
    abstract val isDataValid: Boolean

    companion object {
        private const val PASSWORD_LENGTH = 6

        // A placeholder email validation check
        internal fun isEmailValid(email: String): Boolean {
            return if (email.contains("@")) PatternsCompat.EMAIL_ADDRESS.matcher(email)
                .matches() else false
        }

        // A placeholder password validation check
        internal fun isPasswordValid(password: String): Boolean {
            return password.length >= PASSWORD_LENGTH
        }
    }
}